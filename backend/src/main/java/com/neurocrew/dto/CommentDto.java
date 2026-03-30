package com.neurocrew.dto;

import com.neurocrew.model.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class CommentDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Content is required")
        @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
        private String content;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank(message = "Content is required")
        @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
        private String content;
    }

    @Data
    public static class CommentResponse {
        private Long id;
        private String content;

        private Long authorId;
        private String authorName;
        private String authorRole;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean edited;

        public static CommentResponse from(Comment c) {
            CommentResponse r = new CommentResponse();
            r.id = c.getId();
            r.content = c.getContent();
            r.authorId = c.getAuthor().getId();
            r.authorName = c.getAuthor().getUsername();
            r.authorRole = c.getAuthor().getRole().name();
            r.createdAt = c.getCreatedAt();
            r.updatedAt = c.getUpdatedAt();
            r.edited = c.isEdited();
            return r;
        }
    }
}
