package dev.rhmnlm.rpsbackend.dto;

import dev.rhmnlm.rpsbackend.enums.RPS;
import lombok.Data;

@Data
public class FightRequestDto {
    String gameId;
    RPS playerMove;
}
