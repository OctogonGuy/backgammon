package tech.octopusdragon.proj.backgammon.gui;

import tech.octopusdragon.proj.backgammon.*;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.EventHandler;

public class BackgammonApplication extends Application {
	
	
	// --- CHANGE THIS IF YOU WANT TO TURN ON/OFF HIGHLIGHTING MOVABLE PIECES/SPACES ---
	static final boolean HIGHLIGHT_MOVABLE = true;
	
	static final int MAX_MEN_PER_POINT = 5;
	static final double GRID_SQUARE_SIZE = 60.0;
	static final double ANIMATION_DURATION = 500.0;
	static final Color OUTER_BOARD_COLOR = Color.BROWN;
	static final Color INNER_BOARD_COLOR = Color.BURLYWOOD;
	static final Color POINT1_COLOR = Color.SADDLEBROWN;
	static final Color POINT2_COLOR = Color.SANDYBROWN;
	static final String BACKGROUND_IMAGE = "background.jpg";
	static final String PIECE_SOUND = "piece.wav";
	static final String WIN_SOUND = "win.wav";
	
	Backgammon game;	// The game
	boolean selected;	// Whether a piece is selected
	int selectedIndex;	// Index of selected piece
	
	StackPane root;		// The root node
	GridPane gridPane;	// The board graphic
	List<DieGraphic> dice;	// Die graphics
	Space[] spaces;		// Point spaces
	Space topBar;			// Top bar where hit pieces are
	Space bottomBar;		// Bottom bar where hit pieces are
	Piece topCurPlayerPiece;	// Shows the current player
	Piece bottomCurPlayerPiece;	// Shows the current player
	
	MediaPlayer pieceSound;
	MediaPlayer winSound;
	
	
	@Override
	public void init() {
		game = new Backgammon();
		
		// Initialize the piece sound
		pieceSound = new MediaPlayer(
				new Media(
				getClass().getClassLoader().getResource(PIECE_SOUND).toExternalForm()));
		pieceSound.setOnEndOfMedia(() -> {
			pieceSound.stop();
		});
		
		// Initialize the dice sound
		winSound = new MediaPlayer(
				new Media(
				getClass().getClassLoader().getResource(WIN_SOUND).toExternalForm()));
		winSound.setOnEndOfMedia(() -> {
			winSound.stop();
		});
	}
	

	@Override
	public void start(Stage primaryStage) {
		
		// Create the root node
		root = new StackPane();
		root.setAlignment(Pos.CENTER);
		root.setBackground(
				new Background(
				new BackgroundImage(
				new Image(getClass().getClassLoader().getResourceAsStream(BACKGROUND_IMAGE)),
						  null,
						  null,
						  null,
						  null)));
		
		// Build the board
		buildBoard();
		root.getChildren().add(gridPane);
		
		// Show the pieces on the board
		//updateDieGraphics();
		
		// Highlight movable pieces
		//highlightMovablePieces();
		
		// Set the scene
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Backgammon");
		primaryStage.show();
	}
	
