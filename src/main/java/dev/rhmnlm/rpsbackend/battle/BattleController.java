package dev.rhmnlm.rpsbackend.battle;

import dev.rhmnlm.rpsbackend.auth.AuthInterceptor;
import dev.rhmnlm.rpsbackend.dto.ErrorResponse;
import dev.rhmnlm.rpsbackend.dto.FightRequestDto;
import dev.rhmnlm.rpsbackend.dto.StartBattleRequest;
import dev.rhmnlm.rpsbackend.dto.StartBattleResult;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.service.BattleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;

    @PostMapping("/startBattle")
    public ResponseEntity<Object> startGame(@RequestBody StartBattleRequest request) {
        if (request.getPlayerName() == null || request.getPlayerName().length() < 3) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID_NAME", "Player name must be at least 3 characters."));
        }

        StartBattleResult result = battleService.startBattle(request.getPlayerName(), request.getToken());

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(result.getErrorCode(), result.getErrorMessage()));
        }

        return ResponseEntity.ok(result.getPlayerStats());
    }

    @PostMapping("/fight")
    public ResponseEntity<Object> fight(@RequestBody FightRequestDto request, HttpServletRequest httpRequest) {
        if (request.getGameId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("MISSING_GAME_ID", "Game ID is mandatory."));
        }
        if (request.getPlayerMove() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("EMPTY_MOVE", "Player move is needed."));
        }

        Player player = (Player) httpRequest.getAttribute(AuthInterceptor.AUTHENTICATED_PLAYER);

        return battleService.fight(request.getGameId(), request.getPlayerMove(), player)
                .map(response -> ResponseEntity.ok((Object) response))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ErrorResponse("GAME_NOT_FOUND", "Game not found or not owned by you.")));
    }
}