-- Add timestamp column to battle_history for ordering and DRAW tracking
ALTER TABLE battle_history ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Index for efficient ordering by timestamp within a game
CREATE INDEX idx_battle_history_game_created ON battle_history(game_id_fk, created_at DESC);

-- Add game status to track if game is active, won, or lost (due to loss or excessive DRAWs)
ALTER TABLE game ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
