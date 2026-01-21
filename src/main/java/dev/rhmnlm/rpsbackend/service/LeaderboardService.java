package dev.rhmnlm.rpsbackend.service;

import dev.rhmnlm.rpsbackend.dto.LeaderboardDto;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Leaderboard;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.repository.GameRepository;
import dev.rhmnlm.rpsbackend.repository.LeaderboardRepository;
import dev.rhmnlm.rpsbackend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Transactional
    public LeaderboardDto create(LeaderboardDto dto) {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Leaderboard leaderboard = Leaderboard.builder()
                .id(UUID.randomUUID())
                .player(player)
                .game(game)
                .durationMs(dto.getDurationMs())
                .build();
        Leaderboard saved = leaderboardRepository.save(leaderboard);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<LeaderboardDto> findById(UUID id) {
        return leaderboardRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDto> findAllRanked() {
        return leaderboardRepository.findAllByOrderByDurationMsAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDto> findByPlayerId(UUID playerId) {
        return leaderboardRepository.findByPlayerPlayerIdOrderByDurationMsAsc(playerId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<LeaderboardDto> findByGameId(String gameId) {
        return leaderboardRepository.findByGameGameId(gameId).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardDto> findAll() {
        return leaderboardRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<LeaderboardDto> update(UUID id, LeaderboardDto dto) {
        return leaderboardRepository.findById(id)
                .map(leaderboard -> {
                    leaderboard.setDurationMs(dto.getDurationMs());
                    return toDto(leaderboardRepository.save(leaderboard));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (leaderboardRepository.existsById(id)) {
            leaderboardRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private LeaderboardDto toDto(Leaderboard leaderboard) {
        return LeaderboardDto.builder()
                .id(leaderboard.getId())
                .playerId(leaderboard.getPlayer().getPlayerId())
                .playerName(leaderboard.getPlayer().getPlayerName())
                .gameId(leaderboard.getGame().getGameId())
                .durationMs(leaderboard.getDurationMs())
                .registeredAt(leaderboard.getRegisteredAt())
                .build();
    }
}
