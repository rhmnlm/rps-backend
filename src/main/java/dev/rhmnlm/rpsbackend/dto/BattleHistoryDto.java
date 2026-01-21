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
public class BattleHistoryDto {
    private UUID battleId;
    private Integer round;
    private String move;
    private String result;
    private Boolean hintUsed;
    private String gameId;
    private UUID playerId;
}
