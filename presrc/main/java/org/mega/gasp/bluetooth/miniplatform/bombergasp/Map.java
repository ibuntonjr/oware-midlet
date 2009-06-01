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
// Expand to define CLDC define
@DCLDCVERS@
//#ifdef DJSR82
package org.mega.gasp.bluetooth.miniplatform.bombergasp;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.eiroca.j2me.app.BaseApp;

/**
 *
 * @author Sebire - Negre
 */
public class Map {
    
    public static final int BLOCSIZE=16;
    
    public static int [][] map1=
    {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
     {1,0,0,3,3,0,3,3,3,0,3,3,0,0,1},
     {1,0,2,0,2,3,2,3,2,3,2,0,2,0,1},
     {1,3,0,3,0,3,3,3,3,3,0,3,0,3,1},
     {1,0,2,3,2,3,2,3,2,3,2,3,2,3,1},
     {1,3,0,3,0,3,0,3,0,3,0,3,0,3,1},
     {1,0,2,3,2,3,2,3,2,3,2,3,2,3,1},
     {1,3,0,3,0,3,3,3,3,3,0,3,0,3,1},
     {1,0,2,0,2,3,2,3,2,3,2,0,2,0,1},
     {1,0,0,3,3,0,3,3,3,0,3,3,0,0,1},
     {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
    
    public static int [][] map=new int[11][15];
    
    private static Image[] cases;// 0 sol, 1 bord, 2 murI, 3 murD;
    
    private static final byte
            FLAME=4,//puissance des bombes
            SKULL=5,//mort
            SLOW=6,//ralentir
            SPEED=7;//accelere
    
    private static Image carte,carteimmut;
    public static boolean isInvalid;
    public static Vector bombs;
    private static Vector bombfire;
    private static Vector firewall;
    private static int [][] firemap;
//    public static int [][]itemmap=
//    {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//     {0,0,0,5,0,0,0,0,0,0,0,0,0,0,0},
//     {0,0,0,0,0,7,0,0,0,0,0,0,0,4,0},
//     {0,0,0,0,0,6,4,0,4,0,0,0,0,0,0},
//     {0,0,0,0,0,0,0,0,0,0,0,5,0,0,0},
//     {0,0,0,0,0,6,0,0,0,0,0,0,0,0,0},
//     {0,0,0,0,0,0,0,0,0,0,0,0,0,7,0},
//     {0,0,0,0,0,0,0,6,0,0,0,6,0,0,0},
//     {0,0,0,0,0,0,0,6,0,7,0,0,0,0,0},
//     {0,0,0,5,0,0,0,0,6,0,0,0,0,0,0},
//     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
    public static int [][]itemmap;
    private static  boolean l,r,u,d;
    private static boolean contamined=false;
    
    /** Creates a new instance of Map */
    public Map() {
        (bombs =new Vector()).ensureCapacity(10);
        (bombfire=new Vector()).ensureCapacity(50);
        (firewall=new Vector()).ensureCapacity(10);
        
        firemap=new int[map.length][map[0].length];
        
       for(int i =0;i<map.length;i++){
            for(int j=0; j<map[i].length;j++)
                map[i][j]=map1[i][j];
        }
           
        itemmap=new int[map.length][map[0].length];
        if(App.getInstance().isServer()){
            GenerItemMap();//item static pour l'instant avec GASP;
            Game.sd.newDoItems(itemmap);
        }
        
        contamined=false;
        isInvalid=true;
        
        for(int i =0;i<map.length;i++){
            for(int j=0; j<map[i].length;j++)
                map[i][j]=map1[i][j];
        }
        
        carte=Image.createImage(map[0].length*BLOCSIZE,map.length*BLOCSIZE);
        if(carteimmut==null){
            carteimmut=Image.createImage(map[0].length*BLOCSIZE,map.length*BLOCSIZE);//carte décor fixe
            Graphics g=carteimmut.getGraphics();
            for(int i=0;i<Map.map.length;i++){
                for(int j=0;j<Map.map[i].length;j++){
                    g.drawImage(cases[(map[i][j]<=2)?(map[i][j]):(0)],j*BLOCSIZE,i*BLOCSIZE,0);
                }
            }
        }
    }
    
    public static void loadImages() {
        cases=new Image[11];
        try {
            cases[0]= BaseApp.createImage("/Images/LevelUrban/bomber_Floor.png");
            cases[1]= BaseApp.createImage("/Images/LevelUrban/bomber_BlocAround.png");
            cases[2]= BaseApp.createImage("/Images/LevelUrban/bomber_SuperWall.png");
            cases[3]= BaseApp.createImage("/Images/LevelUrban/bomber_Wall.png");
            cases[4]= BaseApp.createImage("/Images/Item/bomber_Flame.png");
            cases[5]= BaseApp.createImage("/Images/Item/bomber_Skull.png");
            cases[6]= BaseApp.createImage("/Images/Item/bomber_Slow.png");
            cases[7]= BaseApp.createImage("/Images/Item/bomber_Speed.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void drawMap(Graphics g,double time,int offx,int offy){
        if(!isInvalid){
            g.drawImage(carte,-offx,-offy,0);
        } else{
            Graphics gg=carte.getGraphics();
            gg.drawImage(carteimmut,0,0,0);
            for(int i=0;i<map.length;i++){
                for(int j=0;j<map[i].length;j++){
                    if(itemmap[i][j]!=0)//si item
                        gg.drawImage(cases[itemmap[i][j]],j*BLOCSIZE,i*BLOCSIZE,0);
                    if(map[i][j]==3 )////si brique cassable
                        gg.drawImage(cases[map[i][j]],j*BLOCSIZE,i*BLOCSIZE,0);
                }
            }
            g.drawImage(carte,-offx,-offy,0);
            isInvalid=false;
        }
        
        if(!bombs.isEmpty()){//DRAW BOMBS
            int i=0;
            while( i <bombs.size()){
                Bomb b=(Bomb)bombs.elementAt(i);
                b.drawBomb(g,time,offx,offy);
                if(contamined){
                    i=0;
                    contamined=false;
                } else
                    i++;
            }
        }
        
        if(!bombfire.isEmpty()){//DRAW FIRE
            for (int i = bombfire.size()-1; i >= 0; i--) {
                BombFire b=(BombFire)bombfire.elementAt(i);
                b.drawBombFire(g,time,offx,offy);
            }
        }
        if(!firewall.isEmpty()){ //MUR EN FEU
            for (int i = 0; i <firewall.size(); i++) {
                FireWall f=(FireWall)firewall.elementAt(i);
                f.drawFireWall(g,time,offx,offy);
            }
        }
    }
    
    public void Invalidate(){
        isInvalid=true;
    }
    
    public static boolean isWall(int x,int y){
        if(map[y][x]>0 && map[y][x]<=3 || map[y][x]==-1)
            return true;
        else{
            for (int i = 0; i < bombs.size(); i++) {
                Bomb b=(Bomb)bombs.elementAt(i);
                if(b.x==x && b.y==y)
                    return true;
            }
        }
        return false;
    }
    
    public static int isItem(int x,int y){
        if(itemmap[y][x]!=0)
            return itemmap[y][x];
        return 0;
    }
    
    public static boolean isOnFire(int x,int y){
        if(firemap[y][x]>0)
            return true;
        else
            return false;
    }
    
    public void addBomb(Bomb b){
        if(b!=null)
            bombs.addElement(b);
    }
    public static void delBomb(Bomb b){
        bombs.removeElement(b);
    }
    public static int getWidth(){
        return map[0].length*BLOCSIZE;
    }
    public static int getHeight(){
        return map.length*BLOCSIZE;
    }
    public static int XTiletoPix(int x){
        return x*BLOCSIZE;
    }
    public static int YTiletoPix(int y){
        return y*BLOCSIZE;
    }
    
    private static void BombTouchByFireBomb(int x,int y){
        for (int i = 0; i < bombs.size(); i++) {
            Bomb b=(Bomb)bombs.elementAt(i);
            if(b.x==x && b.y==y){
                b.mustexplose=true;
                contamined=true;
            }
        }
    }
    
    public static void addFire(Bomb b,double time) {
        bombfire.addElement(new BombFire(time,BombFire.ALL,false,b.x,b.y));
        firemap[b.y][b.x]++;
        l=r=u=d=false;
        for(int i=1;i<b.puissance+1;i++){
            if(!l){
                if(!isWall(b.x-i,b.y)){
                    firemap[b.y][b.x-i]++;
                    bombfire.addElement(new BombFire(time,BombFire.LEFT,i==b.puissance,b.x-i,b.y));
                    if(isItem(b.x-i,b.y)!=0){
                        itemmap[b.y][b.x-i]=0;
                        isInvalid=true;
                    }
                } else{
                    l=true;
                    BombTouchByFireBomb(b.x-i,b.y);
                    if(map[b.y][b.x-i]==3)
                        addFireWall(b.x-i,b.y,time);
                }
            }
            if(!r){
                if(!isWall(b.x+i,b.y)){
                    firemap[b.y][b.x+i]++;
                    bombfire.addElement(new BombFire(time,BombFire.RIGHT,i==b.puissance,b.x+i,b.y));
                    if(isItem(b.x+i,b.y)!=0){
                        itemmap[b.y][b.x+i]=0;
                        isInvalid=true;
                    }
                } else{
                    r=true;
                    BombTouchByFireBomb(b.x+i,b.y);
                    if(map[b.y][b.x+i]==3)
                        addFireWall(b.x+i,b.y,time);
                }
            }
            if(!u){
                if(!isWall(b.x,b.y-i)){
                    firemap[b.y-i][b.x]++;
                    bombfire.addElement(new BombFire(time,BombFire.UP,i==b.puissance,b.x,b.y-i));
                    if(isItem(b.x,b.y-i)!=0){
                        itemmap[b.y-i][b.x]=0;
                        isInvalid=true;
                    }
                } else{
                    u=true;
                    BombTouchByFireBomb(b.x,b.y-i);
                    if(map[b.y-i][b.x]==3)
                        addFireWall(b.x,b.y-i,time);
                }
            }
            if(!d){
                if(!isWall(b.x,b.y+i)){
                    firemap[b.y+i][b.x]++;
                    bombfire.addElement(new BombFire(time,BombFire.DOWN,i==b.puissance,b.x,b.y+i));
                    if(isItem(b.x,b.y+i)!=0){
                        itemmap[b.y+i][b.x]=0;
                        isInvalid=true;
                    }
                } else{
                    d=true;
                    BombTouchByFireBomb(b.x,b.y+i);
                    if(map[b.y+i][b.x]==3)
                        addFireWall(b.x,b.y+i,time);
                }
            }
        }
    }
    public static void delFire(BombFire bf){
        firemap[bf.y][bf.x]--;
        bombfire.removeElement(bf);
    }
    private static void addFireWall(int x,int y,double time){
        map[y][x]=-1;
        firewall.addElement(new FireWall(x,y,time));
        isInvalid=true;
    }
    static void delFireWall(FireWall f) {
        map[f.y][f.x]=0;
        firewall.removeElement(f);
    }
    
    static private void GenerItemMap(){
        System.err.println("Generate Items");
        int nbr=0;
        for(int i=0;i<Map.map.length;i++){
            for(int j=0;j<Map.map[i].length;j++){
                if(map[i][j]==3)
                    nbr++;
            }
        }
        int nbrFIRE=(int)(nbr/100f*25); //25% d'items
        Random r=new Random();
        
        int m,n;
        while(nbrFIRE>0){
            n=0;
						//#ifdef DCLDCV11
            m=r.nextInt(nbr);
						//#else
            m=r.nextInt() % nbr;
						//#endif
            for(int i=0;i<Map.map.length;i++){
                if(n>=m) break;
                for(int j=0;j<Map.map[i].length;j++){
                    if(map[i][j]==3)
                        n++;
                    if(n==m){
											if(itemmap[i][j]==0){
												//#ifdef DCLDCV11
												itemmap[i][j]=r.nextInt(4)+4;
												//#else
												itemmap[i][j]=(r.nextInt() % 4) +4;
												//#endif
												nbrFIRE--;
											}
                        break;
                    }
                }
            }
        }
        System.err.print('{');
        for(int i=0;i<itemmap.length;i++) {
            System.err.print('{');
            for(int j= 0;j<itemmap[i].length;j++){
                System.err.print(itemmap[i][j]+",");
            }
             System.err.println("},");
        }
    }
    
     public static void ItemsFromServer(String items){
     int nbr=0;
        for(int i=0;i<itemmap.length;i++) {
            System.err.print('{');
            for(int j= 0;j<itemmap[i].length;j++){
               itemmap[i][j]=Integer.valueOf(items.substring(nbr,nbr+1)).intValue();
               System.err.print(itemmap[i][j]+",");
               nbr++;
            }
            System.err.println("},");
        }
    }
    
}
//#endif
