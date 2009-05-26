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

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 *
 * @author Sebire - Negre
 */
public class BombFire {
    protected static Image [] fireimages;
    public static final int UP=0,DOWN=1,LEFT=2,RIGHT=3,ALL=4;
    protected int x,y,direction;
    double start,lasttime;
    int anim=0,tail;
    
    public BombFire(double start,int dir,boolean tail,int x,int y) {
        this.lasttime=this.start=start;
        this.direction=dir;
        this.x=x;
        this.y=y;
        this.tail=tail?4:0;
    }
    
    public static void loadImages() {
        fireimages=new Image[37];
        try {
            for(int i=0;i<4;i++){
                fireimages[i]=Image.createImage("/Images/Fire/Up"+(i+1)+".gif");
                fireimages[4+i]=Image.createImage("/Images/Fire/UpExt"+(i+1)+".gif");
                
                fireimages[8+i]=Image.createImage("/Images/Fire/Down"+(i+1)+".gif");
                fireimages[12+i]=Image.createImage("/Images/Fire/DownExt"+(i+1)+".gif");
                
                fireimages[16+i]=Image.createImage("/Images/Fire/Left"+(i+1)+".gif");
                fireimages[20+i]=Image.createImage("/Images/Fire/LeftExt"+(i+1)+".gif");
                
                fireimages[24+i]=Image.createImage("/Images/Fire/Right"+(i+1)+".gif");
                fireimages[28+i]=Image.createImage("/Images/Fire/RightExt"+(i+1)+".gif");
                
                fireimages[32+i]=Image.createImage("/Images/Fire/Center"+(i+1)+".gif");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void drawBombFire(Graphics g,double time,int offx,int offy) {
        if(time-lasttime>=140){ //Animation
            anim++;
            lasttime=time;
        }
        g.drawImage(fireimages[direction*8  +((anim<=3)?(anim):(3+(4-anim)))+tail],Map.XTiletoPix(x)-offx,Map.YTiletoPix(y)-offy, 0);
        if(lasttime-start>=1000)
            Map.delFire(this);
    }
}
