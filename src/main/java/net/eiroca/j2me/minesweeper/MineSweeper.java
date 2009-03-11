/** GPL >= 2.0
 * FIX
 * Based upon J2ME Minesweeper.
 * Copyright (C) M. Jumari
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
// Expand to define MIDP define
//#define DMIDP20
// Expand to define test define
//#define DNOTEST
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.minesweeper;

import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.GameUISettings;
import net.eiroca.j2me.game.Score;
import net.eiroca.j2me.minesweeper.CustomLevelForm;
import net.eiroca.j2me.minesweeper.MineSweeperScreen;
import net.eiroca.j2me.minesweeper.game.MineSweeperGame;
import net.eiroca.j2me.minesweeper.game.MineSweeperTable;
import com.substanceofcode.rssreader.presentation.FeatureForm;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
//@import net.sf.jlogmicro.util.logging.RecStoreHandler;
//@import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

public class MineSweeper extends BoardGameApp {

  public static String RES_ERROR = "error.png";
  public static String RES_SMILE = "smile.png";
  public static String RES_SMILEOK = "smile_ok.png";
  public static String RES_SMILEKO = "smile_ko.png";
  public static String RES_ICONS = "mine_icons.png";
  public static String RES_BOMB = "mine_bomb.wav";
  public static String RES_CLOCK = "mine_waktu.wav";

  public static final int MSG_MENU_OPTIONS_LEVEL = BoardGameApp.MSG_MINE_USERDEF + 0;

  public static final int MSG_TEXT_LEVEL_01 = BoardGameApp.MSG_MINE_USERDEF + 1;
  public static final int MSG_TEXT_LEVEL_02 = BoardGameApp.MSG_MINE_USERDEF + 2;
  public static final int MSG_TEXT_LEVEL_03 = BoardGameApp.MSG_MINE_USERDEF + 3;
  public static final int MSG_TEXT_LEVEL_04 = BoardGameApp.MSG_MINE_USERDEF + 4;

  public static final int MSG_NAME = BoardGameApp.MSG_MINE_USERDEF + 5;

  public static final int MSG_CUSTOMLEVEL = BoardGameApp.MSG_MINE_USERDEF + 6;
  public static final int MSG_CL_HEIGTH = BoardGameApp.MSG_MINE_USERDEF + 7;
  public static final int MSG_CL_WIDTH = BoardGameApp.MSG_MINE_USERDEF + 8;
  public static final int MSG_CL_BOMBS = BoardGameApp.MSG_MINE_USERDEF + 9;
  public static final int MSG_CL_ERR_HEIGHT = BoardGameApp.MSG_MINE_USERDEF + 10;
  public static final int MSG_CL_ERR_WIDTH = BoardGameApp.MSG_MINE_USERDEF + 11;
  public static final int MSG_CL_ERR_BOMBS = BoardGameApp.MSG_MINE_USERDEF + 12;
  public static final int MSG_CL_ERR_FRM = BoardGameApp.MSG_MINE_USERDEF + 13;
  public static final int MSG_CL_ERR_TO = BoardGameApp.MSG_MINE_USERDEF + 14;

  public static final int MSG_HS_LEVEL = BoardGameApp.MSG_MINE_USERDEF + 15;

  public static int usLevelInit = 0;
  public static int usLevel = 1;
  public static int usLevelLimit = -4;
  public static int usLevelIncr = 1;

  final static public String MINE_SWEEPER_US_LEVEL = "mine-sweeper-us-level";

  public static int width = MineSweeperTable.NBR_COL;
  public static int height = MineSweeperTable.NBR_ROW;
  public static int bomb = 9;

  private CustomLevelForm gameCustomLevel;

  private ChoiceGroup opDifficulty;

  private final Image iError;

	//#ifdef DLOGGING
//@  private boolean fineLoggable;
//@  private boolean finestLoggable;
//@  private boolean traceLoggable;
//@	private Logger logger;
	//#endif

  public MineSweeper() {
		//#ifdef DJMTEST
//@    super("MineSweeper Games Suite");
		//#else
    super();
		//#endif

		//#ifdef DLOGGING
//@		logger = Logger.getLogger("MineSweeper");
//@		fineLoggable = logger.isLoggable(Level.FINE);
//@		finestLoggable = logger.isLoggable(Level.FINEST);
//@		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif

    BaseApp.menu = new short[][] {
        {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_CONTINUE, GameApp.GA_CONTINUE, 0
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_NEWGAME, GameApp.GA_NEWGAME, 1
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HIGHSCORE, GameApp.GA_HIGHSCORE, 2
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 3
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_SETTINGS, GameApp.GA_SETTINGS, 4
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
        }
    };
    GameApp.hsName = "MineSweeper";
		BoardGameApp.storeName = "MINESWEEPER_GAME_STORE";
		GameApp.resSplash = "mine_splash.png";
    GameApp.hsMaxLevel = 3;
    GameApp.hsMaxScore = 1;
    iError = BaseApp.createImage(MineSweeper.RES_ERROR);
		setGameDefaults();
  }

	public void setGameDefaults() {
		MineSweeper.usLevelInit = 0;
		MineSweeper.usLevel = 1;
		MineSweeper.usLevelLimit = -4;
		MineSweeper.usLevelIncr = 1;
		BoardGameApp.gsDeptInit = 0;
		BoardGameApp.gsDeptLimit = 0;
		BoardGameApp.gsDept = 0;
		BoardGameApp.gsDeptIncr = 0;
		MineSweeper.height = 9;
		BoardGameApp.gsRowInit = MineSweeperGame.MINE_MIN_SIZE;
		BoardGameApp.gsRowLimit = -MineSweeperGame.MINE_MAX_SIZE;
		BoardGameApp.gsRow = MineSweeper.height;
		BoardGameApp.gsRowIncr = 1;
		MineSweeper.width = 9;
		BoardGameApp.gsColInit = MineSweeperGame.MINE_MIN_SIZE;
		BoardGameApp.gsCol = MineSweeper.width;
		BoardGameApp.gsColLimit = -MineSweeperGame.MINE_MAX_SIZE;
		BoardGameApp.gsColIncr = 1;
		BoardGameApp.gsNbrPlayers = 1;
		BoardGameApp.gsNbrPlayersLimit = 1;
	}

  protected GameUISettings getSettings() {
    return new GameUISettings(this, GameApp.FT_AUDIO);
  }

  protected Displayable getOptions() {
		final Form form = (Form)super.getOptions();
		if (MineSweeper.usLevelLimit < 0) {
			opDifficulty = new ChoiceGroup(BaseApp.messages[MineSweeper.MSG_MENU_OPTIONS_LEVEL], Choice.EXCLUSIVE);
			for (int i = 0; i < 4; i++) {
				opDifficulty.append(BaseApp.messages[MineSweeper.MSG_TEXT_LEVEL_01 + i], null);
			}
			form.append(opDifficulty);
		}
    return form;
  }

  public void doShowOptions() {
    super.doShowOptions();
		if (MineSweeper.usLevelLimit < 0) {
			opDifficulty.setSelectedIndex(MineSweeper.usLevel -
					MineSweeper.usLevelInit, true);
		}
  }

  public void doApplyOptions() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("doApplyOptions");}
		//#endif
		try {
			if (MineSweeper.usLevelLimit < 0) {
				MineSweeper.usLevel = Application.settingsUpd(
						opDifficulty.getSelectedIndex() + MineSweeper.usLevelInit, 
					MineSweeper.MINE_SWEEPER_US_LEVEL, MineSweeper.usLevel);
			}
			if (MineSweeper.usLevel == 3) {
				if (gameCustomLevel == null) {
					gameCustomLevel = new CustomLevelForm();
					BaseApp.setup(gameCustomLevel, BaseApp.cOK, null);
				}
				gameCustomLevel.setInputs();
				BaseApp.show(null, gameCustomLevel, false);
			}
			else {
				super.doApplyOptions();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

  public void doGameStart() {
    GameApp.hsLevel = MineSweeper.usLevel;
    super.doGameStart();
  }

  public Alert makeAlert(final String err, final int min, final int max) {
    return new Alert(BaseApp.messages[MineSweeper.MSG_CUSTOMLEVEL], err + BaseApp.messages[MineSweeper.MSG_CL_ERR_FRM] + min + BaseApp.messages[MineSweeper.MSG_CL_ERR_TO] + max, iError,
        AlertType.ERROR);
  }

  public void commandAction(final Command c, final Displayable d) {
    boolean processed = false;
    if (c == BaseApp.cOK) {
      if (d == gameCustomLevel) {
        processed = true;
        gameCustomLevel.getInputs();
        boolean ok = true;
        if (ok && ((MineSweeper.height < MineSweeperGame.MINE_MIN_SIZE) || (MineSweeper.height > MineSweeperGame.MINE_MAX_SIZE))) {
          ok = false;
          BaseApp.show(makeAlert(BaseApp.messages[MineSweeper.MSG_CL_ERR_HEIGHT], MineSweeperGame.MINE_MIN_SIZE, MineSweeperGame.MINE_MAX_SIZE), gameCustomLevel, false);
        }
        if (ok && ((MineSweeper.width < MineSweeperGame.MINE_MIN_SIZE) || (MineSweeper.width > MineSweeperGame.MINE_MAX_SIZE))) {
          ok = false;
          BaseApp.show(makeAlert(BaseApp.messages[MineSweeper.MSG_CL_ERR_WIDTH], MineSweeperGame.MINE_MIN_SIZE, MineSweeperGame.MINE_MAX_SIZE), gameCustomLevel, false);
        }
        int max = (MineSweeper.height - 1) * (MineSweeper.width - 1);
        if (max > MineSweeperGame.MAX_BOMB) {
          max = MineSweeperGame.MAX_BOMB;
        }
        if (ok && ((MineSweeper.bomb < MineSweeperGame.MIN_BOMB) || (MineSweeper.bomb > max))) {
          ok = false;
          BaseApp.show(makeAlert(BaseApp.messages[MineSweeper.MSG_CL_ERR_BOMBS], MineSweeperGame.MIN_BOMB, max), gameCustomLevel, false);
        }
        if (ok) {
					super.doApplyOptions();
          BaseApp.back(null);
        }
      }
    }
    if (!processed) {
      super.commandAction(c, d);
    }
  }

  protected Displayable getHighScore() {
    final Form form = new FeatureForm(BaseApp.messages[GameApp.MSG_MENU_MAIN_HIGHSCORE]);
		//#ifdef DMIDP20
    final Font f = Font.getFont(Font.STYLE_BOLD);
		//#else
//@    final Font df = Font.getDefaultFont();
//@    final Font f = Font.getFont(df.getFace(), Font.STYLE_BOLD, df.getSize());
		//#endif
    for (int l = 0; l < GameApp.hsMaxLevel; l++) {
      final Vector scores = GameApp.highscore.getList(l);
      final StringItem txt = new StringItem(BaseApp.messages[MineSweeper.MSG_HS_LEVEL] + BaseApp.messages[MineSweeper.MSG_TEXT_LEVEL_01 + l] + "\n", null);
			//#ifdef DMIDP20
      txt.setFont(f);
			//#endif
      form.append(txt);
      if (scores.size() == 0) {
        form.append(BaseApp.messages[GameApp.MSG_TEXT_HIGHSCORE_01]);
        form.append("\n");
      }
      else {
        Score s;
        for (int i = 0; i < scores.size(); i++) {
          s = (Score) scores.elementAt(i);
          form.append("" + (i + 1) + ": " + s.getScore() + " (" + s.name + ")\n");
        }
      }
    }
    BaseApp.setup(form, BaseApp.cBACK, null);
    return form;
  }

  public GameScreen getGameScreen() {
    return new MineSweeperScreen(this);
  }

  public void init() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			BoardGameApp.playerNames = new String[] {
					BaseApp.messages[net.eiroca.j2me.reversi.midlet.Reversi.MSG_UPPER],
					BaseApp.messages[net.eiroca.j2me.reversi.midlet.Reversi.MSG_LOWER]};
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("init error", e);
			//#endif
		}
  }

  public void processGameAction(final int action) {
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
      case GA_HIGHSCORE: // High score
        doHighScore();
        break;
      case GA_OPTIONS:
        doShowOptions();
        break;
      case GA_SETTINGS:
        doShowSettings();
        break;
      case GA_HELP:
        doHelp();
        break;
      case GA_ABOUT:
        doAbout();
        break;
      case GA_APPLYSETTINGS:
        doApplySettings();
        break;
      case GA_APPLYOPTIONS:
        doApplyOptions();
        break;
      case GA_NEWHIGHSCORE:
        doSetNewHighScore();
        break;
      default:
        break;
    }
  }

  public void loadBoardGameCustomization() {
		try {
			super.loadBoardGameCustomization();
			MineSweeper.usLevel = BaseApp.settings.getInt(
					MineSweeper.MINE_SWEEPER_US_LEVEL, MineSweeper.usLevel);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("loadBoardGameCustomization error", e);
			//#endif
		}
	}

}
