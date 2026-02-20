package com.shaadiseva.repository;

import com.shaadiseva.domain.VendorApplication;
import com.shaadiseva.domain.VendorApplication.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendorApplicationRepository extends JpaRepository<VendorApplication, UUID> {

    List<VendorApplication> findByStatus(ApplicationStatus status);

    List<VendorApplication> findByUserId(UUID userId);
}
