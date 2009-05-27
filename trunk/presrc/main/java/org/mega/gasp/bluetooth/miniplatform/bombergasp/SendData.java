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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
*
* @author Sebire - Negre
*/
public class SendData {
    
    /** instance de la midlet */
    protected static App midlet = App.getInstance();
    /** thread sur lequel s'execute l'envoie et la reception des données */
    private Thread mainloop;
    public VectorClockManager myVectorClockManager;
    /** singleton de la classe */
    private static SendData singleton;
    /** ensemble des evenements deletes disponibles */
    private static Vector delete = new Vector();
    /** ensemble des evenements deletesTiles disponibles */
    private static Vector deleteTiles = new Vector();
    /** ensemble des evenements deletesBonus disponibles */
    private static Vector deleteBonus = new Vector();
    /** ensemble des evenements putBomb disponibles */
    private static Vector putBomb = new Vector();
    /** ensemble des evenements ExplodeBomb disponibles */
    private static Vector explodeBomb = new Vector();
    /** ensemble des evenements DoItems disponibles */
    private static Vector doItems = new Vector();
    /** hashtable des evenements à envoyer */
    protected static Hashtable send = new Hashtable();
    
    /** evenement Update associé au heroe */
    private Update updateHero;
    
    private long[] deviations = new long[4];
    
    
//	------------------------Constructeurs-------------------------------
    /**
     * Constructeur
     * initialise et alloue les variables
     */
    public SendData() {
        updateHero = new Update();
        singleton=this;
    }
//	------------------------Acces au singleton--------------------------
    /**
     * accesseur du singleton de la classe
     */
    public static SendData getSingleton() {
        return singleton;
    }
//	------------------------Gestion des vector--------------------------
    /**
     * Création d'un nouvelle evenement Delete.
     * L'evenement est pris dans le vector delete s'il n'est pas vide et créé sinon.
     * @param : L'identifiant de l'element à supprimer
     */
    public synchronized static Delete newDelete(int id) {
        Delete retrieved;
        if (delete.size() > 0) {
            retrieved = (Delete) delete.firstElement();
            delete.removeElement(retrieved);
        } else {
            retrieved = new Delete();
        }
        retrieved.setId(id);
        addSend(retrieved);
        return retrieved;
    }
    /**
     * Création d'un nouvelle evenement DeleteTiles.
     * L'evenement est pris dans le vector deleteTiles s'il n'est pas vide et créé sinon.
     * @param : m colonne du monde où se situe la brique à detruire
     * @param : n ligne du monde où se situe la brique à detruire
     */
    public synchronized static DeleteTiles newDeleteTiles(int m, int n) {
        DeleteTiles retrieved;
        if (deleteTiles.size() > 0) {
            retrieved = (DeleteTiles) deleteTiles.firstElement();
            deleteTiles.removeElement(retrieved);
        } else {
            retrieved = new DeleteTiles();
        }
        retrieved.setM(m);
        retrieved.setN(n);
        return retrieved;
    }
    /**
     * Création d'un nouvelle evenement DeleteTiles.
     * L'evenement est pris dans le vector deleteTiles s'il n'est pas vide et créé sinon.
     * @param : m colonne du monde où se situe la brique à detruire
     * @param : n ligne du monde où se situe la brique à detruire
     */
    public synchronized static DeleteBonus newDeleteBonus(int id,int x ,int y , int item) {
        DeleteBonus retrieved;
        if (deleteTiles.size() > 0) {
            retrieved = (DeleteBonus) deleteBonus.firstElement();
            deleteBonus.removeElement(retrieved);
        } else {
            retrieved = new DeleteBonus();
        }
        retrieved.setId(id);
        retrieved.x=x;
        retrieved.y=y;
        retrieved.type=item;
        addSend(retrieved);
        return retrieved;
    }
    
    public synchronized static PutBomb newPutBomb(int x, int y,int puiss) {
        PutBomb retrieved;
        if (deleteTiles.size() > 0) {
            retrieved = (PutBomb) putBomb.firstElement();
            putBomb.removeElement(retrieved);
        } else {
            retrieved = new PutBomb();
        }
        retrieved.setX(x);
        retrieved.setY(y);
        retrieved.setPuis(puiss);
        addSend(retrieved);
        return retrieved;
    }
    
    public synchronized static ExplodeBomb newExplodeBomb(int x, int y) {
        ExplodeBomb retrieved;
        if (deleteTiles.size() > 0) {
            retrieved = (ExplodeBomb) explodeBomb.firstElement();
            explodeBomb.removeElement(retrieved);
        } else {
            retrieved = new ExplodeBomb();
        }
        retrieved.setX(x);
        retrieved.setY(y);
        addSend(retrieved);
        return retrieved;
    }
    
