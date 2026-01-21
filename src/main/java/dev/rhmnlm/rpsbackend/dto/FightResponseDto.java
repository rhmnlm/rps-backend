package dev.rhmnlm.rpsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FightResponseDto {
    private String gameId;
    private Integer round;
    private String playerMove;
    private String cpuMove;
    private String result;        // WIN, LOSE, DRAW
    private String gameStatus;    // ACTIVE, WON, LOST
    private Integer drawCount;    // consecutive draws in current round
}