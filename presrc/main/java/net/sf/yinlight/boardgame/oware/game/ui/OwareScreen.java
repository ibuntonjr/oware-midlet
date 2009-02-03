/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was ReversiScreen.java
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
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package net.sf.yinlight.boardgame.oware.game.ui;

import java.util.Timer;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import net.sf.yinlight.boardgame.oware.midlet.OwareMIDlet;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.OwareGame;
import net.sf.yinlight.boardgame.oware.game.OwareMove;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;
import net.sf.yinlight.boardgame.oware.game.OwareTable;
import net.sf.yinlight.boardgame.oware.game.LimitedMinMax;
import net.sf.yinlight.boardgame.oware.game.OwareMinMax;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware game screen (game canvas).
	*/
public final class OwareScreen extends BoardGameScreen {

  private static final String OWARE_STORE = "OWARE_GAME";

  //#ifdef DTEST
	private boolean debug = false;
  //#endif
  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("OwareScreen");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finerLoggable = logger.isLoggable(Level.FINER);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public OwareScreen(final GameApp midlet, final boolean suppressKeys,
			final boolean fullScreen) {
		/* Do not suppress keys.  However, do full screen. */
    super(midlet, false, true, OwareMIDlet.MSG_NAME);
    BoardGameScreen.rgame = new OwareGame();
		// FIX for different AIs and skill
    switch (OwareMIDlet.gsLevel) {
			case OwareMIDlet.gsLevelNormal:
				gMiniMax = new LimitedMinMax();
				break;
			case OwareMIDlet.gsLevelDifficult:
				gMiniMax= new OwareMinMax(0);
				break;
			case OwareMIDlet.gsLevelHard:
			default:
				gMiniMax = new OwareMinMax(1);
				break;
		}
    updateSkillInfo();
	}

