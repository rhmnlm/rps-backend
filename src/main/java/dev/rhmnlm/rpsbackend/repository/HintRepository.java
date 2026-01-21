package dev.rhmnlm.rpsbackend.repository;

import dev.rhmnlm.rpsbackend.entity.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HintRepository extends JpaRepository<Hint, UUID> {
    Optional<Hint> findByGameGameId(String gameId);
}
