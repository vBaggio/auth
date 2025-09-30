package com.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService - Unit Tests")
class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION = 3_600_000L; // 1 hora

    private com.auth.service.JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new com.auth.service.JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);

        userDetails = User
                .withUsername("user@example.com")
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    @DisplayName("Should return a valid token with extra claims")
    void shouldReturnValidTokenWithExtraClaims() {
        Map<String, Object> extraClaims = Map.of("role", "USER");

        String token = jwtService.generateToken(userDetails, extraClaims);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(userDetails.getUsername());
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertThat(roleClaim).isEqualTo("USER");

        Date expirationDate = jwtService.extractExpiration(token);
        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    @DisplayName("Should return true when token belongs to user")
    void shouldReturnTrueWhenTokenBelongsToUser() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when validating expired token")
    void shouldThrowExceptionWhenValidatingExpiredToken() {
        com.auth.service.JwtService expiredJwtService = new com.auth.service.JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "secret", SECRET);
        ReflectionTestUtils.setField(expiredJwtService, "expiration", -1_000L);

        String tokenExpirado = expiredJwtService.generateToken(userDetails);

        assertThatThrownBy(() -> expiredJwtService.validateToken(tokenExpirado, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
        assertThat(expiredJwtService.validateToken(tokenExpirado)).isFalse();
    }

    @Test
    @DisplayName("Should return false when token is invalid or corrupted")
    void shouldReturnFalseWhenTokenIsInvalidOrCorrupted() {
        String tokenValido = jwtService.generateToken(userDetails);
        String tokenCorrompido = tokenValido.substring(0, tokenValido.length() - 5) + "abcde";

        assertThat(jwtService.validateToken(tokenCorrompido)).isFalse();
    }

    @Test
    @DisplayName("Should return a future expiration date")
    void shouldReturnFutureExpirationDate() {
        Date expirationDate = jwtService.getExpirationDate();

        long diff = expirationDate.getTime() - System.currentTimeMillis();
        assertThat(diff).isBetween(EXPIRATION - 2_000L, EXPIRATION + 2_000L);
    }
}
