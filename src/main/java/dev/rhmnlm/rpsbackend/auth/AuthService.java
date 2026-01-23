package dev.rhmnlm.rpsbackend.auth;

import dev.rhmnlm.rpsbackend.entity.Player;
import dev.rhmnlm.rpsbackend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;

    public Optional<Player> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return playerRepository.findByToken(token);
    }

    public Optional<Player> extractPlayerFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring(7);
        return validateToken(token);
    }
}