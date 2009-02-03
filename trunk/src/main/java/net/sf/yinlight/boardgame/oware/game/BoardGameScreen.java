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
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
package net.sf.yinlight.boardgame.oware.game;

import java.util.Timer;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;
import net.sf.yinlight.boardgame.oware.game.BoardGame;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;
import net.sf.yinlight.boardgame.oware.game.ui.MinimaxTimerTask;
import net.sf.yinlight.boardgame.oware.game.OwareMove;
import net.sf.yinlight.boardgame.oware.game.OwareTable;
import net.sf.yinlight.boardgame.oware.game.LimitedMinMax;
import net.sf.yinlight.boardgame.oware.game.OwareMinMax;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Board game screen (game canvas).
	*/
abstract public class BoardGameScreen extends GameScreen implements Runnable {

  protected static final byte STORE_VERS = 2;
  protected static final byte SCREEN_STORE_BYTES = 6;
  protected static final char NL = '\n';
  protected static final int COLOR_TEXT_BG = 0xEEEEEE;
  protected static final int COLOR_TEXT_FG = 0x000000;
  protected static final int COLOR_BG = 0xFFFFD0;
  protected static final int COLOR_FG = 0x000000;
  protected static final int COLOR_P1 = 0xFF0000;
  protected static final int COLOR_P2 = 0x0000FF;
  protected static final String SEP = ": ";
  protected static final int COLOR_DARKBOX = 0x000000;
  protected static final int ASPECT_LIMIT_A = 400; // 1.5
  protected static final int ASPECT_LIMIT_B = 300; // 1.5
  protected final String infoLines[] = new String[Math.abs(BoardGameApp.gsNbrPlayers) + 1];
  protected String message = null;
  protected Image piece1Image = null;
	protected Image turnImage;
  protected BoardGameMove[] possibleMoves;
  final protected FeatureMgr featureMgr;
  protected int sizex;
  protected int sizey;
  protected int vertWidth;
  protected int width;
  protected int height;
  public int selx;
  public int sely;
  public static final int INVALID_KEY_CODE = -2000;
  public int keyCode = INVALID_KEY_CODE;
  public int[] pointerPress = new int[] {-1, -1};
  protected boolean preCalculateMoves = false;

  public long messageEnd;

  protected MinimaxTimerTask mtt;
  protected final Timer timer = new Timer();

  public static byte actPlayer;
  public boolean gameEnded = true;
  public boolean[] isHuman = new boolean[2];
  public GameTable[] tables;
  public static int turnNum;
  public static BoardGameTable table;
	protected GameMinMax gMiniMax;
  public static BoardGame rgame;
  public static boolean twoplayer;

  protected int fontHeight;
  protected int off_y;
  protected int off_x;
  protected int pieceWidth;
  protected int pieceHeight;
  protected int cupWidth;
  protected int cupHeight;
  protected int cupImagexOffset;
  protected int piece_offx;
  protected int piece_offy;
  public byte[] bsavedRec = new byte[0];

  //#ifdef DTEST
//@	protected boolean debug = false;
  //#endif
  //#ifdef DLOGGING
//@  protected Logger logger = Logger.getLogger("BoardGameScreen");
//@  protected boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  protected boolean finerLoggable = logger.isLoggable(Level.FINER);
//@  protected boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@  protected int tableDraws = 0;
//@  protected boolean drawItems = false;
  //#endif

