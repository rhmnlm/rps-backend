package dev.rhmnlm.rpsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "player")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @Column(name = "player_id")
    private UUID playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "token", nullable = false, unique = true, length = 6)
    private String token;
}
