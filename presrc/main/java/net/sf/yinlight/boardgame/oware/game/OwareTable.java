/** GPL >= 2.0
	* FIX empty move
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was ReversiTable.java
 * in mobilesuite.sourceforge.net project.
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
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package net.sf.yinlight.boardgame.oware.game;

import java.util.Stack;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware board table.
	*/
public final class OwareTable implements GameTable {

  /**
   * Two bits for every place: 00: nothing 01: 1 10: 2 11: oops
   */
	// Many of this is partially implemented
  public final static int INIT_SEEDS = 4;
  public final static int NBR_ROW = 2;
  public final static int NBR_COL = 6;
  public final static int NBR_PLAYERS = 2;
  public final static int NBR_ATTRIBUTES = 2;
  public final static int NBR_MAX_STACK = 3;
  private final static int TABLE_STORE_SIZE = (NBR_ATTRIBUTES * OwareTable.NBR_COL * OwareTable.NBR_ROW) + (NBR_PLAYERS * 1) + (NBR_PLAYERS * 3) + 1;
  public final static int WINNING_SCORE = 25;
  protected byte[][] board;
  protected byte[] point;
  protected OwareMove[] lastMove;
  protected int passNum;
	// FIX save stack
  protected Stack prevTbls = new Stack();
  protected Stack redoTbls = new Stack();

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("OwareTable");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public static byte getPlayerItem(final byte player) {
    return (byte) (player + 1);
  }

  public OwareTable() {
    board = new byte[NBR_ATTRIBUTES][NBR_COL * NBR_ROW];
    point = new byte[NBR_PLAYERS];
    lastMove = new OwareMove[NBR_PLAYERS];
    passNum = 0;
    passNum = 0;
		for (int i = 0; i < NBR_PLAYERS; i++) {
			setPoint((byte) i, (byte) 0);
			lastMove[i] = new OwareMove(-1, -1);
			lastMove[i].setPoint(0);
		}
    for (int i = 0; i < NBR_ROW; ++i) {
			for (int j = 0; j < NBR_COL;j++) {
				setItem(i, j, (byte) ((i == 0) ? 1 : 2));
				setSeeds(i, j, (byte) INIT_SEEDS);
			}
		}
	}

