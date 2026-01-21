package dev.rhmnlm.rpsbackend.service;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.rhmnlm.rpsbackend.dto.GameDto;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Player;
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
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public GameDto create(GameDto dto) {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        Game game = Game.builder()
                .gameId("game_" + UuidCreator.getTimeOrderedEpoch())
                .movesets(dto.getMovesets() != null ? dto.getMovesets() : "")
                .player(player)
                .build();
        Game saved = gameRepository.save(game);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<GameDto> findById(String id) {
        return gameRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<GameDto> findByPlayerId(UUID playerId) {
        return gameRepository.findByPlayerPlayerId(playerId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameDto> findAll() {
        return gameRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<GameDto> update(String id, GameDto dto) {
        return gameRepository.findById(id)
                .map(game -> {
                    game.setMovesets(dto.getMovesets());
                    return toDto(gameRepository.save(game));
                });
    }

    @Transactional
    public boolean delete(String id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private GameDto toDto(Game game) {
        return GameDto.builder()
                .gameId(game.getGameId())
                .movesets(game.getMovesets())
                .createdAt(game.getCreatedAt())
                .playerId(game.getPlayer() != null ? game.getPlayer().getPlayerId() : null)
                .build();
    }
}
