-- Drop old element-collection table
DROP TABLE IF EXISTS game_moves;

-- Recreate as proper entity table
CREATE TABLE game_moves (
    id BIGSERIAL PRIMARY KEY,
    game_id VARCHAR(36) NOT NULL,
    move_index INT NOT NULL,

    from_tile VARCHAR(4) NOT NULL,
    to_tile   VARCHAR(4) NOT NULL,

    player VARCHAR(16) NOT NULL,
    piece  VARCHAR(16) NOT NULL,

    time_taken_ms BIGINT NOT NULL,
    remaining_time_ms BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_game_moves_game
        FOREIGN KEY (game_id)
        REFERENCES games(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_game_move_index
        UNIQUE (game_id, move_index)
);
