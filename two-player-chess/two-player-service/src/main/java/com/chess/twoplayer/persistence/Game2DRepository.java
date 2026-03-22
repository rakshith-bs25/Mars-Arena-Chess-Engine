package com.chess.twoplayer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Game2DRepository extends JpaRepository<Game2DEntity, String> {
}