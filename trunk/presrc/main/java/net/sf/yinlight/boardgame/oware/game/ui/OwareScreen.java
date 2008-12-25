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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
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
import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware game screen (game canvas).
	*/
public final class OwareScreen extends GameScreen
	implements Runnable {

  private static final String OWARE_STORE = "OWARE_GAME";
  private static final byte STORE_VERS = 1;
  private static final byte SCREEN_STORE_BYTES = 4 + 5 + 2;
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
  // UNDO private static final String CUP2 = "icon_18x18.bmp";
  // UNDO private static final String TURN_ICON = "owareicon32x32.png";
  private final String infoLines[] = new String[3];
  private String message = null;
  private OwareMove[] possibleMoves;
  final private FeatureMgr featureMgr;
  private int sizex;
  private int sizey;
  private int vertWidth;
  private int width;
  private int height;
  public int selx;
  public int sely;
  public static final int INVALID_KEY_CODE = -2000;
  public int keyCode = INVALID_KEY_CODE;
  private boolean preCalculateMoves = false;
  private Image cup1Image = null;
  //undo private Image cup2Image;
  // FIX get turn image private
	private Image turnImage;

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

  private int fontHeight;
  private int off_y;
  private int off_x;
  private int pieceWidth;
  private int pieceHeight;
  private int cupWidth;
  private int cupHeight;
  private int cupImagexOffset;
  private int piece_offx;
  private int piece_offy;
  public byte[] bsavedRec = new byte[0];

  //#ifdef DTEST
	private boolean debug = false;
  //#endif
  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("OwareScreen");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finerLoggable = logger.isLoggable(Level.FINER);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private int tableDraws = 0;
  private boolean drawItems = false;
  //#endif

  public OwareScreen(final GameApp midlet) {
		/* Do not suppress keys.  However, do full screen. */
    super(midlet, false, true);
    OwareScreen.rgame = new OwareGame();
		if (GameApp.graphics) {
			cup1Image = BaseApp.createImage(CUP1);
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor cup1Image cup1Image.getWidth(),cup1Image.getHeight()=" + ((cup1Image == null) ? "cup1Image is null" : cup1Image.getWidth() + "," + cup1Image.getHeight()));}
		//#endif
		//undo cup2Image = null; // undo BaseApp.createImage(CUP2);
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
    updateSkillInfo();
		featureMgr = new FeatureMgr(this);
		//#ifdef DMIDP10
		featureMgr.setCommandListener(GameApp.midlet, false);
		super.setCommandListener(featureMgr);
		//#endif
		featureMgr.setRunnable(this, true);
	}

  /**
   * Initialize graphics portion only. This is for MIDP 1.0 devices which
	 * do not get screen info until paint.  So, we allow it to be called from
	 * 2 places.
   */
	public Graphics initGraphics() {
		Graphics cscreen = super.initGraphics();
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("initGraphics cscreen=" + cscreen);}
		//#endif
		//#ifdef DMIDP10
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("initGraphics simage,simage.getGraphics()=" + simage + "," + simage.getGraphics());}
		//#endif
		//#endif
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
    fontHeight = cscreen.getFont().getHeight();
    pieceWidth = 20 * sizex / 30;
    pieceHeight = 20 * sizey / 30;
    if (pieceHeight < ((2 * fontHeight) + pieceWidth + 4)) {
			int newSizey = ((2 * fontHeight) + pieceWidth + 4) * 30 / 20;
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
			cupHeight = cupWidth;
		}
		if (cup1Image != null) {
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
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("constructor screenWidth,screenHeight,width,vertWidth,height,sizex,sizey,pieceWidth,pieceHeight,cupWidth,cupHeight,fontHeight=" + screenWidth + "," + screenHeight + "," + width + "," + vertWidth + "," + height + "," + sizex + "," + sizey + "," + pieceWidth + "," + pieceHeight + "," + cupWidth + "," + cupHeight + "," + fontHeight);}
		//#endif
		return cscreen;
  }

  public void init() {
    super.init();
		try { BaseApp.background = 0x00FFFFFF;
			BaseApp.foreground = 0x00000000;
			screen = initGraphics();
			score.beginGame(1, 0, 0);
			// FIX allow to set
			if (bsavedRec.length > 0) {
				loadRecordStore(bsavedRec);
				bsavedRec = new byte[0];
			} else {
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
				OwareScreen.turnNum = 1;
				OwareScreen.table = new OwareTable();
			}
			updateSkillInfo();
			setMessage(BaseApp.messages[OwareMIDlet.MSG_GOODLUCK]);
			gameEnded = false;
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

	//#ifdef DMIDP10
	/*
	 * Paint game
	 * 
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		try {
			g.drawImage(simage, 0, 0, (Graphics.TOP | Graphics.LEFT));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("paint error", e);
			//#endif
		} finally {
			animationThread.notifyPainted();
		}
	}
	//#endif

  /**
   * Executed when hidden.  Stop animation thread, turn off full screen.
   */
  public void hide() {
		try {
			byte[] gameRec = saveRecordStore();
			if (gameRec != null) {
				RecordStore gstore = BaseApp.getRecordStore(OWARE_STORE, true, true);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write(gameRec, 0, gameRec.length);
				BaseApp.writeData(gstore, bos);
				BaseApp.closeRecordStores();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("hide save parameters error", e);
			//#endif
		}
		super.hide();
	}

  protected void drawMessage() {
		// Avoid synchronization errors.
		String cmessage = null;
		long cmessageEnd;
		synchronized (this) {
			cmessage = message;
			cmessageEnd = messageEnd;
		}
    if ((cmessage == null) || ((cmessageEnd != 0) && (cmessageEnd > System.currentTimeMillis()))) { return; }
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

  protected void drawPossibleMoves() {
		try {
			OwareMove[] cpossibleMoves = null;
			synchronized(this) {
				cpossibleMoves = possibleMoves;
			}
			if (cpossibleMoves == null) {
				// end of the game
				return;
			}
			int x;
			int y;
			screen.setColor(OwareScreen.COLOR_DARKBOX);
			for (int i = 0; i < cpossibleMoves.length; ++i) {
				x = off_x + cpossibleMoves[i].col * sizex + sizex / 2;
				y = off_y + cpossibleMoves[i].row * sizey + sizey / 2;
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

  /**
   * This is just a quick procedure to prevent a hang due to something
	 * taking too long.
	 *
   * @param keyCode
   */
  public void keyPressed(final int keyCode) {
		synchronized(this) {
			if (this.keyCode == INVALID_KEY_CODE) {
				this.keyCode = keyCode;
				featureMgr.wakeup(3);
			}
		}
	}

  public void procKeyPressed(final int keyCode) {
		try {
			if (gameEnded) {
				midlet.doGameStop();
			}
			else {
				switch (super.getGameAction(keyCode)) {
					case Canvas.UP:
						sely = (sely + OwareTable.NBR_ROW - 1) % OwareTable.NBR_ROW;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procKeyPressed up selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.DOWN:
						sely = (sely + 1) % OwareTable.NBR_ROW;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procKeyPressed down selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.LEFT:
						selx = (selx + OwareTable.NBR_COL - 1) % OwareTable.NBR_COL;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procKeyPressed left selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
					//#endif
						break;
					case Canvas.RIGHT:
						selx = (selx + 1) % OwareTable.NBR_COL;
						setMessage(null);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procKeyPressed right selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
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
							if (finestLoggable) {logger.finest("procKeyPressed fire msg on selx,sely,OwareScreen.actPlayer=" + selx + "," + sely + "," + OwareScreen.actPlayer);}
							//#endif
						}
						else {
							nextTurn(sely, selx);
						}
						break;
					default:
						GameMinMax.cancel(true);
						midlet.doGamePause();
						break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("procKeyPressed error", e);
			//#endif
		} finally {
				super.wakeup(3);
		}
  }

	public void run() {
		int ckeyCode = INVALID_KEY_CODE;
		synchronized(this) {
			if (this.keyCode != INVALID_KEY_CODE) {
				ckeyCode = this.keyCode;
			}
		}
		if (ckeyCode != INVALID_KEY_CODE) {
			procKeyPressed(ckeyCode);
		}
		synchronized(this) {
			this.keyCode = INVALID_KEY_CODE;
		}
	}

	public void setMessage(final String message) {
		synchronized (this) {
			this.message = message;
		}
		messageEnd = 0;
		//#ifdef DMIDP10
		super.wakeup(3);
		//#endif
	}

  public void setMessage(final String message, final int delay) {
		synchronized (this) {
			this.message = message;
		}
    messageEnd = System.currentTimeMillis() + delay * 1000;
  }

  public void updatePossibleMoves() {
		OwareMove[] cpossibleMoves =
			(OwareMove[]) OwareScreen.rgame.possibleMoves(OwareScreen.table, OwareScreen.actPlayer);
		synchronized(this) {
			possibleMoves = cpossibleMoves;
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("updatePossibleMoves OwareScreen.actPlayer,cpossibleMoves=" + OwareScreen.actPlayer + "," + cpossibleMoves);}
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
			if (finerLoggable) {logger.finer("computerTurn 1 prevMove.row,prevMove.col, move.row,move.col,OwareScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + OwareScreen.actPlayer);}
			//#endif
      move = (OwareMove) gMiniMax.minimax(OwareScreen.getActSkill(), OwareScreen.table, OwareScreen.actPlayer, OwareScreen.rgame, true, 0, true, true, null);
    }
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("2 computerTurn prevMove.row,prevMove.col, move.row,move.col,OwareScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + OwareScreen.actPlayer);}
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
		if (finestLoggable) {logger.finest("nextTurn row,col,OwareScreen.actPlayer,gameEnded,(mtt != null)=" + row + "," + col + "," + OwareScreen.actPlayer + "," + gameEnded + "," + (mtt != null));}
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
    while (!gameEnded && !isHuman[OwareScreen.actPlayer]) {
      mtt = new MinimaxTimerTask();
      final OwareMove computerMove = computerTurn(move);
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("nextTurn computerMove.row,computerMove.col,OwareScreen.actPlayer=" + ((computerMove == null) ? "computerMoves null" : (computerMove.row + "," + computerMove.col)) + "," + OwareScreen.actPlayer);}
			//#endif
      if (computerMove == null) {
				break;
			}
      selx = computerMove.col;
      sely = computerMove.row;
      processMove(computerMove, preCalculateMoves);
      updatePossibleMoves();
			//#ifdef DMIDP10
			super.wakeup(3);
			//#endif
      GameMinMax.clearPrecalculatedMoves();
			break;
    }
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("nextTurn end loop gameEnded,isHuman[OwareScreen.actPlayer],OwareScreen.actPlayer=" + gameEnded + "," + isHuman[OwareScreen.actPlayer] + "," + OwareScreen.actPlayer);}
		//#endif
  }

  /**
   * Process the move.  Change the player to the other player
	 *
   * @param move
   * @param startForeThinking
   * @author Irv Bunton
   */
  protected boolean processMove(final OwareMove move, final boolean startForeThinking) {
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("processMove 1 move.row,move.col,OwareScreen.actPlayer,startForeThinking=" + move.row + "," + move.col + "," + OwareScreen.actPlayer + "," + startForeThinking);}
		//#endif
		try {
			final OwareTable newTable = new OwareTable();
			// Save the table BEFORE we make the current move.
			if (isHuman[OwareScreen.actPlayer]) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("Saving last human move OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
				//#endif
				OwareScreen.table.saveLastTable(OwareScreen.table);
				//#ifdef DLOGGING
			} else {
				if (finestLoggable) {logger.finest("Not saving the AI move OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
				//#endif
			}
			/* Simulate the results of taking the move and put results in newTable. */
			tables = OwareScreen.rgame.animatedTurn(OwareScreen.table, OwareScreen.actPlayer, move, newTable);
			boolean goodMove = (tables != null);
			if (!goodMove) {
				setMessage(BaseApp.messages[OwareMIDlet.MSG_INVALIDMOVE], 2000);
				if (isHuman[OwareScreen.actPlayer]) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("Saving last human move OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
					//#endif
					OwareScreen.table.popLastTable();
					return false;
				}
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
					if (finerLoggable) {logger.finer("1 loop OwareScreen.actPlayer,OwareScreen.rgame.isGameEnded()=" + OwareScreen.actPlayer + "," + OwareScreen.rgame.isGameEnded());}
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
						if (finerLoggable) {logger.finer("2 loop OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
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
			//#ifdef DLOGGING
		} finally {
			if (finerLoggable) {logger.finer("processMove return move.row,move.col,OwareScreen.actPlayer,startForeThinking,tables.length=" + move.row + "," + move.col + "," + OwareScreen.actPlayer + "," + startForeThinking + "," + ((tables == null) ? "tables is null" : String.valueOf(tables.length)));}
			//#endif
			super.wakeup(3);
		}
  }

  public int saveGameParameters(final byte[] b, final int offset) {
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
    // row
    b[index++] = (byte) OwareTable.NBR_ROW;
    // col
    b[index++] = (byte) OwareTable.NBR_COL;
    return index - offset;
  }

  /**
   * Saves data into byte[]
   */
  public byte[] saveRecordStore()
	throws Exception {
    if (OwareScreen.table == null) {
			return null;
		}
    final byte[] result = new byte[OwareScreen.table.tableMaxStoreSize() + SCREEN_STORE_BYTES];
		int offset = 0;
    result[offset++] = STORE_VERS;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("STORE_VERS,offset=" + STORE_VERS + "," + offset);}
		//#endif
    result[offset++] = (byte) OwareMIDlet.gsLevel;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("OwareMIDlet.gsLevel,offset=" + OwareMIDlet.gsLevel + "," + offset);}
		//#endif
		result[offset++] = (byte) OwareMIDlet.gsDept;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("OwareMIDlet.gsDept,offset=" + OwareMIDlet.gsDept + "," + offset);}
		//#endif
		result[offset++] = (byte) selx;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("selx,offset=" + selx + "," + offset);}
		//#endif
		result[offset++] = (byte) sely;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("selx,offset=" + sely + "," + offset);}
		//#endif
    result[offset++] = (byte) (gameEnded ? 0 : 1);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("gameEnded,offset=" + gameEnded + "," + offset);}
		//#endif
    offset += saveGameParameters(result, offset);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("offset=" + offset);}
		//#endif
    OwareScreen.table.toByteArray(result, offset);
    offset += OwareScreen.table.tableStoreSize();
		if (result.length != offset) {
			Exception e = new Exception("Size of result does not match " + result.length + "!=" + offset);
			//#ifdef DLOGGING
			logger.severe(e.getMessage(), e);
			//#endif
			e.printStackTrace();
			throw e;
		}
    return result;
  }

  public int loadGameParameters(final byte[] b, final int offset) {
		try {
			int index = offset;
			isHuman[0] = false;
			isHuman[1] = false;
			if ((b[index] & 1) > 0) {
				isHuman[0] = true;
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("isHuman[0]=" + isHuman[0]);}
				//#endif
			}
			if ((b[index] & 2) > 0) {
				isHuman[1] = true;
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("isHuman[1]=" + isHuman[1]);}
				//#endif
			}
			OwareScreen.twoplayer = isHuman[0] && isHuman[1];
			index++;
			OwareScreen.actPlayer = b[index++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("OwareScreen.actPlayer=" + OwareScreen.actPlayer);}
			//#endif
			OwareScreen.turnNum = b[index++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("OwareScreen.turnNum=" + OwareScreen.turnNum);}
			//#endif
			int row = b[index++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("row=" + row);}
			//#endif
			int col = b[index++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("col=" + col);}
			//#endif
			return index - offset;
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
			int offset = 0;
			int vers = b[offset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore vers,offset=" + vers + "," + offset);}
			//#endif
			OwareMIDlet.gsLevel = b[offset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore OwareMIDlet.gsLevel,offset=" + OwareMIDlet.gsLevel + "," + offset);}
			//#endif
			OwareMIDlet.gsDept = b[offset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore OwareMIDlet.gsDept,offset=" + OwareMIDlet.gsDept + "," + offset);}
			//#endif
			selx = b[offset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore selx,offset=" + selx + "," + offset);}
			//#endif
			sely = b[offset++];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore sely,offset=" + sely + "," + offset);}
			//#endif
			gameEnded = (b[offset++] == 1) ? true : false;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore gameEnded,offset=" + gameEnded + "," + offset);}
			//#endif
			offset += loadGameParameters(b, offset);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("loadRecordStore offset=" + offset);}
			//#endif
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

	public byte[] getSavedGameRecord() {
		DataInputStream dis = null;
		try {
				RecordStore gstore = BaseApp.getRecordStore(OWARE_STORE, true, true);
				if (gstore == null) {
					return new byte[0];
				}
				dis = BaseApp.readRecord(gstore, 1);
				if (dis == null) {
					return new byte[0];
				}
				byte [] brec = new byte[new OwareTable().tableMaxStoreSize() +
					SCREEN_STORE_BYTES];
				int len = dis.read(brec, 0, brec.length);
				BaseApp.closeRecordStores();
				if (len < brec.length) {
					byte [] nbrec = new byte[len];
					System.arraycopy(brec, 0, nbrec, 0, len);
					return nbrec;
				} else {
					return brec;
				}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getSavedGameRecord error", e);
			//#endif
			return null;
			//#ifdef DLOGGING
		} finally {
			if (finestLoggable) {logger.finest("getSavedGameRecord dis,OwareScreen.=" + dis + "," + OwareScreen.table);}
			//#endif
		}
	}
}
