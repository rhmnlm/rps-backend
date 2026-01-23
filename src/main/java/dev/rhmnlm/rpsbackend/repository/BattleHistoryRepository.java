package dev.rhmnlm.rpsbackend.repository;

import dev.rhmnlm.rpsbackend.entity.BattleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BattleHistoryRepository extends JpaRepository<BattleHistory, UUID> {
    List<BattleHistory> findByGameGameIdOrderByRoundAsc(String gameId);
    List<BattleHistory> findByGameGameIdOrderByCreatedAtDesc(String gameId);
    List<BattleHistory> findByPlayerPlayerId(UUID playerId);
    int countByGameGameId(String gameId);

    // Find the latest battle history entry for a game to infer current round
    Optional<BattleHistory> findTopByGameGameIdOrderByCreatedAtDesc(String gameId);

    // Count consecutive DRAWs for current round
    int countByGameGameIdAndRoundAndResult(String gameId, int round, String result);
}
