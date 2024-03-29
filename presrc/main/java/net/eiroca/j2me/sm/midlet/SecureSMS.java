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
   * This was first changed on 2009-05-13 by Irving Bunton, Jr
   */
/**
	TODO
	If connection already opened (if use emulator, exit without bringing up
	 a new emulator, gives error.
	 */
// Expand to define JSR-120 test define
@DJSR120@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR120
package net.eiroca.j2me.sm.midlet;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDletStateChangeException;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.rms.Settings;
import net.eiroca.j2me.sm.data.Address;
import net.eiroca.j2me.sm.data.AddressStore;
import net.eiroca.j2me.sm.data.MessageHandler;
import net.eiroca.j2me.sm.data.SecureMessage;
import net.eiroca.j2me.sm.data.SecureMessageStore;
import net.eiroca.j2me.sm.data.UnknownMessage;
import net.eiroca.j2me.sm.data.UnknownStore;
import net.eiroca.j2me.sm.ui.AddressBookScreen;
import net.eiroca.j2me.sm.ui.AddressScreen;
import net.eiroca.j2me.sm.ui.InsertPINScreen;
import net.eiroca.j2me.sm.ui.MessageListScreen;
import net.eiroca.j2me.sm.ui.MessageScreen;
import net.eiroca.j2me.sm.ui.PINChangeScreen;
import net.eiroca.j2me.sm.ui.SendNewScreen;
import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;
import net.eiroca.j2me.sm.util.StoreObserver;
import net.eiroca.j2me.util.CipherDES;
import com.substanceofcode.rssreader.presentation.FeatureList;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

public class SecureSMS extends Application implements StoreObserver {

  public static final int ME_MAINMENU = 0;
  public static final int ME_CLEANUP = 1;

  public static final int AC_SHOWABOUT = 1;
  public static final int AC_SHOWINBOX = 2;
  public static final int AC_SHOWSENDNEW = 3;
  public static final int AC_SHOWSENTITEMS = 4;
  public static final int AC_SHOWADDRESSBOOK = 5;
  public static final int AC_SENDNEW = 6;
  public static final int AC_ADDRESSBOOKEDT = 7;
  public static final int AC_ADDRESSBOOKADD = 8;
  public static final int AC_ADDRESSBOOKDEL = 9;
  public static final int AC_ADDRESSBOOKSAV = 10;
  public static final int AC_INBOXVIEW = 11;
  public static final int AC_INBOXDELETE = 12;
  public static final int AC_INBOXREPLY = 13;
  public static final int AC_SENTVIEW = 14;
  public static final int AC_SENTDELETE = 15;
  public static final int AC_SHOWPINCHANGE = 16;
  public static final int AC_PINSAVE = 17;
  public static final int AC_PINDELETE = 18;
  public static final int AC_PINOK = 19;
  public static final int AC_CLEANUP = 20;
  public static final int AC_CLEANUP1 = 21;
  public static final int AC_CLEANUP2 = 22;
  public static final int AC_CLEANUP3 = 23;
  public static final int AC_YES = 24;
  public static final int AC_NO = 25;
  public static final int AC_INVALID = 26;

  // System-independent store names - this will stay constant across the
  // releases to archive the backward compatibility
  private static final String ADDRESS_BOOK_STORE_NAME = "ab";
  private static final String INBOX_STORE_NAME = "ib";
  private static final String OUTBOX_STORE_NAME = "ob";
  private static final String UNKNOWN_STORE_NAME = "uk";

  public static Command cADRDEL;
  public static Command cADRSAV;
  public static Command cINBOXDEL;
  public static Command cINBOXREPLY;
  public static Command cSENTDEL;
  public static Command cSENDNEW;
  public static Command cADRADD;
  public static Command cPINSAV;
  public static Command cPINDEL;
  public static Command cPINOK;
  public static Command cYES;
  public static Command cNO;
  public static Command cINVALID;

  // Messenger objects - stores and handlers
  private SecureMessageStore inbox;
  private SecureMessageStore sentItems;
  private AddressStore addressBook;
  private UnknownStore unknown;
  public MessageHandler handler;

  // Temporary message and address objects
  private SecureMessage message;
  private Address address;

  // Messenger screens
  private FeatureList scMenu;
  private FeatureList scMenuCleanUp;
  private SendNewScreen scSendNew;
  private AddressScreen scAddress;
  private AddressBookScreen scAddressBook;
  private MessageScreen scMessage;
  private MessageListScreen scInbox;
  private MessageListScreen scSentItems;
  private PINChangeScreen scPINChange;
  private InsertPINScreen scInsertPIN;

