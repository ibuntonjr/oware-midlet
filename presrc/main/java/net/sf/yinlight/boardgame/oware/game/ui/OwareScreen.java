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
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package net.sf.yinlight.boardgame.oware.game.ui;

import java.util.Timer;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.sf.yinlight.boardgame.oware.midlet.OwareMIDlet;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.OwareGame;
import net.sf.yinlight.boardgame.oware.game.OwareMove;
import net.sf.yinlight.boardgame.oware.game.OwareTable;
import net.sf.yinlight.boardgame.oware.game.LimitedMinMax;
import net.sf.yinlight.boardgame.oware.game.OwareMinMax;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware game screen (game canvas).
	*/
public final class OwareScreen extends GameScreen {

  private static final char NL = '\n';
  private static final String SEP = ": ";
  private static final int COLOR_TEXT_BG = 0xEEEEEE;
  private static final int COLOR_TEXT_FG = 0x000000;
  private static final int COLOR_BG = 0xFFFFD0;
  private static final int COLOR_FG = 0x000000;
  private static final int COLOR_P1 = 0xFF0000;
  private static final int COLOR_P2 = 0x0000FF;
  private static final int COLOR_DARKBOX = 0x000000;
  private static final int ASPECT_LIMIT_A = 400; // 1.5
  private static final int ASPECT_LIMIT_B = 300; // 1.5
  private static final String CUP1 = "icon16.png";
  private static final String CUP2 = "icon_18x18.bmp";
  private static final String TURN_ICON = "owareicon32x32.png";
  private final String infoLines[] = new String[3];
  private String message = null;
  private OwareMove[] possibleMoves;
  private int sizex;
  private int sizey;
  private final int vertWidth;
  private int width;
  private int height;
  public int selx;
  public int sely;
  private Image cup1Image;
  private Image cup2Image;
  // FIX get turn image private
	Image turnImage;

  public long messageEnd;

  private MinimaxTimerTask mtt;
  private final Timer timer = new Timer();

  public static byte actPlayer;
  public boolean gameEnded = true;
  public boolean[] isHuman = new boolean[2];
  public GameTable[] tables;
  public static int turnNum;
  public static OwareTable table;
	protected GameMinMax gMiniMax;
  public static OwareGame rgame;
  public static boolean twoplayer;

  private final int fontHeight;
  private final int off_y;
  private final int off_x;
  private final int pieceWidth;
  private int pieceHeight;
  private int cupWidth;
  private int cupHeight;
  private int cupImagexOffset;
  private final int piece_offx;
  private final int piece_offy;

  //#ifdef DTEST
	private boolean debug = false;
  //#endif
  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("OwareScreen");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private int tableDraws = 0;
  private boolean drawItems = false;
  //#endif

