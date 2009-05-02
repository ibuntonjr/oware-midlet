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
 * This was modified no earlier than 2008-01-15
 */
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.reversi;

import java.util.Vector;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.BoardGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

public final class ReversiGame extends BoardGame {

  protected int[][] heurMatrix;
  protected int libertyPenalty;
  public int numFirstPlayer;
  public int numSecondPlayer;
  protected final int S01_IX = 0;
  protected final int S11_IX = 1;
  protected int sBonus;
  protected boolean squareErase;

	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("ReversiGame");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

  public ReversiGame(final int[][] heurMatrix) {
    this(heurMatrix, 0, 0, false);
  }

  public ReversiGame(final int[][] heurMatrix, final int libertyPenalty, final int sBonus, final boolean squareErase) {
		super();
    this.heurMatrix = heurMatrix;
    this.libertyPenalty = libertyPenalty;
    this.sBonus = sBonus;
    this.squareErase = squareErase;
  }

  public ReversiGame(final ReversiGame rg) {
		this(new int[rg.rTable.nbrRow][rg.rTable.nbrCol], rg.libertyPenalty, rg.sBonus, rg.squareErase);
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor rg.rTable += null=" + (rg.rTable != null));}
		//#endif
		for (int i = 0; i < rg.rTable.nbrRow; i++) {
			System.arraycopy(rg.heurMatrix[i], 0, heurMatrix[i], 0, rg.rTable.nbrCol);
		}
		if (rg.rTable != null) {
			this.rTable = new ReversiTable((ReversiTable)rg.rTable);
		}
		this.numFirstPlayer = rg.numFirstPlayer;
		this.numFirstPlayer = rg.numSecondPlayer;
  }

  private GameTable[] _turn(final ReversiTable table, final byte player, final ReversiMove move, final ReversiTable newTable, final boolean animated
			//#ifdef DLOGGING
//@			,Logger logger
			//#endif
			) {
    final int row = move.row;
    final int col = move.col;
		// If a move is not a pass (!= nbrRow), and already has a piece
		// in it, we cannot make a move.
    if ((row != table.nbrRow) && (table.getItem(row, col) != 0)) { return null; }
    Vector vTables = null;
    GameTable tables[];
    if (animated) {
      vTables = new Vector();
    }
    newTable.copyDataFrom(table);
		newTable.setLastMove(player, move);
		//#ifdef DLOGGING
//@		if (traceLoggable) {logger.trace("_turn row,col=" + row + "," + col);}
		//#endif
    if (row == table.nbrRow) {
      // pass
      newTable.setPassNum(newTable.getPassNum() + 1);
      tables = new GameTable[1];
      tables[0] = newTable;
      return tables;
    }
    newTable.setPassNum(0);
    newTable.setItem(row, col, ReversiTable.getPlayerItem(player));
    if (animated) {
      vTables.addElement(new ReversiTable(newTable));
    }
    boolean flipped = false;
    for (int dirrow = -1; dirrow <= 1; ++dirrow) {
      for (int dircol = -1; dircol <= 1; ++dircol) {
        if ((dirrow == 0) && (dircol == 0)) {
          continue;
        }
        int c = 1;
        while (BoardGameMove.valid(rTable, row + c * dirrow, col + c * dircol) && (newTable.getItem(row + c * dirrow, col + c * dircol) == ReversiTable.getPlayerItem((byte) (1 - player)))) {
          ++c;
        }
        if ((c > 1) && ReversiMove.valid(rTable, row + c * dirrow, col + c * dircol) && (newTable.getItem(row + c * dirrow, col + c * dircol) == ReversiTable.getPlayerItem(player))) {
          flipped = true;
          for (int s1 = 1; s1 < c; ++s1) {
            newTable.flip(row + s1 * dirrow, col + s1 * dircol);
            if (animated) {
              vTables.addElement(new ReversiTable(newTable));
            }
          }
        }
      }
    }
    if (flipped) {
      if (animated) {
        tables = new GameTable[vTables.size()];
        for (int i = 0; i < vTables.size(); ++i) {
          tables[i] = (GameTable) vTables.elementAt(i);
        }
      }
      else {
        tables = new GameTable[1];
        tables[0] = newTable;
      }
      return tables;
    }
    newTable.setItem(row, col, (byte) 0);
    return null;
  }

