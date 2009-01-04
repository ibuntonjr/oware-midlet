/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* FIX game menu
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
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.midlet;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.sf.yinlight.boardgame.oware.game.ui.OwareScreen;
import net.sf.yinlight.boardgame.oware.game.OwareGame;
import net.sf.yinlight.boardgame.oware.game.OwareTable;
import net.sf.yinlight.boardgame.oware.game.OwareMove;
import com.substanceofcode.rssreader.presentation.FeatureForm;
import com.substanceofcode.rssreader.presentation.FeatureMgr;


//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

/**
	* Oware game application.  Handle game options in addition to standard app
	* options on form.  Save options.
	*/ 
public class OwareMIDlet extends GameApp {

  public static short MSG_OFFSET = 0;
  final public static int MSG_NAME = GameApp.MSG_USERDEF + MSG_OFFSET++; // 0
  final public static short MSG_MENU_MAIN_UNDO = (short)(GameApp.MSG_USERDEF + MSG_OFFSET++);
  final public static short MSG_MENU_MAIN_REDO = (short)(GameApp.MSG_USERDEF + MSG_OFFSET++);
  final public static short MSG_MENU_MAIN_PAUSE = (short)(GameApp.MSG_USERDEF + MSG_OFFSET++);
  final public static short MSG_MENU_MAIN_TEST = (short)(GameApp.MSG_USERDEF + MSG_OFFSET++);
  final public static short MSG_MENU_MAIN_LOGGING = (short)(GameApp.MSG_USERDEF + MSG_OFFSET++);
  final public static int MSG_GAMEMODE = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GAMEMODE1 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GAMEMODE2 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_AILEVEL = GameApp.MSG_USERDEF + MSG_OFFSET++; //A.I. Difficulty
  final public static int MSG_AILEVEL1 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_AILEVEL2 = GameApp.MSG_USERDEF + MSG_OFFSET++;
	// FIX use messages for numbers
  final public static int MSG_AILEVEL3 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_AILEVEL4 = GameApp.MSG_USERDEF + MSG_OFFSET++; // 10
  final public static int MSG_NAMEPLAYER1 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_NAMEPLAYER2 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_MAX_HOLES = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM1 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM2 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM3 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM4 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GRAND_SLAM5 = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_GOODLUCK = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_THINKING = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_INVALIDMOVE = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_WONCOMPUTER = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_HUMANWON = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_PLAYERWON = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_DRAW = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_HUMAN = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_COMPUTER = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_PASS = GameApp.MSG_USERDEF + MSG_OFFSET++;
  final public static int MSG_LEVELPREFIX = GameApp.MSG_USERDEF + MSG_OFFSET++;

  public static short ACTION_OFFSET = 0;
  public static final int GA_UNDO = GameApp.GA_USERDEF + 0;
  public static final int GA_REDO = GameApp.GA_USERDEF + 1;
	//#ifdef DMIDP10
  public static final int GA_PAUSE = GameApp.GA_USERDEF + 2;
	//#endif
	//#ifdef DTEST
  public static final int GA_TEST = GameApp.GA_USERDEF + 3;
  public static final int GA_PERFORMTEST = GameApp.GA_USERDEF + 4;
	//#endif
	//#ifdef DLOGGING
  public static final int GA_LOGGING = GameApp.GA_USERDEF + 5;
	//#endif

  public static String[] playerNames;
  private boolean first = true;
  private byte[] bsavedRec = new byte[0];

  protected ChoiceGroup opPlayers;
  protected ChoiceGroup opLevel;
  protected ChoiceGroup opDept;
  protected ChoiceGroup opMaxHoles;
  protected ChoiceGroup opGrandSlam;
	//#ifdef DLOGGING
  protected TextField opLogLevel;
	//#endif
	//#ifdef DTEST
  public static final int MAX_STR_ROW = OwareTable.NBR_COL + 3;
  protected TextField tstName;
  protected TextField tstRow0;
  protected TextField tstRow1;
  protected TextField tstMove;
  protected TextField tstActRow0;
  protected TextField tstActRow1;
  protected TextField tstExpRow0;
  protected TextField tstExpRow1;
  protected TextField tstSuccess;
  protected TextField tstMveSuccess;
  protected TextField tstGameOver;
  protected Displayable gameTest = null;
	//#endif

	/* How many human players. */
  public static int gsPlayer = 1;
  public static int gsFirst = 1;
	/* Skill level. */
  final static public int gsLevelNormal = 0;
  final static public int gsLevelDifficult = 1;
  final static public int gsLevelHard = 2;
  public static int gsLevel = gsLevelDifficult;
	/* Dept.  Number of moves that the AI tests. */
  public static int gsDept = 3;
  public static int gsMaxHoles = OwareTable.NBR_COL;
  public static int gsGrandSlam = 0;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	final public LogManager logManager;
	private Logger logger;
	//#endif

