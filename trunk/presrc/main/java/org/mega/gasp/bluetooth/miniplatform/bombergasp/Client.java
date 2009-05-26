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
package org.mega.gasp.bluetooth.miniplatform.bombergasp;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import java.util.Hashtable;
import org.mega.gasp.bluetooth.miniplatform.GASPClient;
import org.mega.gasp.bluetooth.miniplatform.SubApplicationInstance;


/**
 *
 * @author Sebire - Negre 
 *
 *Ici tous les gameCanvas sont en commentaire car pas encore intégré
 *Il faut juste les decommenter
 */
public class Client extends GASPClient implements CommandListener, Runnable {
    
    private Command joinCommand = new Command("Join", Command.SCREEN, 1);
    private Command loginCommand = new Command("Login", Command.ITEM, 1);
    private Command createCommand = new Command("Create", Command.ITEM, 1);
    private Command startCommand = new Command("Start", Command.ITEM, 1);
    private Command quitAICommand = new Command("QuitSession", Command.ITEM, 1);
    private Command endCommand = new Command("Pause", Command.ITEM, 1);
    private Command quitCommand = new Command("Quit", Command.EXIT, 1);
    protected Vector otherPlayers;
    private App midlet;
    private Display display;
    /** owner actor session ID : identifiant du owner (createur de la partie) dans la partie en cours */
    private int ownerASID;
    private boolean isOwner;
    /** liste des asID des autres joueurs dans la partie en cours */
    protected  Vector gameSessionPlayers;
    /** tableau des Applications Instances IDs (identifiants des parties de ce jeu en cours) */
    private int[] currentAIIDList;
    /** true si le joueur entamme une nouvelles partie, false sinon */
    private boolean newGame=true;
    private boolean firstTime;
    /**
     * mode, player is in:
     * 0 -> mainscreen
     * 1 -> lobby chooseScreen
     * 2 -> waitingRoom
     * 3 -> inGame
     * 4 -> waitingRoom avec start
     * 5 -> startRoom
     */
    protected int mode;
    /** Objet de synchronization : utilisation de la variable mode dans différents threads */
    protected Object modifMode = new Object();;
    /** Array of target locations. */
    private Vector appInstances;
    /** User interface list for selection. */
    private List list;
    /** Current command to proccess. */
    private Command currentCommand;
    /** The current command processing thread. */
    private Thread commandThread;
    /** Message area. */
    private TextBox t;
    
    protected int NumeroJoueur;
    
    public Client(App parent, CustomTypes ct) {
        super(parent,ct);
        midlet=App.getInstance();
        display=Display.getDisplay(midlet);
        gameSessionPlayers = new Vector();
        
        appInstances = new Vector();
        firstTime=true;
        initCustomTypes();
        setMode(2);
        otherPlayers=new Vector();
    }
    
    public void initCustomTypes() {
        customTypes = new CustomTypes();
    }
    
    public void setMode(int mode){
        this.mode = mode;
    }
    
    public Object getDisplay() {
        return display;
    }
    
    public void startApp() {
        System.err.println("CLIENT START");
        if(firstTime) {
            join();
            currentScreen();
            firstTime = false;
        } else {
            display.setCurrent(t);
        }
    }
    
    /*Perform the current command set by the method commandAction.*/
    public void run() {
        if (currentCommand == joinCommand) {
            synchronized(this.modifMode){
                setMode(2);
                join();
                //pkoi ici pas de currentScreen() ????
            }
        } else if (currentCommand == startCommand) {
            synchronized(this.modifMode){
                setMode(3);
                startAI();
                if(!this.midlet.isServer())
                    currentScreen();
                
            }
        } else if (currentCommand == quitAICommand) {
            synchronized(this.modifMode){
                setMode(1);
                quitAI();
                currentScreen();
            }
        } else if (currentCommand == quitCommand) {
            quit();
            //pkoi ici pas de currentScreen() ????
        } else if (currentCommand == endCommand) {
            synchronized(this.modifMode){
                setMode(4);
                endAI();
                currentScreen();
            }
        } synchronized (this) {
            // signal that another command can be processed
            commandThread = null;
        }
    }
    
    /*Gestion des commandes*/
    
    public void commandAction(Command c, Displayable s){
        synchronized (this) {
            if (commandThread != null) {
                // process only one command at a time
                return;
            }
            currentCommand = c;
            commandThread = new Thread(this);
            commandThread.start();
        }
    }
    
