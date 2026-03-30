package com.neurocrew.dto;

import com.neurocrew.model.UserProfile;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ProfileDto {

    @Data
    public static class UpdateRequest {

        @Size(max = 300, message = "Bio cannot exceed 300 characters")
        private String bio;

        @Size(max = 100, message = "Location cannot exceed 100 characters")
        private String location;

        @Size(max = 100, message = "Website cannot exceed 100 characters")
        private String website;

        @Size(max = 100, message = "LinkedIn cannot exceed 100 characters")
        private String linkedin;

        @Size(max = 100, message = "GitHub cannot exceed 100 characters")
        private String github;

        @Size(max = 15, message = "Phone cannot exceed 15 characters")
        private String phone;

        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @Size(max = 500, message = "Skills cannot exceed 500 characters")
        private String skills;                                  // ← comma separated

        private boolean contactVisible;
    }

    @Data
    public static class ContactDetailsRequest {
        
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @Size(max = 15, message = "Phone cannot exceed 15 characters")
        @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Invalid phone format")
        private String phone;

        @Size(max = 100, message = "LinkedIn cannot exceed 100 characters")
        private String linkedin;

        @Size(max = 100, message = "GitHub cannot exceed 100 characters")
        private String github;

        @Size(max = 100, message = "Website cannot exceed 100 characters")
        private String website;

        private boolean contactVisible;
    }

    @Data
    public static class ContactDetailsResponse {
        private String email;
        private String phone;
        private String linkedin;
        private String github;
        private String website;
        private boolean contactVisible;
    }

    @Data
    public static class ProfileResponse {
        private Long id;
        private Long userId;
        private String username;
        private String role;
        private String bio;
        private String location;
        private String website;
        private String linkedin;
        private String github;
        private String phone;                                   // ← only shown if contactVisible
        private String email;                                   // ← only shown if contactVisible
        private List<String> skills;                            // ← parsed as list
        private boolean hasResume;
        private String resumeFileName;
        private boolean contactVisible;
        private boolean profileComplete;
        private int completionPercent;                          // ← profile completion %
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ProfileResponse from(UserProfile profile, boolean showContact) {
            ProfileResponse r = new ProfileResponse();
            r.id = profile.getId();
            r.userId = profile.getUser().getId();
            r.username = profile.getUser().getUsername();
            r.role = profile.getUser().getRole().name();
            r.bio = profile.getBio();
            r.location = profile.getLocation();
            r.website = profile.getWebsite();
            r.linkedin = profile.getLinkedin();
            r.github = profile.getGithub();
            r.contactVisible = profile.isContactVisible();
            r.hasResume = profile.getResumeFilePath() != null;
            r.resumeFileName = profile.getResumeFileName();
            r.createdAt = profile.getCreatedAt();
            r.updatedAt = profile.getUpdatedAt();

            // ← only show phone and email if contactVisible or own profile
            r.phone = showContact ? profile.getPhone() : null;
            r.email = showContact ? profile.getEmail() : null;

            // ← parse skills string to list
            r.skills = profile.getSkills() != null && !profile.getSkills().isBlank()
                ? Arrays.stream(profile.getSkills().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList()
                : List.of();

            // ← calculate completion percent
            r.completionPercent = calculateCompletion(profile);
            r.profileComplete = r.completionPercent >= 80;

            return r;
        }

        private static int calculateCompletion(UserProfile p) {
            int score = 0;
            if (p.getBio() != null && !p.getBio().isBlank())          score += 20;
            if (p.getSkills() != null && !p.getSkills().isBlank())    score += 20;
            if (p.getLocation() != null && !p.getLocation().isBlank()) score += 10;
            if (p.getPhone() != null && !p.getPhone().isBlank())      score += 10;
            if (p.getEmail() != null && !p.getEmail().isBlank())      score += 10;
            if (p.getLinkedin() != null && !p.getLinkedin().isBlank()) score += 10;
            if (p.getGithub() != null && !p.getGithub().isBlank())    score += 10;
            if (p.getResumeFilePath() != null)                         score += 15;
            return score;
        }
    }
}