  public OwareMIDlet() {
		//#ifdef DJMTEST
    super("Mancala Test Suite");
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
		logManager = LogManager.getLogManager();
		logManager.readConfiguration(this);
		logger = Logger.getLogger(logManager, "OwareMIDlet", null);
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
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_UNDO, (short)OwareMIDlet.GA_UNDO, -1
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_REDO, (short)OwareMIDlet.GA_REDO, -1
		//#ifdef DLOGGING
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_TEST, (short)OwareMIDlet.GA_TEST, 0
		//#endif
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 4
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
				//#ifdef DLOGGING
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_LOGGING, (short)OwareMIDlet.GA_LOGGING, 6
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_LOGGING, (short)OwareMIDlet.GA_LOGGING, 6
				//#endif
        }
    };
    GameApp.hsName = "Oware";
  }

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			OwareMIDlet.playerNames = new String[] {
					BaseApp.messages[OwareMIDlet.MSG_NAMEPLAYER1], BaseApp.messages[OwareMIDlet.MSG_NAMEPLAYER2]};
			if (first) {
				first = false;
				bsavedRec = ((OwareScreen)game).getSavedGameRecord();
			}
			if (bsavedRec.length > 0) {
				prepGameMenu(true);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

  public GameScreen getGameScreen() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("getGameScreen");}
		//#endif
		try {
			OwareScreen ows = new OwareScreen(this);
			//#ifdef DMIDP10
			Command cPause = BaseApp.newCommand(OwareMIDlet.MSG_MENU_MAIN_PAUSE, Command.STOP, 1, OwareMIDlet.GA_PAUSE);
			ows.addCommand(cPause);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("getGameScreen cPause=" + cPause);}
			//#endif
			//#endif
			return ows;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getGameScreen error", e);
			//#endif
			return null;
		}
  }

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
			opPlayers = FeatureMgr.createChoiceGroup(OwareMIDlet.MSG_GAMEMODE,
					Choice.EXCLUSIVE, new int[] {
			OwareMIDlet.MSG_GAMEMODE1, OwareMIDlet.MSG_GAMEMODE2});
			opLevel = FeatureMgr.createChoiceGroup(OwareMIDlet.MSG_AILEVEL,
					Choice.EXCLUSIVE,
					new int[] { OwareMIDlet.MSG_AILEVEL1,
			OwareMIDlet.MSG_AILEVEL2, OwareMIDlet.MSG_AILEVEL3});
			/* FIX
			opLevel.append(BaseApp.messages[OwareMIDlet.MSG_AILEVEL4], null);
			*/
			opDept = FeatureMgr.createNumRange(OwareMIDlet.MSG_AILEVEL, 14);
			opMaxHoles = FeatureMgr.createNumRange(OwareMIDlet.MSG_MAX_HOLES,
					OwareTable.NBR_COL);
			opGrandSlam = FeatureMgr.createChoiceGroup(
					OwareMIDlet.MSG_GRAND_SLAM,
					Choice.EXCLUSIVE,
					new int[] { OwareMIDlet.MSG_GRAND_SLAM1,
			OwareMIDlet.MSG_GRAND_SLAM2, OwareMIDlet.MSG_GRAND_SLAM3,
			OwareMIDlet.MSG_GRAND_SLAM4, OwareMIDlet.MSG_GRAND_SLAM5});
			//#ifdef DLOGGING
			opLogLevel = new TextField("Logging level",
							logger.getParent().getLevel().getName(), 20, TextField.ANY);
			//#endif
			form.append(opPlayers);
			form.append(opLevel);
			form.append(opDept);
			form.append(opMaxHoles);
			form.append(opGrandSlam);
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
  protected Displayable getTesting() {
		try {
			final Form form = new FeatureForm(BaseApp.messages[OwareMIDlet.MSG_MENU_MAIN_TEST]);
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
			if (OwareScreen.table != null) {
				tstRow0.setString(OwareScreen.table.toRowString(0));
				tstRow1.setString(OwareScreen.table.toRowString(1));
			}
			BaseApp.show(null, gameTest, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doShowOptions error", e);
			//#endif
		}
	}

	//#endif

  public void doShowOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowOptions");}
		//#endif
		try {
			super.doShowOptions();
			opPlayers.setSelectedIndex(OwareMIDlet.gsPlayer - 1, true);
			opLevel.setSelectedIndex(OwareMIDlet.gsLevel, true);
			opDept.setSelectedIndex(OwareMIDlet.gsDept - 1, true);
			opMaxHoles.setSelectedIndex(OwareMIDlet.gsMaxHoles - 1, true);
			opGrandSlam.setSelectedIndex(OwareMIDlet.gsGrandSlam, true);
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
			Form logf = new RecStoreLoggerForm(logManager, this, BaseApp.midlet);
			BaseApp.setup(logf, BaseApp.cBACK, null);
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
			OwareMIDlet.gsPlayer = opPlayers.getSelectedIndex() + 1;
			OwareMIDlet.gsLevel = opLevel.getSelectedIndex();
			OwareMIDlet.gsDept = opDept.getSelectedIndex() + 1;
			OwareMIDlet.gsMaxHoles = opMaxHoles.getSelectedIndex() + 1;
			OwareMIDlet.gsGrandSlam = opGrandSlam.getSelectedIndex();
			((OwareScreen) GameApp.game).updateSkillInfo();
			//#ifdef DLOGGING
			String logLevel = opLogLevel.getString().toUpperCase();
			logger.getParent().setLevel(Level.parse(logLevel));
			//#endif
			super.doApplyOptions();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

	//#ifdef DTEST
  public void doPerformTest() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doPerformTest");}
		//#endif
		try {
			perfResult result = performTest(tstRow0.getString(), tstRow1.getString(),
			tstExpRow0.getString(), tstExpRow1.getString(), tstMove.getString());
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("doPerformTest result.success=" + result.success);}
			//#endif
			tstSuccess.setString(String.valueOf(result.success));
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("doPerformTest result.mveResult=" + result.mveResult);}
			//#endif
			tstMveSuccess.setString(String.valueOf(result.mveResult));
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("doPerformTest result.actRow0.length(),result.actRow0=" + result.actRow0.length() + "," + result.actRow0);}
			//#endif
			tstActRow0.setString(result.actRow0);
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("doPerformTest result.actRow1.length(),result.actRow1=" + result.actRow1.length() + "," + result.actRow1);}
			//#endif
			tstActRow1.setString(result.actRow1);
			tstGameOver.setString(String.valueOf(result.gameOver));
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doPerformTest error", e);
			//#endif
		}
  }

	public class perfResult {
		public boolean success;
		public boolean mveResult;
		public boolean gameOver;
		public String actRow0;
		public String actRow1;
	}


  public perfResult performTest(String srow0,
                          String srow1,
                          String sexpRow0,
                          String sexpRow1, String smove) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("performTest");}
		//#endif
		perfResult result = new perfResult();
		try {
			OwareScreen.table.fromRowString(0, srow0);
			System.out.println("JUnit srow0:" + srow0);
			OwareScreen.table.fromRowString(1, srow1);
			System.out.println("JUnit srow1:" + srow1);
			OwareTable cmpTable = new OwareTable(OwareScreen.table);
			OwareTable newTable = new OwareTable();
			System.out.println("JUnit smove:" + smove);
			OwareMove move = new OwareMove(
					Integer.valueOf(smove.substring(0, 1)).intValue(),
			Integer.valueOf(smove.substring(1)).intValue());
			((OwareScreen)GameApp.game).actPlayer = (byte)move.row;
			((OwareScreen)GameApp.game).rgame.setPlayer((byte)move.row);
			result.mveResult = ((OwareScreen)GameApp.game).processMove(move, false);
			cmpTable.fromRowString(0, sexpRow0);
			System.out.println("JUnit sexpRow0:" + sexpRow0);
			cmpTable.fromRowString(1, sexpRow1);
			System.out.println("JUnit sexpRow1:" + sexpRow1);
			result.success = OwareScreen.table.equals(cmpTable);
			result.gameOver = ((OwareScreen)GameApp.game).rgame.isGameEnded();
			result.actRow0 = OwareScreen.table.toRowString(0);
			result.actRow1 = OwareScreen.table.toRowString(1);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doPerformTest error", e);
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doPerformTest error", e);
			//#endif
		} finally {
			return result;
		}
  }

  /**
   * Command dispatcher
   */
  public void commandAction(final Command c, final Displayable d) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("commandAction c,d=" + c.getLabel() + "," + c + "," + d);}
		//#endif
		try {
			if (d == gameTest) {
				if (c == BaseApp.cOK) {
					processGameAction(GA_PERFORMTEST);
				} else {
					super.commandAction(c, d);
				}
			} else {
					super.commandAction(c, d);
			}

		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("commandAction error", e);
			//#endif
		}
  }
	//#endif

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
			GameMinMax.cancel(false);
			GameMinMax.clearPrecalculatedMoves();
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
					OwareScreen osc = (OwareScreen)GameApp.game;
					if (OwareScreen.table != null) {
						if (OwareScreen.rgame.checkLastTable()) {
							Application.insertMenuItem(gameMenu, GA_UNDO);
							canUndo = true;
						}
						if (OwareScreen.rgame.checkLastRedoTable()) {
							Application.insertMenuItem(gameMenu, GA_REDO);
							canRedo = true;
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
						((OwareScreen)game).bsavedRec = bsavedRec;
						bsavedRec = new byte[0];
						doGameStart();
					} else {
						doGameResume();
					}
					break;
				case GA_NEWGAME: // New game
					if (bsavedRec.length > 0) {
						bsavedRec = new byte[0];
					}
					doGameStart();
					break;
				case GA_UNDO: // Undo last move
					((OwareScreen) GameApp.game).undoTable();
					doGameResume();
					break;
				case GA_REDO: // Redo last move
					((OwareScreen) GameApp.game).redoTable();
					doGameResume();
					break;
				//#ifdef DTEST
				case GA_TEST: // Redo last move
					doShowTesting();
					break;
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
				case GA_PERFORMTEST:
					doPerformTest();
					break;
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

  /**
   * Game Shutdown
   */
  public void doShutdown() {
  }

}
