/** LGPL >= 2.1
 * Copyright (C) 2002-2004 Salamon Andras
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This software was modified 2008-12-14.  The original file was
 * GameMinMax.java in mobilesuite.sourceforge.net project.
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
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import java.util.Vector;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Min max algorithm with basic computation.
	*/
public class LimitedMinMax extends GameMinMax {

	Thread cthread = Thread.currentThread();

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("LimitedMinMax");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finerLoggable = logger.isLoggable(Level.FINER);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

  public GameMove minimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill, final GameMove killerMove) {
		try {
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("minimax depth,state,player,move.col,BoardGameScreen.actPlayer=" + depth + "," + (state != null) + "," +  player);}
			//#endif
			if (cancelled) {
				cancelled = false;
				return null;
			}
			GameMove bestMove;
			if (depth == 0) {
				/* Should not happen. */
				return null;
			}
			GameMove actMove;
			GameMove kMove;
			boolean cut = false;
			int actPoint;
			int maxPoint = -GameMinMax.MAX_POINT; /* -Integer.MIN_VALUE ?? */
			int bestNum = 0;
			final GameMove pMoves[] = tpg.possibleMoves(state, player);
			final int pmoveLen = pMoves.length;
			if ((pMoves == null) || (pmoveLen == 0)) {
				// game ended or player cannot move.
				return null;
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("minimax depth,player,pMoves.length=" + depth + "," + player + "," + player + "," + pmoveLen);}
			//#endif
			final int[] bestMoves = new int[pmoveLen];
			final GameTable newState = state.copyFrom();
			if ((depth > 2) && order && (pmoveLen > 1)) {
				/* For each potential move, get the points for that move after
					 the turn is taken. */
				final int points[] = new int[pmoveLen];
				for (int oindex = 0; oindex < pmoveLen; ++oindex) {
					tpg.turn(state, player, pMoves[oindex], newState);
					points[oindex] = tpg.getTblPoint(newState, player);
				}
				int oindex3 = 0;
				for (int oindex1 = 0; oindex1 < pmoveLen - 1; ++oindex1) {
					// Get the index of the move with max points
					for (int oindex2 = oindex1; oindex2 < pmoveLen; ++oindex2) {
						if ((oindex2 == oindex1) || (points[oindex2] > points[oindex3])) {
							oindex3 = oindex2;
						}
					}
					if (oindex3 != oindex1) {
						final GameMove swapMove = pMoves[oindex3];
						pMoves[oindex3] = pMoves[oindex1];
						pMoves[oindex1] = swapMove;
					}
				}
			}
			if (kill && (killerMove != null) && (pmoveLen > 1)) {
				int kindex = 0;
				while ((kindex < pmoveLen) && !pMoves[kindex].equals(killerMove)) {
					++kindex;
				}
				if ((kindex < pmoveLen) && (kindex != 0)) {
					// we have a killermove, but it's not the first one
					final GameMove swapMove = pMoves[0];
					pMoves[0] = pMoves[kindex];
					pMoves[kindex] = swapMove;
				}
			}
			actMove = null;
			for (int i = 0; !cut && (i < pmoveLen); ++i) {
				tpg.turn(state, player, pMoves[i], newState);
				if (depth <= 1) {
					actPoint = tpg.getTblPoint(newState, player);
				}
				else {
					if (kill && (i != 0)) {
						kMove = actMove;
					}
					else {
						kMove = null;
					}
					cthread.yield();
					actMove = minimax(depth - 1, newState, (byte) (1 - player), tpg, alphabeta, -maxPoint, order, kill, kMove);
					if (actMove == null) { return null; }
					actPoint = -actMove.getPoint();
				}
				if ((i == 0) || (actPoint > maxPoint)) {
					// better move
					maxPoint = actPoint;
					if (alphabeta && (alpha < maxPoint)) {
						cut = true;
					}
					bestNum = 1;
					bestMoves[0] = i;
				}
				else if (actPoint == maxPoint) {
					// same as the better
					bestMoves[bestNum++] = i;
				}
			}
			int bestIndex;
			if (bestNum > 1) {
				bestIndex = bestMoves[BaseApp.rand(bestNum)];
			}
			else {
				bestIndex = bestMoves[0];
			}
			bestMove = pMoves[bestIndex];
			bestMove.setPoint(maxPoint);
			return bestMove;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("minimax error", e);
			//#endif
			return null;
		}
  }

}
