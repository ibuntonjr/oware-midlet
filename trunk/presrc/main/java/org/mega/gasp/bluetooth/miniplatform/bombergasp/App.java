/***
 * BomberGASP: a GASP Bluetooth (GASPBT) middleware based "Bomberman-like" game.
 * Copyright (C) 2006 CNAM/INT
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Contact: gasp-team@objectweb.org
 *
 * Author: Sebire - Negre
 */
/**
 * This was modified no later than 2009-05-26 by Irving Bunton, Jr
 */

// Expand to define DJSR82 define
@DJSR82@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR82
package org.mega.gasp.bluetooth.miniplatform.bombergasp;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.mega.gasp.bluetooth.miniplatform.SubApplicationInstance;
import org.mega.gasp.bluetooth.miniplatform.GASPClient;

import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.Application;
import com.substanceofcode.rssreader.presentation.FeatureList;
import net.sf.yinlight.boardgame.oware.midlet.AppConstants;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif

/**
 *
 * @author Sebire - Negre
 * @version
 *
 *A Modifier
 *Décommenter le code avec GameCanvas car il manque des fonctions qui faut ajouter dans GameCanvas
 * comme par exemple pause, newGame, restart comme dans FreePussy
 *Peut etre on devrait faire comme dans freePussy avec toutes ses methodes ainsi que run dans GameCanvas
 *et faire une classe fille Game avec des methodes plus caracteristiques du jeu. A voir !!!!!!
 *
 */
abstract public class App extends Application implements org.mega.gasp.bluetooth.miniplatform.App{

  public static final String RES_MSGS = "messages.txt";
	protected Client gc;
	private Server gs;
	private SubApplicationInstance sAI;
	protected int minA = 2;
	protected int maxA = 4;
	public static Display display;

	protected SendData dataflu;
	private static String [] demos = { "Server", "Client"};
	private List mainMenu = new FeatureList("Client or Server?", List.IMPLICIT,demos, null) ;
	private List chooseClientDevices = new FeatureList("Choice of clients",List.MULTIPLE);
	private List chooseServer = new FeatureList("Choice of server",List.IMPLICIT);

	private boolean isServer;
	private boolean isServerReady;
	int mode;
	private CustomTypes ct;
	static private App singleton;
	public static App getInstance() { return singleton; }

	protected Game gameCanvas;

	//#ifdef DLOGGING
	private boolean fineLoggable;
	private boolean finestLoggable;
	private boolean traceLoggable;
	private Logger logger;
	//#endif

	//je comprends pas trop pourkoi il y a 2 clients
	// protected Client httpTest;
	//pas besion pour notre programme. Je pense
	// protected SendData dataflu;

		//#ifdef DJMTEST
	public App(String name)
		//#else
	public App()
		//#endif
	{
		//#ifdef DJMTEST
    super(name);
		//#else
		super();
		//#endif

		//#ifdef DLOGGING
		logger = Logger.getLogger("App");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif

		isServerReady = false; //is the vector of actorSessions instanciated

		ct = new CustomTypes();

		mode = 0; //choix S/C
	}

	/**
	 * Application initialization
	 */
	protected void init() {
		super.init();
		singleton=this;
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("init");}
		//#endif
		try {
			BaseApp.messages = BaseApp.readStrings(App.RES_MSGS);
			BaseApp.cEXIT = BaseApp.newCommand(AppConstants.MSG_LABEL_EXIT, Command.EXIT, 2, BaseApp.AC_EXIT);
			BaseApp.cOK = BaseApp.newCommand(AppConstants.MSG_LABEL_OK, Command.OK, 30,  BaseApp.AC_NONE);
			/* choice between client and server - first screen*/
			BaseApp.setup(mainMenu, BaseApp.cOK, BaseApp.cEXIT);

			/* choose the client devices - second screen server side*/
			BaseApp.setup(chooseClientDevices, BaseApp.cOK, BaseApp.cEXIT);

			/* choose the server - second screen client side */
			BaseApp.setup(chooseServer, BaseApp.cOK, BaseApp.cEXIT);

			show();
		} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
		}
	}

	abstract protected void startAppGame();

	abstract protected void pause();

	public void done(){}

	public int getMinA(){
		return minA;
	}
	public int getMaxA(){
		return maxA;
	}

	public boolean isServer() {
		return isServer;
	}


	public List getServerList() {
		return chooseServer;
	}

	public void show() {
		display=Display.getDisplay(this);

		switch(mode) {
			case 0 : //1st screen
				// TODO remove display.setCurrent(mainMenu);
				BaseApp.show(null, mainMenu, false);
				break;
			case 1 : //2nd screen server side
				// TODO remove display.setCurrent(chooseClientDevices);
				BaseApp.show(null, chooseClientDevices, false);
				break;
			case 2: //2nd screen client side
				// TODO remove display.setCurrent(chooseServer);
				BaseApp.show(null, chooseServer, false);
				break;
			default :
				break;
		}

	}

	public GASPClient getClient() {
		return gc;
	}

	public List getClientDevicesList(){
		return chooseClientDevices;
	}

	public SubApplicationInstance getSAI() {
		return sAI;
	}

	// Implementation of the command listener interface
	public boolean handleAction(int action, final Displayable d, final Command cmd) {
		if (cmd == BaseApp.cEXIT) {
			try {
				destroyApp(true);
			} catch (MIDletStateChangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			notifyDestroyed();
			return true;
		}
		Command c = cmd;
		if (c == BaseApp.cOK && mode == 0) // client or server
		{
			switch(mainMenu.getSelectedIndex()) {

				case 0 : //server
					System.err.println("server");
					isServer = true;
					mode = 1;
					gs = new Server((App)this);

					gc=new Client((App)this,ct);

					sAI = new SubApplicationInstance((App)this,gc,gs,minA,maxA,ct);

					//C'est pour la synchro. Pas uiliser
					dataflu = new SendData();
					//
					show();
					break;

				case 1 : // client
					isServer = false;
					mode = 2;
					gc= new Client(this,ct);

					//
					dataflu = new SendData();

					//
					show();
					break;

				default :
					System.err.println("Unexpected Choice..");
					break;
			}
			c = null;
		}

		if (c == BaseApp.cOK && mode == 1) //choose client devices
		{
			int size = chooseClientDevices.size();
			int i;
			for (i=0;i<size;i++) {
				if (chooseClientDevices.isSelected(i)) {
					sAI.createNewActorSession(i);
				}
			}
			c = null;
			gc.setMode(2);
			gc.startApp();
		}
		if (c == BaseApp.cOK && mode == 2) //choose server
		{
			int indexServer = chooseServer.getSelectedIndex();
			gc.deviceSelected(indexServer);
			c = null;
		}
		return true;
	}

	//	--------------Switching Canvases -----------------------------------
	abstract public void startGame();

	//	--------------Stopping cheerfully ----------------------------------
	void exitRequested(){
		try {
			//      Logging.sendLogs(gc.aSID);
			//
			destroyApp(false);
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
		notifyDestroyed();
	}
}
//#endif
