package com.neurocrew.config;

import com.neurocrew.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Public endpoints ───────────────────────────────
                .requestMatchers(
                    "/api/auth/**",
                    "/signup",
                    "/login",
                    "/h2-console/**"
                ).permitAll()

                // ── Role based access ──────────────────────────────
                .requestMatchers("/api/founder/**").hasRole("Founder")
                .requestMatchers("/api/investor/**").hasRole("Investor")
                .requestMatchers("/api/developer/**").hasRole("Developer")
                .requestMatchers("/api/designer/**").hasRole("Designer")

                // ── Profile endpoints ──────────────────────────────
                .requestMatchers("/api/profile/**").authenticated()  // ← all profile endpoints

                // ── Resume download ────────────────────────────────
                .requestMatchers("/uploads/**").authenticated()      // ← protect upload directory

                // ── Comments ──────────────────────────────────────
                .requestMatchers("/api/ideas/*/comments/**").authenticated() // ← comment endpoints

                // ── Everything else ────────────────────────────────
                .anyRequest().authenticated()
            )
            .headers(h -> h
                .frameOptions(f -> f.sameOrigin())
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"  // ← added PATCH
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of(
            "Content-Disposition",                              // ← expose for resume download
            "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}