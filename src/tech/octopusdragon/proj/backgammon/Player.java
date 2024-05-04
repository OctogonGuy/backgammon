package tech.octopusdragon.proj.backgammon;

import java.util.Stack;

/**
 * Represents a player in backgammon. Can be black or white and can have hit men
 * and beared off men.
 * @author Alex Gill
 *
 */
public enum Player {
	
	BLACK(Man.BLACK), WHITE(Man.WHITE);
	
	
	private Man menType;				// Type of men player has
	private Stack<Man> hitMen;			// Men that have been hit
	
	
	/**
	 * Constructor
	 */
	private Player(Man type) {
		menType = type;
		hitMen = new Stack<Man>();
	}
	
	
	/**
	 * Returns the type of men the player has
	 * @return The type of men the player has
	 */
	public Man menType() {
		return menType;
	}
	
	
	/**
	 * Returns the player's men that have been hit.
	 * @return The player's men that have been hit
	 */
	public Stack<Man> hitMen() {
		return hitMen;
	}
}
