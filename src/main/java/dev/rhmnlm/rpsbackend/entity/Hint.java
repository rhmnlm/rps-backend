package dev.rhmnlm.rpsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "hint")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hint {

    @Id
    @Column(name = "hint_id")
    private UUID hintId;

    @Column(name = "hint_count", nullable = false)
    private Integer hintCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id_fk", nullable = false, unique = true)
    private Game game;

    @PrePersist
    protected void onCreate() {
        if (hintCount == null) {
            hintCount = 3;
        }
    }
}
