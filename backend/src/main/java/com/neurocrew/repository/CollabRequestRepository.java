package com.neurocrew.repository;

import com.neurocrew.model.CollabRequest;
import com.neurocrew.model.Idea;
import com.neurocrew.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollabRequestRepository extends JpaRepository<CollabRequest, Long> {  // ← Long

    List<CollabRequest> findByFromUser(User fromUser);

    List<CollabRequest> findByIdea(Idea idea);

    Optional<CollabRequest> findByFromUserAndIdeaAndType(User fromUser, Idea idea, CollabRequest.RequestType type);

    List<CollabRequest> findByIdeaAuthor(User ideaAuthor);

    List<CollabRequest> findByStatus(CollabRequest.Status status);

    boolean existsByFromUserAndIdea(User fromUser, Idea idea);
}