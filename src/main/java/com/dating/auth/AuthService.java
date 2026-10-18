package com.dating.auth;

import com.dating.profile.User;
import com.dating.profile.UserRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
class AuthService {

    private static final int MINIMUM_AGE = 18;

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().strip());
        user.setBirthDate(request.birthDate());
        user.setGender(request.gender());
        user.setInterestedIn(request.interestedIn());
        if (user.age() < MINIMUM_AGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must be at least 18 years old");
        }

        return tokens.issue(users.save(user).getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        return users.findByEmail(normalize(request.email()))
                .filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()))
                .map(user -> tokens.issue(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    public AuthResponse refresh(RefreshRequest request) {
        return tokens.rotate(request.refreshToken());
    }

    public void logout(RefreshRequest request) {
        tokens.revoke(request.refreshToken());
    }

    private static String normalize(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
