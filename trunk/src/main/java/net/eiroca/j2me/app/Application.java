/**
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 * Copyright (C) 2002 Eugene Morozov (xonixboy@hotmail.com)
 *
 * This software was modified 2008-12-14.  The original file was
 * in mobilesuite.sourceforge.net project.
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
 *
 * Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define logging define
//#define DNOLOGGING
package net.eiroca.j2me.app;

import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
//#ifdef DMIDP20
import javax.microedition.lcdui.ItemCommandListener;
//#endif
import javax.microedition.lcdui.List;

import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;

import com.substanceofcode.rssreader.presentation.FeatureForm;
import com.substanceofcode.rssreader.presentation.FeatureList;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	Support an application.  Have menus, actions, confirm.
	*/
public abstract class Application extends BaseApp implements CommandListener
//#ifdef DMIDP20
, ItemCommandListener
//#endif
{

	//#ifdef DLOGGING
//@	private Logger logger;
//@	private boolean fineLoggable;
//@	private boolean finestLoggable;
	//#endif

	//#ifdef DJMTEST
//@  public Application(String name) {
//@    super(name);
//@	}
	//#endif

  /**
   * Insert the item into the menu (a list) at position ps with menu definition
   * def.  If the item has an image, use that too.
   *
   * @param list
   * @param ps
   * @param def
   * @return
   */
  public static boolean insertMenuItem(final List list, final int ps, final short[] def) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		boolean traceLoggable = logger.isLoggable(Level.TRACE);
//@		logger.finest("insertMenuItem list.size(),ps,def[BaseApp.MD_MENUID]=" + list.size() + "," + ps + "," + def[BaseApp.MD_MENUID]);
		//#endif
		try {
			Image icon = null;
			if (def[BaseApp.MD_MENUIC] >= 0) {
				icon = BaseApp.icons[def[BaseApp.MD_MENUIC]];
			}
			short[] cdef;
			final int clen = BaseApp.menuCombined.size();
			int mix = -1;
			int i = 0;
			for (; i < clen; i++) {
				mix = ((Integer)BaseApp.menuCombined.elementAt(i)).intValue();
				cdef = BaseApp.menu[mix];
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("insertMenuItem i,mix,cdef[BaseApp.MD_MENUAC]=" + i + "," + mix + "," + cdef[BaseApp.MD_MENUAC]);}
				//#endif
				if (cdef[BaseApp.MD_MENUAC] == def[BaseApp.MD_MENUAC]) {
					break;
				}
			}
			// Item must be in combinned menu list
			if (i >= clen) {
				throw new IllegalArgumentException(
						"Definition must be in menuCombined");
			}
			Integer ix = new Integer(mix);
			if (BaseApp.menuShown.indexOf(ix) >= 0) {
				//#ifdef DLOGGING
//@				logger.finest("insertMenuItem i alread shown");
				//#endif
				return false;
			}
			if ((ps >= 0) && (ps <= BaseApp.menuShown.size())) {
				BaseApp.menuShown.insertElementAt(ix, ps);
			} else {
				BaseApp.menuShown.addElement(ix);
			}
			list.insert(ps, BaseApp.messages[def[BaseApp.MD_MENUTX]], icon);
			if ((BaseApp.MD_PROMPTX < def.length) && (def[BaseApp.MD_PROMPTX] >= 0) &&
					(list instanceof FeatureList)) {
				((FeatureList)list).insertPrompt(ps, def[BaseApp.MD_PROMPTX]);
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			Logger.getLogger("Application").severe("insertMenuItem error", e);
			//#endif
		}
		return false;
  }

  /**
   * Insert the item for the action into the menu (a list) at position ps.
	 * If the item has an image, use that too.
   *
   * @param list
   * @param ps
   * @param action
   * @return
   */
  public static boolean insertMenuItem(final List list, final int action) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		boolean traceLoggable = logger.isLoggable(Level.TRACE);
