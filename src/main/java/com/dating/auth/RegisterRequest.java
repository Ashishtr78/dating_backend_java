package com.dating.auth;

import com.dating.profile.Gender;
import com.dating.profile.InterestedIn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 50) String displayName,
        @NotNull @Past LocalDate birthDate,
        @NotNull Gender gender,
        @NotNull InterestedIn interestedIn) {
}
