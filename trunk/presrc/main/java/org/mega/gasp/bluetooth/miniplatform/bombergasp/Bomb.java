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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Sebire - Negre 
 */
public class Bomb {
    private static Image[] images;
    int x,y;
    double starttime,ecoule=0;
    int duree=2000,puissance;
    boolean mustexplose=false;
    
    public Bomb(int x,int y,int puissance,double t) {
        this.x=x;
        this.y=y;
        this.puissance=puissance;
            this.starttime=t;
    }
    
    public void drawBomb(Graphics g,double time,int offx,int offy){
        ecoule=time-starttime;
        if((!App.getInstance().isServer() ||(ecoule<duree)) && !mustexplose ){
            int i=(int)(ecoule/400)%4; //Pour faire grossir la bombe
            g.drawImage(images[(i>2)?(i-2):(i)],Map.XTiletoPix(x)-offx,Map.YTiletoPix(y)-offy,0);
        } else{
            if(App.getInstance().isServer())
              Game.sd.newExplodeBomb(x,y);
          exploser(time);
        }
    }
    public void exploser(double time){
      App.display.vibrate(100);
            Map.addFire(this,time);
            Map.delBomb(this);
    }
    public static void loadImages() {
        images=new Image[3];
        try {
            for(int i=0;i<3;i++){
                images[i]= Image.createImage("/Images/Bombe/Bomb"+(i+1)+".gif");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
