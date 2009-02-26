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
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.app;

import java.util.Timer;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.eiroca.j2me.util.ScheduledWaekup;
import net.eiroca.j2me.util.SchedulerNotify;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	Show splash screen (actually a canvas)
	*/
public class SplashScreen extends Canvas implements SchedulerNotify {

  protected Displayable next;
  private volatile boolean dismissed = false;
  private final int time;
  private Timer timer;
  private Image splashImage;

  //#ifdef DLOGGING
//@  private Logger logger = Logger.getLogger("SplashScreen");
//@  private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  //#endif
  public SplashScreen(final String image, final Displayable next, final int time) {
    this.next = next;
    this.time = time;
		//#ifdef DMIDP20
    setFullScreenMode(true);
		//#endif
    if (image != null) {
      splashImage = BaseApp.createImage(image);
    }
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("constructor image,time,splashImage=" + image + "," + time + "," + splashImage);}
		//#endif
    show();
  }

  public void show() {
    BaseApp.show(null, this, false);
  }

  public void hide() {
    BaseApp.show(null, next, true);
  }

  protected void dismiss() {
    if (!dismissed) {
      dismissed = true;
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
      hide();
    }
  }

  protected void keyPressed(final int keyCode) {
    dismiss();
  }

  protected void pointerPressed(final int x, final int y) {
    dismiss();
  }

  protected void showNotify() {
    timer = ScheduledWaekup.setup(this, time);
  }

  public void wakeup() {
    dismiss();
  }

  public void paint(final Graphics g) {
    final int width = getWidth();
    final int height = getHeight();
    g.setColor(BaseApp.background);
    g.fillRect(0, 0, width, height);
    if (splashImage != null) {
      g.drawImage(splashImage, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
    }
  }

}