    public synchronized static DoItems newDoItems(int[][] itemmap) {
        DoItems retrieved=new DoItems();
        String items="";
        //int[][]  to string
        for(int i=0;i<itemmap.length;i++) {
            System.err.print('{');
            for(int j= 0;j<itemmap[i].length;j++){
                System.err.print(itemmap[i][j]+",");
                items+=String.valueOf(itemmap[i][j]);
            }
            System.err.println("},");
        }
        System.err.println("items="+items);
        //
        retrieved.setItems(items);
        addSend(retrieved);
        return retrieved;
    }
    
    /**
     * Liberation de l'objet passé en paramètre. Remet l'objet dans le vecteur associé.
     * @param : x objet à libérer
     */
    public void release(Object x) {
        if (x instanceof Delete) delete.addElement(x);
        else if (x instanceof DeleteTiles) deleteTiles.addElement(x);
        else if (x instanceof DeleteBonus) deleteBonus.addElement(x);
        else if (x instanceof PutBomb) putBomb.addElement(x);
        else if (x instanceof ExplodeBomb) explodeBomb.addElement(x);
         else if (x instanceof DoItems) doItems.addElement(x);
    }
    
    //	------------------------Gestion de la hashtable--------------------------
    /**
     * Ajout d'un objet à la hashtable send
     * @param : x objet à ajouter
     */
    protected synchronized static void addSend(Object x){
        Utils.logClient("SendData.addSend(x), x:"+x.getClass());
        sendMessage(x);
    }
    /**
     * Libere tous les elements de la hashtable send et la vide
     */
    protected void releaseAll(){
        Enumeration myEnum;
        for (myEnum = send.elements(); myEnum.hasMoreElements(); ) {
            Object current = myEnum.nextElement();
            this.release(current);
        }
        SendData.send.clear();
    }
    
//	------------------------Maj de Update------------------------------------
    /**
     * Mise à jour si necessaire de l'evenement Update du heroe.
     * Et si necessaire ajout dans la hashtable send.
     */
    public void setUpdate(){
        try{
            //remplacer dans if les SimpleGameCanvas par Game
            //if(((SimpleGameCanvas)SimpleGameCanvas.getInstance()).dead_until == Long.MAX_VALUE){
            //remplacer SimpleGameCanvas.getHeroe() par ex : game.getAvatarLocal par exemple
            //getAvatarLocal est l'avatar local de la machine
            //   if (SimpleGameCanvas.getHeroe() == null) return;
            
            int id=0,x=0,y=0,d=0;
            boolean updateChange=false,m=false;
            try{
                updateChange=false;
                //faire des get id, x, y sur l avatar principal
                id = Game.getHeroe().id;
                x = Game.getHeroe().x;
                y = Game.getHeroe().y;
                d= Game.getHeroe().direction;
                m=Game.getHeroe().moving;
            }catch(Exception e){
                Utils.logClient("Update: get heroe infos");
                e.printStackTrace();
            }
            try{
                if (updateHero.getId() != id) {
                    updateHero.setId(id);
                    updateChange=true;
                }
                if (updateHero.getX() != x) {
                    updateHero.setX(x);
                    updateChange=true;
                }
                if (updateHero.getY() != y) {
                    updateHero.setY(y);
                    updateChange=true;
                }
                if (updateHero.getDir() != d) {
                    updateHero.setDir(d);
                    updateChange=true;
                }
                if (updateHero.getMov() != m) {
                    updateHero.setMov(m);
                    updateChange=true;
                }
                
            }catch(Exception e){
                Utils.logClient("Update: compare with last infos");
                e.printStackTrace();
                
            }
            if (updateChange) {
                try{
                    SendData.sendMessage(updateHero);
                }catch(Exception e){
                    Utils.logClient("Update: send infos");
                    e.printStackTrace();
                }
            }
            //}
        }catch(Exception e){
            Utils.logClient("Update: critical bug");
            
        }
    }
    
    /*** MOD BY ROM
     *
     * The aim is to send directly the message without storage of the event in stack
     * and the sendData loop. Also we need to create some functions to send each types
     * instantaneously.
     *
     * @author Cybernash
     */
    public static synchronized void sendMessage(Object o){
        send.clear();
        send.put("0",o);
        //Utils.logClient("V2: SENDATA sending DATAEVENT to server ASID : "+midlet.httpTest.aSID+" hashtable :"+send);
        //midlet.httpTest.sendData(midlet.httpTest.aSID,send);
        Utils.logClient("SENDATA sending DATAEVENT to server ASID : "+midlet.gc.aSID+" hashtable :"+send);
        midlet.gc.sendData(midlet.gc.aSID,send);
    }
    
    public static synchronized void getEvents(){
        //midlet.httpTest.getEvents();
        midlet.gc.getEvents();
    }
    
    
}
//#endif
