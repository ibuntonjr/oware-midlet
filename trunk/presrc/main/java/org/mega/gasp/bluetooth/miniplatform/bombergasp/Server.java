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
import org.mega.gasp.bluetooth.miniplatform.Event;
import org.mega.gasp.bluetooth.miniplatform.GASPServer;

import java.util.Vector;
import org.mega.gasp.bluetooth.miniplatform.ActorSession;
/**
 *
 * @author Sebire - Negre
 */
public class Server extends GASPServer{
    Vector v;
    App app;
    //Les differents joueurs sont rangés dans un tableau
    //peut etre remplacé par un vecteur mais peut etre pas nécessaire
    //maximum de 4 joueurs dans una app
    private int [] joueurs = {-1,-1,-1,-1};
    
	public Server(App app){
            //la fonction log permet juste de faire un affichage
		log("Server construction");
		this.app=app;
                log("joueur1 : "+joueurs[0]);
		log("joueur2 : "+joueurs[1]);
		log("joueur3 : "+joueurs[2]);
                log("joueur4 : "+joueurs[3]);
	}

    //a modifier si on utilise un vecteur !!!
    public int [] getJoueurs(){
	return joueurs;
    }
    
    public void setJoueurI(int i,int j){
	if (i<4){
		joueurs[i]=j;
	}
    }
    
    public void setJoueur(int j){
	int i = 0;
	while (i<4 && joueurs[i]!=-1){	
		i++;
	}
	setJoueurI(i,j);
    }
    
    public void onJoinEvent(Event je) {
         log("SERVER on joinEvent OUT");
    }

    public void onStartEvent(Event se) {
        log("StartEvent received by server: aSID>"+se.getASID());
	start();
    }

    public void onEndEvent(Event ee) {
        log("EndEvent received by server: aSID>"+ee.getASID());
	end();
    }

    public void onQuitEvent(Event qe) {
        log("QuitEvent received by server: aSID>"+qe.getASID());
        int quiter = qe.getASID();
        //le joueur 0 ne peut pas quitter la partie car c'est le serveur
	if(quiter == joueurs[1]){
		setJoueurI(1,-1);
		log("le joueur 2 quitte le jeu");
		log("joueur 1 :"+joueurs[0]);
		log("joueur 2 :"+joueurs[1]);
		log("joueur 3 :"+joueurs[2]);
                log("joueur 4 :"+joueurs[3]);
	}
	else if(quiter == joueurs[2]){
		setJoueurI(2,-1);
		log("le joueur 3 quitte le jeu");
		log("joueur 1 :"+joueurs[0]);
		log("joueur 2 :"+joueurs[1]);
                log("joueur 3 :"+joueurs[2]);
		log("joueur 4 :"+joueurs[3]);			
	}
        else if(quiter == joueurs[3]){
		setJoueurI(3,-1);
		log("le joueur 4 quitte le jeu");
		log("joueur 1 :"+joueurs[0]);
		log("joueur 2 :"+joueurs[1]);
                log("joueur 3 :"+joueurs[2]);
		log("joueur 4 :"+joueurs[3]);			
	}
    }

    public void onDataEvent(Event de) {
        Vector actorSessions = app.getSAI().getActorSessions();
	int sender = de.getASID();
	log("DataEvent received by server: aSID>"+de.getASID() + " Hashtable size:"+de.getHashtable().size());
		
	if (de.getHashtable() == null){
		log("HASHTABLE NULL");
	}
	for (int i =0; i<actorSessions.size();i++){
		if (!(((ActorSession) actorSessions.elementAt(i)).getActorSessionID() == sender)){
			log("Dest:"+((ActorSession) actorSessions.elementAt(i)).getActorSessionID() +" MessageType:"+actorSessions.elementAt(i).getClass());		
			app.getSAI().sendDataToActorSession(((ActorSession) actorSessions.elementAt(i)).getActorSessionID(),de);
		}
	}
    }

    public void onMyJoinEvent(Event je) {
       //je sais pas a koi cela sert car plus utilisé ou je n'ai pas vu !! A voir
         v = app.getSAI().getActorSessions();
	setJoueur(je.getASID());
	log("joueur1 : "+joueurs[0]);
	log("joueur2 : "+joueurs[1]);
	log("joueur3 : "+joueurs[2]);
        log("joueur4 : "+joueurs[3]);
	log("MyJoinEvent received by server: aSID>"+je.getASID());
    }
    
    /**
    * Est appelée en début de partie, vide pour la première version
    * @return void
    */
    public void start() {
	log("Server start running!");
    }
    /**
    * Est appelée en fin de partie, vide pour la première version
    * @return void
    */
    public void end() {
        log("Server stop!");
    }
    
    //fonction d'affichage et de debug
    public void log(String s){
        Utils.logServer(s);
    }
    
}
