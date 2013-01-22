import java.applet.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class Game extends Applet implements KeyListener, MouseListener, Runnable {
	private static final long serialVersionUID = 1;	
	private static final int width = 700, height = 600;
	private Thread mainLoop;
	private int countdown = 50; //timer that determines when balls are fired
	
	private Graphics bufferGraphics;
	private Image offscreen;	
	
	//Keeps track of the image files
	private Map<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	private BufferedImage centerTitle;
	
	private Font hosFont;
	
	private StartingArrow[] startingArrowStrand = new StartingArrow[4]; //The arrows attached to the center molecule
	private Shooter shooter = new Shooter();
	private CurrentBall firedBall;
	
	private Random generator = new Random();
	private int currentLevel, difficulty, setCannon, strand;
	
	private int interfaceNumber = 100, rotation = 0, score = 0, progress = 0;
	

	
	//Keeps track of the entire strands based on the level
	private List<CurrentBall> strandOne = new ArrayList<CurrentBall>(); 
	private List<CurrentBall> strandTwo = new ArrayList<CurrentBall>();
	private List<CurrentBall> strandThree = new ArrayList<CurrentBall>();
	private List<CurrentBall> strandFour = new ArrayList<CurrentBall>();
	
	//Allows for balls that don't belong to any strands at difficulty 3.
	private List<CurrentBall> oddBalls = new ArrayList<CurrentBall>();
	
	//Keeps track of the balls that are connected to the center molecule
	private List<CurrentBall> inPlayOne = new ArrayList<CurrentBall>();
	private List<CurrentBall> inPlayTwo = new ArrayList<CurrentBall>();
	private List<CurrentBall> inPlayThree = new ArrayList<CurrentBall>();
	private List<CurrentBall> inPlayFour = new ArrayList<CurrentBall>();

	//Button lists for the game states.
	private List<Button> chooseYourOwnAdventureButtons = new ArrayList<Button>();
	private List<Button> pathwayMissionButtons = new ArrayList<Button>();
	private List<Button> pauseScreenButtons = new ArrayList<Button>();
	private List<Button> levelButtons = new ArrayList<Button>();
	private List<Button> missionButtons = new ArrayList<Button>();
	private List<Button> tutorialButtons = new ArrayList<Button>();
	private List<Button> helpButtons = new ArrayList<Button>();
	private List<Button> mainMenuButtons = new ArrayList<Button>();
	
	//Determine the game state
	private boolean ballExistence = false, eraserFired = false, simplifying = false, finishedSimplifying = false;
	private boolean pause = false, help = false, playing = false, gameOver = false, pathwayOrdered = false;
	private boolean pickDifficulty = false, tutorial = false;
	
	
	//Variables associated with the simplification portion of the game
	private int simplificationStep = -1, simplificationStrand, simplificationSpeed = 500;
	private ArrowType sType;  //User's guess as to what the two arrows simplify to
	private boolean waitForUserToSimplify = false;
	private ArrayList<CurrentBall> simplificationList;
	private CurrentBall[] simplifyingMolecules = new CurrentBall[2];
	private BufferedImage[] strips = new BufferedImage[5];
	private BufferedImage[] rips = new BufferedImage[2];
	private int[] stripYPos = {217, 182, 144, 105, 65};
	private Point[] ripAPos = new Point[4];
	private Point[] ripBPos = new Point[4];
	private Point[] overArrowAPos = new Point[4];
	private Point[] overMolAPos = new Point[4];
	private Point[] overArrowBPos = new Point[4];
	private Point[] overMolBPos = new Point[4];
	private Point[] eraserMarkPos = new Point[4];
	
	
	/* JAVA APPLET METHODS
	 * -------------------------------------------------------------------------------
	 * init()
	 * run()
	 * destroy()
	 * ------------------------------------------------------------------------------- 
	 * */
	
	public void init() {	
		setSize(width, height);
		offscreen = createImage(width, height);
		bufferGraphics = offscreen.getGraphics();
		setBackground(Color.WHITE);
		hosFont = new Font("Handwriting-Dakota", Font.BOLD, 34);
		try {
			getImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createButtons();
		fillSimplificationArrays();
		rotate();
		addKeyListener(this);	
		addMouseListener(this);
		mainLoop = new Thread(this);
		mainLoop.start();	
	}
	
	@Override
	public void run() {
		while(!gameOver) {
			if (!pause && playing && currentLevel > 0 && !simplifying) {
				rotate();
				moveEraser();
				moveFiredBall();
				setCountdown();
				checkCollisions();
				repaint();
				try {
					Thread.sleep(45);
				} catch (InterruptedException e) {}
			} else if (simplifying) {
				rotate();
				if (simplificationStep == 3) {
					repaint();
					try {
						Thread.sleep(simplificationSpeed);
					} catch (InterruptedException e) {}
					updateLists();
				}
				if (simplificationStep == -2) {
					simplificationStep = -1;
					checkSimplificationProgress();
				}
			} else {
				rotate();
				repaint();
			}
		}
	}
	
	public void destroy() {
		
	}
	
	
	
	
	/* CANNON GAME LOOP 
	 * -------------------------------------------------------------------------------
	 * 
	 * 
	 * METHODS:
	 * moveEraser()
	 * moveFiredBall()
	 * setCountdown()
	 * fireBall()
	 * addFiredBallToStrand()
	 * removeBallFromStrand(CurrentBall b)
	 * checkPreviousBall(Rectangle prevRect, Rectangle ballRec, CurrentBall previousBall)
	 * checkStartingArrow(Rectangle arrowRect, Rectangle ballRec, int arrowStrand)
	 * checkLastInStrandCollision(List<CurrentBall> inPlay, int strandNum)
	 * checkCollisions()
	 * getNextBall()
	 * setCurrentBallStartingPosition()
	 * 
 	 * ------------------------------------------------------------------------------- 
	 * */
	
	
	
	//The countdown until the next ball is fired.
	private void setCountdown() {
		if (!ballExistence) countdown--;
		if (countdown == 0) {
			fireBall();
			countdown = 50;
		}
	}
	
	/* If user has fired the eraser it will move the eraser up towards the ball.
	 * Once the eraser has gone beyond the screen it resets.
	 */
	private void moveEraser() {
		if (eraserFired) {
			shooter.moveUp();
		}
		if (shooter.getPos().y < -70) {
			eraserFired = false;
			shooter.setyPos();
			getNextBall();
		}
	}
	
	//Moves the fired ball
	private void moveFiredBall() {
		if (ballExistence) {
			firedBall.move();
			shooter.setxPos(firedBall.getPosition().x);
		} 
	}
	
	//Makes the ball, currently in the cannon, start moving.
	private void fireBall() {
		ballExistence = true;
		firedBall.setMove(setCannon);
	}
	
	//Adds a ball to the corresponding inPlay array, meaning it's now attached to the strand in game.
	private void addFiredBallToStrand() {
		switch(strand) {
		case 0: inPlayOne.add(firedBall); break; 
		case 1: inPlayTwo.add(firedBall); break;
		case 2: inPlayThree.add(firedBall); break;
		case 3: inPlayFour.add(firedBall); break;
		}
	}
	
	//Removes a ball from its inPlay array, meaning it was hit with the incorrect ball.
	private void removeBallFromStrand(CurrentBall b) {
		switch(b.getStrand()) {
		case 0: inPlayOne.remove(b); break;
		case 1: inPlayTwo.remove(b); break;
		case 2: inPlayThree.remove(b); break;
		case 3: inPlayFour.remove(b); break;
		}
	}
	
	private boolean checkEdgeCases(CurrentBall previousBall) {
		if (currentLevel == 3) {
			if (previousBall.getNameImage() == imageMap.get("orc") || previousBall.getNameImage() == imageMap.get("replicationproteins")) {
				if (firedBall.getNameImage() == imageMap.get("dnareplication")) {
					firedBall.setStrand(previousBall.getStrand());
					firedBall.setBallImage(previousBall.getBallImage());
					strand = previousBall.getStrand();
					return true;
				}
			}
		}
		
		return false;
	}
	
	//Checks the collision between two balls; if they are part of the same strand, the fired ball will be attached.
	//Otherwise, the fired ball will drop from the screen, and the ball it hit will be removed.
	private void checkPreviousBall(Rectangle prevRect, Rectangle ballRec, CurrentBall previousBall) {
		if (ballRec.intersects(prevRect)) {
			if (previousBall.getStrand() == firedBall.getStrand() || checkEdgeCases(previousBall)) {
				progress++;
				score += 25*difficulty;
				addFiredBallToStrand();
				rotate();
				repaint();
				getNextBall();
			} else {
				removeBallFromStrand(previousBall);
				progress--;
				firedBall.setPosChange(0, 10);
			}
		}
	}
	
	/* Checks the collision between the fired ball and the arrow attached to the central molecule, if they collide.
	 * If the strands match, the fired ball will be attached, otherwise it drops from the screen.
	 */
	private void checkStartingArrow(Rectangle arrowRect, Rectangle ballRec, int arrowStrand) {
		if (ballRec.intersects(arrowRect)) {
			if (firedBall.getStrand() != arrowStrand) {
				firedBall.setPosChange(0, 10);
			} else if (firedBall.getPosChange().y != 10) {
				if (progress < 11) progress++;
				score += 25*difficulty;
				addFiredBallToStrand();
				ballExistence = false;
				rotate();
				repaint();
				getNextBall();
			}
		}
	}
	
	/* Checks the collision for a specific strand and the ball being fired.  Depending on the size
	 * of the strand, it will check the starting arrow or the last ball in the strand.
	 */
	private void checkLastInStrandCollision(List<CurrentBall> inPlay, int strandNum) {
		if (inPlay.size() > 0) {
			CurrentBall previousBall = inPlay.get(inPlay.size()-1);
			Rectangle prevRect = new Rectangle(previousBall.getPosition().x, previousBall.getPosition().y, 35, 35);
			checkPreviousBall(prevRect, firedBall.getBounds(), previousBall);
		} else if (startingArrowStrand[strandNum] != null){
			Rectangle startingArrowRect = startingArrowStrand[strandNum].getBounds();
			checkStartingArrow(firedBall.getBounds(), startingArrowRect, strandNum);
		}
	}
	
	// This checks collisions between the ball being fired and the strands already in play, the eraser, and the centerMolecule.
	private void checkCollisions() {		
		if (ballExistence && firedBall.getPosChange().y != 10) {
			if (ballExistence && rotation % 2 == 0) {
				checkLastInStrandCollision(inPlayOne, 0);
				checkLastInStrandCollision(inPlayTwo, 1);
				checkLastInStrandCollision(inPlayThree, 2);
				checkLastInStrandCollision(inPlayFour, 3);				
			}			
			if (eraserFired && ballExistence) {
				if (shooter.getBounds().intersects(firedBall.getBounds())) {
					firedBall.setPosChange(4, 4);
					repaint();
				}
			}
			Rectangle centerRect = new Rectangle(378, 277, imageMap.get("centerMolecule").getWidth(null) - 25, imageMap.get("centerMolecule").getHeight(null) - 25);
			if (centerRect.intersects(firedBall.getBounds())) getNextBall();
		}
		if (ballExistence) {
			if (firedBall.getPosition().y > 680 || firedBall.getPosition().y < 10) {
				getNextBall();
			}
		}
	}	
	
	/* This is called when the previous ball is either attached to correct strand, lost, or erased.
	 * It determines the strand and cannon randomly, and checks whether there are any molecules left
	 * to be fired from that strand.  If the size of the inPlay ArrayLists is less than the total in the
	 * strand, there are more balls to be fired.
	 * If all the balls have been fired, the game sets up the simplification portion of the game. 
	 * 
	 */
	private void getNextBall() {
		ballExistence = false;
		boolean newBall = false;
		
		int oddBallInt = 0;
		while (!newBall) {
			strand = generator.nextInt(4);
			setCannon = generator.nextInt(4);
			
			//At difficulty 3, a ball that doesn't belong to any of the strands can be fired.
			if (difficulty > 2) {
				int prob = generator.nextInt(10);
				if (prob < 2 && oddBalls.size() > 0) {
					strand = 4;
				}
				oddBallInt = generator.nextInt(2);
			}
			
			switch (strand) {
			case 0: if (inPlayOne.size() < strandOne.size()) {firedBall = new CurrentBall(strandOne.get(inPlayOne.size())); newBall = true;} 
				break;
			case 1: if (inPlayTwo.size() < strandTwo.size()) {firedBall = new CurrentBall(strandTwo.get(inPlayTwo.size())); newBall = true;}
				break;
			case 2: if (inPlayThree.size() < strandThree.size()) {firedBall = new CurrentBall(strandThree.get(inPlayThree.size())); newBall = true;}
				break;
			case 3: if (inPlayFour.size() < strandFour.size()) {firedBall = new CurrentBall(strandFour.get(inPlayFour.size())); newBall = true;}
				break;
			case 4: firedBall = oddBalls.get(oddBallInt); oddBalls.remove(oddBallInt); newBall = true; break;
			}
			
			if (newBall) {
				setCurrentBallStartingPosition();
			}
			if (inPlayOne.size() == strandOne.size() && inPlayTwo.size() == strandTwo.size() && inPlayThree.size() == strandThree.size() && inPlayFour.size() == strandFour.size()) {
				newBall = true;	
				ballExistence = false;
				simplifying = true;	
				pathwayOrdered = true;
				checkSimplificationProgress();
			}
		}
	}
	
	//Sets the ball position to the starting position for the cannon it's set as.
	private void setCurrentBallStartingPosition() {
		firedBall.setPosChange(4, 4);
		switch (setCannon) {
		case 0: firedBall.setPos(118, 18); break;
		case 1: firedBall.setPos(635, 18); break;
		case 2: firedBall.setPos(635, 535); break;
		case 3: firedBall.setPos(118, 535); break;
		}
	}
	
	
	/* SIMPLIFICATION GAME LOOP 
	 * -------------------------------------------------------------------------------
	 * 
	 * 
	 * METHODS:
	 * checkSimplificationProgress()
	 * cloneList(List<CurrentBall> inPlay)
	 * removeMoleculesToBeSimplified(List<CurrentBall> inPlay)
	 * rotateToPosition(int position)
	 * setUpSimplification()
	 * tryToSimplify()
	 * simplifySuccess()
	 * updateLists()
	 * failureToSimplifyCorrectly()
 	 * ------------------------------------------------------------------------------- 
	 * */
	
	
	
	/* Goes through the inPlay arrayLists to see if they still need to be simplified.
	 * If there are more than one balls in the arrayList, it will go through the simplification
	 * process.
	 */
	private void checkSimplificationProgress() {
		if (inPlayOne.size() > 1) {
			rotateToPosition(0);
			cloneList(inPlayOne);
			simplificationStrand = 0;
			removeMoleculesToBeSimplified(inPlayOne);	
		} else if (inPlayTwo.size() > 1) {
			rotateToPosition(6);
			cloneList(inPlayTwo);
			simplificationStrand = 1;
			removeMoleculesToBeSimplified(inPlayTwo);
		} else if (inPlayThree.size() > 1) {
			rotateToPosition(4);
			cloneList(inPlayThree);
			simplificationStrand = 2;
			removeMoleculesToBeSimplified(inPlayThree);
		} else if (inPlayFour.size() > 1) {
			rotateToPosition(2);
			cloneList(inPlayFour);
			simplificationStrand = 3;
			removeMoleculesToBeSimplified(inPlayFour);
		} else {
			finishedSimplifying = true;
			rotate();
			repaint();
			try {
				Thread.sleep(simplificationSpeed);
			} catch (InterruptedException e) {}
			simplifying = false;
			interfaceNumber = 12;
			playing = false;
		}
	}
	
	//Clones an arrayList; used to keep a copy of an inPlay arrayList before the simplification process
	private void cloneList(List<CurrentBall> inPlay) {
		simplificationList = new ArrayList<CurrentBall>();
		for (CurrentBall ball:inPlay) simplificationList.add(new CurrentBall(ball));
	}
	
	/* Removes the two balls from the inPlay arrayList that will be going through the simplification
	 * process because their position will change independently of the other balls in their list because
	 * of the simplification animation.
	 */
	private void removeMoleculesToBeSimplified(List<CurrentBall> inPlay) {
		simplifyingMolecules[1] = inPlay.remove(inPlay.size()-1);
		simplifyingMolecules[0] = inPlay.remove(inPlay.size()-1);
		setUpSimplification();
	}
	
	//Plays the animation that rotates the molecule complex to the desired position.
	private void rotateToPosition(int position) {
		while (rotation != position) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
			if (rotation > position) rotation--;
			else rotation++;
			rotate();
			repaint();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		pathwayOrdered = false;
	}
	
	/* This plays the folding animation of the simplification step and then waits for the user
	 * to input how the strand should simplify.
	 */
	private void setUpSimplification() {
		simplificationStep = 0;
		repaint();
		try {
			Thread.sleep(simplificationSpeed);
		} catch (InterruptedException e) {}
		simplificationStep++;
		repaint();
		try {
			Thread.sleep(simplificationSpeed);
		} catch (InterruptedException e) {}
		simplificationStep++;
		repaint();
		waitForUserToSimplify = true;
	}
	
	
	/* Checks to see if the user simplified correctly, looking at the rules of simplification and what the user inputed
	 * The user pressed 'A' to change the two arrows to an Activation arrow, or 'X' to change them to an Inhibition arrow.
	 * */
	private void tryToSimplify() {
		ArrowType atOne, atTwo;
		if (simplificationList.size() > 2) atOne = simplificationList.get(simplificationList.size()-3).getSimplifyType();
		else atOne = startingArrowStrand[simplificationStrand].getArrowType();
		atTwo = simplifyingMolecules[0].getSimplifyType();
		if ((atOne == ArrowType.ACTIVATE && atTwo == ArrowType.ACTIVATE) || (atOne == ArrowType.INHIBIT && atTwo == ArrowType.INHIBIT)) {
			if (sType == ArrowType.ACTIVATE) {
				simplifySuccess();
			} else {
				failureToSimplifyCorrectly();
			}
		}
		if ((atOne == ArrowType.ACTIVATE && atTwo == ArrowType.INHIBIT) || (atOne == ArrowType.INHIBIT && atTwo == ArrowType.ACTIVATE)) {
			if (sType == ArrowType.INHIBIT) {
				simplifySuccess();
			} else {
				failureToSimplifyCorrectly();
			}
		}
	}
	
	/* If the user correctly chooses the right simplification, the simplificationStep is set to 3, so that 
	 * the game will know to draw the eraserMark and update the ArrayLists.
	 * */
	private void simplifySuccess() {
		waitForUserToSimplify = false;
		simplificationStep = 3;
		score += 25;
	}
	
	/* If the user correctly simplifies the strand, the arrayList must be updated to show the change in
	 * balls and arrow types.  The simplificationList, which holds the saved copy of the current strand 
	 * will be changed to reflect these changes, based on the rules of simplification, and then the inPlay 
	 * arrayList will be set to this updated simplificationList.
	 * */
	private void updateLists() {
		if (simplificationList.size() > 2) {
			simplificationList.get(simplificationList.size()-3).setSimplifyType(sType);
			if (sType == ArrowType.ACTIVATE) {
				String arrowImageString = simplificationList.get(simplificationList.size()-3).getArrowColor() + "Arrow";
				simplificationList.get(simplificationList.size()-3).setArrowImage(imageMap.get(arrowImageString));
			} else {
				String arrowImageString = simplificationList.get(simplificationList.size()-3).getArrowColor() + "Inhibitor";
				simplificationList.get(simplificationList.size()-3).setArrowImage(imageMap.get(arrowImageString));
			}
		} else {
			startingArrowStrand[simplificationStrand].setSimplifyType(sType);
			if (sType == ArrowType.ACTIVATE) startingArrowStrand[simplificationStrand].setArrowImage(imageMap.get(startingArrowStrand[simplificationStrand].getArrowColor() + "Arrow"));
			else startingArrowStrand[simplificationStrand].setArrowImage(imageMap.get(startingArrowStrand[simplificationStrand].getArrowColor() + "Inhibitor"));
		}
		simplificationList.remove(simplificationList.size()-2);
		switch(simplificationStrand) {
		case 0: inPlayOne = simplificationList; break;
		case 1: inPlayTwo = simplificationList; break;
		case 2: inPlayThree = simplificationList; break;
		case 3:	inPlayFour = simplificationList; break;
		}
		simplificationStep = -2;
		rotate();
	}
	
	/* If the user chooses the wrong simplification, the arrayList reverts back to what is was before, which was
	 * saved by the simplificationList.
	 * */
	private void failureToSimplifyCorrectly() {
		switch(simplificationStrand) {
		case 0: inPlayOne = simplificationList; break;
		case 1: inPlayTwo = simplificationList; break;
		case 2: inPlayThree = simplificationList; break;
		case 3:	inPlayFour = simplificationList; break;
		}
		rotate();
		waitForUserToSimplify = false;
		simplificationStep = -2;
		repaint();
	}
		
	
	
	/* GRAPHICS SECTION
	 * -------------------------------------------------------------------------------
	 * These are the methods that draw everything on the screen to Graphics bufferGraphics 
	 * and eventually to Graphics g. This is a double buffering technique to prevent 
	 * flickering.
	 * 
	 * METHODS:
	 * paint(final Graphics g)
	 * update(Graphics g)
	 * activateAntiAliasing(Graphics g)
	 * drawBackground()
	 * drawCentralMolecule()
	 * drawSimplification()
	 * drawCannons()
	 * drawEraser()
	 * drawProgressBar()
	 * drawFiredBall()
	 * drawMenu()
	 * drawPauseScreen()
	 * drawHelpScreen()
	 * drawTutorialScreen()
	 * drawDifficultyScreen()
	 * ------------------------------------------------------------------------------- 
	 * */
	
	/* Uses double buffering to first draw all the images to bufferGraphics, and 
	 * then to update the true Graphics, g.
	 * 
	 * Determines which aspects of the game to draw based on the game state.  
	 * For example, during the simplification stage of the game, the cannons,
	 * eraser, and progress bar are not drawn.  Similarly, if the player pauses
	 * the game, the pause screen is shown.
	 * */
	@Override public void paint(final Graphics g) {
		activateAntiAliasing(bufferGraphics);
		drawBackground();
		drawCentralMolecule();
		drawMoleculesInPlay();
		drawSimplification();
		if (!simplifying) drawCannons();
		if (!simplifying) drawEraser();
		if (!simplifying) drawProgressBar();
		if (pathwayOrdered) bufferGraphics.drawImage(imageMap.get("pathwayComplete"), 191, 15, this);
		if (finishedSimplifying) bufferGraphics.drawImage(imageMap.get("pathwaySimplified"), 191, 15, this);
		drawFiredBall();	
		
		if (tutorial && simplifying) bufferGraphics.drawImage(imageMap.get("tutorialPageTwo"), 191, 375, this);
		if (pause) drawPauseScreen();
		if (pause && help) drawHelpScreen();
		if (tutorial && !playing) drawTutorialScreen();
		if (!playing) drawMenu();
		if (pickDifficulty && !playing) drawDifficultyScreen();
		if (playing) bufferGraphics.drawImage(imageMap.get("greyBorder"), 0, 0, this);
		g.drawImage(offscreen, 0, 0, this);
	}
	
	public void update(Graphics g) {
		paint(g);
	}

	static void activateAntiAliasing(Graphics g) {
	    try {
	        Graphics2D g2d = (Graphics2D)g;

	        // for antialiasing geometric shapes
	        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	                              RenderingHints.VALUE_ANTIALIAS_ON );

	        // for antialiasing text
	        g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
	                              RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

	        // to go for quality over speed
	        g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
	                              RenderingHints.VALUE_RENDER_QUALITY );
	    }
	    catch(ClassCastException ignored) {}
	}	
	
	/* Draws the simplification animation based on the simplificationStep.
	 * */
	private void drawSimplification() {
		if (simplificationStep == -2) {
			bufferGraphics.drawImage(strips[simplificationList.size()], 401, stripYPos[simplificationList.size()-1], this);
		}
		if (simplificationStep == 0) {
			bufferGraphics.drawImage(strips[simplificationList.size()-1], 401, stripYPos[simplificationList.size()-1], this);
			bufferGraphics.drawImage(simplifyingMolecules[0].getBallImage(), simplifyingMolecules[0].getPosition().x, simplifyingMolecules[0].getPosition().y, this);
			bufferGraphics.drawImage(simplifyingMolecules[0].getNameImage(), 
									 simplifyingMolecules[0].getPosition().x + 24 - simplifyingMolecules[0].getNameImage().getWidth()/2, 
									 simplifyingMolecules[0].getPosition().y + 24 - simplifyingMolecules[0].getNameImage().getHeight()/2, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getBallImage(), simplifyingMolecules[1].getPosition().x, simplifyingMolecules[1].getPosition().y, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getNameImage(), 
									 simplifyingMolecules[1].getPosition().x + 24 - simplifyingMolecules[1].getNameImage().getWidth()/2, 
									 simplifyingMolecules[1].getPosition().y + 24 - simplifyingMolecules[1].getNameImage().getHeight()/2, this);
			simplifyingMolecules[0].paintArrow((Graphics2D) bufferGraphics, this);
		} else if (simplificationStep == 1) {
			bufferGraphics.drawImage(strips[simplificationList.size()-2], 401, stripYPos[simplificationList.size()-2], this);
			simplifyingMolecules[0].setArrowPos(overArrowAPos[simplificationList.size()-2].x, overArrowAPos[simplificationList.size()-2].y);
			bufferGraphics.drawImage(simplifyingMolecules[0].getBallImage(), simplifyingMolecules[0].getPosition().x, simplifyingMolecules[0].getPosition().y, this);
			bufferGraphics.drawImage(simplifyingMolecules[0].getNameImage(), 
									 simplifyingMolecules[0].getPosition().x + 24 - simplifyingMolecules[0].getNameImage().getWidth()/2, 
									 simplifyingMolecules[0].getPosition().y + 24 - simplifyingMolecules[0].getNameImage().getHeight()/2, this);
			bufferGraphics.drawImage(rips[0], ripAPos[simplificationList.size()-2].x, ripAPos[simplificationList.size()-2].y, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getBallImage(), overMolAPos[simplificationList.size()-2].x, overMolAPos[simplificationList.size()-2].y, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getNameImage(), 
								     overMolAPos[simplificationList.size()-2].x + 24 - simplifyingMolecules[1].getNameImage().getWidth()/2, 
								     overMolAPos[simplificationList.size()-2].y + 24 - simplifyingMolecules[1].getNameImage().getHeight()/2, this);
			simplifyingMolecules[0].paintArrow((Graphics2D) bufferGraphics, this);
		} else if (simplificationStep == 2 || simplificationStep == 3) {
			bufferGraphics.drawImage(strips[simplificationList.size()-2], 401, stripYPos[simplificationList.size()-2], this);
			simplifyingMolecules[0].setArrowPos(overArrowBPos[simplificationList.size()-2].x, overArrowBPos[simplificationList.size()-2].y);
			bufferGraphics.drawImage(rips[1], ripBPos[simplificationList.size()-2].x, ripBPos[simplificationList.size()-2].y, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getBallImage(), overMolBPos[simplificationList.size()-2].x, overMolBPos[simplificationList.size()-2].y, this);
			bufferGraphics.drawImage(simplifyingMolecules[1].getNameImage(), 
								     overMolBPos[simplificationList.size()-2].x + 24 - simplifyingMolecules[1].getNameImage().getWidth()/2, 
								     overMolBPos[simplificationList.size()-2].y + 24 - simplifyingMolecules[1].getNameImage().getHeight()/2, this);
			simplifyingMolecules[0].paintArrow((Graphics2D) bufferGraphics, this);
		}
		if (simplificationStep == 3) {
			bufferGraphics.drawImage(imageMap.get("eraserMark"), eraserMarkPos[simplificationList.size()-2].x, eraserMarkPos[simplificationList.size()-2].y, this);
		}
	}
	
	private void drawDifficultyScreen() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawRect(0, 0, width, height); bufferGraphics.fillRect(0, 0, width, height);
		bufferGraphics.drawImage(imageMap.get("redPause"), 115, 155, this);
		drawString(34, "Pick Difficulty", new Point(144, 216));
		drawButtonsFromList(levelButtons);		
	}
	
	private void drawPauseScreen() {
		bufferGraphics.drawImage(imageMap.get("redPause"), 115, 155, this);
		drawString(34, getTitleString(), new Point(144, 216));
		drawButtonsFromList(pauseScreenButtons);
	}
	
	private void drawHelpScreen() {
		if (simplifying) bufferGraphics.drawImage(imageMap.get("tutorialBig"), 115, 155, this);
		else bufferGraphics.drawImage(imageMap.get("tutorialPageOne"), 115, 155, this);
		drawButtonsFromList(helpButtons);
	}
	
	private void drawTutorialScreen() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawRect(0, 0, width, height); bufferGraphics.fillRect(0, 0, width, height);
		bufferGraphics.drawImage(imageMap.get("tutorialPageOne"), 115, 155, this);
		drawButtonsFromList(tutorialButtons);
	}
	
	private void drawString(int fontSize, String string, Point position) {
		hosFont = hosFont.deriveFont(fontSize);
		bufferGraphics.setFont(hosFont);
		bufferGraphics.setColor(new Color(51, 51, 51));
		bufferGraphics.drawString(string, position.x, position.y);
	}
	
	private String getTitleString() {
		switch(currentLevel) {
		case 1: return "APC"; 
		case 2: return "S-Cdk"; 
		case 3: return "p53"; 
		case 4: return "Delta-Notch"; 
		case 5: return "TGF-Beta"; 
		case 6: return "Hedgehog"; 
		case 7: return "Wnt"; 
		}
		return "";
	}
	
	private void drawBackground() {
		bufferGraphics.drawImage(imageMap.get("background"), 0, 0, this);
		if (pause) {
			bufferGraphics.drawImage(imageMap.get("pauseButtonPressed"), 14, 548, this);
		} else { 
			bufferGraphics.drawImage(imageMap.get("pauseButton"), 14, 548, this);
		}
		hosFont = hosFont.deriveFont(34.0f);
		bufferGraphics.setFont(hosFont);
		FontMetrics fontMetrics = bufferGraphics.getFontMetrics(hosFont);
		bufferGraphics.setColor(Color.RED);
		bufferGraphics.drawString("" + score, 95 - fontMetrics.stringWidth("" + score), 94);
	}
	
	private void drawEraser() {	
		Point p = shooter.getPos();
		bufferGraphics.drawImage( imageMap.get("shooterImg"), p.x, p.y, this);
	}
	
	private void drawProgressBar() {
		int heightProg = getHeightOfBar();
		for (int i = 0; i < progress; i++) {
			bufferGraphics.drawImage(imageMap.get("progressBar"), 623, 515 - heightProg * (i+1), imageMap.get("progressBar").getWidth(this), heightProg, this);
		}
	}
	
	private void drawMenu() {
		if (interfaceNumber == 0) {
			bufferGraphics.drawImage(imageMap.get("missionBackground"), 0, 0, this);
			drawButtonsFromList(pathwayMissionButtons);
		} else if (interfaceNumber == 11) {
			bufferGraphics.drawImage(imageMap.get("chooseBackground"), 0, 0, this);
			drawButtonsFromList(chooseYourOwnAdventureButtons);
		} else if (interfaceNumber == 12) {
			bufferGraphics.drawImage(imageMap.get("missionAccomplished"), 115, 155, this);
			hosFont = hosFont.deriveFont(18.0f);
			bufferGraphics.setFont(hosFont);
			FontMetrics fontMetrics = bufferGraphics.getFontMetrics(hosFont);
			bufferGraphics.setColor(new Color(51, 51, 51));
			bufferGraphics.drawString("" + score, 460 - fontMetrics.stringWidth("" + score), 265);
			bufferGraphics.drawString("+50", 460 - fontMetrics.stringWidth("+50"), 303);
			bufferGraphics.drawString("" + (score+50), 460 - fontMetrics.stringWidth("" + (score+50)), 335);
			drawButtonsFromList(missionButtons);
		} else if (interfaceNumber == 100) {
			bufferGraphics.drawImage(imageMap.get("mainBackground"), 0, 0, this);
			drawButtonsFromList(mainMenuButtons);
		}
	}
	
	private void drawButtonsFromList(List<Button> buttonList) {
		for (int i = 0; i < buttonList.size(); i++) {
			Button b = buttonList.get(i);
			if (b.isPressed()) bufferGraphics.drawImage(b.getButtonPressed(), b.getPosition().x, b.getPosition().y, this);
			else bufferGraphics.drawImage(b.getButton(), b.getPosition().x, b.getPosition().y, this);
		}
	}
	
	/* Checks to see if the inPlay arrayList has at least one ball in it.  If so,
	 * the fired ball can be colored gray at high difficulties.  This is to minimize 
	 * confusion when there could be multiple options for where a ball could be placed
	 * on the central molecule.
	 */
	private boolean checkArrayNumber(int ballStrand) {
		switch (ballStrand) {
		case 0: if (inPlayOne.size() > 0) return true; else break;
		case 1: if (inPlayTwo.size() > 0) return true; else break;
		case 2: if (inPlayThree.size() > 0) return true; else break;
		case 3: if (inPlayFour.size() > 0) return true; else break;
		}
		return false;
	}
	
	private void drawFiredBall() {
		if (firedBall != null && !simplifying) {
			if (difficulty > 1 && (checkArrayNumber(firedBall.getStrand()) || currentLevel > 3)) bufferGraphics.drawImage(imageMap.get("greyBall"), firedBall.getPosition().x, firedBall.getPosition().y, this);
			else bufferGraphics.drawImage(firedBall.getBallImage(), firedBall.getPosition().x, firedBall.getPosition().y, this);
			bufferGraphics.drawImage(firedBall.getNameImage(), firedBall.getPosition().x + 24 - firedBall.getNameImage().getWidth()/2, firedBall.getPosition().y + 24 - firedBall.getNameImage().getHeight()/2, this);
		}
	}
	
	private void drawCannons() {
		bufferGraphics.drawImage(imageMap.get("cannonUL"), 106, 9, this);
		bufferGraphics.drawImage(imageMap.get("cannonUR"), 106, 510, this);
		bufferGraphics.drawImage(imageMap.get("cannonDL"), 610, 9, this);
		bufferGraphics.drawImage(imageMap.get("cannonDR"), 610, 510, this);
	}
	
	private void drawCentralMolecule() {
		if (rotation%2 == 0) {
			bufferGraphics.drawImage(imageMap.get("centerMolecule"), 363, 263, this);	
		} else {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(45), 363 + imageMap.get("centerMolecule").getWidth()/2, 263 + imageMap.get("centerMolecule").getHeight()/2);
			affineTransform.translate(363, 263);
			Graphics2D g2d = (Graphics2D) bufferGraphics;
			g2d.drawImage(imageMap.get("centerMolecule"), affineTransform, this);	
		}

		bufferGraphics.drawImage(centerTitle, 383, 288, this);
	}
	
	private void drawMoleculesFromList(List<CurrentBall> inPlay) {
		for (int i = 0; i < inPlay.size(); i++) {
			CurrentBall b = inPlay.get(i);
			bufferGraphics.drawImage(b.getBallImage(), b.getPosition().x, b.getPosition().y, this);
			bufferGraphics.drawImage(b.getNameImage(), b.getPosition().x + 24 - b.getNameImage().getWidth()/2, b.getPosition().y + 24 - b.getNameImage().getHeight()/2, this);
		}
	}
	
	private void drawArrowsFromList(List<CurrentBall> currentBallList) {
		for (int i = 0; i < currentBallList.size(); i++) {
			currentBallList.get(i).paintArrow((Graphics2D) bufferGraphics, this);
		}
	}
	
	private void drawMoleculesInPlay() {	
		drawMoleculesFromList(inPlayOne);
		drawMoleculesFromList(inPlayTwo);
		drawMoleculesFromList(inPlayThree);
		drawMoleculesFromList(inPlayFour);

		if (startingArrowStrand[0] != null) startingArrowStrand[0].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[1] != null) startingArrowStrand[1].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[2] != null) startingArrowStrand[2].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[3] != null) startingArrowStrand[3].paintArrow((Graphics2D) bufferGraphics, this);
		
		drawArrowsFromList(inPlayOne);
		drawArrowsFromList(inPlayTwo);
		drawArrowsFromList(inPlayThree);
		drawArrowsFromList(inPlayFour);
	}
	
	private int getHeightOfBar() {
		int totalMols = strandOne.size() + strandTwo.size() + strandThree.size() + strandFour.size();
		if (totalMols > 0) return 400 / (totalMols);
		return -100;
	}
	

	
	/* POSITIONING SECTION
	 * -------------------------------------------------------------------------------
	 * These methods deal with positioning the balls and arrows that are in play on the 
	 * screen.  Because a main part of the game deals with rotating the central molecule,
	 * the positions of each of the balls and their respective arrow has to be reestablished.
	 * 
	 * 
	 * METHODS:
	 * rotate()
	 * 
	 * repositionBallsInArray(List<CurrentBall> inPlay, int startingPosition)
	 * 
	 * setStartingArrowPosition(StartingArrow strandArrow, int startingPosition)
	 * 
	 * setBallPosition(int i, int change, CurrentBall b)
	 * ------------------------------------------------------------------------------- 
	 */
	

	/* This sets the position of the balls and arrows that are in play based on their starting position
	 * and the rotation generated by the user rotating the molecule complex.
	 * */
	private void rotate() {
		if (startingArrowStrand[0] != null) setStartingArrowPosition(startingArrowStrand[0], 0);
		if (startingArrowStrand[1] != null) setStartingArrowPosition(startingArrowStrand[1], 2);
		if (startingArrowStrand[2] != null) setStartingArrowPosition(startingArrowStrand[2], 4);
		if (startingArrowStrand[3] != null) setStartingArrowPosition(startingArrowStrand[3], 6);
		
		repositionBallsInArray(inPlayOne, 0);
		repositionBallsInArray(inPlayTwo, 2);
		repositionBallsInArray(inPlayThree, 4);
		repositionBallsInArray(inPlayFour, 6);
	}
	
	/* Iterates through an inPlay ArrayList and sets the position of each according to it's 
	 * */
	private void repositionBallsInArray(List<CurrentBall> inPlay, int startingPosition) {
		for (int i = 0; i < inPlay.size(); i++) {
			CurrentBall b = inPlay.get(i);
			setBallPosition(i, startingPosition, b);
		}
	}

	
	/* Sets the position of the arrows attached to the central molecule according to the rotation
	 * and startingPosition of the arrow.
	 * */
	private void setStartingArrowPosition(StartingArrow strandArrow, int startingPosition) {
		if ((rotation+startingPosition)%8 == 0) {
			strandArrow.setPos(414, 273);
			strandArrow.setImage((rotation+startingPosition)%8);
		}
		if ((rotation+startingPosition)%8 == 1) {
			if (strandArrow.getArrowColor() == "high" || strandArrow.getArrowColor() == "low") {
				strandArrow.setPos(420, 290);
			} else strandArrow.setPos(424, 292);
			strandArrow.setImage((rotation+startingPosition)%8);

		}
		if ((rotation+startingPosition)%8 == 2) {
			if (strandArrow.getArrowColor() == "high" || strandArrow.getArrowColor() == "low") {
				strandArrow.setPos(411, 311);
			} else strandArrow.setPos(414, 314);
			strandArrow.setImage((rotation+startingPosition)%8);
	
		}
		if ((rotation+startingPosition)%8 == 3) {
			if (strandArrow.getArrowColor() == "high" || strandArrow.getArrowColor() == "low") {
				strandArrow.setPos(390, 320);
			} else strandArrow.setPos(392, 325);
			strandArrow.setImage((rotation+startingPosition)%8);

		}
		if ((rotation+startingPosition)%8 == 4) {
			strandArrow.setPos(372, 314);
			strandArrow.setImage((rotation+startingPosition)%8);	
		}
		if ((rotation+startingPosition)%8 == 5) {
			strandArrow.setPos(360, 292);
			strandArrow.setImage((rotation+startingPosition)%8);
			
		}
		if ((rotation+startingPosition)%8 == 6) {
			strandArrow.setPos(372, 273);
			strandArrow.setImage((rotation+startingPosition)%8);
			
		}
		if ((rotation+startingPosition)%8 == 7) {
			strandArrow.setPos(392, 264);
			strandArrow.setImage((rotation+startingPosition)%8);
		}

	}
	
	/*Sets the position of the ball based on the rotation of the central molecule and the strand of the ball.
	 * Unfortunately, hard coding the numbers seemed to be the best way of getting them to line up due to inconsistencies
	 * of the arrowImages, and other slight quirks.
	 * */
	private void setBallPosition(int i, int change, CurrentBall b) {
		if ((rotation+change)%8 == 0) {
			b.setPos(416 + 37*i, 239 - 37*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x + 35, b.getPosition().y - 2);
		}
		if ((rotation+change)%8 == 1) {
			b.setPos(434 + 52*i, 277);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x + 42, b.getPosition().y + 15);
		}
		if ((rotation+change)%8 == 2) {
			b.setPos(418 + 37*i, 316 + 37*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x + 33, b.getPosition().y + 34);
		}
		if ((rotation+change)%8 == 3) {
			b.setPos(377, 334 + 52*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x + 15, b.getPosition().y + 42);
		}
		if ((rotation+change)%8 == 4) {
			b.setPos(335 - 37*i, 315 + 37*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x - 2, b.getPosition().y + 33);
		}
		if ((rotation+change)%8 == 5) {
			b.setPos(320 - 52*i, 277);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x - 10, b.getPosition().y + 15);
		}
		if ((rotation+change)%8 == 6) {
			b.setPos(338 - 37*i, 236 - 37*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x - 3, b.getPosition().y - 3);
		}
		if ((rotation+change)%8 == 7) {
			b.setPos(377, 220 - 52*i);
			b.setArrowImageRotation((rotation+change)%8);
			b.setArrowPos(b.getPosition().x + 15, b.getPosition().y - 10);
		}	
	}
	

	
	/* USER INPUT SECTION
	 * -------------------------------------------------------------------------------
	 * The user will be using the keys to do things like rotate the molecule complex,
	 * and choose how to simplify.  The user will also use the mouse to select the 
	 * buttons in the interface.
	 * 
	 * METHODS:
	 * keyPressed(KeyEvent evt)
	 * 
	 * mouseClicked(MouseEvent mouseEvt)
	 * 
	 * checkToSetUpLevel(Point mouseClickPosition)
	 * 
	 * checkIfButtonPressedArrayList<Button> buttonList, Point p)
	 * 
	 * setUpInterfaceOrLevels(int action)
	 * ------------------------------------------------------------------------------- 
	 */
	
	
	/* Takes in the user's key presses and interprets them based on the game state.
	 */
	@Override
	public void keyPressed(KeyEvent evt) {
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_SPACE && !simplifying && ballExistence) {
			if (firedBall != null) {
				eraserFired = true;
				firedBall.setPosChange(0, 0);
			}
		} else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) && !simplifying) {
			rotation = (rotation + 7)%8;
			rotate();
		} else if (key == KeyEvent.VK_P) {
			pause = !pause;
		} else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) && !simplifying) {
			rotation = (rotation + 1)%8;
			rotate();		
		} else if (key == KeyEvent.VK_X && waitForUserToSimplify) {
			sType = ArrowType.INHIBIT;
			tryToSimplify();
		} else if (key == KeyEvent.VK_A && waitForUserToSimplify) {
			sType = ArrowType.ACTIVATE;
			tryToSimplify();
		}
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvt) {		
		checkToSetUpLevel(mouseEvt.getPoint());
	}
	

	/* When the player clicks with the mouse, the game will check to see if the player is pressing a button.
	 * The buttons the player could be pressing depends what state the player is in (pause screen, main screen,
	 * etc...).  Each of these screens has a list of possible buttons which will be iterated through in 
	 * checkIfButtonPressed.
	 * 
	 * */	
	private void checkToSetUpLevel(Point mouseClickPosition) {
		if (pickDifficulty) {
			checkIfButtonPressed(levelButtons, mouseClickPosition);
		} else if (tutorial && !playing) { 
			checkIfButtonPressed(tutorialButtons, mouseClickPosition);
		} else if (pause) {
			checkIfButtonPressed(pauseScreenButtons, mouseClickPosition);
		} else if (help) {
			checkIfButtonPressed(helpButtons, mouseClickPosition);
		} else if (interfaceNumber == 0) {
			checkIfButtonPressed(pathwayMissionButtons, mouseClickPosition);
		} else if (interfaceNumber == 11) {
			checkIfButtonPressed(chooseYourOwnAdventureButtons, mouseClickPosition);
		} else if (interfaceNumber == 12) {
			checkIfButtonPressed(missionButtons, mouseClickPosition);
		} else if (playing) {
			if (new Rectangle(14, 548, 88, 42).contains(mouseClickPosition)) {
				pause = !pause;
			}
		} else if (interfaceNumber == 100) {
			checkIfButtonPressed(mainMenuButtons, mouseClickPosition);
		}
		repaint();
	}
	
	/* Compares the point a player clicks on with the buttons in the list of buttons from the current game state.
	 * If a button is pressed, the game takes in the new location or action dictated by the button.
	 */
	private void checkIfButtonPressed(List<Button> buttonList, Point p) {
		for (int i = 0; i < buttonList.size(); i++) {
			Button b = buttonList.get(i);
			if (b.getBounds().contains(p)) {
				setUpInterfaceOrLevels(b.getTarget());
			}
		}
	}
	
	/* Takes in the target (as an int) from the user clicking on a button and determines the action the game will take.
	 * If the action is less than 0, it sets the difficulty for the level. 
	 * If the action is between 1 and 7, it sets the level that will be played.
	 * If the action is 0, it goes to the main menu.
	 * If the action is 10, it sets up the tutorial.
	 * If the action is 20, it restarts the level.
	 * If the action is 25, it goes to the next level or the main menu if the user is done with a set of pathways.
	 * If the action is 30, it unpauses the game.
	 * If the action is 40, it gets the help screen.
	 */
	private void setUpInterfaceOrLevels(int action) {
		interfaceNumber = action;
		if (action < 0) {
			difficulty = -action; 
			pickDifficulty = false; 
			setUpLists(currentLevel);
		}
		if (action > 0 && action < 8) {
			currentLevel = action;
			if (tutorial) {
				difficulty = 1;
				setUpLists(currentLevel);
			}
			else pickDifficulty = true;
		}
		switch(action) {
		case 0:  goBackToMainMenu(); break;
		case 10: tutorial = true; break;  
		case 20: simplificationStep = -1; pickDifficulty = false; pause = false; help = false; waitForUserToSimplify = false; simplifying = false; setUpLists(currentLevel); break;
		case 25: if (currentLevel == 3 || currentLevel == 7) {interfaceNumber = 0; goBackToMainMenu(); break;}
				 else {
					 currentLevel++; finishedSimplifying = false; simplificationStep = -1; pathwayOrdered = false; simplifying = false;
					 pickDifficulty = false; tutorial = false;
					 setUpLists(currentLevel); break; 
				 }
		case 30: pause = false; help = false; break;
		case 40: help = true;
		case 70: break;
		case 80: break;
		case 90: break;
		case 100: goBackToMainMenu(); break;
		}
	}
	
	//Sets up the main menu by making sure all of the booleans that determine the state of the game are reset.
	private void goBackToMainMenu() {
		simplificationStep = -1;
		pickDifficulty = false; 
		tutorial = false; 
		pause = false;
		playing = false; 
		help = false; 
		waitForUserToSimplify = false;
		finishedSimplifying = false; 
		simplifying = false;
		repaint();
	}
	
	
	
	/* LEVEL SET UP SECTION
	 * -------------------------------------------------------------------------------
	 * When a new level is started, the arrayLists that contain the complete strands 
	 * have to be updated to reflect that level.  
	 * 
	 * 
	 * setUpLists(int level)
	 * setUpLevelOne() - setUpLevelSeven()
	 * -------------------------------------------------------------------------------
	 * */
	
	
	private void setUpLists(int level) {
		strandOne.clear(); strandTwo.clear(); strandThree.clear(); strandFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		score = 0;
		progress = 0;
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("apcoli"), null, null, false, null));
		
		switch(level) {
		case 1: setUpLevelOne(); break;
		case 2: setUpLevelTwo(); break;
		case 3: setUpLevelThree(); break;
		case 4: setUpLevelFour(); break;
		case 5: setUpLevelFive(); break;
		case 6: setUpLevelSix(); break;
		case 7: setUpLevelSeven(); break;
		}
		getNextBall();
		playing = true;
		rotate();
		repaint();
	}
	/* The setUpLevel____ methods are used to provide the starting point for each level.
	 * 
	 * */
	private void setUpLevelOne() {						
		centerTitle = imageMap.get("apc");
		
		startingArrowStrand[0] = new StartingArrow(0, true,  imageMap.get("blueArrow"), ArrowType.ACTIVATE, "blue");
		startingArrowStrand[1] = new StartingArrow(1, false,  imageMap.get("greenInhibitor"), ArrowType.INHIBIT, "green");
		startingArrowStrand[2] = new StartingArrow(2, false,  imageMap.get("redInhibitor"), ArrowType.INHIBIT, "red");
		startingArrowStrand[3] = null;

		strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("mcdk"), null, null, false, null));
		
		strandTwo.add(new CurrentBall(1, imageMap.get("greenBall"), imageMap.get("mcyclin"), null, null, false, null));
		
		strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("securins"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, "red"));
		strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("separase"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, "red"));
		strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("cohesin"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, "red"));
		strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("anaphase"), null, null, false, null));

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("p21"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("dnadamage"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("delta"), null, null, false, null));

		
	}
	
	private void setUpLevelTwo() {
		centerTitle = imageMap.get("p53");
		
		startingArrowStrand[0] = new StartingArrow(0, false,  imageMap.get("highArrow"), ArrowType.ACTIVATE, "high");
		startingArrowStrand[1] = new StartingArrow(1, false,  imageMap.get("lowArrow"), ArrowType.ACTIVATE, "low");
		startingArrowStrand[2] = new StartingArrow(2, true,  imageMap.get("orangeArrow"), ArrowType.ACTIVATE, "orange");
		startingArrowStrand[3] = null;
		
		strandOne.add(new CurrentBall(0, imageMap.get("redBall"),  imageMap.get("bax"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, "red"));
		strandOne.add(new CurrentBall(0, imageMap.get("redBall"),  imageMap.get("cytc"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, "red"));
		strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("adaptorProteins"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, "red"));
		strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("caspase"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, "red"));
		strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("apoptosis"), null, null, false, null));
		
		strandTwo.add(new CurrentBall(1, imageMap.get("blueBall"), imageMap.get("p21"), ArrowType.INHIBIT,  imageMap.get("blueInhibitor"), false, "blue"));
		strandTwo.add(new CurrentBall(1, imageMap.get("blueBall"), imageMap.get("scdkscyclin"), ArrowType.ACTIVATE,  imageMap.get("blueArrow"), false, "blue"));
		strandTwo.add(new CurrentBall(1, imageMap.get("blueBall"), imageMap.get("dnareplication"), null, null, false, null));

		strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("dnadamage"), null, null, false, null));

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("betacatenin"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("smo"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("securins"), null, null, false, null));

	}
	
	private void setUpLevelThree() {
		centerTitle = imageMap.get("sckd");
		
		startingArrowStrand[0] = new StartingArrow(0, true,  imageMap.get("greenArrow"), ArrowType.ACTIVATE, "green");
		startingArrowStrand[1] = new StartingArrow(1, true,  imageMap.get("redInhibitor"), ArrowType.INHIBIT, "red");
		startingArrowStrand[2] = new StartingArrow(2, false,  imageMap.get("orangeInhibitor"), ArrowType.INHIBIT, "orange");
		startingArrowStrand[3] = new StartingArrow(3, false,  imageMap.get("blueInhibitor"), ArrowType.INHIBIT, "blue");
	
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("mapkinaserelay"),  ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, "green"));
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("ras"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, "green"));
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("rasactprotein"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, "green"));
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("rtks"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, "green"));
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("mitogen"), null, null, false, null));
		
		strandTwo.add(new CurrentBall(1, imageMap.get("redBall"), imageMap.get("p21"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true, "red"));
		strandTwo.add(new CurrentBall(1, imageMap.get("redBall"), imageMap.get("p53"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true, "red"));
		strandTwo.add(new CurrentBall(1, imageMap.get("redBall"), imageMap.get("dnadamage"),  null, null, false, null));

		strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("rb"), ArrowType.INHIBIT,imageMap.get("orangeInhibitor"), false, "orange"));
		strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("replicationproteins"), ArrowType.ACTIVATE, imageMap.get("orangeArrow"), false, "orange"));
		strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("dnareplication"), null, null, false, null));

		strandFour.add(new CurrentBall(3, imageMap.get("blueBall"), imageMap.get("cdc6"), ArrowType.INHIBIT,imageMap.get("blueInhibitor"), false, "blue"));
		strandFour.add(new CurrentBall(3, imageMap.get("blueBall"), imageMap.get("orc"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, "blue"));	
		strandFour.add(new CurrentBall(3, imageMap.get("blueBall"), imageMap.get("dnareplication"), null, null, false, null));	

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("securins"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("mcdk"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("wnt"), null, null, false, null));

	}
	
	private void setUpLevelFour() {
		centerTitle = imageMap.get("notch");
		
		startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("greenArrow"), ArrowType.ACTIVATE, "green");
		startingArrowStrand[1] = null;
		startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("orangeArrow"), ArrowType.ACTIVATE, "orange");
		startingArrowStrand[3] = null;	
		
		strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("genetranscription"),  null, null, false, null));
		
		strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("delta"), null, null, false, null));

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("betacatenin"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("lrp"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("mcdk"), null, null, false, null));

	}
	
	
	/* The set up for levels 5-7 change randomly because the molecule strands are essentially a straight line.
	 * The center molecule can therefore be any of the molecules that aren't at the end of either side.
	 */
	
	
	private void setUpLevelFive() {
		int placeHolder = generator.nextInt(2);
		if (placeHolder == 0){
			centerTitle = imageMap.get("tgfbeta");
			startingArrowStrand[2] = new StartingArrow(2, false, imageMap.get("orangeArrow"), ArrowType.ACTIVATE, "orange");
			startingArrowStrand[0] = new StartingArrow(0, true,imageMap.get("redArrow"), ArrowType.ACTIVATE, "red");
			strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("nodal"),  null, null, true, null));
			strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("smads"), ArrowType.ACTIVATE,imageMap.get("orangeArrow"), false, "orange"));
			strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("genetranscription"), null, null, false, null));
		} else if (placeHolder == 1) {
			centerTitle = imageMap.get("smads");
			startingArrowStrand[2] = new StartingArrow(2, false, imageMap.get("orangeArrow"), ArrowType.ACTIVATE, "orange");
			startingArrowStrand[0] = new StartingArrow(0, true,imageMap.get("redArrow"), ArrowType.ACTIVATE, "red");
			strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("tgfbeta"), ArrowType.ACTIVATE,imageMap.get("redArrow"), true, "red"));
			strandOne.add(new CurrentBall(0, imageMap.get("redBall"), imageMap.get("nodal"),  null, null, true, null));
			strandThree.add(new CurrentBall(2, imageMap.get("orangeBall"), imageMap.get("genetranscription"), null, null, false, null));
		}

		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("dnadamage"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("cohesin"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("mcyclin"), null, null, false, null));

	}
	
	private void setUpLevelSix() {
		int placeHolder = generator.nextInt(3);
		if (placeHolder == 0){
			centerTitle = imageMap.get("patched");
			startingArrowStrand[2] = new StartingArrow(2, false, imageMap.get("redInhibitor"), ArrowType.INHIBIT, "red");
			startingArrowStrand[0] = new StartingArrow(0, true,imageMap.get("blueInhibitor"), ArrowType.INHIBIT, "blue");
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("hedgehog"),  null, null, true, null));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("smo"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, "red"));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, "red"));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, "red"));
		} else if (placeHolder == 1) {
			centerTitle = imageMap.get("smo");
			startingArrowStrand[2] = new StartingArrow(2, false,imageMap.get("redArrow"), ArrowType.ACTIVATE, "red");
			startingArrowStrand[0] = new StartingArrow(0, true,imageMap.get("blueInhibitor"), ArrowType.INHIBIT, "blue");
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("patched"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true, "blue"));
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("hedgehog"), null, null, false, null));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, "red"));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, "red"));
		} else if (placeHolder == 2) {
			centerTitle = imageMap.get("gli");
			startingArrowStrand[2] = new StartingArrow(2, false,imageMap.get("redArrow"), ArrowType.ACTIVATE, "red");
			startingArrowStrand[0] = new StartingArrow(0, true,imageMap.get("blueArrow"), ArrowType.ACTIVATE, "blue");
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("smo"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true, "blue"));
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("patched"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), true, "blue"));
			strandOne.add(new CurrentBall(0, imageMap.get("blueBall"), imageMap.get("hedgehog"), null, null, true, null));
			strandThree.add(new CurrentBall(2, imageMap.get("redBall"), imageMap.get("genetranscription"), null, null, false, null));
		}

		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("mcdk"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("rtks"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("betacatenin"), null, null, false, null));

	}
	
	private void setUpLevelSeven() {
		int placeHolder = generator.nextInt(4);
		if (placeHolder == 0){
			centerTitle = imageMap.get("frizzled");
			startingArrowStrand[2] = new StartingArrow(2, false,imageMap.get("blueArrow"), ArrowType.ACTIVATE, "blue");
			startingArrowStrand[0] = new StartingArrow(0, true, imageMap.get("greenArrow"), ArrowType.ACTIVATE, "green");
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("wnt"),  null, null, false, null));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("lrp"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("apcoli"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"),  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("genetranscription"), null, null, false, null));
		} else if (placeHolder == 1) {
			centerTitle = imageMap.get("lrp");
			startingArrowStrand[2] = new StartingArrow(2, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT, "blue");
			startingArrowStrand[0] = new StartingArrow(0, true, imageMap.get("greenArrow"), ArrowType.ACTIVATE, "green");
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("wnt"), null, null, false, null));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("apcoli"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"),  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("genetranscription"), null, null, false, null));
		} else if (placeHolder == 2) {
			centerTitle = imageMap.get("apcoli");
			startingArrowStrand[2] = new StartingArrow(2, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT, "blue");
			startingArrowStrand[0] = new StartingArrow(0, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT, "green");
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("frizzled"), ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("wnt"), null, null, false, null));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"),  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, "blue"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("genetranscription"), null, null, false, null));
		} else if (placeHolder == 3) {
			centerTitle =  imageMap.get("betacatenin");
			startingArrowStrand[2] = new StartingArrow(2, false,imageMap.get("blueArrow"), ArrowType.ACTIVATE, "blue");
			startingArrowStrand[0] = new StartingArrow(0, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT, "green");
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("apcoli"),  ArrowType.INHIBIT, imageMap.get("greenInhibitor"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandOne.add(new CurrentBall(0, imageMap.get("greenBall"), imageMap.get("wnt"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, "green"));
			strandThree.add(new CurrentBall(2, imageMap.get("blueBall"), imageMap.get("genetranscription"), null, null, false, null));
		}

		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;

		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("dnadamage"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("mcdk"), null, null, false, null));
		oddBalls.add(new CurrentBall(5, imageMap.get("greyBall"), imageMap.get("p53"), null, null, false, null));

	}
	
	
	
	
	//Provides the coordinates for the simplification animation.
	private void fillSimplificationArrays() {
		for (int i = 0; i < 4; i++) {
			ripAPos[i] = new Point(416 + 37*i, 188 - 37*i);
			ripBPos[i] = new Point(408 + 37*i, 206 - 37*i);
			overArrowAPos[i] = new Point(431 + 37*i, 243 - 37*i);
			overMolAPos[i] = new Point(436 + 37*i, 202 - 37*i);
			overArrowBPos[i] = new Point(424 + 37*i, 262 - 37*i);
			overMolBPos[i] = new Point(429 + 37*i, 224 - 37*i);
			eraserMarkPos[i] = new Point(408 + 37*i, 258 - 37*i);
		}
	}
	
	private void getImages() throws IOException {
		URL url = new URL(getCodeBase(), "CannonGameSpriteSheet.png");
		BufferedImage finalSprites = ImageIO.read(url);
		
		imageMap.put("blueArrow", finalSprites.getSubimage(23, 0, 16, 16));
		imageMap.put("blueInhibitor", finalSprites.getSubimage(1024, 0, 19, 18));
		imageMap.put("greenArrow", finalSprites.getSubimage(39, 0, 16, 16));
		imageMap.put("greenInhibitor", finalSprites.getSubimage(1043, 0, 19, 18));
		imageMap.put("redArrow", finalSprites.getSubimage(115, 0, 16, 16));
		imageMap.put("redInhibitor", finalSprites.getSubimage(1102, 0, 19, 19));
		imageMap.put("orangeArrow", finalSprites.getSubimage(99, 0, 16, 16));
		imageMap.put("orangeInhibitor", finalSprites.getSubimage(1083, 0, 19, 18));
		imageMap.put("highArrow", finalSprites.getSubimage(55, 0, 22, 23));
		imageMap.put("lowArrow", finalSprites.getSubimage(77, 0, 22, 23));
		imageMap.put("lowInhibitor", finalSprites.getSubimage(1062, 0, 21, 22));
		imageMap.put("blueBall", finalSprites.getSubimage(2486, 0, 48, 49));
		imageMap.put("greenBall", finalSprites.getSubimage(2534, 0, 48, 48));
		imageMap.put("orangeBall", finalSprites.getSubimage(2582, 0, 48, 47));
		imageMap.put("redBall", finalSprites.getSubimage(2630, 0, 47, 48));
		imageMap.put("cannonUR", finalSprites.getSubimage(268, 0, 84, 84));
		imageMap.put("cannonDR", finalSprites.getSubimage(352, 0, 84, 84));
		imageMap.put("cannonUL", finalSprites.getSubimage(436, 0, 84, 84));
		imageMap.put("cannonDL", finalSprites.getSubimage(520, 0, 84, 84));

		
		imageMap.put("apcoli", finalSprites.getSubimage(0, 0, 23, 42));
		imageMap.put("adaptorProteins", finalSprites.getSubimage(131, 0, 37, 31));
		imageMap.put("apoptosis", finalSprites.getSubimage(168, 0, 40, 20));
		imageMap.put("bax", finalSprites.getSubimage(208, 0, 27, 21));
		imageMap.put("betacatenin", finalSprites.getSubimage(235, 0, 33, 29));
		imageMap.put("caspase", finalSprites.getSubimage(604, 0, 39, 21));
		imageMap.put("cytc", finalSprites.getSubimage(720, 0, 28, 37));
		imageMap.put("dnadamage", finalSprites.getSubimage(748, 0, 31, 32));
		imageMap.put("dnareplication", finalSprites.getSubimage(779, 0, 39, 32));
		imageMap.put("delta", finalSprites.getSubimage(818, 0, 33, 18));
		imageMap.put("frizzled", finalSprites.getSubimage(898, 0, 35, 16));
		imageMap.put("genetranscription", finalSprites.getSubimage(933, 0, 39, 29));
		imageMap.put("gli", finalSprites.getSubimage(972, 0, 19, 25));
		imageMap.put("hedgehog", finalSprites.getSubimage(991, 0, 33, 37));
		imageMap.put("lrp", finalSprites.getSubimage(1121, 0, 31, 25));
		imageMap.put("mapkinaserelay", finalSprites.getSubimage(1152, 0, 34, 39));
		imageMap.put("nodal", finalSprites.getSubimage(1186, 0, 37, 19));
		imageMap.put("notch", finalSprites.getSubimage(1223, 0, 35, 19));
		imageMap.put("orc", finalSprites.getSubimage(1258, 0, 34, 21));
		imageMap.put("patched", finalSprites.getSubimage(1292, 0, 38, 16));
		imageMap.put("ras", finalSprites.getSubimage(1330, 0, 30, 21));
		imageMap.put("rasactprotein", finalSprites.getSubimage(1360, 0, 38, 39));
		imageMap.put("rtks", finalSprites.getSubimage(1398, 0, 35, 18));
		imageMap.put("rb", finalSprites.getSubimage(1433, 0, 20, 21));
		imageMap.put("smads", finalSprites.getSubimage(1493, 0, 38, 18));
		imageMap.put("scdkscyclin", finalSprites.getSubimage(1531, 0, 43, 30));
		imageMap.put("sckd", finalSprites.getSubimage(1574, 0, 36, 17));
		imageMap.put("smo", finalSprites.getSubimage(1610, 0, 32, 25));
		imageMap.put("tgfbeta", finalSprites.getSubimage(1642, 0, 37, 29));
		imageMap.put("wnt", finalSprites.getSubimage(1679, 0, 31, 21));
		imageMap.put("cdc6", finalSprites.getSubimage(2410, 0, 36, 18));
		imageMap.put("mitogen", finalSprites.getSubimage(2446, 0, 40, 22));
		imageMap.put("p21", finalSprites.getSubimage(2677, 0, 22, 30));
		imageMap.put("p53", finalSprites.getSubimage(2699, 0, 27, 30));
		imageMap.put("apc", finalSprites.getSubimage(3671, 0, 35, 24));
		imageMap.put("anaphase", finalSprites.getSubimage(3706, 0, 31, 29));
		imageMap.put("cohesin", finalSprites.getSubimage(3737, 0, 39, 15));
		imageMap.put("mcdk", finalSprites.getSubimage(3776, 0, 39, 17));
		imageMap.put("mcyclin", finalSprites.getSubimage(3815, 0, 30, 38));
		imageMap.put("separase", finalSprites.getSubimage(3845, 0, 34, 29));
		
		imageMap.put("centerMolecule", finalSprites.getSubimage(643, 0, 77, 79));	
		imageMap.put("shooterImg", finalSprites.getSubimage(851, 0, 47, 79));
		imageMap.put("progressBar", finalSprites.getSubimage(2902, 0, 69, 39));
		imageMap.put("background", finalSprites.getSubimage(1710, 0, 700, 600));
		imageMap.put("greyBorder", finalSprites.getSubimage(2971, 0, 700, 600));
		imageMap.put("pauseButton", finalSprites.getSubimage(2726, 0, 88, 42));
		imageMap.put("pauseButtonPressed", finalSprites.getSubimage(2814, 0, 88, 42));
		
		
		url = new URL(getCodeBase(), "simplificationGraphics_default.png");
		BufferedImage simplificationSprites = ImageIO.read(url);
		
		
		rips[0] = simplificationSprites.getSubimage(0, 0, 87, 86);
		rips[1] = simplificationSprites.getSubimage(87, 0, 87, 86);
		strips[0] = simplificationSprites.getSubimage(174, 0, 85, 86);
		strips[1] = simplificationSprites.getSubimage(259, 0, 119, 121);
		strips[2] = simplificationSprites.getSubimage(378, 0, 157, 159);
		strips[3] = simplificationSprites.getSubimage(535, 0, 197, 198);
		strips[4] = simplificationSprites.getSubimage(732, 0, 237, 238);
		imageMap.put("eraserMark", simplificationSprites.getSubimage(969, 0, 44, 42));
		
		
		url = new URL(getCodeBase(), "interfacesprites_default.png");
		BufferedImage interfaceSprites = ImageIO.read(url);
		
		imageMap.put("cell2cell", interfaceSprites.getSubimage(0, 0, 311, 221));
		imageMap.put("cell2cellpressed", interfaceSprites.getSubimage(311, 0, 311, 221));
		imageMap.put("cellcycle", interfaceSprites.getSubimage(622, 0, 310, 220));
		imageMap.put("cellcyclepressed", interfaceSprites.getSubimage(932, 0, 310, 220));
		imageMap.put("chooseown", interfaceSprites.getSubimage(1242, 0, 470, 80));
		imageMap.put("chooseownpressed", interfaceSprites.getSubimage(1712, 0, 470, 80));
		imageMap.put("menu", interfaceSprites.getSubimage(2182, 0, 150, 81));
		imageMap.put("menupressed", interfaceSprites.getSubimage(2332, 0, 150, 81));
		imageMap.put("missionBackground", interfaceSprites.getSubimage(2482, 0, 700, 600));
		imageMap.put("tutorial", interfaceSprites.getSubimage(3182, 0, 150, 80));
		imageMap.put("tutorialpressed", interfaceSprites.getSubimage(3332, 0, 150, 80));
		imageMap.put("greyBall", interfaceSprites.getSubimage(3482, 0, 48, 49));
		
		url = new URL(getCodeBase(), "moreInterface_default.png");
		BufferedImage iSprites = ImageIO.read(url);
		
		imageMap.put("APCButton", iSprites.getSubimage(0, 0, 210, 60));
		imageMap.put("backButton", iSprites.getSubimage(210, 0, 60, 60));
		imageMap.put("backPressed", iSprites.getSubimage(270, 0, 60, 60));
		imageMap.put("btpButton", iSprites.getSubimage(330, 0, 90, 50));
		imageMap.put("bluePause", iSprites.getSubimage(420, 0, 470, 290));
		imageMap.put("chooseBackground", iSprites.getSubimage(890, 0, 700, 600));
		imageMap.put("dnButton", iSprites.getSubimage(1590, 0, 210, 60));
		imageMap.put("hedgehogButton", iSprites.getSubimage(1800, 0, 210, 60));
		imageMap.put("helpButton", iSprites.getSubimage(2010, 0, 91, 50));
		imageMap.put("levelPressed", iSprites.getSubimage(2101, 0, 101, 100)); 
		imageMap.put("pathwayPressed", iSprites.getSubimage(2202, 0, 210, 60));
		imageMap.put("playButton", iSprites.getSubimage(2412, 0, 90, 50));
		imageMap.put("playPressed", iSprites.getSubimage(2502, 0, 90, 50));
		imageMap.put("redPressed", iSprites.getSubimage(2592, 0, 90, 50));
		imageMap.put("redPause", iSprites.getSubimage(2682, 0, 470, 290));
		imageMap.put("restartButton", iSprites.getSubimage(3152, 0, 91, 50));
		imageMap.put("scdkButton", iSprites.getSubimage(3243, 0, 210, 61));
		imageMap.put("smBackButton", iSprites.getSubimage(3453, 0, 60, 40));
		imageMap.put("smBackPressed", iSprites.getSubimage(3513, 0, 60, 40));
		imageMap.put("tgfButton", iSprites.getSubimage(3573, 0, 210, 61));
		imageMap.put("wntButton", iSprites.getSubimage(3783, 0, 210, 60));
		imageMap.put("lvl1", iSprites.getSubimage(3993, 0, 101, 100));
		imageMap.put("lvl2", iSprites.getSubimage(4094, 0, 101, 100));
		imageMap.put("lvl3", iSprites.getSubimage(4195, 0, 101, 100));
		imageMap.put("p53button", iSprites.getSubimage(4296, 0, 210, 61));
		
		url = new URL(getCodeBase(), "tutorialSprites_default.png");
		BufferedImage tutorialSprites = ImageIO.read(url);
		imageMap.put("gotitButton", tutorialSprites.getSubimage(0, 0, 90, 50));
		imageMap.put("missionAccomplished", tutorialSprites.getSubimage(90, 0, 470, 290));
		imageMap.put("pathwayComplete", tutorialSprites.getSubimage(560, 0, 354, 100));
		imageMap.put("pathwaySimplified", tutorialSprites.getSubimage(914, 0, 354, 100));
		imageMap.put("quitLevelButton", tutorialSprites.getSubimage(1268, 0, 90, 50));
		imageMap.put("tutorialPageOne", tutorialSprites.getSubimage(1449, 0, 470, 290));
		imageMap.put("tutorialPageTwo", tutorialSprites.getSubimage(1919, 0, 354, 202));
		imageMap.put("onwards", tutorialSprites.getSubimage(2273, 0, 90, 50));
		imageMap.put("replay", tutorialSprites.getSubimage(1358, 0, 91, 51));
		
		url = new URL(getCodeBase(), "mainMenuImages_default.png");
		BufferedImage mainMenuSprites = ImageIO.read(url);
		imageMap.put("aboutButton", mainMenuSprites.getSubimage(0, 0, 151, 76));
		imageMap.put("aboutPressed", mainMenuSprites.getSubimage(151, 0, 151, 76));
		imageMap.put("backMiisionsMenuButton", mainMenuSprites.getSubimage(302, 0, 91, 61));
		imageMap.put("bluePressed", mainMenuSprites.getSubimage(393, 0, 91, 61));
		imageMap.put("mainHelpButton", mainMenuSprites.getSubimage(484, 0, 150, 75));
		imageMap.put("mainHelpPressed", mainMenuSprites.getSubimage(634, 0, 151, 76));
		imageMap.put("mainBackground", mainMenuSprites.getSubimage(785, 0, 700, 600));
		imageMap.put("missionAccomplished", mainMenuSprites.getSubimage(1485, 0, 470, 290));
		imageMap.put("musicOff", mainMenuSprites.getSubimage(1955, 0, 75, 75));
		imageMap.put("musicOn", mainMenuSprites.getSubimage(2030, 0, 75, 75));
		imageMap.put("pickDifficultyBackground", mainMenuSprites.getSubimage(2105, 0, 470, 290));
		imageMap.put("replicationproteins", mainMenuSprites.getSubimage(2575, 0, 40, 28));
		imageMap.put("musicPressed", mainMenuSprites.getSubimage(2615, 0, 75, 75));
		imageMap.put("securins", mainMenuSprites.getSubimage(2690, 0, 31, 32));
		imageMap.put("soundEffectsOff", mainMenuSprites.getSubimage(2721, 0, 75, 75));
		imageMap.put("soundEffectsOn", mainMenuSprites.getSubimage(2796, 0, 75, 75));
		imageMap.put("startMissionButton", mainMenuSprites.getSubimage(2871, 0, 331, 161));
		imageMap.put("startMissionPressed", mainMenuSprites.getSubimage(3202, 0, 331, 161));
		imageMap.put("tutorialBig", mainMenuSprites.getSubimage(3533, 0, 470, 290));
	}
	
	private void createButtons() {
		Button startMissionBut = new Button(52, 143,  imageMap.get("startMissionButton"),  imageMap.get("startMissionPressed"), 0);
		Button mainHelpBut = new Button(52, 327,  imageMap.get("mainHelpButton"),  imageMap.get("mainHelpPressed"), 60);
		Button aboutBut = new Button(232, 327,  imageMap.get("aboutButton"),  imageMap.get("aboutPressed"), 70);
		Button musicBut = new Button(219, 459,  imageMap.get("musicOn"),  imageMap.get("musicOff"), 80);
		Button soundFXBut = new Button(307, 459,  imageMap.get("soundEffectsOn"),  imageMap.get("soundEffectsOff"), 90);
		mainMenuButtons.add(startMissionBut); mainMenuButtons.add(mainHelpBut); mainMenuButtons.add(aboutBut);
		mainMenuButtons.add(musicBut); mainMenuButtons.add(soundFXBut);
		
		Button lvlOneBut = new Button(165, 254,  imageMap.get("lvl1"),  imageMap.get("levelPressed"), -1);
		Button lvlTwoBut = new Button(300, 254,  imageMap.get("lvl2"),  imageMap.get("levelPressed"), -2);
		Button lvlThreeBut = new Button(436, 254,  imageMap.get("lvl3"),  imageMap.get("levelPressed"), -3);
		Button smBackBut = new Button(144, 378,  imageMap.get("smBackButton"),  imageMap.get("smBackPressed"), 0);
		levelButtons.add(lvlOneBut); levelButtons.add(lvlTwoBut);
		levelButtons.add(lvlThreeBut); levelButtons.add(smBackBut);
	
		Button apcBut = new Button(75, 245, imageMap.get("APCButton"), imageMap.get("pathwayPressed"), 1);
		Button p53But = new Button(75, 333,  imageMap.get("p53button"), imageMap.get("pathwayPressed"), 2);
		Button scdkBut  = new Button(75, 420,  imageMap.get("scdkButton"), imageMap.get("pathwayPressed"), 3);
		Button dnBut = new Button(415, 245,  imageMap.get("dnButton"), imageMap.get("pathwayPressed"), 4);
		Button tgfBut = new Button(415, 333,  imageMap.get("tgfButton"), imageMap.get("pathwayPressed"), 5);
		Button hedgehogBut = new Button(415, 420,  imageMap.get("hedgehogButton"), imageMap.get("pathwayPressed"), 6);
		Button wntBut = new Button(415, 508,  imageMap.get("wntButton"), imageMap.get("pathwayPressed"), 7);
		Button backBut = new Button(32, 508, imageMap.get("backButton"), imageMap.get("backPressed"), 0);
		chooseYourOwnAdventureButtons.add(apcBut); chooseYourOwnAdventureButtons.add(scdkBut);
		chooseYourOwnAdventureButtons.add(p53But); chooseYourOwnAdventureButtons.add(hedgehogBut);
		chooseYourOwnAdventureButtons.add(dnBut); chooseYourOwnAdventureButtons.add(wntBut);
		chooseYourOwnAdventureButtons.add(tgfBut); chooseYourOwnAdventureButtons.add(backBut);
		
		Button cellToCellBut = new Button(360, 237, imageMap.get("cell2cell"), imageMap.get("cell2cellpressed"), 4);
		Button cellCycleBut = new Button(30, 237, imageMap.get("cellcycle"), imageMap.get("cellcyclepressed"), 1);
		Button menuBut = new Button(30, 490, imageMap.get("menu"), imageMap.get("menupressed"), 100);
		Button tutorialBut = new Button(275, 132, imageMap.get("tutorial"), imageMap.get("tutorialpressed"), 10);
		Button chooseOwnBut = new Button(200, 490, imageMap.get("chooseown"), imageMap.get("chooseownpressed"), 11);
		pathwayMissionButtons.add(cellToCellBut); pathwayMissionButtons.add(cellCycleBut);
		pathwayMissionButtons.add(menuBut); pathwayMissionButtons.add(tutorialBut);
		pathwayMissionButtons.add(chooseOwnBut);
		
		
		Button backToPathwaysBut  = new Button(144, 372, imageMap.get("btpButton"),  imageMap.get("playPressed"), 0);
		Button helpBut = new Button(360, 372,  imageMap.get("helpButton"),  imageMap.get("playPressed"), 40);
		Button playBut  = new Button(467, 372,  imageMap.get("playButton"),  imageMap.get("playPressed"), 30);
		Button restartBut = new Button(252, 372,  imageMap.get("restartButton"),  imageMap.get("playPressed"), 20);
		pauseScreenButtons.add(backToPathwaysBut); pauseScreenButtons.add(restartBut);
		pauseScreenButtons.add(helpBut); pauseScreenButtons.add(playBut);
		
		Button backtoMenuBut = new Button(144, 372, imageMap.get("smBackButton"), null, 0);
		Button replayBut = new Button(310, 372,  imageMap.get("replay"), null, 20);
		Button onwardsBut = new Button(467, 372,  imageMap.get("onwards"), null, 25);
		missionButtons.add(replayBut); missionButtons.add(onwardsBut);
		missionButtons.add(backtoMenuBut);
		
		Button gotItBut = new Button(467, 372, imageMap.get("gotitButton"), null, 1);
		Button quitLevelBut = new Button(144, 372, imageMap.get("quitLevelButton"), null, 0);
		tutorialButtons.add(gotItBut); tutorialButtons.add(quitLevelBut);
		
		Button gotItBut2 = new Button(467, 372, imageMap.get("gotitButton"), null, 30);
		Button quitLevelBut2 = new Button(144, 372, imageMap.get("quitLevelButton"), null, 0);
		helpButtons.add(gotItBut2); helpButtons.add(quitLevelBut2);	
	}
	
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}