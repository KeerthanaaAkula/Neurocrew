package com.neurocrew.service;

import com.neurocrew.dto.RequestDto;
import com.neurocrew.model.CollabRequest;
import com.neurocrew.model.Idea;
import com.neurocrew.model.User;
import com.neurocrew.repository.CollabRequestRepository;
import com.neurocrew.repository.IdeaRepository;
import com.neurocrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final CollabRequestRepository requestRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    public RequestDto.RequestResponse createRequest(RequestDto.CreateRequest req) {
        User user = currentUser();

        Idea idea = ideaRepository.findById(req.getIdeaId())        // ← Long now
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + req.getIdeaId()));

        if (idea.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot request on your own idea");
        }

        if (!idea.isOpenToCollab()) {
            throw new RuntimeException("This idea is not accepting requests");
        }

        CollabRequest.RequestType type;
        try {
            type = CollabRequest.RequestType.valueOf(req.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid request type. Must be COLLAB or DEV");
            // ← improved error message
        }

        boolean exists = requestRepository
            .findByFromUserAndIdeaAndType(user, idea, type).isPresent();
        if (exists) {
            throw new RuntimeException("You have already sent a " + type + " request for this idea");
            // ← improved error message
        }

        CollabRequest cr = CollabRequest.builder()
                .fromUser(user)
                .idea(idea)
                .type(type)
                .message(req.getMessage())                          // ← new: save message
                .status(CollabRequest.Status.PENDING)
                .build();

        cr = requestRepository.save(cr);
        log.info("Collaboration request created: ID={}, idea='{}', user='{}'",
            cr.getId(), idea.getTitle(), user.getUsername());
        return RequestDto.RequestResponse.from(cr);
    }

    public List<RequestDto.RequestResponse> getIncoming() {
        User user = currentUser();
        log.info("Fetching incoming requests for user: {}", user.getUsername()); // ← added log
        return requestRepository.findByIdeaAuthor(user).stream()
                .map(RequestDto.RequestResponse::from)
                .toList();
    }

    public List<RequestDto.RequestResponse> getOutgoing() {
        User user = currentUser();
        log.info("Fetching outgoing requests for user: {}", user.getUsername()); // ← added log
        return requestRepository.findByFromUser(user).stream()
                .map(RequestDto.RequestResponse::from)
                .toList();
    }

    public RequestDto.RequestResponse updateStatus(Long requestId,  // ← String to Long
                                                    RequestDto.UpdateRequest req) {
        User user = currentUser();

        CollabRequest cr = requestRepository.findById(requestId)    // ← Long now
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        if (!cr.getIdea().getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this request");
        }

        CollabRequest.Status status;
        try {
            status = CollabRequest.Status.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Must be ACCEPTED or REJECTED");
            // ← improved error message
        }

        cr.setStatus(status);
        cr = requestRepository.save(cr);
        log.info("Request ID={} status updated to {} by user '{}'",
            requestId, status, user.getUsername());                 // ← improved log
        return RequestDto.RequestResponse.from(cr);
    }

    public RequestDto.RequestResponse uploadResume(Long requestId, MultipartFile file) throws IOException {
        User user = currentUser();

        CollabRequest cr = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        // Only the request sender can upload their resume for that request.
        if (!cr.getFromUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to upload resume for this request");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getContentType() == null || !file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        // Keep consistent with frontend/backend profile constraints: 5MB limit
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB");
        }

        Path uploadPath = Paths.get(uploadDir).resolve("requests");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Remove old attached resume (if any)
        if (cr.getResumeFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(cr.getResumeFilePath()));
            } catch (IOException e) {
                log.warn("Could not delete old request resume: {}", e.getMessage());
            }
        }

        String fileName = "request_resume_" + requestId + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        cr.setResumeFileName(file.getOriginalFilename());
        cr.setResumeFilePath(filePath.toString());
        cr.setResumeContentType(file.getContentType());

        cr = requestRepository.save(cr);
        return RequestDto.RequestResponse.from(cr);
    }

    // ── Helpers ──────────────────────────────────────────────

    private Long currentUserId() {                                  // ← new helper
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }

    private User currentUser() {
        Long userId = currentUserId();                              // ← String to Long
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}