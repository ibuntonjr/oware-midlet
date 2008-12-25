/*
   FIX font size
 * FeatureList.java
 *
 * Copyright (C) 2007 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
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
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.rms.Settings;
import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* List with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
public class FeatureList extends List {
	protected FeatureMgr featureMgr;

	private Font font = null;

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("FeatureList");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

	public FeatureList(String title, int listType) {
		super(title, listType);
		init();
	}

	public void init() {
		featureMgr = new FeatureMgr(this);
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Starting FeatureList "
			//#ifdef DMIDP20
				+ super.getTitle()
			//#endif
				);}
		//#endif
		//#ifdef DMIDP20
		//FOX initFont();
		//#endif
	}

	//#ifdef DMIDP20
	/* FIX
	public void initFont(RssReaderMIDlet midlet) {
		if (BaseApp.settings != null) {
			(BaseApp.settings.get(BaseApp.FONT_SIZE_KEY)
		if (midlet.getSettings().getFontChoice() ==
				RssReaderSettings.DEFAULT_FONT_CHOICE) {
			this.font = null;
		} else {
			Font defFont = Font.getDefaultFont();
			this.font = Font.getFont(Font.FACE_SYSTEM, defFont.getStyle(),
					midlet.getSettings().getFontSize());
		}
        final int fitPolicy = midlet.getSettings().getFitPolicy();
        if (fitPolicy != List.TEXT_WRAP_DEFAULT) {
			super.setFitPolicy(fitPolicy);
		}
	}
	*/
	//#endif

	public FeatureList(String title, int listType,
			          String [] stringElements,  Image[] imageElements) {
		super(title, listType, stringElements, imageElements);
		init();
	}

	final public void addPromptCommand(Command cmd, String prompt) {
		super.addCommand(cmd);
		featureMgr.addPromptCommand(cmd, prompt);
	}

	final public void removeCommand(Command cmd) {
		super.removeCommand(cmd);
		featureMgr.removeCommand(cmd);
	}

    final public void setCommandListener(CommandListener cmdListener) {
		super.setCommandListener(featureMgr);
		featureMgr.setCommandListener(cmdListener, false);
	}

    final public void setCommandListener(CommandListener cmdListener,
			boolean background) {
		super.setCommandListener(featureMgr);
		featureMgr.setCommandListener(cmdListener, background);
    }

	//#ifdef DMIDP20
	final public int append(String stringPart, Image imagePart) {
		int rtn = -1;
		try {
			rtn = super.append(stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError(e);
			rtn = super.append(stringPart, imagePart);
		}
		/* FIX
		if (font != null) {
			setFont(rtn, font);
		}
		*/
		return rtn;
	}

	final public void insert(int elementnum, String stringPart, Image imagePart) {
		try {
			super.insert(elementnum, stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError(e);
			super.insert(elementnum, stringPart, imagePart);
		}
		int newElement = (elementnum < 0) ? 0 : elementnum;
		/* FIX
		if (font != null) {
			setFont(newElement, font);
		}
		*/
	}

	final public void set(int elementnum, String stringPart, Image imagePart) {
		try {
			super.set(elementnum, stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError(e);
			super.set(elementnum, stringPart, imagePart);
		}
		/* FIX
		if (font != null) {
			setFont(elementnum, font);
		}
		*/
	}

	private void handleError(RuntimeException e) {
		// Using emulator, this can throw array out of bounds, but
		// this is not in the 
		if (e instanceof ArrayIndexOutOfBoundsException) {
			//#ifdef DLOGGING
			logger.warning("ArrayIndexOutOfBoundsException on setFont");
			//#endif
			/* FIX
			featureMgr.getMidlet().getLoadForm().appendNote(
					"Font not supported by device.  Reset to default or pick another font.");
			featureMgr.getMidlet().getSettings().setFontChoice(
					RssReaderSettings.DEFAULT_FONT_CHOICE);
			this.font = null;
			final int last = super.size() - 1;
			if (last >= 0) {
				super.setFont(last, Font.getDefaultFont());
			}
			*/
		} else {
			//#ifdef DLOGGING
			logger.severe("Other exception " + e.getClass().getName());
			//#endif
			throw e;
		}
	}

    /* FIX
    public Font getFont() {
        return (font);
    }

    public void setFont(Font font) {
        this.font = font;
    }
	*/

	//#endif

    public FeatureMgr getFeatureMgr() {
        return (featureMgr);
    }

}
