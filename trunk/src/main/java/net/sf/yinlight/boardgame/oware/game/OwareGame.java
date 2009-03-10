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
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
//#define DNOLOGGING
package net.sf.yinlight.boardgame.oware.game;

import java.util.Vector;
import java.util.Stack;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.midlet.OwareMIDlet;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware two player game.
	*/
public final class OwareGame extends BoardGame {

  public final static int NBR_MAX_STACK = 6;
  protected static int maxHouses = OwareTable.NBR_COL;
  public static final int GRAND_SLAM_ILLEGAL = 0;
  public static final int GRAND_SLAM_LEGAL_NO_CAPTURE = 1;
  public static final int GRAND_SLAM_LEGAL_OPPONENT = 2;
  public static final int GRAND_SLAM_LEGAL_LAST = 3;
  public static final int GRAND_SLAM_LEGAL_FIRST = 4;
  public static final int GRAND_SLAM_LEGAL_24 = 5;
  public static final int GRAND_SLAM_LEGAL_MAX = 24;

	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("OwareGame");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
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
  private static GameTable[] _turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newTable, final boolean animated,
			boolean captureAll, boolean noCapture, int captureIx
			//#ifdef DLOGGING
//@			,Logger logger
			//#endif
			) {
		GameTable tables[] = null;
		try {
			final int row = move.row;
			final int col = move.col;
			//#ifdef DLOGGING
//@			logger.finest("_turn start row,col,player,OwareTable.getPlayerItem(player),table.getItem(row, col),table.getSeeds(row, col),captureAll,captureIx=" + row + "," + col + "," + player + "," + OwareTable.getPlayerItem(player) + "," + table.getItem(row, col) + "," + table.getSeeds(row, col) + "," + captureAll + "," + captureIx);
			//#endif
			/* If the cup is not for the current player, we are not allowed to
				 do anything with it.  Also, if there are no seeds, you cannot
				 take the turn. */
			if ((row != table.nbrRow) &&
					((table.getItem(row, col) != OwareTable.getPlayerItem(player)) ||
					(table.getSeeds(row, col) == 0))) { return null; }
			Vector vTables = null;
			if (animated) {
				vTables = new Vector();
			}
			newTable.copyDataFrom(table);
			if (row == table.nbrRow) {
				// pass.  Leave table as is
				newTable.setPassNum(newTable.getPassNum() + 1);
				tables = new GameTable[1];
				tables[0] = newTable;
					//#ifdef DLOGGING
//@					logger.finest("_turn newTable.getPassNum()=" + newTable.getPassNum());
					//#endif
				return tables;
			}
			newTable.setPassNum(0);
			boolean changed = false;
			int seeds = newTable.getSeeds(row, col);
			newTable.setSeeds(row, col, (byte)0);
			move.setPoint((byte)seeds);
			newTable.setLastMove(player, move);
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
					ccol = (crow == 1) ? 0 : (table.nbrCol - 1);
				}
				int dir = (crow == 0) ? -1 : 1;
				for (; (ccol >= 0) && (ccol < table.nbrCol); ccol += dir) {
					if ((crow != row) || (ccol != col)) {
						int cseeds = newTable.getSeeds(crow, ccol);
						newTable.setSeeds(crow, ccol, (byte)(cseeds + 1));
						//#ifdef DLOGGING
//@						logger.finest("_turn crow,ccol,dir,(cseeds + 1),seeds=" + crow + "," + ccol + "," + dir + "," + (cseeds + 1) + "," + seeds);
						//#endif
						changed = true;
						lastRow = crow;
						lastCol = ccol;
						if (--seeds <= 0) {
							break;
						}
					//#ifdef DLOGGING
//@					} else {
//@						logger.finest("_turn skipping as it is the same row,col,crow,ccol,dir=" + row + "," + col + "," + crow + "," + ccol + "," + dir + "," + seeds);
						//#endif
					}
				}
			}

			//#ifdef DLOGGING
