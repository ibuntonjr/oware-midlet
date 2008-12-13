/** LGPL >= 2.1
 * Copyright (C) 2002-2004 Salamon Andras
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
// Expand to define logging define
@DLOGDEF@
package net.eiroca.j2me.game.tpg;

import java.util.Vector;
import net.eiroca.j2me.app.BaseApp;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Find the best move for the A.I.
	*/
abstract public class GameMinMax {

  public static final int MAX_POINT = 1000000;
  protected static boolean cancelled;
  protected static Vector precalculated_OpponentMoves = new Vector();
  protected static Vector precalculated_ResponseMoves = new Vector();

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("GameMinMax");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  //#endif

  public static void cancel(final boolean cancel) {
    GameMinMax.cancelled = cancel;
  }

  abstract public GameMove minimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill, final GameMove killerMove);

  /**
   * Determine the possible moves given data.  For each possible move, simulate
	 * the turn for the possible moves and see if the resulting board leaves a
	 * score that is best.  Save a best move for each possible move.
	 *
   * @param depth
   * @param state
   * @param player
   * @param tpg
   * @param alphabeta
   * @param alpha
   * @param order
   * @param kill
   * @author Irv Bunton
   */
  public void foreMinimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill) {
		try {
			GameMinMax.cancelled = false;
			GameMinMax.precalculated_OpponentMoves.removeAllElements();
			GameMinMax.precalculated_ResponseMoves.removeAllElements();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("foreMinimax starting depth,player possible from 1 - player=" + depth + "," + player + "," + (1 - player));}
			//#endif
			final GameMove pMoves[] = tpg.possibleMoves(state, (byte) (1 - player));
			if (pMoves == null) { return; }
			final GameTable newState = state.copyFrom();
			GameMove bestMove;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("foreMinimax 2 depth,player,pMoves.length=" + depth + "," + player + "," + player + "," + pMoves.length);}
			//#endif
			for (int i = 0; i < pMoves.length; ++i) {
				if (GameMinMax.cancelled) {
					break;
				}
				tpg.turn(state, (byte) (1 - player), pMoves[i], newState);
				// Get the best move.  Start out without a killer move.
				bestMove = minimax(depth, newState, player, tpg, alphabeta, alpha, order, kill, null);
				if (bestMove == null) { continue; }
				GameMinMax.precalculated_OpponentMoves.addElement(pMoves[i]);
				GameMinMax.precalculated_ResponseMoves.addElement(bestMove);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("foreMinimax error", e);
			//#endif
		}
  }

  public static void clearPrecalculatedMoves() {
    GameMinMax.precalculated_OpponentMoves.removeAllElements();
    GameMinMax.precalculated_ResponseMoves.removeAllElements();
  }

  public static GameMove precalculatedBestMove(final GameMove move) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("GameMinMax");
		//#endif
		try {
			for (int i = 0; i < GameMinMax.precalculated_OpponentMoves.size(); i++) {
				if (move.equals(GameMinMax.precalculated_OpponentMoves.elementAt(i))) { return (GameMove) GameMinMax.precalculated_ResponseMoves.elementAt(i); }
			}
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("precalculatedBestMove error", e);
			//#endif
			return null;
		}
  }

}
