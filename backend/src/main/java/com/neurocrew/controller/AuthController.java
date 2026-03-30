package com.neurocrew.controller;

import com.neurocrew.dto.ApiResponse;
import com.neurocrew.dto.AuthDto;
import com.neurocrew.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody AuthDto.RegisterRequest req) {
        // ← @Valid added, ApiResponse wrapper added
        // ← try/catch removed (GlobalExceptionHandler handles it now)
        return ResponseEntity.ok(
            ApiResponse.success(authService.register(req), "User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        return ResponseEntity.ok(
            ApiResponse.success(authService.login(req), "Login successful")
        );
    }
}