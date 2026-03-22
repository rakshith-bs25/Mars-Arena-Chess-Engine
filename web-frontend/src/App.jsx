import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LobbyPage from './pages/LobbyPage';
import TwoPlayerGamePage from './pages/TwoPlayerGamePage';
import ThreePlayerGamePage from './pages/ThreePlayerGamePage';
import './App.css';

function App() {
  return (
    <Routes>
      {/* Lobby / Game Selection */}
      <Route path="/" element={<LobbyPage />} />

      {/* 2-Player Mode */}
      <Route path="/two-player-chess" element={<TwoPlayerGamePage />} />

      {/* 3-Player Mode */}
      <Route path="/three-player-chess" element={<ThreePlayerGamePage />} />
      <Route path="/three-player-chess/:gameId" element={<ThreePlayerGamePage />} />

      {/* Catch-all: Redirect unknown routes to Lobby */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;