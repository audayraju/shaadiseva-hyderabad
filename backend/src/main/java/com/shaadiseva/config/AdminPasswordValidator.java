package com.shaadiseva.config;

import com.shaadiseva.domain.User;
import com.shaadiseva.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordValidator implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@shaadiseva.com";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "APP_ADMIN_PASSWORD environment variable is required but not set. " +
                    "Application cannot start.");
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                    "APP_ADMIN_PASSWORD must be at least 12 characters long.");
        }

        if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            User admin = User.builder()
                    .email(ADMIN_EMAIL)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(User.Role.ADMIN)
                    .status(User.Status.ACTIVE)
                    .build();
            userRepository.save(admin);
            log.info("Admin user initialized.");
        } else {
            log.info("Admin user already exists — skipping creation.");
        }
    }
}
