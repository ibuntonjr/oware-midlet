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
package net.eiroca.j2me.sm.ui;

import net.eiroca.j2me.sm.midlet.SecureSMS;
import java.util.Calendar;
import java.util.Date;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.sm.data.SecureMessage;
import net.eiroca.j2me.sm.data.SecureMessageStore;
import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.util.StoreException;
import com.substanceofcode.rssreader.presentation.FeatureList;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;

/**
 * The message store screen.
 */
public class MessageListScreen extends FeatureList {

  protected long[] messageDates;
  private final Command del;
  private final Command reply;
  private final Command invalid;

  /**
   * Creates new MessageStoreScreen
   */
  public MessageListScreen(final int title, final Command del, final Command reply, final Command invalid) {
    super(BaseApp.messages[title], Choice.IMPLICIT);
    // Add commands
    this.del = del;
    this.reply = reply;
    this.invalid = invalid;
    BaseApp.setup(this, BaseApp.cBACK, null);
  }

  /**
   * Returns the Id of the currently selected message.
   */
  public long getSelectedMessageDate() {
    final int selectedIndex = super.getSelectedIndex();
    if ((messageDates != null) && (selectedIndex >= 0) && (selectedIndex < messageDates.length)) {
      // Note: We add addresses in the reverse order (always to the first
      // position in list)
      return messageDates[messageDates.length - 1 - selectedIndex];
    }
    // This should actually never happen
    return -1;
  }

  /**
   * Updates the content of the screen
   */
  public void updateMessageList(final SecureSMS app, final SecureMessageStore messageStore) throws StoreException {
    // Get the id of the message currently selected
    final long date = getSelectedMessageDate();
    // Remove the DELETE command
    if (del != null) {
      super.removeCommand(del);
    }
    if (reply != null) {
      super.removeCommand(reply);
    }
    if (invalid != null) {
      super.removeCommand(invalid);
    }
    // Delete all messages to avoid synchronization problems
    deleteAll();
    // Get the list of the Dates
    messageDates = messageStore.listIds(Store.naturalOrder);
    final int messageIdsLength = messageDates.length;
    if (messageIdsLength > 0) {
      // Create new list and populate it with the message "identifiers" - MessageTimestamp/Name
      SecureMessage message;
      final Calendar c = Calendar.getInstance();
      StringBuffer sb;
      for (int i = 0; i < messageDates.length; i++) {
        // Prepare the item titles
        message = messageStore.getById(messageDates[i]);
        c.setTime(new Date(message.date));
        final String name = app.handler.addressName(message);
        sb = new StringBuffer(32);
        app.formatDate(c, sb).append(' ').append(name);
        // Add the element to the list
        super.insert(0, sb.toString(),
						(message.unread ? SecureSMS.unreadImage : SecureSMS.readImage));
        // Automatically select the message that has been selected the last time user left the list
        if (message.date == date) {
          super.setSelectedIndex(i, true);
        }
      }
      // Add the DELETE command if at least one message has been found
      if (del != null) {
        super.addPromptCommand(del, AppConstants.MSG_ARESURE);
      }
      if (reply != null) {
        super.addCommand(reply);
      }
      if (invalid != null) {
        super.addPromptCommand(invalid, AppConstants.MSG_ARESURE);
      }
    }
  }

}
//#endif
