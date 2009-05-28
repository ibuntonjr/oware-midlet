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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import java.util.Hashtable;
import org.mega.gasp.bluetooth.miniplatform.GASPClient;
import org.mega.gasp.bluetooth.miniplatform.SubApplicationInstance;
import com.substanceofcode.rssreader.presentation.FeatureList;
import com.substanceofcode.rssreader.presentation.FeatureTextBox;
import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

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
    private FeatureList list;
    /** Current command to proccess. */
    private Command currentCommand;
    /** Message area. */
    private FeatureTextBox t;
    
    protected int NumeroJoueur;
    
  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("Client");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif

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
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("onStartEvent this.modifMode=" + this.modifMode);}
		//#endif
    }
    
    public Object getDisplay() {
        return display;
    }
    
    public void startApp() {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("startApp firstTime,t=" + firstTime + "," + ((t == null) ? "null" : t.getClass().getName()) );}
				//#endif
        System.err.println("CLIENT START");
        if(firstTime) {
            join();
            currentScreen();
            firstTime = false;
        } else {
            display.setCurrent(t);
        }
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("gameRoom error", e);
				//#endif
			}
    }
    
    /*Perform the current command set by the method commandAction.*/
    public void run() {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("run this.modifMode=" + this.modifMode);}
				//#endif
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
        }
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("run error", e);
				//#endif
			}
    }
    
    /*Gestion des commandes*/
    
    public void commandAction(Command c, Displayable s){
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("commandAction c,s=" + c + "," + ((s == null) ? "null" : s.getClass().getName()) );}
				//#endif
        synchronized (this) {
            currentCommand = c;
        }
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("commandAction error", e);
				//#endif
			}
    }
    
    void setCommands(Displayable d, int commandMode) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("setCommands d,commandMode=" + ((d == null) ? "null" : d.getClass().getName()) + "," + commandMode);}
			//#endif
			try {
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
				FeatureMgr.setCommandListener(d, this, false, true);
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("setCommands error", e);
				//#endif
			}
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
        t = new FeatureTextBox("Alert", s, s.length(), 0);
        display.setCurrent(t);
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /* Fin de partie, gagnée*/
    protected void win() {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("win this.modifMode=" + this.modifMode);}
				//#endif
        synchronized(this.modifMode){
            alertScreen("Tu es un Winner");
            if (App.getInstance().isServer()) setMode(4);
            else setMode(2);
            newGame=true;
            
            midlet.gameCanvas.quit();
            currentScreen();
        }
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("win error", e);
				//#endif
			}
    }
    
    /*Quitte la partie en cours en se deconnectant*/
    protected void quit() {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("quit mode,aSID=" + mode + "," + aSID);}
				//#endif
        if (mode!= 1){
            // player in waiting room or in game
            quitAI();
        }
        quit(aSID);
        midlet.exitRequested();
        
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("quit error", e);
				//#endif
			}
    }
    
    /*Reinitialisation des variables en fin de partie*/
    public void reInit(){
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("reInit");}
				//#endif
        System.err.println("REINIT");
        aSID = 0;
        gameSessionPlayers.removeAllElements();
        newGame=true;
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("reInit error", e);
				//#endif
			}
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
			try {
				synchronized(this.modifMode){
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("onStartEvent this.modifMode,actorSessionID=" + this.modifMode + "," + actorSessionID);}
					//#endif
					alertScreen("The game session owner starts the game!");
					System.err.println("onStartEvent dans Client");
					setMode(3);
					currentScreen();
					
				}
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onStartEvent error", e);
				//#endif
			}
    }
    
    public void onMyJoinEvent(int actorSessionID) {
			try {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("onMyJoinEvent actorSessionID=" + actorSessionID);}
					//#endif
					Utils.logClientHL("MyJoinEvent:" + actorSessionID);
					NumeroJoueur=actorSessionID-1;
					this.aSID=actorSessionID;
					currentScreen();
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onMyJoinEvent error", e);
				//#endif
			}
    }
    
    /**
     * Mise a jour de la liste des joueurs en waitingroom.
     * Si le bon nombre de joueur est atteind, la owner lance la partie.
     * @param actorSessionID du joueur qui a rejoint la partie
     */
    public void onJoinEvent(int actorSessionID) {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("onJoinEvent actorSessionID=" + actorSessionID);}
				//#endif
				
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
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onJoinEvent error", e);
				//#endif
			}
    }
    
    
		public void onDataEvent(int actorSessionID, Hashtable datas) {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("onDataEvent actorSessionID,datas.size()=" + actorSessionID + "," + datas.size());}
				//#endif
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

			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onDataEvent error", e);
				//#endif
			}
		}
    //	currentScreen();
    
    
    /**
     * Mise en pause de la partie
     * @param actorSessionID du joueur qui a executé le end
     */
    public void onEndEvent(int actorSessionID) {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("onEndEvent actorSessionID=" + actorSessionID);}
				//#endif
				synchronized(this.modifMode){
					if(this.mode==3){
						alertScreen("The game session owner has stopped the game! Return to the waiting room.");
						//if (this.midlet.gameCanvas!=null) this.midlet.gameCanvas.pauseAction();
						setMode(2);
						currentScreen();
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onEndEvent error", e);
				//#endif
			}
    }
    
    /**
     * Si le owner a quitté la partie, le joueur quit la partie en cours et retourne dans le lobbyroom
     * sinon quit puis retour dans la waiting room
     * @param actorSessionID du joueur qui a quitté la partie
     */
    public void onQuitEvent(int actorSessionID) {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("onQuitEvent actorSessionID=" + actorSessionID);}
				//#endif
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
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("onQuitEvent error", e);
				//#endif
			}
    }
    
    
    /**
     * Remove the player corresponding to the actorSessionID.
     * @param actorSessionID
     */
    private void removePlayer(int actorSessionID) {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("removePlayer actorSessionID=" + actorSessionID);}
				//#endif
				for(int i=0; i<gameSessionPlayers.size();i++){
					if (((Integer) gameSessionPlayers.elementAt(i)).intValue() == actorSessionID){
						gameSessionPlayers.removeElementAt(i);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("removePlayer error", e);
				//#endif
			}
    }
    
    
    /*Gestion des ecrans*/
    /**
     * This methods provides to draw the appropriate screen.
     */
    protected void currentScreen() {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("currentScreen this.mode=" + this.mode);}
			//#endif
			try {
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
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("currentScreen error", e);
				//#endif
			}
    }
    
    /**
     * Display the main screen.
     */
    void mainScreen() {
			try {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("mainScreen");}
				//#endif
				String s = "Hi, click join to enter the MGP";
				t = new FeatureTextBox("GPClientTest", s, s.length(), 0);
				setCommands(t, 0);
				mode=2;
				Utils.logClientHL("mainScreen mode:"+mode);
				
				display.setCurrent(t);
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("mainScreen error", e);
				//#endif
			}
    }
    
    
    
    /**
     * Display the Lobby screen.
     */
    void lobbyScreen() {
        try{
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("lobbyScreen aSID=" + aSID);}
					//#endif
            appInstances = getApplicationInstances(aSID);
            int  indexMax = appInstances.size();
            currentAIIDList = new int[indexMax];
            if (indexMax == 0){
                String s = "Currently no game sessions to join";
                t = new FeatureTextBox("Lobby", s, s.length(), 0);
                setCommands(t, 1);
                t.setCommandListener(this, false, true);
                display.setCurrent(t);
            } else{
                int current = -1;
                if (list!=null) current = list.getSelectedIndex();
                list = new FeatureList("Lobby", Choice.EXCLUSIVE);
                list.setCommandListener(this, false, true);
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
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("waitingRoomScreen type=" + type);}
					//#endif
            list = new FeatureList("Waiting Room - Players", Choice.EXCLUSIVE);
            //Nom du joueur du telephone
            list.append("You: aSID>"+aSID+"type>"+NumeroJoueur,null);
            list.setCommandListener(this, false, true);
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
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("startRoomScreen");}
					//#endif
            String s = "Vous pouvez lancer la partie";
            t = new FeatureTextBox("Start Room", s, s.length(), 0);
            setCommands(t, 6);
            t.setCommandListener(this, false, true);
            display.setCurrent(t);
        } catch (Exception e){e.printStackTrace();}
    }
    
    
    /**
     * Display the Waiting room screen.
     */
		void gameRoom() {
			try{
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("gameRoom newGame,this.midlet.gameCanvas=" + newGame + "," + this.midlet.gameCanvas);}
				//#endif
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
			} catch (Exception e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("gameRoom error", e);
				//#endif
			} catch (Throwable e) {
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("gameRoom error", e);
				//#endif
			}

		}
}
//#endif
