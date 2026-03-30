package com.neurocrew.repository;

import com.neurocrew.model.Idea;
import com.neurocrew.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {  // ← Long

    List<Idea> findAllByOrderByCreatedAtDesc();

    List<Idea> findByAuthor(User author);

    List<Idea> findByOpenToCollabTrue();

    List<Idea> findByStatus(Idea.IdeaStatus status);
}