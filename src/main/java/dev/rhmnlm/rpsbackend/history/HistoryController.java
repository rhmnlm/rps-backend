package dev.rhmnlm.rpsbackend.history;

import dev.rhmnlm.rpsbackend.dto.BattleHistoryDto;
import dev.rhmnlm.rpsbackend.dto.ErrorResponse;
import dev.rhmnlm.rpsbackend.dto.GameDto;
import dev.rhmnlm.rpsbackend.service.BattleHistoryService;
import dev.rhmnlm.rpsbackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HistoryController {

    private final BattleHistoryService battleHistoryService;
    private final GameService gameService;

    @GetMapping("/battleHistory")
    public ResponseEntity<Object> getBattleHistory(@RequestParam String gameId) {
        if (gameId == null || gameId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("MISSING_GAME_ID", "Game ID is mandatory."));
        }

        List<BattleHistoryDto> history = battleHistoryService.findByGameIdDesc(gameId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/gameHistory")
    public ResponseEntity<Object> getGameHistory(@RequestParam UUID playerId) {
        if (playerId == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("MISSING_PLAYER_ID", "Player ID is mandatory."));
        }

        List<GameDto> games = gameService.findByPlayerId(playerId);
        return ResponseEntity.ok(games);
    }
}