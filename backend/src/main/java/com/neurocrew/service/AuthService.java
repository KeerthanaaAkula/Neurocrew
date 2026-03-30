package com.neurocrew.service;

import com.neurocrew.dto.AuthDto;
import com.neurocrew.model.User;
import com.neurocrew.repository.UserRepository;
import com.neurocrew.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        log.info("Registering user: {}", req.getUsername());

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(req.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + req.getRole()
                + ". Valid roles are: Founder, Developer, Designer, Investor");
            // ← improved error message with valid options
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        String token = jwtUtil.generateToken(user.getId());
        return new AuthDto.AuthResponse(token, AuthDto.UserResponse.from(user));
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        log.info("Login attempt for user: {}", req.getUsername());  // ← added log

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for user: {}", req.getUsername());  // ← security log
            throw new RuntimeException("Invalid credentials");
        }

        log.info("User logged in successfully: {}", user.getUsername());  // ← added log
        String token = jwtUtil.generateToken(user.getId());
        return new AuthDto.AuthResponse(token, AuthDto.UserResponse.from(user));
    }
}