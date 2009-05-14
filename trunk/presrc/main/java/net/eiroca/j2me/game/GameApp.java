/** GPL >= 2.0
 *
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This software was modified 2008-12-07.  The original file was Reversi.java
 * in mobilesuite.sourceforge.net project.
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
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR135 define
@DJSR135@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
package net.eiroca.j2me.game;

import java.util.Vector;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
//#ifdef DJSR135
import javax.microedition.media.Player;
//#endif
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.SplashScreen;
import com.substanceofcode.rssreader.presentation.FeatureForm;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;


//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
  * Used to handle a game application.  This has the message numbers for
  * common commands (e.g. OK), messages (e.g. game over), menu names (e.g.
  * new game).  It also has resource names (images, and message next).
  * Also has RMS store name?.  Has reference to GameScreen, ScoreManager,
  * game menu, game settings, game options, game new high score.
  * Game actions startup/start, stop, abort, pause, resume,
  * abstract game action, help, about. options, high score, show settings,
  */
public abstract class GameApp extends Application {

  public static final int ME_MAINMENU = 0;

  public static final int GA_NONE = 0;
  public static final int GA_STARTUP = 1;
  public static final int GA_CONTINUE = 2;
  public static final int GA_NEWGAME = 3;
  public static final int GA_HIGHSCORE = 4;
  public static final int GA_OPTIONS = 5;
  public static final int GA_SETTINGS = 6;
  public static final int GA_HELP = 7;
  public static final int GA_ABOUT = 8;
  public static final int GA_APPLYSETTINGS = 9;
  public static final int GA_APPLYOPTIONS = 10;
  public static final int GA_NEWHIGHSCORE = 11;
  public static final int GA_USERDEF = 12;

  public static final int FT_SOUNDFX = 1;
  public static final int FT_MUSIC = 2;
  public static final int FT_AUDIO = GameApp.FT_MUSIC + GameApp.FT_SOUNDFX;
  public static final int FT_VIBRATE = 4;
  public static final int FT_LIGHT = 8;

  public static final String RES_MENUICON = "menu.png";
  public static final String RES_ABOUT = "about.txt";
  public static final String RES_HELP = "help.txt";
  public static final String RES_MSGS = "messages.txt";
  public static final String RES_SPLASH = "splash.png";
  public static String resSplash = RES_SPLASH;

  public static boolean usVibrate = true;
  public static boolean usBackLight = true;
  public static int usVolume = 100;
  public static String GRAPHICS_PROPERY = "boardgame-graphics";
  public static boolean graphics = true;

  public static String RMS_HIGHSCORE = "HighScore";
  public static String hsName = "HighScore";
  public static int hsLevel = 0;
  public static int hsMaxLevel = 1;
  public static int hsMaxScore = 5;

  public static GameScreen game = null;
  public static ScoreManager highscore;

  protected List gameMenu;
  protected Displayable gameSettings;
  protected Displayable gameOptions;
  protected Displayable gameNewHighScore;

  private TextField tName;
  private StringItem tScore;

	//#ifdef DLOGGING
	private Logger logger;
	private boolean fineLoggable;
	private boolean finestLoggable;
	//#endif

	//#ifdef DJMTEST
  public GameApp(String name) {
    super(name);
	}
	//#endif

