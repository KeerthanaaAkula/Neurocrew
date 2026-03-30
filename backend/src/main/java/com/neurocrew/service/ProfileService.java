package com.neurocrew.service;

import com.neurocrew.dto.ProfileDto;
import com.neurocrew.model.User;
import com.neurocrew.model.UserProfile;
import com.neurocrew.repository.UserProfileRepository;
import com.neurocrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;                                   // ← configurable upload directory

    // ── Get own profile ───────────────────────────────────────
    public ProfileDto.ProfileResponse getMyProfile() {
        User user = currentUser();
        UserProfile profile = getOrCreateProfile(user);
        return ProfileDto.ProfileResponse.from(profile, true); // ← show contact for own profile
    }

    // ── Get any user's profile ────────────────────────────────
    public ProfileDto.ProfileResponse getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        UserProfile profile = getOrCreateProfile(user);

        // ← show contact only if contactVisible is true
        return ProfileDto.ProfileResponse.from(profile, profile.isContactVisible());
    }

    // ── Update profile ────────────────────────────────────────
    public ProfileDto.ProfileResponse updateProfile(ProfileDto.UpdateRequest req) {
        User user = currentUser();
        UserProfile profile = getOrCreateProfile(user);

        if (req.getBio() != null)       profile.setBio(req.getBio());
        if (req.getLocation() != null)  profile.setLocation(req.getLocation());
        if (req.getWebsite() != null)   profile.setWebsite(req.getWebsite());
        if (req.getLinkedin() != null)  profile.setLinkedin(req.getLinkedin());
        if (req.getGithub() != null)    profile.setGithub(req.getGithub());
        if (req.getPhone() != null)     profile.setPhone(req.getPhone());
        if (req.getEmail() != null)     profile.setEmail(req.getEmail());
        if (req.getSkills() != null)    profile.setSkills(req.getSkills());
        profile.setContactVisible(req.isContactVisible());

        profile = profileRepository.save(profile);
        log.info("Profile updated for user: {}", user.getUsername());
        return ProfileDto.ProfileResponse.from(profile, true);
    }

    // ── Upload resume ─────────────────────────────────────────
    public ProfileDto.ProfileResponse uploadResume(MultipartFile file) throws IOException {
        User user = currentUser();

        // ← validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Resume file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("Invalid filename");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !originalFilename.toLowerCase().endsWith(".pdf"))) {
            throw new RuntimeException("Only PDF files are allowed for resumes");
        }

        if (file.getSize() > 5 * 1024 * 1024) {               // ← 5MB limit
            throw new RuntimeException("Resume file size must be less than 5MB");
        }

        // ← create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ← generate unique filename with timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = "resume_" + user.getId() + "_" + timestamp + ".pdf";
        Path filePath = uploadPath.resolve(fileName);

        // ← delete old resume if exists
        UserProfile profile = getOrCreateProfile(user);
        if (profile.getResumeFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(profile.getResumeFilePath()));
                log.info("Deleted old resume file for user: {}", user.getUsername());
            } catch (IOException e) {
                log.warn("Could not delete old resume file: {}", e.getMessage());
            }
        }

        // ← save new file with proper error handling
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Resume file saved to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save resume file: {}", e.getMessage());
            throw new RuntimeException("Failed to save resume file");
        }

        profile.setResumeFileName(originalFilename);
        profile.setResumeFilePath(filePath.toString());
        profile.setResumeContentType(contentType);

        profile = profileRepository.save(profile);
        log.info("Resume uploaded successfully for user: {}", user.getUsername());
        return ProfileDto.ProfileResponse.from(profile, true);
    }

    // ── Delete resume ─────────────────────────────────────────
    public ProfileDto.ProfileResponse deleteResume() throws IOException {
        User user = currentUser();
        UserProfile profile = getOrCreateProfile(user);

        if (profile.getResumeFilePath() != null) {
            Files.deleteIfExists(Paths.get(profile.getResumeFilePath()));
            profile.setResumeFileName(null);
            profile.setResumeFilePath(null);
            profile.setResumeContentType(null);
            profile = profileRepository.save(profile);
            log.info("Resume deleted for user: {}", user.getUsername());
        }

        return ProfileDto.ProfileResponse.from(profile, true);
    }

    // ── Download resume ───────────────────────────────────────
    public byte[] downloadResume(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getResumeFilePath() == null) {
            throw new RuntimeException("No resume uploaded");
        }

        return Files.readAllBytes(Paths.get(profile.getResumeFilePath()));
    }

    // ── Contact details methods ────────────────────────────────
    public ProfileDto.ContactDetailsResponse getMyContactDetails() {
        User user = currentUser();
        UserProfile profile = getOrCreateProfile(user);
        
        ProfileDto.ContactDetailsResponse response = new ProfileDto.ContactDetailsResponse();
        response.setEmail(profile.getEmail());
        response.setPhone(profile.getPhone());
        response.setLinkedin(profile.getLinkedin());
        response.setGithub(profile.getGithub());
        response.setWebsite(profile.getWebsite());
        response.setContactVisible(profile.isContactVisible());
        
        return response;
    }

    public ProfileDto.ContactDetailsResponse updateContactDetails(ProfileDto.ContactDetailsRequest req) {
        User user = currentUser();
        UserProfile profile = getOrCreateProfile(user);

        if (req.getEmail() != null)     profile.setEmail(req.getEmail());
        if (req.getPhone() != null)     profile.setPhone(req.getPhone());
        if (req.getLinkedin() != null)  profile.setLinkedin(req.getLinkedin());
        if (req.getGithub() != null)    profile.setGithub(req.getGithub());
        if (req.getWebsite() != null)   profile.setWebsite(req.getWebsite());
        profile.setContactVisible(req.isContactVisible());

        profile = profileRepository.save(profile);
        log.info("Contact details updated for user: {}", user.getUsername());
        
        ProfileDto.ContactDetailsResponse response = new ProfileDto.ContactDetailsResponse();
        response.setEmail(profile.getEmail());
        response.setPhone(profile.getPhone());
        response.setLinkedin(profile.getLinkedin());
        response.setGithub(profile.getGithub());
        response.setWebsite(profile.getWebsite());
        response.setContactVisible(profile.isContactVisible());
        
        return response;
    }

    public ProfileDto.ContactDetailsResponse getContactDetailsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = getOrCreateProfile(user);

        ProfileDto.ContactDetailsResponse response = new ProfileDto.ContactDetailsResponse();
        response.setLinkedin(profile.getLinkedin());
        response.setGithub(profile.getGithub());
        response.setWebsite(profile.getWebsite());
        response.setContactVisible(profile.isContactVisible());
        
        // Only show email and phone if contact visibility is enabled
        if (profile.isContactVisible()) {
            response.setEmail(profile.getEmail());
            response.setPhone(profile.getPhone());
        }
        
        return response;
    }

    // ── Get or create profile ─────────────────────────────────
    private UserProfile getOrCreateProfile(User user) {
        return profileRepository.findByUser(user)
                .orElseGet(() -> {
                    // ← auto create empty profile if not exists
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .contactVisible(false)
                            .profileComplete(false)
                            .build();
                    return profileRepository.save(newProfile);
                });
    }

    // ── Helpers ───────────────────────────────────────────────
    private Long currentUserId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }

    private User currentUser() {
        Long userId = currentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}