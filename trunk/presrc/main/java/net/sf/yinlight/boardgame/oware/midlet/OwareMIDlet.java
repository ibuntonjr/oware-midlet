/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* TODO optional skip first hole
	* FIX game menu
	* FIX Don't assume that a player owns the pits in his role to allow
	*     capture versions.
	* TODO do Riversi modifications
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
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;


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
  protected ChoiceGroup opMultiLap;
  protected ChoiceGroup opStartFirst;
  protected ChoiceGroup opSkipStarting;
  protected ChoiceGroup opSowStore;
  protected ChoiceGroup opCapture;
  protected ChoiceGroup opGrandSlam;
  protected ChoiceGroup opOpponentEmpty;

  final static public String OWARE_INIT_SEEDS = "oware-init-seeds";
  final static public String OWARE_GRAND_SLAM = "oware-grand-slam";
  final static public String OWARE_MAX_HOUSES = "oware-max-houses";
  final static public String OWARE_MULTI_LAP = "oware-multi-lap";
  final static public String OWARE_START_FIRST = "oware-start-first";
  final static public String OWARE_SKIP_STARTING = "oware-skip-starting";
  final static public String OWARE_SOW_STORE = "oware-sow-store";
  final static public String OWARE_CAPTURE = "oware-capture";
  final static public String OWARE_OPP_NO_SEEDS = "oware-opp-no-seeds";
	/* How many human players. */
	/* Skill level. */
  final static public int LEVEL_NORMAL = 0;
  final static public int LEVEL_DIFFICULT = 1;
  final static public int LEVEL_HARD = 2;
  public static int[] gsInitSeeds = new int[] {OwareTable.INIT_SEEDS,
			OwareTable.INIT_SEEDS, 1, -9, 1};
  public static int[] gsMultiLap = new int[] {0, 0, 0, -2, 1};
  public static int[] gsStartFirst = new int[] {0, 0, 0, -1, 1};
  public static int[] gsSkipStarting = new int[] {0, 0, 0, -1, 1};
  public static int[] gsSowStore = new int[] {0, 0, 0, -1, 1};
  public static int[] gsCapture = new int[] {1, 1, 0, -2, 1};
  public static int[] gsMaxHouses = new int[] {6, 6, 1, -8, 1};
  public static int[] gsGrandSlam = new int[]{ 0, 0, 0,
		-OwareGame.GRAND_SLAM_LEGAL_24, 1};
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
		BoardGameApp.gsLevelMsg = new int[] { AppConstants.MSG_AILEVEL1,
			AppConstants.MSG_AILEVEL2, AppConstants.MSG_AILEVEL3};
		BoardGameApp.gsSquareImages = new String[0];
		BoardGameApp.gsPiece1Images =
			new String[] {"oware_icon12.png", "oware_icon14.png", "oware_icon16.png",
				"oware_icon18.png", "oware_icon20.png"};
		BoardGameApp.gsPiece2Images = BoardGameApp.gsPiece1Images;
    GameApp.hsName = "Oware";
		GameApp.resSplash = "oware_splash.png";
		 BoardGameApp.gsTextRow = 2;
		BoardGameApp.gsDepth = new int[] {3, 3, 1, -14, 1};
		BoardGameApp.gsRow =  new int[] {2, 2, 2, -4, 2};
		// Unee has only 3 columns.
		BoardGameApp.gsCol =  new int[] {6, 6, 3, -9, 1};
		BoardGameApp.gsNbrPlayers =  new int[] {2, 2, 2, 2, 1};
		setGameDefaults();
  }

	public void setGameDefaults() {
		super.setGameDefaults();
		OwareMIDlet.gsMaxHouses[PD_CURR] = OwareMIDlet.gsMaxHouses[PD_DFLT];
		OwareMIDlet.gsMultiLap[PD_CURR] = OwareMIDlet.gsMultiLap[PD_DFLT];
		OwareMIDlet.gsStartFirst[PD_CURR] = OwareMIDlet.gsStartFirst[PD_DFLT];
		OwareMIDlet.gsSkipStarting[PD_CURR] = OwareMIDlet.gsSkipStarting[PD_DFLT];
		OwareMIDlet.gsSowStore[PD_CURR] = OwareMIDlet.gsSowStore[PD_DFLT];
		OwareMIDlet.gsCapture[PD_CURR] = OwareMIDlet.gsCapture[PD_DFLT];
		OwareMIDlet.gsInitSeeds[PD_CURR] = OwareMIDlet.gsInitSeeds[PD_DFLT];
		BoardGameApp.gsLevel = BoardGameApp.gsLevelDifficult;
	}

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			//#ifdef DLOGGING
			logger.info("BoardGameApp.precalculate=" + BoardGameApp.precalculate);
			logger.info("BaseApp.messages[AppConstants.MSG_OWARE_NAME]=" + BaseApp.messages[AppConstants.MSG_OWARE_NAME]);
			logger.info("BaseApp.messages[AppConstants.MSG_OWARE_USERDEF - 1]=" + BaseApp.messages[AppConstants.MSG_OWARE_USERDEF - 1]);
			//#endif
			// UNDO Fix pre calculate
			if (BoardGameApp.precalculate) {
				//#ifdef DLOGGING
				logger.severe("Having precalculate on (or mitted) causes aborts.");
				//#endif
				BoardGameApp.precalculate = false;
			}
			BoardGameApp.playerNames = new String[] {
					BaseApp.messages[AppConstants.MSG_NAMEPLAYER1], BaseApp.messages[AppConstants.MSG_NAMEPLAYER2]};
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
			opInitSeeds = BoardGameApp.createNumRangePD(OwareMIDlet.gsInitSeeds,
					AppConstants.MSG_INIT_SEEDS);
			opMaxHouses = BoardGameApp.createNumRangePD(OwareMIDlet.gsMaxHouses,
					AppConstants.MSG_MAX_HOUSES);
			opMultiLap = Application.createChoiceGroup(AppConstants.MSG_MULTI_LAP,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_ONE_LAP,
					AppConstants.MSG_1ST_LAP, AppConstants.MSG_2ND_LAP});
			opStartFirst = Application.createChoiceGroup(
					AppConstants.MSG_START_SOWING,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_SOW_NEXT,
					AppConstants.MSG_SOW_FIRST});
			opSkipStarting = Application.createChoiceGroup(AppConstants.MSG_LOOP_SKIP,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_SKIP_STARTING,
					AppConstants.MSG_SOW_STARTING});
			opSowStore = Application.createChoiceGroup(
					AppConstants.MSG_SOW_STORE_RULE,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_SKIP_STORE,
					AppConstants.MSG_SOW_STORE});
			opCapture = Application.createChoiceGroup(AppConstants.MSG_CAPTURE_RULES,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_CAPTURE_EMPTY,
					AppConstants.MSG_CAPTURE_2_3, AppConstants.MSG_CAPTURE_4});
			opGrandSlam = Application.createChoiceGroup(AppConstants.MSG_GRAND_SLAM,
					Choice.EXCLUSIVE, new int[] {AppConstants.MSG_GRAND_SLAM1,
					AppConstants.MSG_GRAND_SLAM2, AppConstants.MSG_GRAND_SLAM3,
					AppConstants.MSG_GRAND_SLAM4, AppConstants.MSG_GRAND_SLAM5});
			opOpponentEmpty = Application.createChoiceGroup(
					AppConstants.MSG_OPPONENT_EMPTY,
					Choice.EXCLUSIVE,
					new int[] { AppConstants.MSG_OPPONENT_EMPTY1,
			AppConstants.MSG_OPPONENT_EMPTY2});
			form.append(opInitSeeds);
			form.append(opMaxHouses);
			form.append(opMultiLap);
			form.append(opSkipStarting);
			form.append(opSowStore);
			form.append(opCapture);
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
			BoardGameApp.setSelectedChoicePD(opInitSeeds, OwareMIDlet.gsInitSeeds);
			BoardGameApp.setSelectedChoicePD(opMaxHouses, OwareMIDlet.gsMaxHouses);
			BoardGameApp.setSelectedChoicePD(opMultiLap, OwareMIDlet.gsMultiLap);
			BoardGameApp.setSelectedChoicePD(opSkipStarting, OwareMIDlet.gsSkipStarting);
			BoardGameApp.setSelectedChoicePD(opSowStore, OwareMIDlet.gsSowStore);
			BoardGameApp.setSelectedChoicePD(opCapture, OwareMIDlet.gsCapture);
			BoardGameApp.setSelectedChoicePD(opGrandSlam, OwareMIDlet.gsGrandSlam);
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
			BoardGameApp.settingsUpdPD(opInitSeeds, OwareMIDlet.gsInitSeeds,
					OwareMIDlet.OWARE_INIT_SEEDS);
			BoardGameApp.settingsUpdPD(opMaxHouses, OwareMIDlet.gsMaxHouses,
					OwareMIDlet.OWARE_MAX_HOUSES);
			BoardGameApp.settingsUpdPD(opMultiLap, OwareMIDlet.gsMultiLap,
					OwareMIDlet.OWARE_MULTI_LAP);
			BoardGameApp.settingsUpdPD(opSkipStarting, OwareMIDlet.gsSkipStarting,
					OwareMIDlet.OWARE_SKIP_STARTING);
			BoardGameApp.settingsUpdPD(opSowStore, OwareMIDlet.gsSowStore,
					OwareMIDlet.OWARE_SOW_STORE);
			BoardGameApp.settingsUpdPD(opCapture, OwareMIDlet.gsCapture,
					OwareMIDlet.OWARE_CAPTURE);
			BoardGameApp.settingsUpdPD(opGrandSlam, OwareMIDlet.gsGrandSlam,
					OwareMIDlet.OWARE_GRAND_SLAM);
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
			getIntPD(OwareMIDlet.OWARE_INIT_SEEDS, OwareMIDlet.gsInitSeeds); 
			getIntPD(OwareMIDlet.OWARE_GRAND_SLAM, OwareMIDlet.gsGrandSlam); 
			getIntPD(OwareMIDlet.OWARE_MAX_HOUSES, OwareMIDlet.gsMaxHouses); 
			getIntPD(OwareMIDlet.OWARE_MULTI_LAP, OwareMIDlet.gsMultiLap); 
			getIntPD(OwareMIDlet.OWARE_SKIP_STARTING, OwareMIDlet.gsSkipStarting); 
			getIntPD(OwareMIDlet.OWARE_SOW_STORE, OwareMIDlet.gsSowStore); 
			getIntPD(OwareMIDlet.OWARE_CAPTURE, OwareMIDlet.gsCapture); 
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
