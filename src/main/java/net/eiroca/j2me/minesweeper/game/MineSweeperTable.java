/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * Copyright (C) 2002-2004 Salamon Andras
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
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
package net.eiroca.j2me.minesweeper.game;

import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;
import net.eiroca.j2me.minesweeper.game.MineInfo;
import net.eiroca.j2me.minesweeper.game.MineSweeperGame;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

public final class MineSweeperTable extends BoardGameTable {

  /**
   * Two bits for every place: 00: nothing 01: 1 10: 2 11: oops
   */
  public static final int NBR_ROW = 9;
  public static final int NBR_COL = 9;
  protected static final int TABLE_SIZE = NBR_ROW * NBR_COL;
  protected static final int TABLE_STORE_SIZE = TABLE_SIZE + 1;
  public int bomb = 10;;
  public MineInfo[][] field;

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("MineSweeperTable");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public static byte getPlayerItem(final byte player) {
    return (byte) (player + 1);
  }

  public MineSweeperTable(int size_width, int size_height, int nbrPlayers,
			int bomb) {
		super(size_height, size_width, nbrPlayers);
		this.bomb = bomb;
    field = new MineInfo[size_width][size_height];
    for (int y = 0; y < size_height; y++) {
      for (int x = 0; x < size_width; x++) {
        field[x][y] = new MineInfo(x, y, MineSweeperGame.MINE_UNCHECKED, MineSweeperGame.MINE_UNCHECKED, (byte)1);
      }
    }
    // random lokasi bomb
    int z = 0;
    int px;
    int py;
    while (z != bomb) {
      px = BaseApp.rand(size_width);
      py = BaseApp.rand(size_height);
      if (field[px][py].status_real != MineSweeperGame.MINE_BOMB) {
        field[px][py].status_real = MineSweeperGame.MINE_BOMB;
        z++;
      }
    }

    // kasih nilai semua kotak kecuali bomb
    for (int y = 0; y < size_height; y++) {
      for (int x = 0; x < size_width; x++) {
        if (field[x][y].status_real != MineSweeperGame.MINE_BOMB) {
          field[x][y].status_real = bomb(x, y);
        }
      }
    }
  }

  /**
   * Fungsi untuk mencari jumlah bomb disekitar kotak
   * @param x koordinat x
   * @param y koordinat y
   * @return jumlah kotak yang ditandai sebaga bomb yang ada disekitar kotak dan belum terbuka
   */
  private int bomb(final int x, final int y) {
    int result = 0;
    for (int dx = -1; dx < 2; dx++) {
      int nx = x + dx;
      if ((nx >= 0) && (nx < nbrCol)) {
        for (int dy = -1; dy < 2; dy++) {
          if ((dx == 0) & (dy == 0)) {
            continue;
          }
          int ny = y + dy;
          if ((ny >= 0) && (ny < nbrRow)) {
            if (field[nx][ny].status_real == MineSweeperGame.MINE_BOMB) {
              result++;
            }
          }
        }
      }
    }
    return result;
  }

