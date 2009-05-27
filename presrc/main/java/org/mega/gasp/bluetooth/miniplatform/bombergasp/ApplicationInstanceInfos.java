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

import net.eiroca.j2me.app.BaseApp;

/**
 * This class represents a lobby description of an application instance.
 * 
 * @author Sebire - Negre
 */
public class ApplicationInstanceInfos {
	
	private int appInstanceID;
	private int nbP;
	private Vector pList;
	
	public ApplicationInstanceInfos(int applicationInstanceID, int nbPlayers, Vector playerList){
		appInstanceID= applicationInstanceID;
		nbP= nbPlayers;
		pList = playerList;
	}
	
	/**
	 * Returns the ApplicationInstance ID.
	 * 
	 * @return the ApplicationInstance ID
	 */
	public int getApplicationInstanceID(){return appInstanceID;}
	
	
	/**
	 * Returns the number of players currently joined the application instance.
	 * 
	 * @return the number of players
	 */
	public int getNbPlayers(){return nbP;}
	
	
	/**
	 * Returns the player list of the players joined in the application instance.
	 * 
	 * @return the player list
	 */
	public Vector getPlayerList(){return pList;}
}
//#endif
