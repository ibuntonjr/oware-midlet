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

/**
 *
 * @author Sebire - Negre 
 *
 */
public class ExplodeBomb {
      private int x;
    private int y;
    
    /** Creates a new instance of ExplodeBomb */
    public ExplodeBomb() {
    }
    	public int getX() {
		return x;
	}
	
	public void setX(int _x) {
		x = _x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int _y) {
		y = _y;
        }
        
	public void draw(){
		Utils.logGASPBT("ExplodeBomb: "+this.getClass());
		Utils.logGASPBT("> x: "+getX());
		Utils.logGASPBT("> y: "+getY());
		Utils.logGASPBT(" ");
	}
    
    
    
}