	public void buildBoard() {
		
		// Instantiate the board
		gridPane = new GridPane();
		gridPane.setBackground(
				new Background(
				new BackgroundFill(INNER_BOARD_COLOR,
								   null,
								   null)));
		gridPane.setMaxSize(GridPane.USE_PREF_SIZE, GridPane.USE_PREF_SIZE);
		
		// Create the border
		for (int i = 0; i < Board.NUM_POINTS / 2 + 5; i++) {
			if (i == 0 || i == Board.NUM_POINTS / 4 + 1 || i == Board.NUM_POINTS / 2 + 2 || i == Board.NUM_POINTS / 2 + 4) {
				for (int j = 0; j < 5; j++) {
					double height = GRID_SQUARE_SIZE;
					if (j == 1 || j == 3)
						height = GRID_SQUARE_SIZE * MAX_MEN_PER_POINT;
						
					Rectangle square = new Rectangle(GRID_SQUARE_SIZE, height, OUTER_BOARD_COLOR);
					gridPane.add(square, i, j);
				}
			}
			else {
				Rectangle topSquare = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE, OUTER_BOARD_COLOR);
				gridPane.add(topSquare, i, 0);
				if (i == Board.NUM_POINTS / 2 + 3) {
					Rectangle middleSquare = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE, OUTER_BOARD_COLOR);
					gridPane.add(middleSquare, i, 2);
				}
				Rectangle bottomSquare = new Rectangle(GRID_SQUARE_SIZE, GRID_SQUARE_SIZE, OUTER_BOARD_COLOR);
				gridPane.add(bottomSquare, i, 4);
			}
		}
		
		// Create the point spaces
		spaces = new Space[Board.NUM_POINTS + 2];
		for (int i = 0; i < Board.NUM_POINTS + 2; i++) {
			
			Space.StartingPlace start;
			if (i <= Board.NUM_POINTS / 2)
				start = Space.StartingPlace.BOTTOM;
			else
				start = Space.StartingPlace.TOP;
			
			if (i == 0)
				spaces[i] = new Space(Space.StartingPlace.TOP, i, false);
			else if (i == Board.NUM_POINTS + 1)
				spaces[i] = new Space(Space.StartingPlace.BOTTOM, i, false);
			else if (!game.board().point(i).isUnoccupied())
				spaces[i] = new Space(start, i, true, game.board().point(i).menNumber(), game.board().point(i).menType());
			else
				spaces[i] = new Space(start, i, true);
			
			spaces[i].setOnMouseClicked(new SpaceClickHandler());
			
			// Add the space to the board
			int offset = 1;	// Accounts for middle part
			if (i == 0 || i == Board.NUM_POINTS + 1)
				offset = 3;
			else if (i > Board.NUM_POINTS - Board.NUM_POINTS / 4 || i <= Board.NUM_POINTS / 4)
				offset = 2;
			
			if (i <= Board.NUM_POINTS / 2)
				gridPane.add(spaces[i], (Board.NUM_POINTS / 2 - i) + offset, 3);
			else
				gridPane.add(spaces[i], (i - Board.NUM_POINTS / 2 - 1) + offset, 1);
		}
		
		// Create the die graphic list
		dice = new ArrayList<DieGraphic>();
		for (int i = 0; i < game.dice().size(); i++) {
			DieGraphic dieGraphic = new DieGraphic(GRID_SQUARE_SIZE,
												   game.dice().get(i),
												   true);
			dieGraphic.setOnMouseClicked(event -> {
				dieGraphic.stopAnimation();
				
				// If all dice are shown, highlight movable pieces
				boolean foundRolling = false;
				for (DieGraphic die: dice) {
					if (die.isRolling()) {
						foundRolling = true;
						break;
					}
				}
				if (!foundRolling) {
					highlightMovablePieces();
					
					// Create two graphics that show the current player
					Label topCurPlayerLabel = new Label("Current Player");
					topCurPlayerLabel.setRotate(180.0);
					topCurPlayerPiece = new Piece(GRID_SQUARE_SIZE, 1, game.curPlayer().menType());
					VBox topCurPlayerBox = new VBox(topCurPlayerPiece, topCurPlayerLabel);
					topCurPlayerBox.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.5), new CornerRadii(5.0), new Insets(-5.0))));
					topCurPlayerBox.setAlignment(Pos.CENTER);
					topCurPlayerBox.setSpacing(5.0);
					topCurPlayerBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
					StackPane.setMargin(topCurPlayerBox, new Insets(10.0));
					StackPane.setAlignment(topCurPlayerBox, Pos.TOP_LEFT);
					root.getChildren().add(topCurPlayerBox);
					Label bottomCurrentPlayerLabel = new Label("Current Player");
					bottomCurPlayerPiece = new Piece(GRID_SQUARE_SIZE, 1, game.curPlayer().menType());
					VBox bottomCurPlayerBox = new VBox(bottomCurrentPlayerLabel, bottomCurPlayerPiece);
					bottomCurPlayerBox.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.5), new CornerRadii(5.0), new Insets(-5.0))));
					bottomCurPlayerBox.setAlignment(Pos.CENTER);
					bottomCurPlayerBox.setSpacing(5.0);
					bottomCurPlayerBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
					StackPane.setMargin(bottomCurPlayerBox, new Insets(10.0));
					StackPane.setAlignment(bottomCurPlayerBox, Pos.BOTTOM_RIGHT);
					root.getChildren().add(bottomCurPlayerBox);
				}
			});
			dice.add(dieGraphic);
			if (i == 0)
				gridPane.add(dieGraphic, 1 + Board.NUM_POINTS / 4, 1);
			else
				gridPane.add(dieGraphic, 1 + Board.NUM_POINTS / 4, 3);
			if (!game.hasMove())
				nextTurn();
		}
		
		// Create the hit pieces lists
		topBar = new Space(Space.StartingPlace.TOP, 0, false);
		topBar.setOnMouseClicked(new SpaceClickHandler());
		gridPane.add(topBar, Board.NUM_POINTS / 4 + 1, 1);
		bottomBar = new Space(Space.StartingPlace.BOTTOM, Board.NUM_POINTS + 1, false);
		bottomBar.setOnMouseClicked(new SpaceClickHandler());
		gridPane.add(bottomBar, Board.NUM_POINTS / 4 + 1, 3);
		
		// Update the board to show pieces, dice, and movable pieces
		//updateDieGraphics();
		//highlightMovablePieces();
		for (DieGraphic die: dice) {
			die.toFront();
		}
	}
	
	
	/**
	 * Updates the dice to reflect the current state of the game.
	 */
	public void updateDieGraphics() {
		
		// Update the die graphics
		// If the player has not rolled, place one die in the middle
		if (!game.hasRolled()) {
			for (DieGraphic dieGraphic: dice) {
				gridPane.getChildren().remove(dieGraphic);
			}
			dice.clear();
			DieGraphic dieGraphic = new DieGraphic(GRID_SQUARE_SIZE);
			dice.add(dieGraphic);
			dieGraphic.setOnMouseClicked(new DieClickHandler());
			dieGraphic.setOnMouseEntered(new EnterHandler());
			dieGraphic.setOnMouseExited(new ExitHandler());
			gridPane.add(dieGraphic, Board.NUM_POINTS / 4 + 1, 2);
		}
		else {
			for (DieGraphic dieGraphic: dice) {
				gridPane.getChildren().remove(dieGraphic);
			}
			dice.clear();
			// If game has a die that the GUI does not have, add it to the list
			for (Die die: game.dice()) {
				DieGraphic dieGraphic = new DieGraphic(GRID_SQUARE_SIZE, die, false);
				dice.add(dieGraphic);
			}
			// Add or re-add die graphics to the grid pane
			for (int i = 0; i < dice.size(); i++) {
				if (i % 2 == 0)
					gridPane.add(dice.get(i), Board.NUM_POINTS / 4 + 1 - (int)(Math.ceil(i / 2) + 1), 2);
				else
					gridPane.add(dice.get(i), Board.NUM_POINTS / 4 + 1 + (int)(Math.ceil(i / 2) + 1), 2);
			}
		}
	}
	
	
	public void nextTurn() {
		if (game.isOver()) {
			winSound.play();
		}
		else {
			game.nextPlayer();
			topCurPlayerPiece.changeMan(game.curPlayer().menType());
			bottomCurPlayerPiece.changeMan(game.curPlayer().menType());
		}
		updateDieGraphics();
	}
	
	
	/**
	 * Highlights all movable pieces.
	 */
	public void highlightMovablePieces() {
		
		if (!HIGHLIGHT_MOVABLE)
			return;
		
		for (int i = 1; i <= Board.NUM_POINTS; i++) {
			if (game.canMove(i)) {
				spaces[i].highlightPiece();
				spaces[i].setOnMouseEntered(new EnterHandler());
				spaces[i].setOnMouseExited(new ExitHandler());
			}
		}
		
		if (!game.curPlayer().hitMen().isEmpty() && game.curPlayer() == Backgammon.TOP_PLAYER) {
			topBar.highlightPiece();
			topBar.setOnMouseEntered(new EnterHandler());
			topBar.setOnMouseExited(new ExitHandler());
		}
		else if (!game.curPlayer().hitMen().isEmpty() && game.curPlayer() == Backgammon.BOTTOM_PLAYER) {
			bottomBar.highlightPiece();
			bottomBar.setOnMouseEntered(new EnterHandler());
			bottomBar.setOnMouseExited(new ExitHandler());
		}
	}
	
	
	/**
	 * Highlights all spaces the selected piece can move to.
	 */
	public void highlightMovableSpaces() {
		
		if (!HIGHLIGHT_MOVABLE)
			return;
		
		
		for (int i = 0; i < Board.NUM_POINTS + 2; i++) {
			if (game.isValidMove(selectedIndex, i)) {
				spaces[i].setOnMouseEntered(new EnterHandler());
				spaces[i].setOnMouseExited(new ExitHandler());
				
				
				
				// If the space is unoccupied, place a ghost piece
				if (game.board().point(i).isUnoccupied() ||
						game.board().point(i).menType() == game.curPlayer().menType()) {
					spaces[i].highlightSpace();
				}
				
				
				// If the space is occupied by one opponent piece, highlight the
				// opponent piece
				else if (game.board().point(i).menType() != game.curPlayer().menType() &&
						game.board().point(i).menNumber() == 1) {
					spaces[i].highlightPiece();
				}
			}
		}
	}
	
	
	/**
	 * Removes selected and highleted effects from all pieces.
	 */
	public void removeEffects() {
		for (int i = 0; i < Board.NUM_POINTS + 2; i++) {
			for (int j = 0; j < MAX_MEN_PER_POINT; j++) {
				
				// Remove event handlers
				spaces[i].setOnMouseEntered(null);
				spaces[i].setOnMouseExited(null);
				
				// Remove effect from piece
				spaces[i].removeEffect();
			}
		}

		topBar.setOnMouseEntered(null);
		topBar.setOnMouseExited(null);
		topBar.removeEffect();
		bottomBar.setOnMouseEntered(null);
		bottomBar.setOnMouseExited(null);
		bottomBar.removeEffect();
	}
	
	
	/**
	 * Tries to move the selected piece to the point at the given index. If not
	 * a valid space, deselects the piece.
	 * @param index The index of the point to which to move
	 */
	public void moveToPoint(int index) {
		
		// Regardless, the piece is deselected
		selected = false;
		removeEffects();
		
		// If not a valid space, deselect the selected piece
		if (!game.isValidMove(selectedIndex, index)) {
			highlightMovablePieces();
			return;
		}
		
		// Find out whether the move is a capturing move.
		boolean capture = false;
		if (game.isCapture(selectedIndex, index))
			capture = true;
		
		// Move the the selected piece to the given point
		game.move(selectedIndex, index);
		
		// Update the board to show its new state
		/*
		if (selectedIndex == 0 && game.curPlayer() == Backgammon.TOP_PLAYER)
			topBar.remove();
		else if (selectedIndex == Board.NUM_POINTS + 1 && game.curPlayer() == Backgammon.BOTTOM_PLAYER)
			bottomBar.remove();
		else
			spaces[selectedIndex].remove();
		*/
		if (capture) {
			//spaces[index].remove();
			if (game.curOpponent() == Backgammon.TOP_PLAYER)
				topBar.add(game.curOpponent().menType());
			else
				bottomBar.add(game.curOpponent().menType());
		}
		spaces[index].add(game.curPlayer().menType());
		
		// Play the piece sound
		pieceSound.play();
		
		// Update die graphics
		updateDieGraphics();
		
		// Show movable pieces if the player can still move
		if (game.hasMove()) {
			highlightMovablePieces();
		}
		
		// Otherwise, instantiate new die graphics and advance to next player
		else {
			nextTurn();
		}
	}
	
	
	public void moveAnimation(int index) {
		
		// Regardless, the piece is deselected
		selected = false;
		removeEffects();
		
		// If not a valid space, deselect the selected piece
		if (!game.isValidMove(selectedIndex, index)) {
			highlightMovablePieces();
			return;
		}
		
		// Create a temporary player piece to be used for the animation
		Piece tempPlayerPiece = new Piece(
				GRID_SQUARE_SIZE,
				1,
				game.curPlayer().menType());
		StackPane.setAlignment(tempPlayerPiece, Pos.TOP_LEFT);
		
		
		// Create parallel transition to hold all the transitions
		ParallelTransition ptrans = new ParallelTransition();
		
		
		// Create a translate transition to show the piece moving
		TranslateTransition ttrans = new TranslateTransition(
				Duration.millis(ANIMATION_DURATION),
				tempPlayerPiece);
		
		double fromX;
		double fromY;
		if (selectedIndex == 0) {
			fromX = gridPane.getLayoutX() + topBar.getLayoutX() + topBar.getLastPieceLayoutX();
			fromY = gridPane.getLayoutY() + topBar.getLayoutY() + topBar.getLastPieceLayoutY();
		}
		else if (selectedIndex == Board.NUM_POINTS + 1) {
			fromX = gridPane.getLayoutX() + bottomBar.getLayoutX() + bottomBar.getLastPieceLayoutX();
			fromY = gridPane.getLayoutY() + bottomBar.getLayoutY() + bottomBar.getLastPieceLayoutY();
		}
		else {
			fromX = gridPane.getLayoutX() + spaces[selectedIndex].getLayoutX() + spaces[selectedIndex].getLastPieceLayoutX();
			fromY = gridPane.getLayoutY() + spaces[selectedIndex].getLayoutY() + spaces[selectedIndex].getLastPieceLayoutY();
		}
		double toX;
		double toY;
		if (game.isCapture(selectedIndex, index)) {
			toX = gridPane.getLayoutX() + spaces[index].getLayoutX() + spaces[index].getLastPieceLayoutX();
			toY = gridPane.getLayoutY() + spaces[index].getLayoutY() + spaces[index].getLastPieceLayoutY();
		}
		else {
			toX = gridPane.getLayoutX() + spaces[index].getLayoutX() + spaces[index].getNextPieceLayoutX();
			toY = gridPane.getLayoutY() + spaces[index].getLayoutY() + spaces[index].getNextPieceLayoutY();
		}
		ttrans.setFromX(fromX);
		ttrans.setFromY(fromY);
		ttrans.setToX(toX);
		ttrans.setToY(toY);
		
		ptrans.getChildren().add(ttrans);
		
		// Officially update the game when finished
		ttrans.setOnFinished(e -> {
			moveToPoint(index);
			root.getChildren().remove(tempPlayerPiece);
		});
		
		// If remove captured pieces immediately, add a fade out and scale up
		// transition to show a captured piece disappear if the move is a
		// capture
		if (game.isCapture(selectedIndex, index)) {
			Piece tempOpponentPiece = new Piece(
							GRID_SQUARE_SIZE,
							1,
							game.curOpponent().menType());
			StackPane.setAlignment(tempOpponentPiece, Pos.TOP_LEFT);

			double x = gridPane.getLayoutX() + spaces[index].getLayoutX() + spaces[index].getLastPieceLayoutX();
			double y = gridPane.getLayoutY() + spaces[index].getLayoutY() + spaces[index].getLastPieceLayoutY();
			tempOpponentPiece.setTranslateX(x);
			tempOpponentPiece.setTranslateY(y);
			
			
			// Create the fade transition to show the piece disappearing
			FadeTransition ftrans = new FadeTransition(
					Duration.millis(ANIMATION_DURATION / 2),
					tempOpponentPiece);
			ftrans.setDelay(Duration.millis(ANIMATION_DURATION));
			ftrans.setToValue(0.0);
			ptrans.getChildren().add(ftrans);
			
			
			// Create the scale transition to show the piece going up
			ScaleTransition strans = new ScaleTransition(
					Duration.millis(ANIMATION_DURATION / 2),
					tempOpponentPiece);
			strans.setDelay(Duration.millis(ANIMATION_DURATION));
			strans.setByX(2.0);
			strans.setByY(2.0);
			ptrans.getChildren().add(strans);
			
			
			// Remove the temporary piece when finished
			ftrans.setOnFinished(e -> {
				root.getChildren().remove(tempOpponentPiece);
			});
			
			
			// Add the temporary opponent piece and remove the real piece
			root.getChildren().add(tempOpponentPiece);
			spaces[index].remove();
		}
		
		
		// Add the temporary player piece and remove the real piece
		root.getChildren().add(tempPlayerPiece);
		if (selectedIndex == 0)
			topBar.remove();
		else if (selectedIndex == Board.NUM_POINTS + 1)
			bottomBar.remove();
		else
			spaces[selectedIndex].remove();
		
		
		// Play the animations
		ptrans.play();
	}
	
	
	
	/**
	 * Changes the cursor to a hand
	 */
	public void handCursor() {
		
		if (!HIGHLIGHT_MOVABLE)
			return;
		
		root.getScene().setCursor(Cursor.HAND);
	}
	
	
	/**
	 * Changes the cursor to the default
	 */
	public void defaultCursor() {
		
		if (!HIGHLIGHT_MOVABLE)
			return;
		
		root.getScene().setCursor(Cursor.DEFAULT);
	}
	
	
	/**
	 * Determines what happens when a space is clicked.
	 * @author Alex Gill
	 *
	 */
	public class SpaceClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (selected) {
				moveAnimation(((Space)event.getSource()).getPointIndex());
				
				if (game.canMove(((Space)event.getSource()).getPointIndex()))
					handCursor();
				else
					defaultCursor();
			}
			else {
				if (game.canMove(((Space)event.getSource()).getPointIndex())) {
					
					removeEffects();
					
					((Space)event.getSource()).select();
					selected = true;
					selectedIndex = ((Space)event.getSource()).getPointIndex();
					
					highlightMovableSpaces();
					
					defaultCursor();
				}
			}
		}
	}
	
	
	/**
	 * Rolls the dice when a die is clicked, so long as the player has not
	 * rolled yet.
	 * @author Alex Gill
	 *
	 */
	public class DieClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			game.roll();
			updateDieGraphics();
			DieGraphic.diceSound.play();
			if (!game.hasMove()) {
				Timeline waitTimeline = new Timeline();
				//waitTimeline.setDelay(new Duration(2000.0));
				waitTimeline.getKeyFrames().add(new KeyFrame(
						Duration.millis(2000.0),
						e -> nextTurn()));
				waitTimeline.play();
			}
			else
				highlightMovablePieces();
		}
	}
	
	
	/**
	 * Changes the cursor to a hand when the cursor enters a space.
	 * @author Alex Gill
	 *
	 */
	public class EnterHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle (MouseEvent event) {
			handCursor();
		}
	}
	
	
	/**
	 * Changes the cursor to the default when the cursor exits a space.
	 * @author Alex Gill
	 *
	 */
	public class ExitHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle (MouseEvent event) {
			defaultCursor();
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}

}
