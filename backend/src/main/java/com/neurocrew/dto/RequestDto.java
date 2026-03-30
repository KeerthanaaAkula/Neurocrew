package com.neurocrew.dto;

import com.neurocrew.model.CollabRequest;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class RequestDto {

    @Data
    public static class CreateRequest {

        @NotNull(message = "Idea ID is required")
        private Long ideaId;                                          // ← String to Long

        @NotBlank(message = "Type is required")
        @Pattern(
            regexp = "COLLAB|DEV",
            message = "Type must be either COLLAB or DEV"
        )
        private String type;                                          // ← pattern validation added

        @Size(max = 500, message = "Message cannot exceed 500 characters")
        private String message;                                       // ← new: optional message
    }

    @Data
    public static class UpdateRequest {

        @NotBlank(message = "Status is required")
        @Pattern(
            regexp = "ACCEPTED|REJECTED",
            message = "Status must be either ACCEPTED or REJECTED"
        )
        private String status;                                        // ← pattern validation added
    }

    @Data
    public static class RequestResponse {
        private Long id;                                              // ← String to Long
        private Long fromId;                                          // ← new: sender ID
        private String fromName;
        private String fromRole;
        private Long ideaId;                                          // ← String to Long
        private String ideaTitle;
        private String type;
        private String status;
        private String message;                                       // ← new: message field
        private boolean hasResume;
        private String resumeFileName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;                              // ← new: updatedAt

        public static RequestResponse from(CollabRequest r) {
            RequestResponse res = new RequestResponse();
            res.id = r.getId();                                       // ← Long now
            res.fromId = r.getFromUser().getId();                     // ← new
            res.fromName = r.getFromUser().getUsername();
            res.fromRole = r.getFromUser().getRole().name();
            res.ideaId = r.getIdea().getId();                         // ← Long now
            res.ideaTitle = r.getIdea().getTitle();
            res.type = r.getType().name();
            res.status = r.getStatus().name();
            res.message = r.getMessage();                             // ← new
            res.hasResume = r.getResumeFilePath() != null;
            res.resumeFileName = r.getResumeFileName();
            res.createdAt = r.getCreatedAt();
            res.updatedAt = r.getUpdatedAt();                         // ← new
            return res;
        }
    }
}