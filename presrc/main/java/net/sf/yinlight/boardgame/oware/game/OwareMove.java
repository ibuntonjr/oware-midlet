/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was ReversiMove.java
 * in mobilesuite.sourceforge.net project.
 *
 * Copyright (C) 2002-2004 Salamon Andras
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
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
 * This was modified no later than 2009-01-29
 */
// Expand to define logging define
@DLOGDEF@
package net.sf.yinlight.boardgame.oware.game;

import net.eiroca.j2me.game.tpg.GameMove;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
	* Oware game move
	*/
public final class OwareMove extends BoardGameMove {

  public static final int OWARE_MOVE_STORE_SIZE = 3;
  protected int point;

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("OwareMove");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

  public OwareMove(final int row, final int col) {
		super(row, col);
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("constructor row,col=" + row + "," + col);}
		//#endif
  }

  public OwareMove(final int row, final int col, int point) {
		super(row, col);
		this.point = point;
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("constructor row,col=" + row + "," + col);}
		//#endif
  }

  public OwareMove(final OwareMove move) {
		super(move.row, move.col);
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("constructor move has row,col=" + row + "," + col);}
		//#endif
    point = move.point;
  }

  public BoardGameMove getBoardGameMove(final BoardGameMove move) {
		if (!(move instanceof OwareMove)) {
			return null;
		}
		return new OwareMove((OwareMove)move);
	}

  public boolean equals(final Object o) {
    if (!(o instanceof OwareMove)) { return false; }
    final OwareMove r = (OwareMove) o;
    return (row == r.row) && (col == r.col);
  }

	public int hashCode() {
		return row + col + getPoint();
	}

  /**
   * Get the value of point.
   * @return Value of point.
   */
  public int getPoint() {
    return point;
  }

  /**
   * Set the value of point.
   * @param v Value to assign to point.
   */
  public void setPoint(final int v) {
    point = v;
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("setPoint move has row,col,point=" + row + "," + col + "," + point);}
		//#endif
  }

  public String toString() {
    return new StringBuffer(32).append("OwareMove(").append(row).append(", ").append(col).append(")").toString();
  }

}
