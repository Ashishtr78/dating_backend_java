package com.dating.profile;

import com.dating.common.CurrentUserId;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    ProfileResponse get(@CurrentUserId UUID userId) {
        return profileService.get(userId);
    }

    @PutMapping
    ProfileResponse update(@CurrentUserId UUID userId, @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.update(userId, request);
    }

    @PutMapping("/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateLocation(@CurrentUserId UUID userId, @Valid @RequestBody UpdateLocationRequest request) {
        profileService.updateLocation(userId, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@CurrentUserId UUID userId) {
        profileService.delete(userId);
    }
}
