package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.ProfileDto;
import com.neurocrew.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto.ProfileResponse>> myProfile() {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getMyProfile(), "Profile fetched successfully")
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileDto.ProfileResponse>> profileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getProfileByUserId(userId), "Profile fetched successfully")
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto.ProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileDto.UpdateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.updateProfile(req), "Profile updated successfully")
        );
    }

    @PostMapping("/me/resume")
    public ResponseEntity<ApiResponse<ProfileDto.ProfileResponse>> uploadResume(
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.uploadResume(file), "Resume uploaded successfully")
        );
    }

    @DeleteMapping("/me/resume")
    public ResponseEntity<ApiResponse<ProfileDto.ProfileResponse>> deleteResume() throws IOException {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.deleteResume(), "Resume deleted successfully")
        );
    }

    @GetMapping("/{userId}/resume")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long userId) throws IOException {
        byte[] bytes = profileService.downloadResume(userId);

        String fileName = "resume.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }

    @GetMapping("/me/contact-details")
    public ResponseEntity<ApiResponse<ProfileDto.ContactDetailsResponse>> getMyContactDetails() {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getMyContactDetails(), "Contact details fetched successfully")
        );
    }

    @PutMapping("/me/contact-details")
    public ResponseEntity<ApiResponse<ProfileDto.ContactDetailsResponse>> updateContactDetails(
            @Valid @RequestBody ProfileDto.ContactDetailsRequest req) {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.updateContactDetails(req), "Contact details updated successfully")
        );
    }

    @GetMapping("/{userId}/contact-details")
    public ResponseEntity<ApiResponse<ProfileDto.ContactDetailsResponse>> getContactDetailsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getContactDetailsByUserId(userId), "Contact details fetched successfully")
        );
    }
}
