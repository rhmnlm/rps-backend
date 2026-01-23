package dev.rhmnlm.rpsbackend.service;

import dev.rhmnlm.rpsbackend.dto.BattleHistoryDto;
import dev.rhmnlm.rpsbackend.entity.BattleHistory;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.repository.BattleHistoryRepository;
import dev.rhmnlm.rpsbackend.repository.GameRepository;
import dev.rhmnlm.rpsbackend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BattleHistoryService {

    private final BattleHistoryRepository battleHistoryRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public BattleHistoryDto create(BattleHistoryDto dto) {
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        BattleHistory battleHistory = BattleHistory.builder()
                .battleId(UUID.randomUUID())
                .round(dto.getRound())
                .move(dto.getMove())
                .result(dto.getResult())
                .hintUsed(dto.getHintUsed() != null ? dto.getHintUsed() : false)
                .game(game)
                .player(player)
                .build();
        BattleHistory saved = battleHistoryRepository.save(battleHistory);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<BattleHistoryDto> findById(UUID id) {
        return battleHistoryRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<BattleHistoryDto> findByGameId(String gameId) {
        return battleHistoryRepository.findByGameGameIdOrderByRoundAsc(gameId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BattleHistoryDto> findByGameIdDesc(String gameId) {
        return battleHistoryRepository.findByGameGameIdOrderByCreatedAtDesc(gameId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BattleHistoryDto> findByPlayerId(UUID playerId) {
        return battleHistoryRepository.findByPlayerPlayerId(playerId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public int getCurrentRound(String gameId) {
        return battleHistoryRepository.countByGameGameId(gameId);
    }

    @Transactional(readOnly = true)
    public List<BattleHistoryDto> findAll() {
        return battleHistoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<BattleHistoryDto> update(UUID id, BattleHistoryDto dto) {
        return battleHistoryRepository.findById(id)
                .map(battleHistory -> {
                    battleHistory.setRound(dto.getRound());
                    battleHistory.setMove(dto.getMove());
                    battleHistory.setResult(dto.getResult());
                    battleHistory.setHintUsed(dto.getHintUsed());
                    return toDto(battleHistoryRepository.save(battleHistory));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (battleHistoryRepository.existsById(id)) {
            battleHistoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private BattleHistoryDto toDto(BattleHistory battleHistory) {
        return BattleHistoryDto.builder()
                .battleId(battleHistory.getBattleId())
                .round(battleHistory.getRound())
                .move(battleHistory.getMove())
                .result(battleHistory.getResult())
                .hintUsed(battleHistory.getHintUsed())
                .gameId(battleHistory.getGame().getGameId())
                .playerId(battleHistory.getPlayer().getPlayerId())
                .build();
    }
}
