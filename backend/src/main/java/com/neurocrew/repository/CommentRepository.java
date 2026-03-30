package com.neurocrew.repository;

import com.neurocrew.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIdea_IdOrderByCreatedAtAsc(Long ideaId);

    long countByIdea_Id(Long ideaId);
}
