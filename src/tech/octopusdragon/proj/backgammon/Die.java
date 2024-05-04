package tech.octopusdragon.proj.backgammon;

import java.util.Random;

/**
 * Represents a standard six-sided die that can be rolled.
 * @author Alex Gill
 *
 */
public class Die {
	
	public static final int NUM_SIDES = 6;	// Number of sides
	
	private int value;	// Value of the face-up side
	
	/**
	 * Instantiates the die.
	 */
	public Die() {
		roll();
	}
	
	/**
	 * Instantiates a die with an initial face-up value
	 * @param value The initial value of the face-up side
	 */
	public Die(int value) {
		this.value = value;
	}
	
	/**
	 * Returns the current value of the face-up side.
	 * @return The value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Rolls the die to produce a random number between 1 and 6.
	 * @return The roll
	 */
	public int roll() {
		Random rand = new Random();
		value = rand.nextInt(NUM_SIDES) + 1;
		return value;
	}
	
}
