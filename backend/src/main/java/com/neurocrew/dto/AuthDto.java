package com.neurocrew.dto;

import com.neurocrew.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        private String username;                                // ← validation added

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;                                // ← validation added

        @NotBlank(message = "Role is required")
        @Pattern(
            regexp = "Founder|Developer|Designer|Investor",
            message = "Role must be one of: Founder, Developer, Designer, Investor"
        )
        private String role;                                    // ← pattern validation added
    }

    @Data
    public static class LoginRequest {

        @NotBlank(message = "Username is required")
        private String username;                                // ← validation added

        @NotBlank(message = "Password is required")
        private String password;                                // ← validation added
    }

    @Data
    public static class UserResponse {
        private Long id;                                        // ← String to Long
        private String username;
        private String role;

        public static UserResponse from(User user) {
            UserResponse r = new UserResponse();
            r.id = user.getId();                                // ← Long now
            r.username = user.getUsername();
            r.role = user.getRole().name();
            return r;
        }
    }

    @Data
    public static class AuthResponse {
        private String token;
        private UserResponse user;

        public AuthResponse(String token, UserResponse user) {
            this.token = token;
            this.user = user;
        }
    }
}