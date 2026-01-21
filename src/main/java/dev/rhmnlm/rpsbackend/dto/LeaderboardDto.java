package dev.rhmnlm.rpsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDto {
    private UUID id;
    private UUID playerId;
    private String playerName;
    private String gameId;
    private Long durationMs;
    private LocalDateTime registeredAt;
}
