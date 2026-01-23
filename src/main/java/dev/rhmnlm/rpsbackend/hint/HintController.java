package dev.rhmnlm.rpsbackend.hint;

import dev.rhmnlm.rpsbackend.auth.AuthInterceptor;
import dev.rhmnlm.rpsbackend.dto.ErrorResponse;
import dev.rhmnlm.rpsbackend.dto.HintRequestDto;
import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.service.HintService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HintController {

    private final HintService hintService;

    @PostMapping("/hint")
    public ResponseEntity<Object> getHint(@RequestBody HintRequestDto request, HttpServletRequest httpRequest) {
        if (request.getGameId() == null || request.getGameId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("MISSING_GAME_ID", "Game ID is mandatory."));
        }

        Player player = (Player) httpRequest.getAttribute(AuthInterceptor.AUTHENTICATED_PLAYER);

        return hintService.getHint(request.getGameId(), player)
                .map(response -> ResponseEntity.ok((Object) response))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ErrorResponse("GAME_NOT_FOUND", "Game not found or not owned by you.")));
    }
}