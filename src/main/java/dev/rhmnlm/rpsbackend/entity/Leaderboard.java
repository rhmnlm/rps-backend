package dev.rhmnlm.rpsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leaderboard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id_fk", nullable = false)
    private Player player;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id_fk", nullable = false, unique = true)
    private Game game;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }
}
