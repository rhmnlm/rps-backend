-- Add token column to player table
ALTER TABLE player ADD COLUMN token VARCHAR(6);

-- Generate tokens for existing players (uppercase letters and digits only)
UPDATE player SET token = UPPER(SUBSTRING(MD5(RANDOM()::TEXT), 1, 6)) WHERE token IS NULL;

-- Make token not null and unique after populating
ALTER TABLE player ALTER COLUMN token SET NOT NULL;
ALTER TABLE player ADD CONSTRAINT player_token_unique UNIQUE (token);