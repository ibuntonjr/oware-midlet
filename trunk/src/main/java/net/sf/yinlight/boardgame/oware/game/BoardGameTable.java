/** GPL >= 2.0
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
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
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
package net.sf.yinlight.boardgame.oware.game;

import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware board table.
	*/
abstract public class BoardGameTable implements GameTable {

  /**
   * Two bits for every place: 00: nothing 01: 1 10: 2 11: oops
   */
	// Many of this is partially implemented
  public final static int NBR_PLAYERS = 2;
  public final static int BOARD_TABLE_STORE_SIZE = 4;
  public int nbrRow = 0;
  public int nbrCol = 0;
  public int nbrPlayers = 2;
  protected int passNum;
  protected BoardGameMove[] lastMove;

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("BoardGameTable");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public static byte getPlayerItem(final byte player) {
    return (byte) (player + 1);
  }

  public BoardGameTable(final int nbrRow, final int nbrCol, final int nbrPlayers) {
    passNum = 0;
		this.nbrRow = nbrRow;
		this.nbrCol = nbrCol;
		this.nbrPlayers = nbrPlayers;
    lastMove = new BoardGameMove[nbrPlayers];
    for (int i = 0; i < nbrPlayers; ++i) {
			lastMove[i] = null;
		}
	}

  public BoardGameTable(final byte[] byteArray, final int offset) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor byteArray.length,offset=" + byteArray.length + "," + offset + "," + nbrRow + "," + nbrCol);}
		//#endif
		try {
			int coffset = offset;
			passNum = byteArray[coffset++];
			nbrRow = byteArray[coffset++];
			nbrCol = byteArray[coffset++];
			nbrPlayers = byteArray[coffset++];
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("constructor passNum,nbrRow,nbrCol,nbrPlayers,offset=" + passNum + "," + nbrRow + "," + nbrCol + "," + nbrPlayers + "," + offset);}
			//#endif
			lastMove = new BoardGameMove[nbrPlayers];
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("passNum,coffset,nbrRow,nbrCol=" + passNum + "," + coffset + "," + nbrRow + "," + nbrCol);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("OwareTable error", e);
			//#endif
		}
  }

  public BoardGameTable(final BoardGameTable table) {
		passNum = table.passNum;
		nbrRow = table.nbrRow;
		nbrCol = table.nbrCol;
		nbrPlayers = table.nbrPlayers;
    lastMove = new BoardGameMove[nbrPlayers];
		for (int i = 0; i < nbrPlayers; i++) {
			if (table.lastMove[i] != null) {
				lastMove[i] = table.lastMove[i].getBoardGameMove(table.lastMove[i]);
			} else {
				lastMove[i] = null;
			}
		}
	}

  public void copyDataFrom(final GameTable table) {
		final BoardGameTable rtable = (BoardGameTable) table;
			passNum = rtable.passNum;
			nbrRow = rtable.nbrRow;
			nbrCol = rtable.nbrCol;
			nbrPlayers = rtable.nbrPlayers;
			for (int i = 0; i < nbrPlayers; i++) {
				if (rtable.lastMove[i] != null) {
					lastMove[i] = rtable.lastMove[i].getBoardGameMove(rtable.lastMove[i]);
					//#ifdef DLOGGING
//@					if (traceLoggable) {logger.trace("copyDataFrom i,lastMove[i].row,lastMove[i].col,lastMove[i].getPoint()=" + i + "," + lastMove[i].row + "," + lastMove[i].col + "," + lastMove[i].getPoint());}
					//#endif
				} else {
					lastMove[i] = null;
				}
			}
	}

  abstract public GameMove getEmptyMove();

	abstract public BoardGameMove getBoardGameMove(int row, int col);

  abstract public BoardGameTable getEmptyTable();

  abstract public BoardGameTable getBoardGameTable(final byte[] byteArray, final int offset);

  abstract public BoardGameTable getBoardGameTable(final BoardGameTable table);

  abstract public void convertToIntArray(final int[][] array);

  /**
   * Get the player for the coordinates
   * @param row
   * @param col
   * @param value
   * @author Irv Bunton
   */
  abstract public byte getItem(final int row, final int col);

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
  abstract public void setItem(final int row, final int col, final byte value);

  /**
   * Set the value of passNum.
   * @param v Value to assign to passNum.
   */
  public void setPassNum(final int v) {
    passNum = v;
  }

  abstract public int tableStoreSize();

  public static int tableStoreSize(int nbrPlayers) {
		return BoardGameTable.BOARD_TABLE_STORE_SIZE + (3 * nbrPlayers);
	}

  public void toByteArray(final byte[] byteArray, final int offset) {
		int coffset = offset;
		try {
			byteArray[coffset++] = (byte) passNum;
			byteArray[coffset++] = (byte) nbrRow;
			byteArray[coffset++] = (byte) nbrCol;
			byteArray[coffset++] = (byte) nbrPlayers;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("toByteArray error byteArray.length,offset,coffset=" + byteArray.length + "," + offset + "," + coffset, e);
			//#endif
		}
	}

	public void setLastMove(int player, BoardGameMove move) {
		this.lastMove[player] = (move != null) ? move.getBoardGameMove(move) : null;
		//#ifdef DLOGGING
//@		if (traceLoggable) {logger.trace("setLastMove i,move.row,move.col=" + move.row + "," + move.col);}
		//#endif
	}

	public BoardGameMove getLastMove(int player) {
			return (lastMove[player]);
	}

  /**
   * Convert string to seeds
   */
  abstract public void fromRowString(int i, String nums)
	throws IllegalArgumentException;

  /**
   * Convert items to string
   */
  abstract public String toRowItemString(int i);

  /**
   * Convert items to string
   */
  abstract public String toItemString();

  /**
   * Convert seeds to string
   */
  abstract public String toRowString(int i);

}
