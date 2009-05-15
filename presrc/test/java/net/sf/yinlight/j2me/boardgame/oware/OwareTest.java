/** GPL >= 2.0
	* FIX arc piece shape and size OwareScreen, no vibrate,flashBacklight for 1.0 for GameApp
	* FIX game menu
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
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
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
public class OwareTest {
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

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	final public LogManager logManager;
	private Logger logger;
	//#endif

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			OwareMIDlet.playerNames = new String[] {
					BaseApp.messages[AppConstants.MSG_NAMEPLAYER1], BaseApp.messages[AppConstants.MSG_NAMEPLAYER2]};
			if (first) {
				first = false;
				bsavedRec = ((OwareScreen)game).getSavedGameRecord();
			}
			prepGameMenu(bsavedRec.length > 0);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
  }

	//#ifdef DTEST
  protected Displayable getTesting() {
		try {
			final Form form = new FeatureForm(BaseApp.messages[AppConstants.MSG_MENU_MAIN_TEST]);
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

	//#endif

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

	//#ifdef DTEST
  public void doPerformTest() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("doPerformTest");}
		//#endif
		try {
			perfResult result = performTest(tstRow0.getString(), tstRow1.getString(),
					"6", "2",
			
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
                          String srow,
                          String scol,
                          String sexpRow0,
                          String sexpRow1, String smove) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("performTest");}
		//#endif
		perfResult result = new perfResult();
		try {
			BoardGameScreen.table.fromRowString(0, srow0);
			System.out.println("JUnit srow0:" + srow0);
			BoardGameScreen.table.fromRowString(1, srow1);
			System.out.println("JUnit srow1:" + srow1);
			OwareTable cmpTable = new OwareTable((OwareTable)BoardGameScreen.table);
			OwareTable newTable = (OwareTable)BoardGameScreen.table.getEmptyTable();
			System.out.println("JUnit smove:" + smove);
			OwareMove move = new OwareMove(
					Integer.valueOf(smove.substring(0, 1)).intValue(),
			Integer.valueOf(smove.substring(1)).intValue());
			OwareScreen os = (OwareScreen)GameApp.game;
			(os).actPlayer = (byte)move.row;
			((OwareScreen)GameApp.game).actPlayer = (byte)move.row;
			OwareGame og = (OwareGame)((OwareScreen)GameApp.game).rgame;
			OwareTable ot = (OwareTable)os.table;
			og.setPlayer((byte)move.row);
			result.mveResult = ((OwareScreen)GameApp.game).processMove(move, false);
			cmpTable.fromRowString(0, sexpRow0);
			System.out.println("JUnit sexpRow0:" + sexpRow0);
			cmpTable.fromRowString(1, sexpRow1);
			System.out.println("JUnit sexpRow1:" + sexpRow1);
			result.success = BoardGameScreen.table.equals(cmpTable);
			result.gameOver = ((OwareScreen)GameApp.game).rgame.isGameEnded(
					og, ot, (os).actPlayer);
			result.actRow0 = BoardGameScreen.table.toRowString(0);
			result.actRow1 = BoardGameScreen.table.toRowString(1);
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

}
//#endif
