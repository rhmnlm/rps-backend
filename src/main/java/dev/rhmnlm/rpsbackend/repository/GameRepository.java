package dev.rhmnlm.rpsbackend.repository;

import dev.rhmnlm.rpsbackend.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findByPlayerPlayerId(UUID playerId);
}
