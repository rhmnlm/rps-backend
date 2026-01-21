package dev.rhmnlm.rpsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "battle_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleHistory {

    @Id
    @Column(name = "battle_id")
    private UUID battleId;

    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "move", nullable = false, length = 20)
    private String move;

    @Column(name = "result", nullable = false, length = 10)
    private String result;

    @Column(name = "hint_used", nullable = false)
    private Boolean hintUsed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id_fk", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id_fk", nullable = false)
    private Player player;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
