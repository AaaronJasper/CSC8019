package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private UserDetails userDetails(String email) {
        return User.builder()
                .email(email)
                .password("password")
                .name("Test User")
                .role(Role.CUSTOMER)
                .build();
    }

    // ── generateToken / extractUsername ───────────────────────────────────────

    @Test
    void generateToken_extractedUsernameMathchesInput() {
        UserDetails user = userDetails("alice@test.com");

        String token = jwtService.generateToken(user);
        String extracted = jwtService.extractUsername(token);

        assertThat(extracted).isEqualTo("alice@test.com");
    }

    @Test
    void generateToken_withExtraClaims_extractsUsername() {
        UserDetails user = userDetails("bob@test.com");

        String token = jwtService.generateToken(Map.of("role", "CUSTOMER"), user);
        String extracted = jwtService.extractUsername(token);

        assertThat(extracted).isEqualTo("bob@test.com");
    }

    @Test
    void generateToken_producesNonEmptyToken() {
        String token = jwtService.generateToken(userDetails("alice@test.com"));

        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_differentUsersProduceDifferentTokens() {
        String tokenA = jwtService.generateToken(userDetails("a@test.com"));
        String tokenB = jwtService.generateToken(userDetails("b@test.com"));

        assertThat(tokenA).isNotEqualTo(tokenB);
    }

    // ── isTokenValid ──────────────────────────────────────────────────────────

    @Test
    void isTokenValid_validToken_returnsTrue() {
        UserDetails user = userDetails("alice@test.com");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        UserDetails alice = userDetails("alice@test.com");
        UserDetails bob = userDetails("bob@test.com");
        String aliceToken = jwtService.generateToken(alice);

        assertThat(jwtService.isTokenValid(aliceToken, bob)).isFalse();
    }

    // ── extractClaim ──────────────────────────────────────────────────────────

    @Test
    void extractClaim_subjectMatchesUsername() {
        UserDetails user = userDetails("carol@test.com");
        String token = jwtService.generateToken(user);

        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        assertThat(subject).isEqualTo("carol@test.com");
    }

    @Test
    void extractClaim_expirationIsInFuture() {
        UserDetails user = userDetails("carol@test.com");
        String token = jwtService.generateToken(user);

        java.util.Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());

        assertThat(expiration).isAfter(new java.util.Date());
    }
}
