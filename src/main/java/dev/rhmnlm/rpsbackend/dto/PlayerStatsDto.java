package dev.rhmnlm.rpsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsDto {
    private String gameId;
    private UUID playerId;
    private String playerName;
    private Integer currentRound;
    private Integer hintsLeft;
}
