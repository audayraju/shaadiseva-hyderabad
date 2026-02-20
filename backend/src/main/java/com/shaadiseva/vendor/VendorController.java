package com.shaadiseva.vendor;

import com.shaadiseva.domain.User;
import com.shaadiseva.vendor.dto.VendorApplyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping("/applications")
    public ResponseEntity<Map<String, UUID>> applyAsVendor(
            @Valid @RequestBody VendorApplyRequest request) {
        UUID applicationId = vendorService.applyAsVendor(request);
        return ResponseEntity.ok(Map.of("applicationId", applicationId));
    }

    @PostMapping("/applications/{id}/documents")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<Map<String, UUID>> uploadDocument(
            @PathVariable UUID id,
            @RequestParam String docType,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {
        UUID documentId = vendorService.uploadDocument(id, docType, file, currentUser);
        return ResponseEntity.ok(Map.of("documentId", documentId));
    }

    @GetMapping("/documents/{id}/download")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<InputStreamResource> downloadDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return vendorService.downloadDocument(id, currentUser);
    }
}
