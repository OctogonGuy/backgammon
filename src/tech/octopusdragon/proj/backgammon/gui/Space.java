package tech.octopusdragon.proj.backgammon.gui;

import tech.octopusdragon.proj.backgammon.*;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * An invisible pane that represents a clickable point on a backgammon board.
 * @author Alex Gill
 *
 */
public class Space extends StackPane {
	
	private StartingPlace start;
	private int pointIndex;	// Index of the corresponding point on the board
	
	private GridPane gridPane;
	private List<Piece> pieces;
	private GhostPiece ghostPiece;
	
	
	/**
	 * Default onstructor
	 * @param start Whether the pieces start stacking on top or bottom
	 * @param pointIndex The index of the corresponding point on the board
	 * @param point Whether there is to be a triangle graphic
	 */
	public Space(StartingPlace start, int pointIndex, boolean point) {
		this.start = start;
		this.pointIndex = pointIndex;
		
		gridPane = new GridPane();
		pieces = new ArrayList<Piece>();
		
		if (point) {
			PointGraphic.Direction dir;
			if (start == StartingPlace.TOP)
				dir = PointGraphic.Direction.S;
			else
				dir = PointGraphic.Direction.N;
			
			PointGraphic triangle = new PointGraphic(
					dir,
					BackgammonApplication.GRID_SQUARE_SIZE,
					BackgammonApplication.GRID_SQUARE_SIZE * BackgammonApplication.MAX_MEN_PER_POINT,
					pointIndex % 2 == 1 ? BackgammonApplication.POINT1_COLOR: BackgammonApplication.POINT2_COLOR);
			GridPane.setRowSpan(triangle, BackgammonApplication.MAX_MEN_PER_POINT);
			gridPane.add(triangle, 0, 0);
		}
		
		// Add invisible squares to keep each grid square the same size
		for (int i = 0; i < BackgammonApplication.MAX_MEN_PER_POINT; i++) {
			gridPane.add(new Rectangle(BackgammonApplication.GRID_SQUARE_SIZE, BackgammonApplication.GRID_SQUARE_SIZE, Color.TRANSPARENT), 0, i);
		}
		
		this.getChildren().add(gridPane);
	}
	
	
	/**
	 * This constructor constructs the space as well as adds a number of men
	 * @param start Whether the pieces start stacking on top or bottom
	 * @param pointIndex The index of the corresponding point on the board
	 * @param point Whether there is to be a triangle graphic
	 * @param numMen The number of men to add
	 * @param type The type of men to add
	 */
	public Space(StartingPlace start, int pointIndex, boolean point, int numMen, Man type) {
		this(start, pointIndex, point);
		for (int i = 0; i < numMen; i++) {
			add(type);
		}
	}
	
	/**
	 * Returns the index of the corresponding point on the board.
	 * @return The index of the corresponding point on the board
	 */
	public int getPointIndex() {
		return pointIndex;
	}
	
	
	public void add(Man type) {
		Piece newPiece = new Piece(BackgammonApplication.GRID_SQUARE_SIZE, pieces.size() / BackgammonApplication.MAX_MEN_PER_POINT + 1, type);
		gridPane.add(newPiece, 0, nextSpot());
		pieces.add(newPiece);
	}
	
	
	public void remove() {
		gridPane.getChildren().remove(pieces.get(pieces.size() - 1));
		pieces.remove(pieces.size() - 1);
	}
	
	
	/**
	 * Returns the index of the next available spot a piece can be placed in
	 * @return The next available spot
	 */
	public int nextSpot() {
		if (start == StartingPlace.TOP)
			return pieces.size() % BackgammonApplication.MAX_MEN_PER_POINT;
		else
			return (BackgammonApplication.MAX_MEN_PER_POINT - 1) - (pieces.size() % BackgammonApplication.MAX_MEN_PER_POINT);
	}
	
	
	/**
	 * Returns the index of the highest occupied spot
	 * @return The highest occupied spot or -1 if empty
	 */
	public int lastSpot() {
		if (pieces.isEmpty())
			return -1;
		else if (start == StartingPlace.TOP)
			return BackgammonApplication.MAX_MEN_PER_POINT - ((pieces.size() - 1) % BackgammonApplication.MAX_MEN_PER_POINT);
		else
			return (pieces.size() - 1) % BackgammonApplication.MAX_MEN_PER_POINT;
	}
	
	
	public void highlightPiece() {
		if (!pieces.isEmpty())
			pieces.get(pieces.size() - 1).highlight();
	}
	
	
	public void highlightSpace() {
		if (pieces.size() >= BackgammonApplication.MAX_MEN_PER_POINT) {
			pieces.get(pieces.size() - BackgammonApplication.MAX_MEN_PER_POINT).highlight();
		}
		else {
			ghostPiece = new GhostPiece(BackgammonApplication.GRID_SQUARE_SIZE);
			GridPane.setHalignment(ghostPiece, HPos.CENTER);
			GridPane.setValignment(ghostPiece, VPos.CENTER);
			gridPane.add(ghostPiece, 0, nextSpot());
		}
	}
	
	
	public void select() {
		if (!pieces.isEmpty())
			pieces.get(pieces.size() - 1).select();
	}
	
	
	public void removeEffect() {
		if (ghostPiece != null) {
			gridPane.getChildren().remove(ghostPiece);
			ghostPiece = null;
		}
		
		if (!pieces.isEmpty()) {
			pieces.get(pieces.size() - 1).removeEffect();
		}
		
		if (pieces.size() >= BackgammonApplication.MAX_MEN_PER_POINT) {
			pieces.get(pieces.size() - BackgammonApplication.MAX_MEN_PER_POINT).removeEffect();;
		}
	}
	
	
	public double getLastPieceLayoutX() {
		return pieces.get(pieces.size() - 1).getLayoutX();
	}
	
	
	public double getLastPieceLayoutY() {
		return pieces.get(pieces.size() - 1).getLayoutY();
	}
	
	
	public double getNextPieceLayoutX() {
		return 0;
	}
	
	
	public double getNextPieceLayoutY() {
		if (pieces.size() >= BackgammonApplication.MAX_MEN_PER_POINT)
			return pieces.get(pieces.size() - BackgammonApplication.MAX_MEN_PER_POINT).getLayoutY();
		else if (start == StartingPlace.TOP)
			return BackgammonApplication.GRID_SQUARE_SIZE * pieces.size();
		else
			return BackgammonApplication.GRID_SQUARE_SIZE * (BackgammonApplication.MAX_MEN_PER_POINT - 1) - BackgammonApplication.GRID_SQUARE_SIZE * pieces.size();
	}
	
	
	/**
	 * Whether the piece stacks start at the top or bottom of the point
	 * @author Alex Gill
	 *
	 */
	public enum StartingPlace {
		TOP, BOTTOM
	}
}
