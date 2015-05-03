#Release notes for Alpha 6
# 1.0 2008-12-27 Alpha 2 Version #
  * Handle grand slam variations.
  * Don't allow move if opponent has no seeds.
  * Support changes in max holes captured.
  * Fix redo
  * Fix JAR URL

# 1.0 2008-12-31 Alpha 3 Version #
  * Handle pointer press/drag
  * Future font support
  * Fix top piece above board not shown
  * Fix JAD jar size problem

# 1.0 2009-01-04 Alpha 4 Version #
  * Some setup to use JMUnit
  * Fix problems with screen on N80, etc
  * Some more logging
  * Put text files under jar root in addition to locale dir
  * Fix problems with undo/redo changing the user to move next
  * Prepare for prompts using message number
  * Allow removal of prompt for command without removing the command
  * Make sure we don't get infinate loop if no depth.
  * Fix scoring.
  * Fix top piece above table not showing up.
  * Fix grand slam variations
  * Fix display of skill and depth
  * Fix skill settings
  * Allow testing with JMUnit
  * Allow interactive testing.

# 1.0 2009-02-03 Alpha 5 Version #
  * Handle if opponent get seeds if they cannot make a move or the player gets
> them
  * Allow settings of initial seeds
  * Have separate large version of the program.
  * Parameterize rows, columns, tables, players to support more Mancala/Oware
> > games
  * Optional piece/cup images inside squares/rectangles
  * Have CLDC 1.1 thread name start with T
  * More changes for MIDP 1.0
  * Have images for undo/redo
  * New grand slam variation if captured >= 24, is legal
  * Don't write past the end of array to hold images if they don't match
  * Call desroy app before quiting
  * Have separate splash for Oware and Reversi.
  * Have Reversi game.
  * Make Reversi thread safe.
  * Have board game classes to allow making board games easier and add undo/redo
> > and save of game parms.
  * Smaller images for Oware and (future) Mancala
  * Allow customization of columns, initial seeds, (future) rows
  * Save game parms (e.g. 1 or 2 players) separate from game state (e.g. score)
  * Make changes to allow future > 2 player for Mancala
  * Show last move for each player with black strip over piece for Reversi
> > (Oware/Mancala already had an indicator)
  * Added comments
  * More logging
  * Fix saving/loading of initial seeds for Oware

# 1.0 2009-02-04 Alpha 6 Version #
  * Fix exception when setting options
  * Optionally modify game parameters based on limit < 0
  * More logging
  * Don't produce versions that are functionally the same as others (i.e. have
> > JSR's that are not used)
  * Fix JSR75 properties file (Not used)