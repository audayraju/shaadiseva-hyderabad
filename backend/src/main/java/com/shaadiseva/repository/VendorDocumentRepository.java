package com.shaadiseva.repository;

import com.shaadiseva.domain.VendorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendorDocumentRepository extends JpaRepository<VendorDocument, UUID> {

    List<VendorDocument> findByVendorApplicationId(UUID vendorApplicationId);

    List<VendorDocument> findByVendorApplicationIdAndDocType(UUID vendorApplicationId, String docType);
}