//@			logger.finest("_turn finished for crow,ccol,lastRow,lastCol,seeds=" + crow + "," + ccol + "," + lastRow + "," + lastCol + "," + seeds);
			//#endif
			/* Score points/capture. */
			int holesCaptured = 0;
			int lastCaptureCol = lastCol;
			if ((captureAll || (captureIx >= 0)) && (lastRow != row)) {
				// Move in the opposite direction this time
				crow = lastRow;
				ccol = lastCol;
				int dir = (crow == 0) ? 1 : -1;
				for (; (ccol >= 0) && (ccol < table.nbrCol); ccol += dir) {
					final int cseeds = newTable.getSeeds(crow, ccol);
					if ((cseeds == 2) || (cseeds == 3)) {
						if ((captureIx >= 0) && (captureIx == ccol)) {
							continue;
						}
						newTable.setPoint(player, (byte)(newTable.getPoint(player) +
								newTable.getSeeds(crow, ccol)));
						//#ifdef DLOGGING
//@						logger.finest("_turn capturing crow,ccol,dir,newTable.getSeeds(crow, ccol=" + crow + "," + ccol + "," + dir + "," + newTable.getSeeds(crow, ccol));
						//#endif
						newTable.setSeeds(crow, ccol, (byte)0);
						lastCaptureCol = ccol;
						if (++holesCaptured > maxHouses) {
							//#ifdef DLOGGING
//@							logger.finest("_turn capturing over max crow,ccol,holesCaptured=" + crow + "," + ccol + "," + holesCaptured);
							//#endif
							break;
						}
					} else {
						break;
					}
				}
			}

			boolean emptyHoles = allEmptyHoles(newTable, (1 - player));

			if (captureAll && changed && (holesCaptured > 0) && emptyHoles) {
					switch (OwareMIDlet.gsGrandSlam) {
					case GRAND_SLAM_ILLEGAL:
					default:
						//#ifdef DLOGGING
//@						logger.finest("_turn return illegal grand slam player=" + player);
						//#endif
						return null;
					case GRAND_SLAM_LEGAL_NO_CAPTURE:
						//#ifdef DLOGGING
//@						logger.finest("_turn return legal grand slam no capture player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, true,
							-1	
							//#ifdef DLOGGING
//@							,logger
							//#endif
							);
					case GRAND_SLAM_LEGAL_OPPONENT:
						// Give seeds to the opponent
						giveToPlayer(player, (byte)(1 - player), newTable);
						break;
					case GRAND_SLAM_LEGAL_LAST:
						//#ifdef DLOGGING
//@						logger.finest("_turn return legal grand slam take last player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, false,
							lastCaptureCol	
							//#ifdef DLOGGING
//@							,logger
							//#endif
							);
					case GRAND_SLAM_LEGAL_FIRST:
						//#ifdef DLOGGING
//@						logger.finest("_turn return legal grand slam take first player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, false,
								lastCol
							//#ifdef DLOGGING
