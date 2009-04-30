/** GPL >= 2.0
 * Based upon jtReversi game written by Jataka Ltd.
 *
 * This software was modified 2008-12-07.  The original file was Reversi.java
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
 *
 */
/**
 * This was modified no later than 2009-01-29
 */
// Expand to define MIDP define
//#define DMIDP20
// Expand to define test define
//#define DNOTEST
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define logging define
//#define DNOLOGGING
package net.sf.yinlight.boardgame.oware.midlet;

import net.sf.yinlight.boardgame.oware.game.BoardGameApp;

/**
	* Game application constants.
	*/ 
public class AppConstants {

  public static short msgOffset = 0;
  final public static int MSG_OWARE_NAME = BoardGameApp.MSG_USERDEF + msgOffset++; // 0
  final public static int MSG_INIT_SEEDS = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_MAX_HOUSES = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_MULTI_LAP = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_ONE_LAP = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_1ST_LAP = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_2ND_LAP = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_START_SOWING = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SOW_NEXT = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SOW_FIRST = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_LOOP_SKIP = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SKIP_STARTING = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SOW_STARTING = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SOW_STORE_RULE = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SKIP_STORE = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_SOW_STORE = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_CAPTURE_RULES = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_CAPTURE_EMPTY = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_CAPTURE_2_3 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_CAPTURE_4 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM1 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM2 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM3 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM4 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM5 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_GRAND_SLAM6 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_OPPONENT_EMPTY = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_OPPONENT_EMPTY1 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_OPPONENT_EMPTY2 = BoardGameApp.MSG_USERDEF + msgOffset++;
  final public static int MSG_OWARE_USERDEF = BoardGameApp.MSG_USERDEF + msgOffset;
  final public static int MSG_REVERSI_NAME = BoardGameApp.MSG_USERDEF + msgOffset++; // 0
  final public static int MSG_UPPER = BoardGameApp.MSG_USERDEF + msgOffset++; // 0
  final public static int MSG_LOWER = BoardGameApp.MSG_USERDEF + msgOffset++; // 0
  final public static int MSG_REVERSI_USERDEF = BoardGameApp.MSG_USERDEF + msgOffset++; // 0

}
