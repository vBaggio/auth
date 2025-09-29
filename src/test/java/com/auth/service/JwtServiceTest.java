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

/**
 * Testes unitários para {@link JwtService}.
 *
 * Segue o mesmo padrão utilizado nos demais testes do módulo, com foco
 * na clareza dos cenários e na validação das regras principais de geração e
 * validação de tokens JWT.
 */
@DisplayName("JwtService - Testes Unitários")
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
    @DisplayName("Deve gerar token válido com claims extras")
    void deveGerarTokenValidoComClaimsExtras() {
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
    @DisplayName("Deve validar token para usuário correspondente")
    void deveValidarTokenParaUsuarioCorrespondente() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token expirado")
    void deveLancarExcecaoAoValidarTokenExpirado() {
    com.auth.service.JwtService expiredJwtService = new com.auth.service.JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "secret", SECRET);
        ReflectionTestUtils.setField(expiredJwtService, "expiration", -1_000L);

        String tokenExpirado = expiredJwtService.generateToken(userDetails);

        assertThatThrownBy(() -> expiredJwtService.validateToken(tokenExpirado, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
        assertThat(expiredJwtService.validateToken(tokenExpirado)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar falso quando token é inválido ou corrompido")
    void deveRetornarFalsoQuandoTokenEInvalidoOuCorrompido() {
        String tokenValido = jwtService.generateToken(userDetails);
        String tokenCorrompido = tokenValido.substring(0, tokenValido.length() - 5) + "abcde";

        assertThat(jwtService.validateToken(tokenCorrompido)).isFalse();
    }

    @Test
    @DisplayName("Deve calcular data de expiração futura")
    void deveCalcularDataDeExpiracaoFutura() {
        Date expirationDate = jwtService.getExpirationDate();

        long diff = expirationDate.getTime() - System.currentTimeMillis();
        assertThat(diff).isBetween(EXPIRATION - 2_000L, EXPIRATION + 2_000L);
    }
}
