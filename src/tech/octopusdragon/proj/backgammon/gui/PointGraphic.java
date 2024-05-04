package tech.octopusdragon.proj.backgammon.gui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

/**
 * An isosceles triangle
 * @author Alex Gill
 *
 */
public class PointGraphic extends Polygon {
	
	/**
	 * Constructs an isosceles triangle.
	 * @param dir The direction to which the apex points
	 * @param width The width of the square shape the triangle encompasses
	 * @param height The height of the square shape the triangle encompasses
	 */
	public PointGraphic(Direction dir,
			double width, double height) {
		super();
		
		// Add the apex vertex
		switch (dir) {
		case N:
			this.getPoints().addAll(width / 2, 0.0);
			break;
		case S:
			this.getPoints().addAll(width / 2, height);
			break;
		case E:
			this.getPoints().addAll(width, height / 2);
			break;
		case W:
			this.getPoints().addAll(0.0, height / 2);
			break;
		}
		
		// Add the base vertices
		switch (dir) {
		case N:
			this.getPoints().addAll(width, height);
			this.getPoints().addAll(0.0, height);
			break;
		case S:
			this.getPoints().addAll(0.0, 0.0);
			this.getPoints().addAll(width, 0.0);
			break;
		case E:
			this.getPoints().addAll(0.0, height);
			this.getPoints().addAll(0.0, 0.0);
			break;
		case W:
			this.getPoints().addAll(width, 0.0);
			this.getPoints().addAll(width, height);
			break;
		}
	}
	

	/**
	 * Constructs an isosceles triangle.
	 * @param dir The direction to which the apex points
	 * @param width The width of the square shape the triangle encompasses
	 * @param height The height of the square shape the triangle encompasses
	 * @param fill The fill of the interior of the triangle
	 */
	public PointGraphic(Direction dir,
			double width, double height,
			Paint fill) {
		this(dir, width, height);
		this.setFill(fill);
	}
	
	
	/**
	 * The direction to which the apex of an isosceles triangle points
	 * @author Alex Gill
	 *
	 */
	public enum Direction {
		N, S, E, W
	}


}
