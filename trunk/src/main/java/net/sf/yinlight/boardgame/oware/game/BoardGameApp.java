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
//#define DMIDP20
// Expand to define test define
//#define DNOTEST
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define logging define
//#define DNOLOGGING
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
//#ifdef DTEST
//@import net.sf.yinlight.boardgame.oware.game.OwareTable;
//#endif
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import com.substanceofcode.rssreader.presentation.FeatureForm;
import net.eiroca.j2me.rms.Settings;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;


//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
//@import net.sf.jlogmicro.util.presentation.LoggerRptForm;
//#endif

/**
	* Oware game application.  Handle game options in addition to standard app
	* options on form.  Save options.
	*/ 
abstract public class BoardGameApp extends GameApp {

  public static String GRAPHICS_PRECALCULATE = "boardgame-precalculate";
  public static boolean precalculate = true;
  public static String storeName = "BOARD_GAME_STORE";

  public static int ACTION_OFFSET = 0;
  public static final int GA_UNDO = GameApp.GA_USERDEF + 0;
  public static final int GA_REDO = GameApp.GA_USERDEF + 1;
  public static final int GA_ENDGAME = GameApp.GA_USERDEF + 2;
	//#ifdef DMIDP10
//@  public static final int GA_PAUSE = GameApp.GA_USERDEF + 3;
	//#endif
	//#ifdef DTEST
//@  public static final int GA_TEST_ENDGAME = GameApp.GA_USERDEF + 4;
//@  public static final int GA_TEST = GameApp.GA_USERDEF + 5;
//@  public static final int GA_PERFORMTEST = GameApp.GA_USERDEF + 6;
	//#endif
	//#ifdef DLOGGING
//@  public static final int GA_LOGGING = GameApp.GA_USERDEF + 7;
//@	private Form logf = null;
	//#endif

  public static String[] playerNames;
  private boolean first = true;
  private byte[] bsavedRec = new byte[0];

  protected ChoiceGroup opPlayers;
  protected ChoiceGroup opLevel = null;
  protected ChoiceGroup opDept = null;
  protected ChoiceGroup opRow = null;
  protected ChoiceGroup opCol = null;
  protected ChoiceGroup opNbrPlayers = null;
	//#ifdef DLOGGING
//@  protected TextField opLogLevel;
	//#endif
	//#ifdef DTEST
//@  public static final int MAX_STR_ROW = OwareTable.NBR_COL + 3;
//@  protected Displayable gameTest = null;
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
  static public String[] gsSquareImages = new String[0];
  static public String[] gsPiece1Images = new String[0];
  static public String[] gsPiece2Images = new String[0];
  static public int[] gsLevelMsg = new int[0];
  /**
	* Initialization index into parameter definition
	*/
  public static final int PD_CURR = 0;
  public static final int PD_DFLT = 1;
  public static final int PD_INIT = 2;
  public static final int PD_LIMIT = 3;
  public static final int PD_INCR = 4;
  public static int[] gsDepth;
  public static int[] gsRow;
  public static int[] gsCol;
  public static int[] gsNbrPlayers;
  public static int gsTextRow = 0;
	/* Skill level. */
  final static public int gsLevelNormal = 0;
  final static public int gsLevelDifficult = 1;
  final static public int gsLevelHard = 2;
  public static int gsLevel = gsLevelDifficult;

	//#ifdef DLOGGING
//@  private boolean fineLoggable;
//@  private boolean finestLoggable;
//@  private boolean traceLoggable;
//@	final public LogManager logManager;
//@	private Logger logger;
	//#endif

