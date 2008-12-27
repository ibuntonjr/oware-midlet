/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* FIX game menu
	* FIX no getGraphics for GameScreen 1.0 for GameScreen
	* FIX no suppress keys for 1.0 for GameApp
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
	//#ifdef DLOGGING
  public static final int GA_LOGGING = GameApp.GA_USERDEF + 3;
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

	/* How many human players. */
  public static int gsPlayer = 1;
	/* Skill level. */
  final static public int gsLevelNormal = 1;
  final static public int gsLevelDifficult = 2;
  final static public int gsLevelHard = 3;
  public static int gsLevel = gsLevelDifficult;
	/* Dept.  Number of moves that the AI tests. */
  public static int gsDept = 3;
  public static int gsMaxHoles = OwareTable.NBR_COL;
  public static int gsGrandSlam = 1;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	final public LogManager logManager;
	private Logger logger;
	//#endif

  public OwareMIDlet() {
    super();
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
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_UNDO, (short)OwareMIDlet.GA_UNDO, 2
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_REDO, (short)OwareMIDlet.GA_REDO, 3
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
			OwareMIDlet.MSG_GRAND_SLAM2, OwareMIDlet.MSG_GRAND_SLAM2,
			OwareMIDlet.MSG_GRAND_SLAM3, OwareMIDlet.MSG_GRAND_SLAM4});
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

  public void doShowOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowOptions");}
		//#endif
		try {
			super.doShowOptions();
			opPlayers.setSelectedIndex(OwareMIDlet.gsPlayer - 1, true);
			opLevel.setSelectedIndex(OwareMIDlet.gsLevel - 1, true);
			opDept.setSelectedIndex(OwareMIDlet.gsDept - 1, true);
			opMaxHoles.setSelectedIndex(OwareMIDlet.gsMaxHoles - 1, true);
			opGrandSlam.setSelectedIndex(OwareMIDlet.gsGrandSlam, true);
			//#ifdef DLOGGING
			opLogLevel.setString(
							logger.getParent().getLevel().getName());
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
			OwareMIDlet.gsLevel = opLevel.getSelectedIndex() + 1;
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
						if (OwareScreen.table.checkLastTable()) {
							Application.insertMenuItem(gameMenu, GA_UNDO);
							canUndo = true;
						}
						if (OwareScreen.table.checkLastRedoTable()) {
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
					((OwareScreen)GameApp.game).table.undoTable();
					doGameResume();
					break;
				case GA_REDO: // Redo last move
					((OwareScreen)GameApp.game).table.redoTable();
					doGameResume();
					break;
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
