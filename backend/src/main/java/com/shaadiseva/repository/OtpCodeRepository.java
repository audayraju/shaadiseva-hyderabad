package com.shaadiseva.repository;

import com.shaadiseva.domain.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {

    Optional<OtpCode> findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String phone, OffsetDateTime now);
}
