package com.shaadiseva.admin;

import com.shaadiseva.domain.User;
import com.shaadiseva.domain.VendorApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/vendor-applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VendorApplication>> listPendingApplications() {
        return ResponseEntity.ok(adminService.listPendingApplications());
    }

    @PostMapping("/vendor-applications/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorApplication> approveApplication(
            @PathVariable UUID id,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(adminService.approveApplication(id, admin));
    }

    @PostMapping("/vendor-applications/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorApplication> rejectApplication(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User admin) {
        String reason = body.getOrDefault("reason", "");
        return ResponseEntity.ok(adminService.rejectApplication(id, reason, admin));
    }
}
