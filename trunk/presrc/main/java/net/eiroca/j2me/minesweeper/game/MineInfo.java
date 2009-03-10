/** GPL >= 2.0
 * Based upon J2ME Minesweeper.
 * Copyright (C) M. Jumari
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
// Expand to define logging define
@DLOGDEF@
package net.eiroca.j2me.minesweeper.game;

import net.sf.yinlight.boardgame.oware.game.BoardGameTableSquare;
import net.sf.yinlight.boardgame.oware.game.BoardGameTable;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif
//

public class MineInfo extends BoardGameTableSquare {

  final public static int MINE_INFO_SIZE = BoardGameTableSquare.BOARD_GAME_TABLE_SQ_SIZE + 2 * 3;
  public int status_real;
  public int status_guess;
  public byte item;

  //#ifdef DLOGGING
  private Logger logger = Logger.getLogger("MineInfo");
  private boolean fineLoggable = logger.isLoggable(Level.FINE);
  private boolean finestLoggable = logger.isLoggable(Level.FINEST);
  private boolean traceLoggable = logger.isLoggable(Level.TRACE);
  //#endif
	//
  /**
   * Constructor class MineKotak
   * @param x coordinate row
   * @param y coordinate column
   * @param status informasi nilai kotak
   * @param status_guess informasi suatu kotak di tandai atau tidak
   */
  public MineInfo(final int x, final int y, final int status, final int status_guess, byte item) {
		super(x, y);
		this.status_real = status;
		this.status_guess = status_guess;
		this.item = item;
  }

  /**
   * Constructor class MineKotak
   * @param minfo
   */
  public MineInfo(final MineInfo minfo) {
		super(minfo);
		this.status_real = minfo.status_real;
		this.status_guess = minfo.status_guess;
		this.item = minfo.item;
  }

  /**
   * Constructor class
   * @param x coordinate row
   * @param y coordinate column
   * @param status informasi nilai kotak
   * @param status_guess informasi suatu kotak di tandai atau tidak
   */
  public MineInfo(final byte[] byteArray, final int offset) {
		super(byteArray, offset);
		int coffset = offset + BoardGameTableSquare.BOARD_GAME_TABLE_SQ_SIZE;
			this.status_real = ((byteArray[coffset++] == 1) ? -1 : 1) *
			(byteArray[coffset++] << 8 + byteArray[coffset++]);
			this.status_guess = ((byteArray[coffset++] == 1) ? -1 : 1) *
			(byteArray[coffset++] << 8 + byteArray[coffset++]);
  }

  public void toByteArray(final byte[] byteArray, final int offset) {
	try {
		super.toByteArray(byteArray, offset);
		int coffset = offset + BoardGameTableSquare.BOARD_GAME_TABLE_SQ_SIZE;
		byteArray[coffset++] = (byte)((this.status_real < 0) ? 1 : 0);
		byteArray[coffset++] = (byte)((this.status_real & 0xFF00) >> 8);
		byteArray[coffset++] = (byte)((this.status_real & 0x0000) >> 8);
		byteArray[coffset++] = (byte)((this.status_guess < 0) ? 1 : 0);
		byteArray[coffset++] = (byte)((this.status_guess & 0xFF00) >> 8);
		byteArray[coffset++] = (byte)((this.status_guess & 0x0000) >> 8);
	} catch (Throwable e) {
			e.printStackTrace();
			//#ifdef DLOGGING
			logger.severe("loadBoardGameCustomization error", e);
			//#endif
		}
	}

}
