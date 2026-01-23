package dev.rhmnlm.rpsbackend.auth;

import dev.rhmnlm.rpsbackend.entity.Player;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTHENTICATED_PLAYER = "authenticatedPlayer";

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        Optional<Player> playerOpt = authService.extractPlayerFromHeader(authHeader);

        if (playerOpt.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing token. Use Bearer <token> in Authorization header.\"}");
            return false;
        }

        request.setAttribute(AUTHENTICATED_PLAYER, playerOpt.get());
        return true;
    }
}