  private static String STS_PIN = "PIN";

  short[][] menuCleanUp;

	final public static String RES_UNREAD = "sm_unread.png";
	final public static String RES_READ = "sm_read.png";
  public static Image unreadImage;
  public static Image readImage;

	//#ifdef DLOGGING
  private boolean fineLoggable;
  private boolean finestLoggable;
  private boolean traceLoggable;
	private Logger logger;
	//#endif
  public SecureSMS() {
		//#ifdef DJMTEST
    super("SecureSMS Games Suite");
		//#else
    super();
		//#endif
		//#ifdef DLOGGING
		logger = Logger.getLogger("SecureSMS");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
    BaseApp.messages = BaseApp.readStrings("messages.txt");
    BaseApp.cOK = BaseApp.newCommand(AppConstants.MSG_LABEL_OK, Command.OK, 30,  BaseApp.AC_NONE);
    BaseApp.cBACK = BaseApp.newCommand(AppConstants.MSG_LABEL_BACK, Command.BACK, 20, BaseApp.AC_BACK);
    BaseApp.cEXIT = BaseApp.newCommand(AppConstants.MSG_LABEL_EXIT, Command.EXIT, 10, BaseApp.AC_EXIT);
    SecureSMS.cADRADD = BaseApp.newCommand(AppConstants.MSG_ADD, Command.OK, 2, SecureSMS.AC_ADDRESSBOOKADD);
    SecureSMS.cADRDEL = BaseApp.newCommand(AppConstants.MSG_DELETE, Command.OK, 2, SecureSMS.AC_ADDRESSBOOKDEL);
    SecureSMS.cADRSAV = BaseApp.newCommand(AppConstants.MSG_SAVE, Command.OK, 2, SecureSMS.AC_ADDRESSBOOKSAV);
    SecureSMS.cINBOXDEL = BaseApp.newCommand(AppConstants.MSG_DELETE, Command.OK, 2, SecureSMS.AC_INBOXDELETE);
    SecureSMS.cINBOXREPLY = BaseApp.newCommand(AppConstants.MSG_REPLY, Command.OK, 2, SecureSMS.AC_INBOXREPLY);
    SecureSMS.cSENTDEL = BaseApp.newCommand(AppConstants.MSG_DELETE, Command.OK, 2, SecureSMS.AC_SENTDELETE);
    SecureSMS.cSENDNEW = BaseApp.newCommand(AppConstants.MSG_SEND, Command.OK, 2, SecureSMS.AC_SENDNEW);
    SecureSMS.cPINSAV = BaseApp.newCommand(AppConstants.MSG_SAVE, Command.OK, 2, SecureSMS.AC_PINSAVE);
    SecureSMS.cPINDEL = BaseApp.newCommand(AppConstants.MSG_DELETE, Command.OK, 2, SecureSMS.AC_PINDELETE);
    SecureSMS.cPINOK = BaseApp.newCommand(AppConstants.MSG_LABEL_OK, Command.OK, 2, SecureSMS.AC_PINOK);
    SecureSMS.cYES = BaseApp.newCommand(AppConstants.MSG_LABEL_YES, Command.OK, 2, SecureSMS.AC_YES);
    SecureSMS.cNO = BaseApp.newCommand(AppConstants.MSG_LABEL_NO, Command.BACK, 1, SecureSMS.AC_NO);
    SecureSMS.cINVALID = BaseApp.newCommand(AppConstants.MSG_INVALID, Command.OK, 1, SecureSMS.AC_INVALID);
    BaseApp.menu = new short[][] {
        {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_INBOX, SecureSMS.AC_SHOWINBOX, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_SENDNEW, SecureSMS.AC_SHOWSENDNEW, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_SENTITEMS, SecureSMS.AC_SHOWSENTITEMS, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_ADDRESSBOOK, SecureSMS.AC_SHOWADDRESSBOOK, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_CHANGEPIN, SecureSMS.AC_SHOWPINCHANGE, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_CLEANUP, SecureSMS.AC_CLEANUP, -1
        }, {
            SecureSMS.ME_MAINMENU, (short)AppConstants.MSG_MENU_MAIN_ABOUT, SecureSMS.AC_SHOWABOUT, -1
        }, {
            SecureSMS.ME_CLEANUP, (short)AppConstants.MSG_CLEANUP1, SecureSMS.AC_CLEANUP1, -1
        }, {
            SecureSMS.ME_CLEANUP, (short)AppConstants.MSG_CLEANUP2, SecureSMS.AC_CLEANUP2, -1
        }, {
            SecureSMS.ME_CLEANUP, (short)AppConstants.MSG_CLEANUP3, SecureSMS.AC_CLEANUP3, -1
        }
    };
    Settings.load();
  }