  public void init() {
		try {
			BoardGameScreen.table = new OwareTable(Math.abs(BoardGameApp.gsRow),
					Math.abs(BoardGameApp.gsCol), Math.abs(BoardGameApp.gsNbrPlayers),
					Math.abs(OwareMIDlet.gsInitSeeds));
			super.init();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

  public boolean tick() {
		try {
			screen.setColor(BaseApp.background);
			screen.fillRect(0, 0, screenWidth, screenHeight);
			drawBoard();
			drawTable();
			drawSelectionBox();
			drawPossibleMoves();
			drawVertInfo();
			drawMessage();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("tick error", e);
			//#endif
			return true;
		}
  }

  /**
   * Draw a piece on the board at the row/col for the player.  If onBoard
	 * is true, the piece is in the playing area.  If false, it is on the
	 * right to identify the players.
	 *
   * @param row
   * @param col
   * @param player
   * @param onBoard
   */
  protected void drawPiece(final int row, final int col, final int player,
			boolean onBoard, Image cupImage, int lastMovePoint) {
		try {
			OwareTable ot = (OwareTable)BoardGameScreen.table;
			final int x = off_x + col * sizex + piece_offx;
			int y = off_y + row * sizey + piece_offy;
			if (y < 0) {
				y = 0;
			}
			int lastMove = onBoard ? lastMovePoint : (byte)0;
			int seeds = onBoard ? ot.getSeeds(row, col) : 0;
			if (onBoard && (seeds == 0)) {
				// Reverse the square
				screen.setColor(BaseApp.foreground);
				screen.drawRect(x, y, cupWidth, cupHeight);
				screen.fillRect(x, y, cupWidth, cupHeight);
				screen.setColor(BaseApp.background);
				screen.fillArc(x, y, cupWidth, cupHeight, 0, 360);
			} else {
				if (player == OwareMIDlet.gsFirst) {
					screen.setColor(BoardGameScreen.COLOR_P1);
				}
				else {
					screen.setColor(BoardGameScreen.COLOR_P2);
				}
				if (cupImage == null) {
					screen.fillArc(x, y, cupWidth, cupHeight, 0, 360);
				} else {
					screen.drawImage(cupImage, x + cupImagexOffset, y, Graphics.TOP | Graphics.LEFT);
				}
			}
			if (onBoard) {
				screen.setColor(BaseApp.foreground);
				screen.drawString(" " + String.valueOf(
							(int) seeds),
							x, y + cupHeight + 1, Graphics.TOP | Graphics.HCENTER);
				if (lastMove > 0) {
					screen.drawString(" " + String.valueOf(
								(int) -lastMove),
								x, y + cupHeight + 2 + fontHeight, Graphics.TOP | Graphics.HCENTER);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("drawPiece error", e);
			//#endif
		}
  }

  protected void drawSelectionBox() {
    if (BoardGameScreen.getActPlayer() == 0) {
      screen.setColor(BoardGameScreen.COLOR_P1);
    }
    else {
      screen.setColor(BoardGameScreen.COLOR_P2);
    }
    screen.drawRect(off_x + selx * sizex, off_y + sely * sizey, sizex, sizey);
    screen.drawRect(off_x + selx * sizex + 1, off_y + sely * sizey + 1, sizex - 2, sizey - 2);
  }

  protected void drawTable() {
		try {
			OwareTable ot = (OwareTable)BoardGameScreen.table;
			int item;
			for (int i = 0; i < BoardGameScreen.table.nbrRow; ++i) {
				int lastCol = -10;
				int lastRow = -10;
				int lastPoint = 10;
				OwareMove clastMove = (OwareMove)BoardGameScreen.table.getLastMove(i);
				if (clastMove != null) {
					lastRow = clastMove.row;
					lastCol = clastMove.col;
					lastPoint = clastMove.getPoint();
				}
				//#ifdef DLOGGING
				if ((tableDraws-- > 0) && finestLoggable) {drawItems = true;logger.finest("drawTable up i,lastRow,lastCol,lastPoint,BoardGameScreen.actPlayer=" + i + "," + lastRow + "," + lastCol + "," + lastPoint + "," + BoardGameScreen.actPlayer);}
				//#endif
				for (int j = 0; j < BoardGameScreen.table.nbrCol; ++j) {
					item = BoardGameScreen.table.getItem(i, j);
					//#ifdef DTEST
					if (debug) {System.out.println("drawTable item=" + item);}
					//#endif
					if (item != 0) {
						drawPiece(i, j, item, true, piece1Image, ((i == lastRow) &&
									(j == lastCol)) ? lastPoint : 0);
					}
				}
			}
			infoLines[0] = " " + Integer.toString(ot.getPoint((byte)0));
			infoLines[1] = " " + Integer.toString(ot.getPoint((byte)1));
			//#ifdef DLOGGING
			if (drawItems) {
				drawItems = false;
			}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("drawTable error", e);
			//#endif
		}
  }

  public void drawVertInfo() {
		try {
			// two pieces
			//undo
			drawPiece(-1, BoardGameScreen.table.nbrCol - 1, 1, false,
					((BoardGameScreen.actPlayer == 0) ? piece1Image : null), 0); /* y, x */
			drawPiece(BoardGameScreen.table.nbrRow, BoardGameScreen.table.nbrCol - 1, 0, false,
					((BoardGameScreen.actPlayer == 1) ? piece1Image : null), 0); /* y, x */
			// numbers
			screen.setColor(BaseApp.foreground);
			screen.drawString(infoLines[0],
					width + vertWidth, off_y + cupHeight + 1 + piece_offy,
					Graphics.TOP | Graphics.RIGHT);
			screen.drawString(infoLines[1], width + vertWidth,
					off_y + ((BoardGameScreen.table.nbrRow - 1) * sizey) + cupHeight + 1 + piece_offy,
					Graphics.BOTTOM | Graphics.RIGHT);
			// active player screen.
			// FIX if height problem as we could put the image in this square
			if (turnImage == null) {
				screen.drawRect(width + vertWidth - sizex,
						off_y + BoardGameScreen.getActPlayer() * ((BoardGameScreen.table.nbrRow - 1) * sizey), sizex, sizey);
			}
			// skill
			// Put at middle of height.
			if (infoLines[2] != null) { screen.drawString(infoLines[2], width + vertWidth, screenHeight / 2, Graphics.BASELINE | Graphics.RIGHT); }
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("drawVertInfo error", e);
			//#endif
		}
  }

  public void nextTurn(final int row, final int col) {
    if ((mtt != null) && preCalculateMoves) {
      mtt.cancel();
      while (mtt.ended == false) {
        synchronized (this) {
          try {
            wait(50);
          }
          catch (final Exception e) {
            //
						e.printStackTrace();
          }
        }
      }
    }
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("nextTurn row,col,BoardGameScreen.actPlayer,gameEnded,(mtt != null)=" + row + "," + col + "," + BoardGameScreen.actPlayer + "," + gameEnded + "," + (mtt != null));}
		//#endif
    if (gameEnded) { return; }
    final OwareMove move = new OwareMove(row, col);
    if (!processMove(move, false)) {
			return;
		}
    updatePossibleMoves();
		//#ifdef DMIDP10
		super.wakeup(3);
		//#endif
  mtt = new MinimaxTimerTask();
  final OwareMove computerMove = (OwareMove)computerTurn(move);
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("nextTurn computerMove.row,computerMove.col,BoardGameScreen.actPlayer=" + ((computerMove == null) ? "computerMoves null" : (computerMove.row + "," + computerMove.col)) + "," + BoardGameScreen.actPlayer);}
		//#endif
		if (computerMove == null) {
			return;
		}
		selx = computerMove.col;
		sely = computerMove.row;
		processMove(computerMove, preCalculateMoves);
		updatePossibleMoves();
		//#ifdef DMIDP10
		super.wakeup(3);
		//#endif
		GameMinMax.clearPrecalculatedMoves();
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("nextTurn end loop gameEnded,isHuman[BoardGameScreen.actPlayer],BoardGameScreen.actPlayer=" + gameEnded + "," + isHuman[BoardGameScreen.actPlayer] + "," + BoardGameScreen.actPlayer);}
		//#endif
  }

  /**
   * Process the move.  Change the player to the other player
	 *
   * @param move
   * @param startForeThinking
   * @author Irv Bunton
   */
  //#ifdef DTEST
  public
  //#else
  protected
	//#endif
		boolean processMove(final BoardGameMove move, final boolean startForeThinking) {
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("processMove 1 move.row,move.col,BoardGameScreen.actPlayer,startForeThinking=" + move.row + "," + move.col + "," + BoardGameScreen.actPlayer + "," + startForeThinking);}
		//#endif
		try {
			final OwareTable newTable = (OwareTable)table.getEmptyTable();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("Saving last human move BoardGameScreen.actPlayer=" + BoardGameScreen.actPlayer);}
			//#endif
			/* Simulate the results of taking the move and put results in newTable. */
			tables = BoardGameScreen.rgame.animatedTurn(BoardGameScreen.table, BoardGameScreen.actPlayer, move, newTable);
			boolean goodMove = (tables != null);
			if (!goodMove) {
				setMessage(BaseApp.messages[OwareMIDlet.MSG_INVALIDMOVE], 2000);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("No valid move BoardGameScreen.actPlayer,=isHuman[BoardGameScreen.actPlayer])" + BoardGameScreen.actPlayer + "," + isHuman[BoardGameScreen.actPlayer]);}
				//#endif
				return false;
			}
			else {
				if (startForeThinking) {
					mtt.setStartGame(gMiniMax, BoardGameScreen.rgame,
							tables[tables.length - 1], getActSkill(), getActPlayer());
					timer.schedule(mtt, 0);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("processMove 1b scheduled");}
					//#endif
				}

