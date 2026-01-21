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
public class HintDto {
    private UUID hintId;
    private Integer hintCount;
    private String gameId;
}