	//#ifdef DJMTEST
//@  public BoardGameApp(String name)
	//#else
  public BoardGameApp()
	//#endif
	{
		//#ifdef DJMTEST
//@    super(name);
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
//@		logManager = LogManager.getLogManager();
//@		logManager.readConfiguration(this);
//@		logger = Logger.getLogger(logManager, "BoardGameApp", null);
//@		fineLoggable = logger.isLoggable(Level.FINE);
//@		finestLoggable = logger.isLoggable(Level.FINEST);
//@		traceLoggable = logger.isLoggable(Level.TRACE);
//@		logger.info("AppConstants.MSG_GAMEAPP_USERDEF=" + AppConstants.MSG_GAMEAPP_USERDEF);
//@		logger.info("AppConstants.MSG_OWARE_USERDEF=" + AppConstants.MSG_OWARE_USERDEF);
//@		logger.info("AppConstants.MSG_REVERSI_USERDEF=" + AppConstants.MSG_REVERSI_USERDEF);
		//#endif
    BaseApp.menu = new short[][] {
        {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_CONTINUE, GameApp.GA_CONTINUE, 0
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_NEWGAME, GameApp.GA_NEWGAME, 1
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_UNDO, (short)BoardGameApp.GA_UNDO, 7
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_REDO, (short)BoardGameApp.GA_REDO, 8
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_ENDGAME, (short)BoardGameApp.GA_ENDGAME, -1, AppConstants.MSG_SURE_END
	//#ifdef DTEST
//@        }, {
//@            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_TEST_ENDGAME, (short)BoardGameApp.GA_TEST_ENDGAME, -1
//@        }, {
//@            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_TEST, (short)BoardGameApp.GA_TEST, 0
		//#endif
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 4
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
				//#ifdef DLOGGING
//@        }, {
//@            GameApp.ME_MAINMENU, AppConstants.MSG_MENU_MAIN_LOGGING, (short)BoardGameApp.GA_LOGGING, -1
				//#endif
        }
    };
  }

  public void init() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			// Need to do this before game screen and table are created.
			if (first) {
				loadBoardGameCustomization();
			}
			super.init();
			final String gval = super.readAppProperty(GRAPHICS_PRECALCULATE, "true");
			BoardGameApp.precalculate = gval.equals("true");
			if (first) {
				bsavedRec = ((BoardGameScreen)game).getSavedGameRecord();
			}
			//#ifdef DLOGGING
//@			logger.info("BaseApp.messages[AppConstants.MSG_BOARDAPP_USERDEF - 1]=" + BaseApp.messages[AppConstants.MSG_BOARDAPP_USERDEF - 1]);
			//#endif
			prepGameMenu(bsavedRec.length > 0);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("init error", e);
			//#endif
		} finally {
			first = false;
		}
  }

	public void setGameDefaults() {
		BoardGameApp.gsDepth[PD_CURR] = BoardGameApp.gsDepth[PD_DFLT];
		BoardGameApp.gsRow[PD_CURR] = BoardGameApp.gsRow[PD_DFLT];
		BoardGameApp.gsCol[PD_CURR] = BoardGameApp.gsCol[PD_DFLT];
		BoardGameApp.gsNbrPlayers[PD_CURR] = BoardGameApp.gsNbrPlayers[PD_DFLT];
	}

	//#ifdef DMIDP10
//@  public BoardGameScreen updGameScreen(BoardGameScreen bgs) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("updGameScreen");}
		//#endif
//@		try {
//@			Command cPause = BaseApp.newCommand(AppConstants.MSG_MENU_MAIN_PAUSE, Command.STOP, 1, BoardGameApp.GA_PAUSE);
//@			bgs.addCommand(cPause);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("updGameScreen cPause=" + cPause);}
			//#endif
//@			return bgs;
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("updGameScreen error", e);
			//#endif
//@			return null;
//@		}
//@  }
	//#endif

	//#ifdef DMIDP10
