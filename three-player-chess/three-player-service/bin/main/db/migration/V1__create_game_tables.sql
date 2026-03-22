-- =========================
-- Games table
-- =========================
CREATE TABLE games (
    id VARCHAR(255) PRIMARY KEY,

    game_status VARCHAR(50) NOT NULL,
    current_player VARCHAR(50) NOT NULL,

    time_control_seconds BIGINT NOT NULL,

    white_remaining_ms BIGINT NOT NULL,
    black_remaining_ms BIGINT NOT NULL,
    red_remaining_ms   BIGINT NOT NULL,

    turn_started_at TIMESTAMP NOT NULL
);

-- =========================
-- Game moves (ElementCollection)
-- =========================
CREATE TABLE game_moves (
    game_id VARCHAR(255) NOT NULL,
    move_index INTEGER NOT NULL,
    move VARCHAR(50) NOT NULL,

    CONSTRAINT pk_game_moves PRIMARY KEY (game_id, move_index),
    CONSTRAINT fk_game_moves_game
        FOREIGN KEY (game_id)
        REFERENCES games(id)
        ON DELETE CASCADE
);

-- Index for faster reconstruction
CREATE INDEX idx_game_moves_game_id
    ON game_moves(game_id);
