/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* FIX game menu
	* FIX Don't assume that a player owns the pits in his role to allow
	*     capture versions.
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
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import com.substanceofcode.rssreader.presentation.FeatureForm;
import com.substanceofcode.rssreader.presentation.FeatureMgr;


//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

/**
	* Oware game application.  Handle game options in addition to standard app
	* options on form.  Save options.
	*/ 
public class OwareMIDlet extends BoardGameApp {

  protected ChoiceGroup opInitSeeds;
  protected ChoiceGroup opMaxHouses;
  protected ChoiceGroup opGrandSlam;
  protected ChoiceGroup opOpponentEmpty;

  final static public String OWARE_INIT_SEEDS = "oware-init-seeds";
  final static public String OWARE_GRAND_SLAM = "oware-grand-slam";
  final static public String OWARE_MAX_HOUSES = "oware-max-houses";
  final static public String OWARE_OPP_NO_SEEDS = "oware-opp-no-seeds";
	/* How many human players. */
	/* Skill level. */
  final static public int LEVEL_NORMAL = 0;
  final static public int LEVEL_DIFFICULT = 1;
  final static public int LEVEL_HARD = 2;
	/* Dept.  Number of moves that the AI tests. */
  public static int gsInitSeedsInit = 1;
  public static int gsInitSeeds = 4;
  public static int gsInitSeedsLimit = 6;
  public static int gsInitSeedsIncr = 1;
  public static int gsMaxHousesInit = OwareTable.NBR_COL;
  public static int gsMaxHouses = OwareTable.NBR_COL;
  public static int gsMaxHousesLimit = OwareTable.NBR_COL;
  public static int gsMaxHousesIncr = 1;
  public static int gsGrandSlam = 0;
  public static boolean gsOpponentEmpty = true;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	private Logger logger;
	//#endif

  public OwareMIDlet() {
		//#ifdef DJMTEST
    super("Mancala Test Suite");
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
		logger = Logger.getLogger("OwareMIDlet");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
		BoardGameApp.storeName = "OWARE_GAME_STORE";
		BoardGameApp.gsLevelMsg = new int[] { BoardGameApp.MSG_AILEVEL1,
			BoardGameApp.MSG_AILEVEL2, BoardGameApp.MSG_AILEVEL3};
		BoardGameApp.gsSquareImages = new String[0];
		BoardGameApp.gsPiece1Images =
			new String[] {"icon12.png","icon14.png","icon16.png","icon18.png",
			"icon20.png"};
		BoardGameApp.gsPiece2Images = BoardGameApp.gsPiece1Images;
    GameApp.hsName = "Oware";
		GameApp.resSplash = "oware_splash.png";
		setGameDefaults();
  }

	public void setGameDefaults() {
		BoardGameApp.gsRowInit = 2;
		BoardGameApp.gsRow = 2;
		BoardGameApp.gsRowLimit = -4;
		BoardGameApp.gsRowIncr = 2;
		BoardGameApp.gsColInit = 4;
		BoardGameApp.gsCol = 6;
		BoardGameApp.gsColLimit = -8;
		BoardGameApp.gsColIncr = 1;
		BoardGameApp.gsNbrPlayers = 2;
		BoardGameApp.gsNbrPlayersLimit = 2;
		BoardGameApp.gsTextRow = 2;
		OwareMIDlet.gsInitSeedsInit = 1;
		OwareMIDlet.gsInitSeeds = OwareTable.INIT_SEEDS;
		OwareMIDlet.gsInitSeedsLimit = -6;
		OwareMIDlet.gsInitSeedsIncr = 1;
		BoardGameApp.gsLevel = BoardGameApp.gsLevelDifficult;
		OwareMIDlet.gsMaxHousesInit = 1;
		OwareMIDlet.gsMaxHouses = BoardGameApp.gsCol;
		OwareMIDlet.gsMaxHousesLimit = -Math.abs(BoardGameApp.gsColLimit);
		OwareMIDlet.gsMaxHousesIncr = 1;
	}

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			//#ifdef DLOGGING
			logger.info("BoardGameApp.precalculate=" + BoardGameApp.precalculate);
			//#endif
			// UNDO Fix pre calculate
			if (BoardGameApp.precalculate) {
				//#ifdef DLOGGING
				logger.severe("Having precalculate on (or mitted) causes aborts.");
				//#endif
				BoardGameApp.precalculate = false;
			}
			BoardGameApp.playerNames = new String[] {
					BaseApp.messages[BoardGameApp.MSG_NAMEPLAYER1], BaseApp.messages[BoardGameApp.MSG_NAMEPLAYER2]};
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
			OwareScreen ows = new OwareScreen(this, false, true);
			//#ifdef DMIDP10
			updGameScreen(ows);
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

