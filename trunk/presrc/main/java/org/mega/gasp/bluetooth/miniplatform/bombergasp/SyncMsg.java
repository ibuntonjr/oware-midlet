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
public class SyncMsg {
    
    private int id;
    private int step;
    private long time;
    
    public final int nbSteps = 5;
    public final int deltaTimeBetweenMsg = 200;
	
	public SyncMsg(){
		id = 0;
		step = 0;
		time = 0;
	}
	
	public void setId(int _id){
	    id = _id;
	}
	
	public void setStep(int _step){
	    step = _step;
	}
	
	public void setTime(long _time){
	    time = _time;
	}
	
	public int getId(){
	    return id;
	}
	
	public int getStep(){
	    return step;
	}
	
	public long getTime(){
	    return time;
	}
	
	public void drawIncoming(){
		Utils.logIncommingMessage("--------------------------");
		Utils.logIncommingMessage("SyncMsg: "+this.getClass());
		Utils.logIncommingMessage("* id: "+getId());
		Utils.logIncommingMessage("* step: "+getStep());
		Utils.logIncommingMessage("* timestamp: "+getTime());
		Utils.logIncommingMessage("--------------------------");
	}
	
	public void drawOutgoing(){
		Utils.logOutgoingMessage("--------------------------");
		Utils.logOutgoingMessage("SyncMsg: "+this.getClass());
		Utils.logOutgoingMessage("* id: "+getId());
		Utils.logOutgoingMessage("* step: "+getStep());
		Utils.logOutgoingMessage("* timestamp: "+getTime());
		Utils.logOutgoingMessage("--------------------------");
	}
}
//#endif
