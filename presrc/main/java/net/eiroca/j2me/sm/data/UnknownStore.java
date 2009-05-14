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
package net.eiroca.j2me.sm.data;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;
import net.eiroca.j2me.sm.util.StoreFilterByID;

public class UnknownStore extends Store {

  public UnknownStore(final String storeName) throws StoreException {
    super(storeName);
  }

  public void store(final UnknownMessage message) throws StoreException {
    replaceFirst(new StoreFilterByID(message.date), message.serialize());
  }

  public void recoverMessages(final MessageHandler mh, final Address address) {
    RecordEnumeration enm = null;
    final String number = address.number;
    try {
      enm = recordStore.enumerateRecords(null, null, true);
      while (enm.hasNextElement()) {
        try {
          final ByteArrayInputStream bin = new ByteArrayInputStream(enm.nextRecord());
          final DataInputStream din = new DataInputStream(bin);
          final long date = din.readLong();
          final String numberStored = din.readUTF();
          if (number.equals(numberStored)) {
            final int len = din.readInt();
            final byte[] data = new byte[len];
            din.read(data);
            mh.addMessage(new Long(date), address, data);
            recordStore.deleteRecord(enm.previousRecordId());
          }
          din.close();
          bin.close();
        }
        catch (final StoreException e) {
        }
        catch (final IOException e) {
        }
      }
    }
    catch (final RecordStoreException rse) {
    }
    finally {
      if (enm != null) {
        enm.destroy();
      }
    }
  }

}
