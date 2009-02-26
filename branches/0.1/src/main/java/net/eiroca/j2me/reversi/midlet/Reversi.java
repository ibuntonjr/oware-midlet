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
// Expand to define test define
//#define DNOTEST
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define logging define
//#define DNOLOGGING
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
import net.sf.yinlight.boardgame.oware.midlet.OwareMIDlet;

public class Reversi extends BoardGameApp {

  public static short msgOffset = 0;
  final public static int MSG_NAME = OwareMIDlet.MSG_USERDEF + msgOffset++; // 0

  public Reversi() {
	//#ifdef DJMTEST
//@    super("Reversi Games Suite");
	//#else
    super();
	//#endif
		BoardGameApp.gsRow = 8;
		BoardGameApp.gsCol = 8;
		BoardGameApp.gsNbrPlayers = 2;
		BoardGameApp.gsTextRow = 0;
		BoardGameApp.gsPieceImages = new String[0];
		BoardGameApp.gsLevelMsg = new int[0];
    GameApp.hsName = "Reversi";
		GameApp.resSplash = "reversi_splash.png";
  }

  public void init() {
    super.init();
  }

  public GameScreen getGameScreen() {
    return new ReversiScreen(this);
  }

  protected Displayable getOptions() {
    final Form form = (Form)super.getOptions();
    return form;
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
