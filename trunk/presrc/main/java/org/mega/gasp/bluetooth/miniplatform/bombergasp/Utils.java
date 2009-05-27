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
public class Utils {
    
    public static final boolean DEBUG_ALL = false;
    public static final boolean DEBUG_CLIENT = false;
    public static final boolean DEBUG_HIGHLEVEL_CLIENT = false;
    
    public static final boolean DEBUG_SERVER = false;
    public static final boolean DEBUG_GASPBT = false;
    public static final boolean DEBUG_MSGCOM = false;
    public static final boolean DEBUG_SYNC= false;
    public static final boolean DEBUG_DELAY= false;
     
    
    public static void logClient(String s){
        if (DEBUG_ALL || DEBUG_CLIENT) System.err.println("CLIENT > "+s);
    }
    
    public static void logClientHL(String s){
        if (DEBUG_ALL || DEBUG_HIGHLEVEL_CLIENT) System.err.println("CLIENT > "+s);
    }
    
    
    public static void logIncommingMessage(String s){
        if (DEBUG_ALL || DEBUG_MSGCOM || DEBUG_SYNC) System.err.println("MSG <<| "+s);
    }
    
    public static void logOutgoingMessage(String s){
        if (DEBUG_ALL || DEBUG_MSGCOM || DEBUG_SYNC) System.err.println("MSG |>> "+s);
    }
    
    public static void logSynchronizationFeature(String s){
        if (DEBUG_ALL || DEBUG_MSGCOM || DEBUG_SYNC || DEBUG_DELAY) 
       System.err.println(s);
    }
    
    public static void logClientUni(String s){
        if (DEBUG_ALL || DEBUG_CLIENT) System.err.println("CLIENT UNI. > "+s);
    }
    
    public static void logClientHyb(String s){
        if (DEBUG_ALL || DEBUG_CLIENT) System.err.println("CLIENT HYB. > "+s);
    }

    public static void logServer(String s){
        if (DEBUG_ALL || DEBUG_SERVER) System.err.println("SERVER > "+s);
    }
    
    public static void logGASPBT(String s){
        if (DEBUG_ALL || DEBUG_GASPBT) System.err.println("GASPBT > "+s);
    }
}
//#endif
