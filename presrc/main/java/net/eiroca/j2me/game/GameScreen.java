/** GPL >= 2.0
 *
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
@DMIDPVERS@
// Expand to define CLDC define
@DCLDCVERS@
package net.eiroca.j2me.game;

import javax.microedition.lcdui.Graphics;
//#ifdef DMIDP20
import javax.microedition.lcdui.game.GameCanvas;
//#else
import javax.microedition.lcdui.Canvas;
//#endif

/**
	* Support game screen.  Has graphics screen, width, height, animation thread.
	* Show, hide, store/restore data.
	*/
abstract public class GameScreen extends
//#ifdef DMIDP20
GameCanvas
//#else
Canvas
//#endif
{

  protected final GameApp midlet;
  protected final Graphics screen;
  protected final int screenWidth;
  protected final int screenHeight;
  protected final boolean fullScreenMode;

  protected boolean active = false;
  protected GameThread animationThread;
  public String name;
  public Score score;

  public GameScreen(final GameApp aMidlet, final boolean suppressKeys, final boolean fullScreen) {
    super(suppressKeys);
    midlet = aMidlet;
    fullScreenMode = fullScreen;
    setFullScreenMode(fullScreenMode);
    score = new Score();
    screen = getGraphics();
    screenWidth = screen.getClipWidth();
    screenHeight = screen.getClipHeight();
  }

  public void init() {
    active = true;
  }

  public void show() {
    setFullScreenMode(fullScreenMode);
	//#ifdef DCLDCV11
    animationThread = new GameThread(this, "GameScreen");
	//#else
    animationThread = new GameThread(this);
	//#endif
    animationThread.start();
  }

  abstract public boolean tick();

  public void hide() {
    if (animationThread != null) {
      animationThread.stopped = true;
      animationThread = null;
      setFullScreenMode(false);
    }
  }

  public void done() {
    hide();
    active = false;
    score.endGame();
  }

  public void wakeUp(int tries) {
	  animationThread.wakeUp(tries);
  }

  abstract public byte[] saveRecordStore();

  abstract public boolean loadRecordStore(final byte[] b);

  public final boolean isActive() {
    return active;
  }

}
