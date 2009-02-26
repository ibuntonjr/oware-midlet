/** GPL >= 2.0
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
 * This software was modified 2008-12-09.  The original file was alphabet.c
 * in http://oware.ivorycity.com/. Also, some code was taken from 
 * GameMinMax.java in mobilesuite.sourceforge.net project.
 */
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import java.util.Vector;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif
/**
	* Oware wizard min/max algorithm.
	*/
public final class OwareMinMax extends GameMinMax {

	final public static int MAXDEPTH     = 16;
	final public static int ENDGAMEDEPTH = 20;
	final public static int  MAX = 1;
	final public static int  MIN = 2;
	final public static int  HEURISTIC_DFLT = 0;
	/* Array of heuristics.  Each elmement is a different skill level. */
	static private OwareHeuristic[] heuristics = new OwareHeuristic[256];
	static private int gheuristic = 0;

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("OwareMinMax");
	private boolean finerLoggable = logger.isLoggable(Level.FINER);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

	public OwareMinMax(int heuristic) {
		OwareMinMax.heuristics[0] = new DefaultHeuristic();
		OwareMinMax.heuristics[1] = new DefenseHeuristic();
		OwareMinMax.gheuristic = heuristic;
	}

	void alphabetaSetHeuristic(int id, OwareHeuristic h) {
		OwareMinMax.heuristics[id] = h;
	}

	private OwareMove alphabetaPly( int depth, OwareTable table, byte player, OwareGame g,
		int heuristic,
		int  bestmax, int  bestmin) {

		OwareMove bestmove = null;

		try {

			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("alphabetaPly start depth,player,heuristic,bestmax,bestmin=" + depth + "," + player + "," + heuristic + "," + bestmax + "," + bestmin);}
			//#endif

			if (GameMinMax.cancelled) {
				return null;
			}

			final OwareMove pMoves[] = (OwareMove[])g.possibleMoves(table, player);
			if ((pMoves == null) || (pMoves.length == 0)) {
				return null;
			}

			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("alphabetaPly 2 depth,player,heuristic,bestmax,bestmin,pMoves.length=" + depth + "," + player + "," + heuristic + "," + bestmax + "," + bestmin + "," + pMoves.length);}
			//#endif
			int result = bestmin;
			for (int i = 0; (i < pMoves.length); ++i)
			{
				OwareTable testTable = new OwareTable(table);

				if (GameMinMax.cancelled) {
					return null;
				}

				if (!OwareGame.turn(table, player, (OwareMove)pMoves[i], testTable)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("alphabetaPly turn continue i,row,col=" + i + ",(" + pMoves[i].row + "," + pMoves[i].col + ")");}
					//#endif
					continue; /*An illegal move*/
				}
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("alphabetaPly turn process i,row,col=" + i + ",(" + pMoves[i].row + "," + pMoves[i].col + ")");}
				//#endif

				if (bestmove == null) {
					bestmove = new OwareMove(pMoves[i]);
					bestmove.setPoint(0);
				}

				if( (depth<=0) || g.isGameEnded(g, testTable, player)) {
					int tmpResult = OwareMinMax.heuristics[heuristic].getResult(player, testTable);
					if (tmpResult > (byte)result) {
						bestmove = new OwareMove(pMoves[i]);
						bestmove.setPoint(tmpResult);
						result = tmpResult;
					}
				} else {
					//#ifdef DLOGGING
					if (finerLoggable) {logger.finer("alphabetaPly recursive i,row,col,depth - 1,player,heuristic,bestmax,bestmin=" + i + ",(" + pMoves[i].row + "," + pMoves[i].col + ")," + (depth - 1) + "," + player + "," + heuristic + "," + bestmax + "," + bestmin);}
					//#endif

					OwareMove tmpmove = alphabetaPly( depth - 1, testTable,
							(byte) (1 - player), g, heuristic, bestmax, bestmin);
					if (tmpmove == null) {
						continue;
					}
					if (-tmpmove.getPoint() > result) {
						bestmove = new OwareMove(pMoves[i]);
						bestmove.setPoint(-tmpmove.getPoint());
						result = bestmove.getPoint();
					}
				}

				// FIX?
				if(player == 0)
				{
					if(result >= bestmin){
						bestmove.setCoordinates(((OwareMove)pMoves[i]).row, ((OwareMove)pMoves[i]).col);
						bestmove.setPoint(bestmin);
						return bestmove;
					}
					if(result>bestmax) {
						bestmove.setCoordinates(((OwareMove)pMoves[i]).row, ((OwareMove)pMoves[i]).col);
						bestmax = result;
						bestmove.setPoint(bestmax);
					}
				} else {
					if(result <= bestmax){
						bestmove.setCoordinates(((OwareMove)pMoves[i]).row, ((OwareMove)pMoves[i]).col);
						bestmove.setPoint(bestmax);
						return bestmove;
					}
					if(result < bestmin) {
						bestmove.setCoordinates(((OwareMove)pMoves[i]).row, ((OwareMove)pMoves[i]).col);
						bestmin = result;
					}
				}
			}

			if (bestmove == null) {
				return null;
			} else if (player == 0) {
					bestmove.setPoint(bestmax);
					return bestmove;
			} else {
					bestmove.setPoint(bestmin);
					return bestmove;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("alphabetaPly error", e);
			bestmove = null;
			//#endif
			return null;
			//#ifdef DLOGGING
		} finally {
			if (finerLoggable) {logger.finer("alphabetaPly return bestmove.row,bestmove.col,bestmove.getPoint(),depth,player,bestmax,bestmin=(" + ((bestmove == null) ? "bestmove is null" : (bestmove.row + "," + bestmove.col + ")," + bestmove.getPoint())) + "," + depth + "," + player + "," + bestmax + "," + bestmin);}
			//#endif
		}
	}

  public GameMove minimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill, final GameMove killerMove) {
		OwareMove bestmove = null;
		try {
			OwareTable testTable = new OwareTable((OwareTable)state);
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("OwareMinMax minimax start depth,player=" + depth + "," + player);}
			//#endif
			if (depth == 0) {
				return null;
			}
			bestmove = 
			alphabetaPly( depth - 1, testTable, player, (OwareGame)tpg, gheuristic, -GameMinMax.MAX_POINT, GameMinMax.MAX_POINT);

			if (GameMinMax.cancelled) {
				GameMinMax.cancelled = false;
				return null;
			}

			if (bestmove == null) {
				return null;
			} if (bestmove.getPoint() == -GameMinMax.MAX_POINT) {
				return null;
			} else {
				return bestmove;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("minimax error", e);
			bestmove = null;
			//#endif
			return null;
			//#ifdef DLOGGING
		} finally {
			if (finerLoggable) {logger.finer("minimax return bestmove.row,bestmove.col,bestmove.getPoint(),depth,player=(" + ((bestmove == null) ? "bestmove is null" : (bestmove.row + "," + bestmove.col + ")," + bestmove.getPoint())) + "," + depth + "," + player);}
			//#endif
		}
	}

	public class DefaultHeuristic extends OwareHeuristic {
		public int getResult(byte player, OwareTable table) {
			if (player == 0) {
				return table.getPoint((byte)0) - table.getPoint((byte)1);
			} else {
				return table.getPoint((byte)1) - table.getPoint((byte)0);
			}
		}
	}

	public class DefenseHeuristic extends OwareHeuristic {
		public int getResult(byte player, OwareTable table) {
			if (player == 0) {
				return table.getPoint((byte)0) - table.getPoint((byte)1);
			} else {
				return (3 * table.getPoint((byte)1)) - table.getPoint((byte)0);
			}
		}
	}

}