  public OwareScreen(final GameApp midlet) {
		/* Do not suppress keys.  However, do full screen. */
    super(midlet, false, true);
    OwareScreen.rgame = new OwareGame();
		cup1Image = BaseApp.createImage(CUP1);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor cup1Image cup1Image.getWidth(),cup1Image.getHeight()=" + cup1Image.getWidth() + "," + cup1Image.getHeight());}
		//#endif
		cup2Image = null; // undo BaseApp.createImage(CUP2);
		turnImage = cup1Image;
		// FIX turnImage = BaseApp.createImage(TURN_ICON);
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
    name = BaseApp.messages[OwareMIDlet.MSG_NAME];
		/* Leave space to the right of the board for putting other info. */
    width = screenWidth * OwareTable.NBR_COL / (OwareTable.NBR_COL + 1);
    vertWidth = screenWidth - width;
    height = screenHeight;
    sizex = (width - 1) / OwareTable.NBR_COL;
    sizey = (height - 1) / OwareTable.NBR_ROW;
    final int origSizey = sizey;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor sizex,sizey=" + sizex + "," + sizey);}
		//#endif
    if (OwareScreen.ASPECT_LIMIT_B * sizex > OwareScreen.ASPECT_LIMIT_A * sizey) {
      sizex = sizey * OwareScreen.ASPECT_LIMIT_A / OwareScreen.ASPECT_LIMIT_B;
    }
    if (OwareScreen.ASPECT_LIMIT_B * sizey > OwareScreen.ASPECT_LIMIT_A * sizex) {
      sizey = sizex * OwareScreen.ASPECT_LIMIT_A / OwareScreen.ASPECT_LIMIT_B;
    }
    fontHeight = screen.getFont().getHeight();
    pieceWidth = 20 * sizex / 30;
    pieceHeight = 20 * sizey / 30;
    if (pieceHeight < (fontHeight + pieceWidth + 2)) {
			int newSizey = (fontHeight + pieceWidth + 2) * 30 / 20;
			if (newSizey < origSizey) {
				sizey = newSizey;
				pieceHeight = 20 * sizey / 30;
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("constructor origSizey,newSizey,sizey,pieceWidth,pieceHeight=" + origSizey + "," + newSizey + "," + sizey + "," + pieceWidth + "," + pieceHeight);}
			//#endif
		}
    height = sizey * OwareTable.NBR_ROW;
    width = sizex * OwareTable.NBR_COL;
    piece_offx = (sizex - pieceWidth) / 2;
    piece_offy = (sizey - pieceHeight) / 2;
    cupWidth = pieceWidth;
    cupHeight = pieceHeight - fontHeight - 2;
    if (cupWidth > cupHeight) {
			cupWidth = cupHeight;
		} else {
			cupHeight = cupHeight;
		}
		int imageWidth = cup1Image.getWidth();
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("imageWidth=" + imageWidth);}
		//#endif
		if (imageWidth < cupWidth) {
			cupImagexOffset = (cupWidth - imageWidth) / 2;
		} else {
			cup1Image = null;
			cupImagexOffset = 0;
		}
		// FIX for different widths for cup and turn image
		//#ifdef DLOGGING
		//if (finestLoggable) {logger.finest("turnImage.getWidth()=" + turnImage.getWidth());}
		//#endif
		/*
		if (turnImage.getWidth() > cupWidth) {
			turnImage = null;
		}
		*/
    selx = 0;
    sely = 0;
    off_y = (screenHeight - height) / 2;
    off_x = 2;
    updateSkillInfo();
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor screenWidth,screenHeight,width,vertWidth,height,sizex,sizey,pieceWidth,pieceHeight,cupWidth,cupHeight,fontHeight=" + screenWidth + "," + screenHeight + "," + width + "," + vertWidth + "," + height + "," + sizex + "," + sizey + "," + pieceWidth + "," + pieceHeight + "," + cupWidth + "," + cupHeight + "," + fontHeight);}
		//#endif
  }

  public void init() {
    super.init();
		try {
			BaseApp.background = 0x00FFFFFF;
			BaseApp.foreground = 0x00000000;
			score.beginGame(1, 0, 0);
			// FIX allow to set
			OwareScreen.actPlayer = 1;
			if (OwareMIDlet.gsPlayer == 1) {
				isHuman[OwareScreen.actPlayer] = true;
				isHuman[1 - OwareScreen.actPlayer] = false;
				OwareScreen.twoplayer = false;
			}
			else {
				isHuman[0] = true;
				isHuman[1] = true;
				OwareScreen.twoplayer = true;
			}
			updateSkillInfo();
			setMessage(BaseApp.messages[OwareMIDlet.MSG_GOODLUCK]);
			gameEnded = false;
			OwareScreen.turnNum = 1;
			OwareScreen.table = new OwareTable();
			updatePossibleMoves();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("isHuman[0],isHuman[1]=" + isHuman[0] + "," + isHuman[1]);}
			//#endif
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

  protected void drawMessage() {
		// Avoid synchronization errors.
		String cmessage = null;
		synchronized (this) {
			cmessage = message;
		}
    if ((cmessage == null) || ((messageEnd != 0) && (messageEnd > System.currentTimeMillis()))) { return; }
    int startIndex;
    int endIndex = -1;
    final int breaks = BaseApp.lineBreaks(cmessage);
    final int maxWidth = BaseApp.maxSubWidth(screen.getFont(), cmessage) + 10;
    int cornerX = (width - maxWidth) / 2;
    if (cornerX < 0) {
      cornerX = (screenWidth - maxWidth) / 2;
    }
    else {
      cornerX += off_x;
    }
    int cornerY = off_y + (height - (breaks + 1) * fontHeight - 6) / 2;
    screen.setColor(OwareScreen.COLOR_TEXT_BG);
    screen.fillRect(cornerX - 1, cornerY - 1, maxWidth, (breaks + 1) * fontHeight + 6);
    screen.setColor(OwareScreen.COLOR_TEXT_FG);
    screen.drawRect(cornerX - 1, cornerY - 1, maxWidth, (breaks + 1) * fontHeight + 6);
    screen.drawRect(cornerX, cornerY, maxWidth - 2, (breaks + 1) * fontHeight + 4);
    while (endIndex < cmessage.length()) {
      startIndex = endIndex + 1;
      endIndex = cmessage.indexOf(NL, startIndex);
      if (endIndex == -1) {
        endIndex = cmessage.length();
      }
      final String submessage = cmessage.substring(startIndex, endIndex);
      screen.drawString(submessage, cornerX + 5, cornerY + 2, Graphics.TOP | Graphics.LEFT);
      cornerY += fontHeight;
    }
  }

  protected void drawBoard() {
    screen.setColor(OwareScreen.COLOR_BG);
    screen.fillRect(off_x, off_y, width, height);
    screen.setColor(OwareScreen.COLOR_FG);
	/* Draw horizontal lines of rows. */
    for (int i = 0; i <= OwareTable.NBR_ROW; ++i) {
      screen.drawLine(off_x, off_y + i * sizey, off_x + width, off_y + i * sizey);
	}
	/* Draw vertical lines of cols. */
    for (int i = 0; i <= OwareTable.NBR_COL; ++i) {
      screen.drawLine(off_x + i * sizex, off_y, off_x + i * sizex, off_y + height);
    }
  }

  /**
   * Draw a piece on the board at the row/col for the player.  If onBoard
	 * is true, the piece is in the playing area.  If falce, it is on the
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
			final int x = off_x + col * sizex + piece_offx;
			final int y = off_y + row * sizey + piece_offy;
			int lastMove = onBoard ? lastMovePoint : (byte)0;
			int seeds = onBoard ? OwareScreen.table.getSeeds(row, col) : 0;
			if (onBoard && (seeds == 0)) {
				// Reverse the square
				screen.setColor(BaseApp.foreground);
				screen.drawRect(x, y, cupWidth, cupHeight);
				screen.fillRect(x, y, cupWidth, cupHeight);
				screen.setColor(BaseApp.background);
				screen.fillArc(x, y, cupWidth, cupHeight, 0, 360);
			} else {
				if (player == 1) {
					screen.setColor(OwareScreen.COLOR_P1);
				}
				else {
					screen.setColor(OwareScreen.COLOR_P2);
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
							(int) ((seeds == 0) ? -lastMove : seeds)),
							x, y + cupHeight + 1, Graphics.TOP | Graphics.HCENTER);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("drawPiece error", e);
			//#endif
		}
  }

  protected void drawPossibleMoves() {
		try {
			if (possibleMoves == null) {
				// end of the game
				return;
			}
			int x;
			int y;
			screen.setColor(OwareScreen.COLOR_DARKBOX);
			for (int i = 0; i < possibleMoves.length; ++i) {
				x = off_x + possibleMoves[i].col * sizex + sizex / 2;
				y = off_y + possibleMoves[i].row * sizey + sizey / 2;
				screen.fillRect(x, y, 2, 2);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("drawPossibleMoves error", e);
			//#endif
		}
  }

  protected void drawSelectionBox() {
    if (OwareScreen.getActPlayer() == 0) {
      screen.setColor(OwareScreen.COLOR_P1);
    }
    else {
      screen.setColor(OwareScreen.COLOR_P2);
    }
    screen.drawRect(off_x + selx * sizex, off_y + sely * sizey, sizex, sizey);
    screen.drawRect(off_x + selx * sizex + 1, off_y + sely * sizey + 1, sizex - 2, sizey - 2);
  }

  protected void drawTable() {
		try {
			int item;
			for (int i = 0; i < OwareTable.NBR_ROW; ++i) {
				int lastRow = OwareScreen.table.getLastMove(i).row;
				int lastCol = OwareScreen.table.getLastMove(i).col;
				int lastPoint = OwareScreen.table.getLastMove(i).getPoint();
				//#ifdef DLOGGING
				if ((tableDraws-- > 0) && finestLoggable) {drawItems = true;logger.finest("drawTable up i,lastRow,lastCol,lastPoint,OwareScreen.actPlayer=" + i + "," + lastRow + "," + lastCol + "," + lastPoint + "," + OwareScreen.actPlayer);}
				//#endif
				for (int j = 0; j < OwareTable.NBR_COL; ++j) {
					item = OwareScreen.table.getItem(i, j);
					//#ifdef DTEST
					if (debug) {System.out.println("drawTable item=" + item);}
					//#endif
					if (item != 0) {
						drawPiece(i, j, item, true, cup1Image, ((i == lastRow) &&
									(j == lastCol)) ? lastPoint : 0);
					}
				}
			}
			infoLines[0] = " " + Integer.toString(OwareScreen.table.getPoint((byte)0));
			infoLines[1] = " " + Integer.toString(OwareScreen.table.getPoint((byte)1));
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
			drawPiece(-1, OwareTable.NBR_COL - 1, 1, false,
					((OwareScreen.actPlayer == 0) ? cup1Image : null), 0); /* y, x */
			drawPiece(OwareTable.NBR_ROW, OwareTable.NBR_COL - 1, 0, false,
					((OwareScreen.actPlayer == 1) ? cup1Image : null), 0); /* y, x */
			// numbers
			screen.setColor(BaseApp.foreground);
			screen.drawString(infoLines[0],
					width + vertWidth, off_y + cupHeight + 1 + piece_offy,
					Graphics.TOP | Graphics.RIGHT);
			screen.drawString(infoLines[1], width + vertWidth,
					off_y + ((OwareTable.NBR_ROW - 1) * sizey) + cupHeight + 1 + piece_offy,
					Graphics.BOTTOM | Graphics.RIGHT);
			// active player screen.
			// FIX if height problem as we could put the image in this square
			if (turnImage == null) {
				screen.drawRect(width + vertWidth - sizex,
						off_y + OwareScreen.getActPlayer() * ((OwareTable.NBR_ROW - 1) * sizey), sizex, sizey);
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

  public void keyPressed(final int keyCode) {
		try {
			if (gameEnded) {
				midlet.doGameStop();
				super.wakeUp(3);
			}
			else {
				switch (getGameAction(keyCode)) {
					case Canvas.UP:
						sely = (sely + OwareTable.NBR_ROW - 1) % OwareTable.NBR_ROW;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("keyPressed up selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.DOWN:
						sely = (sely + 1) % OwareTable.NBR_ROW;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("keyPressed down selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.LEFT:
						selx = (selx + OwareTable.NBR_COL - 1) % OwareTable.NBR_COL;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("keyPressed left selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.RIGHT:
						selx = (selx + 1) % OwareTable.NBR_COL;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("keyPressed right selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.FIRE:
						String cmessage = null;
						synchronized (this) {
							cmessage = message;
						}
						if (cmessage != null) {
							setMessage(null);
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("keyPressed fire msg on selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
							//#endif
						}
						else {
							nextTurn(sely, selx);
						}
						break;
					default:
						midlet.doGamePause();
						break;
				}
				super.wakeUp(3);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("keyPressed error", e);
			//#endif
		}
  }

  public void setMessage(final String message) {
		synchronized (this) {
			this.message = message;
		}
    messageEnd = 0;
  }

  public void setMessage(final String message, final int delay) {
		synchronized (this) {
			this.message = message;
		}
    messageEnd = System.currentTimeMillis() + delay * 1000;
  }

  public void updatePossibleMoves() {
    possibleMoves = (OwareMove[]) OwareScreen.rgame.possibleMoves(OwareScreen.table, OwareScreen.actPlayer);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("updatePossibleMoves OwareScreen.actPlayer,possibleMoves=" + OwareScreen.actPlayer + "," + possibleMoves);}
		//#endif
  }

  public void updateSkillInfo() {
    if (!OwareScreen.twoplayer) {
      infoLines[2] = BaseApp.messages[OwareMIDlet.MSG_LEVELPREFIX] + OwareMIDlet.gsLevel;
    }
    else {
      infoLines[2] = null;
    }
  }

  protected OwareMove computerTurn(final OwareMove prevMove) {
    OwareMove move = (OwareMove) GameMinMax.precalculatedBestMove(prevMove);
    if (move == null) {
      setMessage(BaseApp.messages[OwareMIDlet.MSG_THINKING]);
      GameMinMax.cancel(false);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("computerTurn 1 prevMove.row,prevMove.col, move.row,move.col,OwareScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + OwareScreen.actPlayer);}
			//#endif
      move = (OwareMove) gMiniMax.minimax(OwareScreen.getActSkill(), OwareScreen.table, OwareScreen.actPlayer, OwareScreen.rgame, true, 0, true, true, null);
    }
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("2 computerTurn prevMove.row,prevMove.col, move.row,move.col,OwareScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + OwareScreen.actPlayer);}
		//#endif
    setMessage(null);
    OwareScreen.rgame.resetEvalNum();
    return move;
  }

  public static byte getActPlayer() {
    return OwareScreen.actPlayer;
  }

  public static int getActSkill() {
    int actSkill = OwareMIDlet.gsDept;
    if (OwareScreen.turnNum > 50) {
      actSkill++;
    }
    if (OwareScreen.turnNum > 55) {
      actSkill++;
    }
    return actSkill;
  }

  public void nextTurn(final int row, final int col) {
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
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("nextTurn row,col,OwareScreen.actPlayer,gameEnded,(mtt != null)=" + row + "," + col + "," + OwareScreen.actPlayer + "," + gameEnded + "," + (mtt != null));}
		//#endif
    if (gameEnded) { return; }
    final OwareMove move = new OwareMove(row, col);
    processMove(move, false);
    updatePossibleMoves();
    while (!gameEnded && !isHuman[OwareScreen.actPlayer]) {
      mtt = new MinimaxTimerTask();
      final OwareMove computerMove = computerTurn(move);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("nextTurn computerMove.row,computerMove.col,OwareScreen.actPlayer=" + ((computerMove == null) ? "computerMoves null" : (computerMove.row + "," + computerMove.col)) + "," + OwareScreen.actPlayer);}
			//#endif
      if (computerMove == null) {
				break;
			}
      selx = computerMove.col;
      sely = computerMove.row;
      processMove(computerMove, true);
      updatePossibleMoves();
      GameMinMax.clearPrecalculatedMoves();
    }
  }

  /**
   * Process the move.  Change the player to the other player
	 *
   * @param move
   * @param startForeThinking
   * @author Irv Bunton
   */
  protected void processMove(final OwareMove move, final boolean startForeThinking) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("processMove 1 move.row,move.col,OwareScreen.actPlayer,startForeThinking=" + move.row + "," + move.col + "," + OwareScreen.actPlayer + "," + startForeThinking);}
			//#endif
		try {
			final OwareTable newTable = new OwareTable();
			/* Simulate the results of taking the move and put results in newTable. */
			tables = OwareScreen.rgame.animatedTurn(OwareScreen.table, OwareScreen.actPlayer, move, newTable);
			boolean goodMove = (tables != null);
			if (!goodMove) {
				setMessage(BaseApp.messages[OwareMIDlet.MSG_INVALIDMOVE], 2000);
			}
			else {
				if (startForeThinking) {
					mtt.setStartTable(gMiniMax, tables[tables.length - 1]);
					timer.schedule(mtt, 0);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("processMove 1b scheduled");}
					//#endif
				}
				synchronized (this) {
					for (int i = 0; i < tables.length; ++i) {
						OwareScreen.table = (OwareTable) tables[i];
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
				OwareScreen.table = newTable;
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("processMove 2 OwareScreen.actPlayer,goodMove=" + OwareScreen.actPlayer + "," + goodMove);}
				//#endif
				while (!nonPass && !gameEnded) {
					/* Process the move. */
					OwareScreen.rgame.process(newTable, OwareScreen.actPlayer);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("1 loop OwareScreen.actPlayer,OwareScreen.rgame.isGameEnded()=" + OwareScreen.actPlayer + "," + OwareScreen.rgame.isGameEnded());}
					//#endif
					if (OwareScreen.rgame.isGameEnded()) {
						final int result = OwareScreen.rgame.getGameResult();
						String endMessage;
						final boolean firstWin = ((result == TwoPlayerGame.LOSS) && (OwareScreen.actPlayer == 0)) || ((result == TwoPlayerGame.WIN) && (OwareScreen.actPlayer == 1));
						final int winner = firstWin ? 1 : 0;
						if (!OwareScreen.twoplayer && firstWin) {
							endMessage = BaseApp.messages[OwareMIDlet.MSG_WONCOMPUTER];
						}
						else if (result == TwoPlayerGame.DRAW) {
							endMessage = BaseApp.messages[OwareMIDlet.MSG_DRAW];
						}
						else {
							if (OwareScreen.twoplayer) {
								endMessage = OwareMIDlet.playerNames[winner] + BaseApp.messages[OwareMIDlet.MSG_PLAYERWON];
							}
							else {
								endMessage = BaseApp.messages[OwareMIDlet.MSG_HUMANWON];
							}
						}
						final int firstNum = OwareScreen.rgame.numFirstPlayer;
						final int secondNum = OwareScreen.rgame.numSecondPlayer;
						endMessage += OwareScreen.NL + OwareMIDlet.playerNames[0] + OwareScreen.SEP + firstNum + OwareScreen.NL + OwareMIDlet.playerNames[1] + OwareScreen.SEP + secondNum;
						setMessage(endMessage);
						gameEnded = true;
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("processMove game ended result,firstWin,winner,firstNum,secondNum=" + result + "," + firstWin + "," + winner + firstNum + "," + secondNum);}
						//#endif
					}
					else {
						/* Change to other player. */
						OwareScreen.actPlayer = (byte) (1 - OwareScreen.actPlayer);
						OwareScreen.turnNum++;
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("2 loop OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
						//#endif
						if (!OwareScreen.rgame.hasPossibleMove(OwareScreen.table, OwareScreen.actPlayer)) {
							String message;
							if (isHuman[OwareScreen.actPlayer]) {
								if (OwareScreen.twoplayer) {
									message = OwareMIDlet.playerNames[OwareScreen.actPlayer];
								}
								else {
									message = BaseApp.messages[OwareMIDlet.MSG_HUMAN];
								}
							}
							else {
								message = BaseApp.messages[OwareMIDlet.MSG_COMPUTER];
							}
							setMessage(message + OwareMIDlet.MSG_PASS, 3000);
							OwareScreen.table.setPassNum(OwareScreen.table.getPassNum() + 1);
							// just to be sure
							GameMinMax.clearPrecalculatedMoves();
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("processMove has increase pass number possible move OwareScreen.table.getPassNum()=" + OwareScreen.table.getPassNum());}
							//#endif
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
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("processMove error", e);
			//#endif
			//#ifdef DLOGGING
		} finally {
			if (finestLoggable) {logger.finest("processMove return move.row,move.col,OwareScreen.actPlayer,startForeThinking,tables.length=" + move.row + "," + move.col + "," + OwareScreen.actPlayer + "," + startForeThinking + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));}
			//#endif
			super.wakeUp(3);
		}
  }

  public void saveGameParameters(final byte[] b, final int offset) {
    int index = offset;
    // isHuman
    b[index] = 0;
    if (isHuman[0]) {
      b[index] |= 1;
    }
    if (isHuman[1]) {
      b[index] |= 2;
    }
    index++;
    // actPlayer
    b[index++] = OwareScreen.actPlayer;
    // turnNum
    b[index++] = (byte) OwareScreen.turnNum;
  }

  /**
   * Saves data into byte[]
   */
  public byte[] saveRecordStore() {
    final byte[] result = new byte[(3 * OwareTable.NBR_COL * OwareTable.NBR_ROW) + (OwareTable.NBR_PLAYERS * (1 + 2)) + 6];
    result[0] = (byte) OwareMIDlet.gsLevel;
    result[1] = (byte) (gameEnded ? 0 : 1);
    saveGameParameters(result, 2);
    OwareScreen.table.toByteArray(result, 5);
    return result;
  }

  public int loadGameParameters(final byte[] b, final int offset) {
		try {
			int index = offset;
			isHuman[0] = false;
			isHuman[1] = false;
			if ((b[index] & 1) > 0) {
				isHuman[0] = true;
			}
			if ((b[index] & 2) > 0) {
				isHuman[1] = true;
			}
			OwareScreen.twoplayer = isHuman[0] && isHuman[1];
			index++;
			OwareScreen.actPlayer = b[index++];
			OwareScreen.turnNum = b[index++];
			return index;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("loadGameParameters error", e);
			//#endif
			return 0;
		}
	}

  /**
   * Loads data from byte[]
   */
  public boolean loadRecordStore(final byte[] b) {
		try {
			// FIX
			if (b.length != 70) { return false; }
			int offset = 0;
			OwareMIDlet.gsLevel = b[offset++];
			OwareMIDlet.gsDept = b[offset++];
			gameEnded = (b[offset++] == 1) ? true : false;
			loadGameParameters(b, offset);
			OwareScreen.table = new OwareTable(b, offset);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("loadRecordStore error", e);
			//#endif
			return false;
		}
  }

}
