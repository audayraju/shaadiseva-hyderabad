package com.shaadiseva;

import com.shaadiseva.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShaadiSevaApplicationTests {

    // 64-char secret (512 bits) – satisfies HMAC-SHA256 minimum
    private static final String TEST_SECRET =
            "shaadiseva-test-secret-key-for-unit-tests-must-be-at-least-64-chars!!";
    private static final long EXPIRY_MS = 3_600_000L;       // 1 hour
    private static final long REFRESH_EXPIRY_MS = 86_400_000L; // 24 hours

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(TEST_SECRET, EXPIRY_MS, REFRESH_EXPIRY_MS);
    }

    @Test
    void generateToken_and_validateToken_shouldReturnTrue() {
        UserDetails user = buildUser("vendor@example.com", "ROLE_VENDOR");
        String token = jwtUtils.generateToken(user, false);
        assertTrue(jwtUtils.validateToken(token), "Access token should be valid");
    }

    @Test
    void generateRefreshToken_and_validateToken_shouldReturnTrue() {
        UserDetails user = buildUser("customer@example.com", "ROLE_CUSTOMER");
        String token = jwtUtils.generateToken(user, true);
        assertTrue(jwtUtils.validateToken(token), "Refresh token should be valid");
    }

    @Test
    void getUsername_shouldReturnCorrectSubject() {
        UserDetails user = buildUser("admin@shaadiseva.com", "ROLE_ADMIN");
        String token = jwtUtils.generateToken(user, false);
        assertEquals("admin@shaadiseva.com", jwtUtils.getUsername(token));
    }

    @Test
    void validateToken_withTamperedToken_shouldReturnFalse() {
        UserDetails user = buildUser("test@example.com", "ROLE_CUSTOMER");
        String token = jwtUtils.generateToken(user, false);
        String tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertFalse(jwtUtils.validateToken(tampered), "Tampered token should be invalid");
    }

    @Test
    void validateToken_withGarbageString_shouldReturnFalse() {
        assertFalse(jwtUtils.validateToken("not.a.jwt"), "Garbage string should be invalid");
    }

    private UserDetails buildUser(String username, String role) {
        return new User(username, "password",
                List.of(new SimpleGrantedAuthority(role)));
    }
}
