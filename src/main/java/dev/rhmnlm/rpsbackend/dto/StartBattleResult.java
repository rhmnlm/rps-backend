package dev.rhmnlm.rpsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartBattleResult {
    private boolean success;
    private String errorCode;
    private String errorMessage;
    private PlayerStatsDto playerStats;

    public static StartBattleResult success(PlayerStatsDto playerStats) {
        return StartBattleResult.builder()
                .success(true)
                .playerStats(playerStats)
                .build();
    }

    public static StartBattleResult error(String errorCode, String errorMessage) {
        return StartBattleResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}