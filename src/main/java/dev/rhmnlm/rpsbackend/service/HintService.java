package dev.rhmnlm.rpsbackend.service;

import dev.rhmnlm.rpsbackend.dto.HintDto;
import dev.rhmnlm.rpsbackend.dto.HintResponseDto;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Hint;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.enums.RPS;
import dev.rhmnlm.rpsbackend.repository.BattleHistoryRepository;
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
    private final BattleHistoryRepository battleHistoryRepository;

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

    @Transactional
    public Optional<HintResponseDto> getHint(String gameId, Player authenticatedPlayer) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return Optional.empty();
        }

        Game game = gameOpt.get();

        // Validate ownership
        if (!game.getPlayer().getPlayerId().equals(authenticatedPlayer.getPlayerId())) {
            return Optional.empty();
        }

        // Check if game is still active
        if (!"ACTIVE".equals(game.getStatus())) {
            return Optional.of(HintResponseDto.builder()
                    .gameId(gameId)
                    .hint("Game is no longer active.")
                    .hintsRemaining(0)
                    .build());
        }

        // Check hints remaining
        Optional<Hint> hintOpt = hintRepository.findByGameGameId(gameId);
        if (hintOpt.isEmpty() || hintOpt.get().getHintCount() <= 0) {
            return Optional.of(HintResponseDto.builder()
                    .gameId(gameId)
                    .hint("No hints remaining!")
                    .hintsRemaining(0)
                    .build());
        }

        Hint hint = hintOpt.get();

        // Infer current round
        int currentRound = inferCurrentRound(gameId);

        // Get CPU move for current round
        String[] movesets = game.getMovesets().split(",");
        RPS cpuMove = RPS.fromString(movesets[currentRound - 1]);

        // Generate hint based on round
        String hintMessage = generateHintMessage(currentRound, cpuMove);

        // Decrement hint count
        hint.setHintCount(hint.getHintCount() - 1);
        hintRepository.save(hint);

        return Optional.of(HintResponseDto.builder()
                .gameId(gameId)
                .round(currentRound)
                .hint(hintMessage)
                .hintsRemaining(hint.getHintCount())
                .build());
    }

    private int inferCurrentRound(String gameId) {
        return battleHistoryRepository.findTopByGameGameIdOrderByCreatedAtDesc(gameId)
                .map(lastBattle -> {
                    if ("WIN".equals(lastBattle.getResult())) {
                        return lastBattle.getRound() + 1;
                    }
                    // DRAW stays on same round
                    return lastBattle.getRound();
                })
                .orElse(1);
    }

    private String generateHintMessage(int round, RPS cpuMove) {
        if (round <= 3) {
            // Direct hint - reveal CPU move
            return String.format("Let me see...Maybe I'll play %s", cpuMove.name());
        } else if (round <= 7) {
            // Tricky hint - say "opposite of" what beats the CPU move
            RPS whatBeatsCpu = cpuMove.beatenBy();
            return String.format("Hehe...maybe I'll play the opposite of %s", whatBeatsCpu.name());
        } else {
            // Deny hint
            return "You came this far. Why should I give you an advantage?";
        }
    }

    private HintDto toDto(Hint hint) {
        return HintDto.builder()
                .hintId(hint.getHintId())
                .hintCount(hint.getHintCount())
                .gameId(hint.getGame().getGameId())
                .build();
    }
}