//@		logger.finest("insertMenuItem list.size(),action=" + list.size() + "," + action);
		//#endif
		try {
			short[] cdef = null;
			final int clen = BaseApp.menuCombined.size();
			int mix = -1;
			int i = 0;
			for (; i < clen; i++) {
				mix = ((Integer)BaseApp.menuCombined.elementAt(i)).intValue();
				cdef = BaseApp.menu[mix];
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("insertMenuItem i,mix,cdef[BaseApp.MD_MENUAC]=" + i + "," + mix + "," + cdef[BaseApp.MD_MENUAC]);}
				//#endif
				if (cdef[BaseApp.MD_MENUAC] == action) {
					break;
				}
			}
			// Item must be in combinned menu list
			if ((i >= clen) || (cdef == null)) {
				throw new IllegalArgumentException(
						"Definition must be in menuCombined");
			}
			Integer ix = new Integer(mix);
			if (BaseApp.menuShown.indexOf(ix) >= 0) {
				//#ifdef DLOGGING
//@				logger.finest("insertMenuItem i alread shown");
				//#endif
				return false;
			}
			final int slen = BaseApp.menuShown.size();
			int j = 0;
			for (; j < slen; j++) {
				int cix = ((Integer)BaseApp.menuShown.elementAt(j)).intValue();
				if (mix < cix) {
					break;
				}
			}
			if (j < slen) {
				BaseApp.menuShown.insertElementAt(ix, j);
			} else {
				BaseApp.menuShown.addElement(ix);
			}
			Image icon = null;
			if (cdef[BaseApp.MD_MENUIC] >= 0) {
				icon = BaseApp.icons[cdef[BaseApp.MD_MENUIC]];
			}
			list.insert(j, BaseApp.messages[cdef[BaseApp.MD_MENUTX]], icon);
			if ((BaseApp.MD_PROMPTX < cdef.length) &&
				(cdef[BaseApp.MD_PROMPTX] >= 0) &&
					(list instanceof FeatureList)) {
				((FeatureList)list).insertPrompt(j, cdef[BaseApp.MD_PROMPTX]);
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			Logger.getLogger("Application").severe("insertMenuItem error", e);
			//#endif
		}
		return false;
  }

  /**
   * Get action from selected menu item in the menu list.  Return the
   * action defined for the menu item.
   *
   * @param id
   * @param list
   * @return
   */
  public static short getAction(final int id, final List list) {
    final int idx = list.getSelectedIndex();
    short[] def;
    int ps = 0;
    for (int i = 0; i < BaseApp.menu.length; i++) {
      def = BaseApp.menu[i];
      if (def[BaseApp.MD_MENUID] == id) {
        if (ps == idx) { return def[BaseApp.MD_MENUAC]; }
        ps++;
      }
    }
    return -1;
  }

  /**
   * Get the menu as a list for the given menu id.  Add command to the list.
   *
   * @param owner
   * @param title
   * @param menuID
   * @param menuAction
   * @param special
   * @param cmd
   * @return
   */
  public static List getMenu(final String title, final int menuID, final int special, final Command cmd) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		boolean traceLoggable = logger.isLoggable(Level.TRACE);
//@		logger.finest("getMenu title,special,cmd=" + title + "," + special + "," + cmd.getLabel());
		//#endif
		final FeatureList list = new FeatureList(title, Choice.IMPLICIT);
		try {
			BaseApp.menuShown = new Vector();
			BaseApp.menuCombined = new Vector();
			short[] def;
			int ps = 0;
			for (int i = 0; i < BaseApp.menu.length; i++) {
				def = BaseApp.menu[i];
				final int action = def[BaseApp.MD_MENUAC];
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("i,action,special,menuID,def[BaseApp.MD_MENUID]=" + i + "," + action + "," + special + "," + menuID + "," + def[BaseApp.MD_MENUID]);}
				//#endif
				if (def[BaseApp.MD_MENUID] == menuID) {
					Integer ix = new Integer(i);
					BaseApp.menuCombined.addElement(ix);
					// FIX for special
					if (action == special) {
					} else {
						Application.insertMenuItem(list, ps, def);
						if (action != BaseApp.AC_NONE) {
							BaseApp.registerListItem(list, ps, action);
						}
						ps++;
					}
				}
			}

			BaseApp.setup(list, cmd, null);
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("getMenu error", e);
			//#endif
		}
		return list;
  }

  /**
   * Remove command (implemented as list) for the action from the list.
   *
   * @param list to remove from
   * @param action
   * @return
   */
  public static boolean deleteMenuItem(final List list, final int action) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		boolean traceLoggable = logger.isLoggable(Level.TRACE);
