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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Sebire - Negre 
 */
public class Avatar {
    private static Image[] Images;
    public int direction=0;     //DOWN=0 UP=1 Right=2 Left=3
    public int x,y;
    public int step=0;
    public double laststep,lastmove;
    public boolean moving;
    public int puissance=1;
    public float VITESSE=0.06f;
    boolean dead=false;
    public boolean local;
    public int id;
    public Avatar(int id,boolean local) {
        this.local=local;
        this.id=id;
        if(id==0)
            x=y=1*Map.BLOCSIZE;
        else if(id==1)
        {
            y=1*Map.BLOCSIZE; x=13*Map.BLOCSIZE;
        }
       else if(id==2)
       {
            y=9*Map.BLOCSIZE; x=1*Map.BLOCSIZE;
       }
        else if(id==3)
        {
            y=9*Map.BLOCSIZE; x=13*Map.BLOCSIZE;
        }
    }
    public static void loadImages() {
        Images=new Image[8*5-3];
        try {
            for(int i=0;i<8;i++){
                Images[8*0+i]= Image.createImage("/Images/Bomberman/Down"+(i+1)+".gif");
                Images[8*1+i]= Image.createImage("/Images/Bomberman/Up"+(i+1)+".gif");
                Images[8*2+i]= Image.createImage("/Images/Bomberman/Right"+(i+1)+".gif");
                Images[8*3+i]= Image.createImage("/Images/Bomberman/Left"+(i+1)+".gif");
                if(i<4)
                    Images[8*4+i]= Image.createImage("/Images/Bomberman/Die"+(i+1)+".gif");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void drawAvatar(Graphics g,double time,int offx,int offy){
        if(!dead){
            if(moving){
                if(time-laststep>=100 ){ //Animation
                    if(++step>7)
                        step=0;
                    laststep=time;
                }
                if(local)
                    Deplacement(time);
            }
            g.drawImage(Images[direction*8+step],OffX(offx),OffY(offy)-8,0);
        } else{
            if(step<4 &&time-laststep>=220){ //Animation
                step++;
                laststep=time;
            }
            if(step<=3)
                g.drawImage(Images[4*8+step],OffX(offx),OffY(offy)-8,0);
        }
    }
    private int OffX(int offx){//retourne X selon Offx
        if(local && offx!=0){
            if(offx==Map.getWidth()-Game.WIDTH)//A DROITE 
                return Game.WIDTH+x-Map.getWidth();
            return Game.WIDTH/2;//AU MILIEU
        } else
            return x-offx;//A GAUCHE SI LOCAL && PARTOUT SINON
    }
    private int OffY(int offy){//retourne Y selon Offy
        if(local && offy!=0){
            if(offy==Map.getHeight()-Game.HEIGHT)
                return Game.HEIGHT+y-Map.getHeight();
            return Game.HEIGHT/2;
        } else
            return y-offy;
    }
    public void Deplacement(double time){
        double deltatime=time-lastmove;
        if(direction==0){                //Deplacement DOWN
            int cxl=x/Map.BLOCSIZE,cxr=cxl+1;
            int cy=(y+(int)(deltatime*VITESSE)+16)/Map.BLOCSIZE;
            if(x%Map.BLOCSIZE<=4){
                if(!Map.isWall(cxl,cy) || getClosestX()==cxl && getClosestY()==cy){
                    x=cxl*Map.BLOCSIZE;
                    y+=(int)(deltatime*VITESSE);
                }
            } else if(x%Map.BLOCSIZE>=12){
                if(!Map.isWall(cxr,cy) || getClosestX()==cxr && getClosestY()==cy){
                    x=cxr*Map.BLOCSIZE;
                    y+=(int)(deltatime*VITESSE);
                }
            }
        } else if(direction==1){            //UP
            int cxl=x/Map.BLOCSIZE,cxr=cxl+1;
            int cy=(y-(int)(deltatime*VITESSE))/Map.BLOCSIZE;
            if(x%Map.BLOCSIZE<=4){
                if(!Map.isWall(cxl,cy) || getClosestX()==cxl && getClosestY()==cy){
                    x=cxl*Map.BLOCSIZE;
                    y-=(int)(deltatime*VITESSE);
                }
            } else if(x%Map.BLOCSIZE>=12){
                if(!Map.isWall(cxr,cy) || getClosestX()==cxr && getClosestY()==cy){
                    x=cxr*Map.BLOCSIZE;
                    y-=(int)(deltatime*VITESSE);
                }
            }
        } else if(direction==2){                //RIGHT
            int cyt=y/Map.BLOCSIZE,cyb=cyt+1;
            int cx=(x+(int)(deltatime*VITESSE)+16)/Map.BLOCSIZE;
            if(y%Map.BLOCSIZE<=4){
                if(!Map.isWall(cx,cyt) || getClosestX()==cx && getClosestY()==cyt){
                    y=cyt*Map.BLOCSIZE;
                    x+=(int)(deltatime*VITESSE);
                }
            } else if(y%Map.BLOCSIZE>=12){
                if(!Map.isWall(cx,cyb) || getClosestX()==cx && getClosestY()==cyb){
                    y=cyb*Map.BLOCSIZE;
                    x+=(int)(deltatime*VITESSE);
                }
            }
        } else  if(direction==3){               //LEFT
            int cyt=y/Map.BLOCSIZE, cyb=cyt+1;
            int cx=(x-(int)(deltatime*VITESSE))/Map.BLOCSIZE;
            if(y%Map.BLOCSIZE<=4){
                if(!Map.isWall(cx,cyt) || getClosestX()==cx && getClosestY()==cyt){
                    y=cyt*Map.BLOCSIZE;
                    x-=(int)(deltatime*VITESSE);
                }
            } else if(y%Map.BLOCSIZE>=12){
                if(!Map.isWall(cx,cyb) || getClosestX()==cx && getClosestY()==cyb){
                    y=cyb*Map.BLOCSIZE;
                    x-=(int)(deltatime*VITESSE);
                }
            }
        }
        lastmove=time;
                Game.sd.setUpdate();//update position
    }
    
    public int getClosestX(){
        if(x%Map.BLOCSIZE>(Map.BLOCSIZE/2))
            return x/Map.BLOCSIZE+1;
        else
            return x/Map.BLOCSIZE;
    }
    public int getClosestY(){
        if(y%Map.BLOCSIZE>(Map.BLOCSIZE/2))
            return y/Map.BLOCSIZE+1;
        else
            return y/Map.BLOCSIZE;
    }
    
    public Bomb addBomb(double time){
        int x=getClosestX();
        int y= getClosestY();
        if(!Map.isWall(x,y)){
            Game.sd.newPutBomb(x,y,puissance);
            return new Bomb(x ,y,puissance,time);
       }
        else
            return null;
    }
    
    public void  CheckDead() {
        if(!dead)
            if(Map.isOnFire((x+4)/Map.BLOCSIZE,(y+4)/Map.BLOCSIZE)||    //2 coins suffisent sur une carte standard
                //Map.isOnFire((localplayer.x+16)/Map.BLOCSIZE,localplayer.y/Map.BLOCSIZE)||
                //Map.isOnFire(localplayer.x/Map.BLOCSIZE,(localplayer.y+16)/Map.BLOCSIZE)||
                Map.isOnFire((x+Map.BLOCSIZE-2)/Map.BLOCSIZE,(y+Map.BLOCSIZE-2)/Map.BLOCSIZE)){
            setDying();
            }
    }
    public void  CheckItem() {
        if(!dead){
            int item;
            if((item=Map.isItem((x+4)/Map.BLOCSIZE,(y+4)/Map.BLOCSIZE))>0){
                useItem(item,(x+4)/Map.BLOCSIZE ,(y+4)/Map.BLOCSIZE);
            } else
                if((item=Map.isItem((x+Map.BLOCSIZE-2)/Map.BLOCSIZE,(y+Map.BLOCSIZE-2)/Map.BLOCSIZE))>0){
                useItem(item,(x+Map.BLOCSIZE-2)/Map.BLOCSIZE,(y+Map.BLOCSIZE-2)/Map.BLOCSIZE);
                }
        }
    }
    
    public void useItem(int item,int x,int y ){
        if(App.getInstance().isServer())
            Game.sd.newDeleteBonus(id,x,y,item);
        switch(item){
            case 4: FlameItem();break;
            case 5: SkullItem();break;
            case 6: SpeedSlowItem();break;
            case 7: SpeedUpItem();break;
        }
         Map.itemmap[y][x]=0;
         Map.isInvalid=true;
    }
    private void FlameItem(){
        this.puissance++;
    }
    private void SkullItem(){
        if(local)
            setDying();
    }
    private void SpeedUpItem(){
        VITESSE+=.01f;
    }
    private void SpeedSlowItem(){
        VITESSE-=.01f;
    }
    private void setDying(){
        if(local){
            App.display.vibrate(2000);
            App.display.flashBacklight(2000);
            local=false;
        }
        Game.sd.newDelete(this.id);
        
        dead=true;
        step=0;
        
    }
       public void setDyingFromServer(){
        if(local){
            App.display.vibrate(2000);
            App.display.flashBacklight(2000);
            local=false;
        }
        dead=true;
        step=0;
    }
}