//@  /**
//@   * Handle the action
//@   */
//@  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
//@		if ((d == GameApp.game) && (action == GA_PAUSE)) {
//@			doGamePause();
//@			return true;
//@		}
//@    return false;
//@  }
	//#endif

  protected Displayable getOptions() {
		try {
			final Form form = new FeatureForm(BaseApp.messages[AppConstants.MSG_MENU_MAIN_OPTIONS]);
			Command cdefault = new Command(
					BaseApp.messages[AppConstants.MSG_MENU_OPTIONS_DEAULT],
					Command.SCREEN, 9);
			form.addCommand(cdefault);
			opPlayers = Application.createChoiceGroup(AppConstants.MSG_GAMEMODE,
					Choice.EXCLUSIVE, new int[] {
			AppConstants.MSG_GAMEMODE1, AppConstants.MSG_GAMEMODE2});
			if (BoardGameApp.gsLevelMsg.length > 0) {
				opLevel = Application.createChoiceGroup(AppConstants.MSG_AILEVEL,
						Choice.EXCLUSIVE, BoardGameApp.gsLevelMsg);
			}
			opDept = BoardGameApp.createNumRangePD(gsDepth,
					AppConstants.MSG_SKILL_LEVEL);
			opRow = BoardGameApp.createNumRangePD(gsRow,
					AppConstants.MSG_ROW);
			opCol = BoardGameApp.createNumRangePD(gsCol,
					AppConstants.MSG_COL);
			opNbrPlayers = BoardGameApp.createNumRangePD(gsNbrPlayers,
					AppConstants.MSG_NBR_PLAYERS);
			//#ifdef DLOGGING
//@			opLogLevel = new TextField("Logging level",
//@							logger.getParent().getLevel().getName(), 20, TextField.ANY);
			//#endif
			form.append(opPlayers);
			if (opLevel != null) {
				form.append(opLevel);
			}
			if (opDept != null) {
				form.append(opDept);
			}
			if (opRow != null) {
				form.append(opRow);
			}
			if (opCol != null) {
				form.append(opCol);
			}
			if (opNbrPlayers != null) {
				form.append(opNbrPlayers);
			}
			//#ifdef DLOGGING
//@			form.append(opLogLevel);
			//#endif
			BaseApp.setup(form, BaseApp.cBACK, BaseApp.cOK);
			return form;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("getOptions error", e);
			//#endif
			return null;
		}
  }

	//#ifdef DTEST
//@	/*
//@  protected Displayable getTesting() {
//@		try {
//@			final Form form = new FeatureForm(BaseApp.messages[AppConstants.MSG_MENU_MAIN_TEST]);
//@			tstName = new TextField("Test name",
//@							"", 80, TextField.ANY);
//@			tstRow0 = new TextField("Input row 0 cups/total",
//@							"", MAX_STR_ROW, TextField.ANY);
//@			tstRow1 = new TextField("Input row 1 cups/total",
//@							"", MAX_STR_ROW, TextField.ANY);
//@			tstMove = new TextField("Move", "", 2, TextField.ANY);
//@			tstActRow0 = new TextField("Actual row 0 cups/total",
//@							"", MAX_STR_ROW, TextField.DECIMAL | TextField.UNEDITABLE);
//@			tstActRow1 = new TextField("Actual row 1 cups/total",
//@							"", MAX_STR_ROW, TextField.DECIMAL | TextField.UNEDITABLE);
//@			tstExpRow0 = new TextField("Expected row 0 cups/total",
//@							"", MAX_STR_ROW, TextField.ANY);
//@			tstExpRow1 = new TextField("Expected row 1 cups/total",
//@							"", MAX_STR_ROW, TextField.ANY);
//@			tstSuccess = new TextField("Test success",
//@							"", 8, TextField.UNEDITABLE);
//@			tstMveSuccess = new TextField("Test move success",
//@							"", 8, TextField.UNEDITABLE);
//@			tstGameOver = new TextField("Test game over",
//@							"", 8, TextField.UNEDITABLE);
//@			form.append(tstName);
//@			form.append(tstRow0);
//@			form.append(tstRow1);
//@			form.append(tstMove);
//@			form.append(tstActRow0);
//@			form.append(tstActRow1);
//@			form.append(tstExpRow0);
//@			form.append(tstExpRow1);
//@			form.append(tstSuccess);
//@			form.append(tstMveSuccess);
//@			form.append(tstGameOver);
//@			BaseApp.setup(form, BaseApp.cBACK, BaseApp.cOK);
//@			return form;
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("getTesting error", e);
			//#endif
