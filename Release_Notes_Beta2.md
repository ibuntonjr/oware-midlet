#Release notes for Beta 2
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
  * Parameterize rows, columns, tables, players to support more Mancala/Oware games
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
  * Have board game classes to allow making board games easier and add undo/redo and save of game parms.
  * Smaller images for Oware and (future) Mancala
  * Allow customization of columns, initial seeds, (future) rows
  * Save game parms (e.g. 1 or 2 players) separate from game state (e.g. score)
  * Make changes to allow future > 2 player for Mancala
  * Show last move for each player with black strip over piece for Reversi (Oware/Mancala already had an indicator)
  * Added comments
  * More logging
  * Fix saving/loading of initial seeds for Oware

# 1.0 2009-02-04 Alpha 6 Version #
  * Fix exception when setting options
  * Optionally modify game parameters based on limit < 0
  * More logging
  * Don't produce versions that are functionally the same as others (i.e. have JSR's that are not used)
  * Fix JSR75 properties file (Not used)

# 1.0 2009-02-16 Alpha 7 Version #
  * Add end game so people can decide to end the game.
  * Make Reversi more thread safe
  * Share more code with BoardGameScreen.
  * Fix end of game scoring
  * Allow prompt command from list used as command menu (not finished).

# 1.0 2009-02-27 Beta 1 Version #
  * Fixed introduced bug where Oware no longer works due to problems with precalculate.  Now, precalculation is turned off for Oware.
  * Support feature form for logging report.
  * Simplify support concatenation of Reversi and logging in build file.
  * Simplify build file.
  * Introduced bug where marking of last move on Reversi does not work.
  * Changed initial and current option variables so that they are not negative, only the limit needs to be set negative to indicate that it can be customized.
  * Fixed code to prevent infinite loop.
  * Fix null pointer with updating options.
  * Make some log statements trace instead of finest.
  * Fixed problem where logger report did not do the backward command after the second try.
  * Have Reversi use images of squares, light, and dark pieces.
  * Prevent infinite loop if the computer has no move.
  * Take logging out of jad/manifest for releases.
  * Optionally pre calculate moves based on manifest/jad.
  * Allow user to end the game optionally.  Also, have test option to end the game and set a player to be the computer.
  * Fix display of winner/loser
  * More logging.
  * Workaround synchronized problem in VM of Sony Ericsson.
  * More space for numbers in Oware squares.
  * Fix Reversi problem where if the screen was too small for the images, they are not substituted for circles.
  * Fix placement of scores.
  * Reset undo/redo for new game.
  * Fix MIDP 1.0 not having pause.
  * Have logging form for form append.
  * Reduce height for rectangles and increase size of icons.
  * Refer to players as upper/lower
  * Allow setting of defaults.
  * Skill level message for dept.
  * Make game AI thread safe.
  * Allow both midlets to have optional graphics.
  * Allow change of rows/columns for Reversi.
  * Remove tick as it matches what's in BoardGameScreen
  * Remove drawSelectionBox as it matches what's in BoardGameScreen
  * Allow more players in the future.
  * Fix bug where player vs player did a computer move.
  * Remove unnecessary code.
  * Concatenate logging midlet.
  * Remove logging entries from manifest/jad.
  * Workedaround exceptions with pre calculate on for Reversi.  Pre calculcate needs to be disabled as it has bugs.

# 1.0 2009-04-17 Beta 2 Version #
  * Highlight pieces in verticle bar.
  * Start of minesweeper app.  This is only available for test version.
  * Remove unneeded declaratoins.
  * Remove unneeded logging.
  * Remove unneeded space for Reversi.
  * Fix computer moves not working with Reversi.
  * Fix problem where icons were not appearing for Reversi if they need to be smaller than the icons in the JAR file.
  * More logging.
  * Mark last Reversi pieces moves
  * Allow creation of release file with different names.
  * Allow creation of JAR without Oware.
  * Use JAD for obfuscate.
  * Use drawRegion to split images for menu as this is documented to do so for MIDP 2.0.
  * Put in help text for Reversi.
  * Allow creating a program with just one app.

Issues:

Highlighting of lower player is not the right place for Oware.