package com.dating.auth;

import com.dating.common.SecurityProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
class TokenService {

    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokens;
    private final SecurityProperties properties;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public AuthResponse issue(UUID userId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plus(properties.accessTokenTtl()))
                .build();
        String accessToken = jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshToken(userId, hash(refreshToken), now.plus(properties.refreshTokenTtl())));

        return new AuthResponse(accessToken, refreshToken, properties.accessTokenTtl().toSeconds());
    }

    @Transactional
    public AuthResponse rotate(String refreshToken) {
        RefreshToken token = refreshTokens.findByTokenHash(hash(refreshToken))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        refreshTokens.delete(token);
        return issue(token.getUserId());
    }

    @Transactional
    public void revoke(String refreshToken) {
        refreshTokens.deleteByTokenHash(hash(refreshToken));
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpired() {
        refreshTokens.deleteExpired(Instant.now());
    }

    private static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