  public OwareTable(final byte[] byteArray, final int offset) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor byteArray.length=" + byteArray.length);}
		//#endif
		board = new byte[NBR_ATTRIBUTES][NBR_COL * NBR_ROW];
    point = new byte[NBR_PLAYERS];
		lastMove = new OwareMove[NBR_PLAYERS];
		try {
			int coffset = offset;
			passNum = byteArray[coffset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("passNum,coffset=" + passNum + "," + coffset);}
			//#endif
			for (int i = 0; i < NBR_PLAYERS; i++) {
				point[i] = byteArray[coffset++];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("point[i],coffset=" + point[i] + "," + coffset);}
				//#endif
			}
			for (int i = 0; i < NBR_PLAYERS; i++) {
				lastMove[i] = new OwareMove(byteArray[coffset++],
						byteArray[coffset++]);
				lastMove[i].setPoint(byteArray[coffset++]);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("lastMove[i].getPoint(),coffset=" + lastMove[i].getPoint() + "," + coffset);}
				//#endif
			}
			for (int i = 0; i < NBR_ATTRIBUTES; i++, coffset+=(NBR_COL * NBR_ROW)) {
				System.arraycopy(byteArray, coffset, board[i], 0, NBR_COL * NBR_ROW);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("coffset=" + coffset);}
				//#endif
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("OwareTable error", e);
			//#endif
		}
  }

  public OwareTable(final OwareTable table) {
    board = new byte[NBR_ATTRIBUTES][NBR_COL * NBR_ROW];
    point = new byte[NBR_PLAYERS];
    lastMove = new OwareMove[NBR_PLAYERS];
		BaseApp.copyInto(table.prevTbls, prevTbls);
		BaseApp.copyInto(table.redoTbls, redoTbls);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor board[0].length,board[1].length,board[1].length=" + board[0].length + "," + board[1].length);}
		//#endif
		try {
			for (int i = 0; i < NBR_ATTRIBUTES; i++) {
				System.arraycopy(table.board[i], 0, board[i], 0, board[i].length);
			}
			passNum = table.passNum;
			for (int i = 0; i < NBR_PLAYERS; i++) {
				point[i] = table.point[i];
			}
			for (int i = 0; i < NBR_PLAYERS; i++) {
				lastMove[i] = new OwareMove(table.lastMove[i]);
				lastMove[i].setPoint(table.lastMove[i].getPoint());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("OwareTable constructor error", e);
			//#endif
		}
  }

  public void convertToIntArray(final int[][] array) {
    for (int i = 0; i < NBR_ROW; ++i) {
      for (int j = 0; j < NBR_COL; ++j) {
        array[i][j] = getItem(i, j);
      }
    }
  }

  private void copyDataFromMain(final GameTable table) {
		try {
			final OwareTable rtable = (OwareTable) table;
			for (int i = 0; i < NBR_ATTRIBUTES; i++) {
				System.arraycopy(rtable.board[i], 0, board[i], 0, NBR_COL * NBR_ROW);
			}
			passNum = rtable.passNum;
			for (int i = 0; i < NBR_PLAYERS; i++) {
				point[i] = rtable.getPoint((byte)i);
			}
			for (int i = 0; i < NBR_PLAYERS; i++) {
				lastMove[i] = new OwareMove(rtable.lastMove[i]);
				lastMove[i].setPoint(rtable.lastMove[i].getPoint());
				//#ifdef DLOGGING
				if (traceLoggable) {logger.trace("copyDataFromMain i,lastMove[i].row,lastMove[i].col,lastMove[i].getPoint()=" + i + "," + lastMove[i].row + "," + lastMove[i].col + "," + lastMove[i].getPoint());}
				//#endif
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("copyDataFrom error", e);
			//#endif
		}
  }

  public void copyDataFrom(final GameTable table) {
		try {
			copyDataFromMain(table);
			BaseApp.copyInto(((OwareTable)table).prevTbls, prevTbls);
			BaseApp.copyInto(((OwareTable)table).redoTbls, redoTbls);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("copyDataFrom error", e);
			//#endif
		}
  }

  public GameTable copyFrom() {
    final OwareTable rtable = new OwareTable(this);
    return rtable;
  }

  public GameMove getEmptyMove() {
    return new OwareMove(0, 0);
  }

	public void saveLastTable(OwareTable ot) {
		synchronized(this) {
			if (prevTbls.size() >= NBR_MAX_STACK) {
				prevTbls.removeElementAt(NBR_MAX_STACK - 1);
			}
			if (redoTbls.size() > 0) {
				redoTbls.removeAllElements();
			}
			prevTbls.push(new OwareTable(ot));
		}
	}

	public OwareTable popLastTable() {
		synchronized(this) {
			if (prevTbls.size() > 0) {
				return (OwareTable)prevTbls.pop();
			} else {
				return null;
			}
		}
	}

	public OwareTable undoTable() {
		return unwindTable(prevTbls, redoTbls);
	}

	public OwareTable redoTable() {
		return unwindTable(redoTbls, prevTbls);
	}

	private OwareTable unwindTable(Stack removeTbls, Stack addTbls) {
		synchronized(this) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unwindTable removeTbls.size(),addTbls.size()=" + removeTbls.size() + "," + addTbls.size());}
			//#endif
			if (removeTbls.size() == 0) {
				return null;
			}
			
			if (addTbls.size() >= NBR_MAX_STACK) {
				addTbls.removeElementAt(NBR_MAX_STACK - 1);
			}
			OwareTable lastTbl = (OwareTable)removeTbls.pop();
			addTbls.push(lastTbl);
			copyDataFromMain(lastTbl);
			return lastTbl;
		}
	}

	public boolean checkLastTable() {
		synchronized(this) {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("checkLastTable prevTbls.size(),redoTbls.size()=" + "," + prevTbls.size() + "," + redoTbls.size());}
			//#endif
			return ((prevTbls.size() > 0) && (redoTbls.size() < NBR_MAX_STACK));
		}
	}

	public boolean checkLastRedoTable() {
		synchronized(this) {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("checkLastRedoTable redoTbls.size(),prevTbls.size()=" + "," + redoTbls.size() + "," + prevTbls.size());}
			//#endif
			return ((redoTbls.size() > 0) && (prevTbls.size() < NBR_MAX_STACK));
		}
	}

  /**
   * Get the player for the coordinates
   * @param row
   * @param col
   * @param value
   * @author Irv Bunton
   */
  public byte getItem(final int row, final int col) {
		try {
			return board[0][row * NBR_COL + col];
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getItem error row,col,board[0].length=" + row + "," + col + "," + board[0].length, e);
			//#endif
			return 0;
		}
  }

  /**
   * Get the number of seeds for the coordinates
   * @param row
   * @param col
   * @param value
   * @author Irv Bunton
   */
  public byte getSeeds(final int row, final int col) {
		try {
			return board[1][row * NBR_COL + col];
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getSeeds error row,col=" + row + "," + col, e);
			//#endif
			return 0;
		}
  }

  /**
   * Get the number of points for the player
   * @param player
   * @author Irv Bunton
   */
  public byte getPoint(final byte player) {
		try {
			return point[player];
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getPoint error player=" + player, e);
			//#endif
			return 0;
		}
  }

  /**
   * Get the value of passNum.
   * @return Value of passNum.
   */
  public int getPassNum() {
    return passNum;
  }

  /**
   * Set the player for the coordinates
   * @param row
   * @param col
   * @param value
   * @author Irv Bunton
   */
  public void setItem(final int row, final int col, final byte value) {
		try {
			board[0][row * NBR_COL + col] = value;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("setItem error row,col=" + row + "," + col, e);
			//#endif
		}
  }

  /**
   * Set the number of seeds for the coordinates
   * @param row
   * @param col
   * @param value
   * @author Irv Bunton
   */
  public void setSeeds(final int row, final int col, final byte value) {
		try {
			board[1][row * NBR_COL + col] = value;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("setSeeds error row,col,value=" + row + "," + col + "," + value, e);
			//#endif
		}
  }

  /**
   * Set the number of point for the player
   * @param player
   * @author Irv Bunton
   */
  public void setPoint(final byte player, byte score) {
		try {
			point[player] = score;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("setPoint error player,score=" + player + "," + score, e);
			//#endif
		}
  }

  /**
   * Set the value of passNum.
   * @param v Value to assign to passNum.
   */
  public void setPassNum(final int v) {
    passNum = v;
  }

  public int tableMaxStoreSize() {
		return TABLE_STORE_SIZE;
	}

  public int tableStoreSize() {
		return TABLE_STORE_SIZE;
	}

  public byte[] toByteArray() {
    final byte[] byteArray = new byte[TABLE_STORE_SIZE];
    toByteArray(byteArray, 0);
    return byteArray;
  }

  public int toByteArray(final byte[] byteArray, final int offset) {
		int coffset = offset;
		try {
			byteArray[coffset++] = (byte) passNum;
			for (int i = 0; i < NBR_PLAYERS; i++) {
				byteArray[coffset++] = point[i];
			}
			for (int i = 0; i < NBR_PLAYERS; i++) {
				byteArray[coffset++] = (byte)lastMove[i].row;
				byteArray[coffset++] = (byte)lastMove[i].col;
				byteArray[coffset++] = (byte)lastMove[i].getPoint();
			}
			for (int i = 0; i < NBR_ATTRIBUTES; i++, coffset+=board[0].length) {
				System.arraycopy(board[i], 0, byteArray, coffset, board[i].length);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("toByteArray offset,coffset=" + offset + "," + coffset);}
			//#endif
			return (coffset - offset);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("toByteArray error TABLE_STORE_SIZE,byteArray.length,offset,coffset=" + TABLE_STORE_SIZE + "," + byteArray.length + "," + offset + "," + coffset, e);
			//#endif
			return 0;
		}
  }

  /**
   * Should use StringBuffer instead of String, but this method is only for debug purposes.
   */
  public String toString() {
    final StringBuffer ret = new StringBuffer(80);
    for (int i = 0; i < NBR_ROW; ++i) {
      for (int j = 0; j < NBR_COL; ++j) {
        ret.append(getItem(i, j));
      }
      ret.append('\n');
    }
    ret.append("pass: ").append(getPassNum()).append('\n');
    return ret.toString();
  }

	public void setLastMove(int player, int row, int col, byte lastPoint) {
			this.lastMove[player] = new OwareMove(row, col);
			this.lastMove[player].setPoint(lastPoint);
	}

	public OwareMove getLastMove(int player) {
			return (lastMove[player]);
	}

}
