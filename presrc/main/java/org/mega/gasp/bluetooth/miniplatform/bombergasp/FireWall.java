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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.eiroca.j2me.app.BaseApp;

/**
 *
 * @author Sebire - Negre
 */
public class FireWall {
    private static Image[] Images;
    private double start;
    int x,y;
    
    public FireWall(int x,int y,double start) {
        this.start=start;
        this.x=x;
        this.y=y;
    }
    
    public static void loadImages() {
        Images=new Image[5];
        try {
            for(int i=0;i<5;i++){
                Images[i]= BaseApp.createImage("/Images/LevelUrban/FireWall/bomber_FireWall"+(i+1)+".png");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void drawFireWall(Graphics g,double time,int offx,int offy){
        if(time-start<=1000)
            g.drawImage(Images[((int)(time-start)/200)%5],Map.XTiletoPix(x)-offx,Map.YTiletoPix(y)-offy, 0);
        else
            Map.delFireWall(this);
    }
}
//#endif