//@		logger.finest("deleteFromList list.size(),action,BaseApp.menuShown.size(),BaseApp.menu.length=" + list.size() + "," + action + "," + BaseApp.menuShown.size());
		//#endif
		try {
			if (BaseApp.menuShown.size() == 0) {
				//#ifdef DLOGGING
//@				logger.finest("deleteFromList No show items, exiting???");
				//#endif
				return false;
			}
			final int clen = BaseApp.menuShown.size();
			int i = 0;
			short[] cdef;
			for (; i < clen; i++) {
				cdef = BaseApp.menu[
						((Integer)BaseApp.menuShown.elementAt(i)).intValue()];
				//#ifdef DLOGGING
//@				if (traceLoggable) {logger.trace("deleteFromList i,cdef[BaseApp.MD_MENUAC]=" + i + "," + cdef[BaseApp.MD_MENUAC]);}
				//#endif
				if (cdef[BaseApp.MD_MENUAC] == action) {
					BaseApp.menuShown.removeElementAt(i);
					list.delete(i);
					//#ifdef DLOGGING
//@					logger.finest("deleteFromList cdef[BaseApp.MD_MENUAC] == action " + cdef[BaseApp.MD_MENUAC] + "," + action);
					//#endif
					return true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("deleteFromList error", e);
			//#endif
		}
		return false;
  }

  /**
   * Create a form to confirm the user choice with the title, questiion,
   * and yes/now commands. Set the command listner to midlet, and display
   * the form.  Save the current displayable as previous.
   *
   * @param title
   * @param question
   * @param yes
   * @param no
   */
  public void confirm(final int title, final int question, final Command yes, final Command no) {
		//#ifdef DLOGGING
//@		if (logger == null) {
//@			logger = Logger.getLogger("Application");
//@			fineLoggable = logger.isLoggable(Level.FINE);
//@			finestLoggable = logger.isLoggable(Level.FINEST);
//@			if (finestLoggable) {logger.finest("confirm");}
//@		}
		//#endif
    final Form qform = new FeatureForm(BaseApp.messages[title]);
    qform.append(BaseApp.messages[question]);
    qform.addCommand(yes);
    qform.addCommand(no);
    qform.setCommandListener(this);
    BaseApp.show(null, qform, true);
  }

  /**
   * If the new settings value (newValue) != the previous value (prevValue)
	 * update the value in settings.  This allows valuesChanged to be set in
	 * settings, and to store only if changed.
   *
   * @param newValue
   * @param settingsKey
   * @param prevValue
   * @return    int
   * @author Irv Bunton
   */
  public static int settingsUpd(int newValue, String settingsKey, int prevValue) {

		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		logger.finest("settingsUpd newValue,settingsKey prevValue=" + newValue + "," + settingsKey + "," + prevValue);
		//#endif
		try {
			if (newValue != prevValue) {
				BaseApp.settings.putInt(settingsKey, newValue);
			}
			return newValue;
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("settingsUpd error", e);
			//#endif
			return prevValue;
		}
	}

	public static ChoiceGroup createChoiceGroup(int msgNbr, int choiceType,
			int[] choiceMsgNbrs) {
		ChoiceGroup choiceGrp = new ChoiceGroup(BaseApp.messages[msgNbr],
					choiceType);

		for (int i = 0; i < choiceMsgNbrs.length; i++) {
			choiceGrp.append(BaseApp.messages[choiceMsgNbrs[i]], null);
		}
		return choiceGrp;
	}

	public static ChoiceGroup createNumRange(int msgNbr, int start, int end, int incr) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("Application");
//@		logger.finest("createNumRange msgNbr,start,end,incr=" + msgNbr + "," + start + "," + end + "," + incr);
		//#endif
		ChoiceGroup numRange = new ChoiceGroup(BaseApp.messages[msgNbr],
			//#ifdef DMIDP20
					Choice.POPUP
			//#else
//@					Choice.EXCLUSIVE
			//#endif
					);
			for (int i = start; i <= end;) {
				numRange.append(Integer.toString(i), null);
				 i+= incr;
			}
			return numRange;
	}

}
