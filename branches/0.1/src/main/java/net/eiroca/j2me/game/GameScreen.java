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
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDCV10
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#ifdef DMIDP20
import javax.microedition.lcdui.game.GameCanvas;
//#else
//@import javax.microedition.lcdui.Canvas;
//#endif

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Support game screen.  Has graphics screen, width, height, animation thread.
	* Show, hide, store/restore data.
	*/
abstract public class GameScreen extends
//#ifdef DMIDP20
GameCanvas
//#else
//@Canvas
//#endif
{

  protected final GameApp midlet;
  protected Graphics screen = null;
	//#ifdef DMIDP10
//@  protected Image simage;
	//#endif
  protected int screenWidth;
  protected int screenHeight;
  protected final boolean fullScreenMode;

  protected boolean active = false;
  protected GameThread animationThread;
  public String name;
  public Score score;

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("GameScreen");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  //#endif

  public GameScreen(final GameApp aMidlet, final boolean suppressKeys, final boolean fullScreen) {
		//#ifdef DMIDP20
    super(suppressKeys);
		//#else
//@    super();
		//#endif
    midlet = aMidlet;
    fullScreenMode = fullScreen;
		//#ifdef DMIDP20
    super.setFullScreenMode(fullScreenMode);
		//#endif
    score = new Score();
	}

	//#ifdef DMIDP10
//@  /**
//@   * Initialize graphics portion only. This is for MIDP 1.0 devices which
//@	 * do not get screen info until paint.  It is also used by MIDP 2.x
//@	 * to be consistent.  So, we allow it to be called from 2 places.
//@   */
//@	public Graphics initGraphics(int x, int y) {
//@		simage = Image.createImage(x, y);
//@		Graphics cscreen = simage.getGraphics();
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics simage,cscreen=" + simage + "," + cscreen);}
		//#endif
//@    screenWidth = x;
//@    screenHeight = y;
//@		return cscreen;
//@  }
	//#endif

  /**
   * Initialize graphics portion only. This is for MIDP 1.0 devices which
	 * do not get screen info until paint.  It is also used by MIDP 2.x
	 * to be consistent.  So, we allow it to be called from 2 places.
   */
	public Graphics initGraphics() {
		//#ifdef DMIDP20
		Graphics cscreen = getGraphics();
    screenWidth = cscreen.getClipWidth();
    screenHeight = cscreen.getClipHeight();
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("initGraphics cscreen,screenWidth,screenHeight=" + cscreen + "," + screenWidth + "," + screenHeight);}
		//#endif
		return cscreen;
		//#else
//@		return initGraphics(super.getWidth(), super.getHeight());
		//#endif
	}

	//#ifdef DMIDP10
//@  /**
//@   * Initialize graphics portion only. This is for MIDP 1.0 devices which
//@	 * do not get screen info until paint.  So, we allow it to be called from
//@	 * 2 places.
//@   */
//@	public void sizeChanged(int x, int y) {
//@		screen = initGraphics(x, y);
//@  }
	//#endif

  public void init() {
    active = true;
    if (screen == null) {
			screen = initGraphics();
		}
  }

  public void show() {
		//#ifdef DMIDP20
    super.setFullScreenMode(fullScreenMode);
		//#endif
		synchronized(this) {
			//#ifdef DCLDCV11
//@			animationThread = new GameThread(this, "TGameScreen");
			//#else
			animationThread = new GameThread(this);
			//#endif
		}
    animationThread.start();
  }

  abstract public boolean tick();

  /**
   * Executed when hidden.  Stop animation thread, turn off full screen.
   */
  public void hide() {
		GameThread canimationThread = null;
		synchronized(this) {
			canimationThread = animationThread;
		}
    if (canimationThread != null) {
      canimationThread.stopped = true;
			synchronized(this) {
				animationThread = null;
			}
			//#ifdef DMIDP20
      setFullScreenMode(false);
			//#endif
    }
  }

  public void done() {
    hide();
    active = false;
    score.endGame();
  }

  public void wakeup(int tries) {
		synchronized(this) {
			if (animationThread != null) {
				animationThread.wakeup(tries);
			}
		}
  }

  public final boolean isActive() {
    return active;
  }

}
