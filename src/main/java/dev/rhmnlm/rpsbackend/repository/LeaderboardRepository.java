package dev.rhmnlm.rpsbackend.repository;

import dev.rhmnlm.rpsbackend.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    List<Leaderboard> findAllByOrderByDurationMsAsc();
    List<Leaderboard> findByPlayerPlayerIdOrderByDurationMsAsc(UUID playerId);
    Optional<Leaderboard> findByGameGameId(String gameId);
    List<Leaderboard> findTop10ByOrderByDurationMsAsc();
}
