/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* FIX Piece image
	* FIX game menu
	* FIX rows/columns
	* TODO do Riversi
	* FIX no getGraphics for GameScreen 1.0 for GameScreen
	* FIX no suppress keys for 1.0 for GameApp
	* FIX take out fromRowString from OwareTable
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was Reversi.java
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
 *
 */
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.sf.yinlight.boardgame.oware.game.OwareGame;
import net.sf.yinlight.boardgame.oware.game.OwareTable;
import net.sf.yinlight.boardgame.oware.game.OwareMove;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import com.substanceofcode.rssreader.presentation.FeatureForm;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import net.eiroca.j2me.rms.Settings;


//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.presentation.LoggerRptForm;
//#endif

/**
	* Oware game application.  Handle game options in addition to standard app
	* options on form.  Save options.
	*/ 
abstract public class BoardGameApp extends GameApp {

  public static String GRAPHICS_PRECALCULATE = "boardgame-precalculate";
  public static boolean precalculate = true;
  public static String storeName = "BOARD_GAME_STORE";
  public static short msgOffset = 0;
  final public static short MSG_MENU_MAIN_UNDO = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_REDO = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_ENDGAME = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_PAUSE = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_TEST_ENDGAME = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_TEST = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_MAIN_LOGGING = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static short MSG_MENU_OPTIONS_DEAULT = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static int MSG_GAMEMODE = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GAMEMODE1 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GAMEMODE2 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_AILEVEL = GameApp.MSG_USERDEF + msgOffset++; //A.I. Difficulty
  final public static int MSG_AILEVEL1 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_AILEVEL2 = GameApp.MSG_USERDEF + msgOffset++;
	// FIX use messages for numbers
  final public static int MSG_AILEVEL3 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_AILEVEL4 = GameApp.MSG_USERDEF + msgOffset++; // 10
  final public static int MSG_SKILL_LEVEL = GameApp.MSG_USERDEF + msgOffset++; // 10
  final public static int MSG_NAMEPLAYER1 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_NAMEPLAYER2 = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_ROW = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_COL = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_NBR_PLAYERS = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GOODLUCK = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_THINKING = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_INVALIDMOVE = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_WONCOMPUTER = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_HUMANWON = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_PLAYERWON = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_DRAW = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_HUMAN = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_COMPUTER = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_PASS = GameApp.MSG_USERDEF + msgOffset++;
  final public static short MSG_SURE_END = (short)(GameApp.MSG_USERDEF + msgOffset++);
  final public static int MSG_LEVELPREFIX = GameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_USERDEF = GameApp.MSG_USERDEF + msgOffset + 2;

  public static int ACTION_OFFSET = 0;
  public static final int GA_UNDO = GameApp.GA_USERDEF + 0;
  public static final int GA_REDO = GameApp.GA_USERDEF + 1;
  public static final int GA_ENDGAME = GameApp.GA_USERDEF + 2;
	//#ifdef DMIDP10
  public static final int GA_PAUSE = GameApp.GA_USERDEF + 3;
	//#endif
	//#ifdef DTEST
  public static final int GA_TEST_ENDGAME = GameApp.GA_USERDEF + 4;
  public static final int GA_TEST = GameApp.GA_USERDEF + 5;
  public static final int GA_PERFORMTEST = GameApp.GA_USERDEF + 6;
	//#endif
	//#ifdef DLOGGING
  public static final int GA_LOGGING = GameApp.GA_USERDEF + 7;
	private Form logf = null;
	//#endif

  public static String[] playerNames;
  private boolean first = true;
  private byte[] bsavedRec = new byte[0];

  protected ChoiceGroup opPlayers;
  protected ChoiceGroup opLevel;
  protected ChoiceGroup opDept = null;
  protected ChoiceGroup opRow = null;
  protected ChoiceGroup opCol = null;
  protected ChoiceGroup opNbrPlayers = null;
	//#ifdef DLOGGING
  protected TextField opLogLevel;
	//#endif
	//#ifdef DTEST
  public static final int MAX_STR_ROW = OwareTable.NBR_COL + 3;
  protected Displayable gameTest = null;
	//#endif

