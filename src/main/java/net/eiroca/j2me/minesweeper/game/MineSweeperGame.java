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
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.minesweeper.game;

import java.util.Vector;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.GameMove;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.BoardGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif


public class MineSweeperGame extends BoardGame {

  public static final int GE_RUNNING = 0;
  public static final int GE_EXPLODED = 1;
  public static final int GE_RESOLVED = 2;

  public static final int MINE_MIN_SIZE = 9;
  public static final int MINE_MAX_SIZE = 30;
  public static final int MINE_CHECKED = -100;
  public static final int MINE_UNCHECKED = -200;
  public static final int MINE_BOMB = -300;
  public static final int MIN_BOMB = 9;
  public static final int MAX_BOMB = 99;

  public int size_width = 9;
  public int size_height = 9;
  public int bomb = 10;
  public int status = MineSweeperGame.GE_RUNNING;

	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("MineSweeperGame");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

  public int getGameResult(final byte player) {
	  if (status == MineSweeperGame.GE_EXPLODED) {
      return TwoPlayerGame.LOSS;
		} else if (status == MineSweeperGame.GE_RESOLVED) {
      return TwoPlayerGame.WIN;
		} else {
      return TwoPlayerGame.DRAW; // UNDO MINE
		}
  }

  public int getTblPoint(final GameTable table, final byte player) {
    if (!(table instanceof MineSweeperTable)) { return 0; }
		return 0; // UNDO MINE
		/*
    return (((MineSweeperTable)rTable).getPoint(player) -
				((MineSweeperTable)rTable).getPoint((byte)(1 - player)));
				*/
  }

  public MineSweeperGame() {
    //
  }

  /**
   * Fungsi untuk membuat game baru dengan paramater Level B = Beginer, I = Intermediate, E = Expert
   */
  public void newGame(final char level) {
    if (level == 'B') {
      newGame(9, 9, 10);
    }
    if (level == 'I') {
      newGame(15, 17, 40);
    }
    if (level == 'E') {
      newGame(25, 20, 99);
    }
  }