				synchronized (this) {
					for (int i = 0; i < tables.length; ++i) {
						BoardGameScreen.table = (OwareTable) tables[i];
						if (i < tables.length - 1) {
							try {
								wait(300);
							}
							catch (final InterruptedException e) {
								// do something
							}
						}
					}
				}
				boolean nonPass = false;
				/* Make current table the simulated move. */
				BoardGameScreen.table = newTable;
				BoardGameScreen.rgame.saveLastTable(BoardGameScreen.table,
						BoardGameScreen.actPlayer, BoardGameScreen.turnNum);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("processMove 2 BoardGameScreen.actPlayer,goodMove=" + BoardGameScreen.actPlayer + "," + goodMove);}
				//#endif
				while (!nonPass && !gameEnded) {
					/* Process the move. */
					BoardGameScreen.rgame.process(newTable, BoardGameScreen.actPlayer);
					//#ifdef DLOGGING
					if (finerLoggable) {logger.finer("1 loop BoardGameScreen.actPlayer,BoardGameScreen.rgame.isGameEnded()=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.rgame.isGameEnded());}
					//#endif
					if (BoardGameScreen.rgame.isGameEnded()) {
						gameEnded = true;
						procEndGame();
					}
					else {
						/* Change to other player. */
						BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
						BoardGameScreen.turnNum++;
						//#ifdef DLOGGING
						if (finerLoggable) {logger.finer("2 loop BoardGameScreen.actPlayer=" + BoardGameScreen.actPlayer);}
						//#endif
						if (!BoardGameScreen.rgame.hasPossibleMove(BoardGameScreen.table, BoardGameScreen.actPlayer)) {
							String message;
							if (isHuman[BoardGameScreen.actPlayer]) {
								if (BoardGameScreen.twoplayer) {
									message = OwareMIDlet.playerNames[BoardGameScreen.actPlayer];
								}
								else {
									message = BaseApp.messages[OwareMIDlet.MSG_HUMAN];
								}
							}
							else {
								message = BaseApp.messages[OwareMIDlet.MSG_COMPUTER];
							}
							setMessage(message + OwareMIDlet.MSG_PASS, 3000);
							BoardGameScreen.table.setPassNum(BoardGameScreen.table.getPassNum() + 1);
							// just to be sure
							GameMinMax.clearPrecalculatedMoves();
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("processMove has increase pass number possible move BoardGameScreen.table.getPassNum()=" + BoardGameScreen.table.getPassNum());}
							//#endif
							break;
						}
						else {
							nonPass = true;
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("processMove nonPass=" + nonPass);}
							//#endif
						}
					}
				}
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("processMove error", e);
			//#endif
			return false;
		} finally {
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("processMove return move.row,move.col,BoardGameScreen.actPlayer,startForeThinking,tables.length=" + move.row + "," + move.col + "," + BoardGameScreen.actPlayer + "," + startForeThinking + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));}
			tableDraws = 2;
			//#endif
			super.wakeup(3);
		}
  }

	public void procEndGame() {
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("procEndGame");}
		//#endif
		try {
					OwareTable ot = (OwareTable)BoardGameScreen.table;
					BoardGameScreen.rgame.procEndGame();
					final int result = BoardGameScreen.rgame.getGameResult();
					String endMessage;
					final boolean firstWin = ((result == TwoPlayerGame.LOSS) && (BoardGameScreen.actPlayer == 0)) || ((result == TwoPlayerGame.WIN) && (BoardGameScreen.actPlayer == 1));
					final int winner = firstWin ? 1 : 0;
					if (!BoardGameScreen.twoplayer && firstWin) {
						endMessage = BaseApp.messages[OwareMIDlet.MSG_WONCOMPUTER];
					}
					else if (result == TwoPlayerGame.DRAW) {
						endMessage = BaseApp.messages[OwareMIDlet.MSG_DRAW];
					}
					else {
						if (BoardGameScreen.twoplayer) {
							endMessage = OwareMIDlet.playerNames[winner] + BaseApp.messages[OwareMIDlet.MSG_PLAYERWON];
						}
						else {
							endMessage = BaseApp.messages[OwareMIDlet.MSG_HUMANWON];
						}
					}
					final int firstNum = ot.getPoint((byte)0);
					final int secondNum = ot.getPoint((byte)1);
					endMessage += BoardGameScreen.NL + OwareMIDlet.playerNames[0] + BoardGameScreen.SEP + firstNum + BoardGameScreen.NL + OwareMIDlet.playerNames[1] + BoardGameScreen.SEP + secondNum;
					setMessage(endMessage);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("processMove game ended result,firstWin,winner,firstNum,secondNum=" + result + "," + firstWin + "," + winner + firstNum + "," + secondNum);}
					//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("procEndGame error", e);
			//#endif
		}
	}

}
