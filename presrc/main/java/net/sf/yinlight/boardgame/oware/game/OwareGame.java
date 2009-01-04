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
import java.util.Stack;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.midlet.OwareMIDlet;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware two player game.
	*/
public final class OwareGame extends TwoPlayerGame {

  public final static int NBR_MAX_STACK = 6;
  protected int evalNum = 0;
  protected byte rPlayer;
  protected OwareTable rTable;
  protected static int maxHoles = OwareTable.NBR_COL;
  public static final int GRAND_SLAM_ILLEGAL = 0;
  public static final int GRAND_SLAM_LEGAL_NO_CAPTURE = 1;
  public static final int GRAND_SLAM_LEGAL_OPPONENT = 2;
  public static final int GRAND_SLAM_LEGAL_LAST = 3;
  public static final int GRAND_SLAM_LEGAL_FIRST = 4;
  protected int[][] tableIntArray = new int[OwareTable.NBR_ROW][OwareTable.NBR_COL];
  protected Stack prevTbls = new Stack();
  protected Stack redoTbls = new Stack();
  protected Stack prevPlayers = new Stack();
  protected Stack redoPlayers = new Stack();
  protected int point;


	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("OwareGame");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finerLoggable = logger.isLoggable(Level.FINER);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
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
			,Logger logger
			//#endif
			) {
		GameTable tables[] = null;
		try {
			final int row = move.row;
			final int col = move.col;
			//#ifdef DLOGGING
			logger.finest("_turn start row,col,player,OwareTable.getPlayerItem(player),table.getItem(row, col),table.getSeeds(row, col),captureAll,captureIx=" + row + "," + col + "," + player + "," + OwareTable.getPlayerItem(player) + "," + table.getItem(row, col) + "," + table.getSeeds(row, col) + "," + captureAll + "," + captureIx);
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
			/* Score points/capture. */
			int holesCaptured = 0;
			int lastCaptureCol = lastCol;
			if ((captureAll || (captureIx >= 0)) && (lastRow != row)) {
				// Move in the opposite direction this time
				crow = lastRow;
				ccol = lastCol;
				int dir = (crow == 0) ? 1 : -1;
				for (; (ccol >= 0) && (ccol < OwareTable.NBR_COL); ccol += dir) {
					final int cseeds = newTable.getSeeds(crow, ccol);
					if ((cseeds == 2) || (cseeds == 3)) {
						if ((captureIx >= 0) && (captureIx == ccol)) {
							continue;
						}
						newTable.setPoint(player, (byte)(newTable.getPoint(player) +
								newTable.getSeeds(crow, ccol)));
						//#ifdef DLOGGING
						logger.finest("_turn capturing crow,ccol,dir,newTable.getSeeds(crow, ccol=" + crow + "," + ccol + "," + dir + "," + newTable.getSeeds(crow, ccol));
						//#endif
						newTable.setSeeds(crow, ccol, (byte)0);
						lastCaptureCol = ccol;
						if (++holesCaptured > maxHoles) {
							//#ifdef DLOGGING
							logger.finest("_turn capturing over max crow,ccol,holesCaptured=" + crow + "," + ccol + "," + holesCaptured);
							//#endif
							break;
						}
					} else {
						break;
					}
				}
			}

			boolean emptyHoles = allEmptyHoles(newTable, player);

			if (captureAll && changed && (holesCaptured > 0) && emptyHoles) {
					switch (OwareMIDlet.gsGrandSlam) {
					case GRAND_SLAM_ILLEGAL:
					default:
						//#ifdef DLOGGING
						logger.finest("_turn return illegal grand slam player=" + player);
						//#endif
						return null;
					case GRAND_SLAM_LEGAL_NO_CAPTURE:
						//#ifdef DLOGGING
						logger.finest("_turn return legal grand slam no capture player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, true,
							-1	
							//#ifdef DLOGGING
							,logger
							//#endif
							);
					case GRAND_SLAM_LEGAL_OPPONENT:
						crow = player;
						ccol = 0;
						for (; ccol < OwareTable.NBR_COL; ccol++) {
							newTable.setPoint((byte)(1 - player),
										(byte)(newTable.getPoint((byte)(1 - player)) +
										newTable.getSeeds(crow, ccol)));
								//#ifdef DLOGGING
								logger.finest("_turn capturing opponent crow,ccol,newTable.getSeeds(crow, ccol=" + crow + "," + ccol + "," + newTable.getSeeds(crow, ccol));
								//#endif
								newTable.setSeeds(crow, ccol, (byte)0);
						}
						break;
					case GRAND_SLAM_LEGAL_LAST:
						//#ifdef DLOGGING
						logger.finest("_turn return legal grand slam take last player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, false,
							lastCaptureCol	
							//#ifdef DLOGGING
							,logger
							//#endif
							);
					case GRAND_SLAM_LEGAL_FIRST:
						//#ifdef DLOGGING
						logger.finest("_turn return legal grand slam take first player=" + player);
						//#endif
						return _turn(table, player, move, newTable, animated, false, false,
								lastCol
							//#ifdef DLOGGING
							,logger
							//#endif
							);
					}
			} else if (allEmptyHoles(newTable, 1 - player)) {
					//#ifdef DLOGGING
					logger.finest("_turn return illegal opponent has no seeds player=" + player);
					//#endif
					return null;
			}
			
			if (animated) {
				vTables.addElement(new OwareTable(newTable));
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
			logger.severe("_turn error", e);
			//#endif
			return null;
			//#ifdef DLOGGING
		} finally {
			logger.finest("_turn return player,tables.length=" + player + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));
			//#endif
		}
  }

	public static boolean allEmptyHoles(OwareTable newTable, int player) {
		int crow = 1 - player;
		int ccol = 0;
		for (; (ccol >= 0) && (ccol < OwareTable.NBR_COL); ccol++) {
			if (newTable.getSeeds(crow, ccol) > 0) {
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
			,Logger logger
			//#endif
			) {
		return _turn(table, player, move, newTable, animated, true, false, -1
			//#ifdef DLOGGING
			,logger
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
   * @param endGame
   * @author Irv Bunton
   */
  protected void eval(boolean fullProcess, boolean endGame) {
		try {
			rTable.convertToIntArray(tableIntArray);
			boolean lazyProcess = fullProcess || endGame || isGameEnded();
			int pointFirstPlayer = rTable.getPoint((byte)0);
			int pointSecondPlayer = rTable.getPoint((byte)1);
			if (lazyProcess || endGame) {
				for (int i = 0; i < OwareTable.NBR_ROW; ++i) {
					for (int j = 0; j < OwareTable.NBR_COL; ++j) {
						int cseeds = rTable.getSeeds(i, j);
						if (cseeds > 0) {
							if (endGame) {
								rTable.setPoint((byte)i, (byte)(rTable.getPoint((byte)i) +
										cseeds));
								rTable.setSeeds(i, j, (byte)0);
							}
						}
						if (i == 0) {
							pointFirstPlayer += cseeds;
						} else {
								pointSecondPlayer += cseeds;
						}
					}
				}
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("eval pointFirstPlayer,pointSecondPlayer,rTable.getPassNum()=" + pointFirstPlayer + "," + pointSecondPlayer + "," +  rTable.getPassNum());}
			//#endif
			point = pointFirstPlayer - pointSecondPlayer;
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
		eval(true, false);
    int score = point;
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
    return (((OwareTable)rTable).getPoint(player) -
				((OwareTable)rTable).getPoint((byte)(1 - player)));
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
    eval(fullProcess, false);
  }

	public void procEndGame() {
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("procEndGame");}
		//#endif
		try {
			eval(false, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("procEndGame error", e);
			//#endif
		}
	}

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

  public static boolean turn(final OwareTable table, final byte player, final OwareMove move, final OwareTable newt) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("OwareGame");
		//#endif
    return OwareGame._turn(table, player, move, newt, false, true, false, -1
		//#ifdef DLOGGING
				, logger
		//#endif
				) != null;
  }

  public boolean turn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
    return OwareGame._turn((OwareTable) table, player, (OwareMove) move, (OwareTable) newt, false, true, false, -1
		//#ifdef DLOGGING
				, logger
		//#endif
				) != null;
  }

	public void saveLastTable(OwareTable ot, byte player) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("saveLastTable ot,player=" + ot + "," + player);}
		//#endif
		saveLast(redoTbls, prevTbls, new OwareTable(ot));
		saveLast(redoPlayers, prevPlayers, new Byte(player));
	}

	public void saveLast(Stack removeTbls, Stack addTbls, Object obj) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("saveLast removeTbls.size(),addTbls.size()=" + removeTbls.size() + "," + addTbls.size());}
		//#endif
		synchronized(this) {
			if (addTbls.size() >= NBR_MAX_STACK) {
				addTbls.removeElementAt(NBR_MAX_STACK - 1);
			}
			if (removeTbls.size() > 0) {
				removeTbls.removeAllElements();
			}
			addTbls.push(obj);
		}
	}

	public OwareTable popLastTable(byte player) {
		if ((prevTbls.size() == 0) || (prevPlayers.size() == 0)) {
			return null;
		}
		byte cplayer = ((Byte)prevPlayers.pop()).byteValue();
		if (cplayer != player) {
			return null;
		}
		return (OwareTable)prevTbls.pop();
	}

	public OwareTable popLast() {
		synchronized(this) {
			if ((prevTbls.size() == 0) || (prevPlayers.size() == 0)) {
				return null;
			}
			prevPlayers.pop();
			return (OwareTable)prevTbls.pop();
		}
	}

	public OwareTable undoTable(OwareTable ot, byte player) {
		Byte bplayer =  (Byte)unwindTable(prevPlayers, redoPlayers);
		OwareTable lastTbl = (OwareTable)unwindTable(prevTbls, redoTbls);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("undoTable rPlayer,bplayer,lastTbl=" + rPlayer + "," + bplayer + "," + lastTbl);}
		//#endif
		if ((bplayer == null) || (lastTbl == null)) {
			return null;
		}
		byte cplayer =  bplayer.byteValue();
		if (cplayer != player) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.severe("undoTable Error != player,player=" + player + "," + cplayer);}
			//#endif
			return null;
		}
		ot.copyDataFrom(lastTbl);
		setTable(ot, player, false);
		return ot;
	}

	public OwareTable redoTable(OwareTable ot, byte player) {
		Byte bplayer =  (Byte)unwindTable(redoPlayers, prevPlayers);
		OwareTable lastTbl = (OwareTable)unwindTable(redoTbls, prevTbls);
		if ((bplayer == null) || (lastTbl == null)) {
			return null;
		}
		byte cplayer =  bplayer.byteValue();
		if (cplayer != player) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.severe("undoTable Error != player,player=" + player + "," + cplayer);}
			//#endif
			return null;
		}
		ot.copyDataFrom(lastTbl);
		setTable(ot, player, false);
		return (OwareTable)ot;
	}

	private Object unwindTable(Stack removeTbls, Stack addTbls) {
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
			Object lastTbl = (Object)removeTbls.pop();
			addTbls.push(lastTbl);
			return lastTbl;
		}
	}

	public boolean checkLastTable() {
		synchronized(this) {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("checkLastTable prevTbls.size(),redoTbls.size(),prevTbls.size(),rPlayer=" + "," + prevTbls.size() + "," + redoTbls.size() + "," + rPlayer);}
			//#endif
			if (checkLast(prevTbls, prevPlayers, redoTbls, 0, rPlayer)) {
				return true;
			} else {
				return checkLast(prevTbls, prevPlayers, redoTbls, 1, rPlayer);
			}
		}
	}

	public boolean checkLast(Stack checkTbls, Stack checkPlayers,
			Stack addTbls, int ix, byte player) {
		synchronized(this) {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("checkLast checkTbls.size(),addTbls.size(),ix,player=" + "," + checkTbls.size() + "," + addTbls.size() + "," + ix + "," + player);}
			//#endif
			return ((checkPlayers.size() > (ix + 1)) && (checkPlayers.size() < NBR_MAX_STACK) &&
					(((Byte)checkPlayers.elementAt(
								checkPlayers.size() - ix - 1)).byteValue() == player) &&
			(checkTbls.size() > 0) && (addTbls.size() < NBR_MAX_STACK));
		}
	}

	public boolean checkLastRedoTable() {
		synchronized(this) {
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("checkLastRedoTable redoTbls.size(),prevTbls.size()=" + "," + redoTbls.size() + "," + prevTbls.size());}
			//#endif
			if (checkLast(redoTbls, redoPlayers, prevTbls, 0, rPlayer)) {
				return true;
			} else {
				return checkLast(redoTbls, redoPlayers, prevTbls, 1, rPlayer);
			}
		}
	}

	public void setMaxHoles(int maxHoles) {
			OwareGame.maxHoles = maxHoles;
	}

	public int getMaxHoles() {
			return (OwareGame.maxHoles);
	}

}