  protected Displayable getOptions() {
		try {
			final Form form = (Form)super.getOptions();
			if (OwareMIDlet.gsInitSeedsLimit < 0) {
				opInitSeeds = Application.createNumRange(AppConstants.MSG_INIT_SEEDS,
						OwareMIDlet.gsInitSeedsInit, OwareMIDlet.gsInitSeedsLimit,
						OwareMIDlet.gsInitSeedsIncr);
			}
			opMaxHouses = Application.createNumRange(AppConstants.MSG_MAX_HOUSES,
					OwareMIDlet.gsMaxHousesInit, OwareMIDlet.gsMaxHousesLimit,
					OwareMIDlet.gsMaxHousesIncr);
			opGrandSlam = Application.createChoiceGroup(
					AppConstants.MSG_GRAND_SLAM,
					Choice.EXCLUSIVE,
					new int[] { AppConstants.MSG_GRAND_SLAM1,
			AppConstants.MSG_GRAND_SLAM2, AppConstants.MSG_GRAND_SLAM3,
			AppConstants.MSG_GRAND_SLAM4, AppConstants.MSG_GRAND_SLAM5});
			opOpponentEmpty = Application.createChoiceGroup(
					AppConstants.MSG_OPPONENT_EMPTY,
					Choice.EXCLUSIVE,
					new int[] { AppConstants.MSG_OPPONENT_EMPTY1,
			AppConstants.MSG_OPPONENT_EMPTY2});
			if (OwareMIDlet.gsInitSeedsLimit < 0) {
				form.append(opInitSeeds);
			}
			form.append(opMaxHouses);
			form.append(opGrandSlam);
			form.append(opOpponentEmpty);
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
			if (OwareMIDlet.gsInitSeedsLimit < 0) {
				opInitSeeds.setSelectedIndex(OwareMIDlet.gsInitSeeds -
						OwareMIDlet.gsInitSeedsInit, true);
			}
			if (OwareMIDlet.gsMaxHousesLimit < 0) {
				opMaxHouses.setSelectedIndex(OwareMIDlet.gsMaxHouses -
						OwareMIDlet.gsMaxHousesInit, true);
			}
			opGrandSlam.setSelectedIndex(OwareMIDlet.gsGrandSlam, true);
			opOpponentEmpty.setSelectedIndex(
					OwareMIDlet.gsOpponentEmpty ? 1 : 0, true);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doShowOptions error", e);
			//#endif
		}
  }

  public void doApplyOptions() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doApplyOptions");}
		//#endif
		try {
			super.doApplyOptions();
			if (OwareMIDlet.gsInitSeedsLimit < 0) {
				OwareMIDlet.gsInitSeeds = Application.settingsUpd(
						opInitSeeds.getSelectedIndex() + 1, 
					OwareMIDlet.OWARE_INIT_SEEDS, OwareMIDlet.gsInitSeeds);
			}
			if (OwareMIDlet.gsMaxHousesLimit < 0) {
				OwareMIDlet.gsMaxHouses = Application.settingsUpd(
						opMaxHouses.getSelectedIndex() + 1, 
					OwareMIDlet.OWARE_MAX_HOUSES, OwareMIDlet.gsMaxHouses);
			}
			OwareMIDlet.gsGrandSlam = Application.settingsUpd(
				opGrandSlam.getSelectedIndex(),
				OwareMIDlet.OWARE_GRAND_SLAM, OwareMIDlet.gsGrandSlam);
			OwareMIDlet.gsOpponentEmpty = (Application.settingsUpd(
				opOpponentEmpty.getSelectedIndex(),
				OwareMIDlet.OWARE_OPP_NO_SEEDS,
				(OwareMIDlet.gsOpponentEmpty ? 1 : 0)) == 1);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("doApplyOptions error", e);
			//#endif
		}
  }

  public void loadBoardGameCustomization() {
		try {
			super.loadBoardGameCustomization();
			OwareMIDlet.gsInitSeeds = BaseApp.settings.getInt(OwareMIDlet.OWARE_INIT_SEEDS, OwareMIDlet.gsInitSeeds);
			OwareMIDlet.gsGrandSlam = BaseApp.settings.getInt(OwareMIDlet.OWARE_GRAND_SLAM, OwareMIDlet.gsGrandSlam);
			OwareMIDlet.gsMaxHouses = BaseApp.settings.getInt(OwareMIDlet.OWARE_MAX_HOUSES, OwareMIDlet.gsMaxHouses);
			OwareMIDlet.gsOpponentEmpty = (BaseApp.settings.getInt(OwareMIDlet.OWARE_OPP_NO_SEEDS,  (OwareMIDlet.gsOpponentEmpty ? 1 : 0)) == 1);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("loadBoardGameCustomization error", e);
			//#endif
		}
	}

  /**
   * Game Shutdown
   */
  public void doShutdown() {
  }

}
