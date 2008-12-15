/** GPL >= 2.0
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
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.sf.yinlight.boardgame.oware.game.ui.OwareScreen;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
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

  public static final short GA_UNDO = (short)GameApp.GA_USERDEF + 0;
  public static final short GA_REDO = (short)GameApp.GA_USERDEF + 1;

  public static String[] playerNames;

  protected ChoiceGroup opPlayers;
  protected ChoiceGroup opLevel;
  protected ChoiceGroup opDept;

	/* How many human players. */
  public static int gsPlayer = 1;
	/* Skill level. */
  final static public int gsLevelNormal = 1;
  final static public int gsLevelDifficult = 2;
  final static public int gsLevelHard = 3;
  public static int gsLevel = gsLevelDifficult;
	/* Dept.  Number of moves that the AI tests. */
  public static int gsDept = 3;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
	private Logger logger;
	//#endif

  public OwareMIDlet() {
    super();
		//#ifdef DLOGGING
		LogManager.getLogManager().readConfiguration(this);
		logger = Logger.getLogger("OwareMIDlet");
		//#endif
    BaseApp.menu = new short[][] {
        {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_CONTINUE, GameApp.GA_CONTINUE, 0
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_NEWGAME, GameApp.GA_NEWGAME, 1
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_UNDO, OwareMIDlet.GA_UNDO, 2
        }, {
            GameApp.ME_MAINMENU, OwareMIDlet.MSG_MENU_MAIN_REDO, OwareMIDlet.GA_REDO, 3
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 4
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
        }
    };
    GameApp.hsName = "OwareMIDlet";
  }

  public void init() {
		try {
			super.init();
			OwareMIDlet.playerNames = new String[] {
					BaseApp.messages[OwareMIDlet.MSG_NAMEPLAYER1], BaseApp.messages[OwareMIDlet.MSG_NAMEPLAYER2]
			};
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

  public GameScreen getGameScreen() {
		try {
			return new OwareScreen(this);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getGameScreen error", e);
			//#endif
			return null;
		}
  }

  protected Displayable getOptions() {
		try {
			final Form form = new Form(BaseApp.messages[GameApp.MSG_MENU_MAIN_OPTIONS]);
			opPlayers = new ChoiceGroup(BaseApp.messages[OwareMIDlet.MSG_GAMEMODE], Choice.EXCLUSIVE);
			opPlayers.append(BaseApp.messages[OwareMIDlet.MSG_GAMEMODE1], null);
			opPlayers.append(BaseApp.messages[OwareMIDlet.MSG_GAMEMODE2], null);
			opLevel = new ChoiceGroup(BaseApp.messages[OwareMIDlet.MSG_AILEVEL], Choice.EXCLUSIVE);
			opLevel.append(BaseApp.messages[OwareMIDlet.MSG_AILEVEL1], null);
			opLevel.append(BaseApp.messages[OwareMIDlet.MSG_AILEVEL2], null);
			opLevel.append(BaseApp.messages[OwareMIDlet.MSG_AILEVEL3], null);
			/* FIX
			opLevel.append(BaseApp.messages[OwareMIDlet.MSG_AILEVEL4], null);
			*/
			opDept = new ChoiceGroup(BaseApp.messages[OwareMIDlet.MSG_AILEVEL], Choice.POPUP);
			for (int i = 1; i <= 14; i++) {
				opDept.append(Integer.toString(i), null);
			}
			form.append(opPlayers);
			form.append(opLevel);
			form.append(opDept);
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
		try {
			super.doShowOptions();
			opPlayers.setSelectedIndex(OwareMIDlet.gsPlayer - 1, true);
			opLevel.setSelectedIndex(OwareMIDlet.gsLevel - 1, true);
			opDept.setSelectedIndex(OwareMIDlet.gsDept - 1, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doShowOptions error", e);
			//#endif
		}
  }

  public void doApplyOptions() {
		try {
			OwareMIDlet.gsPlayer = opPlayers.getSelectedIndex() + 1;
			OwareMIDlet.gsLevel = opLevel.getSelectedIndex() + 1;
			OwareMIDlet.gsDept = opDept.getSelectedIndex() + 1;
			((OwareScreen) GameApp.game).updateSkillInfo();
			super.doApplyOptions();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

  public void doGameAbort() {
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

  public void processGameAction(final int action) {
		try {
			switch (action) {
				case GA_STARTUP: // Continue
					doStartup();
					break;
				case GA_CONTINUE: // Continue
					doGameResume();
					break;
				case GA_NEWGAME: // New game
					doGameStart();
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
