/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
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
// Expand to define logging define
@DLOGDEF@
package net.eiroca.j2me.reversi.midlet;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.reversi.ui.ReversiScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameScreen;
import net.sf.yinlight.boardgame.oware.game.BoardGameApp;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

public class Reversi extends BoardGameApp {


	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	private Logger logger;
	//#endif

  public Reversi() {
		//#ifdef DJMTEST
    super("Reversi Games Suite");
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
		logger = Logger.getLogger("Reversi");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
		BoardGameApp.gsTextRow = 0;
		BoardGameApp.gsSquareImages =
			new String[] {"reversi_board12.png","reversi_board14.png",
				"reversi_board16.png","reversi_board18.png","reversi_board20.png",
			"reversi_board22.png","reversi_board24.png"};
		BoardGameApp.gsPiece1Images =
			new String[] {"reversi_black_btn12.png","reversi_black_btn14.png",
				"reversi_black_btn16.png","reversi_black_btn18.png","reversi_black_btn20.png",
			"reversi_black_btn22.png","reversi_black_btn24.png"};
		BoardGameApp.gsPiece2Images =
			new String[] {"reversi_white_btn12.png","reversi_white_btn14.png",
				"reversi_white_btn16.png","reversi_white_btn18.png","reversi_white_btn20.png",
			"reversi_white_btn22.png","reversi_white_btn24.png"};
		BoardGameApp.storeName = "REVERSI_GAME_STORE";
		BoardGameApp.gsLevelMsg = new int[0];
    GameApp.hsName = "Reversi";
		GameApp.resSplash = "reversi_splash.png";
		BoardGameApp.gsDepth = new int[] {3, 3, 1, -14, 1};
		BoardGameApp.gsRow =  new int[] {8, 8, 4, 8, 1};
		BoardGameApp.gsCol =  new int[] {8, 8, 4, 8, 1};
		BoardGameApp.gsNbrPlayers =  new int[] {2, 2, 2, 2, 1};
		setGameDefaults();
  }

  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			super.init();
			//#ifdef DLOGGING
			logger.info("BaseApp.messages[AppConstants.MSG_REVERSI_NAME]=" + BaseApp.messages[AppConstants.MSG_REVERSI_NAME]);
			logger.info("BaseApp.messages[AppConstants.MSG_REVERSI_USERDEF - 1]=" + BaseApp.messages[AppConstants.MSG_REVERSI_USERDEF - 1]);
			//#endif
			BoardGameApp.playerNames = new String[] {
					BaseApp.messages[AppConstants.MSG_UPPER],
					BaseApp.messages[AppConstants.MSG_LOWER]};
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
			ReversiScreen rs = new ReversiScreen(this);
			//#ifdef DMIDP10
			updGameScreen(rs);
			//#endif
			return rs;
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
			return form;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("getGameScreen error", e);
			//#endif
			return null;
		}
  }

  public void doShowOptions() {
    super.doShowOptions();
  }

  public void doApplyOptions() {
    super.doApplyOptions();
  }

  public void doGameAbort() {
    super.doGameAbort();
  }

}
