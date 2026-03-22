let audioCtx = null;
let lastSpokenWinner = null; // Internal lock to prevent heartbeat cutoffs

const initContext = () => {
    if (!audioCtx) {
        audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
};

const playTone = (freq, type, duration, volume = 0.1) => {
    try {
        initContext();
        const oscillator = audioCtx.createOscillator();
        const gainNode = audioCtx.createGain();

        oscillator.type = type;
        oscillator.frequency.setValueAtTime(freq, audioCtx.currentTime);
        
        gainNode.gain.setValueAtTime(volume, audioCtx.currentTime);
        // Smooth fade out at the end of the long beep
        gainNode.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + duration);

        oscillator.connect(gainNode);
        gainNode.connect(audioCtx.destination);

        oscillator.start();
        oscillator.stop(audioCtx.currentTime + duration);
    } catch (e) {
        console.warn("Audio playback failed", e);
    }
};

export const SoundEngine = {
    playMove: () => {
        playTone(600, 'sine', 0.1, 0.1);
    },
    playNewGame: () => {
        lastSpokenWinner = null; // Reset winner lock for new game
        playTone(200, 'square', 0.1, 0.05);
        setTimeout(() => playTone(400, 'square', 0.1, 0.05), 100);
        setTimeout(() => playTone(600, 'square', 0.2, 0.05), 200);
    },
    playTimeout: () => {
        // Nasal Flatline: Square wave provides the buzzy/nasal harmonic
        playTone(900, 'square', 8.0, 0.03); // Low volume because square is piercing
    },
    playGameOver: () => {
        playTone(440, 'triangle', 0.5, 0.1);
        playTone(554, 'triangle', 0.5, 0.1);
        playTone(659, 'triangle', 0.5, 0.1);
    },
    playQueenPromotion: () => {
        if ('speechSynthesis' in window) {
            const utterance = new SpeechSynthesisUtterance("Yass Queen");
            utterance.pitch = 1.2; 
            utterance.rate = 1.0;
            utterance.volume = 0.6;
            window.speechSynthesis.speak(utterance);
        }
    },
    playGameOverVoice: (winnerName, isThreePlayer = true) => {
        // BLOCKER: If we already announced this winner, don't restart (prevents heartbeat cutoff)
        if (lastSpokenWinner === winnerName) return;
        
        if ('speechSynthesis' in window) {
            window.speechSynthesis.cancel(); // Clear any existing speech
            lastSpokenWinner = winnerName;

            const message = isThreePlayer 
                ? `Match concluded. ${winnerName} is the sovereign of the triad. The other two were mere distractions. Absolute losers.`
                : `Combat terminated. ${winnerName} has crushed the opposition. Your soul was too slow for Mars. Absolute loser.`;

            const utterance = new SpeechSynthesisUtterance(message);
            utterance.pitch = 0.5; 
            utterance.rate = 0.9;  // Slightly faster ensures it finishes before next sync
            utterance.volume = 1.0;

            // Small delay to ensure cancel() finishes
            setTimeout(() => {
                window.speechSynthesis.speak(utterance);
            }, 150);
        }
    },

    // Used for the Flatline Beep (keeping name for compatibility)
    playDevilLaugh: () => {
        if ('speechSynthesis' in window) {
            window.speechSynthesis.cancel();
        }
        // 8.0 seconds of Nasal Square Wave flatline
        playTone(950, 'square', 8.0, 0.03);
    },
};