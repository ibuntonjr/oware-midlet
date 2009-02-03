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
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package net.eiroca.j2me.reversi;

import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

public final class ReversiTable extends BoardGameTable {

  /**
   * Two bits for every place: 00: nothing 01: 1 10: 2 11: oops
   */
  protected static final int NBR_ROW = 8;
  protected static final int NBR_COL = 8;
  protected static final int TABLE_SIZE = NBR_ROW * NBR_COL;
  protected static final int TABLE_STORE_SIZE = TABLE_SIZE + 1;
  protected byte[] board;

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("ReversiTable");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public static byte getPlayerItem(final byte player) {
    return (byte) (player + 1);
  }

  public ReversiTable() {
		super(NBR_ROW, NBR_COL, 2);
    board = new byte[TABLE_SIZE];
    setItem(3, 3, (byte) 2);
    setItem(4, 4, (byte) 2);
    setItem(3, 4, (byte) 1);
    setItem(4, 3, (byte) 1);
  }

  public ReversiTable(final byte[] byteArray, final int offset) {
		super(byteArray, offset);
		try {
			int coffset = offset + BoardGameTable.BOARD_TABLE_STORE_SIZE;
			for (int i = 0; i < nbrPlayers; i++) {
				if (lastMove[i] != null) {
					byteArray[coffset++] = (byte)lastMove[i].row;
					byteArray[coffset++] = (byte)lastMove[i].col;
					byteArray[coffset++] = (byte)lastMove[i].getPoint();
				} else {
					byteArray[coffset++] = (byte)255;
					byteArray[coffset++] = (byte)255;
					byteArray[coffset++] = (byte)255;
				}
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("coffset=" + coffset);}
			//#endif
			board = new byte[TABLE_SIZE];
			System.arraycopy(byteArray, coffset, board, 0, TABLE_SIZE);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("OwareTable error", e);
			//#endif
		}
  }

  public ReversiTable(final ReversiTable table) {
		super(table);
    board = new byte[TABLE_SIZE];
    System.arraycopy(table.board, 0, board, 0, TABLE_SIZE);
  }

  public BoardGameTable getEmptyTable() {
		return new ReversiTable();
	}

	public BoardGameMove getBoardGameMove(int row, int col) {
		return new ReversiMove(row, col);
	}

  public BoardGameTable getBoardGameTable(final byte[] byteArray, final int offset) {
		return new ReversiTable(byteArray, offset);
	}

  public BoardGameTable getBoardGameTable(final BoardGameTable table) {
		return new ReversiTable((ReversiTable)table);
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
    final ReversiTable rtable = (ReversiTable) table;
		super.copyDataFrom(rtable);
    System.arraycopy(rtable.board, 0, board, 0, TABLE_SIZE);
  }

  public GameTable copyFrom() {
    final ReversiTable rtable = new ReversiTable(this);
    return rtable;
  }

  public void flip(final int row, final int col) {
    setItem(row, col, (byte) (3 - getItem(row, col)));
  }

  public GameMove getEmptyMove() {
    return new ReversiMove(0, 0);
  }

  public byte getItem(final int row, final int col) {
    return board[row * NBR_COL + col];
  }

  public void setItem(final int row, final int col, final byte value) {
    board[row * NBR_COL + col] = value;
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
			for (int i = 0; i < nbrPlayers; i++) {
				if (lastMove[i] != null) {
					byteArray[coffset++] = (byte)lastMove[i].row;
					byteArray[coffset++] = (byte)lastMove[i].col;
					byteArray[coffset++] = (byte)lastMove[i].getPoint();
				} else {
					byteArray[coffset++] = (byte)255;
					byteArray[coffset++] = (byte)255;
					byteArray[coffset++] = (byte)255;
				}
			}
			System.arraycopy(board, 0, byteArray, coffset, board.length);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("toByteArray offset,coffset=" + offset + "," + coffset);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("toByteArray error TABLE_STORE_SIZE,byteArray.length,offset=" + TABLE_STORE_SIZE + "," + byteArray.length + "," + offset, e);
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
					logger.severe("fromRowString error i,j,item=" + i + "," + j + "," + item, e);
					//#endif
					throw e;
				}
				setItem(i, j, item);
			}
			//#ifdef DLOGGING
			if (finestLoggable && (len != (nbrCol + 1))) {logger.finest("fromRowString length error i,bnums.length=" + i + "," + bnums.length);}
			//#endif
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("fromRowString error i,bnums.length=" + i + "," + bnums.length, e);
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
