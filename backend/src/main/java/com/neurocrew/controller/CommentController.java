package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.CommentDto;
import com.neurocrew.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas/{ideaId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ── GET all comments for an idea ─────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<?>>> getComments(
            @PathVariable Long ideaId) {
        return ResponseEntity.ok(
            ApiResponse.success(
                commentService.getComments(ideaId),
                "Comments fetched successfully"
            )
        );
    }

    // ── GET comment count ─────────────────────────────────────
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getCount(
            @PathVariable Long ideaId) {
        return ResponseEntity.ok(
            ApiResponse.success(
                commentService.getCommentCount(ideaId),
                "Comment count fetched"
            )
        );
    }

    // ── POST add a comment ────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addComment(
            @PathVariable Long ideaId,
            @Valid @RequestBody CommentDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                commentService.addComment(ideaId, req),
                "Comment added successfully"
            )
        );
    }

    // ── PUT edit a comment ────────────────────────────────────
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @PathVariable Long ideaId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto.UpdateRequest req) {
        return ResponseEntity.ok(
            ApiResponse.success(
                commentService.updateComment(commentId, req),
                "Comment updated successfully"
            )
        );
    }

    // ── DELETE a comment ──────────────────────────────────────
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @PathVariable Long ideaId,
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Comment deleted successfully")
        );
    }
}