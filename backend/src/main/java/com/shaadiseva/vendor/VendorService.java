package com.shaadiseva.vendor;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.shaadiseva.domain.Category;
import com.shaadiseva.domain.User;
import com.shaadiseva.domain.VendorApplication;
import com.shaadiseva.domain.VendorDocument;
import com.shaadiseva.domain.VendorProfile;
import com.shaadiseva.repository.UserRepository;
import com.shaadiseva.repository.VendorApplicationRepository;
import com.shaadiseva.repository.VendorDocumentRepository;
import com.shaadiseva.vendor.dto.VendorApplyRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorService {

    private final UserRepository userRepository;
    private final VendorApplicationRepository applicationRepository;
    private final VendorDocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmazonS3 amazonS3;
    private final EntityManager entityManager;

    @Value("${minio.bucket}")
    private String bucket;

    @Transactional
    public UUID applyAsVendor(VendorApplyRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(request.getPassword() != null
                        ? passwordEncoder.encode(request.getPassword())
                        : null)
                .role(User.Role.VENDOR)
                .status(User.Status.PENDING)
                .build();
        user = userRepository.save(user);

        Category category = null;
        if (request.getCategoryId() != null) {
            category = entityManager.getReference(Category.class, request.getCategoryId());
        }

        VendorProfile profile = VendorProfile.builder()
                .user(user)
                .category(category)
                .businessName(request.getBusinessName())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .bio(request.getBio())
                .yearsExperience(request.getYearsExperience())
                .build();
        entityManager.persist(profile);

        VendorApplication application = VendorApplication.builder()
                .user(user)
                .vendorProfile(profile)
                .status(VendorApplication.ApplicationStatus.PENDING_VERIFICATION)
                .build();
        application = applicationRepository.save(application);

        return application.getId();
    }

    @Transactional
    public UUID uploadDocument(UUID applicationId, String docType,
                               MultipartFile file, User currentUser) throws IOException {
        VendorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));

        boolean isOwner = application.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Access denied: you do not own this application");
        }

        String originalFilename = file.getOriginalFilename();
        String baseName = (originalFilename != null && !originalFilename.isBlank())
                ? new java.io.File(originalFilename).getName()
                : "upload";
        String safeName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = String.format("vendor-docs/%s/%s/%s",
                applicationId, docType, safeName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, key, file.getInputStream(), metadata);

        VendorDocument document = VendorDocument.builder()
                .vendorApplication(application)
                .docType(docType.toUpperCase())
                .fileName(safeName)
                .storageKey(key)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();
        document = documentRepository.save(document);

        log.info("Document uploaded: {}", key);
        return document.getId();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<InputStreamResource> downloadDocument(UUID documentId, User currentUser) {
        VendorDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        VendorApplication application = doc.getVendorApplication();
        boolean isOwner = application.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Access denied");
        }

        S3Object s3Object = amazonS3.getObject(bucket, doc.getStorageKey());
        String contentType = doc.getContentType() != null
                ? doc.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }
}
