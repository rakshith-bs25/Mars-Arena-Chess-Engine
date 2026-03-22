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

import java.awt.*;
import java.net.*;
import java.io.*;

import javax.swing.*;
import javax.swing.JPanel;

import java.util.Properties;
import java.util.logging.Logger;

/** Class representing the game interface which is seen by a player and
 * where are lockated available for player opptions, current games and where
 * can he start a new game (load it or save it)
 */
public class GUI
{

    public Game game;
    static final public Properties configFile = GUI.getConfigFile();
        // current theme as a reference object (replaces raw String value)
        private static Theme currentTheme = initTheme();

        private static Theme initTheme() {
            if (configFile == null) {
                return new Theme("default");
            }
            String themeName = configFile.getProperty("THEME", "default");
            return new Theme(themeName);
        }
    
        /** Allow other parts of the app to update the current theme. */
        public static void setTheme(Theme theme) {
            if (theme != null) {
                currentTheme = theme;
            }
        }
    
        public static Theme getTheme() {
            return currentTheme;
        }
    

    public GUI()
    {
        this.game = new Game();

        //this.drawGUI();
    }/*--endOf-GUI--*/

    /*Method load image by a given name with extension
     * @name     : string of image to load for ex. "chessboard.jpg"
     * @returns  : image or null if cannot load
     * */

     static Image loadImage(String name)
     {
         if (configFile == null)
         {
             return null;
         }
         Image img = null;
         URL url = null;
         Toolkit tk = Toolkit.getDefaultToolkit();
         try
         {
             // Use Theme reference instead of manually building the path
             String imageLink = currentTheme.imagePath(name);
             System.out.println("Current theme: " + currentTheme.getName());
             url = JChessApp.class.getResource(imageLink);
             img = tk.getImage(url);
 
         }
         catch (Exception e)
         {
             System.out.println("some error loading image!");
             e.printStackTrace();
         }
         return img;
     }
 

    static boolean themeIsValid(String name)
    {
        return true;
    }

    static String getJarPath()
    {
        String path = GUI.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = path.replaceAll("[a-zA-Z0-9%!@#$%^&*\\(\\)\\[\\]\\{\\}\\.\\,\\s]+\\.jar", "");
        int lastSlash = path.lastIndexOf(File.separator); 
        if(path.length()-1 == lastSlash)
        {
            path = path.substring(0, lastSlash);
        }
        path = path.replace("%20", " ");
        return path;
    }

    static Properties getConfigFile() {
        Properties conf = new Properties();
    
        // Preferred: load from classpath: /jchess/config.txt
        try (InputStream in = GUI.class.getResourceAsStream("/jchess/config.txt")) {
            if (in != null) {
                conf.load(in);
                return conf;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Fallback 1: next to the jar (legacy behavior)
        File outFile = new File(GUI.getJarPath() + File.separator + "config.txt");
        if (outFile.exists()) {
            try (InputStream in = new FileInputStream(outFile)) {
                conf.load(in);
                return conf;
            } catch (IOException ignored) {}
        }
    
        // Fallback 2: working directory (last resort)
        try (InputStream in = new FileInputStream("config.txt")) {
            conf.load(in);
        } catch (IOException ignored) {}
    
        return conf;
    }
}