  /*
   * @Override
   * @see net.eiroca.j2me.minmax.TwoPlayerGame#animatedTurn(net.eiroca.j2me.minmax.Table,
   *      byte, net.eiroca.j2me.minmax.Move, net.eiroca.j2me.minmax.Table)
   */
  public GameTable[] animatedTurn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
    return _turn((ReversiTable) table, player, (ReversiMove) move, (ReversiTable) newt, true
		//#ifdef DLOGGING
//@				, logger
		//#endif
				);
  }

  protected void eraseSquareHeuristic(final int[][] heurMatrix,
			final int[] savedCells, final int i, final int j, final int id, final int jd) {
    savedCells[S01_IX] = heurMatrix[i][j + jd];
    savedCells[S11_IX] = heurMatrix[i + id][j + jd];
    heurMatrix[i][j + jd] = 0;
    heurMatrix[i + id][j] = 0;
    heurMatrix[i + id][j + jd] = 0;
  }

  /**
   *  Evaluate the table either with actual points or estimated goodness with
	 *  heuristic
	 *
   * @param lazyProcess - If lazyProcess is true, it gets actual points
	 * 											else it get goodness of board using heuristic.
   * @param bg			    - Board game
   * @param t           - Board table
   * @param player      - Current player
   * @param endGame     - End of game
   */
  public void eval(boolean lazyProcess, BoardGame bg, GameTable t,
			final byte player, boolean endGame) {
		try {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("eval lazyProcess,endGame=" + lazyProcess + "," + endGame);}
			//#endif
			BoardGameTable bgt = (BoardGameTable)t;
			ReversiGame rg = (ReversiGame)bg;
			int[][] tableIntArray = new int[bgt.nbrRow][bgt.nbrCol];
			bgt.convertToIntArray(tableIntArray);
			rg.numFirstPlayer = 0;
			rg.numSecondPlayer = 0;
			int pointFirstPlayer = 0;
			int pointSecondPlayer = 0;
			int numFirstFreeNeighbours = 0;
			int numSecondFreeNeighbours = 0;
			int[] savedCells = new int[2];
			boolean saved = false;
			if (!lazyProcess && rg.squareErase) {
				int lastRowIx = bgt.nbrRow - 1;
				int lastColIx = bgt.nbrCol - 1;
				if (tableIntArray[0][0] != 0) {
					rg.eraseSquareHeuristic(rg.heurMatrix, savedCells, 0, 0, 1, 1);
					saved = true;
				}
				if (tableIntArray[0][lastColIx] != 0) {
					rg.eraseSquareHeuristic(rg.heurMatrix, savedCells, 0, ReversiTable.MAX_NBR_COL - 1, 1, -1);
					saved = true;
				}
				if (tableIntArray[lastRowIx][lastColIx] != 0) {
					rg.eraseSquareHeuristic(rg.heurMatrix, savedCells, ReversiTable.MAX_NBR_ROW - 1, ReversiTable.MAX_NBR_COL - 1, -1, -1);
					saved = true;
				}
				if (tableIntArray[lastRowIx][0] != 0) {
					rg.eraseSquareHeuristic(rg.heurMatrix, savedCells, ReversiTable.MAX_NBR_ROW - 1, 0, -1, 1);
					saved = true;
				}
			}
			for (int i = 0; i < bgt.nbrRow; ++i) {
				for (int j = 0; j < bgt.nbrCol; ++j) {
					final int item = tableIntArray[i][j];
					switch (item) {
						case 1:
							++rg.numFirstPlayer;
							if (!lazyProcess) {
								pointFirstPlayer += rg.heurMatrix[i][j];
								if (libertyPenalty != 0) {
									numFirstFreeNeighbours += rg.freeNeighbours(tableIntArray, i, j);
								}
							}
							break;
						case 2:
							++rg.numSecondPlayer;
							if (!lazyProcess) {
								pointSecondPlayer += rg.heurMatrix[i][j];
								if (libertyPenalty != 0) {
									numSecondFreeNeighbours += rg.freeNeighbours(tableIntArray, i, j);
								}
							}
							break;
					}
				}
			}
			if (!lazyProcess && rg.squareErase && saved) {
				rg.restoreSquareHeuristic(rg.heurMatrix, savedCells, 0, 0, 1, 1);
				rg.restoreSquareHeuristic(rg.heurMatrix, savedCells, 0, ReversiTable.MAX_NBR_COL - 1, 1, -1);
				rg.restoreSquareHeuristic(rg.heurMatrix, savedCells, ReversiTable.MAX_NBR_ROW - 1, ReversiTable.MAX_NBR_COL - 1, -1, -1);
				rg.restoreSquareHeuristic(rg.heurMatrix, savedCells, ReversiTable.MAX_NBR_ROW - 1, 0, -1, 1);
			}
			int squareBonusPoint = 0;
			if (!lazyProcess && (rg.sBonus != 0)) {
				squareBonusPoint = rg.squareBonus(tableIntArray);
			}
			if (lazyProcess) {
				rg.point = rg.numFirstPlayer - rg.numSecondPlayer;
				if (rg.isGameEnded(bg, bgt, player)) {
					if (rg.point > 0) {
						rg.point += GameMinMax.MAX_POINT;
					}
					else if (rg.point < 0) {
						rg.point -= GameMinMax.MAX_POINT;
					}
				}
			}
			else {
				rg.point = pointFirstPlayer - pointSecondPlayer + libertyPenalty * (numSecondFreeNeighbours - numFirstFreeNeighbours) + rg.sBonus * squareBonusPoint;
			}
			if (player == 1) {
				rg.point = -rg.point;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("eval error", e);
			//#endif
		}
  }

  /**
   * Evaluate points
	 *
   * @param fullProcess - If true, use estimate of goodness with heuristic.
   */
  protected void eval(boolean fullProcess) {
    boolean lazyProcess = !fullProcess || isGameEnded() || (numFirstPlayer + numSecondPlayer > 58);
		eval(lazyProcess, this, rTable, rPlayer, isGameEnded());
	}
	protected int freeNeighbours(int[][] tableIntArray, final int i, final int j) { int freeNeighbours = 0;
			for (int id = -1; id <= 1; ++id) {
      for (int jd = -1; jd <= 1; ++jd) {
        if ((i + id >= 0) && (i + id < rTable.nbrRow) && (j + jd >= 0) && (j + jd < rTable.nbrCol) && (tableIntArray[i + id][j + jd] == 0)) {
          ++freeNeighbours;
        }
      }
    }
    return freeNeighbours;
  }

  public int getEvalNum() {
    return evalNum;
  }

  public int getGameResult(byte player) {
    int piecediff = numFirstPlayer - numSecondPlayer;
    if (player == 1) {
      piecediff = -piecediff;
    }
    if (piecediff > 0) {
      return TwoPlayerGame.WIN;
    }
    else if (piecediff < 0) {
      return TwoPlayerGame.LOSS;
    }
    else {
      return TwoPlayerGame.DRAW;
    }
  }

  public int getPoint() {
    return point;
  }

  public boolean hasPossibleMove(final GameTable table, final byte player) {
		try {
			if (!(table instanceof BoardGameTable)) { return false; }
			final ReversiMove[] moves = (ReversiMove[]) possibleMoves(table, player);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("hasPossibleMove moves.length,moves[0].row=" + ((moves == null) ? "null" : Integer.toString(moves.length) +
//@					"," + moves[0].row));}
			//#endif
			return (moves != null) && ((moves.length > 1) || (moves[0].row != ((BoardGameTable)table).nbrRow));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("hasPossibleMove error player=" + player, e);
			//#endif
			return false;
		}
  }

  public int getTblPoint(final GameTable t, final byte player) {
	  ReversiGame rg = new ReversiGame(this);
	  rg.rTable = (BoardGameTable)t;
		// Use estimate of goodness based on heuristic.
		eval(false, rg, t, player, false);
		return rg.getPoint();
  }

  public boolean isGameEnded(BoardGame bg, BoardGameTable t, byte player) {
    if (!(bg instanceof ReversiGame)) { return false; }
    if (!(t instanceof ReversiTable)) { return false; }
		ReversiGame rg = (ReversiGame)bg;
		ReversiTable rt = (ReversiTable)t;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("isGameEnded rg.numFirstPlayer,rg.numSecondPlayer,rt.getPassNum()=" + rg.numFirstPlayer + "," + rg.numSecondPlayer + "," + rt.getPassNum());}
		//#endif
    if ((rg.numFirstPlayer + rg.numSecondPlayer == 64) || (rg.numFirstPlayer == 0) || (rg.numSecondPlayer == 0) || (rt.getPassNum() == 2)) { return true; }
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("isGameEnded false");}
		//#endif
    return false;
  }

  public boolean isGameEnded() {
		return isGameEnded(this, rTable, rPlayer);
  }

  public GameMove[] possibleMoves(final GameTable table, final byte player) {
		try {
			if (!(table instanceof ReversiTable)) { return null; }
			final Vector moves = new Vector();
			if (((ReversiTable) table).getPassNum() == 2) {
				// two passes: end of the game
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("possibleMoves pass num == 2");}
				//#endif
				return null;
			}
			BoardGameTable bgt = (BoardGameTable)table;
			final ReversiTable newTable = (ReversiTable)bgt.getEmptyTable();
			final ReversiMove move = new ReversiMove(0, 0);
			boolean hasMove = false;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("possibleMoves starting player=" + player);}
			//#endif
			for (int row = 0; row < bgt.nbrRow; ++row) {
				for (int col = 0; col < ((BoardGameTable)table).nbrCol; ++col) {
					move.setCoordinates(row, col);
					if (!hasMove && (((ReversiTable) table).getItem(row, col) == 0)) {
						hasMove = true;
					}
					final boolean goodMove = turn(table, player, move, newTable);
					if (goodMove) {
						moves.addElement(new ReversiMove(move));
					}
				}
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("possibleMoves player,hasMove,moves.size()=" + player + "," + hasMove + "," + moves.size());}
			//#endif
			if (!hasMove) { return null; }
			if (moves.size() == 0) {
				// need to pass
				moves.addElement(new ReversiMove(bgt.nbrRow, bgt.nbrCol));
			}
			final GameMove[] retMoves = new ReversiMove[moves.size()];
			moves.copyInto(retMoves);
			return retMoves;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("possibleMoves error player=" + player, e);
			//#endif
			return null;
		}
  }

  public void resetEvalNum() {
    evalNum = 0;
  }

  protected void restoreSquareHeuristic(final int[][] heurMatrix,
			final int[] savedCells, final int i, final int j, final int id,
			final int jd) {
		try {
			heurMatrix[i][j + jd] = savedCells[S01_IX];
			heurMatrix[i + id][j] = savedCells[S01_IX];
			heurMatrix[i + id][j + jd] = savedCells[S11_IX];
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("restoreSquareHeuristic error", e);
			//#endif
		}
	}

  /**
   * Set the table to the parameter and evauate points.
	 *
   * @param table - Board table
   * @param player - current player
   * @param fullProcess - If true, use estimate of goodness with heuristic.
   */
  protected void setTable(final GameTable table, final byte player, final boolean fullProcess) {
    if (!(table instanceof ReversiTable)) { throw new IllegalArgumentException(); }
    rTable = (ReversiTable) table;
    rPlayer = player;
    ++evalNum;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("setTable rTable != null,rPlayer,player,evalNum,fullProcess=" + (rTable != null) + "," + rPlayer + "," + player + "," + ReversiTable.getPlayerItem(player) + "," + evalNum + "," + fullProcess);}
		//#endif
    eval(fullProcess);
  }

	public void procEndGame(byte player) {
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("procEndGame");}
		//#endif
		try {
			// Use actual points instead of estimated points (lazyProcess = true).
			eval(true, this, rTable, player, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("procEndGame error", e);
			//#endif
		}
	}

  protected int squareBonus(int[][] tableIntArray) {
		final int bonus[] = new int[3];
		bonus[1] = 0;
		bonus[2] = 0;
		try {
			boolean c1 = true;
			boolean c2 = true;
			boolean c3 = true;
			boolean c4 = true;
			final int corner1 = tableIntArray[0][0];
			if (corner1 != 0) {
				int c1r = 1;
				while ((c1r < rTable.nbrCol) && (tableIntArray[0][c1r] == corner1)) {
					++c1r;
				}
				bonus[corner1] += c1r - 1;
				if (c1r == rTable.nbrCol) {
					c2 = false;
				}
			}
			int lastRowIx = rTable.nbrRow - 1;
			int lastColIx = rTable.nbrCol - 1;
			final int corner2 = tableIntArray[0][lastColIx];
			if (corner2 != 0) {
				if (c2) {
					int c2l = 1;
					while ((c2l < rTable.nbrCol) && (tableIntArray[0][lastColIx - c2l] == corner2)) {
						++c2l;
					}
					bonus[corner2] += c2l - 1;
					if (c2l == rTable.nbrCol) {
						c1 = false;
					}
				}
				int c2r = 1;
				while ((c2r < rTable.nbrRow) && (tableIntArray[c2r][lastColIx] == corner2)) {
					++c2r;
				}
				bonus[corner2] += c2r - 1;
				if (c2r == rTable.nbrRow) {
					c3 = false;
				}
			}
			final int corner3 = tableIntArray[lastRowIx][lastRowIx];
			if (corner3 != 0) {
				if (c3) {
					int c3l = 1;
					while ((c3l < rTable.nbrRow) && (tableIntArray[lastRowIx - c3l][lastColIx] == corner3)) {
						++c3l;
					}
					bonus[corner3] += c3l - 1;
				}
				int c3r = 1;
				while ((c3r < rTable.nbrRow) && (tableIntArray[lastRowIx][lastColIx - c3r] == corner3)) {
					++c3r;
				}
				bonus[corner3] += c3r - 1;
				if (c3r == rTable.nbrRow) {
					c4 = false;
				}
			}
			final int corner4 = tableIntArray[lastRowIx][0];
			if (corner4 != 0) {
				if (c4) {
					int c4l = 1;
					while ((c4l < rTable.nbrCol) && (tableIntArray[lastRowIx][c4l] == corner4)) {
						++c4l;
					}
					bonus[corner4] += c4l - 1;
				}
				int c4r = 1;
				while ((c4r < rTable.nbrRow) && (tableIntArray[lastRowIx - c4r][0] == corner4)) {
					++c4r;
				}
				bonus[corner4] += c4r - 1;
				if (c4r == rTable.nbrRow) {
					c1 = false;
				}
			}
			if ((corner1 != 0) && c1) {
				int c1l = 1;
				while ((c1l < rTable.nbrRow) && (tableIntArray[c1l][0] == corner1)) {
					++c1l;
				}
				bonus[corner1] += c1l - 1;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("squareBonus error", e);
			//#endif
		}
    return bonus[1] - bonus[2];
  }

  public boolean turn(final GameTable table, final byte player, final GameMove move, final GameTable newt) {
    return _turn((ReversiTable) table, player, (ReversiMove) move, (ReversiTable) newt, false
		//#ifdef DLOGGING
//@				, logger
		//#endif
				) != null;
  }

}