  public BoardGameScreen(final GameApp midlet, final boolean suppressKeys,
			final boolean fullScreen, int appName) {
		/* Do not suppress keys.  However, do full screen. */
    super(midlet, false, true);
    name = BaseApp.messages[appName];
		featureMgr = new FeatureMgr(this);
		//#ifdef DMIDP10
//@		featureMgr.setCommandListener(GameApp.midlet, false);
//@		super.setCommandListener(featureMgr);
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
//@		if (finestLoggable) {logger.finest("initGraphics cscreen=" + cscreen);}
		//#endif
		//#ifdef DMIDP10
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics simage,simage.getGraphics(),BoardGameApp.gsCol,BoardGameApp.gsRow=" + simage + "," + simage.getGraphics() + "," + BoardGameApp.gsCol + "," + BoardGameApp.gsRow);}
		//#endif
		//#endif
		// FIX turnImage = BaseApp.createImage(TURN_ICON);
		/* Leave space to the right of the board for putting other info. */
		int bcol = Math.abs(BoardGameApp.gsCol);
		int brow = Math.abs(BoardGameApp.gsRow);
    width = screenWidth * bcol / (bcol + 1);
    vertWidth = screenWidth - width;
    height = screenHeight;
    sizex = (width - 1) / Math.abs(bcol);
    sizey = (height - 1) / Math.abs(brow);
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics  width,sizex,vertWidth,sizey" + width + "," + sizex + "," + vertWidth + "," + screenHeight + "," + sizey);}
		//#endif
    final int origSizey = sizey;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics sizex,sizey=" + sizex + "," + sizey);}
		//#endif
    if (BoardGameScreen.ASPECT_LIMIT_B * sizex > BoardGameScreen.ASPECT_LIMIT_A * sizey) {
      sizex = sizey * BoardGameScreen.ASPECT_LIMIT_A / BoardGameScreen.ASPECT_LIMIT_B;
    }
    if (BoardGameScreen.ASPECT_LIMIT_B * sizey > BoardGameScreen.ASPECT_LIMIT_A * sizex) {
      sizey = sizex * BoardGameScreen.ASPECT_LIMIT_A / BoardGameScreen.ASPECT_LIMIT_B;
    }
    fontHeight = cscreen.getFont().getHeight();
    pieceWidth = 20 * sizex / 30;
    pieceHeight = 20 * sizey / 30;
		if ((BoardGameApp.gsTextRow > 0) &&
			(pieceHeight < ((BoardGameApp.gsTextRow * fontHeight + 2) + pieceWidth))) {
			int newSizey = ((BoardGameApp.gsTextRow * (fontHeight + 2)) + pieceWidth ) * 30 / 20;
			if (newSizey < origSizey) {
				sizey = newSizey;
				pieceHeight = 20 * sizey / 30;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("initGraphics origSizey,newSizey,sizey,pieceWidth,pieceHeight=" + origSizey + "," + newSizey + "," + sizey + "," + pieceWidth + "," + pieceHeight);}
			//#endif
		}
    height = sizey * Math.abs(brow);
    width = sizex * Math.abs(bcol);
    piece_offx = (sizex - pieceWidth) / 2;
    piece_offy = (sizey - pieceHeight) / 2;
    cupWidth = pieceWidth;
    cupHeight = pieceHeight;
		if (BoardGameApp.gsTextRow > 0) {
			cupHeight = pieceHeight - (BoardGameApp.gsTextRow * (fontHeight - 1));
		}
    if (cupWidth > cupHeight) {
			cupWidth = cupHeight;
		} else {
			cupHeight = cupWidth;
		}
		if (GameApp.graphics) {
			int ix = BoardGameApp.gsPieceImages.length - 1;
			while (ix >= 0) {
				piece1Image = BaseApp.createImage(BoardGameApp.gsPieceImages[ix--]);
				if ((piece1Image != null) && (piece1Image.getWidth() < cupWidth)) {
					break;
				}
			}
		}
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor piece1Image piece1Image.getWidth(),piece1Image.getHeight()=" + ((piece1Image == null) ? "piece1Image is null" : piece1Image.getWidth() + "," + piece1Image.getHeight()));}
		//#endif
		//undo cup2Image = null; // undo BaseApp.createImage(CUP2);
		turnImage = piece1Image;
		if (piece1Image != null) {
			int imageWidth = piece1Image.getWidth();
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("imageWidth=" + imageWidth);}
			//#endif
			if (imageWidth < cupWidth) {
				cupImagexOffset = (cupWidth - imageWidth) / 2;
			} else {
				piece1Image = null;
				cupImagexOffset = 0;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("initGraphics piece1Image piece1Image.getWidth(),piece1Image.getHeight()=" + ((piece1Image == null) ? "piece1Image is null" : piece1Image.getWidth() + "," + piece1Image.getHeight()));}
			//#endif
			// FIX for different widths for cup and turn image
			//#ifdef DLOGGING
//@			//if (finestLoggable) {logger.finest("turnImage.getWidth()=" + turnImage.getWidth());}
			//#endif
			/*
			if (turnImage.getWidth() > cupWidth) {
				turnImage = null;
			}
			*/
		}
		selx = 0;
		sely = 0;
		off_y = (screenHeight - height) / 2;
		off_x = 2;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics screenWidth,screenHeight,width,vertWidth,height,sizex,off_x,sizey,off_y,pieceWidth,pieceHeight,cupWidth,cupHeight,fontHeight=" + screenWidth + "," + screenHeight + "," + width + "," + vertWidth + "," + height + "," + sizex + "," + sizey + "," + pieceWidth + "," + pieceHeight + "," + cupWidth + "," + cupHeight + "," + fontHeight);}
		//#endif
		return cscreen;
  }

  /**
   * This does init.  Must create new table before calling this from
	 * screen.
   */
  public void init() {
    super.init();
		try {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("init BoardGameApp.gsRow,BoardGameApp.gsCol,BoardGameApp.gsNbrPlayers=" + BoardGameApp.gsRow + "," + BoardGameApp.gsCol + "," + BoardGameApp.gsNbrPlayers);}
			//#endif
			BaseApp.background = 0x00FFFFFF;
			BaseApp.foreground = 0x00000000;
			score.beginGame(1, 0, 0);
			// FIX allow to set
			int rtn = 0;
			if (bsavedRec.length > 0) {
				rtn = loadRecordStore(bsavedRec);
			}
			if (rtn == 0) {
				BoardGameScreen.turnNum = 1;
				gameEnded = false;
			}
			BoardGameScreen.actPlayer = (byte)BoardGameApp.gsFirst;
			if (BoardGameApp.gsPlayer == 1) {
				isHuman[BoardGameScreen.actPlayer] = true;
				isHuman[1 - BoardGameScreen.actPlayer] = false;
				BoardGameScreen.twoplayer = false;
			}
			else {
				isHuman[0] = true;
				isHuman[1] = true;
				BoardGameScreen.twoplayer = true;
			}
			BoardGameScreen.rgame.process(BoardGameScreen.table, 
					BoardGameScreen.actPlayer);
			BoardGameScreen.rgame.resetEvalNum();
			BoardGameScreen.rgame.saveLastTable(BoardGameScreen.table,
					(byte)(1 - BoardGameScreen.actPlayer), BoardGameScreen.turnNum);
			updateSkillInfo();
			updatePossibleMoves();
			setMessage(BaseApp.messages[BoardGameApp.MSG_GOODLUCK]);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("isHuman[0],isHuman[1]=" + isHuman[0] + "," + isHuman[1]);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("init error", e);
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
//@			logger.severe("tick error", e);
			//#endif
			return true;
		}
  }

	//#ifdef DMIDP10
