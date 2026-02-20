package com.shaadiseva.auth;

import com.shaadiseva.auth.dto.AuthResponse;
import com.shaadiseva.auth.dto.LoginRequest;
import com.shaadiseva.auth.dto.OtpSendRequest;
import com.shaadiseva.auth.dto.OtpVerifyRequest;
import com.shaadiseva.domain.OtpCode;
import com.shaadiseva.domain.User;
import com.shaadiseva.repository.OtpCodeRepository;
import com.shaadiseva.repository.UserRepository;
import com.shaadiseva.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public String sendOtp(OtpSendRequest request) {
        String phone = request.getPhone();

        // Generate 6-digit OTP using a cryptographically secure random source
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        String codeHash = passwordEncoder.encode(otp);

        OtpCode otpCode = OtpCode.builder()
                .phone(phone)
                .codeHash(codeHash)
                .expiresAt(OffsetDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        otpCodeRepository.save(otpCode);

        // In production replace with actual SMS provider
        log.info("OTP for {}: {}", phone, otp);

        return "OTP sent successfully to " + phone;
    }

    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        OtpCode otpCode = otpCodeRepository
                .findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        request.getPhone(), OffsetDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("OTP not found or expired"));

        if (!passwordEncoder.matches(request.getOtp(), otpCode.getCodeHash())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        User user = userRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .phone(request.getPhone())
                            .role(User.Role.CUSTOMER)
                            .status(User.Status.ACTIVE)
                            .build();
                    return userRepository.save(newUser);
                });

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtils.generateToken(user, false);
        String refreshToken = jwtUtils.generateToken(user, true);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }
}
