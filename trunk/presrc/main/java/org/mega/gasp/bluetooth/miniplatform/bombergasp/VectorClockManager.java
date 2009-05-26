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

/**
 * @author Sebire - Negre
 */
public class VectorClockManager {
    
    public static final int nbSteps = 21;
    public static final int deltaTimeBetweenMsg = 1000;
    
	public boolean haveToSendMyVectorClock;
    public VectorClock myVectorClock; 
	public long[] timeVector = new long[3];
	public int[] stepVector = new int[3];
	public boolean[] syncFinished = new boolean[3];
	public boolean[] vectorClockReceived = new boolean[3];

	public VectorClockManager(int aSID){
	    haveToSendMyVectorClock = true;
	    
	    myVectorClock = new VectorClock();
	    myVectorClock.setId(aSID);
	    myVectorClock.setDelay(0,0);
	    myVectorClock.setDelay(1,0);
	    myVectorClock.setDelay(2,0);
	    System.err.println("myVectorClock id: "+myVectorClock.getId());
	
		stepVector[0] = 0;
		stepVector[1] = 0;
		stepVector[2] = 0;
		
		syncFinished[0] = false;
		syncFinished[1] = false;
		syncFinished[2] = false;
		
		vectorClockReceived[0] = false;
		vectorClockReceived[1] = false;
		vectorClockReceived[2] = false;
		vectorClockReceived[aSID]=true;
	}
	
	
	public void updatePlayerTime(int aSID, int stepNb, long time){
	    long delay = Math.abs(System.currentTimeMillis()-time);
	    
	    Utils.logSynchronizationFeature("from player "+aSID+" step "+stepNb+" delay "+delay);
	    
	    if (delay > myVectorClock.getDelay(aSID) && stepNb>1){
	        myVectorClock.setDelay(aSID,delay);
	    }
	    
	    stepVector[aSID] = stepNb;
	    if (stepNb == nbSteps) syncFinished[aSID]=true;
	}
	
	public int getLastStepOk(){
	    int lastStep = Math.min(stepVector[0], Math.min(stepVector[1],stepVector[2]));
	    return lastStep;
	}
	
	public boolean isSyncFinished(){
	    return (syncFinished[0] && syncFinished[1] && syncFinished[2]);
	}


	public boolean isAllVectorClockReceived(){
		return (vectorClockReceived[0] && vectorClockReceived[1] && vectorClockReceived[2]);
	}

	
	public void update(VectorClock vc){
	    if(vc.getDelay(0)>myVectorClock.getDelay(0)) myVectorClock.setDelay(0,vc.getDelay(0));
	    if(vc.getDelay(1)>myVectorClock.getDelay(1)) myVectorClock.setDelay(1,vc.getDelay(1));
	    if(vc.getDelay(2)>myVectorClock.getDelay(2)) myVectorClock.setDelay(2,vc.getDelay(2));
	    
	    vectorClockReceived[vc.getId()]=true;
	}

    public long getDelay(int i) {
        return timeVector[i];
    }
    
    public VectorClock getMyVectorClock(){ 
        return myVectorClock;
    }


    /**
     * @return
     */
    public boolean allStepOneReceived() {
        if (stepVector[0] >= 1 && stepVector[1] >= 1 && stepVector[2] >= 1) 
            return true;
        else 
            return false;
    }
}