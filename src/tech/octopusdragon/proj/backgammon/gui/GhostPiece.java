package tech.octopusdragon.proj.backgammon.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * A graphic to show where a player's piece can go,
 * @author Alex Gill
 *
 */
public class GhostPiece extends Group {
	
	// --- Constants ---
	private static final Color COLOR = Color.YELLOW;
	private static final double FADE_DURATION = 1500.0;
	
	
	/**
	 * Constructs a ghost piece.
	 * @param size The width and height of the piece
	 */
	public GhostPiece(double size) {
		super();
		
		
		// Determine properties for the piece graphic
		double radius = size / 3;

		
		// Create the group to put all the parts into
		Group group = new Group();
		
		
		// Add the circle
		Circle circle = new Circle(radius, radius, radius, COLOR);
		group.getChildren().add(circle);
		
		
		// Add a blur effect
		BoxBlur blur = new BoxBlur();
		blur.setIterations(2);
		blur.setWidth((size / 2 - radius) / 2);
		blur.setHeight((size / 2 - radius) / 2);
		this.setEffect(blur);
		
		
		// Create an animation that fades the glow in and out
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		KeyValue kv = new KeyValue(circle.opacityProperty(), 0.0);
		KeyFrame kf = new KeyFrame(Duration.millis(FADE_DURATION), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
		
		
		// Put everything together
		this.getChildren().add(group);
	}

}
