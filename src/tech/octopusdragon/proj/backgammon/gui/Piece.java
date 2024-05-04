package tech.octopusdragon.proj.backgammon.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import tech.octopusdragon.proj.backgammon.Man;

/**
 * A piece in the game of backgammon
 * @author Alex Gill
 *
 */
public class Piece extends Group {
	
	// --- Constants ---
	private static final Color BLACK_COLOR = Color.GRAY;
	private static final Color WHITE_COLOR = Color.WHITE;
	private static final Color SELECT_COLOR = Color.YELLOW;
	private static final Color HIGHLIGHT_COLOR = Color.YELLOW;
	private static final double FADE_DURATION = 1500.0;

	
	// --- Instance variables ---
	private double size;	// The width and height of the square
	private boolean highlighted;	// Whether the piece is highlighted
	private boolean selected;		//Whether the piece is selected
	
	
	// --- GUI components ---
	Circle outerCircle;
	Circle innerCircle;
	InnerShadow innerShadow;
	Circle concavity;
	
	
	/**
	 * Constructs a backgammon piece.
	 * @param size The width and height of the piece
	 * @param stackSize The number of pieces in the stack (1 - 3)
	 * @param type The type of piece
	 * @param point The index of the point this piece occupies
	 */
	public Piece(double size, int stackSize, Man type) {
		super();
		
		// Set the instance variables
		this.size = size;
		highlighted = false;
		selected = false;
		
		
		// Determine the fill color based on the type
		Color fill;
		if (type == Man.BLACK)
			fill = BLACK_COLOR;
		else
			fill = WHITE_COLOR;
		
		
		// Determine the edge width based on the stack size
		double edgeWidth = 0;
		if (stackSize == 1)
			edgeWidth = size / 18;
		else if (stackSize == 2)
			edgeWidth = size / 6;
		else if (stackSize == 3)
			edgeWidth = size / 3;
		else {
			System.out.println("Error: Stack size: " + stackSize
					+ ". Stack size must be between 1 and 3");
			System.exit(1);
		}
		
		
		// Determine properties for the piece graphic
		double radius = size / 2;
		double hue = fill.getHue();
		double saturation = fill.getSaturation();
		double brightness = fill.getBrightness();
		Color edgeColor = Color.hsb(
				hue,
				saturation,
				brightness * 0.5);
		Color innerShadowColor = Color.hsb(
				hue,
				saturation,
				brightness > 0.1 ? brightness - 0.1: 0.0);

		
		// Create the group to put all the parts into
		Group group = new Group();
		
		
		// Add the outer circle
		outerCircle = new Circle(radius, radius, radius, edgeColor);
		group.getChildren().add(outerCircle);
		
		
		// Add the inner circle
		innerCircle = new Circle(radius, radius, radius - edgeWidth,
				fill);
		group.getChildren().add(innerCircle);

		
		// Add the concavity
		innerShadow = new InnerShadow(radius, innerShadowColor);
		concavity = new Circle(radius, radius, (radius - edgeWidth) * 0.75, fill);
		concavity.setEffect(innerShadow);
		group.getChildren().add(concavity);
		
		
		// Put everything together
		this.getChildren().add(group);
	}
	
	
	public void changeMan(Man type) {
		
		// Determine the fill color based on the type
		Color fill;
		if (type == Man.BLACK)
			fill = BLACK_COLOR;
		else
			fill = WHITE_COLOR;
		
		
		// Determine properties for the piece graphic
		double hue = fill.getHue();
		double saturation = fill.getSaturation();
		double brightness = fill.getBrightness();
		Color edgeColor = Color.hsb(
				hue,
				saturation,
				brightness * 0.5);
		Color innerShadowColor = Color.hsb(
				hue,
				saturation,
				brightness > 0.1 ? brightness - 0.1: 0.0);
		
		
		outerCircle.setFill(edgeColor);
		innerCircle.setFill(fill);
		innerShadow.setColor(innerShadowColor);
		concavity.setFill(fill);
	}


	/**
	 * Returns whether the piece is highlighted or selected.
	 * @return
	 */
	public boolean hasEffect() {
		return selected || highlighted;
	}
	
	
	/**
	 * Returns whether the piece is highlighted.
	 * @return Whether the piece is highlighted.
	 */
	public boolean isHighlighted() {
		return highlighted;
	}
	
	
	/**
	 * Returns whether the piece is selected.
	 * @return Whether the piece is selected.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	
	/**
	 * Visually shows that this piece has can be moved.
	 */
	public void highlight() {
		
		// Set this piece as highlighted
		selected = false;
		highlighted = true;
		
		// Add a glowing effect
		InnerShadow glow = new InnerShadow();
		glow.setBlurType(BlurType.THREE_PASS_BOX);
		glow.setColor(HIGHLIGHT_COLOR);
		glow.setRadius(size);
		this.setEffect(glow);
		
		// Create an animation that fades the glow in and out
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		KeyValue kv = new KeyValue(glow.colorProperty(),
				Color.web(HIGHLIGHT_COLOR.toString(), 0));
		KeyFrame kf = new KeyFrame(Duration.millis(FADE_DURATION), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}
	
	
	/**
	 * Visually shows that this piece has been selected.
	 */
	public void select() {
		
		// Set this piece as selected
		highlighted = false;
		selected = true;
		
		// Add a glowing effect
		InnerShadow glow = new InnerShadow();
		glow.setBlurType(BlurType.THREE_PASS_BOX);
		glow.setColor(SELECT_COLOR);
		glow.setRadius(size);
		this.setEffect(glow);
	}
	
	
	/**
	 * Visually shows that this piece is not selected or highlighted
	 */
	public void removeEffect() {
		
		// Set this piece as neither selected or highlighted
		selected = false;
		highlighted = false;
		
		// Remove the effect
		this.setEffect(null);
	}
}
