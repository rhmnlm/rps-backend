package dev.rhmnlm.rpsbackend.service;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.rhmnlm.rpsbackend.dto.FightResponseDto;
import dev.rhmnlm.rpsbackend.dto.PlayerStatsDto;
import dev.rhmnlm.rpsbackend.entity.BattleHistory;
import dev.rhmnlm.rpsbackend.entity.Game;
import dev.rhmnlm.rpsbackend.entity.Hint;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.enums.RPS;
import dev.rhmnlm.rpsbackend.repository.GameRepository;
import dev.rhmnlm.rpsbackend.repository.HintRepository;
import dev.rhmnlm.rpsbackend.repository.PlayerRepository;
import dev.rhmnlm.rpsbackend.repository.BattleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BattleService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final HintRepository hintRepository;
    private final BattleHistoryRepository battleHistoryRepository;

    @Transactional
    public PlayerStatsDto startBattle(String playerName) {
        // Find or create player
        Player player = playerRepository.findByPlayerName(playerName)
                .orElseGet(() -> {
                    Player newPlayer = Player.builder()
                            .playerId(UuidCreator.getTimeOrderedEpoch())
                            .playerName(playerName)
                            .build();
                    return playerRepository.save(newPlayer);
                });

        // Create new game
        Game game = Game.builder()
                .gameId("game_" + UuidCreator.getTimeOrderedEpoch())
                .movesets(RPS.generateMoveSets(10))
                .player(player)
                .build();
        game = gameRepository.save(game);

        // Create hint for the game (default 3 hints)
        Hint hint = Hint.builder()
                .hintId(UuidCreator.getTimeOrderedEpoch())
                .hintCount(3)
                .game(game)
                .build();
        hintRepository.save(hint);

        return PlayerStatsDto.builder()
                .gameId(game.getGameId())
                .playerId(player.getPlayerId())
                .playerName(player.getPlayerName())
                .currentRound(0)
                .hintsLeft(3)
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getPlayerStats(String gameId) {
        return gameRepository.findById(gameId)
                .map(game -> {
                    int currentRound = battleHistoryRepository.countByGameGameId(gameId);
                    int hintsLeft = hintRepository.findByGameGameId(gameId)
                            .map(Hint::getHintCount)
                            .orElse(0);

                    return PlayerStatsDto.builder()
                            .gameId(game.getGameId())
                            .playerId(game.getPlayer().getPlayerId())
                            .playerName(game.getPlayer().getPlayerName())
                            .currentRound(currentRound)
                            .hintsLeft(hintsLeft)
                            .build();
                });
    }

    private static final int MAX_DRAWS_PER_ROUND = 5;
    private static final int TOTAL_ROUNDS = 10;

    @Transactional
    public Optional<FightResponseDto> fight(String gameId, RPS playerMove) {
        // Find the game
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return Optional.empty();
        }

        Game game = gameOpt.get();

        // Check if game is still active
        if (!"ACTIVE".equals(game.getStatus())) {
            return Optional.of(FightResponseDto.builder()
                    .gameId(gameId)
                    .gameStatus(game.getStatus())
                    .build());
        }

        // Infer current round from latest battle history
        int currentRound = inferCurrentRound(gameId);

        // Check if already exceeded max draws for this round
        int drawCount = battleHistoryRepository.countByGameGameIdAndRoundAndResult(gameId, currentRound, "DRAW");
        if (drawCount >= MAX_DRAWS_PER_ROUND) {
            game.setStatus("LOST");
            gameRepository.save(game);
            return Optional.of(FightResponseDto.builder()
                    .gameId(gameId)
                    .round(currentRound)
                    .gameStatus("LOST")
                    .drawCount(drawCount)
                    .build());
        }

        // Get CPU move from movesets
        String[] movesets = game.getMovesets().split(",");
        RPS cpuMove = RPS.fromString(movesets[currentRound - 1]);

        // Determine result
        String result = determineResult(playerMove, cpuMove);

        // Save battle history
        BattleHistory battleHistory = BattleHistory.builder()
                .battleId(UuidCreator.getTimeOrderedEpoch())
                .round(currentRound)
                .move(playerMove.name())
                .result(result)
                .hintUsed(false)
                .game(game)
                .player(game.getPlayer())
                .build();
        battleHistoryRepository.save(battleHistory);

        // Update game status based on result
        String gameStatus = "ACTIVE";

        if ("LOSE".equals(result)) {
            game.setStatus("LOST");
            gameRepository.save(game);
            gameStatus = "LOST";
        } else if ("DRAW".equals(result)) {
            // Check if this draw causes exceeding max draws
            int newDrawCount = drawCount + 1;
            if (newDrawCount >= MAX_DRAWS_PER_ROUND) {
                game.setStatus("LOST");
                gameRepository.save(game);
                gameStatus = "LOST";
            }
            drawCount = newDrawCount;
        } else if ("WIN".equals(result) && currentRound == TOTAL_ROUNDS) {
            game.setStatus("WON");
            gameRepository.save(game);
            gameStatus = "WON";
        }

        return Optional.of(FightResponseDto.builder()
                .gameId(gameId)
                .round(currentRound)
                .playerMove(playerMove.name())
                .cpuMove(cpuMove.name())
                .result(result)
                .gameStatus(gameStatus)
                .drawCount("DRAW".equals(result) ? drawCount : null)
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
                .orElse(1); // No history means round 1
    }

    private String determineResult(RPS playerMove, RPS cpuMove) {
        if (playerMove == cpuMove) {
            return "DRAW";
        }
        return playerMove.beats(cpuMove) ? "WIN" : "LOSE";
    }
}
