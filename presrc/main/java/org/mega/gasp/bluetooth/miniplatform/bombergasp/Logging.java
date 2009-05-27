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

import java.io.DataOutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
*
* @author Sebire - Negre
*/
public class Logging {

	// http request vars
	private static String logsServerUrl = "http://kelua-paris.dyndns.org/ROM1/LogServer/Logger";
	static protected HttpConnection connection = null;
	static private DataOutputStream dos = null;
	static public Object monitor = new Object();
	
	// logs buffer
	private static StringBuffer logs = new StringBuffer();
	
	public static void log(String s){
		logs.append(s+"\r");
	}
	
	public static String getLogs(){
		return logs.toString();
	}
	
	public static void sendLogs(int aSID){
		try{
	    	connection = (HttpConnection) Connector.open(
	        								logsServerUrl,
											Connector.WRITE,
											true);
			connection.setRequestMethod(HttpConnection.POST);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			
			dos = connection.openDataOutputStream();
			dos.writeShort(aSID);
			System.err.println("Send: aSID>"+ aSID);
	        dos.writeUTF(logs.toString());
	        System.err.println("Send: logs>"+ logs.toString());
			dos.close();
	
			if (connection.getResponseCode() == HttpConnection.HTTP_OK){
				System.err.println("Sending OK");
			}
			connection.close();
    	}
    	catch (Exception e) {
    		System.err.println("Erreur HTTP:"+ e.getMessage());
    		e.printStackTrace();
    	}
    	finally {
    		if (connection !=null) {try {connection.close();} catch (Exception e){}}
    		if (dos !=null) {try {dos.close();} catch (Exception e){}}
    	}
	}

}
//#endif
