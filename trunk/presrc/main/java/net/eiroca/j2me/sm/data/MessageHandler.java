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
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR120
package net.eiroca.j2me.sm.data;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;
import net.eiroca.j2me.sm.util.StoreObserver;
import net.eiroca.j2me.util.CipherDES;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Implementation of the MessageHandler. This implementation is Nokia Series 60 specific, but may be compatible with other phones supporting WMA.
 */
public class MessageHandler implements MessageListener, StoreObserver {

  MessageConnection connection;

  private static final int port = 9087;

  public CipherDES chiper;
  public SecureMessageStore inboxStore;
  public SecureMessageStore outboxStore;
  public AddressStore addressBookStore;
  public UnknownStore unknownStore;

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("MessageHandler");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

  /**
   * The constructor made the package-visible for security reasons
   */
  public MessageHandler() {
    //
  }

  public void init() throws StoreException {
    final String connectionString = "sms://:" + MessageHandler.port;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init connectionString=" + connectionString);}
		//#endif
    try {
      connection = (MessageConnection) Connector.open(connectionString);
      connection.setMessageListener(this);
    }
    catch (final IOException ioe) {
			ioe.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", ioe);
			//#endif
      throw new MessageHandlerException(MessageHandlerException.ERR_OPENCONNECTION);
		}
    catch (final Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
      throw new MessageHandlerException(MessageHandlerException.ERR_OPENCONNECTION);
    }
  }

  public void done() throws StoreException {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("done");}
		//#endif
    try {
      connection.setMessageListener(null);
      connection.close();
    }
    catch (final IOException ioe) {
			ioe.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("done error", ioe);
			//#endif
      throw new MessageHandlerException(MessageHandlerException.ERR_CLOSECONNECTION);
    }
  }

  public synchronized void send(final SecureMessage msg) throws StoreException {
    MessageSender sender;
    // Use the key supplied to transcode the message and send it
    // Encode the message using the key.  Other fields not sent.
    byte[] key = null;
    final Address address = addressBookStore.getByNumber(msg.number, false);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("send msg.number,address=" + msg.number + "," + address);}
		//#endif
    if (address != null) {
      key = address.getKeyData();
    }
    if (key == null) { throw new MessageHandlerException(MessageHandlerException.ERR_INVALIDKEY); }
    byte[] data = Store.encodeData(msg.text);
    data = chiper.encode(data, key);
    // data = PackerHelper.compress(data);
    // Find the connection to use and send the message
    // Create listener for each address in the AddressStore
    final String wmaAddress = new StringBuffer("sms://").append(address.number).append(":").append(MessageHandler.port).toString();
    final BinaryMessage wmaMessage = (BinaryMessage) connection.newMessage(MessageConnection.BINARY_MESSAGE);
    wmaMessage.setAddress(wmaAddress);
    wmaMessage.setPayloadData(data);
    sender = new MessageSender(this, wmaMessage);
    sender.start();
    int count = 99;
    while ((sender.getStatus() == 0) & (--count >= 0)) {
      try {
        Thread.sleep(100);
      }
      catch (final InterruptedException e) {
      }
    }
    if (sender.getStatus() > 0) {
      outboxStore.store(msg);
    }
    else {
			new Exception("send error").printStackTrace();
      throw new MessageHandlerException(MessageHandlerException.ERR_SENDMESSAGE);
    }
  }

  public synchronized void addMessage(final Long aDate, final Address address, byte[] data) throws StoreException {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("addMessage address.id,address.name,data.length=" + address.id + "," + address.name + "," + data.length);}
		//#endif
    final byte[] key = address.getKeyData();
    data = chiper.decode(data, key);
    // data = PackerHelper.decompress(data);
    final String msg = Store.decodeData(data);
    // Create new message
    final SecureMessage message = new SecureMessage(aDate, address.number, msg, 0, true);
    inboxStore.store(message);
  }

  public synchronized void receive() {
    try {
      // Store the message in the message store
      // Note: use fixed factory class name as we know for sure
      // which factory should be used.
      final Message wmaMessage = connection.receive();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("receive wmaMessage=" + wmaMessage + "," + ((wmaMessage != null) ? "null" : wmaMessage.getClass().getName()));}
			//#endif
      if (!(wmaMessage instanceof BinaryMessage)) {
        // It is safe to skip non-binary message, return.
        return;
      }
      // WMA address is in format "sms://<number>:<port>", so parse it here to remove prefix and port number
      final String wmaAddress = wmaMessage.getAddress();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("receive wmaAddress=" + wmaAddress);}
			//#endif
			// With Sun WTK tests, the ':' may  not appear.
      final int colon2 = wmaAddress.indexOf(":", 6);
      final String number = wmaAddress.substring(6, (colon2 < 0) ? wmaAddress.length() : colon2);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("receive number=" + number);}
			//#endif
      Address address = addressBookStore.getByNumber(number, false);
      if (address == null) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("receive Unkwnon address=null");}
				//#endif
        address = new Address("Unkwnon", number, "");
        addressBookStore.store(address);
        final UnknownMessage mes = new UnknownMessage(number, ((BinaryMessage) wmaMessage).getPayloadData());
        unknownStore.store(mes);
      }
      else {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("receive address.id,address.name=" + address.id + "," + address.name);}
				//#endif
        addMessage(null, address, ((BinaryMessage) wmaMessage).getPayloadData());
      }
    }
    catch (final IOException ioe) {
			ioe.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("receive error", ioe);
			//#endif
    }
    catch (final StoreException sme) {
			sme.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("receive error", sme);
			//#endif
    }
    catch (final Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("receive error", e);
			//#endif
		}
  }

	/**
		For the given secure message, get the name in the address book that
		has the number in the secure message.  In this case return the
		address name.  If there is no address for that message, return the
		number stored in the message.
		*/
  public String addressName(final SecureMessage message) {
    String number = message.number;
    Address address = null;
    try {
      address = addressBookStore.getByNumber(number, false);
    }
    catch (final StoreException e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("addressName error", e);
			//#endif
    }
    if (address != null) {
      number = address.name;
    }
    return number;
  }

  public void actionDone(final int action, final Object obj, final Store store) {
    if (action == StoreObserver.ADD) {
      final Address address = (Address) obj;
      final byte[] key = address.getKeyData();
      final String number = address.number;
      if ((key != null) && (number != null)) {
        unknownStore.recoverMessages(this, address);
      }
    }
  }

  /**
   * Hook for incoming message notification
   * @param messageConnection MessageConnection on which the message was received.
   */
  public void notifyIncomingMessage(final MessageConnection messageConnection) {
    // To avoid spending too much memory we use same instance of Runnable (this) to start all threads.
    new MessageReceiver(this).start();
  }

}
//#endif
