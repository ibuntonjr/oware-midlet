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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;

/**
 * Platform-independent implementation of the Address interface.
 */
public final class Address {

  private final ByteArrayOutputStream bout = new ByteArrayOutputStream();
  private final DataOutputStream dout = new DataOutputStream(bout);

  public final long id;
  public String name;
  public String number;
  public String key;

  /**
   * Creates new <code>Address</code> instance.
   * @param addressId Address ID in the AddressStore
   * @param addressName Name on the address
   * @param addressNumber Address MSISDN
   * @param addressKey byte[] encryption key associated
   */
  public Address(final String addressName, final String addressNumber, final String addressKey) {
    id = System.currentTimeMillis();
    name = addressName;
    number = addressNumber;
    key = addressKey;
  }

  public byte[] getKeyData() {
    if (key == null) { return null; }
    if (key.length() == 0) { return null; }
    final byte[] data = new byte[16];
    byte[] tmp;
    tmp = Store.encodeData(key);
    int l = tmp.length;
    if (l > 16) {
      l = 16;
    }
    System.arraycopy(tmp, 0, data, 0, l);
    return data;
  }

  public Address(final byte[] data) throws StoreException {
    final ByteArrayInputStream bin = new ByteArrayInputStream(data);
    final DataInputStream din = new DataInputStream(bin);
    try {
      id = din.readLong();
      name = din.readUTF();
      number = din.readUTF();
      key = din.readUTF();
    }
    catch (final IOException e) {
      throw new MessageHandlerException(StoreException.ERR_STOREREAD);
    }
    finally {
      try {
        din.close();
      }
      catch (final IOException e) {
      }
    }
  }

  public synchronized byte[] serialize() {
    try {
      bout.reset();
      dout.writeLong(id);
      dout.writeUTF(name);
      dout.writeUTF(number);
      dout.writeUTF(key);
      dout.flush();
    }
    catch (final IOException e) {
      return null;
    }
    return bout.toByteArray();
  }

  public static final boolean check(final byte[] data, final long aID, final String aName, final String aNumber) {
    long id;
    String name;
    String number;
    final ByteArrayInputStream bin = new ByteArrayInputStream(data);
    final DataInputStream din = new DataInputStream(bin);
    try {
      id = din.readLong();
      name = din.readUTF();
      number = din.readUTF();
    }
    catch (final IOException e) {
      return false;
    }
    finally {
      try {
        din.close();
      }
      catch (final IOException e) {
        return false;
      }
    }
    return (((aID == id) || ((aName != null) && aName.equals(name)) || ((aNumber != null) && aNumber.equals(number))));
  }

}
//#endif
