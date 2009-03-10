/** GPL >= 2.0
 * Based upon J2ME Minesweeper.
 * Copyright (C) M. Jumari
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package net.eiroca.j2me.minesweeper;

import net.eiroca.j2me.minesweeper.MineSweeper;
import net.eiroca.j2me.minesweeper.game.MineSweeperTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
//#ifdef DMIDP20
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
//#endif
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.minesweeper.game.MineInfo;
import net.eiroca.j2me.minesweeper.game.MineSweeperGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

public final class MineSweeperScreen extends BoardGameScreen {

  private static final int BLKSIZE = 16;
  private static final int TOPSPACE = 20;

  private int sizeY = 11;
  private int sizeX = 10;
  private int base_y = 25;
  private int base_x = 8;
  //#ifdef DMIDP20
  private final Sprite icons;
	//#endif

  private final Image iSmile;
  private final Image iSmileOK;
  private final Image iSmileKO;
  private final Image iAll;

  //#ifdef DMIDP20
  private Player pBomb;
  private Player pTicTac;
	//#endif
  private int sec = 0;
  private long last;
  private final Font textFont;

  int offX;
  int offY;

  private int[][] info;

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("MineSweeperScreen");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finerLoggable = logger.isLoggable(Level.FINER);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public MineSweeperScreen(final GameApp midlet) {
		super(midlet, false, true, MineSweeper.MSG_NAME);
    try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("constructor ");}
			//#endif
			//#ifdef DMIDP20
      pBomb = Manager.createPlayer(getClass().getResourceAsStream(MineSweeper.RES_BOMB), "audio/x-wav");
      pTicTac = Manager.createPlayer(getClass().getResourceAsStream(MineSweeper.RES_CLOCK), "audio/x-wav");
			//#endif
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("constructor pBomb,pTicTac=" + pBomb + "," + pTicTac);}
			//#endif
    }
    catch (final Exception e1) {
      //
			e1.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("construction error", e1);
			//#endif
    }
		iSmile = BaseApp.createImage(MineSweeper.RES_SMILE);
		iSmileOK = BaseApp.createImage(MineSweeper.RES_SMILEOK);
		iSmileKO = BaseApp.createImage(MineSweeper.RES_SMILEKO);
		iAll = BaseApp.createImage(MineSweeper.RES_ICONS);
		icons = new Sprite(iAll, 15, 15);
		icons.setFrame(11);
		rgame = new MineSweeperGame();
		textFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
  }

  public void init() {
		try {
			MineSweeperGame game = (MineSweeperGame)rgame;
			// This (newGame) also creates a new BoardGameScreen.table. 
			if (MineSweeper.usLevel == 3) {
				game.newGame(BoardGameApp.gsCol, BoardGameApp.gsRow, MineSweeper.bomb);
			}
			else {
				if (MineSweeper.usLevel == 0) {
					game.newGame('B');
				}
				if (MineSweeper.usLevel == 1) {
					game.newGame('I');
				}
				if (MineSweeper.usLevel == 2) {
					game.newGame('E');
				}
			}
			super.init();
			final int gsx = game.size_width * MineSweeperScreen.BLKSIZE;
			final int gsy = game.size_height * MineSweeperScreen.BLKSIZE;
			base_x = (screenWidth - gsx) / 2;
			if (base_x < 0) {
				base_x = 0;
			}
			base_y = ((screenHeight - MineSweeperScreen.TOPSPACE) - gsy) / 2;
			if (base_y < 0) {
				base_y = 0;
			}
			base_y = base_y + MineSweeperScreen.TOPSPACE;
			info = new int[game.size_width][game.size_height];
			for (int x = 0; x < game.size_width; x++) {
				for (int y = 0; y < game.size_height; y++) {
					info[x][y] = ((MineSweeperTable)table).field[x][y].status_guess;
				}
			}
			sizeX = screenWidth / MineSweeperScreen.BLKSIZE;
			sizeY = (screenHeight - MineSweeperScreen.TOPSPACE) / MineSweeperScreen.BLKSIZE;
			sely = Math.min(sizeX, game.size_height) / 2;
			selx = Math.min(sizeY, game.size_width) / 2;
			//#ifdef DMIDP20
			VolumeControl vol;
			try {
				vol = (VolumeControl) pBomb.getControl("VolumeControl");
				vol.setLevel(GameApp.usVolume * 20);
				vol = (VolumeControl) pTicTac.getControl("VolumeControl");
				vol.setLevel(GameApp.usVolume * 20);
			}
			//#endif
			catch (final Exception e) {
				//
			}
			sec = 0;
			last = System.currentTimeMillis();
			offX = 0;
			offY = 0;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

  public void localShow() {
    last = System.currentTimeMillis();
    draw(screen);
  }

  public void show() {
    super.show();
    localShow();
  }

  public void hide() {
    sec += System.currentTimeMillis() - last;
  }

  public boolean tick() {
    draw(screen);
    return true;
  }

  public int getElapsed() {
    return (sec + (int) (System.currentTimeMillis() - last)) / 1000;
  }

  protected void drawSelectionBox() {
		screen.setColor(0x00FF0000);
		screen.fillRect(base_x + ((selx - offX) * MineSweeperScreen.BLKSIZE) - 1, base_y + ((sely - offY) * MineSweeperScreen.BLKSIZE) - 1, MineSweeperScreen.BLKSIZE + 1, MineSweeperScreen.BLKSIZE + 1);
	}

  public void draw(final Graphics g) {
    g.setColor(BaseApp.background);
    g.fillRect(0, 0, screenWidth, screenHeight);
		MineSweeperGame game = (MineSweeperGame)rgame;
    if (selx < offX) {
      offX--;
    }
    else if (selx >= (offX + sizeX)) {
      offX++;
    }
    if (offX > (game.size_width - sizeX)) {
      offX = game.size_width - sizeX;
    }
    if (offX < 0) {
      offX = 0;
    }

    if (sely < offY) {
      offY--;
    }
    else if (sely >= (offY + sizeY)) {
      offY++;
    }
    if (offY > (game.size_height - sizeY)) {
      offY = game.size_height - sizeY;
    }
    if (offY < 0) {
      offY = 0;
    }
    for (int x = offX; x < Math.min(offX + sizeX, game.size_width); x++) {
      for (int y = offY; y < Math.min(offY + sizeY, game.size_height); y++) {
				if ((y == sely) && (x == selx)) {
					//undo? g.setColor(0x00FF0000);
					drawSelectionBox();
				}
        if (info[x][y] == MineSweeperGame.MINE_UNCHECKED) {
          icons.setFrame(11);
          icons.setPosition(base_x + ((x - offX) * MineSweeperScreen.BLKSIZE), base_y + ((y - offY) * MineSweeperScreen.BLKSIZE));
          icons.paint(g);
        }
        if (info[x][y] == MineSweeperGame.MINE_CHECKED) {
          icons.setFrame(10);
          icons.setPosition(base_x + ((x - offX) * MineSweeperScreen.BLKSIZE), base_y + ((y - offY) * MineSweeperScreen.BLKSIZE));
          icons.paint(g);
        }
        if (info[x][y] == MineSweeperGame.MINE_BOMB) {
          icons.setFrame(9);
          icons.setPosition(base_x + ((x - offX) * MineSweeperScreen.BLKSIZE), base_y + ((y - offY) * MineSweeperScreen.BLKSIZE));
          icons.paint(g);
        }
        if ((info[x][y] >= 0) && (info[x][y] <= 8)) {
          icons.setFrame(info[x][y]);
          icons.setPosition(base_x + ((x - offX) * MineSweeperScreen.BLKSIZE), base_y + ((y - offY) * MineSweeperScreen.BLKSIZE));
          icons.paint(g);
        }
      }
    }
    if (game.status == MineSweeperGame.GE_RESOLVED) {
      g.drawImage(iSmileOK, screenWidth / 2, 2, Graphics.HCENTER | Graphics.TOP);
    }
    else if (game.status == MineSweeperGame.GE_EXPLODED) {
      g.drawImage(iSmileKO, screenWidth / 2, 2, Graphics.HCENTER | Graphics.TOP);
    }
    else {
      g.drawImage(iSmile, screenWidth / 2, 2, Graphics.HCENTER | Graphics.TOP);
    }
    if (game.status == MineSweeperGame.GE_RUNNING) {
      g.setColor(BaseApp.foreground);
      g.setFont(textFont);
      final int timeElapsed = getElapsed();
      final int min = timeElapsed / 60;
      final int sec = timeElapsed - min * 60;
      g.drawString(BaseApp.lpad("" + min, "0", 2) + ":" + BaseApp.lpad("" + sec, "0", 2), screenWidth - 6, 4, Graphics.RIGHT | Graphics.TOP);
      g.drawString(Integer.toString(game.bomb - game.checked()), 6, 4, 0);
		} else {
			gameEnded = true;
    }
  }

  protected void drawPiece(final int row, final int col, final int player,
			boolean onBoard, Image cupImage, int yadjust, int lastMovePoint) {
		// UNDO MINE
	}

  protected void drawTable() {
		// UNDO MINE
	}

  //#ifdef DTEST
  public
  //#else
  protected
	//#endif
		boolean processMove(final BoardGameMove move, final boolean startForeThinking) {
			return true; // UNDO MINE
		}

	public void procEndGame(byte player) {
		// UNDO MINE
	}

  public void procKeyPressed(final int aKeyCode) {
    final int action = getGameAction(aKeyCode);
    if (gameEnded) {
      midlet.doGameStop();
      return;
    }
		MineSweeperGame game = (MineSweeperGame)rgame;
    if (aKeyCode == Canvas.KEY_NUM0) {
      midlet.doGamePause();
      return;
    }
    else if (action == Canvas.LEFT) {
      if (selx > 0) {
        selx--;
      }
    }
    else if (action == Canvas.RIGHT) {
      if (selx < (game.size_width - 1)) {
        selx++;
      }
    }
    else if (action == Canvas.UP) {
      if (sely > 0) {
        sely--;
      }
    }
    else if (action == Canvas.DOWN) {
      if (sely < (game.size_height - 1)) {
        sely++;
      }
    }
    else if (aKeyCode == Canvas.KEY_NUM1) {
      if ((selx > 0) && (sely > 0)) {
        selx--;
        sely--;
      }
    }
    else if (aKeyCode == Canvas.KEY_NUM3) {
      if ((selx < game.size_width - 1) && (sely > 0)) {
        selx++;
        sely--;
      }
    }
    else if (aKeyCode == Canvas.KEY_NUM9) {
      if ((selx < game.size_width - 1) && (sely < game.size_height - 1)) {
        selx++;
        sely++;
      }
    }
    else if (aKeyCode == Canvas.KEY_NUM7) {
      if ((selx > 0) && (sely < game.size_height - 1)) {
        selx--;
        sely++;
      }
    }
    else if (action == Canvas.FIRE) {
      final Vector v = game.checkCell(selx, sely);
      if (game.status == MineSweeperGame.GE_EXPLODED) {
        GameApp.play(pBomb);
      }
      else {
        GameApp.play(pTicTac);
      }
      for (int i = 0; i < v.size(); i++) {
        final MineInfo m = (MineInfo) v.elementAt(i);
        info[m.x][m.y] = m.status_guess;
      }
    }
    else if (aKeyCode == Canvas.KEY_STAR) {
      if (game.canDoubleClick(selx, sely)) {
        final Vector v = game.doubleClick(selx, sely);
        if (game.status == MineSweeperGame.GE_EXPLODED) {
          GameApp.play(pBomb);
        }
        else {
          GameApp.play(pTicTac);
        }
        for (int i = 0; i < v.size(); i++) {
          final MineInfo m = (MineInfo) v.elementAt(i);
          info[m.x][m.y] = m.status_guess;
        }
      }
    }
    else if (aKeyCode == Canvas.KEY_POUND) {
      if (((info[selx][sely] == MineSweeperGame.MINE_UNCHECKED) && (game.checked() < game.bomb)) || (info[selx][sely] == MineSweeperGame.MINE_CHECKED)) {
        final Vector v = game.markBomb(selx, sely);
        for (int i = 0; i < v.size(); i++) {
          final MineInfo m = (MineInfo) v.elementAt(i);
          info[m.x][m.y] = m.status_guess;
        }
      }
    }
    if (game.status == MineSweeperGame.GE_RESOLVED) {
      final int timeElapsed = -getElapsed();
      score.addScore(timeElapsed);
    }
		gameEnded = (game.status != MineSweeperGame.GE_RUNNING);
  }

}
