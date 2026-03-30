package com.neurocrew.service;

import com.neurocrew.dto.CommentDto;
import com.neurocrew.model.Comment;
import com.neurocrew.model.Idea;
import com.neurocrew.model.User;
import com.neurocrew.repository.CommentRepository;
import com.neurocrew.repository.IdeaRepository;
import com.neurocrew.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<CommentDto.CommentResponse> getComments(Long ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + ideaId));

        return commentRepository
                .findByIdea_IdOrderByCreatedAtAsc(idea.getId())
                .stream()
                .map(CommentDto.CommentResponse::from)
                .toList();
    }

    @Transactional
    public long getCommentCount(Long ideaId) {
        return commentRepository.countByIdea_Id(ideaId);
    }

    @Transactional
    public CommentDto.CommentResponse addComment(Long ideaId, CommentDto.CreateRequest req) {
        User user = currentUser();

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + ideaId));

        Comment c = Comment.builder()
                .content(req.getContent().trim())
                .author(user)
                .idea(idea)
                .edited(false)
                .build();

        c = commentRepository.save(c);
        log.info("Comment added: id={}, ideaId={}, author={}", c.getId(), ideaId, user.getUsername());
        return CommentDto.CommentResponse.from(c);
    }

    @Transactional
    public CommentDto.CommentResponse updateComment(Long commentId, CommentDto.UpdateRequest req) {
        User user = currentUser();

        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (!c.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this comment");
        }

        c.setContent(req.getContent().trim());
        c.setEdited(true);
        c = commentRepository.save(c);

        return CommentDto.CommentResponse.from(c);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User user = currentUser();

        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (!c.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(c);
    }

    private Long currentUserId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }

    private User currentUser() {
        Long userId = currentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
