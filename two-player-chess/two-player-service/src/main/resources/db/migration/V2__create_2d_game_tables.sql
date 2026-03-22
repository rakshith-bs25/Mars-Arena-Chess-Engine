-- Create the main games table for 2D
CREATE TABLE games_2d (
    id VARCHAR(255) PRIMARY KEY,
    game_status VARCHAR(50) NOT NULL,
    current_player VARCHAR(50) NOT NULL,
    time_control_seconds BIGINT NOT NULL,
    white_remaining_ms BIGINT NOT NULL,
    black_remaining_ms BIGINT NOT NULL,
    turn_started_at TIMESTAMP NOT NULL
);

-- Create the moves history table for 2D
CREATE TABLE game_moves_2d (
    id BIGSERIAL PRIMARY KEY,
    game_id VARCHAR(255) NOT NULL,
    move_index INT NOT NULL,
    from_x INT NOT NULL,
    from_y INT NOT NULL,
    to_x INT NOT NULL,
    to_y INT NOT NULL,
    player VARCHAR(16) NOT NULL,
    piece  VARCHAR(16) NOT NULL,
    time_taken_ms BIGINT NOT NULL,
    remaining_time_ms BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_game_moves_2d_game
        FOREIGN KEY (game_id)
        REFERENCES games_2d(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_game_move_2d_index
        UNIQUE (game_id, move_index)
);

-- Index for performance
CREATE INDEX idx_game_moves_2d_game_id ON game_moves_2d(game_id);