package dev.rhmnlm.rpsbackend.service;

import dev.rhmnlm.rpsbackend.dto.PlayerDto;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerDto create(PlayerDto dto) {
        Player player = Player.builder()
                .playerId(UUID.randomUUID())
                .playerName(dto.getPlayerName())
                .build();
        Player saved = playerRepository.save(player);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDto> findById(UUID id) {
        return playerRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDto> findByName(String playerName) {
        return playerRepository.findByPlayerName(playerName).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<PlayerDto> findAll() {
        return playerRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<PlayerDto> update(UUID id, PlayerDto dto) {
        return playerRepository.findById(id)
                .map(player -> {
                    player.setPlayerName(dto.getPlayerName());
                    return toDto(playerRepository.save(player));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PlayerDto toDto(Player player) {
        return PlayerDto.builder()
                .playerId(player.getPlayerId())
                .playerName(player.getPlayerName())
                .build();
    }
}
