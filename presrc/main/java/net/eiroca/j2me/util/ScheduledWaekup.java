/** GPL >= 2.0
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
// Expand to define logging define
@DLOGDEF@
package net.eiroca.j2me.util;

import java.util.Timer;
import java.util.TimerTask;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Timer task.  
	*/
public class ScheduledWaekup extends TimerTask {

  public static Timer setup(final SchedulerNotify listener, final int delay) {
    final Timer myTimer = new Timer();
    myTimer.schedule(new ScheduledWaekup(listener), delay);
		//#ifdef DLOGGING
    Logger logger = Logger.getLogger("ScheduledWaekup");
    logger.finest("Timer scheduled");
		//#endif
    return myTimer;
  }

  private final SchedulerNotify listener;

  public ScheduledWaekup(final SchedulerNotify listener) {
    this.listener = listener;
  }

  public void run() {
    listener.wakeup();
  }

}