//@	/*
//@	 * Paint game
//@	 * 
//@	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
//@	 */
//@	public void paint(Graphics g) {
//@		try {
//@			g.drawImage(simage, 0, 0, (Graphics.TOP | Graphics.LEFT));
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("paint error", e);
			//#endif
//@		} finally {
//@			animationThread.notifyPainted();
//@		}
//@	}
	//#endif

  /**
   * Executed when hidden.  Stop animation thread, turn off full screen.
   */
  public void hide() {
		try {
			byte[] gameRec = saveRecordStore();
			if (gameRec != null) {
				RecordStore gstore = BaseApp.getRecordStore(BoardGameApp.storeName, true, true);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write(gameRec, 0, gameRec.length);
				BaseApp.writeData(gstore, bos);
				BaseApp.closeRecordStores();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("hide save parameters error", e);
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
    screen.setColor(BoardGameScreen.COLOR_TEXT_BG);
    screen.fillRect(cornerX - 1, cornerY - 1, maxWidth, (breaks + 1) * fontHeight + 6);
    screen.setColor(BoardGameScreen.COLOR_TEXT_FG);
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
    screen.setColor(BoardGameScreen.COLOR_BG);
    screen.fillRect(off_x, off_y, width, height);
    screen.setColor(BoardGameScreen.COLOR_FG);
	/* Draw horizontal lines of rows. */
    for (int i = 0; i <= table.nbrRow; ++i) {
      screen.drawLine(off_x, off_y + i * sizey, off_x + width, off_y + i * sizey);
	}
	/* Draw vertical lines of cols. */
    for (int i = 0; i <= table.nbrCol; ++i) {
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
  abstract protected void drawPiece(final int row, final int col, final int player,
			boolean onBoard, Image cupImage, int lastMovePoint);

  protected void drawPossibleMoves() {
		try {
			BoardGameMove[] cpossibleMoves = null;
			synchronized(this) {
				cpossibleMoves = possibleMoves;
			}
			if (cpossibleMoves == null) {
				// end of the game
				return;
			}
			int x;
			int y;
			screen.setColor(BoardGameScreen.COLOR_DARKBOX);
			for (int i = 0; i < cpossibleMoves.length; ++i) {
				x = off_x + cpossibleMoves[i].col * sizex + sizex / 2;
				y = off_y + cpossibleMoves[i].row * sizey + sizey / 2;
				screen.fillRect(x, y, 2, 2);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("drawPossibleMoves error", e);
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

  abstract protected void drawTable();

  public void drawVertInfo() {
		try {
			// two pieces
			//undo
			drawPiece(-1, table.nbrCol - 1, 1, false,
					((BoardGameScreen.actPlayer == 0) ? piece1Image : null), 0); /* y, x */
			drawPiece(table.nbrRow, table.nbrCol - 1, 0, false,
					((BoardGameScreen.actPlayer == 1) ? piece1Image : null), 0); /* y, x */
			// numbers
			screen.setColor(BaseApp.foreground);
			screen.drawString(infoLines[0],
					width + vertWidth, off_y + cupHeight + 1 + piece_offy,
					Graphics.TOP | Graphics.RIGHT);
			screen.drawString(infoLines[1], width + vertWidth,
					off_y + ((table.nbrRow - 1) * sizey) + cupHeight + 1 + piece_offy,
					Graphics.BOTTOM | Graphics.RIGHT);
			// active player screen.
			// FIX if height problem as we could put the image in this square
			if (turnImage == null) {
				screen.drawRect(width + vertWidth - sizex,
						off_y + BoardGameScreen.getActPlayer() * ((table.nbrRow - 1) * sizey), sizex, sizey);
			}
			// skill
			// Put at middle of height.
			if (infoLines[2] != null) { screen.drawString(infoLines[2], width + vertWidth, screenHeight / 2, Graphics.BASELINE | Graphics.RIGHT); }
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("drawVertInfo error", e);
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

  /**
   * This is just a quick procedure to prevent a hang due to something
	 * taking too long.
	 *
   * @param keyCode
   */
  public void pointerPressed(final int x, final int y) {
		synchronized(this) {
			if ((this.pointerPress[0] == -1) && (this.pointerPress[1] == -1)) {
				this.pointerPress[0] = x;
				this.pointerPress[1] = y;
				featureMgr.wakeup(3);
			}
		}
	}

  /**
   * This is just a quick procedure to prevent a hang due to something
	 * taking too long.
	 *
   * @param keyCode
   */
  public void pointerDragged(final int x, final int y) {
		pointerPressed(x, y);
	}

  public void procKeyPressed(final int keyCode) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("procKeyPressed keyCode=" + keyCode);}
		//#endif
		try {
			if (gameEnded) {
				midlet.doGameStop();
			}
			else {
				switch (super.getGameAction(keyCode)) {
					case Canvas.UP:
						sely = (sely + table.nbrRow - 1) % table.nbrRow;
						setMessage(null);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("procKeyPressed up selx,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
					//#endif
						break;
					case Canvas.DOWN:
						sely = (sely + 1) % table.nbrRow;
						setMessage(null);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("procKeyPressed down selx,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
					//#endif
						break;
					case Canvas.LEFT:
						selx = (selx + table.nbrCol - 1) % table.nbrCol;
						setMessage(null);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("procKeyPressed left selx,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
					//#endif
						break;
					case Canvas.RIGHT:
						selx = (selx + 1) % table.nbrCol;
						setMessage(null);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("procKeyPressed right selx,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
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
//@							if (finestLoggable) {logger.finest("procKeyPressed fire msg on selx,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
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
//@			logger.severe("procKeyPressed error", e);
			//#endif
		} finally {
				super.wakeup(3);
		}
  }

  public void procPointerPressed(final int x, final int y) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("procPointerPressed x,y=" + x + "," + y);}
		//#endif
		try {
			if (gameEnded) {
				midlet.doGameStop();
			}
			else {
				if ((off_x < x) && (x < ((table.nbrCol * sizex) - off_x)) &&
				    (off_y < y) && (y < ((table.nbrRow * sizex) - off_y))) {
						selx = (selx  - off_x) / table.nbrCol;
						sely = (sely  - off_y) / table.nbrRow;
						setMessage(null);
						super.wakeup(3);
				}
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("procPointerPressed x,off_x,selx,y,off_y,sely,BoardGameScreen.actPlayer=" + selx + "," + sely + "," + BoardGameScreen.actPlayer);}
				//#endif
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("procPointerPressed error", e);
			//#endif
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
			synchronized(this) {
				this.keyCode = INVALID_KEY_CODE;
			}
		}
		int cx = -1;
		int cy = -1;
		synchronized(this) {
			if ((this.pointerPress[0] != -1) && (this.pointerPress[1] != -1)) {
				cx = this.pointerPress[0];
				cy = this.pointerPress[1];
			}
		}
		if ((cx != -1) && (cy != -1)) {
			procPointerPressed(cx, cy);
			synchronized(this) {
				this.pointerPress[0] = -1;
				this.pointerPress[1] = -1;
			}
		}
	}

	public void setMessage(final String message) {
		synchronized (this) {
			this.message = message;
		}
		messageEnd = 0;
		//#ifdef DMIDP10
//@		super.wakeup(3);
		//#endif
	}

  public void setMessage(final String message, final int delay) {
		synchronized (this) {
			this.message = message;
		}
    messageEnd = System.currentTimeMillis() + delay * 1000;
  }

  public void updatePossibleMoves() {
		BoardGameMove[] cpossibleMoves =
			(BoardGameMove[]) BoardGameScreen.rgame.possibleMoves(BoardGameScreen.table, BoardGameScreen.actPlayer);
		synchronized(this) {
			possibleMoves = cpossibleMoves;
		}
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("updatePossibleMoves BoardGameScreen.actPlayer,cpossibleMoves=" + BoardGameScreen.actPlayer + "," + cpossibleMoves);}
		//#endif
  }

  public void updateSkillInfo() {
    if (!BoardGameScreen.twoplayer) {
      infoLines[2] = BaseApp.messages[BoardGameApp.MSG_LEVELPREFIX + BoardGameApp.gsLevel] + BoardGameApp.gsDept;
    }
    else {
      infoLines[2] = null;
    }
  }

  protected BoardGameMove computerTurn(final BoardGameMove prevMove) {
    BoardGameMove move = (BoardGameMove) GameMinMax.precalculatedBestMove(prevMove);
    if (move == null) {
      setMessage(BaseApp.messages[BoardGameApp.MSG_THINKING]);
      GameMinMax.cancel(false);
			//#ifdef DLOGGING
//@			if (finerLoggable) {logger.finer("computerTurn 1 prevMove.row,prevMove.col, move.row,move.col,BoardGameScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + BoardGameScreen.actPlayer);}
			//#endif
      move = (BoardGameMove) gMiniMax.minimax(BoardGameScreen.getActSkill(), BoardGameScreen.table, BoardGameScreen.actPlayer, BoardGameScreen.rgame, true, 0, true, true, null);
    }
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("2 computerTurn prevMove.row,prevMove.col, move.row,move.col,BoardGameScreen.actPlayer=" + prevMove.row + "," + prevMove.col + "," + ((move == null) ? "move null" : move.row + "," + move.col) + "," + BoardGameScreen.actPlayer);}
		//#endif
    setMessage(null);
    BoardGameScreen.rgame.resetEvalNum();
    return move;
  }

  public static byte getActPlayer() {
    return BoardGameScreen.actPlayer;
  }

  public static int getActSkill() {
    int actSkill = BoardGameApp.gsDept;
    if (BoardGameScreen.turnNum > 50) {
      actSkill++;
    }
    if (BoardGameScreen.turnNum > 55) {
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
//@		if (finestLoggable) {logger.finest("nextTurn row,col,BoardGameScreen.actPlayer,gameEnded,(mtt != null)=" + row + "," + col + "," + BoardGameScreen.actPlayer + "," + gameEnded + "," + (mtt != null));}
		//#endif
    if (gameEnded) { return; }
    final BoardGameMove move = table.getBoardGameMove(row, col);
    if (!processMove(move, false)) {
			return;
		}
    updatePossibleMoves();
		//#ifdef DMIDP10
//@		super.wakeup(3);
		//#endif
    while (!gameEnded && !isHuman[BoardGameScreen.actPlayer]) {
      mtt = new MinimaxTimerTask();
      final BoardGameMove computerMove = computerTurn(move);
			//#ifdef DLOGGING
//@			if (finerLoggable) {logger.finer("nextTurn computerMove.row,computerMove.col,BoardGameScreen.actPlayer=" + ((computerMove == null) ? "computerMoves null" : (computerMove.row + "," + computerMove.col)) + "," + BoardGameScreen.actPlayer);}
			//#endif
      if (computerMove == null) {
				break;
			}
      selx = computerMove.col;
      sely = computerMove.row;
      processMove(computerMove, preCalculateMoves);
      updatePossibleMoves();
			//#ifdef DMIDP10
//@			super.wakeup(3);
			//#endif
      GameMinMax.clearPrecalculatedMoves();
			break;
    }
		//#ifdef DLOGGING
//@		if (finerLoggable) {logger.finer("nextTurn end loop gameEnded,isHuman[BoardGameScreen.actPlayer],BoardGameScreen.actPlayer=" + gameEnded + "," + isHuman[BoardGameScreen.actPlayer] + "," + BoardGameScreen.actPlayer);}
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
//@  abstract public
  //#else
  abstract protected
	//#endif
		boolean processMove(final BoardGameMove move, final boolean startForeThinking);

	abstract public void procEndGame();

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
    b[index++] = BoardGameScreen.actPlayer;
    b[index++] = (byte) BoardGameScreen.table.nbrRow;
    // row
    b[index++] = (byte) BoardGameScreen.table.nbrCol;
    // col
    b[index++] = (byte) BoardGameApp.gsLevel;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("BoardGameApp.gsLevel,offset=" + BoardGameApp.gsLevel + "," + offset);}
		//#endif
		b[index++] = (byte) BoardGameApp.gsDept;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("BoardGameApp.gsDept,offset=" + BoardGameApp.gsDept + "," + offset);}
		//#endif
    return index - offset;
  }

  /**
   * Saves data into byte[]
   */
  public byte[] saveRecordStore()
	throws Exception {
		try {
			if (BoardGameScreen.table == null) {
				return null;
			}
			final byte[] result = new byte[SCREEN_STORE_BYTES];
			int offset = 0;
			result[offset++] = STORE_VERS;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore STORE_VERS,offset=" + STORE_VERS + "," + offset);}
			//#endif
			result[offset++] = (byte) selx;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore selx,offset=" + selx + "," + offset);}
			//#endif
			result[offset++] = (byte) sely;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore selx,offset=" + sely + "," + offset);}
			//#endif
			result[offset++] = (byte) (gameEnded ? 1 : 0);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore gameEnded,offset=" + gameEnded + "," + offset);}
			//#endif
			// turnNum
			result[offset++] = (byte)((BoardGameScreen.turnNum & 0xFF00) >> 16);
			result[offset++] = (byte)(BoardGameScreen.turnNum & 0x00FF);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore turnNum,offset=" + turnNum + "," + offset);}
			//#endif
			if (result.length != offset) {
				Exception e = new Exception("saveRecordStore Size of result does not match result.length != offset " + result.length + "!=" + offset);
				//#ifdef DLOGGING
//@				logger.severe(e.getMessage(), e);
				//#endif
				e.printStackTrace();
				throw e;
			}
			byte[] byteArray = BoardGameScreen.table.toByteArray();
			byte[] nresult = new byte[byteArray.length + result.length];
			System.arraycopy(result, 0, nresult, 0, result.length);
			System.arraycopy(byteArray, 0, nresult, result.length, byteArray.length);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("saveRecordStore saveRecordStore result.length,byteArray.length,nresult.length=" + result.length + "," + byteArray.length + "," + nresult.length);}
			//#endif
			return nresult;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("saveRecordStore error", e);
			//#endif
			return new byte[0];
		}
  }

  /**
   * Loads data from byte[]
   */
  public int loadRecordStore(final byte[] b) {
		try {
			int offset = 0;
			int vers = b[offset++];
			if (vers != STORE_VERS) {
				//#ifdef DLOGGING
//@				logger.severe("loadRecordStore vers not current=" + vers + "," + STORE_VERS);
				//#endif
				return 0;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("loadRecordStore vers,offset=" + vers + "," + offset);}
			//#endif
			selx = b[offset++];
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("loadRecordStore selx,offset=" + selx + "," + offset);}
			//#endif
			sely = b[offset++];
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("loadRecordStore sely,offset=" + sely + "," + offset);}
			//#endif
			gameEnded = (b[offset++] == 1) ? true : false;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("loadRecordStore gameEnded,offset=" + gameEnded + "," + offset);}
			//#endif
			BoardGameScreen.turnNum = (b[offset++] << 16) + b[offset++];
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("BoardGameScreen.turnNum=" + BoardGameScreen.turnNum);}
			//#endif
			if (SCREEN_STORE_BYTES != offset) {
				Exception e = new Exception("Size of result does not match " + SCREEN_STORE_BYTES + "!=" + offset);
				//#ifdef DLOGGING
//@				logger.severe(e.getMessage(), e);
				//#endif
				e.printStackTrace();
				throw e;
			}
			BoardGameScreen.table = BoardGameScreen.table.getBoardGameTable(b, offset);
			return offset;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("loadRecordStore error", e);
			//#endif
			return 0;
		}
  }

	public byte[] getSavedGameRecord() {
		DataInputStream dis = null;
		try {
				RecordStore gstore = BaseApp.getRecordStore(BoardGameApp.storeName, true, true);
				if (gstore == null) {
					return new byte[0];
				}
				dis = BaseApp.readRecord(gstore, 1);
				if (dis == null) {
					return new byte[0];
				}
				byte [] brec = new byte[BaseApp.getRecordSize(gstore, 1)];
				int len = dis.read(brec, 0, brec.length);
				BaseApp.closeRecordStores();
				if (len == 0) {
					return new byte[0];
				}
				if (brec[0] != STORE_VERS) {
					//#ifdef DLOGGING
//@					logger.severe("getSavedGameRecord error store vers does not match " + brec[0] + "," + STORE_VERS);
					//#endif
					return new byte[0];
				}
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
//@			logger.severe("getSavedGameRecord error", e);
			//#endif
			return null;
			//#ifdef DLOGGING
//@		} finally {
//@			if (finestLoggable) {logger.finest("getSavedGameRecord dis,BoardGameScreen.=" + dis + "," + BoardGameScreen.table);}
			//#endif
		}
	}

	/* Undo last move.  If playing against AI, need to do undo AI move
		 first. */
	public boolean undoTable() {
		synchronized(this) {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("undoTable=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			if (!isHuman[BoardGameScreen.actPlayer]) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("AI undoTable " + BoardGameScreen.actPlayer);}
				//#endif
				if (rgame.undoTable(BoardGameScreen.actPlayer) == null) {
					//#ifdef DLOGGING
//@					logger.severe("AI undoTable null");
					//#endif
					return false;
				}
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("undoTable AI check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
				//#endif
				BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			}
			if (rgame.undoTable(BoardGameScreen.actPlayer) == null) {
				//#ifdef DLOGGING
//@				logger.severe("player undoTable null");
				//#endif
				return false;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("undoTable human check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			BoardGameScreen.table = (BoardGameTable)BoardGameScreen.rgame.getTable();
			updatePossibleMoves();
			return true;
		}
	}

	/* check last move.  If playing against AI, need to do undo AI move
		 first. */
	public boolean checkLastTable() {
		synchronized(this) {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("checkLastTable BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			byte prevPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			byte ix = 0;
			if (!isHuman[prevPlayer]) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("AI checkLastTable prevPlayer=" + prevPlayer);}
				//#endif
				if (!rgame.checkLast(prevPlayer, ix)) {
					//#ifdef DLOGGING
//@					logger.severe("AI checkLast null");
					//#endif
					return false;
				}
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("checkLastTable AI check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
				//#endif
				prevPlayer = (byte) (1 - prevPlayer);
				ix++;
			}
			if (!rgame.checkLast(prevPlayer, ix)) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("checkLastTable human check failed prevPlayer,ix=" + prevPlayer + "," + ix);}
				//#endif
				return false;
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("checkLastTable human check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			return true;
		}
	}

	public boolean redoTable() {
		synchronized(this) {
			if ((rgame.redoTable(BoardGameScreen.actPlayer)) ==
					null) {
				//#ifdef DLOGGING
//@				logger.severe("human redoTable null");
				//#endif
				return false;
			}
			BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			if (!isHuman[BoardGameScreen.actPlayer]) {
				if (rgame.redoTable(BoardGameScreen.actPlayer) == null) {
					//#ifdef DLOGGING
//@					logger.severe("AI redoTable null");
					//#endif
					return false;
				}
				BoardGameScreen.actPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			}
			BoardGameScreen.table = (BoardGameTable)BoardGameScreen.rgame.getTable();
			updatePossibleMoves();
			return true;
		}
	}

	/* check last redo move.  If playing against AI, need to do undo AI move
		 first. */
	public boolean checkLastRedoTable() {
			synchronized(this) {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("checkLastRedoTable=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			byte ix = 0;
			if (!rgame.checkLastRedo(BoardGameScreen.actPlayer, ix)) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("checkLastRedoTable human check failed BoardGameScreen.actPlayer,ix=" + BoardGameScreen.actPlayer + "," + ix);}
				//#endif
				return false;
			}
			byte nextPlayer = (byte) (1 - BoardGameScreen.actPlayer);
			ix++;
			if (!isHuman[nextPlayer]) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("AI undoRedoTable nextPlayer=" + nextPlayer);}
				//#endif
				if (!rgame.checkLastRedo(nextPlayer, ix)) {
					//#ifdef DLOGGING
//@					logger.severe("AI checkLastRedo null");
					//#endif
					return false;
				}
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("undoRedoTable AI check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
				//#endif
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("checkLastRedoTable human check BoardGameScreen.actPlayer,BoardGameScreen.turnNum=" + BoardGameScreen.actPlayer + "," + BoardGameScreen.turnNum);}
			//#endif
			return true;
		}
	}

}
