package com.neurocrew.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ideas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)      // ← Audit support
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← changed from UUID to IDENTITY
    private Long id;                                      // ← changed from String to Long

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @Column(nullable = false)
    private boolean openToCollab;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    private IdeaStatus status;                           // ← added status field

    @ManyToOne(fetch = FetchType.LAZY)                   // ← changed EAGER to LAZY (better performance)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "idea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollabRequest> requests;

    // ── Audit Fields ────────────────────────────────────────
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;                     // ← handled by JPA auditing now

    @LastModifiedDate
    private LocalDateTime updatedAt;                     // ← new field

    // ── Status Enum ─────────────────────────────────────────
    public enum IdeaStatus {
        DRAFT, PUBLISHED, UNDER_REVIEW, APPROVED, REJECTED
    }
}