//@							,logger
							//#endif
							);
					case GRAND_SLAM_LEGAL_24:
						if (newTable.getPoint(player) <= GRAND_SLAM_LEGAL_MAX) {
							//#ifdef DLOGGING
//@							logger.finest("_turn return illegal grand slam not > 24 =" + player);
							//#endif
							return null;
						}
					}
			} else if (allEmptyHoles(newTable, 1 - player)) {
					//#ifdef DLOGGING
//@					logger.finest("_turn return illegal opponent has no seeds player=" + player);
					//#endif
					return null;
			}
			
			if (animated) {
				vTables.addElement((OwareTable)newTable.getBoardGameTable(newTable));
			}
			if (changed) {
				if (animated) {
					tables = new GameTable[vTables.size()];
					try {
						vTables.copyInto(tables);
					} catch (Throwable e) {
						for (int i = 0; i < vTables.size(); ++i) {
							tables[i] = (GameTable) vTables.elementAt(i);
						}
					}
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
//@			logger.severe("_turn error", e);
			//#endif
			return null;
			//#ifdef DLOGGING
//@		} finally {
//@			logger.finest("_turn return player,tables.length=" + player + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));
			//#endif
		}
  }

	static public void giveToPlayer(final byte player, final byte opPlayer,
			OwareTable table) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("OwareGame");
		//#endif
		for (int crow = 0; crow < table.nbrRow; crow++) {
			for (int ccol = 0; ccol < table.nbrCol; ccol++) {
				int ix = table.getItem(crow, ccol) - 1;
				if (ix == player) {
					table.setPoint((byte)(1 - opPlayer),
								(byte)(table.getPoint((byte)(1 - opPlayer)) +
								table.getSeeds(crow, ccol)));
						//#ifdef DLOGGING
//@						logger.finest("_turn capturing opponent crow,ccol,table.getSeeds(crow, ccol=" + crow + "," + ccol + "," + table.getSeeds(crow, ccol));
						//#endif
						table.setSeeds(crow, ccol, (byte)0);
				}
			}
			}
		}

	public static boolean allEmptyHoles(OwareTable table, int player) {
		int crow = player;
		int ccol = 0;
		for (; (ccol >= 0) && (ccol < table.nbrCol); ccol++) {
			if (table.getSeeds(crow, ccol) > 0) {
				return false;
			}
		}
		return true;
	}

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
   * @param animated
   * @return    GameTable[]
   * @author Irv Bunton
   */
  private static GameTable[] _turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newTable, final boolean animated
			//#ifdef DLOGGING
