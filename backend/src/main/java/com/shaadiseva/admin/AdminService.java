package com.shaadiseva.admin;

import com.shaadiseva.domain.User;
import com.shaadiseva.domain.VendorApplication;
import com.shaadiseva.domain.VendorApplication.ApplicationStatus;
import com.shaadiseva.repository.UserRepository;
import com.shaadiseva.repository.VendorApplicationRepository;
import com.shaadiseva.repository.VendorDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Set<String> MANDATORY_DOC_TYPES = Set.of(
            "GST", "PAN", "AADHAAR", "BUSINESS_REG", "ADDRESS_PROOF");

    private final VendorApplicationRepository applicationRepository;
    private final VendorDocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<VendorApplication> listPendingApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING_VERIFICATION);
    }

    @Transactional
    public VendorApplication approveApplication(UUID id, User admin) {
        VendorApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        // Validate mandatory documents
        Set<String> uploadedDocTypes = documentRepository
                .findByVendorApplicationId(id)
                .stream()
                .map(doc -> doc.getDocType().toUpperCase())
                .collect(Collectors.toSet());

        Set<String> missingDocs = MANDATORY_DOC_TYPES.stream()
                .filter(required -> !uploadedDocTypes.contains(required))
                .collect(Collectors.toSet());

        if (!missingDocs.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot approve: missing mandatory documents: " + missingDocs);
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewedBy(admin);
        application.setReviewedAt(OffsetDateTime.now());

        User vendor = application.getUser();
        vendor.setStatus(User.Status.ACTIVE);
        userRepository.save(vendor);

        VendorApplication saved = applicationRepository.save(application);
        log.info("Application {} approved by admin {}", id, admin.getId());
        return saved;
    }

    @Transactional
    public VendorApplication rejectApplication(UUID id, String reason, User admin) {
        VendorApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectionReason(reason);
        application.setReviewedBy(admin);
        application.setReviewedAt(OffsetDateTime.now());

        VendorApplication saved = applicationRepository.save(application);
        log.info("Application {} rejected by admin {} with reason: {}", id, admin.getId(), reason);
        return saved;
    }
}
