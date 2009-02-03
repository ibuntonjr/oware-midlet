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
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDCV10
package net.eiroca.j2me.game;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Game thread for animation.
	*/
public class GameThread extends Thread {

  private static final int MILLIS_PER_TICK = 100;
  private static final int MILLIS_PER_PAINT = 200;
  private static final int MILLIS_PER_MAX_PAINT = 400;

  public GameScreen screen;
  public boolean stopped = false;
  public int tries = 0;
	private int wait4repaint = 0;
	//#ifdef DMIDP10
//@	private long repaintLast = 0L;
	//#endif

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("OwareTable");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  public GameThread(final GameScreen canvas) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor canvas=" + canvas);}
		//#endif
    screen = canvas;
  }

	//#ifdef DCLDCV11
//@	// This isn't documented, but this is only available on CLDC >=1.1
//@  public GameThread(final GameScreen canvas, final String name) {
//@		super(name);
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor name=" + name);}
		//#endif
//@    screen = canvas;
//@  }
	//#endif

  public void run() {
    try {
			//#ifdef DLOGGING
//@			int logCount = 0;
			//#endif
      while (!stopped) {
        final long drawStartTime = System.currentTimeMillis();
        if (screen.isShown() && screen.tick()) {
						//#ifdef DMIDP20
            screen.flushGraphics();
						//#else
//@						screen.repaint();
						//#ifdef DLOGGING
//@						if (traceLoggable && ((logCount % 10) == 0)) {logger.trace("run");}
						//#endif
//@						synchronized (this) {
//@							wait4repaint++;
//@						}
						//#endif
				//#ifdef DLOGGING
//@				} else {
//@						if (traceLoggable && ((logCount % 10) == 0)) {logger.trace("run not shown screen.isShown()=" + screen.isShown());}
				//#endif
        }
        final long timeTaken = System.currentTimeMillis() - drawStartTime;
				synchronized (this) {
					if ((wait4repaint > 0) || (timeTaken < GameThread.MILLIS_PER_TICK)) {
							if ((wait4repaint > 0) || (tries-- <= 0)) {
								wait((wait4repaint > 0) ? GameThread.MILLIS_PER_PAINT :
											(GameThread.MILLIS_PER_TICK - timeTaken));
								//#ifdef DLOGGING
//@								if (traceLoggable) {
//@									if (logCount > 21) {
//@										logCount = 0;
//@									}
//@								}
								//#endif
								//#ifdef DMIDP10
//@								if (repaintLast == 0L) {
//@									repaintLast = timeTaken;
//@								} else if ((timeTaken - repaintLast) >
//@										GameThread.MILLIS_PER_MAX_PAINT) {
//@										repaintLast = 0L;
//@								}
								//#endif
							}
					}
					else {
						Thread.yield();
					}
				}
      }
    }
    catch (final InterruptedException e) {
      // Nothing to do
    }
		// Save memory
		// FIX? for midp 2.0 
    screen = null;
  }

	//#ifdef DMIDP10
//@	public void notifyPainted() {
//@		synchronized (this) {
//@			wait4repaint--;
//@		}
//@	}
	//#endif

	public void wakeup(int tries) {
		synchronized (this) {
			this.tries+= tries;
			super.notify();
		}
	}

}