  /**
   * Fungsi untuk membuat game baru dengan parameter lebar, tinggi dan jumlah bomb
   * @param width lebar game
   * @param height tinggi game
   * @param bomb jumlah bomb
   */
  public void newGame(final int width, final int height, final int bomb) {
    size_width = width;
    size_height = height;
    this.bomb = bomb;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("newGame size_width,size_height,bomb=" + size_width + "," + size_height + "," + bomb);}
		//#endif
    init();
    status = MineSweeperGame.GE_RUNNING;
  }

  public boolean turn(GameTable t, byte player, GameMove move, GameTable newTable) {
		return true; // UNDO MINE use chekCell, etc.
	}

  public GameTable[] animatedTurn(GameTable table, byte player, GameMove move, GameTable newt) {
		return null; // UNDO MINE use chekCell, etc.
	}

  /**
   * Fungi untuk klik kiri game
   * @param x koordinat x
   * @param y koordinat y
   */
  public Vector checkCell(final int x, final int y) {
    final Vector result = new Vector();
    final MineInfo m = ((MineSweeperTable)rTable).field[x][y];
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("checkCell x,y,m.status_guess=" + x + "," + y + "," + m.status_guess);}
		//#endif
    if (m.status_guess == MineSweeperGame.MINE_UNCHECKED) {
      if (m.status_real > 0) {
        m.status_guess = m.status_real;
        result.addElement(m);
      }
      else if (m.status_real == MineSweeperGame.MINE_BOMB) {
        showBomb(result);
      }
      else if (m.status_real == 0) {
        m.status_guess = m.status_real;
        result.addElement(m);
        checkMines(result);
      }
    }
    checkVictory();
    return result;
  }

  /**
   * Fungi untuk klik kanan game
   * @param x koordinat x
   * @param y koordinat y
   */
  public Vector markBomb(final int x, final int y) {
    final Vector result = new Vector();
    final MineInfo m = ((MineSweeperTable)rTable).field[x][y];
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("markBomb x,y,m.status_guess=" + x + "," + y + "," + m.status_guess);}
		//#endif
    if (m.status_guess == MineSweeperGame.MINE_CHECKED) {
      m.status_guess = MineSweeperGame.MINE_UNCHECKED;
      result.addElement(m);
    }
    else {
      m.status_guess = MineSweeperGame.MINE_CHECKED;
      result.addElement(m);
    }
    return result;
  }

  public void doubleCheck(final Vector result, final MineInfo mi) {
    if ((mi.status_guess == MineSweeperGame.MINE_UNCHECKED) && (mi.status_real == MineSweeperGame.MINE_BOMB)) {
      showBomb(result);
    }
    if (mi.status_guess == MineSweeperGame.MINE_UNCHECKED) {
      mi.status_guess = mi.status_real;
      result.addElement(mi);
      if (mi.status_real == 0) {
        checkMines(result);
      }
    }
  }

  public boolean canDoubleClick(final int x, final int y) {
    final MineInfo m = ((MineSweeperTable)rTable).field[x][y];
    boolean result = false;
    int bombs = 0;
    if (m.status_guess > 0) {
      for (int dx = -1; dx < 2; dx++) {
        int nx = x + dx;
        if ((nx >= 0) && (nx < size_width)) {
          for (int dy = -1; dy < 2; dy++) {
            if ((dx == 0) & (dy == 0)) {
              continue;
            }
            int ny = y + dy;
            if ((ny >= 0) && (ny < size_height)) {
              if (((MineSweeperTable)rTable).field[nx][ny].status_guess == MineSweeperGame.MINE_CHECKED) {
                bombs++;
              }
            }
          }
        }
      }
      result = (m.status_guess == bombs);
    }
    return result;
  }

  /**
   * Fungi untuk klik ganda game
   * @param x koordinat x
   * @param y koordinat y
   */
  public Vector doubleClick(final int x, final int y) {
    final Vector result = new Vector();
    for (int dx = -1; dx < 2; dx++) {
      if (status == MineSweeperGame.GE_EXPLODED) {
        break;
      }
      int nx = x + dx;
      if ((nx >= 0) && (nx < size_width)) {
        for (int dy = -1; dy < 2; dy++) {
          if ((dx == 0) & (dy == 0)) {
            continue;
          }
          int ny = y + dy;
          if ((ny >= 0) && (ny < size_height)) {
            doubleCheck(result, ((MineSweeperTable)rTable).field[nx][ny]);
          }
          if (status == MineSweeperGame.GE_EXPLODED) {
            break;
          }
        }
      }
    }
    checkVictory();
    return result;
  }

  /**
   * Fungsi untuk mencari jumlah kotak yang ditandai
   * @return jumlah kotak yang ditandai dan belum terbuka
   */
  public int checked() {
    int c_checked = 0;
    for (int y = 0; y < size_height; y++) {
      for (int x = 0; x < size_width; x++) {
        if (((MineSweeperTable)rTable).field[x][y].status_guess == MineSweeperGame.MINE_CHECKED) {
          c_checked++;
        }
      }
    }
    return c_checked;
  }

  /**
   * Fungsi untuk mencari jumlah kotak yang belum ditandai
   * @return jumlah kotak yang belum ditandai dan belum terbuka
   */
  private int unchecked() {
    int c_unchecked = 0;
    for (int y = 0; y < size_height; y++) {
      for (int x = 0; x < size_width; x++) {
        if (((MineSweeperTable)rTable).field[x][y].status_guess == MineSweeperGame.MINE_UNCHECKED) {
          c_unchecked++;
        }
      }
    }
    return c_unchecked;
  }

  /**
   * Fungsi untuk mengecek menang
   */
  private void checkVictory() {
    if (checked() + unchecked() == bomb) {
      status = MineSweeperGame.GE_RESOLVED;
    }
  }

  private void check(final Vector vector, final MineInfo mi) {
    if (!vector.contains(mi)) {
      if ((mi.status_guess == MineSweeperGame.MINE_UNCHECKED) && (mi.status_real >= 0)) {
        mi.status_guess = mi.status_real;
        vector.addElement(mi);
      }
    }
  }

  /**
   * Fungsi untuk membuka sekitar
   * @param res vector temporery yang digunakan
   */
  private void checkMines(final Vector res) {
    int i = 0;
    while (true) {
      if (i >= res.size()) {
        break;
      }
      final MineInfo m = (MineInfo) res.elementAt(i);
      if (m.status_real == 0) {
        for (int dx = -1; dx < 2; dx++) {
          int nx = m.x + dx;
          if ((nx >= 0) && (nx < size_width)) {
            for (int dy = -1; dy < 2; dy++) {
              if ((dx == 0) & (dy == 0)) {
                continue;
              }
              int ny = m.y + dy;
              if ((ny >= 0) && (ny < size_height)) {
                check(res, ((MineSweeperTable)rTable).field[nx][ny]);
              }
            }
          }
        }
      }
      i++;
    }
  }

  /**
   * Fungsi untuk membuka semua bomb
   */
  private void showBomb(final Vector result) {
    status = MineSweeperGame.GE_EXPLODED;
    result.removeAllElements();
    for (int y = 0; y < size_height; y++) {
      for (int x = 0; x < size_width; x++) {
        if ((((MineSweeperTable)rTable).field[x][y].status_guess == MineSweeperGame.MINE_UNCHECKED) &&
				(((MineSweeperTable)rTable).field[x][y].status_real == MineSweeperGame.MINE_BOMB)) {
          ((MineSweeperTable)rTable).field[x][y].status_guess = MineSweeperGame.MINE_BOMB;
          result.addElement(((MineSweeperTable)rTable).field[x][y]);
        }
      }
    }
  }

  /**
   * Fungsi untuk membuat kotak game baru
   */
  private void init() {
    // buat kotak kosong
    BoardGameScreen.table = new MineSweeperTable(size_width, size_height,
			BoardGameApp.gsNbrPlayers[BoardGameApp.PD_CURR], bomb);
  }

  public boolean isGameEnded(BoardGame bg, BoardGameTable t,
			byte player) {
    if (!(bg instanceof MineSweeperGame)) { return false; }
    if (!(t instanceof MineSweeperTable)) { return false; }
		return (((MineSweeperGame)bg).status != MineSweeperGame.GE_RUNNING);
	}

  public boolean isGameEnded() {
		return isGameEnded(this, rTable, rPlayer);
	}

  public void eval(boolean lazyProcess, BoardGame bg, GameTable t,
			final byte player, boolean endGame) {
		// UNDO MINE
	}

  public GameMove[] possibleMoves(final GameTable table, final byte player) {
		return null; // UNDO MINE
	}

  protected void setTable(final GameTable table, final byte player, final boolean fullProcess) {
    if (!(table instanceof MineSweeperTable)) { throw new IllegalArgumentException(); }
    rTable = (MineSweeperTable) table;
    rPlayer = player;
    ++evalNum;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("setTable rTable != null,rPlayer,player,evalNum=" + (rTable != null) + "," + rPlayer + "," + player + "," + MineSweeperTable.getPlayerItem(player) + "," + evalNum);}
		//#endif
    boolean lazyProcess = !fullProcess || isGameEnded();
		eval(lazyProcess, this, rTable, player, false);
  }

	public void procEndGame(byte player) {
		// UNDO MINE
	}

}