//@			return null;
//@		}
//@  }
//@
//@  public void doShowTesting() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doShowTesting");}
		//#endif
//@		try {
//@			if (gameTest == null) {
//@				gameTest = getTesting();
//@			}
//@			if (BoardGameScreen.table != null) {
//@				tstRow0.setString(BoardGameScreen.table.toRowString(0));
//@				tstRow1.setString(BoardGameScreen.table.toRowString(1));
//@			}
//@			BaseApp.show(null, gameTest, true);
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("doShowOptions error", e);
			//#endif
//@		}
//@	}
//@	*/
	//#endif

  public void doShowOptions() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doShowOptions");}
		//#endif
		try {
			super.doShowOptions();
			opPlayers.setSelectedIndex(BoardGameApp.gsPlayer - 1, true);
			if (opLevel != null) {
				opLevel.setSelectedIndex(BoardGameApp.gsLevel, true);
			}
			if (opDept != null) {
				BoardGameApp.setSelectedChoicePD(opDept, gsDepth);
			}
			if (opRow != null) {
				BoardGameApp.setSelectedChoicePD(opRow, gsRow);
			}
			if (opCol != null) {
				BoardGameApp.setSelectedChoicePD(opCol, gsCol);
			}
			if (opNbrPlayers != null) {
				BoardGameApp.setSelectedChoicePD(opNbrPlayers, gsNbrPlayers);
			}
			//#ifdef DLOGGING
//@			opLogLevel.setString( logger.getParent().getLevel().getName());
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("doShowOptions error", e);
			//#endif
		}
  }

	//#ifdef DLOGGING
//@  /**
//@   * Show is in the record store log file.
//@	 * FIX do other loggers.
//@	 *
//@   * @author Irv Bunton
//@   */
//@  public void doShowLogging() {
//@		if (finestLoggable) {logger.finest("doShowLogging");}
//@		try {
//@			if (logf == null) {
//@				logf = new LoggerRptForm(logManager, this,
//@						BaseApp.midlet, "net.sf.jlogmicro.util.logging.FormHandler");
//@				BaseApp.setup(logf, BaseApp.cBACK, null);
//@			}
//@			BaseApp.show(null, logf, true);
//@		} catch (Throwable e) {
//@			e.printStackTrace();
//@			logger.severe("doShowLogging error", e);
//@		}
//@  }
	//#endif

  public void doApplyOptions() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doApplyOptions");}
		//#endif
		try {
			BoardGameApp.gsPlayer = settingsGameUpd(opPlayers.getSelectedIndex() + 1,
				BoardGameApp.BOARD_GAME_PLAYER, BoardGameApp.gsPlayer);
			if (opLevel != null) {
				BoardGameApp.gsLevel = settingsGameUpd(opLevel.getSelectedIndex(),
						BoardGameApp.BOARD_GAME_LEVEL,
						BoardGameApp.gsLevel);
			}
			if (opDept != null) {
				settingsGameUpdPD(opDept, BoardGameApp.gsDepth,
						BoardGameApp.BOARD_GAME_DEPT);
			}
			if (opRow != null) {
				settingsGameUpdPD(opRow, BoardGameApp.gsRow,
						BoardGameApp.BOARD_GAME_ROW);
			}
			if (opCol != null) {
				settingsGameUpdPD(opCol, BoardGameApp.gsCol,
						BoardGameApp.BOARD_GAME_COL);
			}
			if (opNbrPlayers != null) {
				settingsGameUpdPD(opNbrPlayers, BoardGameApp.gsNbrPlayers,
						BoardGameApp.BOARD_GAME_NBR_PLAYERS);
			}
			//#ifdef DLOGGING
//@			String logLevel = opLogLevel.getString().toUpperCase();
//@			logger.getParent().setLevel(Level.parse(logLevel));
			//#endif
			super.doApplyOptions();
			if (BaseApp.settings != null) {
				BaseApp.settings.saveUpdated();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

  private static int settingsGameUpd(int newValue, String settingsKey, int prevValue) {
		return Application.settingsUpd(newValue, GameApp.hsName + "_" + settingsKey,
				prevValue);
	}

  /**
   * Command dispatcher
   */
  public void commandAction(final Command c, final Displayable d) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("commandAction c,d=" + c.getLabel() + "," + c + "," + d);}
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
//@			else if (d == logf) {
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("LoggerRptForm");}
				//#endif