//@			,Logger logger
			//#endif
			) {
		return _turn(table, player, move, newTable, animated, true, false, -1
			//#ifdef DLOGGING
//@			,logger
			//#endif
			);
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
			return _turn((OwareTable) table, player, (OwareMove) move, (OwareTable) newt, true, true, false, -1
		//#ifdef DLOGGING
//@				, logger
		//#endif
				);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("animatedTurn error", e);
			//#endif
			return null;
		}
  }

  /**
   * Evaluate the board to determine points.
	 *
   * @param fullProcess
   * @param endGame
   * @author Irv Bunton
   */
  public void eval(boolean lazyProcess, BoardGame bg, GameTable t,
			final byte player, boolean endGame) {
		try {
			OwareTable ctable = (OwareTable)t;
			int pointFirstPlayer = ctable.getPoint((byte)0);
			int pointSecondPlayer = ctable.getPoint((byte)1);
			if (endGame) {
				for (int i = 0; i < ctable.nbrRow; ++i) {
					for (int j = 0; j < ctable.nbrCol; ++j) {
						int ix = ctable.getItem(i, j) - 1;
						int cseeds = ctable.getSeeds(i, j);
						if (cseeds > 0) {
							if (endGame) {
								ctable.setPoint((byte)ix, (byte)(ctable.getPoint((byte)ix) +
										cseeds));
								ctable.setSeeds(i, j, (byte)0);
							}
						}
						if (ix == 0) {
							pointFirstPlayer += cseeds;
						} else {
								pointSecondPlayer += cseeds;
						}
					}
				}
			}
			point = pointFirstPlayer - pointSecondPlayer;
			if (player == 1) {
				point = -point;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("eval player,pointFirstPlayer,pointSecondPlayer,point,ctable.getPassNum()=" + player + "," + pointFirstPlayer + "," + pointSecondPlayer + "," +  point + "," + ctable.getPassNum());}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("eval error", e);
			//#endif
		}
  }

  protected void eval(boolean fullProcess, boolean endGame, byte player) {
    boolean lazyProcess = !fullProcess || isGameEnded();
		eval(lazyProcess, this, rTable, player, endGame);
	}

  public int getGameResult(final byte player) {
		eval(false, true, player);
    int score = point;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("getGameResult player,score=" + player + "," + score);}
		//#endif
    if (score > 0) {
      return TwoPlayerGame.WIN;
    }
    else if (score < 0) {
      return TwoPlayerGame.LOSS;
    }
    else {
      return TwoPlayerGame.DRAW;
    }
  }

  public int getTblPoint(final GameTable table, final byte player) {
    if (!(table instanceof OwareTable)) { return 0; }
    return (((OwareTable)rTable).getPoint(player) -
				((OwareTable)rTable).getPoint((byte)(1 - player)));
  }

  public boolean hasPossibleMove(final GameTable table, final byte player) {
		try {
			final OwareMove[] moves = (OwareMove[]) possibleMoves(table, player);
			return (moves != null) && ((moves.length > 1) || (moves[0].row !=
						((BoardGameTable)table).nbrRow));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("hasPossibleMove error", e);
			//#endif
			return false;
		}
  }

  public boolean isGameEnded(BoardGame bg, BoardGameTable t, byte player) {
    if (!(bg instanceof OwareGame)) { return false; }
    if (!(t instanceof OwareTable)) { return false; }
    OwareTable ot = (OwareTable)t;
    if ((ot.getPoint((byte)0) >= OwareTable.WINNING_SCORE) || (ot.getPoint((byte)1) >= OwareTable.WINNING_SCORE) || (t.getPassNum() == 2) ||
				(allEmptyHoles(ot, player) && allEmptyHoles(ot, (1 - player)))) { return true; }
    return false;
  }

  public boolean isGameEnded() {
		return isGameEnded(this, rTable, rPlayer);
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
//@				if (finestLoggable) {logger.finest("possibleMoves pass num == 2");}
				//#endif
				// two passes: end of the game
				return null;
			}
			// This has no values, but they are put in when _turn does a copy
			// to this table.
			OwareTable newTable = (OwareTable)((OwareTable)table).getEmptyTable();

			// Possible moves can only be in the current row.
			OwareMove move = null;
			boolean hasMove = false;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("possibleMoves starting player=" + player);}
			//#endif
			int col = 0;
			for (int row = player; col < ((BoardGameTable)table).nbrCol; ++col) {
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
//@			if (finestLoggable) {logger.finest("possibleMoves player,hasMove,moves.size()=" + player + "," + hasMove + "," + moves.size());}
			//#endif
			if (!hasMove) {
				if (OwareMIDlet.gsOpponentEmpty) {
					// Give seeds to the current player (who is the opponent of the
					// previous player).
					giveToPlayer((byte)(1 - player), player, (OwareTable)table);
				} else {
					// Give seeds to the current player.
					giveToPlayer(player, (byte)(1 - player), (OwareTable)table);
				}
				return null;
			}
			// Oware does not have pass.  May need later for other Mancala.
			final GameMove[] retMoves = new OwareMove[moves.size()];
			try {
				moves.copyInto(retMoves);
			} catch (Throwable e) {
				for (int m = 0; m < moves.size(); ++m) {
					retMoves[m] = (GameMove) moves.elementAt(m);
				}
			}
			return retMoves;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("possibleMoves error player=" + player, e);
			//#endif
			return null;
		}
  }

  protected void setTable(final GameTable table, final byte player, final boolean fullProcess) {
    if (!(table instanceof OwareTable)) { throw new IllegalArgumentException(); }
    rTable = (OwareTable) table;
    rPlayer = player;
    ++evalNum;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("setTable rTable != null,rPlayer,player,evalNum=" + (rTable != null) + "," + rPlayer + "," + player + "," + OwareTable.getPlayerItem(player) + "," + evalNum);}
		//#endif
    eval(fullProcess, false, player);
  }

	public void procEndGame(byte player) {
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("procEndGame");}
		//#endif
		try {
			eval(false, true, player);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("procEndGame error", e);
			//#endif
		}
	}

  public static boolean turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newt) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("OwareGame");
		//#endif
    return OwareGame._turn(table, player, move, newt, false, true, false, -1
		//#ifdef DLOGGING
//@				, logger
		//#endif
				) != null;
  }

  public boolean turn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
    return OwareGame._turn((OwareTable) table, player, (OwareMove) move, (OwareTable) newt, false, true, false, -1
		//#ifdef DLOGGING
//@				, logger
		//#endif
				) != null;
  }

	public void setMaxHouses(int maxHouses) {
			OwareGame.maxHouses = maxHouses;
	}

	public int getMaxHouses() {
			return (OwareGame.maxHouses);
	}

}
