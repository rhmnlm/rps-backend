package dev.rhmnlm.rpsbackend.service;

import dev.rhmnlm.rpsbackend.dto.HintDto;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Hint;
import dev.rhmnlm.rpsbackend.repository.GameRepository;
import dev.rhmnlm.rpsbackend.repository.HintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HintService {

    private final HintRepository hintRepository;
    private final GameRepository gameRepository;

    @Transactional
    public HintDto create(HintDto dto) {
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Hint hint = Hint.builder()
                .hintId(UUID.randomUUID())
                .hintCount(dto.getHintCount() != null ? dto.getHintCount() : 3)
                .game(game)
                .build();
        Hint saved = hintRepository.save(hint);
        return toDto(saved);
    }

    @Transactional
    public HintDto createForGame(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Hint hint = Hint.builder()
                .hintId(UUID.randomUUID())
                .hintCount(3)
                .game(game)
                .build();
        Hint saved = hintRepository.save(hint);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<HintDto> findById(UUID id) {
        return hintRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<HintDto> findByGameId(String gameId) {
        return hintRepository.findByGameGameId(gameId).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<HintDto> findAll() {
        return hintRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<HintDto> update(UUID id, HintDto dto) {
        return hintRepository.findById(id)
                .map(hint -> {
                    hint.setHintCount(dto.getHintCount());
                    return toDto(hintRepository.save(hint));
                });
    }

    @Transactional
    public Optional<HintDto> decrementHint(String gameId) {
        return hintRepository.findByGameGameId(gameId)
                .map(hint -> {
                    if (hint.getHintCount() > 0) {
                        hint.setHintCount(hint.getHintCount() - 1);
                        return toDto(hintRepository.save(hint));
                    }
                    return toDto(hint);
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (hintRepository.existsById(id)) {
            hintRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private HintDto toDto(Hint hint) {
        return HintDto.builder()
                .hintId(hint.getHintId())
                .hintCount(hint.getHintCount())
                .gameId(hint.getGame().getGameId())
                .build();
    }
}
