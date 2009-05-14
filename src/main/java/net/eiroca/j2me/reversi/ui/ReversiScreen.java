/** GPL >= 2.0
	* FIX Selct minmax
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
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
package net.eiroca.j2me.reversi.ui;

import java.util.Timer;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.eiroca.j2me.reversi.midlet.Reversi;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.eiroca.j2me.reversi.ReversiGame;
import net.eiroca.j2me.reversi.ReversiMove;
import net.eiroca.j2me.reversi.ReversiTable;
import net.sf.yinlight.boardgame.oware.game.ui.MinimaxTimerTask;
import net.sf.yinlight.boardgame.oware.game.BoardGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;
import net.sf.yinlight.boardgame.oware.game.LimitedMinMax;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

public final class ReversiScreen extends BoardGameScreen {

  private static final int[][] heurMatrix = {
      {
          500, -240, 85, 69, 69, 85, -240, 500
      }, {
          -240, -130, 49, 23, 23, 49, -130, -240
      }, {
          85, 49, 1, 9, 9, 1, 49, 85
      }, {
          69, 23, 9, 32, 32, 9, 23, 69
      }, {
          69, 23, 9, 32, 32, 9, 23, 69
      }, {
          85, 49, 1, 9, 9, 1, 49, 85
      }, {
          -240, -130, 49, 23, 23, 49, -130, -240
      }, {
          500, -240, 85, 69, 69, 85, -240, 500
      }
  };
  private final int pnums[] = new int[2];

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("ReversiScreen");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  //#endif

  public ReversiScreen(final GameApp midlet) {
    super(midlet, false, true, AppConstants.MSG_REVERSI_NAME);
    BoardGameScreen.rgame = new ReversiGame(ReversiScreen.heurMatrix, 10, 18, true);
		gMiniMax = new LimitedMinMax(); // FIX
  }

  public void init() {
		try {
			synchronized (this) {
				BoardGameScreen.table = new ReversiTable(
						BoardGameApp.gsRow[BoardGameApp.PD_CURR],
						BoardGameApp.gsCol[BoardGameApp.PD_CURR]);
			}
			super.init();
			//#ifdef DLOGGING
//@			if (finestLoggable) {tableDraws = 2;}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("init error", e);
			//#endif
		}
  }

  protected void drawPiece(BoardGameTable bgt, final int row, final int col, final int player,
			boolean onBoard, Image cupImage, int yadjust, int lastMovePoint) {
		final int x = off_x + col * sizex + piece_offx;
		int y = off_y + row * sizey + piece_offy + yadjust;
		if (y < 0) {
			y = 0;
		}
    if (player == 1) {
      screen.setColor(BoardGameScreen.COLOR_P1);
    }
    else {
      screen.setColor(BoardGameScreen.COLOR_P2);
    }
		if (cupImage == null) {
			screen.fillArc(x, y, pieceWidth, pieceHeight, 0, 360);
		} else {
			screen.drawImage(cupImage, x + cupImagexOffset, y, Graphics.TOP | Graphics.LEFT);
		}
		if (lastMovePoint > 0) {
      screen.setColor(BoardGameScreen.COLOR_FG);
			screen.fillRect(x, y + (pieceHeight / 2) +
					(pieceHeight / bgt.nbrRow),
											pieceWidth, (pieceHeight / 4));
			//#ifdef DLOGGING
//@			if ((drawItems || (tableDraws-- > 0)) && finestLoggable) {logger.finest("drawPiece line row,col=" + row + "," + col);}
			//#endif
		}
  }

  protected void drawTable(BoardGameTable bgt) {
		try {
			//#ifdef DLOGGING
//@			if ((tableDraws-- > 0) && finestLoggable) {drawItems = true;logger.finest("drawTable up BoardGameScreen.actPlayer=" + BoardGameScreen.actPlayer);}
			//#endif
			pnums[0] = 0;
			pnums[1] = 0;
			int item;
			for (int i = 0; i < bgt.nbrRow; ++i) {
				for (int j = 0; j < bgt.nbrCol; ++j) {
					item = bgt.getItem(i, j);
					if (item != 0) {
						int lastCol = -10;
						int lastRow = -10;
						ReversiMove clastMove =
							(ReversiMove)bgt.getLastMove(item - 1);
						// Invalid move with row/col = max nbr row/col, it is off the
						// table.  Highlight in vertical info.
						if ((clastMove != null) &&
								clastMove.valid(bgt, clastMove)) {
							lastRow = clastMove.row;
							lastCol = clastMove.col;
						}
						drawPiece(bgt, i, j, item, true, (item == 1) ? piece1Image : piece2Image,
								0, ((i == lastRow) && (j == lastCol)) ? 1 : 0);
						pnums[item - 1]++;
					}
				}
			}
			infoLines[0] = Integer.toString(pnums[0]);
			infoLines[1] = Integer.toString(pnums[1]);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("drawTable error", e);
			//#endif
			//#ifdef DLOGGING
//@		} finally {
//@			if (drawItems) {
//@				drawItems = false;
//@			}
			//#endif
		}
  }

  public void drawVertInfo(BoardGameTable bgt) {
    // two pieces
		drawVertPiece(bgt, 0, bgt.nbrCol, 1,
				((BoardGameApp.gsFirst == 0) ? piece2Image : piece1Image), 0);
		if (BoardGameScreen.actPlayer == 0) {
			drawSelectionBox(bgt.nbrCol, 0, 0);
		}
		drawVertPiece(bgt, bgt.nbrRow - 1, bgt.nbrCol, 2,
				((BoardGameApp.gsFirst == 1) ? piece2Image : piece1Image), 0); /* y, x */
		if (BoardGameScreen.actPlayer == 1) {
			drawSelectionBox(bgt.nbrCol,
					bgt.nbrRow - 1, 0);
		}
    // numbers
    screen.setColor(BaseApp.foreground);
    screen.drawString(infoLines[0], width + vertWidth, off_y + sizey + 2, Graphics.TOP | Graphics.RIGHT);
    screen.drawString(infoLines[1], width + vertWidth, off_y +
				((bgt.nbrRow - 1)  * sizey),
				Graphics.BOTTOM | Graphics.RIGHT);
    // active player
    screen.fillRect((bgt.nbrRow + 1) * sizex - sizex / 2,
				off_y + sizey / 2 + BoardGameScreen.getActPlayer() * (bgt.nbrRow - 1) * sizey, 2, 2);
    // skill
    if (infoLines[2] != null) {
      screen.drawString(infoLines[2], width + vertWidth, screenHeight / 2, Graphics.BASELINE | Graphics.RIGHT);
    }
  }

  protected void drawVertPiece(BoardGameTable bgt, final int row, final int col,
			final int player, Image cupImage, int yadjust) {
		ReversiMove clastMove =
			(ReversiMove)bgt.getLastMove(player - 1);
		drawPiece(bgt, row, col, player, false, cupImage, yadjust,
				((clastMove == null) ? 0 : (clastMove.valid(bgt, clastMove) ? 0 : 1))); /* y, x */
	}

  public void nextTurn(final int row, final int col) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("nextTurn row,col,BoardGameScreen.actPlayer,gameEnded,(mtt != null)=" + row + "," + col + "," + BoardGameScreen.actPlayer + "," + gameEnded + "," + (mtt != null));}