    void setCommands(Displayable d, int commandMode) {
        switch (commandMode){
            case 0:
                d.addCommand(loginCommand);
                break;
            case 1: // Lobby screen with no game session
                d.addCommand(createCommand);
                d.addCommand(quitCommand);
                break;
            case 2: // Lobby screen with game session
                d.addCommand(createCommand);
                d.addCommand(joinCommand);
                d.addCommand(quitCommand);
                break;
            case 3: // Waiting room
                d.addCommand(quitAICommand);
                d.addCommand(quitCommand);
                break;
            case 5: // Waiting room
                if (this.aSID == SubApplicationInstance._LOC_ASID) {d.addCommand(startCommand);   System.err.println("_loc_asid oui");}
                
                d.addCommand(quitAICommand);
                d.addCommand(quitCommand);
                break;
            case 6: // Start room
                d.addCommand(startCommand);
                d.addCommand(quitCommand);
                break;
            case 4: // In Game room commands
                if (App.getInstance().isServer()) d.addCommand(endCommand);
                d.addCommand(quitAICommand);
                d.addCommand(quitCommand);
                break;
            default: break;
        }
        d.setCommandListener(this);
    }
    /*Gestion des commande du jeu*/
    /* Le joueur rejoint une partie*/
    private void join() {
        aSID = joinAI(0,0);
        return;
    }
    
    protected void startAI() {
        startAI(aSID);
    }
    
    public void startGame() {
        // TODO Auto-generated method stub
    }
    
    /*Met la partie en cours en pause*/
    private void endAI() {
        //this.midlet.gameCanvas.pauseAction();
        endAI(aSID);
    }
    
    /*Quitte la partie en cours mais reste connecté*/
    private void quitAI() {
        quitAI(aSID);
        reInit();
        //if(this.midlet.gameCanvas!=null) this.midlet.gameCanvas.quit();
    }
    
    /*Fin de partie, perdue*/
    protected void loose() {
        synchronized(this.modifMode){
            alertScreen("Game Over");
            if (App.getInstance().isServer()) setMode(4);
            else setMode(2);
            newGame=true;
            
             midlet.gameCanvas.quit();
            currentScreen();
        }
    }
    
