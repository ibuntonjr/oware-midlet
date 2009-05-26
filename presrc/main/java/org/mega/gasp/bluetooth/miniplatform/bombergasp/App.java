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

package org.mega.gasp.bluetooth.miniplatform.bombergasp;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.mega.gasp.bluetooth.miniplatform.*;

import javax.microedition.lcdui.game.GameCanvas;

import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.Application;

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
public class App extends Application implements CommandListener, org.mega.gasp.bluetooth.miniplatform.App{
    
    protected Client gc;
    private Server gs;
    private SubApplicationInstance sAI;
    private int minA = 2;
    private int maxA = 4;
    public static Display display;
    
    private final Command EXIT_CMD = new Command("Exit",Command.EXIT,2);
    private final Command OK_CMD = new Command("OK",Command.SCREEN,1);
    protected SendData dataflu;
    private static String [] demos = { "Serveur", "Client"};
    private List mainMenu = new List("Client ou Serveur?", List.IMPLICIT,demos, null) ;
    private List chooseClientDevices = new List("Choix des clients",List.MULTIPLE);
    private List chooseServer = new List("Choix du serveur",List.IMPLICIT);
    
    private boolean isServer;
    private boolean isServerReady;
    int mode;
    private CustomTypes ct;
    static private App singleton;
    public static App getInstance() { return singleton; }
    
    protected Game gameCanvas;
    
    //je comprends pas trop pourkoi il y a 2 clients
    // protected Client httpTest;
    //pas besion pour notre programme. Je pense
    // protected SendData dataflu;
    
    public App(){
        isServerReady = false; //is the vector of actorSessions instanciated
        
        ct = new CustomTypes();
        
        /* choice between client and server - first screen*/
        mainMenu.addCommand(EXIT_CMD);
        mainMenu.addCommand(OK_CMD);
        mainMenu.setCommandListener(this);
        
        /* choose the client devices - second screen server side*/
        chooseClientDevices.addCommand(EXIT_CMD);
        chooseClientDevices.addCommand(OK_CMD);
        chooseClientDevices.setCommandListener(this);
        
        /* choose the server - second screen client side */
        chooseServer.addCommand(EXIT_CMD);
        chooseServer.addCommand(OK_CMD);
        chooseServer.setCommandListener(this);
        
        mode = 0; //choix S/C
    }
    
    public void startApp(){
        singleton=this;
        show();
    }
    
    protected void startAppGame() {
        try{
            gameCanvas = new Game();
            startGame();
        } catch(Exception e){
            Utils.logClient("Unable to start game");
            e.printStackTrace();
        }
    }
    
    public void pauseApp() {
        if(Display.getDisplay(this).getCurrent() == gameCanvas){
            //gameCanvas.pause();
        }
    }
    
    public void destroyApp(boolean unconditional) throws MIDletStateChangeException{
        if(Display.getDisplay(this).getCurrent() == gameCanvas){
            //gameCanvas.stop();
        }
    }
    
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
                display.setCurrent(mainMenu);
                break;
            case 1 : //2nd screen server side
                display.setCurrent(chooseClientDevices);
                break;
            case 2: //2nd screen client side
                display.setCurrent(chooseServer);
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
    
    public void commandAction(Command c, Displayable d){
        if (c == EXIT_CMD) {
            try {
                destroyApp(true);
            } catch (MIDletStateChangeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            notifyDestroyed();
            return;
        }
        if (c == OK_CMD && mode == 0) // client or server
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
        
        if (c == OK_CMD && mode == 1) //choose client devices
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
        if (c == OK_CMD && mode == 2) //choose server
        {
            int indexServer = chooseServer.getSelectedIndex();
            gc.deviceSelected(indexServer);
            c = null;
        }
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
