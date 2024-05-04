package tech.octopusdragon.proj.backgammon;

import java.util.ArrayList;
import java.util.List;

public class Backgammon {
	
	// --- Constants ---
	public static final int NUM_DICE = 2;	// Number of dice
	public static final Player TOP_PLAYER = Board.BLACK_ON_TOP ? Player.BLACK: Player.WHITE;	// Player whose home is on the bottom
	public static final Player BOTTOM_PLAYER = Board.BLACK_ON_TOP ? Player.WHITE: Player.BLACK;	// Player whose home is on top
	
	
	// --- Instance variables ---
	private Board board;	// The board
	private List<Die> dice;	// Dice usable for the current player
	private Player curPlayer;	// Holds the type of the current player
	private boolean hasRolled;	// Holds whether the player has rolled this turn
	private boolean hasMoved;	// Holds whether the current player has moved
	//private boolean rolledDouble;	// Holds whether the player rolled a double
	
	
	/**
	 * Instantiates a new game of backgammon.
	 */
	public Backgammon() {
		board = new Board();
		dice = new ArrayList<Die>();
		for (int i = 0; i < NUM_DICE; i++)
			dice.add(new Die());
		
		
		// Roll to determine the order of play
		do {
			for (int i = 0; i < NUM_DICE; i++)
				dice.get(i).roll();
			
			if (dice.get(0).getValue() > dice.get(1).getValue())
				curPlayer = Player.BLACK;
			else if (dice.get(0).getValue() < dice.get(1).getValue())
				curPlayer = Player.WHITE;
			
		} while (dice.get(0).getValue() == dice.get(1).getValue());
		
		
		// Instance variable starting values
		hasRolled = true;
		hasMoved = false;
		//rolledDouble = false;
		
		// While the player cannot move, go to the next player
		while (!hasMove()) {
			hasMoved = true;
			nextPlayer();
			hasRolled = true;
		}
	}
	
	
	/**
	 * The direction in which the given player's men move.
	 * @param type The player
	 * @return 1 if the player moves up or -1 if the player moves down
	 */
	public int direction(Player player) {
		if (player == TOP_PLAYER)
			return 1;
		else
			return -1;
	}
	
	
	/**
	 * The index of the start of the given player's home board.
	 * @param type The player
	 * @return The index of the start of the home board
	 */
	public int homeStart(Player player) {
		if (player == TOP_PLAYER)
			return (Board.NUM_POINTS + 1) - (Board.NUM_POINTS / 4);
		else
			return Board.NUM_POINTS / 4;
	}
	
	
	/**
	 * The index of the given player's offboard.
	 * @param type The player
	 * @return The index of the offboard
	 */
	public int offboard(Player player) {
		if (player == TOP_PLAYER)
			return Board.NUM_POINTS + 1;
		else
			return 0;
	}
	
	
	/**
	 * Returns the game board.
	 * @return The game board
	 */
	public Board board() {
		return board;
	}
	
	
	/**
	 * Returns the game dice.
	 * @return The game dice
	 */
	public List<Die> dice() {
		return new ArrayList<Die>(dice);
	}
	
	
	/**
	 * Returns a game die.
	 * @param index The index of the game die
	 * @return The game die
	 */
	public Die die(int index) {
		return dice.get(index);
	}
	
	
	/**
	 * Returns the current player.
	 * @return The current player
	 */
	public Player curPlayer() {
		return curPlayer;
	}
	
	
	/**
	 * Returns the current player's opponent.
	 * @return The current player's opponent
	 */
	public Player curOpponent() {
		if (curPlayer == Player.BLACK)
			return Player.WHITE;
		else
			return Player.BLACK;
	}
	
	
	/**
	 * Returns whether the current player has rolled this turn.
	 * @return Whether the current player has rolled this turn
	 */
	public boolean hasRolled() {
		return hasRolled;
	}
	
	
	/**
	 * Returns whether the current player has moved this turn.
	 * @return Whether the current player has moved this turn
	 */
	public boolean hasMoved() {
		return hasMoved;
	}
	
	
	/**
	 * Advances to the next player as long as the last player's turn is over.
	 */
	public void nextPlayer() {
		
		if (!hasMoved)
			return;
		
		// If the player did not roll a double, change the player
		//if (!rolledDouble) {
			if (curPlayer == Player.BLACK)
				curPlayer = Player.WHITE;
			else
				curPlayer = Player.BLACK;
		//}
		
		// Reset to the default number of dice
		dice.clear();
		dice.add(new Die());
		dice.add(new Die());
		
		// The player has not gone yet; reset appropriate variables
		hasRolled = false;
		hasMoved = false;
		//rolledDouble = false;
		
	}
	
	
	/**
	 * Rolls the dice.
	 */
	public void roll() {
		
		// Do not roll if the player has already rolled
		if (hasRolled)
			return;
		
		for (int i = 0; i < dice.size(); i++) {
			dice.get(i).roll();
		}
		
		// Add two more dice if the player rolled a double
		if (dice.get(0).getValue() == dice.get(1).getValue()) {
			dice.add(new Die(dice.get(0).getValue()));
			dice.add(new Die(dice.get(0).getValue()));
		//	rolledDouble = true;
		}
		
		hasRolled = true;
		
		// If the player cannot move, allow move to the next player
		if (!hasMove()) {
			hasMoved = true;
		//	rolledDouble = false;
		}
	}
	
	
	/**
	 * Returns whether the given move can be made.
	 * @param fromIndex The point index from which to move a piece
	 * @param toIndex The point index to which to move the piece
	 * @return Whether the move can be made
	 */
	public boolean isValidMove(int fromIndex, int toIndex) {
		
		// The player cannot move if they have not rolled yet
		if (!hasRolled)
			return false;
		
		
		// The player cannot move if they have already moved
		if (hasMoved)
			return false;
		
		
		// If the player has hit men, non-hit men cannot move
		if (!curPlayer.hitMen().isEmpty()) {
			if (fromIndex != offboard(curOpponent())) {
				return false;
			}
		}
		
		
		// If the from space is not occupied by the player's men, return false
		if (fromIndex != 0 && fromIndex != Board.NUM_POINTS + 1 && board.point(fromIndex).menType() != curPlayer.menType())
			return false;
		
		
		// If the player can bear off, a to index that is the offboard point
		// is valid
		if (canBearOff() && toIndex == offboard(curPlayer))
			for (Die die: dice)
				if (fromIndex + (direction(curPlayer) * die.getValue()) == toIndex)
					return true;
		
		
		// If the player can bear off, and the index is more than one off from
		// the end point, the piece must be the at the highest occupied point.
		if (canBearOff() &&
				((curPlayer == TOP_PLAYER && toIndex >= offboard(curPlayer)) ||
				 (curPlayer == BOTTOM_PLAYER && toIndex <= offboard(curPlayer)))) {
			// If there is no exact match, but the die is higher, and there are
			// no men in a higher point, return true
			boolean hasMenAtHigherPoint = false;
			for (int i = fromIndex - direction(curPlayer);
					i != homeStart(curPlayer) - direction(curPlayer);
					i -= direction(curPlayer)) {
				if (board.point(i).menType() == curPlayer.menType()) {
					hasMenAtHigherPoint = true;
					break;
				}
			}
			if (hasMenAtHigherPoint) {
				return false;
			}
			else {
				if (curPlayer == TOP_PLAYER) {
					for (Die die: dice)
						if (fromIndex + (direction(curPlayer) * die.getValue()) >= toIndex)
							return true;
				}
				else if (curPlayer == BOTTOM_PLAYER) {
					for (Die die: dice)
						if (fromIndex + (direction(curPlayer) * die.getValue()) <= toIndex)
							return true;
				}
				
			}
		}
		
		
		// If the player cannot bear off and the to index is not valid, return
		// false
		else if (!canBearOff() && (toIndex <= 0 || toIndex >= Board.NUM_POINTS + 1))
			return false;
		
		
		// If the to space has more than one opponent pieces, return false
		else if (board.point(toIndex).menType() == curOpponent().menType() && board.point(toIndex).menNumber() > 1)
			return false;
		
		
		// Finally, if all conditions are fulfilled, go through the dice, and if
		// there is a die matching the desired number of spaces to move, then
		// return true
		for (Die die: dice)
			if (fromIndex + (direction(curPlayer) * die.getValue()) == toIndex)
				return true;
		
		
		// Otherwise, return false
		return false;
	}
	
	
	/**
	 * Returns whether the given move is a capturing move
	 * @param fromIndex The point index from which to move a piece
	 * @param toIndex The point index to which to move the piece
	 * @return Whether the move is a capture
	 */
	public boolean isCapture(int fromIndex, int toIndex) {
		if (isValidMove(fromIndex, toIndex) &&
				board.point(toIndex).menType() == curOpponent().menType() &&
				board.point(toIndex).menNumber() == 1) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Returns whether the move is a bearing off move
	 */
	public boolean isBearingOffMove(int fromIndex, int toIndex) {
		if (canBearOff() &&
				isValidMove(fromIndex, toIndex) &&
				((curPlayer == TOP_PLAYER && toIndex >= offboard(curPlayer)) ||
				 (curPlayer == BOTTOM_PLAYER && toIndex <= offboard(curPlayer))))
			return true;
		return false;
	}
	
	
	/**
	 * Returns whether a piece from the point at the given index can be moved.
	 * @param index The index of the point
	 * @return Whether the piece can move
	 */
	public boolean canMove(int index) {
		for (Die die: dice)
			if (isValidMove(index, index + (direction(curPlayer) * die.getValue())))
				return true;
		
		return false;
	}
	
	
	/**
	 * Returns whether the current player has a valid move.
	 * @return Whether the the player can move.
	 */
	public boolean hasMove() {
		
		for (int i = 1; i <= Board.NUM_POINTS; i++)
			if (canMove(i))
				return true;
		
		if (!curPlayer.hitMen().isEmpty())
			if (canMove(offboard(curOpponent())))
				return true;

		return false;
	}
	
	
	/**
	 * Returns whether the current player can bear off their men.
	 * @return Whether the current player can bear off their men
	 */
	public boolean canBearOff() {
		
		// If the current player still has hit men, they cannot bear off, return
		// false
		if (!curPlayer.hitMen().isEmpty())
			return false;
		
		
		// If the player has at least one man that is not in the home board,
		// return false
		if (curPlayer == TOP_PLAYER) {
			for (int i = 1; i < homeStart(curPlayer); i++) {
				if (board.point(i).menType() == curPlayer.menType())
					return false;
			}
		}
		else {
			for (int i = Board.NUM_POINTS; i > homeStart(curPlayer); i--) {
				if (board.point(i).menType() == curPlayer.menType())
					return false;
			}
		}
		
		
		// Otherwise, return true
		return true;
	}
	
	
	public boolean isOver() {
		boolean whiteFound = false;
		boolean blackFound = false;
		for (int i = 1; i <= Board.NUM_POINTS; i++) {
			if (board.point(i).menType() == Man.BLACK)
				blackFound = true;
			else if (board.point(i).menType() == Man.WHITE)
				whiteFound = true;
			
			if (blackFound && whiteFound)
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Tries to move a man from a point to another point. Does nothing if no
	 * die exists that can be used for this move.
	 * @param fromIndex The index of the point of the piece to move
	 * @param toIndex The index of the point to which to move
	 */
	public void move(int fromIndex, int toIndex) {
		
		// Go through each of the die looking for a match.
		for (Die die: dice) {
			
			if (fromIndex + (direction(curPlayer) * die.getValue()) == toIndex ||
					(isBearingOffMove(fromIndex, toIndex) && isValidMove(fromIndex, toIndex) && (die.getValue() >= Math.abs(fromIndex - toIndex)
					// && isLowestDie(die)
							))) {
				
				// Remove the player's man from the origin
				if (fromIndex == 0 || fromIndex == Board.NUM_POINTS + 1) {
					curPlayer.hitMen().pop();
				}
				else
					board.point(fromIndex).remove();
				
				
				// If the user captured an opponent's man, remove it from the
				// board and add it to the hit men
				if (board.point(toIndex).menType() == curOpponent().menType() &&
						board.point(toIndex).menNumber() == 1) {
					curOpponent().hitMen().push(board.point(toIndex).remove());
				}
				
				
				// Add the player's man to the destination
				board.point(toIndex).add(curPlayer.menType());
				
				
				// Remove the used die from play
				dice.remove(die);
				
				break;
			}
		}
		
		// If the player cannot move, allow move to the next player
		if (!hasMove()) {
			dice.clear();
			hasMoved = true;
		}
	}
}
