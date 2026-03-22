/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess;

import java.io.Serializable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Class representing game settings available for the current player.
 *
 * NOTE:
 *  - We keep the original public fields and enum names for backwards compatibility.
 *  - We add accessors and helper methods so new code can follow the Law of Demeter
 *    and use methods instead of directly manipulating fields.
 */
public class Settings implements Serializable {

    private static ResourceBundle loc = null;

    // ---- original public fields (kept for compatibility) ----
    public int timeForGame;
    public boolean runningChat;
    public boolean runningGameClock;
    /** tells us if player chose a time limit or it's infinity */
    public boolean timeLimitSet;
    public boolean upsideDown;

    public enum gameModes {
        newGame, loadGame
    }
    public gameModes gameMode;

    public Player playerWhite;
    public Player playerBlack;

    public enum gameTypes {
        local, network
    }
    public gameTypes gameType;

    public boolean renderLabels = true;

    // ---- constructor ----
    public Settings() {
        this.playerWhite = new Player("", "white");
        this.playerBlack = new Player("", "black");
        this.timeLimitSet = false;

        this.gameMode = gameModes.newGame;
        this.gameType = gameTypes.local; // sensible default
    }

    // --------------------------------------------------------------------
    // Accessors / helper methods (for cleaner design & Law of Demeter)
    // --------------------------------------------------------------------

    // time control
    /** Old method kept as-is for compatibility. */
    public int getTimeForGame() {
        return this.timeForGame;
    }

    /** New explicit setter for time-for-game. */
    public void setTimeForGame(int timeForGame) {
        this.timeForGame = timeForGame;
    }

    // players
    public Player getWhitePlayer() {
        return playerWhite;
    }

    public Player getBlackPlayer() {
        return playerBlack;
    }

    public void setWhitePlayer(Player player) {
        this.playerWhite = player;
    }

    public void setBlackPlayer(Player player) {
        this.playerBlack = player;
    }

    /**
     * Convenience method to configure both players in one place.
     * This is useful for "Replace Value with Reference" and "Factory Method" refactorings.
     */
    public void configurePlayers(String whiteName, String blackName) {
        if (playerWhite == null) {
            playerWhite = new Player("", "white");
        }
        if (playerBlack == null) {
            playerBlack = new Player("", "black");
        }

        playerWhite.setName(whiteName);
        playerBlack.setName(blackName);
        playerWhite.setType(Player.playerTypes.localUser);
        playerBlack.setType(Player.playerTypes.localUser);
    }

    // game mode / type
    public gameModes getGameMode() {
        return gameMode;
    }

    public void setGameMode(gameModes mode) {
        this.gameMode = mode;
    }

    public gameTypes getGameType() {
        return gameType;
    }

    public void setGameType(gameTypes type) {
        this.gameType = type;
    }

    // flags
    public boolean isRunningChat() {
        return runningChat;
    }

    public void setRunningChat(boolean runningChat) {
        this.runningChat = runningChat;
    }

    public boolean isRunningGameClock() {
        return runningGameClock;
    }

    public void setRunningGameClock(boolean runningGameClock) {
        this.runningGameClock = runningGameClock;
    }

    public boolean isTimeLimitSet() {
        return timeLimitSet;
    }

    public void setTimeLimitSet(boolean timeLimitSet) {
        this.timeLimitSet = timeLimitSet;
    }

    public boolean isUpsideDown() {
        return upsideDown;
    }

    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
    }

    public boolean isRenderLabels() {
        return renderLabels;
    }

    public void setRenderLabels(boolean renderLabels) {
        this.renderLabels = renderLabels;
    }

    // --------------------------------------------------------------------
    // i18n helper (kept from original)
    // --------------------------------------------------------------------
    public static String lang(String key) {
        if (Settings.loc == null) {
            Settings.loc = PropertyResourceBundle.getBundle("jchess.resources.i18n.main");
            Locale.setDefault(Locale.ENGLISH);
        }
        String result;
        try {
            result = Settings.loc.getString(key);
        } catch (java.util.MissingResourceException exc) {
            result = key;
        }
        System.out.println(Settings.loc.getLocale().toString());
        return result;
    }
}
