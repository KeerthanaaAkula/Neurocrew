package com.neurocrew.dto;

import com.neurocrew.model.CollabRequest;
import com.neurocrew.model.Idea;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class IdeaDto {

    @Data
    public static class CreateRequest {

        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        private String title;

        @NotBlank(message = "Description is required")
        @Size(min = 10, message = "Description must be at least 10 characters")
        private String description;

        private boolean openToCollab;
        private String status;
    }

    // ── NEW: UpdateRequest ────────────────────────────────────
    @Data
    public static class UpdateRequest {

        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        private String title;

        @NotBlank(message = "Description is required")
        @Size(min = 10, message = "Description must be at least 10 characters")
        private String description;

        private boolean openToCollab;

        private Idea.IdeaStatus status;                         // ← directly use enum
    }

    @Data
    public static class IdeaResponse {
        private Long id;
        private String title;
        private String description;
        private boolean openToCollab;
        private String status;
        private Long authorId;
        private String authorName;
        private String authorRole;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String myCollabRequest;
        private String myDevRequest;
        private int commentCount;
        private List<RequestSummary> requests;
        private int totalRequests;

        public static IdeaResponse from(Idea idea, Long currentUserId,
                                        List<CollabRequest> allRequests,
                                        int commentCount) {
            IdeaResponse r = new IdeaResponse();
            r.id = idea.getId();
            r.title = idea.getTitle();
            r.description = idea.getDescription();
            r.openToCollab = idea.isOpenToCollab();
            r.status = idea.getStatus() != null ? idea.getStatus().name() : null;
            r.authorId = idea.getAuthor().getId();
            r.authorName = idea.getAuthor().getUsername();
            r.authorRole = idea.getAuthor().getRole().name();
            r.createdAt = idea.getCreatedAt();
            r.updatedAt = idea.getUpdatedAt();
            r.totalRequests = allRequests.size();
            r.commentCount = commentCount;

            if (currentUserId != null) {
                allRequests.stream()
                    .filter(req -> req.getFromUser().getId().equals(currentUserId))
                    .forEach(req -> {
                        if (req.getType() == CollabRequest.RequestType.COLLAB) {
                            r.myCollabRequest = req.getStatus().name();
                        } else {
                            r.myDevRequest = req.getStatus().name();
                        }
                    });
            }

            r.requests = allRequests.stream()
                .map(RequestSummary::from)
                .toList();

            return r;
        }
    }

    @Data
    public static class RequestSummary {
        private Long id;
        private String fromUsername;
        private String type;
        private String status;

        public static RequestSummary from(CollabRequest r) {
            RequestSummary s = new RequestSummary();
            s.id = r.getId();
            s.fromUsername = r.getFromUser().getUsername();
            s.type = r.getType().name();
            s.status = r.getStatus().name();
            return s;
        }
    }
}