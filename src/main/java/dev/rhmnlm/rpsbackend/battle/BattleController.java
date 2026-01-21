package dev.rhmnlm.rpsbackend.battle;

import dev.rhmnlm.rpsbackend.dto.ErrorResponse;
import dev.rhmnlm.rpsbackend.dto.FightRequestDto;
import dev.rhmnlm.rpsbackend.dto.PlayerStatsDto;
import dev.rhmnlm.rpsbackend.dto.StartBattleRequest;
import dev.rhmnlm.rpsbackend.enums.RPS;
import dev.rhmnlm.rpsbackend.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BattleController {

    private static final Logger log = LoggerFactory.getLogger(BattleController.class);
    private final BattleService battleService;

    @PostMapping("/startBattle")
    public ResponseEntity<PlayerStatsDto> startGame(@RequestBody StartBattleRequest request) {
        if (request.getPlayerName() == null || request.getPlayerName().length() < 3) {
            return ResponseEntity.badRequest().build();
        }
        PlayerStatsDto playerStats = battleService.startBattle(request.getPlayerName());
        return ResponseEntity.ok(playerStats);
    }

    @PostMapping("/fight")
    public ResponseEntity<Object> fight(@RequestBody FightRequestDto request) {
        if (request.getGameId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("MISSING_GAME_ID", "Game ID is mandatory."));
        }
        if (request.getPlayerMove() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("EMPTY_MOVE", "Player move is needed."));
        }

        return battleService.fight(request.getGameId(), request.getPlayerMove())
                .map(response -> ResponseEntity.ok((Object) response))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ErrorResponse("GAME_NOT_FOUND", "Game not found.")));
    }
}