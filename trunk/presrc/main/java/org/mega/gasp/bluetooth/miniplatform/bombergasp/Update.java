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

/**
 * @author Sebire - Negre
 */
public class Update {
	
	private int id;
	private int x;
	private int y;
	private int direction;
	private boolean moving;
	public Update(){
		id=0;
		x=0; 
		y=0;
                        direction=0;
                        moving=false;
	}
	
	/**
	 * 
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @uml.property name="id"
	 */
	public void setId(int id1) {
		id = id1;
	}
	
	/**
	 * 
	 * @uml.property name="x"
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * 
	 * @uml.property name="x"
	 */
	public void setX(int x1) {
		x = x1;
	}
	
	/**
	 * 
	 * @uml.property name="y"
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * 
	 * @uml.property name="y"
	 */
	public void setY(int y1) {
		y = y1;
	}
	  
        public int getDir() {
               return direction;
        }
	 public void setDir(int d) {
                direction=d;
        }
	        public boolean getMov() {
               return moving;
        }
	 public void setMov(boolean m) {
                moving=m;
        }
	
	
	public void drawIncoming(){
		Utils.logIncommingMessage("--------------------------");
		Utils.logIncommingMessage("Update: "+this.getClass());
		Utils.logIncommingMessage("* id: "+getId());
		Utils.logIncommingMessage("* x: "+getX());
		Utils.logIncommingMessage("* y: "+getY());
                Utils.logIncommingMessage("* direction: "+getDir());
                          Utils.logIncommingMessage("* moving: "+getMov());
		Utils.logIncommingMessage("--------------------------");
	}
	
	public void drawOutgoing(){
		Utils.logOutgoingMessage("--------------------------");
		Utils.logOutgoingMessage("Update: "+this.getClass());
		Utils.logOutgoingMessage("* id: "+getId());
		Utils.logOutgoingMessage("* x: "+getX());
		Utils.logOutgoingMessage("* y: "+getY());
                       Utils.logOutgoingMessage("* direction: "+getDir());
                              Utils.logOutgoingMessage("* moving: "+getMov());
		Utils.logOutgoingMessage("--------------------------");
	}


}
//#endif
