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
 * Classe de transport des information relatives à la 
 * destruction d'une brique 
 * @version 1.0
 * @author Sebire - Negre 
 */
public class DeleteTiles {
	
	/**
	 * position de la brique en ligne
	 */
	private int m;
	/**
	 * position de la brique en colonne
	 */	
	private int n;
	
	
	/**
	 * Constructeur : initialise m et n 
	 */
	public DeleteTiles(){ 
		m = -1;
		n=-1;
	}
	
	/**
	 * Accesseur à la variable m
	 * @return la valeur de m
	 * 
	 */
	public int getM() {
		return m;
	}
	
	/**
	 * Fonction d'attribution d'un valeur à la variable m
	 * @param id1 la valeur que l'on veut donner à m
	 * 
	 */
	public void setM(int _m) {
		m = _m;
	}
	
	/**
	 * Accesseur à la variable n
	 * @return la valeur de n
	 * 
	 * @uml.property name="n"
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * Fonction d'attribution d'un valeur à la variable n
	 * @param id1 la valeur que l'on veut donner à n
	 * 
	 * @uml.property name="n"
	 */
	public void setN(int _n) {
		n = _n;
	}
	
	/**
	 * Fonction de d'affichage des éléments de la classe
	 */
	public void draw(){
		Utils.logGASPBT("DeleteTiles: "+this.getClass());
		Utils.logGASPBT("> m: "+getM());
		Utils.logGASPBT("> n: "+getN());
		Utils.logGASPBT(" ");
	}
}
//#endif
