/** GPL >= 2.0
 * Based upon SecureMessenger
 * Copyright (C) 2002 Eugene Morozov
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

import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;
import net.eiroca.j2me.sm.util.StoreFilterByID;
import net.eiroca.j2me.sm.util.StoreObserver;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Implementation of the MessageStore interface. Note: this implementation may be platform-specific even if it uses the RMS.
 */
public final class SecureMessageStore extends Store {

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("SecureMessageStore");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  /**
   * Creates new <code>MessageStoreImpl</code> instance. The additional string parameter allows us to create different MessageStores (different folders).
   * @param storeName The store name
   */
  public SecureMessageStore(final String storeName) throws StoreException {
    super(storeName);
  }

  public void store(final SecureMessage message) throws StoreException {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("store message.date,message.number,message.date,message.unread=" + message.date + "," + message.number + "," + message.date + "," + message.unread);}
		//#endif
    replaceFirst(new StoreFilterByID(message.date), message.serialize());
    notifyAction(StoreObserver.ADD, message);
  }

  public SecureMessage remove(final long id) throws StoreException {
    final SecureMessage message = getById(id);
    removeFirst(new StoreFilterByID(id));
    notifyAction(StoreObserver.DEL, message);
    return message;
  }

  public SecureMessage getById(final long id) throws StoreException {
    final byte[] res = findFirst(new StoreFilterByID(id));
    if (res == null) { throw new MessageHandlerException(MessageHandlerException.ERR_NOMESSAGE); }
		//#ifdef DLOGGING
		if (finestLoggable) {SecureMessage msg = new SecureMessage(res);logger.finest("getById id,msg.number,msg.date,msg.unread=" + id + "," + msg.number + "," + msg.date + "," + msg.unread);}
		//#endif
    return new SecureMessage(res);
  }

}
//#endif
