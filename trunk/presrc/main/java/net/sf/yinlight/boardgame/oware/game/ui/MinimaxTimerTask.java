/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was MinimaxTimerTask.java
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
 */
package net.sf.yinlight.boardgame.oware.game.ui;

import java.util.TimerTask;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameTable;

/**
	* Task to run min/max algorithms.
	*/
public class MinimaxTimerTask extends TimerTask {

  public boolean ended;
  protected GameTable startTable;
  protected GameMinMax gMiniMax;

  public boolean cancel() {
    GameMinMax.cancel(true);
    return true;
  }

  public void run() {
    ended = false;
    gMiniMax.foreMinimax(OwareScreen.getActSkill(), startTable, OwareScreen.getActPlayer(), OwareScreen.rgame, true, 0, true, true);
    System.gc();
    ended = true;
  }

  public void setStartTable(final GameMinMax gMiniMax, final GameTable startTable) {
    this.startTable = startTable;
    this.gMiniMax = gMiniMax;
  }

}
