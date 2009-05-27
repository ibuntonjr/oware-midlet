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

import java.util.Vector;

/**
 * @author Sebire - Negre
 */
public class VectorClock {
   
    private int id;
	public long[] timeVector = new long[3];

	public VectorClock(){
		timeVector[0] = 0;
		timeVector[1] = 0;
		timeVector[2] = 0;
		
		id = 0;
	}

	public void setId(int _id){
	    id = _id;
	}
	
	public int getId(){
	    return id;
	}
	
	public void setDelay(int i, long delay) {
        timeVector[i] = delay;
    }
	
    public long getDelay(int i) {
        return timeVector[i];
    }
    
    public void drawIncoming(){
		Utils.logIncommingMessage("--------------------------");
		Utils.logIncommingMessage("VectorClock: "+this.getClass());
		Utils.logIncommingMessage("* id: "+getId());
		Utils.logIncommingMessage("* delay j0: "+getDelay(0));
		Utils.logIncommingMessage("* delay j1: "+getDelay(1));
		Utils.logIncommingMessage("* delay j2: "+getDelay(2));
		Utils.logIncommingMessage("--------------------------");
	}
	
	public void drawOutgoing(){
	    Utils.logOutgoingMessage("--------------------------");
		Utils.logOutgoingMessage("VectorClock: "+this.getClass());
		Utils.logOutgoingMessage("* id: "+getId());
		Utils.logOutgoingMessage("* delay j0: "+getDelay(0));
		Utils.logOutgoingMessage("* delay j1: "+getDelay(1));
		Utils.logOutgoingMessage("* delay j2: "+getDelay(2));
		Utils.logOutgoingMessage("--------------------------");
	}
	
	public void draw(){
	    Utils.logSynchronizationFeature("--------------------------");
		Utils.logSynchronizationFeature("VectorClock: "+this.getClass());
		Utils.logSynchronizationFeature("* id: "+getId());
		Utils.logSynchronizationFeature("* delay j0: "+getDelay(0));
		Utils.logSynchronizationFeature("* delay j1: "+getDelay(1));
		Utils.logSynchronizationFeature("* delay j2: "+getDelay(2));
		Utils.logSynchronizationFeature("--------------------------");
	}
}
//#endif
