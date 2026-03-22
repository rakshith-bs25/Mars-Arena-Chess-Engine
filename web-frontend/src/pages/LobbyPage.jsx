import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import lobbyVideo from '../assets/theme/lobby-bg.mp4';

export default function LobbyPage() {
  const navigate = useNavigate();
  const videoRef = useRef(null);
  const [isMuted, setIsMuted] = useState(true);

  const toggleAudio = () => {
    if (videoRef.current) {
      videoRef.current.muted = !videoRef.current.muted;
      setIsMuted(videoRef.current.muted);
    }
  };

  return (
    <div className="lobby-container">
      {}
      <video 
        ref={videoRef}
        className="bg-video" 
        autoPlay 
        loop 
        muted 
        playsInline
      >
        <source src={lobbyVideo} type="video/mp4" />
      </video>

      {/* Dark Gradient Overlay */}
      <div className="video-overlay" />

      {/* Main Branding */}
      <h1 className="glitch-title">MARS ARENA</h1>
      <p className="glitch-subtitle"><b>THE SOUL BLITZ</b></p>
      
      {/* Audio Toggle HUD Element */}
      <button className="audio-toggle-hud" onClick={toggleAudio}>
        {isMuted ? "PLAY TO KNOW!" : "SHUT ME DOWN, FOOL!"}
        <span className={isMuted ? "audio-wave-static" : "audio-wave-active"}></span>
      </button>

      {/* Entry HUD Cards */}
      <div 
        className="hero-card card-top-left"
        onClick={() => navigate('/two-player-chess')}
      >
        <div className="card-title">DUAL STRIKE</div>
        <div className="card-desc">ARENA X · 1v1 CLASSIC</div>
      </div>

      <div 
        className="hero-card card-top-right"
        onClick={() => navigate('/three-player-chess')}
      >
        <div className="card-title">TRIAD VOID</div>
        <div className="card-desc">MARS CORE · 3 PLAYER HEX</div>
      </div>

      {/* Bottom HUD info */}
      <div className="hud-bottom-info">
        PLANETARY STATUS: MARS SECURE // CONNECTION: {isMuted ? "SILENT" : "ENCRYPTED"} // CCD-M2
      </div>
    </div>
  );
}