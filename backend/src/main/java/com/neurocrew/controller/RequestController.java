package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.RequestDto;
import com.neurocrew.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @PreAuthorize("hasAnyRole('Developer', 'Designer', 'Investor')")  // ← only these roles can request
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody RequestDto.CreateRequest req) {
        // ← @Valid added, ApiResponse wrapper, try/catch removed
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(requestService.createRequest(req), "Request created successfully")
        );
    }

    @GetMapping("/incoming")
    @PreAuthorize("hasRole('Founder')")                               // ← only Founders see incoming
    public ResponseEntity<ApiResponse<List<?>>> incoming() {
        return ResponseEntity.ok(
            ApiResponse.success(requestService.getIncoming(), "Incoming requests fetched successfully")
        );
    }

    @GetMapping("/outgoing")
    public ResponseEntity<ApiResponse<List<?>>> outgoing() {
        return ResponseEntity.ok(
            ApiResponse.success(requestService.getOutgoing(), "Outgoing requests fetched successfully")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Founder')")                               // ← only Founders can update status
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,                                    // ← String to Long
            @Valid @RequestBody RequestDto.UpdateRequest req) {       // ← @Valid added
        return ResponseEntity.ok(
            ApiResponse.success(requestService.updateStatus(id, req), "Request status updated successfully")
        );
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<RequestDto.RequestResponse>> uploadResume(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(
                ApiResponse.success(requestService.uploadResume(id, file), "Resume uploaded successfully")
        );
    }
}