//@				if (c == BaseApp.cBACK) {
//@					super.commandAction(c, d);
//@				} else {
//@					((CommandListener)logf).commandAction(c, d);
//@					((Runnable)logf).run();
//@				}
//@			}
			//#endif
			//#ifdef DTEST
//@			else if (d == gameTest) {
//@				if (c == BaseApp.cOK) {
//@					processGameAction(GA_PERFORMTEST);
//@				} else {
//@					super.commandAction(c, d);
//@				}
//@			}
			//#endif
			else {
					super.commandAction(c, d);
			}

		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("commandAction error", e);
			//#endif
		}
  }

  /**
   * Pause the game
   */
  public void doGamePause() {
		super.doGamePause();
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doGamePause");}
		//#endif
		prepGameMenu(true);
	}

  public void doGameAbort() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doGameAbort");}
		//#endif
		try {
			super.doGameAbort();
			((BoardGameScreen)GameApp.game).gMiniMax.cancel(false);
			((BoardGameScreen)GameApp.game).gMiniMax.clearPrecalculatedMoves();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("doGameAbort error", e);
			//#endif
		}
  }

	public List getGameMenu() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("getGameMenu bsavedRec=" + bsavedRec);}
		//#endif
		List gameMenu = Application.getMenu(GameApp.game.name, GameApp.ME_MAINMENU, GameApp.GA_CONTINUE, BaseApp.cEXIT);
		return gameMenu;
	}

  protected void prepGameMenu(boolean canContinue) {
		try {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("prepGameMenu canContinue=" + canContinue);}
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
//@							Application.insertMenuItem(gameMenu, GA_TEST_ENDGAME);
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
//@				Application.insertMenuItem(gameMenu, GA_TEST_ENDGAME);
				//#endif
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("prepGameMenu canUndo,canRedo=" + canUndo + "," + canRedo);}
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("prepGameMenu error", e);
			//#endif
		}
	}

  public void processGameAction(final int action) {
		try {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("processGameAction action=" + action);}
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
//@				case GA_TEST_ENDGAME: // Force end game for computer
//@					((BoardGameScreen)GameApp.game).isHuman[0] = false;
//@					((BoardGameScreen)GameApp.game).twoplayer = false;
				//#endif
				case GA_ENDGAME: // Force end game
					((BoardGameScreen)GameApp.game).procEndGame();
					doGameResume();
					break;
				//#ifdef DTEST
//@					/* UNDO
//@				case GA_TEST: // Redo last move
//@					doShowTesting();
//@					break;
//@					*/
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
//@					/* UNDO
//@				case GA_PERFORMTEST:
//@					doPerformTest();
//@					break;
//@					*/
					//#endif
					//#ifdef DLOGGING
//@				case GA_LOGGING:
//@					doShowLogging();
//@					break;
					//#endif
				default:
					break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("processGameAction error", e);
			//#endif
		}
  }

  public void loadBoardGameCustomization() {
		try {
			if (BaseApp.settings == null) {
				BaseApp.settings = new Settings();
			}
			BaseApp.settings.load();
			BoardGameApp.gsPlayer = getIntGame(BoardGameApp.BOARD_GAME_PLAYER, BoardGameApp.gsPlayer);
			BoardGameApp.gsFirst = getIntGame(BoardGameApp.BOARD_GAME_FIRST, BoardGameApp.gsFirst); 
			BoardGameApp.gsLevel = getIntGame(BoardGameApp.BOARD_GAME_LEVEL, BoardGameApp.gsLevel); 
			getIntGamePD(BoardGameApp.BOARD_GAME_DEPT, BoardGameApp.gsDepth); 
			getIntGamePD(BoardGameApp.BOARD_GAME_ROW, BoardGameApp.gsRow); 
			getIntGamePD(BoardGameApp.BOARD_GAME_COL, BoardGameApp.gsCol); 
			getIntGamePD(BoardGameApp.BOARD_GAME_NBR_PLAYERS, BoardGameApp.gsNbrPlayers); 
			BoardGameApp.gsTextRow = getIntGame(BoardGameApp.BOARD_GAME_TEXT_ROW, BoardGameApp.gsTextRow);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("loadBoardGameCustomization error", e);
			//#endif
		}
	}

	public int getIntGame( String name, int defaultValue ) {
		return BaseApp.settings.getInt( GameApp.hsName + "_" + name, defaultValue );
	}

	public void getIntGamePD( String name, int[] pdef ) {
		pdef[BoardGameApp.PD_CURR] = BaseApp.settings.getInt(
				GameApp.hsName + "_" + name, pdef[BoardGameApp.PD_CURR] );
	}

	public void getIntPD( String name, int[] pdef ) {
		pdef[BoardGameApp.PD_CURR] = BaseApp.settings.getInt( name,
				pdef[BoardGameApp.PD_CURR] );
	}

  /**
   * Destroy the game
   */
  public void done() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("done");}
		//#endif
		if (BaseApp.settings != null) {
			BaseApp.settings.saveUpdated();
		}
		super.done();
	}

	public static ChoiceGroup createNumRangePD(int[] pdef, int msgNbr) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("BoardGameApp");