  /**
   * Initialize the game
   */
  public void init() {
		//#ifdef DLOGGING
		logger = Logger.getLogger("GameApp");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		if (finestLoggable) {logger.finest("init");}
		//#endif
    super.init();
		try {
			final String gval = super.readAppProperty(GRAPHICS_PROPERY, "true");
			GameApp.graphics = gval.equals("true");
			BaseApp.messages = BaseApp.readStrings(GameApp.RES_MSGS);
			//#ifdef DMIDP20
			if (GameApp.graphics)
			//#else
			if (false)
			//#endif
			{
				BaseApp.icons = BaseApp.splitImages(GameApp.RES_MENUICON, 9, 12, 12);
			} else {
				BaseApp.icons = new Image[9];
				for (int i = 0; i < BaseApp.icons.length; i++) {
					BaseApp.icons[i] = null;
				}
			}
			//#ifdef DLOGGING
			logger.info("BaseApp.messages[AppConstants.MSG_GAMEAPP_USERDEF - 1]=" + BaseApp.messages[AppConstants.MSG_GAMEAPP_USERDEF - 1]);
			//#endif
			GameApp.highscore = new ScoreManager(GameApp.RMS_HIGHSCORE, GameApp.hsName, GameApp.hsMaxLevel, GameApp.hsMaxScore, true);
			GameApp.game = getGameScreen();
			BaseApp.cOK = BaseApp.newCommand(AppConstants.MSG_LABEL_OK, Command.OK, 30, BaseApp.AC_NONE);
			BaseApp.cBACK = BaseApp.newCommand(AppConstants.MSG_LABEL_BACK, Command.BACK, 20, BaseApp.AC_BACK);
			BaseApp.cEXIT = BaseApp.newCommand(AppConstants.MSG_LABEL_EXIT, Command.EXIT, 10, BaseApp.AC_EXIT);
			gameMenu = getGameMenu();
			processGameAction(GameApp.GA_STARTUP);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

  /**
   * Resume the game
   */
  public void resume() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("resume");}
		//#endif
    BaseApp.show(null, gameMenu, true);
  }

  /**
   * Pause the game
   */
  public void pause() {
    doGamePause();
  }

