package com.neurocrew.service;

import com.neurocrew.dto.IdeaDto;
import com.neurocrew.model.CollabRequest;
import com.neurocrew.model.Idea;
import com.neurocrew.model.User;
import com.neurocrew.repository.CollabRequestRepository;
import com.neurocrew.repository.CommentRepository;
import com.neurocrew.repository.IdeaRepository;
import com.neurocrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final CollabRequestRepository requestRepository;
    private final CommentRepository commentRepository;

    public List<IdeaDto.IdeaResponse> getAllIdeas() {
        Long userId = currentUserId();                              // ← String to Long
        return ideaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(idea -> {
                    List<CollabRequest> reqs = requestRepository.findByIdea(idea);
                    int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());
                    return IdeaDto.IdeaResponse.from(idea, userId, reqs, commentCount);
                })
                .toList();
    }

    public List<IdeaDto.IdeaResponse> getMyIdeas() {
        User user = currentUser();
        return ideaRepository.findByAuthor(user).stream()
                .map(idea -> {
                    List<CollabRequest> reqs = requestRepository.findByIdea(idea);
                    int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());
                    return IdeaDto.IdeaResponse.from(idea, user.getId(), reqs, commentCount);
                })
                .toList();
    }

    public IdeaDto.IdeaResponse createIdea(IdeaDto.CreateRequest req) {
        User user = currentUser();
        log.info("Creating idea '{}' for user {}", req.getTitle(), user.getUsername());

        Idea idea = Idea.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .openToCollab(req.isOpenToCollab())
                .status(Idea.IdeaStatus.DRAFT)                     // ← default status on create
                .author(user)
                .build();

        idea = ideaRepository.save(idea);
        log.info("Idea created successfully with ID: {}", idea.getId());
        return IdeaDto.IdeaResponse.from(idea, user.getId(), List.of(), 0);
    }

    // ── New Methods ──────────────────────────────────────────

    public List<IdeaDto.IdeaResponse> getOpenIdeas() {            // ← new
        Long userId = currentUserId();
        return ideaRepository.findByOpenToCollabTrue().stream()
                .map(idea -> {
                    List<CollabRequest> reqs = requestRepository.findByIdea(idea);
                    int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());
                    return IdeaDto.IdeaResponse.from(idea, userId, reqs, commentCount);
                })
                .toList();
    }

    public void deleteIdea(Long id) {                              // ← new
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        log.info("Deleting idea with ID: {}", id);
        ideaRepository.delete(idea);
    }

    public IdeaDto.IdeaResponse updateStatus(Long id, Idea.IdeaStatus status) {  // ← new
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        idea.setStatus(status);
        idea = ideaRepository.save(idea);
        log.info("Updated idea {} status to {}", id, status);
        Long userId = currentUserId();
        List<CollabRequest> reqs = requestRepository.findByIdea(idea);
        int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());
        return IdeaDto.IdeaResponse.from(idea, userId, reqs, commentCount);
    }

    public IdeaDto.IdeaResponse getIdeaById(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));

        Long userId = currentUserId();
        List<CollabRequest> reqs = requestRepository.findByIdea(idea);
        int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());

        return IdeaDto.IdeaResponse.from(idea, userId, reqs, commentCount);
    }

    public IdeaDto.IdeaResponse updateIdea(Long id, IdeaDto.UpdateRequest req) {
        User user = currentUser();

        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));

        if (!idea.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this idea");
        }

        idea.setTitle(req.getTitle());
        idea.setDescription(req.getDescription());
        idea.setOpenToCollab(req.isOpenToCollab());
        if (req.getStatus() != null) {
            idea.setStatus(req.getStatus());
        }

        idea = ideaRepository.save(idea);

        List<CollabRequest> collabRequests = requestRepository.findByIdea(idea);
        int commentCount = (int) commentRepository.countByIdea_Id(idea.getId());

        return IdeaDto.IdeaResponse.from(idea, user.getId(), collabRequests, commentCount);
    }

    // ── Helpers ──────────────────────────────────────────────

    private Long currentUserId() {                                 // ← changed String to Long
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);                                 // ← parse to Long
    }

    private User currentUser() {
        Long userId = currentUserId();
        return userRepository.findById(userId)                     // ← Long id
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}