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
// Expand to define JSR-120 define
@DJSR120@
// Expand to define J2ME define
@DJ2MEDEF@
// Expand to define J2SE define
@DJ2SEDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR120
package net.eiroca.j2me.sm.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
//#ifdef DJ2ME
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.eiroca.j2me.app.BaseApp;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif
//#endif

/**
 * This is the superclass for all storage implementations.
 */
public class Store {

  private static final String MESSAGE_ENCODING = "UTF-8";

	//#ifdef DJ2ME
  public static RecordComparator naturalOrder = new StoreComparatorByID();

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("Store");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif
	//#endif

  public static String decodeData(final byte[] data) {
    String msg;
    try {
      msg = new String(data, Store.MESSAGE_ENCODING);
    }
    catch (final UnsupportedEncodingException uee) {
      msg = new String(data);
    }
    return msg;
  }

  public static byte[] encodeData(final String msg) {
    byte[] data;
    try {
      data = msg.getBytes(Store.MESSAGE_ENCODING);
    }
    catch (final UnsupportedEncodingException uee) {
      data = msg.getBytes();
    }
    return data;
  }

	//#ifdef DJ2ME
  public static final long getID(final byte[] serializedMessage) {
    return BaseApp.encodeBytesToLong(serializedMessage, 0);
  }
	//#endif

  private final Vector observers = new Vector();
  protected final String name;
	//#ifdef DJ2ME
  protected RecordStore recordStore;
	//#endif

  public Store(final String storeName) throws StoreException {
    name = storeName;
		//#ifdef DJ2ME
    try {
      // Get the RecordStore instance for the given name, create the underlying
      // RDS store if needed.
      recordStore = RecordStore.openRecordStore(name, true);
    }
    catch (final RecordStoreException rse) {
			rse.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("Store error", rse);
			//#endif
      // Rethrow the exception
      throw new StoreException(StoreException.ERR_STOREOPEN);
    }
		//#endif

  }

  public void registerObserver(final StoreObserver observer) {
    observers.addElement(observer);
  }

  public void unregisterObserver(final StoreObserver observer) {
    observers.removeElement(observer);
  }

  /**
   * Notifies all observers of the store about action on the object.
   * @param obj The added object.
   */
  protected void notifyAction(final int action, final Object obj) {
    // Notify each of the observers
    final Enumeration enm = observers.elements();
    while (enm.hasMoreElements()) {
      ((StoreObserver) enm.nextElement()).actionDone(action, obj, this);
    }
  }

	//#ifdef DJ2ME
  public long[] listIds(final RecordComparator c) throws StoreException {
    // Request the enumeration
    RecordEnumeration enm = null;
    try {
      // Create the array of IDs
      final long[] ids = new long[recordStore.getNumRecords()];
      int idCounter = 0;
      // Enumerate records
      enm = recordStore.enumerateRecords(null, c, false);
      byte[] what;
      while (enm.hasNextElement()) {
        what = enm.nextRecord();
        ids[idCounter] = Store.getID(what);
        idCounter++;
      }
      return ids;
    }
    catch (final RecordStoreException rse) {
			rse.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("listIds error", rse);
			//#endif
      // Notify the caller
      throw new StoreException(StoreException.ERR_STORELISTIDS);
    }
    finally {
      // Make sure the enumeration destroyed
      if (enm != null) {
        enm.destroy();
      }
    }
  }

  public byte[] findFirst(final RecordFilter f) throws StoreException {
    RecordEnumeration enm = null;
    byte[] result = null;
    try {
      enm = recordStore.enumerateRecords(f, null, false);
      if (enm.hasNextElement()) {
        result = enm.nextRecord();
      }
    }
    catch (final RecordStoreException rse) {
			rse.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("findFirst error", rse);
			//#endif
      throw new StoreException(StoreException.ERR_STOREFIND);
    }
    finally {
      // Make sure the enumeration destroyed
      if (enm != null) {
        enm.destroy();
      }
    }
    return result;
  }

  // Javadoc inherited from the interface
  public boolean removeFirst(final RecordFilter f) throws StoreException {
    RecordEnumeration enm = null;
    boolean res = false;
    try {
      enm = recordStore.enumerateRecords(f, null, false);
      if (enm.hasNextElement()) {
        // Replace the record to storage
        recordStore.deleteRecord(enm.nextRecordId());
        res = true;
      }
    }
    catch (final RecordStoreException rse) {
      // Notify the caller
      throw new StoreException(StoreException.ERR_STOREDELETE);
    }
    finally {
      // Make sure the enumeration destroyed
      if (enm != null) {
        enm.destroy();
      }
    }
    return res;
  }

  public void replaceFirst(final RecordFilter f, final byte[] data) throws StoreException {
    RecordEnumeration enm = null;
    try {
      enm = recordStore.enumerateRecords(f, null, false);
      // Replace the record to storage or add a new one
      if (enm.hasNextElement()) {
        recordStore.setRecord(enm.nextRecordId(), data, 0, data.length);
      }
      else {
        recordStore.addRecord(data, 0, data.length);
      }
    }
    catch (final RecordStoreException rse) {
      // Notify the caller
      throw new StoreException(StoreException.ERR_STORESAVE);
    }
    finally {
      // Make sure the enumeration has been destroyed before the call
      // to the method returns
      if (enm != null) {
        enm.destroy();
      }
    }
  }

  public void cleanup() {
    try {
      if (recordStore != null) {
        recordStore.closeRecordStore();
      }
      RecordStore.deleteRecordStore(name);
      recordStore = RecordStore.openRecordStore(name, true);
    }
    catch (final RecordStoreException e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("cleanup error", e);
			//#endif
    }
  }
	//#endif

}
//#endif
