/** GPL >= 2.0
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
	* Oware two player game.
	*/
public final class OwareGame extends TwoPlayerGame {

  protected int evalNum = 0;
  protected int libertyPenalty;
  protected int numFirstFreeNeighbours;
  protected int numSecondFreeNeighbours;
  public int numFirstPlayer;
  public int numSecondPlayer;
  protected int point;
  protected int pointFirstPlayer;
  protected int pointSecondPlayer;
  protected int s01;
  protected int s11;
  protected int sBonus;
  protected boolean squareErase;
  protected byte rPlayer;
  protected OwareTable rTable;
  protected int[][] tableIntArray = new int[OwareTable.NBR_ROW][OwareTable.NBR_COL];

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("OwareGame");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

  /**
   * Use the table to determine if a move is possible, then create the
	 * simulation of the move and put it into newTable (table is not modified)
	 * and return an array.  If animated, return 
   *
   * @param table
   * @param player
   * @param move
   * @param newTable
   * @param animated
   * @return    GameTable[]
   * @author Irv Bunton
   */
  private static GameTable[] _turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newTable, final boolean animated
			//#ifdef DLOGGING
			,Logger logger
			//#endif
			) {
		GameTable tables[] = null;
		try {
			final int row = move.row;
			final int col = move.col;
			//#ifdef DLOGGING
			logger.finest("_turn start row,col,player,OwareTable.getPlayerItem(player),table.getItem(row, col),table.getSeeds(row, col)=" + row + "," + col + "," + player + "," + OwareTable.getPlayerItem(player) + "," + table.getItem(row, col) + "," + table.getSeeds(row, col));
			//#endif
			/* If the cup is not for the current player, we are not allowed to
				 do anything with it.  Also, if there are no seeds, you cannot
				 take the turn. */
			if ((row != OwareTable.NBR_ROW) &&
					((table.getItem(row, col) != OwareTable.getPlayerItem(player)) ||
					(table.getSeeds(row, col) == 0))) { return null; }
			Vector vTables = null;
			if (animated) {
				vTables = new Vector();
			}
			newTable.copyDataFrom(table);
			if (row == OwareTable.NBR_ROW) {
				// pass.  Leave table as is
				newTable.setPassNum(newTable.getPassNum() + 1);
				tables = new GameTable[1];
				tables[0] = newTable;
					//#ifdef DLOGGING
					logger.finest("_turn newTable.getPassNum()=" + newTable.getPassNum());
					//#endif
				return tables;
			}
			newTable.setPassNum(0);
			boolean changed = false;
			int seeds = newTable.getSeeds(row, col);
			newTable.setSeeds(row, col, (byte)0);
			newTable.setLastMove(player, row, col, (byte)seeds);
			int ccol = col;
			int crow = row;
			boolean first = true;
			int lastRow = crow;
			int lastCol = ccol;
			while (seeds > 0) {
				if (first) {
					first = false;
				} else {
					crow = 1 - crow;
					ccol = (crow == 1) ? 0 : (OwareTable.NBR_COL - 1);
				}
				int dir = (crow == 0) ? -1 : 1;
				for (; (ccol >= 0) && (ccol < OwareTable.NBR_COL); ccol += dir) {
					if ((crow != row) || (ccol != col)) {
						int cseeds = newTable.getSeeds(crow, ccol);
						newTable.setSeeds(crow, ccol, (byte)(cseeds + 1));
						//#ifdef DLOGGING
						logger.finest("_turn crow,ccol,dir,(cseeds + 1),seeds=" + crow + "," + ccol + "," + dir + "," + (cseeds + 1) + "," + seeds);
						//#endif
						changed = true;
						lastRow = crow;
						lastCol = ccol;
						if (--seeds <= 0) {
							break;
						}
					//#ifdef DLOGGING
					} else {
						logger.finest("_turn skipping as it is the same row,col,crow,ccol,dir=" + row + "," + col + "," + crow + "," + ccol + "," + dir + "," + seeds);
						//#endif
					}
				}
			}

			//#ifdef DLOGGING
			logger.finest("_turn finished for crow,ccol,lastRow,lastCol,seeds=" + crow + "," + ccol + "," + lastRow + "," + lastCol + "," + seeds);
			//#endif
			/* Score points. */
			if (lastRow != row) {
				// Move in the opposite direction this time
				crow = lastRow;
				ccol = lastCol;
				int dir = (crow == 0) ? 1 : -1;
				for (; (ccol >= 0) && (ccol < OwareTable.NBR_COL); ccol += dir) {
					if ((newTable.getSeeds(crow, ccol) == 2) ||
						(newTable.getSeeds(crow, ccol) == 3)) {
						newTable.setPoint(player, (byte)(newTable.getPoint(player) +
								newTable.getSeeds(crow, ccol)));
						//#ifdef DLOGGING
						logger.finest("_turn capturing crow,ccol,dir,newTable.getSeeds(crow, ccol=" + crow + "," + ccol + "," + dir + "," + newTable.getSeeds(crow, ccol));
						//#endif
						newTable.setSeeds(crow, ccol, (byte)0);
					} else {
						break;
					}
				}
			}
			if (animated) {
				vTables.addElement(new OwareTable(newTable));
			}
			if (changed) {
				if (animated) {
					tables = new GameTable[vTables.size()];
					vTables.copyInto(tables);
				}
				else {
					tables = new GameTable[1];
					tables[0] = newTable;
				}
				return tables;
			}
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("_turn error", e);
			//#endif
			return null;
			//#ifdef DLOGGING
		} finally {
			logger.finest("_turn return player,tables.length=" + player + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));
			//#endif
		}
  }

  /*
   * Take the turn for the given move for the given player.
	 *
   * @Override
   * @see net.eiroca.j2me.minmax.TwoPlayerGame#animatedTurn(net.eiroca.j2me.minmax.Table,
   *      byte, net.eiroca.j2me.minmax.Move, net.eiroca.j2me.minmax.Table)
   */
  public GameTable[] animatedTurn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
		try {
			return _turn((OwareTable) table, player, (OwareMove) move, (OwareTable) newt, true
		//#ifdef DLOGGING
				, logger
		//#endif
				);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("animatedTurn error", e);
			//#endif
			return null;
		}
  }

  /**
   * Evaluate the board to determine points.
	 *
   * @param fullProcess
   * @author Irv Bunton
   */
  protected void eval(boolean fullProcess) {
		try {
			rTable.convertToIntArray(tableIntArray);
			boolean lazyProcess = !fullProcess || isGameEnded() || (numFirstPlayer + numSecondPlayer > 58);
			numFirstPlayer = 0;
			numSecondPlayer = 0;
			pointFirstPlayer = 0;
			pointSecondPlayer = 0;
			numFirstFreeNeighbours = 0;
			numSecondFreeNeighbours = 0;
			for (int i = 0; i < OwareTable.NBR_ROW; ++i) {
				for (int j = 0; j < OwareTable.NBR_COL; ++j) {
					final int item = tableIntArray[i][j];
						int cseeds = rTable.getSeeds(i, j);
					switch (item) {
						case 1:
							if (cseeds > 0) {
								numFirstPlayer += rTable.getSeeds(i, j);
							} else {
								numFirstFreeNeighbours += 1;
							}
							break;
						case 2:
							if (cseeds > 0) {
								numSecondPlayer += rTable.getSeeds(i, j);
							} else {
									numSecondFreeNeighbours += 1;
							}
							break;
					}
				}
			}
			pointFirstPlayer = rTable.getPoint((byte)0);
			pointSecondPlayer = rTable.getPoint((byte)1);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("eval pointFirstPlayer,pointSecondPlayer,numFirstPlayer,numSecondPlayer,rTable.getPassNum()=" + pointFirstPlayer + "," + pointSecondPlayer + "," + numFirstPlayer + "," + numSecondPlayer + "," + rTable.getPassNum());}
			//#endif
			point = pointFirstPlayer - pointSecondPlayer;
			if (lazyProcess) {
				if (isGameEnded()) {
					if (point > 0) {
						point += GameMinMax.MAX_POINT;
					}
					else if (point < 0) {
						point -= GameMinMax.MAX_POINT;
					}
				}
			}
			if (rPlayer == 1) {
				point = -point;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("eval error", e);
			//#endif
		}
  }

  public int getEvalNum() {
    return evalNum;
  }

  public int getGameResult() {
    int score = (pointFirstPlayer - pointSecondPlayer);
    if (rPlayer == 1) {
      score = -score;
    }
    if (score >= 0) {
      return TwoPlayerGame.WIN;
    }
    else if (score <= 0) {
      return TwoPlayerGame.LOSS;
    }
    else {
      return TwoPlayerGame.DRAW;
    }
  }

  public int getPoint() {
    return point;
  }

  public int getTblPoint(final GameTable table, final byte player) {
    if (!(table instanceof OwareTable)) { return 0; }
    return (0 - player) * (((OwareTable)rTable).getPoint((byte)0) - ((OwareTable)rTable).getPoint((byte)1));
  }

  public boolean hasPossibleMove(final GameTable table, final byte player) {
		try {
			final OwareMove[] moves = (OwareMove[]) possibleMoves(table, player);
			return (moves != null) && ((moves.length > 1) || (moves[0].row != OwareTable.NBR_ROW));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("hasPossibleMove error", e);
			//#endif
			return false;
		}
  }

  static public boolean isGameEnded(OwareTable cTable) {
    if ((cTable.getPoint((byte)0) >= OwareTable.WINNING_SCORE) || (cTable.getPoint((byte)1) >= OwareTable.WINNING_SCORE) || (cTable.getPassNum() == 2)) { return true; }
    return false;
  }

  public boolean isGameEnded() {
		return OwareGame.isGameEnded(rTable);
  }

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
  public GameMove[] possibleMoves(final GameTable table, final byte player) {
		try {
			if (!(table instanceof OwareTable)) { return null; }
			final Vector moves = new Vector();
			if (((OwareTable) table).getPassNum() == 2) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("possibleMoves pass num == 2");}
				//#endif
				// two passes: end of the game
				return null;
			}
			// This has no values, but they are put in when _turn does a copy
			// to this table.
			OwareTable newTable = new OwareTable();
			// Possible moves can only be in the current row.
			OwareMove move = null;
			boolean hasMove = false;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("possibleMoves starting player=" + player);}
			//#endif
			int col = 0;
			for (int row = player; col < OwareTable.NBR_COL; ++col) {
					if (move == null) {
						move = new OwareMove(row, col);
					} else {
						move.setCoordinates(row, col);
					}
					// FIX
					if (!hasMove && (((OwareTable) table).getSeeds(row, col) > 0)) {
						hasMove = true;
					}
					OwareTable oldTable = new OwareTable(newTable);
					final boolean goodMove = turn(table, player, move, newTable);
					if (goodMove) {
						moves.addElement(new OwareMove(move));
					}
					newTable = oldTable;
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("possibleMoves player,hasMove,moves.size()=" + player + "," + hasMove + "," + moves.size());}
			//#endif
			if (!hasMove) { return null; }
			if (moves.size() == 0) {
				// need to pass
				moves.addElement(new OwareMove(OwareTable.NBR_ROW, OwareTable.NBR_COL));
			}
			final GameMove[] retMoves = new OwareMove[moves.size()];
		  moves.copyInto(retMoves);
			return retMoves;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("possibleMoves error player=" + player, e);
			//#endif
			return null;
		}
  }

  public void resetEvalNum() {
    evalNum = 0;
  }

  protected void setTable(final GameTable table, final byte player, final boolean fullProcess) {
    if (!(table instanceof OwareTable)) { throw new IllegalArgumentException(); }
    rTable = (OwareTable) table;
    rPlayer = player;
    ++evalNum;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("setTable rTable != null,rPlayer,player,evalNum=" + (rTable != null) + "," + rPlayer + "," + player + "," + OwareTable.getPlayerItem(player) + "," + evalNum);}
		//#endif
    eval(fullProcess);
  }

  protected GameTable getTable() {
		return rTable;
	}

  protected byte getPlayer() {
		return rPlayer;
	}

  static public boolean turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newt) {
    return OwareGame._turn(table, player, move, newt, false
		//#ifdef DLOGGING
				,Logger.getLogger("OwareGame")
		//#endif
				) != null;
  }

  public boolean turn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
    return OwareGame._turn((OwareTable) table, player, (OwareMove) move, (OwareTable) newt, false
		//#ifdef DLOGGING
				, logger
		//#endif
				) != null;
  }

}
