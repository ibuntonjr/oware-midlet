## Short Summary ##

This is a Reversi/Othello freeware / free game.  There is also has a separate Oware
game that is checked in that is in a separate program.
The Reversi game is on the download page.  The reversi game runs on most Java
enabled
phones including Symbian as well as Palm OS PDAs/Phones with 5.x and
IBM WME (see below).
See [wikipedia.org](http://wikipedia.org) for rules for Reversi.
This should work
with most recent devices as they have MIDP 2.0. If it does not work, use the
midp10_... version which is for older phones. This is licensed under GPL.
Source code is at [http://code.google.com/p/oware-midlet/](http://code.google.com/p/oware-midlet/)._


## Program features ##

  * Undo/redo
  * Configuarable options on Game Options screen.

  * Player vs player or against A.I.

  * Up to 14 level A.I.  The level is the number of moves the A.I. is to look ahead.
  * For player vs player, the phone/PDA must be passed between the two players.


  * The game state is saved when you go from the board to the menu.
  * The last moves are marked with a black slash so that you know what the A.I. did and what you did.
  * Works on Java enabled phones/devices.
  * Works on Plam OS devices with IBM Websphere Micro Environment for Palm OS 5.x upwards.
  * Source code available under GPL at http://code.google.com/p/oware-midlet http://code.google.com/p/oware-midlet.


The current square on the board has a red or blue highlight to indicate that if select is used, a piece will be put into that square.
If the square selected is not a valid move, no piece will be put in the square
(and an invalid move error will be shown).  The valid moves have a tiny black spot in them.
The undo/redo
is not saved when the program exits.  Only the current game state is saved
upon exit.

The following is a screen shot:
![http://oware-midlet.sourceforge.net/images/reversi_small_board.png](http://oware-midlet.sourceforge.net/images/reversi_small_board.png)
