/*  FIX use baseap for commands, etc.  Use confirm and settings
 * FeatureMgr.java
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
// Expand to define CLDC define
@DCLDCVERS@
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
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Font;

import net.eiroca.j2me.app.BaseApp;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Allow Form/List with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel. Also, perform commandAction
   in thread to prevent hangs.  */

public class FeatureMgr implements CommandListener, Runnable {

	private Hashtable promptCommands = null;
	//#ifdef DMIDP20
    public static final int DEFAULT_FONT_CHOICE = 0;
    protected int fontChoice = DEFAULT_FONT_CHOICE;
    protected Font font;
	//#endif
	final private Displayable disp;
	private Command origCmd = null;
	protected Command exCmd = null;
	private Displayable exDisp = null;
    private boolean     background = false;   // Flag to continue looping
    private int         loop = 0;   // Number of times to loop
    private Thread      netThread = null;  // The thread for networking, etc

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("FeatureMgr");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

	private CommandListener cmdFeatureUser = null;
	private Runnable runFeatureUser = null;

	public FeatureMgr (Displayable disp) {
		this.disp = disp;
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Starting FeatureMgr " + disp.getClass().getName() + "," + disp);}
		//#endif
	}

    public void setRunnable(Runnable runFeatureUser, boolean background) {
		synchronized(this) {
			if (background) {
				if (runFeatureUser != null) {
					if (!(runFeatureUser instanceof Runnable)) {
						throw new IllegalArgumentException(
							"Listener must implement Runnable");
					}
					this.runFeatureUser = runFeatureUser;
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("setRunnable runFeatureUser" + runFeatureUser.getClass().getName() + "," + runFeatureUser);}
					//#endif
				} else {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("setRunnable runFeatureUser=null");}
					//#endif
					this.runFeatureUser = runFeatureUser;
				}
			}
			this.background = background;
		}
		if (background) {
			startWakeup(false);
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("cmdFeatureUser,background,calling thread,new thread=" + cmdFeatureUser + "," + background + "," + Thread.currentThread() + "," + netThread);}
		//#endif
    }

    public void setCommandListener(CommandListener cmdFeatureUser,
			boolean background) {
		synchronized(this) {
			this.cmdFeatureUser = cmdFeatureUser;
			if (background) {
				if (cmdFeatureUser != null) {
					if (!(cmdFeatureUser instanceof Runnable)) {
						throw new IllegalArgumentException(
								"Listener must implement Runnable");
					}
					this.runFeatureUser = (Runnable)cmdFeatureUser;
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("setCommandListener cmdFeatureUser" + cmdFeatureUser.getClass().getName() + "," + cmdFeatureUser);}
					//#endif
				} else {
					this.runFeatureUser = (Runnable)cmdFeatureUser;
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("setCommandListener cmdFeatureUser=null");}
					//#endif
				}
			}
			this.background = background;
		}
		if (background) {
			startWakeup(false);
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("cmdFeatureUser,background,calling thread,new thread=" + cmdFeatureUser + "," + background + "," + Thread.currentThread() + "," + netThread);}
		//#endif
    }

	public void addPromptCommand(Command cmd, int prompt) {
		synchronized(this) {
			if (promptCommands == null) {
				promptCommands = new Hashtable();
			}
			promptCommands.put(cmd, new Integer(prompt));
		}
	}

	public void removeCommand(Command cmd) {
		removePrompt(cmd);
		disp.removeCommand(cmd);
	}

	public void removePrompt(Command cmd) {
		synchronized(this) {
			if (promptCommands != null) {
				promptCommands.remove(cmd);
			}
		}
	}

	/* Create prompt alert. */
	public void run() {
        /* Use networking if necessary */
        long lngStart;
        long lngTimeTaken;
		do {
			try {
				Command ccmd = null;
				Displayable cdisp = null;
				Command corigCmd = null;
				synchronized(this) {
					if (exCmd != null) {
						ccmd = exCmd;
						cdisp = exDisp;
					}
					corigCmd = origCmd;
				}
				if ((ccmd != null) && (cdisp != null)) {
					try {
						Hashtable cpromptCommands = null;
						synchronized(this) {
							cpromptCommands = promptCommands;
						}
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine("disp,ccmd,cpromptCommands,corigCmd,thread=" + disp + "," + ccmd.getLabel() + "," + cpromptCommands + "," + "," + corigCmd + "," + Thread.currentThread());}
						//#endif
						if ((cpromptCommands != null)
								&& cpromptCommands.containsKey(ccmd)) {
							synchronized(this) {
								origCmd = ccmd;
							}
							int promptMsg = ((Integer)cpromptCommands.get(ccmd)).intValue();
							// Due to a quirk on T637 (MIDP 1.0), we need to create a form
							// before the alert or the alert will not be seen.
							// FIX
							Form formAlert = new Form(ccmd.getLabel());
							formAlert.append(BaseApp.messages[promptMsg]);
							formAlert.addCommand(BaseApp.cOK);
							formAlert.addCommand(BaseApp.cBACK);
							formAlert.setCommandListener(this);
							BaseApp.setDisplay(formAlert);
							/* FIX
							Alert promptAlert = new Alert(ccmd.getLabel(),
									promptMsg, null,
									AlertType.CONFIRMATION);
							promptAlert.setTimeout(Alert.FOREVER);
							promptAlert.addCommand(new Command("OK", Command.OK, 0));
							promptAlert.addCommand(new Command("Cancel", Command.CANCEL, 1));
							promptAlert.setCommandListener(this);
							BaseApp.setDisplay(promptAlert, formAlert);
							*/
							BaseApp.setDisplay(formAlert);
						} else if (cdisp.equals(disp)) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine("Equal cdisp,disp,cmdFeatureUser=" + ccmd.getLabel() + "," + cdisp + "," + disp + "," + cmdFeatureUser);}
							//#endif
							cmdFeatureUser.commandAction(ccmd, cdisp);
							if (background && (runFeatureUser != null)) {
								runFeatureUser.run();
							}
						}
						if (!cdisp.equals(disp)) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine("Not equal corigCmd,cdisp,disp=" + ((corigCmd == null) ? "corigCmd null" : corigCmd.getLabel()) + "," + cdisp + "," + disp);}
							//#endif

							try {
								if ((ccmd == BaseApp.cOK)
								//#ifdef DMIDP20
									   || ccmd.equals(Alert.DISMISS_COMMAND)
								//#endif
										) {
									//#ifdef DLOGGING
									if (fineLoggable) {
										logger.fine("corigCmd,type=" + corigCmd.getLabel() + "," + corigCmd.getCommandType());
									}
									//#endif
									BaseApp.setDisplay(disp);
									cmdFeatureUser.commandAction(corigCmd, disp);
									if (background && (runFeatureUser != null)) {
										runFeatureUser.run();
									}
								} else if (ccmd == BaseApp.cBACK) {
									BaseApp.setDisplay(disp);
								}
							} finally {
								synchronized(this) {
									origCmd = null;
								}
							}
						}
					} catch (Throwable e) {
						//#ifdef DLOGGING
						logger.severe("commandAction caught ", e);
						//#endif
						System.out.println("commandAction caught " + e + " " + e.getMessage());
					} finally {
						synchronized(this) {
							exCmd = null;
							exDisp = null;
						}
					}
				} else {
					if (background && (runFeatureUser != null)) {
						runFeatureUser.run();
					}
				}
				lngStart = System.currentTimeMillis();
				lngTimeTaken = System.currentTimeMillis()-lngStart;
				if(lngTimeTaken<100L) {
					synchronized(this) {
						if (loop-- <= 0) {
							super.wait(75L-lngTimeTaken);
						}
					}
				}
			} catch (InterruptedException e) {
				break;
			}
		} while (background);
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable cdisp) {
		synchronized(this) {
			this.exCmd = cmd;
			this.exDisp = cdisp;
		}
		startWakeup(true);
	}

	public void startWakeup(boolean wakeupThread) {
		if ( (netThread == null) || !netThread.isAlive() ) {
			try {
				//#ifdef DCLDCV11
				netThread = new Thread(this, "T" + disp.getClass().getName());
				//#else
				netThread = new Thread(this);
				//#endif
				netThread.start();
			} catch (Exception e) {
				System.err.println("Could not restart thread.");
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("Could not restart thread.", e);
				//#endif
			}
			//#ifdef DLOGGING
			logger.info(this.getClass().getName() +
					" thread not started.  Started now.");
			//#endif
		} else if (wakeupThread) {
			wakeup(3);
		}
	}

	/* Notify us that we are finished. */
	public void wakeup(int loop) {
    
		synchronized(this) {
			this.loop += loop;
			super.notify();
		}
	}

    public void setBackground(boolean background) {
        this.background = background;
    }

	//#ifdef DMIDP20
	/* Get the font size. This is the actual size of the font */
	final public int getFontSize(int fontChoice) {
		int fontSize;
		switch (fontChoice) {
			case 1:
				fontSize = Font.SIZE_SMALL;
				break;
			case 2:
				fontSize = Font.SIZE_MEDIUM;
				break;
			case 3:
				fontSize = Font.SIZE_LARGE;
				break;
			case DEFAULT_FONT_CHOICE:
			default:
				fontSize = Font.getDefaultFont().getSize();
				break;
		}
		return fontSize;
	}

	public void initFont() {
		if (fontChoice == DEFAULT_FONT_CHOICE) {
			this.font = Font.getDefaultFont();
		} else {
			Font defFont = Font.getDefaultFont();
			this.font = Font.getFont(Font.FACE_SYSTEM, defFont.getStyle(),
					getFontSize(fontChoice));
		}
		/* UNDO
        final int fitPolicy = midlet.getSettings().getFitPolicy();
        if (fitPolicy != List.TEXT_WRAP_DEFAULT) {
			super.setFitPolicy(fitPolicy);
		}
		*/
	}
	//#endif

}