    synchronized void alertScreen(String s) {
        t = new TextBox("Alert", s, s.length(), 0);
        display.setCurrent(t);
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /* Fin de partie, gagnée*/
    protected void win() {
        synchronized(this.modifMode){
            alertScreen("Tu es un Winner");
            if (App.getInstance().isServer()) setMode(4);
            else setMode(2);
            newGame=true;
            
            midlet.gameCanvas.quit();
            currentScreen();
        }
    }
    
    /*Quitte la partie en cours en se deconnectant*/
    protected void quit() {
        if (mode!= 1){
            // player in waiting room or in game
            quitAI();
        }
        quit(aSID);
        midlet.exitRequested();
        
    }
    
    /*Reinitialisation des variables en fin de partie*/
    public void reInit(){
        System.err.println("REINIT");
        aSID = 0;
        gameSessionPlayers.removeAllElements();
        newGame=true;
    }
    
    public void endGame() {
        // TODO Auto-generated method stub
    }
    
    /*Gestion des evenements*/
    
    /**
     * Lancement de la partie
     * @param actorSessionID du joueur qui a lancé la partie
     */
    public void onStartEvent(int actorSessionID) {
        synchronized(this.modifMode){
            alertScreen("The game session owner starts the game!");
            System.err.println("onStartEvent dans Client");
            setMode(3);
            currentScreen();
            
        }
    }
    
    public void onMyJoinEvent(int actorSessionID) {
        Utils.logClientHL("MyJoinEvent:" + actorSessionID);
        NumeroJoueur=actorSessionID-1;
        this.aSID=actorSessionID;
        currentScreen();
    }
    
    /**
     * Mise a jour de la liste des joueurs en waitingroom.
     * Si le bon nombre de joueur est atteind, la owner lance la partie.
     * @param actorSessionID du joueur qui a rejoint la partie
     */
    public void onJoinEvent(int actorSessionID) {
        
        // the first player join the application instance was the owner
        Utils.logClientHL("JoinEvent:" + actorSessionID);
        if (actorSessionID == this.aSID) return;
        
        int i = gameSessionPlayers.size();
        gameSessionPlayers.insertElementAt(new Integer(actorSessionID),i);
        if (actorSessionID == SubApplicationInstance._LOC_ASID){
            ownerASID = actorSessionID;
            alertScreen("The game session owner joins: aSID>"+actorSessionID);
        } else{
            alertScreen("A new player joins: aSID>"+actorSessionID);
        }
        otherPlayers.insertElementAt(new Integer(actorSessionID-1),i);
        if(actorSessionID == SubApplicationInstance._LOC_ASID && gameSessionPlayers.size()>2) {
            setMode(5);
        }
        setMode(4);
        currentScreen();
    }
    
    
    public void onDataEvent(int actorSessionID, Hashtable datas) {
     /*
      *Types d'evenements possibles :
      *Update : update de la postion des joueur non local
      *Delete : suppression d'un joueur non local ou d'un bonus non prit par le joueur local
      *DeleteTiles : destruction d'une brique
      *PutBomb : poser une bombe
      *ExplodeBomb : exposion d'une bombe
      */
        Utils.logClientHL("onDataEvent");
        int nb = datas.size();
        for (int i=0; i<nb; i++) {
            Object current = datas.get(""+i);
            if (current instanceof Update){
                            /*
                             *Il faut faire une difference entre les differents joueurs
                             *Il y a le joueur local est les autres
                             *Ici on s'occupe que des autres joueurs (le local ne recoit pas de DataEvent update car il sait ou il se trouve )
                             *Les autres joueurs sont des joueurs locaux mais sont qd meme different
                             *Ils n'ont pas la meme gestion des mouvements et des contacts à caude de la période de latence (dans freepussy cela correspond au Phantom )
                             *
                             *Il faut donc suivant l'id de l Update obtenu deplacer tel ou tel joueur
                             */
                for (Enumeration $myEnum1 = Game.otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
                    Avatar av=((Avatar)$myEnum1.nextElement());
                    if(av.id==((Update)current).getId()){
                        av.x=((Update)current).getX();
                        av.y=((Update)current).getY();
                        av.direction=((Update)current).getDir();
                        if(!(av.moving=((Update)current).getMov()))
                            av.step=0;
                        //  av.lastmove=Game.time;
                        //   av.laststep=Game.time; //DEBUT DE L'ANIM
                        
                    }
                }
                //if (SimpleGameCanvas.phantom1.getId()==((Update)current).getId()){
                //	SimpleGameCanvas.phantom1.setX(((Update)current).getX());
                //	SimpleGameCanvas.phantom1.setY(((Update)current).getY());
                //}
                //else{
                //	SimpleGameCanvas.phantom2.setX(((Update)current).getX());
                //	SimpleGameCanvas.phantom2.setY(((Update)current).getY());
                //}
            } else  if (current instanceof Delete){
                int id=((Delete)current).getId(),nbrAlive=0;
                if(Game.getHeroe().id==id){
                    Game.getHeroe().setDyingFromServer();
                    for (Enumeration $myEnum1 = Game.otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
                        Avatar b=(Avatar)$myEnum1.nextElement();
                        if(!b.dead)
                            nbrAlive++;
                    }
                } else{
                    for (Enumeration $myEnum1 = Game.otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
                        Avatar b=(Avatar)$myEnum1.nextElement();
                        if(id==b.id){
                            b.setDyingFromServer();
                        }
                        if(!b.dead)
                            nbrAlive++;
                    }
                }
                if(nbrAlive==1 && Game.getHeroe().dead)
                    loose();
                else if(nbrAlive==0 && !Game.getHeroe().dead)
                    win();
                //on fait une recherche si le joueur ou le bonus existe
                                /*Actor actor = GameCanvas.getInstance().searchActor(id);
                                if (actor!=null){
                                 //Remplacer Phantom par autre joureur ou avatar
                                        if(actor instanceof Phantom){
                                                synchronized(this.modifMode){
                                                        //Supprimer le joueur de l'ecran ( peut pas etre le joueur local car on utilise la fonction loose() por cela)
                                                }
                                        }
                                        if(actor instanceof Bonus){
                                                synchronized(this.modifMode){
                                                        //Supprimer le Bonus
                                                }
                                        }
                                 
                                }*/
            } //else if (current instanceof DeleteTiles){
//                //On supprime une brique avec comme coordonnées M ((DeleteTiles)current).getM() et N ((DeleteTiles)current).getN()
//                //Si il y a un bonus il ne faut pas l'oublier de l'afficher'
//                //TiledWorld.getSingleton().decreaseStrength(((DeleteTiles)current).getM(),((DeleteTiles)current).getN(),System.currentTimeMillis(),false);
//            }
            //Je pense que je fais une erreur avec cette classe d'evenement car on peu supprimer le bonus
            //avec la classe d'evenement delete car la je fais un doublon je pense
            //C'est suivant si on uilise les coordonnées pour reperer le bonus
            //( alors il faut modifier la classe DeleteBonus, CustomTypes et sendData) ou l'id
            else if (current instanceof DeleteBonus){
                int id=((DeleteBonus)current).getId();
                if(Game.getHeroe().id==id)
                    Game.getHeroe().useItem(((DeleteBonus)current).type,((DeleteBonus)current).x,((DeleteBonus)current).y);
                else{
                    for (Enumeration $myEnum1 = Game.otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
                        Avatar b=(Avatar)$myEnum1.nextElement();
                        if(id==b.id){
                            b.useItem(((DeleteBonus)current).type,((DeleteBonus)current).x,((DeleteBonus)current).y);
                            break;
                        }
                    }
                }
            }
            //faut il rajouter poser et faire exploser la bombe avec comme identifiant des coordonnées ???
            else if (current instanceof PutBomb){
                Game.map.addBomb(new Bomb(((PutBomb)current).getX(),((PutBomb)current).getY(),((PutBomb)current).getPuis(),System.currentTimeMillis()));
                //On affiche une bombe aux coordonnées x (PutBomb)current).getX() et y (PutBomb)current).getY()
            } else if (current instanceof ExplodeBomb){
                for (Enumeration $myEnum1 = Map.bombs.elements(); $myEnum1.hasMoreElements(); ) {
                    Bomb b=((Bomb)$myEnum1.nextElement());
                    if(b.x==((ExplodeBomb)current).getX() && b.y==((ExplodeBomb)current).getY() ){
                        b.mustexplose=true;
                        break;
                    }
                }
                //On fait exploser la bombe aux coordonnées x (PutBomb)current).getX() et y (PutBomb)current).getY() et puissance (PutBomb)current).getPuissance()
            } else if (current instanceof DoItems){
              System.err.println ("g recu doItems="+((DoItems)current).getItems());
              Map.ItemsFromServer(((DoItems)current).getItems());
             }
        }
        
    }
    //	currentScreen();
    
    
    /**
     * Mise en pause de la partie
     * @param actorSessionID du joueur qui a executé le end
     */
    public void onEndEvent(int actorSessionID) {
        synchronized(this.modifMode){
            if(this.mode==3){
                alertScreen("The game session owner has stopped the game! Return to the waiting room.");
                //if (this.midlet.gameCanvas!=null) this.midlet.gameCanvas.pauseAction();
                setMode(2);
                currentScreen();
            }
        }
    }
    
    /**
     * Si le owner a quitté la partie, le joueur quit la partie en cours et retourne dans le lobbyroom
     * sinon quit puis retour dans la waiting room
     * @param actorSessionID du joueur qui a quitté la partie
     */
    public void onQuitEvent(int actorSessionID) {
        synchronized(this.modifMode){
            if (ownerASID == actorSessionID){
                setMode(1);
                alertScreen("The game session owner has leaved the game, the game session has been distructed.\nReturn to the Lobby choose room.");
                reInit();
                //if(this.midlet.gameCanvas!=null) this.midlet.gameCanvas.quit();
                currentScreen();
            } else{
                setMode(2);
                alertScreen("The player n°"+actorSessionID+" has leaved the game");
                if (App.getInstance().isServer()) endAI(this.aSID);
                removePlayer(actorSessionID);
                
                newGame = true;
                //if(this.midlet.gameCanvas!=null) this.midlet.gameCanvas.quit();
                currentScreen();
            }
        }
    }
    
    
    /**
     * Remove the player corresponding to the actorSessionID.
     * @param actorSessionID
     */
    private void removePlayer(int actorSessionID) {
        for(int i=0; i<gameSessionPlayers.size();i++){
            if (((Integer) gameSessionPlayers.elementAt(i)).intValue() == actorSessionID){
                gameSessionPlayers.removeElementAt(i);
            }
        }
    }
    
    
    /*Gestion des ecrans*/
    /**
     * This methods provides to draw the appropriate screen.
     */
    protected void currentScreen() {
        switch (this.mode){
            case 0:
                mainScreen();
                break;
            case 1:
                lobbyScreen();
                break;
            case 2:
                waitingRoomScreen(true);
                break;
            case 4:
                waitingRoomScreen(false);
                break;
            case 5:
                startRoomScreen();
                break;
            case 3:
                gameRoom();
                break;
            default: break;
        }
    }
    
    /**
     * Display the main screen.
     */
    void mainScreen() {
        String s = "Hi, click join to enter the MGP";
        t = new TextBox("GPClientTest", s, s.length(), 0);
        setCommands(t, 0);
        mode=2;
        Utils.logClientHL("mainScreen mode:"+mode);
        
        display.setCurrent(t);
    }
    
    
    
    /**
     * Display the Lobby screen.
     */
    void lobbyScreen() {
        try{
            appInstances = getApplicationInstances(aSID);
            int  indexMax = appInstances.size();
            currentAIIDList = new int[indexMax];
            if (indexMax == 0){
                String s = "Currently no game sessions to join";
                t = new TextBox("Lobby", s, s.length(), 0);
                setCommands(t, 1);
                t.setCommandListener(this);
                display.setCurrent(t);
            } else{
                int current = -1;
                if (list!=null) current = list.getSelectedIndex();
                list = new List("Lobby", Choice.EXCLUSIVE);
                list.setCommandListener(this);
                for (int i = 0; i < appInstances.size(); i++) {
                    ApplicationInstanceInfos appInfos = (ApplicationInstanceInfos)appInstances.elementAt(i);
                    int aIID = appInfos.getApplicationInstanceID();
                    currentAIIDList[i] = aIID;
                    int nbP = appInfos.getNbPlayers();
                    list.append("ApplicationInstanceID: "+aIID+ " nbPlayers:"+ nbP, null);
                }
                if (current!=-1)
                    if(current<list.size()) list.setSelectedIndex(current,true);
                    else list.setSelectedIndex(list.size()-1,true);
                setCommands(list, 2);
                display.setCurrent(list);
            }
        } catch (Exception e){e.printStackTrace();}
    }
    
    /**
     * Display the Waiting room screen.
     */
    void waitingRoomScreen(boolean type) {
        try{
            list = new List("Waiting Room - Players", Choice.EXCLUSIVE);
            //Nom du joueur du telephone
            list.append("You: aSID>"+aSID+"type>"+NumeroJoueur,null);
            list.setCommandListener(this);
            for (int i = 0; i < gameSessionPlayers.size(); i++) {
                Integer aSIDOf = (Integer)gameSessionPlayers.elementAt(i);
                //Noms des joueurs dans la Waiting Room
                if (aSIDOf.intValue()== ownerASID) list.append("Owner: aSID>"+aSIDOf.intValue(),null);
                else list.append("Player: aSID>"+aSIDOf.intValue(),null);
            }
            if (type) setCommands(list, 3);
            else setCommands(list, 5);
            display.setCurrent(list);
        } catch (Exception e){e.printStackTrace();}
    }
    
    /**
     * Display the start screen.
     */
    void startRoomScreen() {
        try{
            String s = "Vous pouvez lancer la partie";
            t = new TextBox("Start Room", s, s.length(), 0);
            setCommands(t, 6);
            t.setCommandListener(this);
            display.setCurrent(t);
        } catch (Exception e){e.printStackTrace();}
    }
    
    
    /**
     * Display the Waiting room screen.
     */
    void gameRoom() {
        try{
            if(newGame){
                alertScreen("Chargement de la partie en cours...");
            }
            if(this.midlet.gameCanvas==null) {
                this.midlet.startAppGame();
                newGame=false;
            } else {
                if (newGame) {
                    midlet.startGame();
                    newGame=false;
                } else {
                    midlet.gameCanvas.restart();
                    System.err.println("startGame dans gameRoom");
                }
            }
            setCommands(midlet.gameCanvas, 4);
            midlet.gameCanvas.setCommandListener(this);
            display.setCurrent(midlet.gameCanvas);
        } catch (Exception e){e.printStackTrace();}
        
    }
}
