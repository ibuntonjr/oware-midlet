/** GPL >= 2.0
	* FIX use get factory for table and move.
	* Put in more factory calls
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was ReversiGame.java
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
 * This was modified no later than 2009-01-29.  Based on TwoPlayerGame
 */
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import java.util.Stack;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.BoardGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Two player game board game.
	*/
abstract public class BoardGame extends TwoPlayerGame {

  public final static int NBR_MAX_STACK = 6;
  protected int evalNum = 0;
  protected byte rPlayer;
  protected BoardGameTable rTable;
  protected int redoTop = 0;
  protected Stack prevTbls = new Stack();
  protected int point;


	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("BoardGame");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finerLoggable = logger.isLoggable(Level.FINER);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

  abstract public int getGameResult();

	public BoardGame() {
	}

	public BoardGame(final BoardGame bg) {
		this.evalNum = bg.evalNum;
		this.rPlayer = bg.rPlayer;
		this.rTable = bg.rTable;
		this.redoTop = bg.redoTop;
		this.prevTbls = new Stack();
		final int len = bg.prevTbls.size();
		for (int i = 0; i < len; i++) {
			this.prevTbls.addElement(bg.prevTbls.elementAt(i));
		}
		this.point = bg.point;
	}

  public int getPoint() {
    return point;
  }

  public boolean hasPossibleMove(final GameTable table, final byte player) {
    if (!(table instanceof BoardGameTable)) { return false; }
		try {
			final BoardGameMove[] moves = (BoardGameMove[]) possibleMoves(table, player);
			return (moves != null) && ((moves.length > 1) || (moves[0].row != ((BoardGameTable)table).nbrRow));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("hasPossibleMove error", e);
			//#endif
			return false;
		}
  }

  abstract public boolean isGameEnded(BoardGame bg, BoardGameTable t,
			byte player);

  abstract public boolean isGameEnded();

  /**
   * Calculates the point (goodness) of the table.
   * @param t Table (position) to be checked.
   * @param player Player.
   * @return Goodness of the position.
   */
  public int point(final BoardGame bg, final GameTable t, final byte player) {
		eval(true, bg, t, player, false);
    return getPoint();
  }

  abstract public void eval(boolean lazyProcess, BoardGame bg, GameTable t,
			final byte player, boolean endGame);

  /**
   * Get possible moves by simulating the move for the given row/col and if
	 * it has a result (is allowed), return the moves.  
	 * Except 2 passes is end of game
   *
   * @param table
   * @param player
   * @return    GameMove[]
   * @author Irv Bunton
   */
  abstract public GameMove[] possibleMoves(final GameTable table, final byte player);

  public void resetEvalNum() {
    evalNum = 0;
  }

  public int getEvalNum() {
    return evalNum;
  }

	abstract public void procEndGame();

  protected GameTable getTable() {
		return rTable;
	}

  protected byte getPlayer() {
		return rPlayer;
	}

	//#ifdef DTEST
  public void setPlayer(byte player) {
		rPlayer = player;
	}
	//#endif

	public void saveLastTable(BoardGameTable bgt, byte player, int turnNum) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("saveLastTable bgt,player=" + bgt + "," + player);}
		//#endif
		if (prevTbls.size() >= NBR_MAX_STACK) {
			prevTbls.removeElementAt(0);
		}
		int psize = prevTbls.size();
		while ((redoTop < psize) && (psize > 0)) {
			prevTbls.removeElementAt(psize-- - 1);
		}
		prevTbls.push(new RedoInfo(bgt.getBoardGameTable(bgt), player, turnNum));
		redoTop = prevTbls.size();
	}

	private BoardGameTable undoTable(BoardGameTable bgt, byte player, int ix,
			boolean removeEntry) {
		synchronized(this) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("undoTable parms player,ix,redoTop,prevTbls.size()=" + player + "," + ix + "," + redoTop + "," + prevTbls.size());}
			//#endif
			int undoTop = redoTop - 2 - ix;
			if (undoTop < 0) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.severe("undoTable newRedo < 2 - " + ix);}
				//#endif
				return null;
			}
			if (player != ((RedoInfo)prevTbls.elementAt(redoTop - 1 - ix)).player) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.severe("undoTable Error != player=" + player + "," + player);}
				//#endif
				return null;
			}
			RedoInfo ri =  (RedoInfo)prevTbls.elementAt(undoTop);
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("undoTable ri ri.player,ri.turnNum=" + ri.player + "," + ri.turnNum);}
			//#endif
			// If remove entry, do not update.
			if (!removeEntry) {
				return bgt.getBoardGameTable(ri.tbl);
			}
			redoTop--;
			setTable(ri.tbl.copyFrom(), player, false);
			BoardGameScreen.turnNum = ri.turnNum;
			return bgt;
		}
	}

	public BoardGameTable undoTable(byte player) {
		return undoTable(rTable, player, 0, true);
	}

	private BoardGameTable redoTable(BoardGameTable bgt, byte player, int ix,
			boolean removeEntry) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("redoTable parms player,ix,redoTop,prevTbls.size()=" + player + "," + ix + "," + redoTop + "," + prevTbls.size());}
		//#endif
		int newRedo = redoTop + 1 - ix;
		if (newRedo > prevTbls.size()) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("redoTable newRedo=" + newRedo);}
			//#endif
			return null;
		}
		if (newRedo < 0) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("redoTable newRedo < 0 =" + newRedo);}
			//#endif
			return null;
		}
		RedoInfo ri = (RedoInfo)prevTbls.elementAt(redoTop - ix);
		if (ri.player != player) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.severe("redoTable Error != player,ri.player=" + player + "," + ri.player);}
			//#endif
			return null;
		}
		if (!removeEntry) {
			return bgt.getBoardGameTable(ri.tbl);
		}
		redoTop++;
		bgt.copyDataFrom(ri.tbl);
		setTable(bgt, player, false);
		BoardGameScreen.turnNum = ri.turnNum;
		return (BoardGameTable)bgt;
	}

	public BoardGameTable redoTable(byte player) {
		return redoTable(rTable, player, 0, true);
	}

	public boolean checkLast(byte player, byte ix) {
		return undoTable(rTable, player, ix, false) != null;
	}

	public boolean checkLastRedo(byte player, byte ix) {
		return redoTable(rTable, player, ix, false) != null;
	}

	private class RedoInfo {
		BoardGameTable tbl;
		byte player;
		int turnNum;

		RedoInfo(BoardGameTable tbl, byte player, int turnNum) {
			this.tbl = tbl;
			this.player = player;
			this.turnNum = turnNum;
		}
	}

}
