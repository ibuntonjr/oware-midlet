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
//#ifdef DJSR82
package org.mega.gasp.bluetooth.miniplatform.bombergasp;
/*
 *
 * Created on 10 avril 2006, 21:08
 */

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Sebire - Negre 
 */
public class Game extends GameCanvas implements Runnable{
    
    protected App midlet = App.getInstance();
    private static Avatar localplayer;
    protected static Vector otherplayer;
    public static Map map;
    private static int KeyPressed=Integer.MAX_VALUE;
    protected static double time,lasttime;
    private static int VIEWOFFXMIN,VIEWOFFXMAX,VIEWOFFYMIN,VIEWOFFYMAX,OFFXMAX,OFFYMAX;
    public static int WIDTH,HEIGHT;
    private static int offx=0,offy=0;
    Thread t;
    public static SendData sd;
    private boolean process = true;

    public Game() {
        super(false);
        
        VIEWOFFXMIN=getWidth()/2; //Moitié de la vue
        VIEWOFFXMAX=Map.getWidth()-VIEWOFFXMIN; //Taille de la map - la moitié de la vue
        VIEWOFFYMIN=getHeight()/2;
        VIEWOFFYMAX=Map.getHeight()-VIEWOFFYMIN;
        
        WIDTH=getWidth();
        HEIGHT=getHeight();
        
        OFFXMAX=Map.getWidth()-WIDTH;//OFFSET DE LA VUE SI A DROITE
        OFFYMAX=Map.getHeight()-HEIGHT;//OFFSET DE LA VUE SI EN BAS
        
        loadImages();
        
        
        //t.start();
    }
    
    private void loadImages(){
        Avatar.loadImages();
        Bomb.loadImages();
        BombFire.loadImages();
        Map.loadImages();
        FireWall.loadImages();
    }
    
    protected void keyPressed(int keyCode) {
        KeyPressed=getGameAction(keyCode);
    }
    protected void keyReleased(int keyCode) {
        KeyPressed=Integer.MAX_VALUE;
        if(localplayer.moving==true){
            localplayer.moving=false;
            sd.setUpdate();
            if(!localplayer.dead)
                localplayer.step=0;//POSITION DE L'AVATAR PAR DEFAULT
        }
    }
    
    //  static int  cps ,cthiss;
    //  static long  lastcsptime;
    boolean syncMode;
    public void run() {
        // for synchronization
//	    long lastSyncMsgTime = 0;
//	    SyncMsg syncMsg = new SyncMsg();
//	    int nbStep = 1;
//	    this.midlet.dataflu.myVectorClockManager = new VectorClockManager(midlet.gc.NumeroJoueur);
//
        
        while(process){
            
//            if (syncMode){
//                // chech if synchronization is finished
//                syncMode = !this.midlet.dataflu.myVectorClockManager.isAllVectorClockReceived();
//
//                // send sync logs to logs server
//                if(!syncMode){
//                    //System.err.println("HTTP START");
//                    //Logging.sendLogs(this.midlet.httpTest.typeHeroe);
//                    //System.err.println("HTTP END");
//                    this.midlet.dataflu.myVectorClockManager.getMyVectorClock().draw();
//                }
//
//                if ((nbStep < VectorClockManager.nbSteps+1) && (System.currentTimeMillis()-lastSyncMsgTime > VectorClockManager.deltaTimeBetweenMsg)){
//
//                    if (nbStep == 1 || this.midlet.dataflu.myVectorClockManager.allStepOneReceived()){
//
//                        // generate and send a synchronization message periodically
//                        syncMsg.setId(this.midlet.gc.NumeroJoueur);
//                        syncMsg.setStep(nbStep);
//                        syncMsg.setTime(System.currentTimeMillis());
//                        this.midlet.dataflu.myVectorClockManager.updatePlayerTime(syncMsg.getId(),syncMsg.getStep(),syncMsg.getTime());
//                        SendData.sendMessage(syncMsg);
//
//                        // set local vars
//                        nbStep++;
//                        lastSyncMsgTime = syncMsg.getTime();
//                    }
//                } else if (this.midlet.dataflu.myVectorClockManager.isSyncFinished()
//                && this.midlet.dataflu.myVectorClockManager.haveToSendMyVectorClock){
//
//                    // generate and send a synchronization message periodically
//                    SendData.sendMessage(this.midlet.dataflu.myVectorClockManager.getMyVectorClock());
//                    this.midlet.dataflu.myVectorClockManager.haveToSendMyVectorClock = false;
//                }
//            } else {
//
            time=System.currentTimeMillis();
            if(time-lasttime>=40){//25CPS
                SendData.getEvents();
                //Test fait par le server
                if(App.getInstance().isServer()){
                    int nbrAlive=0;
                    localplayer.CheckDead();
                    localplayer.CheckItem();
                    for (Enumeration $myEnum1 = otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
                        Avatar av=((Avatar)$myEnum1.nextElement());
                        av.CheckDead();//TODO Envoyer dead
                        av.CheckItem();//TODO Envoyer getitem
                        if(!av.dead)
                            nbrAlive++;
                    }
                    if(nbrAlive==1 && localplayer.dead){
                        midlet.gc.loose();break;
                    } else if(nbrAlive==0 && !localplayer.dead) {
                        midlet.gc.win();break;
                    }
                    
                }
                
                UpdateAction();
                UpdateOffset();
                repaint();
                lasttime=time;
                
     /*           if(System.currentTimeMillis()-lastcsptime>=1000){//FPS
                    lastcsptime=System.currentTimeMillis();
                    cps=cthiss;
                    cthiss=0;
                } else
                    cthiss++;*/
            } else{
                try {
                    Thread.sleep(40-(int)(time-lasttime));
                } catch (InterruptedException ex) {}
            }
        }
        //   }
    }
    
