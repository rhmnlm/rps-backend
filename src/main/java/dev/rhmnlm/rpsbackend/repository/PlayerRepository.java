package dev.rhmnlm.rpsbackend.repository;

import dev.rhmnlm.rpsbackend.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional<Player> findByPlayerName(String playerName);
    Optional<Player> findByToken(String token);
    boolean existsByToken(String token);
}
