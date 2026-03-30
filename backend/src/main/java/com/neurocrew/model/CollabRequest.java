package com.neurocrew.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "collab_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)       // ← Audit support
public class CollabRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← changed from UUID to IDENTITY
    private Long id;                                      // ← changed from String to Long

    @ManyToOne(fetch = FetchType.LAZY)                   // ← changed EAGER to LAZY (better performance)
    @JoinColumn(name = "from_user_id", nullable = false)
    @NotNull(message = "From user is required")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)                   // ← changed EAGER to LAZY
    @JoinColumn(name = "idea_id", nullable = false)
    @NotNull(message = "Idea is required")
    private Idea idea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Request type is required")
    private RequestType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(length = 500)
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;                              // ← new: optional message with request

    // ── Resume metadata attached to this request ─────────────
    private String resumeFileName;
    private String resumeFilePath;
    private String resumeContentType;

    // ── Audit Fields ─────────────────────────────────────────
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;                     // ← handled by JPA auditing now

    @LastModifiedDate
    private LocalDateTime updatedAt;                     // ← new field

    // ── Enums ────────────────────────────────────────────────
    public enum RequestType { COLLAB, DEV }

    public enum Status { PENDING, ACCEPTED, REJECTED }
}