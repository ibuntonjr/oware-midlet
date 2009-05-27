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
public class Delete {
	
	private int id;
	
	public Delete(){ 
		id = 0;
	}
	

	
	public int getId() {
		return id;
	}
	

	public void setId(int id1) {
		id = id1;
	}
	

	
	public void draw(){
		Utils.logGASPBT("Delete: "+this.getClass());
		Utils.logGASPBT("> id: "+getId());
		Utils.logGASPBT(" ");
	}
	
}
//#endif
