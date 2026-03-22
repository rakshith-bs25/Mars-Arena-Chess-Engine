package jchess;

import javax.swing.*;
import java.awt.*;

public class ChessboardView extends JPanel {
  private final BoardModel model;

  public ChessboardView(BoardModel model) { this.model = model; setDoubleBuffered(true); }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // temporarily delegate to existing Chessboard painting later; for now simple background:
    g.setColor(new Color(0xF0,0xD9,0xB5));
    g.fillRect(0,0,getWidth(),getHeight());
  }
}
