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
import org.mega.gasp.bluetooth.miniplatform.GASPClient;

import javax.microedition.lcdui.game.GameCanvas;

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
 *et faire une classe fille Game avec des methodes plus caracteristiques of jeu. A voir !!!!!!
 *
 */
public class BomberApp extends App {

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

	public BomberApp(){
		//#ifdef DJMTEST
		super("Bomber Games Suite");
		//#else
		super();
		//#endif
		//#ifdef DLOGGING
		logger = Logger.getLogger("BomberApp");
		fineLoggable = logger.isLoggable(Level.FINE);
		finestLoggable = logger.isLoggable(Level.FINEST);
		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif

	}

	/**
	 * Application initialization
	 */
	protected void init() {
		super.init();
		minA = 2;
		maxA = 4;

	}

	protected void startAppGame() {
		try{
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("startAppGame gameCanvas=" + gameCanvas);}
			//#endif
			gameCanvas = new Game();
			startGame();
		} catch(Exception e){
			Utils.logClient("Unable to start game");
			e.printStackTrace();
		}
	}

	protected void pause() {
		if(Display.getDisplay(this).getCurrent() == gameCanvas){
			//gameCanvas.pause();
		}
	}

	public void done() {
		if(Display.getDisplay(this).getCurrent() == gameCanvas){
			//gameCanvas.stop();
		}
		super.done();
	}

	public int getMinA(){
		return minA;
	}
	public int getMaxA(){
		return maxA;
	}

	public void show() {
		if ((0 <= mode) && (mode <= 2)) {
			super.show();
			return;
		}

		/*
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
		*/

	}

	public GASPClient getClient() {
		return gc;
	}

	// Implementation of the command listener interface
	public boolean handleAction(int action, final Displayable d, final Command cmd) {
		if (super.handleAction(action, d, cmd)) {
			return true;
		}

		/*
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
					gs = new Server(this);

					gc=new Client(this,ct);

					sAI = new SubApplicationInstance(this,gc,gs,minA,maxA,ct);

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
		*/
		return true;
	}

	//	--------------Switching Canvases -----------------------------------
	public void startGame() {
		try{
			//gameCanvas.newGame();
			gameCanvas.restart();
			System.err.println("startGame dans APP");
		} catch (Exception e){
			Utils.logClient("Unable to start game");
			e.printStackTrace();
		}
	}
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
