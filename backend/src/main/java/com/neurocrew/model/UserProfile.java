package com.neurocrew.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Size(max = 300)
    private String bio;

    @Size(max = 100)
    private String location;

    @Size(max = 100)
    private String website;

    @Size(max = 100)
    private String linkedin;

    @Size(max = 100)
    private String github;

    @Size(max = 15)
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    /**
     * Stored as a comma-separated string, e.g. "React, Node, SQL"
     * (Frontend sends the same format.)
     */
    @Size(max = 500)
    private String skills;

    @Column(nullable = false)
    @Builder.Default
    private boolean contactVisible = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean profileComplete = false;

    // ── Resume metadata ─────────────────────────────────────────
    private String resumeFileName;
    private String resumeFilePath;
    private String resumeContentType;

    // ── Audit Fields ─────────────────────────────────────────────
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
