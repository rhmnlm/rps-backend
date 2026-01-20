-- Player table (no dependencies)
CREATE TABLE player (
    player_id UUID PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL CHECK (length(player_name) >= 3)
);

-- Game table (no dependencies)
CREATE TABLE game (
    game_id VARCHAR(255) PRIMARY KEY,
    movesets VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Battle history table (depends on game, player)
CREATE TABLE battle_history (
    battle_id UUID PRIMARY KEY,
    round INT NOT NULL,
    move VARCHAR(20) NOT NULL,
    result VARCHAR(10) NOT NULL,
    hint_used BOOLEAN NOT NULL DEFAULT FALSE,
    game_id_fk VARCHAR(255) NOT NULL REFERENCES game(game_id),
    player_id_fk UUID NOT NULL REFERENCES player(player_id)
);

-- Hint table (one-to-one with game)
CREATE TABLE hint (
    hint_id UUID PRIMARY KEY,
    hint_count INT NOT NULL DEFAULT 3,
    game_id_fk VARCHAR(255) NOT NULL UNIQUE REFERENCES game(game_id)
);

-- Leaderboard table (one-to-one with game, many-to-one with player)
CREATE TABLE leaderboard (
    id UUID PRIMARY KEY,
    player_id_fk UUID NOT NULL REFERENCES player(player_id),
    game_id_fk VARCHAR(255) NOT NULL UNIQUE REFERENCES game(game_id),
    duration_ms BIGINT NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for foreign keys (improves JOIN performance)
CREATE INDEX idx_battle_history_game_id ON battle_history(game_id_fk);
CREATE INDEX idx_battle_history_player_id ON battle_history(player_id_fk);
CREATE INDEX idx_leaderboard_player_id ON leaderboard(player_id_fk);

-- Index for leaderboard ranking queries (fastest completions)
CREATE INDEX idx_leaderboard_duration ON leaderboard(duration_ms ASC);
