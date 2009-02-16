/** LGPL >= 2.1
 * Copyright (C) 2002-2004 Salamon Andras
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
/**
 * This was modified no later than 2009-01-29
 */
package net.eiroca.j2me.game.tpg;

/**
 * Abstract class for describing a two-player game. Rules + Evaluation.
 */
public abstract class TwoPlayerGame {

  /**
   * One of the possible outcome of the game: DRAW
   */
  public static final int DRAW = 2;

  /**
   * One of the possible outcome of the game: LOSS
   */
  public static final int LOSS = 3;

  /**
   * One of the possible outcome of the game: WIN
   */
  public static final int WIN = 1;

  /**
   * Calculates a turn of the game. Also calculates the steps of a single move.
   * @param t Table before the turn.
   * @param player Next player.
   * @param move Move to be processed.
   * @param newTable Table after the turn.
   * @return Array of tables, which represents the steps of the single move.
   */
  public abstract GameTable[] animatedTurn(GameTable table, byte player, GameMove move, GameTable newt);

  /**
   * Number of times the evaluation function has been called.
   * @return Number of times the evaluation function has been called.
   */
  public abstract int getEvalNum();

  /**
   * Result of the last processed position.
   * @return Result of the last processed position.
   */
  public abstract int getGameResult(byte player);

  // evaluation

  /**
   * Point of the last processed position.
   * @return Goodness of the position.
   */
  public abstract int getPoint();

  /**
   * Calculates if it's possible to move.
   * @param t Table to be checked.
   * @param player Next player.
   * @return Is there a possible move?
   */
  public abstract boolean hasPossibleMove(GameTable t, byte player);

  /**
   * Is the game ended int the last processed position.
   * @return True if the game is ended.
   */
  public abstract boolean isGameEnded();

  /**
   * Calculates the point (goodness) of the table.
   * @param t Table (position) to be checked.
   * @param player Player.
   * @return Goodness of the position.
   */
  public int point(final GameTable t, final byte player) {
    setTable(t, player, true);
    return getPoint();
  }

  /**
   * Calculates the point (goodness) of the table.
   * @param t Table (position) to be checked.
   * @param player Player.
   * @return Goodness of the position.
   */
  public abstract int getTblPoint(final GameTable t, final byte player);

  /**
   * List of possible moves.
   * @param t Table to be checked.
   * @param player Next player.
   * @return Array of possible moves. null if there is no possible move.
   */
  public abstract GameMove[] possibleMoves(GameTable t, byte player);

  public void process(final GameTable t, final byte player) {
    setTable(t, player, false);
  }

  /**
   * Reset the number of evaluation.
   */
  public abstract void resetEvalNum();

  protected abstract void setTable(GameTable table, byte player, boolean fullProcess);

  protected abstract GameTable getTable();

  protected abstract byte getPlayer();

  /**
   * Calculates a turn of the game.
   * @param t Table before the turn.
   * @param player Next player.
   * @param move Move to be processed.
   * @param newTable Table after the turn.
   * @return True if the move is legal.
   */
  public abstract boolean turn(GameTable t, byte player, GameMove move, GameTable newTable);
}
