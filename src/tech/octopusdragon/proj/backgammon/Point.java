package tech.octopusdragon.proj.backgammon;

import java.util.Stack;

public class Point {
	
	private Stack<Man> men;	// The men on the point
	
	
	/**
	 * Creates a point with no men on it.
	 */
	public Point() {
		men = new Stack<Man>();
	}
	
	
	/**
	 * Creates a point with men on it.
	 * @param type The type of men
	 * @param numMen The number of men
	 */
	public Point(Man type, int numMen) {
		this();
		for (int i = 0; i < numMen; i++) {
			men.push(type);
		}
	}
	
	
	/**
	 * Returns the type of the men occupying the point.
	 * @return The type of men or null if unoccupied
	 */
	public Man menType() {
		if (!men.isEmpty())
			return men.peek();
		else
			return null;
	}
	
	
	/**
	 * Returns the number of men occupying the point.
	 * @return The number of men
	 */
	public int menNumber() {
		return men.size();
	}
	
	
	/**
	 * Returns whether the point is unoccupied. 
	 * @return Whether the point is unoccupied
	 */
	public boolean isUnoccupied() {
		return men.isEmpty();
	}
	
	
	/**
	 * Adds a man to the point only if there are no opponent men already on the
	 * point.
	 * @param type The type of man
	 * @return Whether the man was successfully added
	 */
	public boolean add(Man type) {
		if (menType() == type || menType() == null) {
			men.push(type);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Removes a man from the point as long as there is at least one man on the
	 * point.
	 * @return 
	 * @return The removed man or null if nothing was removed
	 */
	public Man remove() {
		if (menNumber() > 0)
			return men.pop();
		return null;
	}
}