  public static String BOARD_GAME_PLAYER = "board-game-player";
  public static String BOARD_GAME_FIRST = "board-game-first";
  public static String BOARD_GAME_ROW = "board-game-row";
  public static String BOARD_GAME_COL = "board-game-col";
  public static String BOARD_GAME_NBR_PLAYERS = "board-game-nbrplayers";
  public static String BOARD_GAME_TEXT_ROW = "board-game-text-row";
  public static String BOARD_GAME_LEVEL = "board-game-level";
  public static String BOARD_GAME_DEPT = "board-game-dept";
	/* How many human players. */
  public static int gsPlayer = 1;
  public static int gsFirst = 1;
  //undo static public String[] pieceImage = new String[0];
  static public String[] gsSquareImages = new String[0];
  static public String[] gsPiece1Images = new String[0];
  static public String[] gsPiece2Images = new String[0];
  static public int[] gsLevelMsg = new int[0];
  public static int gsDeptInit = 1;
  public static int gsDept = 3;
  public static int gsDeptLimit = 14;
  public static int gsDeptIncr = 2;
  public static int gsRowInit = 2;
  public static int gsRow = 2;
  public static int gsRowLimit = 2;
  public static int gsRowIncr = 2;
  public static int gsColInit = 6;
  public static int gsCol = 6;
  public static int gsColLimit = 6;
  public static int gsColIncr = 1;
  public static int gsNbrPlayersInit = 2;
  public static int gsNbrPlayers = 2;
  public static int gsNbrPlayersLimit = 2;
  public static int gsNbrPlayersIncr = 1;
  public static int gsTextRow = 0;
	/* Skill level. */
  final static public int gsLevelNormal = 0;
  final static public int gsLevelDifficult = 1;
  final static public int gsLevelHard = 2;
  public static int gsLevel = gsLevelDifficult;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	final public LogManager logManager;
	private Logger logger;
	//#endif

