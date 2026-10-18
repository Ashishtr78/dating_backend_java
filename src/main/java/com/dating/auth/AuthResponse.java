package com.dating.auth;

public record AuthResponse(String accessToken, String refreshToken, long expiresIn) {
}
