/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-09.  The original file was alphabet.c
 * in http://oware.ivorycity.com/.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
/**
 * This was modified no later than 2009-01-29.  Based on mobilesuite and
 * oware by Ivory City
 */
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import java.util.Vector;
import net.eiroca.j2me.game.tpg.GameMinMax;
import net.eiroca.j2me.game.tpg.GameMove;
import net.eiroca.j2me.game.tpg.GameTable;
import net.eiroca.j2me.game.tpg.TwoPlayerGame;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware heuristic abstract class which has method to define calculation.
	*/
abstract public class OwareHeuristic {
	abstract public int getResult(byte player, OwareTable table);
}