//@		if (finestLoggable) {tableDraws = 2;}
		//#endif
    if (mtt != null) {
      mtt.cancel();
      while (mtt.ended == false) {
        synchronized (this) {
          try {
            wait(50);
          }
          catch (final Exception e) {
            //
          }
        }
      }
    }
    if (gameEnded) { return; }
    final ReversiMove move = new ReversiMove(row, col);
    boolean validMove = processMove(move, false);
    updatePossibleMoves();
		if (!validMove) {
			return;
		}
		int numMoves = 0;
		final int maxMoves = BoardGameScreen.table.nbrRow *
			BoardGameScreen.table.nbrCol;
    while (!gameEnded && !isHuman[BoardGameScreen.actPlayer] &&
				(numMoves++ < maxMoves)) {
			if (BoardGameApp.precalculate) {
				mtt = new MinimaxTimerTask();
			}
      ReversiMove computerMove = (ReversiMove)computerTurn(
					new ReversiGame((ReversiGame)BoardGameScreen.rgame,
						(ReversiTable)BoardGameScreen.table), move);
      if (computerMove == null) {
				computerMove = (ReversiMove)((ReversiTable)BoardGameScreen.table).getEmptyMove();
				computerMove.row = ((ReversiTable)BoardGameScreen.table).nbrRow;
				computerMove.col = ((ReversiTable)BoardGameScreen.table).nbrCol;
			} else {
				selx = computerMove.row;
				sely = computerMove.col;
			}
      processMove(computerMove, BoardGameApp.precalculate);
      updatePossibleMoves();
      gMiniMax.clearPrecalculatedMoves();
    }
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("nextTurn end BoardGameScreen.actPlayer,isHuman[BoardGameScreen.actPlayer],numMoves,gameEnded=" + BoardGameScreen.actPlayer + "," + isHuman[BoardGameScreen.actPlayer] + "," + numMoves + "," + gameEnded);}
//@		tableDraws = 2;
		//#endif
  }

	public void procEndGame(byte player) {
		byte secondPlayer = (byte)(1 - BoardGameApp.gsFirst);
		if (player != secondPlayer) {
			BoardGameScreen.rgame.procEndGame(player);
		}
		BoardGameScreen.rgame.procEndGame(secondPlayer);
		super.procEndGame(((ReversiGame)BoardGameScreen.rgame).numFirstPlayer,
			((ReversiGame)BoardGameScreen.rgame).numSecondPlayer, secondPlayer);
	}

  //#ifdef DTEST
