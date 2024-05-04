package tech.octopusdragon.proj.backgammon.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import tech.octopusdragon.proj.backgammon.Die;


/**
 * A die graphic that, once instantiated, rolls until the roll method is called,
 * at which point, a die value will be shown face-up.
 * @author Alex Gill
 *
 */
public class DieGraphic extends ImageView {
	
	// --- Static constants ---
	private static final double DURATION = 1000.0;	// Duration of animation
	private static final int IMAGE_CHANGES = 12;	// Number of image changes
	private static final String ONE_IMAGE = "diceOne.png";
	private static final String TWO_IMAGE = "diceTwo.png";
	private static final String THREE_IMAGE = "diceThree.png";
	private static final String FOUR_IMAGE = "diceFour.png";
	private static final String FIVE_IMAGE = "diceFive.png";
	private static final String SIX_IMAGE = "diceSix.png";
	private static final String ANIMATION_DIRECTORY = "Animation";
	private static final String DICE_SOUND = "dice.wav";
	private static final Random rand = new Random();
	
	
	// --- Static variables ---
	private static Map<Integer, Image> dieImages;
	private static Image[] animationImages;
	public static MediaPlayer diceSound;
	
	
	// --- Instance variables ---
	Die die;			// The Die object this graphic represents
	Timeline animation;	// The animation
	boolean rolling;	// Whether the rolling animation is going
	
	
	/**
	 * Static constructor instantiates the images and audio used for all die
	 * graphics.
	 */
	static {
		
		// Create a map of die values and die images
		dieImages = new HashMap<Integer, Image>();
		dieImages.put(1, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(ONE_IMAGE)));
		dieImages.put(2, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(TWO_IMAGE)));
		dieImages.put(3, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(THREE_IMAGE)));
		dieImages.put(4, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(FOUR_IMAGE)));
		dieImages.put(5, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(FIVE_IMAGE)));
		dieImages.put(6, new Image(DieGraphic.class.getClassLoader().getResourceAsStream(SIX_IMAGE)));
		
		// Create an array of images to be used in the rolling animation
		animationImages = new Image[32];
		for (int i = 0; i < 32; i++) {
			animationImages[i] = new Image(DieGraphic.class.getClassLoader().getResourceAsStream(String.format("%s/%02d.png", ANIMATION_DIRECTORY, (i + 1))));
		}
		
		// Initialize the dice sound
		diceSound = new MediaPlayer(
				new Media(
				DieGraphic.class.getClassLoader().getResource(DICE_SOUND).toExternalForm()));
		diceSound.setOnEndOfMedia(() -> {
			diceSound.stop();
		});
	}
	
	
	/**
	 * The width and height of the die graphic
	 * @param size The width and height of the die graphic
	 * @param 
	 */
	public DieGraphic(double size) {
		super();
		this.setPreserveRatio(true);
		this.setFitWidth(size);
		
		// Instantiate animation
		animation = new Timeline();
		playNextKeyFrame();
		rolling = true;
	}
	
	
	/**
	 * The width and height of the die graphic
	 * @param size The width and height of the die graphic
	 * @param die The Die object that this die graphic represents
	 * @param playAnimation Whether to play an animation before rolling
	 */
	public DieGraphic(double size, Die die, boolean playAnimation) {
		super();
		this.setPreserveRatio(true);
		this.setFitWidth(size);
		
		// Instantiate the die
		this.die = die;
		
		// Instantiate animation
		animation = new Timeline();
		
		// Display the face-up side.
		if (playAnimation) {
			playNextKeyFrame();
			rolling = true;
		}
		else {
			this.setImage(dieImages.get(die.getValue()));
			rolling = false;
		}
	}
	
	
	/**
	 * Returns the Die object that this die graphic represents
	 * @return The Die object
	 */
	public Die getDie() {
		return die;
	}
	
	
	/**
	 * Returns the timeline animation of the die graphic
	 * @return
	 */
	public Timeline getAnimation() {
		return animation;
	}
	
	
	/**
	 * Returns the Die object that this die graphic represents
	 * @param die The Die object
	 */
	public void setDie(Die die) {
		this.die = die;
	}
	
	
	/**
	 * Plays the next key frame in the infinite rolling animation
	 */
	public void playNextKeyFrame() {
		
		// Generate a key frame that displays a random die image
		int animationImageIndex = rand.nextInt(animationImages.length);
		KeyValue changeImage = new KeyValue(
				this.imageProperty(),
				animationImages[animationImageIndex]);
		
		// Restart the animation with the new value
		animation.stop();
		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(
				Duration.millis(DURATION / IMAGE_CHANGES),
				e -> playNextKeyFrame(),
				changeImage));
		animation.play();
	}
	
	
	/**
	 * Starts an animation that lasts a duration and then displays the die
	 */
	/*
	public void playAnimation() {
		
		// Determine the ending image
		Image endImage = dieImages.get(die.getValue());
		
		// Create the change image animation
		if (animation != null)
			animation.stop();
		animation = new Timeline();
		
		// Create a Random object for generating random images
		Random rand = new Random();
		
		// Generate random images in the middle
		for (int i = 0; i < IMAGE_CHANGES - 1; i++) {
			animation.getKeyFrames().add(new KeyFrame(
					new Duration(DURATION / IMAGE_CHANGES * i),
					new KeyValue(
							this.imageProperty(),
							animationImages[rand.nextInt(animationImages.length)])));
		}
			
		// End with endImage
		animation.getKeyFrames().add(new KeyFrame(
				new Duration(DURATION - DURATION / IMAGE_CHANGES),
				new KeyValue(
						this.imageProperty(),
						endImage)));
		
		// Play the animation
		animation.play();
	}
	*/
	
	
	/**
	 * Stops the animation and shows the face side up die
	 */
	public void stopAnimation() {
		animation.stop();
		
		this.setImage(dieImages.get(die.getValue()));
		
		diceSound.play();
		
		rolling = false;
	}
	
	
	/**
	 * Returns whether the rolling animation is still going
	 * @return
	 */
	public boolean isRolling() {
		return rolling;
	}

}
