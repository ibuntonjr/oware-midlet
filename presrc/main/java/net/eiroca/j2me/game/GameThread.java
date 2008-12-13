/** GPL >= 2.0
 *
 * This software was modified 2008-12-07.  The file is GameThread.java
 * in mobilesuite.sourceforge.net project.

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
// Expand to define CLDC define
@DCLDCVERS@
package net.eiroca.j2me.game;

/**
	* Game thread for animation.
	*/
public class GameThread extends Thread {

  private static final int MILLIS_PER_TICK = 50;

  public GameScreen screen;
  public boolean stopped = false;
  public int tries = 0;

  public GameThread(final GameScreen canvas) {
    screen = canvas;
  }

	//#ifdef DCLDCV11
	// This isn't documented, but this is only available on CLDC >=1.1
  public GameThread(final GameScreen canvas, final String name) {
		super(name);
    screen = canvas;
  }
	//#endif

  public void run() {
    try {
      while (!stopped) {
        final long drawStartTime = System.currentTimeMillis();
        if (screen.isShown()) {
          if (screen.tick()) {
            screen.flushGraphics();
          }
        }
        final long timeTaken = System.currentTimeMillis() - drawStartTime;
        if (timeTaken < GameThread.MILLIS_PER_TICK) {
          synchronized (this) {
						if (tries-- > 0) {
							wait(GameThread.MILLIS_PER_TICK - timeTaken);
						}
          }
        }
        else {
          Thread.yield();
        }
      }
    }
    catch (final InterruptedException e) {
      // Nothing to do
    }
    screen = null;
  }

	public void wakeUp(int tries) {
		synchronized (this) {
			this.tries = tries;
			super.notify();
		}
	}

}