    public void paint(Graphics g){//DRAW LA CARTE (CARTE BOMBE ITEMS EXPLOSIONS) et AVATAR
        //g.fillRect(0,0,getWidth(),getHeight());
        map.drawMap(g,time,offx,offy);
        localplayer.drawAvatar(g,time,offx,offy);
        
        for (Enumeration $myEnum1 = otherplayer.elements(); $myEnum1.hasMoreElements(); ) {
            ((Avatar)$myEnum1.nextElement()).drawAvatar(g,time,offx,offy);
        }
    }
    
    private void UpdateOffset(){
        if(!localplayer.dead){
            if(Map.getWidth()>WIDTH){
                if(localplayer.x<VIEWOFFXMIN)       //Si a l'extreme Gauche
                    offx=0;
                else if(localplayer.x>VIEWOFFXMAX)  //a l'extreme Droite
                    offx=OFFXMAX;
                else                                // dans la nature
                    offx=localplayer.x-VIEWOFFXMIN;
            }
            if(Map.getHeight()>HEIGHT){
                if(localplayer.y<VIEWOFFYMIN)         //Si en Haut
                    offy=0;
                else if(localplayer.y>VIEWOFFYMAX)    //en bas
                    offy=OFFYMAX;
                else                                  // dans la nature
                    offy=localplayer.y-VIEWOFFYMIN;
            }
        } else{
            if(Map.getWidth()>WIDTH)
                switch(KeyPressed){
                    case Canvas.RIGHT:  if(offx<=OFFXMAX-4)offx+=3 ;break;
                    case Canvas.LEFT:   if(offx>=4) offx-=3 ;break;
                }
                if(Map.getHeight()>HEIGHT)
                    switch(KeyPressed){
                        case Canvas.DOWN:  if(offy<=OFFYMAX-4)  offy+=3 ;break;
                        case Canvas.UP:    if(offy>=4)  offy-=3 ;break;
                    }
        }
    } //DECALAGE DE L AFFICHAGE
    
    public void UpdateAction(){
        if(!localplayer.dead){
            if(KeyPressed!=Integer.MAX_VALUE){//SI BOUTON PRESSE
                if(KeyPressed==Canvas.FIRE){        //POSER UNE BOMBE
                    map.addBomb(localplayer.addBomb(time));
                }
                if(  localplayer.moving==false && (
                        KeyPressed==Canvas.DOWN ||    //SI DEPLACEMENT
                        KeyPressed==Canvas.UP ||
                        KeyPressed==Canvas.RIGHT ||
                        KeyPressed==Canvas.LEFT)) {
                    
                    localplayer.lastmove=time;
                    localplayer.laststep=time; //DEBUT DE L'ANIM
                    localplayer.moving=true;
                    switch(KeyPressed){
                        case Canvas.DOWN:   localplayer.direction=0 ;break;
                        case Canvas.UP:     localplayer.direction=1 ;break;
                        case Canvas.RIGHT:  localplayer.direction=2 ;break;
                        case Canvas.LEFT:   localplayer.direction=3 ;break;
                    }
                }
            }
        }
    } //DEPLACEMENT ET POSAGE DES BOMBES
    
    public void restart(){
        localplayer=new Avatar(App.getInstance().gc.NumeroJoueur,true);
        otherplayer=new Vector();
        for(int i=0; i< App.getInstance().gc.gameSessionPlayers.size();i++){
            otherplayer.addElement(new Avatar(((Integer)App.getInstance().gc.otherPlayers.elementAt(i)).intValue(),false));
        }
        map=new Map();
        sd=new SendData();
        t=new Thread(this);
        t.start();
    }
    protected synchronized void quit() {
				process = false;
        t=null;
        sd.releaseAll();
    }
    public static Avatar getHeroe() {
        return localplayer;
    }
    
}
//#endif