	//#ifdef DJMTEST
  public BoardGameApp(String name)
	//#else
  public BoardGameApp()
	//#endif
	{
		//#ifdef DJMTEST
    super(name);
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
		logManager = LogManager.getLogManager();
		logManager.readConfiguration(this);
		logger = Logger.getLogger(logManager, "BoardGameApp", null);
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
    BaseApp.menu = new short[][] {
        {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_CONTINUE, GameApp.GA_CONTINUE, 0
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_NEWGAME, GameApp.GA_NEWGAME, 1
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_UNDO, (short)BoardGameApp.GA_UNDO, 7
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_REDO, (short)BoardGameApp.GA_REDO, 8
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_ENDGAME, (short)BoardGameApp.GA_ENDGAME, -1, MSG_SURE_END
	//#ifdef DTEST
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_TEST_ENDGAME, (short)BoardGameApp.GA_TEST_ENDGAME, -1
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_TEST, (short)BoardGameApp.GA_TEST, 0
		//#endif
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 4
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
				//#ifdef DLOGGING
        }, {
            GameApp.ME_MAINMENU, BoardGameApp.MSG_MENU_MAIN_LOGGING, (short)BoardGameApp.GA_LOGGING, -1
				//#endif
        }
    };
  }

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			// Need to do this before game screen and table are created.
			if (first) {
				loadBoardGameCustomization();
			}
			super.init();
			final String gval = super.getAppProperty(GRAPHICS_PRECALCULATE);
			BoardGameApp.precalculate = ((gval == null) || gval.equals("true"));
			if (first) {
				bsavedRec = ((BoardGameScreen)game).getSavedGameRecord();
			}
			prepGameMenu(bsavedRec.length > 0);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		} finally {
			first = false;
		}
  }

	abstract public void setGameDefaults();

	//#ifdef DMIDP10
  public BoardGameScreen updGameScreen(BoardGameScreen bgs) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("updGameScreen");}
		//#endif
		try {
			Command cPause = BaseApp.newCommand(BoardGameApp.MSG_MENU_MAIN_PAUSE, Command.STOP, 1, BoardGameApp.GA_PAUSE);
			bgs.addCommand(cPause);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("updGameScreen cPause=" + cPause);}
			//#endif
			return bgs;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("updGameScreen error", e);
			//#endif
			return null;
		}
  }
	//#endif

	//#ifdef DMIDP10
  /**
   * Handle the action
   */
  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
		if ((d == GameApp.game) && (action == GA_PAUSE)) {
			doGamePause();
			return true;
		}
    return false;
  }
	//#endif

  protected Displayable getOptions() {
		try {
			final Form form = new FeatureForm(BaseApp.messages[GameApp.MSG_MENU_MAIN_OPTIONS]);
			Command cdefault = new Command(
					BaseApp.messages[BoardGameApp.MSG_MENU_OPTIONS_DEAULT],
					Command.SCREEN, 9);
			form.addCommand(cdefault);
			opPlayers = Application.createChoiceGroup(BoardGameApp.MSG_GAMEMODE,
					Choice.EXCLUSIVE, new int[] {
			BoardGameApp.MSG_GAMEMODE1, BoardGameApp.MSG_GAMEMODE2});
			if (BoardGameApp.gsLevelMsg.length > 0) {
				opLevel = Application.createChoiceGroup(BoardGameApp.MSG_AILEVEL,
						Choice.EXCLUSIVE, BoardGameApp.gsLevelMsg);
			}
			if (BoardGameApp.gsDeptLimit < 0) {
				opDept = Application.createNumRange(BoardGameApp.MSG_SKILL_LEVEL,
						BoardGameApp.gsDeptInit,
						BoardGameApp.gsDeptLimit, BoardGameApp.gsDeptIncr);
			}
			if (BoardGameApp.gsRowLimit < 0) {
				opRow = Application.createNumRange(BoardGameApp.MSG_ROW,
						BoardGameApp.gsRowInit,
						BoardGameApp.gsRowLimit, BoardGameApp.gsRowIncr);
			}
			if (BoardGameApp.gsColLimit < 0) {
				opCol = Application.createNumRange(BoardGameApp.MSG_COL,
						BoardGameApp.gsColInit,
						BoardGameApp.gsColLimit, gsColIncr);
			}
			if (BoardGameApp.gsNbrPlayersLimit < 0) {
				opNbrPlayers = Application.createNumRange(BoardGameApp.MSG_NBR_PLAYERS,
						BoardGameApp.gsNbrPlayers,
						BoardGameApp.gsNbrPlayersLimit, 1);
			}
			//#ifdef DLOGGING
			opLogLevel = new TextField("Logging level",
							logger.getParent().getLevel().getName(), 20, TextField.ANY);
			//#endif
			form.append(opPlayers);
			if (BoardGameApp.gsLevelMsg.length > 0) {
				form.append(opLevel);
			}
			if (BoardGameApp.gsDeptLimit < 0) {
				form.append(opDept);
			}
			if (BoardGameApp.gsRowLimit < 0) {
				form.append(opRow);
			}
			if (BoardGameApp.gsColLimit < 0) {
				form.append(opCol);
			}
			if (BoardGameApp.gsNbrPlayersLimit < 0) {
				form.append(opNbrPlayers);
			}
			//#ifdef DLOGGING
			form.append(opLogLevel);
			//#endif
			BaseApp.setup(form, BaseApp.cBACK, BaseApp.cOK);
			return form;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getOptions error", e);
			//#endif
			return null;
		}
  }

	//#ifdef DTEST
	/*
  protected Displayable getTesting() {
		try {
			final Form form = new FeatureForm(BaseApp.messages[BoardGameApp.MSG_MENU_MAIN_TEST]);
			tstName = new TextField("Test name",
							"", 80, TextField.ANY);
			tstRow0 = new TextField("Input row 0 cups/total",
							"", MAX_STR_ROW, TextField.ANY);
			tstRow1 = new TextField("Input row 1 cups/total",
							"", MAX_STR_ROW, TextField.ANY);
			tstMove = new TextField("Move", "", 2, TextField.ANY);
			tstActRow0 = new TextField("Actual row 0 cups/total",
							"", MAX_STR_ROW, TextField.DECIMAL | TextField.UNEDITABLE);
			tstActRow1 = new TextField("Actual row 1 cups/total",
							"", MAX_STR_ROW, TextField.DECIMAL | TextField.UNEDITABLE);
			tstExpRow0 = new TextField("Expected row 0 cups/total",
							"", MAX_STR_ROW, TextField.ANY);
			tstExpRow1 = new TextField("Expected row 1 cups/total",
							"", MAX_STR_ROW, TextField.ANY);
			tstSuccess = new TextField("Test success",
							"", 8, TextField.UNEDITABLE);
			tstMveSuccess = new TextField("Test move success",
							"", 8, TextField.UNEDITABLE);
			tstGameOver = new TextField("Test game over",
							"", 8, TextField.UNEDITABLE);
			form.append(tstName);
			form.append(tstRow0);
			form.append(tstRow1);
			form.append(tstMove);
			form.append(tstActRow0);
			form.append(tstActRow1);
			form.append(tstExpRow0);
			form.append(tstExpRow1);
			form.append(tstSuccess);
			form.append(tstMveSuccess);
			form.append(tstGameOver);
			BaseApp.setup(form, BaseApp.cBACK, BaseApp.cOK);
			return form;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getTesting error", e);
			//#endif
			return null;
		}
  }

  public void doShowTesting() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowTesting");}
		//#endif
		try {
			if (gameTest == null) {
				gameTest = getTesting();
			}
			if (BoardGameScreen.table != null) {
				tstRow0.setString(BoardGameScreen.table.toRowString(0));
				tstRow1.setString(BoardGameScreen.table.toRowString(1));
			}
			BaseApp.show(null, gameTest, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doShowOptions error", e);
			//#endif
		}
	}
	*/
	//#endif

  public void doShowOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowOptions");}
		//#endif
		try {
			super.doShowOptions();
			opPlayers.setSelectedIndex(BoardGameApp.gsPlayer - 1, true);
			if (BoardGameApp.gsLevelMsg.length > 0) {
				opLevel.setSelectedIndex(BoardGameApp.gsLevel, true);
			}
			if (BoardGameApp.gsDeptLimit < 0) {
				opDept.setSelectedIndex(BoardGameApp.gsDept - BoardGameApp.gsDeptInit, true);
			}
			if (BoardGameApp.gsRowLimit < 0) {
				opRow.setSelectedIndex(BoardGameApp.gsRow - BoardGameApp.gsRowInit,
						true);
			}
			if (BoardGameApp.gsColLimit < 0) {
				opCol.setSelectedIndex(BoardGameApp.gsCol - BoardGameApp.gsColInit, true);
			}
			if (BoardGameApp.gsNbrPlayersLimit < 0) {
				opNbrPlayers.setSelectedIndex(BoardGameApp.gsNbrPlayers -
						BoardGameApp.gsNbrPlayersInit, true);
			}
			//#ifdef DLOGGING
			opLogLevel.setString( logger.getParent().getLevel().getName());
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doShowOptions error", e);
			//#endif
		}
  }

	//#ifdef DLOGGING
  /**
   * Show is in the record store log file.
	 * FIX do other loggers.
	 *
   * @author Irv Bunton
   */
  public void doShowLogging() {
		if (finestLoggable) {logger.finest("doShowLogging");}
		try {
			if (logf == null) {
				logf = new LoggerRptForm(logManager, this,
						BaseApp.midlet, "net.sf.jlogmicro.util.logging.FormHandler");
				BaseApp.setup(logf, BaseApp.cBACK, null);
			}
			BaseApp.show(null, logf, true);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.severe("doShowLogging error", e);
		}
  }
	//#endif

  public void doApplyOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doApplyOptions");}
		//#endif
		try {
			BoardGameApp.gsPlayer = settingsGameUpd(opPlayers.getSelectedIndex() + 1,
				BoardGameApp.BOARD_GAME_PLAYER, BoardGameApp.gsPlayer);
			if (BoardGameApp.gsLevelMsg.length > 0) {
				BoardGameApp.gsLevel = settingsGameUpd(opLevel.getSelectedIndex(),
						BoardGameApp.BOARD_GAME_LEVEL,
						BoardGameApp.gsLevel);
			}
			if (BoardGameApp.gsDeptLimit < 0) {
				BoardGameApp.gsDept = settingsGameUpd(
						opDept.getSelectedIndex() + BoardGameApp.gsDeptInit,
					BoardGameApp.BOARD_GAME_DEPT, BoardGameApp.gsDept);
			}
			if (BoardGameApp.gsRowLimit < 0) {
				BoardGameApp.gsRow = settingsGameUpd(
						(opRow.getSelectedIndex() + BoardGameApp.gsRowInit),
					BoardGameApp.BOARD_GAME_ROW, BoardGameApp.gsRow);
			}
			if (BoardGameApp.gsColLimit < 0) {
				BoardGameApp.gsCol = settingsGameUpd(
						opCol.getSelectedIndex() + BoardGameApp.gsColInit,
					BoardGameApp.BOARD_GAME_COL, BoardGameApp.gsCol);
			}
			if (BoardGameApp.gsNbrPlayersLimit < 0) {
				BoardGameApp.gsNbrPlayers = settingsGameUpd(
						opNbrPlayers.getSelectedIndex() + BoardGameApp.gsNbrPlayersInit,
					BoardGameApp.BOARD_GAME_NBR_PLAYERS, BoardGameApp.gsNbrPlayers);
			}
			//#ifdef DLOGGING
			String logLevel = opLogLevel.getString().toUpperCase();
			logger.getParent().setLevel(Level.parse(logLevel));
			//#endif
			super.doApplyOptions();
			if (BaseApp.settings != null) {
				BaseApp.settings.saveUpdated();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

  private static int settingsGameUpd(int newValue, String settingsKey, int prevValue) {
		return settingsUpd(newValue, GameApp.hsName + "_" + settingsKey,
				prevValue);
	}

  /**
   * Command dispatcher
   */
  public void commandAction(final Command c, final Displayable d) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("commandAction c,d=" + c.getLabel() + "," + c + "," + d);}
		//#endif
		try {
			if (d == gameOptions) {
				// The options have only 1 command that is not back or OK.  This
				// is of type screen.
				if (c.getCommandType() == Command.SCREEN) {
					setGameDefaults();
					doShowOptions();
					doApplyOptions();
				} else {
					super.commandAction(c, d);
				}
			}
			//#ifdef DLOGGING
			else if (d == logf) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("LoggerRptForm");}
				//#endif
				if (c == BaseApp.cBACK) {
					super.commandAction(c, d);
				} else {
					((CommandListener)logf).commandAction(c, d);
					((Runnable)logf).run();
				}
			}
			//#endif
			//#ifdef DTEST
			else if (d == gameTest) {
				if (c == BaseApp.cOK) {
					processGameAction(GA_PERFORMTEST);
				} else {
					super.commandAction(c, d);
				}
			}
			//#endif
			else {
					super.commandAction(c, d);
			}

		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("commandAction error", e);
			//#endif
		}
  }

  /**
   * Pause the game
   */
  public void doGamePause() {
		super.doGamePause();
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGamePause");}
		//#endif
		prepGameMenu(true);
	}

  public void doGameAbort() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGameAbort");}
		//#endif
		try {
			super.doGameAbort();
			((BoardGameScreen)GameApp.game).gMiniMax.cancel(false);
			((BoardGameScreen)GameApp.game).gMiniMax.clearPrecalculatedMoves();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doGameAbort error", e);
			//#endif
		}
  }

	public List getGameMenu() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("getGameMenu bsavedRec=" + bsavedRec);}
		//#endif
		List gameMenu = Application.getMenu(GameApp.game.name, GameApp.ME_MAINMENU, GameApp.GA_CONTINUE, BaseApp.cEXIT);
		return gameMenu;
	}

  protected void prepGameMenu(boolean canContinue) {
		try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("prepGameMenu canContinue=" + canContinue);}
			//#endif
			boolean canUndo = false;
			boolean canRedo = false;
			if (canContinue) {
				Application.insertMenuItem(gameMenu, GA_CONTINUE);
				if (GameApp.game != null) {
					if (BoardGameScreen.table != null) {
						if (((BoardGameScreen)GameApp.game).checkLastTable()) {
							Application.insertMenuItem(gameMenu, GA_UNDO);
							canUndo = true;
						}
						if (((BoardGameScreen)GameApp.game).checkLastRedoTable()) {
							Application.insertMenuItem(gameMenu, GA_REDO);
							canRedo = true;
						}
						if (canUndo || canRedo) {
							Application.insertMenuItem(gameMenu, GA_ENDGAME);
							//#ifdef DTEST
							Application.insertMenuItem(gameMenu, GA_TEST_ENDGAME);
							//#endif
						}
					}
				}
			} else {
				Application.deleteMenuItem(gameMenu, GA_CONTINUE);
			}
			if (!canUndo) {
				Application.deleteMenuItem(gameMenu, GA_UNDO);
			}
			if (!canRedo) {
				Application.deleteMenuItem(gameMenu, GA_REDO);
			}
			if (!canUndo && !canRedo) {
				Application.deleteMenuItem(gameMenu, GA_ENDGAME);
				//#ifdef DTEST
				Application.insertMenuItem(gameMenu, GA_TEST_ENDGAME);
				//#endif
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("prepGameMenu canUndo,canRedo=" + canUndo + "," + canRedo);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("prepGameMenu error", e);
			//#endif
		}
	}

  public void processGameAction(final int action) {
		try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("processGameAction action=" + action);}
			//#endif
			switch (action) {
				case GA_STARTUP: // Startup
					doStartup();
					break;
				case GA_CONTINUE: // Continue
					if (bsavedRec.length > 0) {
						((BoardGameScreen)game).bsavedRec = bsavedRec;
						bsavedRec = new byte[0];
						doGameStart();
					} else {
						doGameResume();
					}
					break;
				case GA_NEWGAME: // New game
					if (bsavedRec.length > 0) {
						bsavedRec = new byte[0];
						((BoardGameScreen)game).bsavedRec = bsavedRec;
					}
					doGameStart();
					break;
				case GA_UNDO: // Undo last move
					((BoardGameScreen) GameApp.game).undoTable();
					doGameResume();
					break;
				case GA_REDO: // Redo last move
					((BoardGameScreen) GameApp.game).redoTable();
					doGameResume();
					break;
				//#ifdef DTEST
				case GA_TEST_ENDGAME: // Force end game for computer
					((BoardGameScreen)GameApp.game).isHuman[0] = false;
					((BoardGameScreen)GameApp.game).twoplayer = false;
				//#endif
				case GA_ENDGAME: // Force end game
					((BoardGameScreen)GameApp.game).procEndGame();
					doGameResume();
					break;
				//#ifdef DTEST
					/* UNDO
				case GA_TEST: // Redo last move
					doShowTesting();
					break;
					*/
				//#endif
				case GA_OPTIONS:
					doShowOptions();
					break;
				case GA_HELP:
					doHelp();
					break;
				case GA_ABOUT:
					doAbout();
					break;
				case GA_APPLYOPTIONS:
					doApplyOptions();
					break;
					//#ifdef DLOGGING
					/* UNDO
				case GA_PERFORMTEST:
					doPerformTest();
					break;
					*/
					//#endif
					//#ifdef DLOGGING
				case GA_LOGGING:
					doShowLogging();
					break;
					//#endif
				default:
					break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("processGameAction error", e);
			//#endif
		}
  }

  public void loadBoardGameCustomization() {
		try {
			if (BaseApp.settings == null) {
				BaseApp.settings = new Settings();
			}
			BaseApp.settings.load();
			BoardGameApp.gsPlayer = getInt(BoardGameApp.BOARD_GAME_PLAYER, BoardGameApp.gsPlayer);
			BoardGameApp.gsFirst = getInt(BoardGameApp.BOARD_GAME_FIRST, BoardGameApp.gsFirst); 
			BoardGameApp.gsLevel = getInt(BoardGameApp.BOARD_GAME_LEVEL, BoardGameApp.gsLevel); 
			BoardGameApp.gsDept = getInt(BoardGameApp.BOARD_GAME_DEPT, BoardGameApp.gsDept); 
			BoardGameApp.gsRow = getInt(BoardGameApp.BOARD_GAME_ROW, BoardGameApp.gsRow); 
			BoardGameApp.gsCol = getInt(BoardGameApp.BOARD_GAME_COL, BoardGameApp.gsCol); 
			BoardGameApp.gsNbrPlayers = getInt(BoardGameApp.BOARD_GAME_NBR_PLAYERS, BoardGameApp.gsNbrPlayers);
			BoardGameApp.gsTextRow = getInt(BoardGameApp.BOARD_GAME_TEXT_ROW, BoardGameApp.gsTextRow);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("loadBoardGameCustomization error", e);
			//#endif
		}
	}

	public int getInt( String name, int defaultValue ) {
		return BaseApp.settings.getInt( GameApp.hsName + "_" + name, defaultValue );
	}

  /**
   * Destroy the game
   */
  public void done() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("done");}
		//#endif
		if (BaseApp.settings != null) {
			BaseApp.settings.saveUpdated();
		}
		super.done();
	}
}