//@			logger.finest("createNumRangePD pdef[BoardGameApp.PD_INIT],pdef[BoardGameApp.PD_LIMIT]=" + pdef[BoardGameApp.PD_INIT] + "," + pdef[BoardGameApp.PD_LIMIT]);
			//#endif
			if (pdef[BoardGameApp.PD_LIMIT] < 0) {
				return Application.createNumRange(msgNbr, pdef[BoardGameApp.PD_INIT],
						pdef[BoardGameApp.PD_LIMIT], pdef[BoardGameApp.PD_INCR]);
			} else {
				return null;
			}
	}

	public static void setSelectedChoicePD(Choice choice, int[] pdef) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("BoardGameApp");
//@			logger.finest("setSelectedChoicePD pdef[BoardGameApp.PD_INIT],pdef[BoardGameApp.PD_CURR]=" + pdef[BoardGameApp.PD_INIT] + "," + pdef[BoardGameApp.PD_CURR]);
			//#endif
			choice.setSelectedIndex(pdef[BoardGameApp.PD_CURR] -
					pdef[BoardGameApp.PD_INIT], true);
	}

  private static void settingsGameUpdPD(Choice choice, int[] pdef,
			String settingsKey) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("BoardGameApp");
//@			logger.finest("settingsGameUpdPD settingsKey,pdef[BoardGameApp.PD_INIT],pdef[BoardGameApp.PD_CURR]=" + settingsKey + "," + pdef[BoardGameApp.PD_INIT] + "," + pdef[BoardGameApp.PD_CURR]);
			//#endif
				pdef[BoardGameApp.PD_CURR] = settingsGameUpd(
						choice.getSelectedIndex() + pdef[BoardGameApp.PD_INIT],
					settingsKey, pdef[BoardGameApp.PD_CURR]);
	}

  public static void settingsUpdPD(Choice choice, int[] pdef,
			String settingsKey) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("BoardGameApp");
//@			logger.finest("settingsUpdPD settingsKey,pdef[BoardGameApp.PD_INIT],pdef[BoardGameApp.PD_CURR]=" + settingsKey + "," + pdef[BoardGameApp.PD_INIT] + "," + pdef[BoardGameApp.PD_CURR]);
			//#endif
				pdef[BoardGameApp.PD_CURR] = Application.settingsUpd(
						choice.getSelectedIndex() + pdef[BoardGameApp.PD_INIT],
					settingsKey, pdef[BoardGameApp.PD_CURR]);
	}

}