//@  public
  //#else
  protected
	//#endif
  boolean processMove(final BoardGameMove pmove, final boolean startForeThinking) {
		final ReversiMove move = (ReversiMove)pmove;
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("processMove 1 move.row,move.col,BoardGameScreen.actPlayer,startForeThinking=" + move.row + "," + move.col + "," + BoardGameScreen.actPlayer + "," + startForeThinking);}
		//#endif
		byte lastPlayer = BoardGameScreen.actPlayer;
		try {
			final ReversiTable newTable = (ReversiTable)((BoardGameTable)BoardGameScreen.table).getEmptyTable();
			tables = BoardGameScreen.rgame.animatedTurn(BoardGameScreen.table, BoardGameScreen.actPlayer, move, newTable);
			boolean goodMove = (tables != null);
			if (!goodMove) {
				setMessage(BaseApp.messages[AppConstants.MSG_INVALIDMOVE], 60);
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("processMove No valid move BoardGameScreen.actPlayer,=isHuman[BoardGameScreen.actPlayer])" + BoardGameScreen.actPlayer + "," + isHuman[BoardGameScreen.actPlayer]);}
				//#endif
				return false;
			}
			else {
				if (startForeThinking) {
					mtt.setStartGame(gMiniMax, (BoardGame)(new ReversiGame(
								(ReversiGame)BoardGameScreen.rgame)),
								tables[tables.length - 1], getActSkill(), getActPlayer());
					timer.schedule(mtt, 0);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("processMove 1b scheduled");}
					//#endif
				}
				/* Show each move on the board separately. */
				synchronized (this) {
					for (int i = 0; i < tables.length; ++i) {
						BoardGameScreen.table = (ReversiTable)tables[i];
						if (i < tables.length - 1) {
							try {
								super.wakeup(3);
								wait(300);
							}
							catch (final InterruptedException e) {
								// do something
							}
						}
					}
				}
				boolean nonPass = false;
				synchronized (this) {
					BoardGameScreen.table = newTable;
				}
				BoardGameScreen.rgame.saveLastTable(BoardGameScreen.table,
						BoardGameScreen.actPlayer, BoardGameScreen.turnNum);
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("processMove 2 BoardGameScreen.actPlayer,goodMove=" + BoardGameScreen.actPlayer + "," + goodMove);}
				//#endif
				while (!nonPass && !gameEnded) {
					BoardGameScreen.rgame.process(newTable, BoardGameScreen.actPlayer);
					if (BoardGameScreen.rgame.isGameEnded()) {
						gameEnded = true;
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("1 loop BoardGameScreen.actPlayer,gameEnded=" + BoardGameScreen.actPlayer + "," + gameEnded);}
						//#endif
						procEndGame((byte)(1 - BoardGameScreen.actPlayer));
					}
					else {
						BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
						BoardGameScreen.turnNum++;
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("2 loop player change BoardGameScreen.actPlayer=" + BoardGameScreen.actPlayer);}
						//#endif
						if (!BoardGameScreen.rgame.hasPossibleMove(BoardGameScreen.table, BoardGameScreen.actPlayer)) {
							String message;
							if (isHuman[BoardGameScreen.actPlayer]) {
								if (BoardGameScreen.twoplayer) {
									message = Reversi.playerNames[BoardGameScreen.actPlayer];
								}
								else {
									message = BaseApp.messages[AppConstants.MSG_HUMAN];
								}
							}
							else {
								message = BaseApp.messages[AppConstants.MSG_COMPUTER];
							}
							setMessage(message + AppConstants.MSG_PASS, 61);
							BoardGameScreen.table.setPassNum(BoardGameScreen.table.getPassNum() + 1);
							// just to be sure
							gMiniMax.clearPrecalculatedMoves();
							final ReversiMove passMove = new ReversiMove(BoardGameScreen.table.nbrRow, BoardGameScreen.table.nbrCol);
							BoardGameScreen.table.setLastMove(BoardGameScreen.actPlayer, passMove);
							// Save pass
							BoardGameScreen.rgame.saveLastTable(BoardGameScreen.table,
									BoardGameScreen.actPlayer, BoardGameScreen.turnNum);
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("processMove has increase pass number possible move BoardGameScreen.table.getPassNum()=" + BoardGameScreen.table.getPassNum());}
							//#endif
						}
						else {
							nonPass = true;
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("processMove nonPass=" + nonPass);}
							//#endif
						}
					}
				}
				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("processMove error", e);
			//#endif
			return (lastPlayer != BoardGameScreen.actPlayer);
		} finally {
			//#ifdef DLOGGING
//@			if (finerLoggable) {logger.finer("processMove return move.row,move.col,BoardGameScreen.actPlayer,startForeThinking,tables.length=" + move.row + "," + move.col + "," + BoardGameScreen.actPlayer + "," + startForeThinking + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));}
//@			tableDraws = 2;
			//#endif
			super.wakeup(3);
			Thread.currentThread().yield();
		}
  }

}
