package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.AuthDto;
import com.neurocrew.dto.IdeaDto;
import com.neurocrew.dto.RequestDto;
import com.neurocrew.service.AuthService;
import com.neurocrew.service.IdeaService;
import com.neurocrew.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Deprecated                                                         // ← marked as deprecated
public class LegacyApiController {

    private final AuthService authService;
    private final IdeaService ideaService;
    private final RequestService requestService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(
            @Valid @RequestBody AuthDto.RegisterRequest req) {      // ← @Valid added
        log.warn("Legacy /signup endpoint used. Please migrate to /api/auth/register");
        // ← warn instead of info (legacy endpoints should be migrated)
        return ResponseEntity.ok(
            ApiResponse.success(authService.register(req), "User registered successfully")
        );
        // ← ApiResponse wrapper, try/catch removed
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody AuthDto.LoginRequest req) {         // ← @Valid added
        log.warn("Legacy /login endpoint used. Please migrate to /api/auth/login");
        return ResponseEntity.ok(
            ApiResponse.success(authService.login(req), "Login successful")
        );
    }

    @PostMapping("/ideas")
    public ResponseEntity<ApiResponse<?>> createIdea(
            @Valid @RequestBody IdeaDto.CreateRequest req) {        // ← @Valid added
        log.warn("Legacy /ideas endpoint used. Please migrate to /api/ideas");
        return ResponseEntity.ok(
            ApiResponse.success(ideaService.createIdea(req), "Idea created successfully")
        );
    }

    @PostMapping("/collab-request")
    public ResponseEntity<ApiResponse<?>> collabRequest(
            @Valid @RequestBody RequestDto.CreateRequest req) {     // ← @Valid added
        log.warn("Legacy /collab-request endpoint used. Please migrate to /api/requests");
        return ResponseEntity.ok(
            ApiResponse.success(requestService.createRequest(req), "Request created successfully")
        );
    }
}