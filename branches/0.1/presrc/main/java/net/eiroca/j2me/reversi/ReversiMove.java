/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
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
package net.eiroca.j2me.reversi;

import net.eiroca.j2me.game.tpg.GameMove;
import net.sf.yinlight.boardgame.oware.game.BoardGameMove;

public final class ReversiMove extends BoardGameMove {

  protected int point;

  public ReversiMove(final int row, final int col) {
		super(row, col);
  }

  public ReversiMove(final ReversiMove move) {
		super(move);
  }

  public BoardGameMove getBoardGameMove(final BoardGameMove move) {
    if (!(move instanceof ReversiMove)) { return null; }
		return new ReversiMove((ReversiMove)move);
	}

  public boolean equals(final Object o) {
    if (!(o instanceof ReversiMove)) { return false; }
    final ReversiMove r = (ReversiMove) o;
    return (row == r.row) && (col == r.col);
  }

	public int hashCode() {
		return (super.hashCode() + point);
	}

  /**
   * Get the value of point.
   * @return Value of point.
   */
  public int getPoint() {
    return point;
  }

  public void setCoordinates(final int row, final int col) {
		super.setCoordinates(row, col);
  }

  /**
   * Set the value of point.
   * @param v Value to assign to point.
   */
  public void setPoint(final int v) {
    point = v;
  }

  public String toString() {
    return new StringBuffer(32).append("ReversiMove(").append(row).append(", ").append(col).append(")").toString();
  }

}
