// Use DBML to define your database structure
// Docs: https://dbml.dbdiagram.io/docs

Table game {
  game_id varchar [primary key]
  movesets varchar [not null, note: 'Comma-separated pre-generated CPU moves (10 rounds)']
  created_at timestamp [not null, default: `now()`]
}

Table battle_history {
  battle_id uuid [primary key]
  round int [not null, note: '1-10, used to index into game.movesets']
  move varchar [not null, note: 'Player move: ROCK, PAPER, or SCISSORS']
  result varchar [not null, note: 'WIN, LOSE, or DRAW']
  hint_used bool [not null, default: false]
  game_id_fk varchar [not null]
  player_id_fk uuid [not null]
}

Table player {
  player_id uuid [primary key]
  player_name varchar [not null, check: `length(player_name) >= 3`]
}

Table hint {
  hint_id uuid [primary key]
  hint_count int [not null, default: 3, note: 'Remaining hints for this game']
  game_id_fk varchar [not null, unique]
}

Table leaderboard {
  id uuid [primary key]
  player_id_fk uuid [not null]
  game_id_fk varchar [not null, unique]
  duration_ms bigint [not null, note: 'Time to complete all 10 rounds in milliseconds']
  registered_at timestamp [not null, default: `now()`]
}

Ref: battle_history.game_id_fk > game.game_id
Ref: battle_history.player_id_fk > player.player_id
Ref: hint.game_id_fk - game.game_id
Ref: leaderboard.game_id_fk - game.game_id
Ref: leaderboard.player_id_fk > player.player_id