  public MineSweeperTable(final byte[] byteArray, final int offset) {
		super(byteArray, offset);
		try {
			int coffset = offset + BoardGameTable.BOARD_TABLE_STORE_SIZE;

			field = new MineInfo[nbrRow][nbrCol];
			for (int y = 0; y < nbrRow; y++) {
				for (int x = 0; x < nbrCol; x++) {
					field[x][y] = new MineInfo(byteArray, coffset+=MineInfo.MINE_INFO_SIZE);
				}
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("coffset=" + coffset);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("OwareTable error", e);
			//#endif
		}
  }

  public MineSweeperTable(final MineSweeperTable table) {
		super(table.nbrRow, table.nbrCol, table.nbrPlayers);
    field = new MineInfo[table.nbrRow][table.nbrCol];
    for (int y = 0; y < table.nbrCol; y++) {
      for (int x = 0; x < table.nbrRow; x++) {
        field[x][y] = new MineInfo(field[x][y]);
      }
    }
  }

  public BoardGameTable getEmptyTable() {
		return new MineSweeperTable(nbrRow, nbrCol, nbrPlayers, bomb);
	}

	public BoardGameMove getBoardGameMove(int row, int col) {
		return null; // UNDO MINE
	}

  public BoardGameTable getBoardGameTable(final byte[] byteArray, final int offset) {
		return new MineSweeperTable(byteArray, offset);
	}

  public BoardGameTable getBoardGameTable(final BoardGameTable table) {
		return new MineSweeperTable((MineSweeperTable)table);
	}

  public int tableStoreSize() {
		return BoardGameTable.tableStoreSize(nbrPlayers) + (nbrRow * nbrCol);
	}

  public void convertToIntArray(final int[][] array) {
    for (int i = 0; i < NBR_ROW; ++i) {
      for (int j = 0; j < NBR_COL; ++j) {
        array[i][j] = getItem(i, j);
      }
    }
  }

  public void copyDataFrom(final GameTable table) {
    final MineSweeperTable rtable = (MineSweeperTable) table;
		super.copyDataFrom(rtable);
		// UNDO MINE
  }

  public GameTable copyFrom() {
    final MineSweeperTable rtable = new MineSweeperTable(this);
    return rtable;
		// UNDO MINE
  }

  public GameMove getEmptyMove() {
    return null; // UNDO MINE
  }

  public byte getItem(final int x, final int y) {
    return (byte)field[x][y].item;
  }

  public void setItem(final int x, final int y, final byte value) {
    field[x][y].item = value;
  }

  public byte[] toByteArray() {
    final byte[] byteArray = new byte[tableStoreSize()];
    toByteArray(byteArray, 0);
    return byteArray;
  }

  public void toByteArray(final byte[] byteArray, final int offset) {
		try {
			super.toByteArray(byteArray, offset);
			int coffset = offset + BoardGameTable.BOARD_TABLE_STORE_SIZE;
			for (int y = 0; y < nbrRow; y++) {
				for (int x = 0; x < nbrCol; x++) {
					field[x][y].toByteArray(
							byteArray, coffset+=MineInfo.MINE_INFO_SIZE);
				}
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("toByteArray offset,coffset=" + offset + "," + coffset);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("toByteArray error TABLE_STORE_SIZE,byteArray.length,offset=" + TABLE_STORE_SIZE + "," + byteArray.length + "," + offset, e);
			//#endif
		}
  }

  /**
   * Should use StringBuffer instead of String, but this method is only for debug purposes.
   */
  public String toString() {
    final StringBuffer ret = new StringBuffer(80);
    for (int i = 0; i < NBR_ROW; ++i) {
      for (int j = 0; j < NBR_COL; ++j) {
        ret.append(getItem(j, i));
      }
      ret.append('\n');
    }
    ret.append("pass: ").append(getPassNum()).append('\n');
    return ret.toString();
  }

  /**
   * Convert items to string
   */
  public String toRowItemString(int i) {
    final StringBuffer ret = new StringBuffer(nbrRow * nbrCol);
		for (int j = 0; j < nbrCol; ++j) {
			ret.append(getItem(i, j));
    }
    return ret.toString();
  }

  /**
   * Convert string to items
   */
  public void fromRowString(int i, String nums)
	throws IllegalArgumentException {
		final byte[] bnums = nums.getBytes();
		final int len = bnums.length;
		int j = 0;
		try {
			for (; (j < len) && (j < nbrCol); ++j) {
				byte item = (j < len) ? (byte)(bnums[j] - '0') : (byte)0;
				if ((item <= 0) || (item > 2)) {
					Exception e = new IllegalArgumentException("item # invalid " + j);
					e.printStackTrace();
					//#ifdef DLOGGING
//@					logger.severe("fromRowString error i,j,item=" + i + "," + j + "," + item, e);
					//#endif
					throw e;
				}
				setItem(i, j, item);
			}
			//#ifdef DLOGGING
//@			if (finestLoggable && (len != (nbrCol + 1))) {logger.finest("fromRowString length error i,bnums.length=" + i + "," + bnums.length);}
			//#endif
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("fromRowString error i,bnums.length=" + i + "," + bnums.length, e);
			//#endif
		}
  }

  /**
   * Convert items to string
   */
  public String toItemString() {
    final StringBuffer ret = new StringBuffer(nbrRow * nbrCol);
    for (int i = 0; i < nbrRow; ++i) {
			ret.append(toRowItemString(i));
      ret.append('\n');
    }
    ret.append("pass: ").append(getPassNum()).append('\n');
    return ret.toString();
  }

  /**
   * Convert seeds to string
   */
  public String toRowString(int i) {
		return toRowItemString(i);
  }

}
