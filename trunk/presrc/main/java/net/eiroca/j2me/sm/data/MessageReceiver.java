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
/**
 * Modification started 2009-05-13.
 */
// Expand to define JSR-120 test define
@DJSR120@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR120
package net.eiroca.j2me.sm.data;

public class MessageReceiver extends Thread {

  MessageHandler handler;

  public MessageReceiver(final MessageHandler handler) {
    super();
    this.handler = handler;
  }

  /**
   * Implementation of runnable interface
   */
  public void run() {
    handler.receive();
  }

}
//#endif
