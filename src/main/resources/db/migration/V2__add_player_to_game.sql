-- Add player reference to game table
ALTER TABLE game ADD COLUMN player_id_fk UUID REFERENCES player(player_id);

-- Create index for the foreign key
CREATE INDEX idx_game_player_id ON game(player_id_fk);