  /**
   * Destroy the game
   */
  public void done() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("done");}
		//#endif
    doGameAbort();
    BaseApp.closeRecordStores();
    super.done();
  }

  /**
   * The game is active?
   * @return
   */
  private final boolean isActive() {
    return (GameApp.game != null) && (GameApp.game.isActive());
  }

  /**
   * Command dispatcher
   */
  public void commandAction(final Command c, final Displayable d) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("commandAction c,d=" + c.getLabel() + "," + c + "," + d);}
		//#endif
		try {
			int gameAction = GameApp.GA_NONE;
			if (d == gameMenu) {
				int index = gameMenu.getSelectedIndex();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("commandAction index,isActive()=" + index + "," +  "," + isActive());}
				//#endif
				index = ((Integer)BaseApp.menuShown.elementAt(index)).intValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("commandAction modified index=" + index);}
				//#endif
				gameAction = BaseApp.menu[index][BaseApp.MD_MENUAC];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("commandAction mindex,gameAction=" + index + "," +  gameAction);}
				//#endif
			}
			//#ifdef DMIDP10
			else if (d == game) {
				process(c, d, null);
			}
			//#endif
			else if (c == BaseApp.cOK) {
				if (d == gameSettings) {
					gameAction = GameApp.GA_APPLYSETTINGS;
				}
				else if (d == gameOptions) {
					gameAction = GameApp.GA_APPLYOPTIONS;
				}
				else if (d == gameNewHighScore) {
					gameAction = GameApp.GA_NEWHIGHSCORE;
				}
			}
			if (c == BaseApp.cEXIT) {
				try {
					BaseApp.midlet.destroyApp(true);
				} catch (MIDletStateChangeException e) {
					e.printStackTrace();
				};
				BaseApp.midlet.notifyDestroyed();
			}
			else if (c == BaseApp.cBACK) {
				BaseApp.back(null);
			}
			else {
				processGameAction(gameAction);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("commandAction error", e);
			//#endif
		}
  }

  /**
   * Implements the game action
   * @param action
   */
  abstract public void processGameAction(int action);

  /**
   *
   */
  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
    return false;
  }

  /**
   * Game Startup
   */
  public void doStartup() {
    Displayable splash = null;
		if (GameApp.graphics) {
			splash = getSplash();
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doStartup splash=" + splash);}
		//#endif
    if (splash != null) {
      BaseApp.setDisplay(splash);
    }
    else {
      BaseApp.show(null, gameMenu, true);
    }
  }

  /**
   * Abort the game
   */
  public void doGameAbort() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGameAbort isActive()=" + isActive());}
		//#endif
		try {
			if (isActive()) {
				GameApp.game.done();
				prepGameMenu(isActive());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doGameAbort error", e);
			//#endif
		}
  }

  /**
   * Start the game.  Abort a previous game.  Prepare to continue if needed.
	 * Initialize the game and show it.
   */
  public void doGameStart() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGameStart");}
		//#endif
		try {
			doGameAbort();
			prepGameMenu(isActive());
			GameApp.game.init();
			GameApp.game.show();
			BaseApp.show(null, GameApp.game, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doGameStart error", e);
			//#endif
		}
  }

  /**
   * Resume the game
   */
  public void doGameResume() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGameResume isActive()=" + isActive());}
		//#endif
    if (isActive()) {
      GameApp.game.show();
      BaseApp.show(null, GameApp.game, true);
    }
  }

  /**
   * Pause the game
   */
  public void doGamePause() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGamePause isActive()=" + isActive());}
		//#endif
    if (isActive()) {
      GameApp.game.hide();
      BaseApp.back(null, gameMenu, true);
    }
  }

  /**
   * Stop the game
   */
  public void doGameStop() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doGameStop isActive()=" + isActive());}
		//#endif
    if (isActive()) {
      GameApp.game.hide();
      GameApp.game.done();
			prepGameMenu(isActive());
      int score = GameApp.game.score.getScore();
      Score hs = null;
      if (GameApp.hsLevel >= GameApp.hsMaxLevel) {
        score = 0;
      }
      else {
        hs = GameApp.highscore.getHighScore(GameApp.hsLevel);
      }
      if ((score > 0) && (GameApp.highscore.isHighScore(GameApp.hsLevel, GameApp.game.score))) {
        gameNewHighScore = getNewHighScore();
        final StringBuffer buf = new StringBuffer(80);
        buf.append(BaseApp.messages[AppConstants.MSG_TEXT_HIGHSCORE_03]).append(GameApp.game.score.getScore()).append(BaseApp.CR);
        tScore.setLabel(buf.toString());
        tName.setString(GameApp.game.score.name);
        BaseApp.setDisplay(gameNewHighScore);
      }
      else {
        final String[] msg = new String[3];
        msg[0] = BaseApp.messages[AppConstants.MSG_TEXT_GAMEOVER_01];
        if (score > 0) {
          msg[1] = BaseApp.messages[AppConstants.MSG_TEXT_GAMEOVER_02] + score;
        }
        if (hs != null) {
          msg[2] = BaseApp.messages[AppConstants.MSG_TEXT_GAMEOVER_03] + hs.getScore();
        }
        GameApp.flashBacklight(1000);
        BaseApp.setDisplay(new GameUIMessage(msg, gameMenu));
      }
    }
    else {
      BaseApp.back(null, gameMenu, false);
    }
  }

  /**
   * Set new high score
   */
  public void doSetNewHighScore() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doSetNewHighScore isActive()=" + isActive());}
		//#endif
    GameApp.game.score.name = tName.getString();
    GameApp.highscore.addNewScore(GameApp.hsLevel, GameApp.game.score);
    BaseApp.back(null, gameMenu, true);
  }

  /**
   * Show About
   */
  public void doAbout() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doAbout isActive()=" + isActive());}
		//#endif
    final Displayable d = BaseApp.getTextForm(AppConstants.MSG_MENU_MAIN_ABOUT, GameApp.RES_ABOUT);
    BaseApp.show(null, d, true);
  }

  /**
   * Show help
   */
  public void doHelp() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doHelp");}
		//#endif
    final Displayable d = BaseApp.getTextForm(AppConstants.MSG_MENU_MAIN_HELP, GameApp.RES_HELP);
    BaseApp.show(null, d, true);
  }

  /**
   * Show high score
   */
  public void doHighScore() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doHighScore");}
		//#endif
    final Displayable d = getHighScore();
    BaseApp.show(null, d, true);
  }

  /**
   * Show game options
   */
  public void doShowOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowOptions");}
		//#endif
    if (gameOptions == null) {
      gameOptions = getOptions();
    }
    BaseApp.show(null, gameOptions, true);
  }

  /**
   * Apply the game options
   */
  public void doApplyOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doApplyOptions");}
		//#endif
    BaseApp.back(null);
  }

  /**
   * Show game settings
   */
  public void doShowSettings() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doShowSettings gameSettings=" + gameSettings);}
		//#endif
    if (gameSettings == null) {
      gameSettings = getSettings();
    }
    GameUISettings.setVals();
    BaseApp.show(null, gameSettings, true);
  }

  /**
   * Apply new game settings
   */
  public void doApplySettings() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doApplySettings");}
		//#endif
    GameUISettings.getVals();
    BaseApp.back(null);
  }

  /**
   * Build Splash display
   * @return
   */
  protected Displayable getSplash() {
    return new SplashScreen(GameApp.resSplash, gameMenu, 3000);
  }

  /**
   * Build High score Display
   * @return
   */
  protected Displayable getHighScore() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("getHighScore");}
		//#endif
    final Form form = new FeatureForm(BaseApp.messages[AppConstants.MSG_MENU_MAIN_HIGHSCORE]);
    final Vector scores = GameApp.highscore.getList(GameApp.hsLevel);
    if (scores.size() == 0) {
      form.append(BaseApp.messages[AppConstants.MSG_TEXT_HIGHSCORE_01]);
    }
    else {
      Score s;
      for (int i = 0; i < scores.size(); i++) {
        s = (Score) scores.elementAt(i);
        final StringBuffer buf = new StringBuffer(80);
        buf.append((i + 1)).append(": ").append(s.getScore()).append(" (").append(s.name).append(')').append(BaseApp.CR);
        form.append(buf.toString());
      }
    }
    BaseApp.setup(form, BaseApp.cBACK, null);
    return form;
  }

  /**
   * Build new high score display
   * @return
   */
  protected Displayable getNewHighScore() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("getNewHighScore");}
		//#endif
    final Form form = new FeatureForm(BaseApp.messages[AppConstants.MSG_TEXT_HIGHSCORE_02]);
    tScore = new StringItem(BaseApp.messages[AppConstants.MSG_TEXT_HIGHSCORE_03], null);
    tName = new TextField(BaseApp.messages[AppConstants.MSG_TEXT_HIGHSCORE_04], "", 8,
		//#ifdef DMIDP20
				TextField.INITIAL_CAPS_SENTENCE);
		//#else
				TextField.ANY);
		//#endif
    form.append(tScore);
    form.append(tName);
    BaseApp.setup(form, BaseApp.cOK, null);
    return form;
  }

  /**
   * Build Options display
   * @return
   */
  protected Displayable getOptions() {
    return null;
  }

  /**
   * Build Settings display
   * @return
   */
  protected GameUISettings getSettings() {
    return null;
  }

  /**
   * Build main game display
   * @return
   */
  abstract protected GameScreen getGameScreen();

  /**
   * Build main game menu
   * @return
   */
  abstract protected List getGameMenu();

  /**
   * Handle preparation for presenting menu to the user
   */
  abstract protected void prepGameMenu(boolean canContinue);

  /**
   * Vibrate the handset
   * @param millis
   */
  public static void vibrate(final int millis) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("GameApp");
		logger.finest("vibrate millis,GameApp.usVibrate=" + millis + "," + GameApp.usVibrate);
		//#endif
    if (GameApp.usVibrate) {
			//#ifdef DMIDP20
      BaseApp.display.vibrate(millis);
			//#endif
    }
  }

  /**
   * Flash backlight
   * @param millis
   */
  public static void flashBacklight(final int millis) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("GameApp");
		logger.finest("flashBacklight millis=" + millis);
		//#endif
    if (GameApp.usBackLight) {
			//#ifdef DMIDP20
      BaseApp.display.flashBacklight(millis);
			//#endif
    }
  }

  //#ifdef DJSR135
  /**
   * Play a sound
   * @param p
   */
  public static void play(final Player p) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("GameApp");
		logger.finest("play p,GameApp.usVolume=" + p + "," + GameApp.usVolume);
		//#endif
    if (GameApp.usVolume > 0) {
      try {
        p.start();
      }
      catch (final Exception e) {
        // Nothing to do
				e.printStackTrace();
      }
    }
  }
  //#endif

}