  /**
   * Start the MIDlet.
   */
  public void init() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
    try {
			super.init();
			//#ifdef DLOGGING
			logger.info("BaseApp.messages.length=" + BaseApp.messages.length);
			logger.info("BaseApp.messages[AppConstants.MSG_SECURESMS_NAME]=" + BaseApp.messages[AppConstants.MSG_SECURESMS_NAME]);
			logger.info("BaseApp.messages[AppConstants.MSG_SECURESMS_USERDEF - 1]=" + BaseApp.messages[AppConstants.MSG_SECURESMS_USERDEF - 1]);
			//#endif
			unreadImage = BaseApp.createImage(SecureSMS.RES_UNREAD);
			readImage   = BaseApp.createImage(SecureSMS.RES_READ);
      // Initialize everything
      // Initialize the address book and two message stores
      addressBook = new AddressStore(SecureSMS.ADDRESS_BOOK_STORE_NAME);
      inbox = new SecureMessageStore(SecureSMS.INBOX_STORE_NAME);
      sentItems = new SecureMessageStore(SecureSMS.OUTBOX_STORE_NAME);
      unknown = new UnknownStore(SecureSMS.UNKNOWN_STORE_NAME);
      handler = new MessageHandler();
      handler.inboxStore = inbox;
      handler.outboxStore = sentItems;
      handler.addressBookStore = addressBook;
      handler.unknownStore = unknown;
      handler.chiper = new CipherDES();
      handler.init();
      inbox.registerObserver(this);
      addressBook.registerObserver(handler);
      scMenu = Application.getMenu(BaseApp.messages[AppConstants.MSG_SECURESMS_NAME], SecureSMS.ME_MAINMENU, -1, BaseApp.cEXIT);
      final String pin = Settings.get(SecureSMS.STS_PIN);
      if (pin != null) {
        scInsertPIN = new InsertPINScreen(AppConstants.MSG_INSERTPIN);
        BaseApp.setDisplay(scInsertPIN);
      }
      else {
        BaseApp.show(null, scMenu, true);
      }
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
			if (e instanceof StoreException) {
				// Exit without any messages
				try {
					destroyApp(true);
				} catch (MIDletStateChangeException mse) {
					e.printStackTrace();
					//#ifdef DLOGGING
					logger.severe("init error", mse);
					//#endif
				}
			}
    }
  }

  protected void done() {
    try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("done");}
			//#endif
      handler.done();
    }
    catch (final StoreException e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("done error", e);
			//#endif
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("done error", e);
			//#endif
		}
    super.done();
  }

  private int nextAction = SecureSMS.AC_NO;

  // Implementation of the command listener interface
  public boolean handleAction(int action, final Displayable d, final Command cmd) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("handleAction action,d,cmd=" + action + "," + ((d == null) ? "null" : d.getClass().getName()) + "," + cmd.getLabel() );}
		//#endif
    boolean confirmed = false;
    if (action == SecureSMS.AC_YES) {
      confirmed = true;
      action = nextAction;
    }
    if (action == SecureSMS.AC_NO) {
      BaseApp.back(null);
      return true;
    }
    nextAction = action;
    int errMsg = AppConstants.MSG_MESSAGESTOREERROR;
    Displayable back = scMenu;
    boolean processed = true;
    try {
      long id;
      switch (action) {
        case AC_SHOWINBOX:
          scInbox = new MessageListScreen(AppConstants.MSG_INBOX, SecureSMS.cINBOXDEL, SecureSMS.cINBOXREPLY, SecureSMS.cINVALID);
          BaseApp.registerList(scInbox, SecureSMS.AC_INBOXVIEW);
          scInbox.updateMessageList(this, inbox);
          BaseApp.show(null, scInbox, true);
          break;
        case AC_SHOWSENDNEW:
          errMsg = AppConstants.MSG_ADDRESSBOOKEMPTY;
          back = scMenu;
          message = new SecureMessage(null, null, "", 0, true);
          scSendNew = new SendNewScreen();
          final boolean valid = scSendNew.updateMessage(message, addressBook);
          if (valid) {
            BaseApp.show(null, scSendNew, true);
          }
          else {
            throw new Exception();
          }
          break;
        case AC_SHOWSENTITEMS:
          scSentItems = new MessageListScreen(AppConstants.MSG_SENTITEMS, SecureSMS.cSENTDEL, null, null);
          BaseApp.registerList(scSentItems, SecureSMS.AC_SENTVIEW);
          scSentItems.updateMessageList(this, sentItems);
          BaseApp.show(null, scSentItems, true);
          break;
        case AC_SHOWADDRESSBOOK:
          scAddressBook = new AddressBookScreen();
          scAddressBook.updateAddressList(addressBook);
          BaseApp.registerList(scAddressBook, SecureSMS.AC_ADDRESSBOOKEDT);
          BaseApp.show(null, scAddressBook, true);
          break;
        case AC_SHOWABOUT:
          BaseApp.show(null, BaseApp.getTextForm(AppConstants.MSG_MENU_MAIN_ABOUT, "about.txt"), true);
          break;
        case AC_SENDNEW:
          errMsg = AppConstants.MSG_MESSAGEHASNOTBEENSENT;
          back = scSendNew;
          id = scSendNew.getSelectedAddressId();
          message.number = addressBook.getById(id).number;
          message.text = scSendNew.getMessageText();
          handler.send(message);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_MESSAGESENT, null, AlertType.INFO, scMenu, Alert.FOREVER);
          break;
        case AC_ADDRESSBOOKEDT:
          errMsg = AppConstants.MSG_ADDRESSSTOREERROR;
          back = scAddressBook;
          id = scAddressBook.getSelectedAddressId();
          address = addressBook.getById(id);
          scAddress = new AddressScreen(AppConstants.MSG_ADDRESS, false);
          scAddress.fromAddress(address);
          BaseApp.show(null, scAddress, false);
          break;
        case AC_ADDRESSBOOKADD:
          errMsg = AppConstants.MSG_ADDRESSSTOREERROR;
          back = scAddressBook;
          address = new Address("", "", "");
          scAddress = new AddressScreen(AppConstants.MSG_NEWADDRESS, true);
          scAddress.fromAddress(address);
          BaseApp.show(null, scAddress, false);
          break;
        case AC_ADDRESSBOOKDEL:
          errMsg = AppConstants.MSG_ADDRESSSTOREERROR;
          back = scAddressBook;
          id = scAddressBook.getSelectedAddressId();
          addressBook.remove(id);
          scAddressBook.updateAddressList(addressBook);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_ADDRESSDELETED, null, AlertType.INFO, scAddressBook, Alert.FOREVER);
          break;
        case AC_ADDRESSBOOKSAV:
          errMsg = AppConstants.MSG_ADDRESSSTOREERROR;
          back = scAddressBook;
          scAddress.toAddress(address, BaseApp.messages[AppConstants.MSG_NUMPREFIX]);
          addressBook.store(address);
          scAddressBook.updateAddressList(addressBook);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_ADDRESSSAVED, null, AlertType.INFO, scAddressBook, Alert.FOREVER);
          break;
        case AC_INBOXVIEW:
          back = scInbox;
          id = scInbox.getSelectedMessageDate();
          message = inbox.getById(id);
          scMessage = new MessageScreen(AppConstants.MSG_MESSAGE, AppConstants.MSG_FROM, AppConstants.MSG_TEXT, SecureSMS.cINBOXREPLY, SecureSMS.cINBOXDEL);
          scMessage.updateMessage(this, message);
					message.unread = false;
          inbox.store(message);
          BaseApp.show(null, scMessage, true);
          break;
        case AC_INBOXDELETE:
          errMsg = AppConstants.MSG_MESSAGEHASNOTBEENDELETED;
          back = scInbox;
          id = scInbox.getSelectedMessageDate();
          inbox.remove(id);
          scInbox.updateMessageList(this, inbox);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_MESSAGEDELETED, null, AlertType.INFO, scInbox, Alert.FOREVER);
          break;
        case AC_INVALID:
          back = scInbox;
          id = scInbox.getSelectedMessageDate();
          message = inbox.getById(id);
          SecureMessage message = inbox.remove(id);
          unknown.store(new UnknownMessage(message));
          scInbox.updateMessageList(this, inbox);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_MESSAGEIVALID, null, AlertType.INFO, scInbox, Alert.FOREVER);
          break;
        case AC_INBOXREPLY:
          back = scInbox;
          id = scInbox.getSelectedMessageDate();
          message = inbox.getById(id);
          scSendNew = new SendNewScreen();
          scSendNew.updateMessage(message, addressBook);
          BaseApp.show(null, scSendNew, false);
          break;
        case AC_SENTVIEW:
          back = scSentItems;
          id = scSentItems.getSelectedMessageDate();
          message = sentItems.getById(id);
          scMessage = new MessageScreen(AppConstants.MSG_MESSAGE, AppConstants.MSG_TO, AppConstants.MSG_TEXT, SecureSMS.cSENTDEL, null);
          scMessage.updateMessage(this, message);
          BaseApp.show(null, scMessage, true);
          break;
        case AC_SENTDELETE:
          errMsg = AppConstants.MSG_MESSAGEHASNOTBEENDELETED;
          back = scSentItems;
          id = scSentItems.getSelectedMessageDate();
          sentItems.remove(id);
          scSentItems.updateMessageList(this, sentItems);
          BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_MESSAGEDELETED, null, AlertType.INFO, scSentItems, Alert.FOREVER);
          break;
        case AC_SHOWPINCHANGE:
          scPINChange = new PINChangeScreen(AppConstants.MSG_CHANGEPIN);
          scPINChange.setPIN(Settings.get(SecureSMS.STS_PIN));
          BaseApp.show(null, scPINChange, true);
          break;
        case AC_PINSAVE:
          final String newPIN = scPINChange.getPIN();
          if (newPIN == null) {
            BaseApp.showAlert(AppConstants.MSG_ERROR, AppConstants.MSG_WRONGPIN, null, AlertType.ERROR, scPINChange, Alert.FOREVER);
          }
          else {
            Settings.put(SecureSMS.STS_PIN, newPIN);
            Settings.save();
            BaseApp.back(null);
          }
          break;
        case AC_PINDELETE:
          Settings.put(SecureSMS.STS_PIN, null);
          Settings.save();
          BaseApp.back(null);
          break;
        case AC_PINOK:
          final String pin = Settings.get(SecureSMS.STS_PIN);
          final String pinIns = scInsertPIN.getPIN();
          System.out.println("QUI " + pinIns + "=" + pin);
          if (pinIns.equals(pin)) {
            BaseApp.show(null, scMenu, true);
          }
          else {
            scInsertPIN.err.setText(BaseApp.messages[AppConstants.MSG_INVALINDPIN]);
          }
          break;
        case AC_CLEANUP:
          scMenuCleanUp = Application.getMenu(BaseApp.messages[AppConstants.MSG_CLEANUP], SecureSMS.ME_CLEANUP, -1, BaseApp.cBACK);
          BaseApp.show(null, scMenuCleanUp, true);
          break;
        case AC_CLEANUP1:
          if (confirmed) {
            inbox.cleanup();
            unknown.cleanup();
            BaseApp.back(null, scMenu, false);
          }
          else {
            confirm(AppConstants.MSG_CONFIRM, AppConstants.MSG_ARESURE, SecureSMS.cYES, SecureSMS.cNO);
          }
          break;
        case AC_CLEANUP2:
          if (confirmed) {
            sentItems.cleanup();
            BaseApp.back(null, scMenu, false);
          }
          else {
            confirm(AppConstants.MSG_CONFIRM, AppConstants.MSG_ARESURE, SecureSMS.cYES, SecureSMS.cNO);
          }
          break;
        case AC_CLEANUP3:
          if (confirmed) {
            addressBook.cleanup();
            BaseApp.back(null, scMenu, false);
          }
          else {
            confirm(AppConstants.MSG_CONFIRM, AppConstants.MSG_ARESURE, SecureSMS.cYES, SecureSMS.cNO);
          }
          break;
        default:
          processed = false;
          break;
      }
    }
    catch (final Throwable th) {
      th.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("handleAction error", th);
			//#endif
      // set the alert type and next displayable and show the alert
      BaseApp.showAlert(AppConstants.MSG_ERROR, errMsg, null, AlertType.ERROR, back, Alert.FOREVER);
    }
    return processed;
  }

  // -----------------------------------------------------------------------
  // Implementation of the StoreObserver interface
  public void actionDone(final int action, final Object obj, final Store store) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("actionDone action,obj=" + action + "," + obj);}
		//#endif
    if (action == StoreObserver.ADD) {
      try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("action action=StoreObserver.ADD");}
				//#endif
        // Note: As we register to listen on the incoming message store we should
        // not check the store reference
        // Check if we need to update the current inbox view
        // Note: display.getCurrent() may return null.
        if ((scInbox != null) && (BaseApp.getDisplay() == scInbox)) {
          scInbox.updateMessageList(this, inbox);
        }
        // Show the alert
        // Left the alertNext as is. In case of overlapping Alerts the
        // screen to be shown next remains correct.
        BaseApp.showAlert(AppConstants.MSG_INFO, AppConstants.MSG_MESSAGERECEIVED, null, AlertType.INFO, null, Alert.FOREVER);
      }
      catch (final Throwable th) {
        // Ignored
				th.printStackTrace();
				//#ifdef DLOGGING
				logger.warning("actionDone error", th);
				//#endif
      }
    }
  }

}
//#endif
