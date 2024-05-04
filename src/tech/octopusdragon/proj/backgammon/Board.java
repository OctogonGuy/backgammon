package tech.octopusdragon.proj.backgammon;

public class Board {
	
	// --- Constants ---
	public static final int NUM_POINTS = 24;			// Number of points
	public static final int NUM_MEN = 15;				// Number of men per player
	public static final boolean BLACK_ON_TOP = true;	// Whether black home is on top
	
	
	// --- Instance variables ---
	private Point[] points;	// The points on the board
	
	
	/**
	 * Constructs a board with the standard backgammon layout.
	 */
	public Board() {
		points = new Point[NUM_POINTS + 2];
		
		for (int i = 0; i < (NUM_POINTS + 2) / 2; i++) {
			switch (i) {
			
			case 1:
				points[i] = new Point(BLACK_ON_TOP ? Man.BLACK: Man.WHITE, 2);
				points[NUM_POINTS + 2 - i - 1] = new Point(BLACK_ON_TOP ? Man.WHITE: Man.BLACK, 2);
				break;
			
			case 6:
				points[i] = new Point(BLACK_ON_TOP ? Man.WHITE: Man.BLACK, 5);
				points[NUM_POINTS + 2 - i - 1] = new Point(BLACK_ON_TOP ? Man.BLACK: Man.WHITE, 5);
				break;
				
			case 8:
				points[i] = new Point(BLACK_ON_TOP ? Man.WHITE: Man.BLACK, 3);
				points[NUM_POINTS + 2 - i - 1] = new Point(BLACK_ON_TOP ? Man.BLACK: Man.WHITE, 3);
				break;
				
			case 12:
				points[i] = new Point(BLACK_ON_TOP ? Man.BLACK: Man.WHITE, 5);
				points[NUM_POINTS + 2 - i - 1] = new Point(BLACK_ON_TOP ? Man.WHITE: Man.BLACK, 5);
				break;
				
			default:
				points[i] = new Point();
				points[NUM_POINTS + 2 - i - 1] = new Point();
			}
		}
	}
	
	
	/**
	 * Returns a point on the board.
	 * @param index The index of the point
	 * @return The point
	 */
	public Point point(int index) {
		return points[index];
	}
}
