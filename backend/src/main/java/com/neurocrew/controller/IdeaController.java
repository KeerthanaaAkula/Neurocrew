package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.IdeaDto;
import com.neurocrew.model.Idea;
import com.neurocrew.service.IdeaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaController {

    private final IdeaService ideaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<?>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.getAllIdeas(), "Ideas fetched successfully")
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<?>>> getMine() {
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.getMyIdeas(), "Your ideas fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.getIdeaById(id), "Idea fetched successfully")
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Founder', 'Developer', 'Designer')")
    public ResponseEntity<ApiResponse<?>> create(
            @Valid @RequestBody IdeaDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(ideaService.createIdea(req), "Idea created successfully")
        );
    }

    @PutMapping("/{id}")                                        // ← Edit idea
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody IdeaDto.UpdateRequest req) {    // ← new UpdateRequest DTO
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.updateIdea(id, req), "Idea updated successfully")
        );
    }

    @DeleteMapping("/{id}")                                     // ← Delete idea
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        ideaService.deleteIdea(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Idea deleted successfully")
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('Founder')")
    public ResponseEntity<ApiResponse<?>> updateStatus(
            @PathVariable Long id,
            @RequestParam Idea.IdeaStatus status) {
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.updateStatus(id, status), "Status updated successfully")
        );
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<?>>> getOpenIdeas() {
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.getOpenIdeas(), "Open ideas fetched successfully")
        );
    }
}