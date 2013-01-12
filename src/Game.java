import java.applet.*;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Game extends Applet implements KeyListener, MouseListener, Runnable {
	
	private static final long serialVersionUID = 1;	
	private static final int width = 700, height = 600;
	public long currentTime;
	private Thread mainLoop;
	private boolean running = true;
	private int countdown = 50;
	
	public Graphics bufferGraphics;
	
	public Image offscreen;	
	
	private BufferedImage finalSprites;
	private BufferedImage[] cannons = new BufferedImage[4];
	private BufferedImage[] balls = new BufferedImage[5];
	
	
	private BufferedImage centerMolecule;
	private BufferedImage shooterImg, progressBar, background, greyBorder, pauseButton, pauseButtonPressed;
	private BufferedImage blueArrow, blueInhibitor, greenArrow, 
						  greenInhibitor, orangeArrow, orangeInhibitor, 
						  redArrow, redInhibitor, highArrow, lowArrow, 
						  lowInhibitor;
	private BufferedImage cell2cell, cell2cellpressed, cellcycle, cellcyclepressed, 
	  					  chooseown, chooseownpressed, menu, menupressed, missionBackground,
	  					  tutorial, tutorialpressed;
	private BufferedImage apcoli, adaptorProteins, apoptosis, bax, 
						  betacatenin, caspase, cytc, dnadamage, 
						  dnareplication, delta, frizzled, 
						  genetranscription, gli, hedgehog, lrp, 
						  mapkinaserelay, nodal, notch, orc, patched, 
						  ras, rasactprotein, rtks, rb, replicationproteins, 
						  smads, scdkscyclin, sckd, smo, tgfbeta, wnt, cdc6, 
						  mitogen, p21, p53, apc, anaphase, 
						  cohesin, mcdk, mcyclin, separase, securins; 
	
	private StartingArrow strandOne, strandTwo, strandThree, strandFour;	
	private Shooter shooter;
	private CurrentBall currentBall;
	private Font hosFont;
	
	private Random generator = new Random();
	private int currentLevel = 0;
	private int difficulty = 1;
	private int cannon = 0, setCannon = 0, rotation = 0, strand = 0;
	private int score = 0, progress = 0;
	private int arr1 = 0, arr2 = 0, arr3 = 0, arr4 = 0;
	private int numberOfMoleculesOne = 0, numberOfMoleculesTwo = 0, numberOfMoleculesThree = 0, numberOfMoleculesFour = 0;
	
	private BufferedImage centerTitle = apc;
	private ArrayList<CurrentBall> listOne = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listTwo = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listThree = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listFour = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listFive = new ArrayList<CurrentBall>();
	
	private ArrayList<CurrentBall> inPlayOne = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayTwo = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayThree = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayFour = new ArrayList<CurrentBall>();

	private boolean ballExistence = false;
	private boolean eraserFired = false;
	private boolean simplifying = false;
	private boolean pause = false;
	private boolean playing = false;
	private boolean gameOver = false;

	private Font myfont;
	
	public void init() {
		
		setSize(width, height);
		offscreen = createImage(width, height);
		bufferGraphics = offscreen.getGraphics();
		setBackground(Color.WHITE);
		try {
			hosFont = Font.createFont(Font.TRUETYPE_FONT, new File("handsean.ttf"));
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			getImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createButtons();
		shooter = new Shooter();
		simplifying = false;
		rotate();
		addKeyListener(this);	
		addMouseListener(this);
		mainLoop = new Thread(this);
		mainLoop.start();
		
	}
	
	private void setUpLists(int level) {
		arr1 = 0; arr2 = 0; arr3 = 0; arr4 = 0;
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		score = 0;
		progress = 0;
		listFive.add(new CurrentBall(5, balls[4], apcoli, null, null, false, false));
		
		switch(level) {
		case 1: setUpLevelOne(); break;
		case 2: setUpLevelTwo(); break;
		case 3: setUpLevelThree(); break;
		case 4: setUpLevelFour(); break;
		case 5: setUpLevelFive(); break;
		case 6: setUpLevelSix(); break;
		case 7: setUpLevelSeven(); break;
		}
	}
	
	private void setUpLevelOne() {
		numberOfMoleculesOne = 1;
		numberOfMoleculesTwo = 1;
		numberOfMoleculesThree = 4;
		numberOfMoleculesFour = 0;
						
		centerTitle = apc;
		
		strandOne = new StartingArrow(0, true, blueArrow, ArrowType.ACTIVATE);
		strandTwo = new StartingArrow(1, false, greenInhibitor, ArrowType.INHIBIT);
		strandThree = new StartingArrow(2, false, redInhibitor, ArrowType.INHIBIT);
		strandFour = null;

		
		listOne.add(new CurrentBall(0, balls[0], mcdk, null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[1], mcyclin, null, null, false, false));
		
		listThree.add(new CurrentBall(2, balls[3], securins, ArrowType.INHIBIT, redInhibitor, false, true));
		listThree.add(new CurrentBall(2, balls[3], separase, ArrowType.INHIBIT, redInhibitor, false, false));
		listThree.add(new CurrentBall(2, balls[3], cohesin, ArrowType.INHIBIT, redInhibitor, false, true));
		listThree.add(new CurrentBall(2, balls[3], anaphase, null, null, false, true));

	}
	
	private void setUpLevelTwo() {
		numberOfMoleculesOne = 5;
		numberOfMoleculesTwo = 3;
		numberOfMoleculesThree = 1;
		numberOfMoleculesFour = 0;
		
		strandOne = new StartingArrow(0, false, redArrow, ArrowType.ACTIVATE);
		strandTwo = new StartingArrow(1, false, blueArrow, ArrowType.ACTIVATE);
		strandThree = new StartingArrow(2, true, orangeArrow, ArrowType.ACTIVATE);;
		strandFour = null;
		
		score = 0;
		progress = 0;
		centerTitle = p53;
		
		arr1 = 0; arr2 = 0; arr3 = 0; arr4 = 0;
		
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear(); 
	
		listOne.add(new CurrentBall(0, balls[3], bax, ArrowType.ACTIVATE, redArrow, false, true));
		listOne.add(new CurrentBall(0, balls[3], cytc, ArrowType.ACTIVATE, redArrow, false, true));
		listOne.add(new CurrentBall(0, balls[3], adaptorProteins, ArrowType.ACTIVATE, redArrow, false, true));
		listOne.add(new CurrentBall(0, balls[3], caspase, ArrowType.ACTIVATE, redArrow, false, false));
		listOne.add(new CurrentBall(0, balls[3], apoptosis, null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[0], p21, ArrowType.INHIBIT, blueInhibitor, false, true));
		listTwo.add(new CurrentBall(1, balls[0], scdkscyclin, ArrowType.ACTIVATE, blueArrow, false, true));
		listTwo.add(new CurrentBall(1, balls[0], dnareplication, null, null, false, true));

		listThree.add(new CurrentBall(2, balls[2], dnadamage, null, null, false, false));
	}
	
	private void setUpLevelThree() {
		numberOfMoleculesOne = 5;
		numberOfMoleculesTwo = 3;
		numberOfMoleculesThree = 3;
		numberOfMoleculesFour = 3;
		
		strandOne = new StartingArrow(0, true, greenArrow, ArrowType.ACTIVATE);
		strandTwo = new StartingArrow(1, true, redArrow, ArrowType.INHIBIT);
		strandThree = new StartingArrow(2, false, orangeInhibitor, ArrowType.INHIBIT);
		strandFour = new StartingArrow(3, false, blueInhibitor, ArrowType.INHIBIT);
		
		score = 0;
		progress = 0;
		centerTitle = sckd;
		
		arr1 = 0; arr2 = 0; arr3 = 0; arr4 = 0;

		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		
		listOne.add(new CurrentBall(0, balls[1], mapkinaserelay,  ArrowType.ACTIVATE, greenArrow, true, true));
		listOne.add(new CurrentBall(0, balls[1], ras, ArrowType.ACTIVATE, greenArrow, true, true));
		listOne.add(new CurrentBall(0, balls[1], rasactprotein, ArrowType.ACTIVATE, greenArrow, true, true));
		listOne.add(new CurrentBall(0, balls[1], rtks, ArrowType.ACTIVATE, greenArrow, true, false));
		listOne.add(new CurrentBall(0, balls[1], mitogen, null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[3], p21,  ArrowType.ACTIVATE, redArrow, true, true));
		listTwo.add(new CurrentBall(1, balls[3], p53,  ArrowType.ACTIVATE, redArrow, true, false));
		listTwo.add(new CurrentBall(1, balls[3], dnadamage,  null, null, false, false));

		listThree.add(new CurrentBall(2, balls[2], rb, ArrowType.INHIBIT, orangeInhibitor, false, true));
		listThree.add(new CurrentBall(2, balls[2], replicationproteins, ArrowType.ACTIVATE, orangeArrow, false, false));
		listThree.add(new CurrentBall(2, balls[2], dnareplication, null, null, false, false));

		listFour.add(new CurrentBall(3, balls[0], cdc6, ArrowType.INHIBIT, blueInhibitor, false, true));
		listFour.add(new CurrentBall(3, balls[0], orc, ArrowType.ACTIVATE, blueArrow, false, false));	
		listFour.add(new CurrentBall(3, balls[0], dnareplication, null, null, false, false));	

	}
	
	private void setUpLevelFour() {
		
		numberOfMoleculesOne = 1;
		numberOfMoleculesTwo = 0;
		numberOfMoleculesThree = 1;
		numberOfMoleculesFour = 0;
		centerTitle = notch;
		strandOne = new StartingArrow(0, false, greenArrow, ArrowType.ACTIVATE);
		strandTwo = null;
		strandThree = new StartingArrow(2, true, orangeArrow, ArrowType.ACTIVATE);
		strandFour = null;	
		listOne.add(new CurrentBall(0, balls[1], genetranscription,  null, null, false, true));
		listThree.add(new CurrentBall(2, balls[2], delta, null, null, false, false));
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelFive() {
		int placeHolder = generator.nextInt(2);
		if (placeHolder == 0){
			numberOfMoleculesOne = 2;
			numberOfMoleculesThree = 1;
			centerTitle = tgfbeta;
			strandOne = new StartingArrow(0, false, orangeArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, redArrow, ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[3], nodal,  null, null, true, false));
			listOne.add(new CurrentBall(0, balls[2], smads, ArrowType.ACTIVATE, redArrow, false, true));
			listOne.add(new CurrentBall(0, balls[2], genetranscription, null, null, false, false));
		} else if (placeHolder == 1) {
			numberOfMoleculesOne = 1;
			numberOfMoleculesThree = 2;
			centerTitle = smads;
			strandOne = new StartingArrow(0, false, orangeArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, redArrow, ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[3], tgfbeta, ArrowType.ACTIVATE, redArrow, true, false));
			listThree.add(new CurrentBall(2, balls[3], nodal,  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[2], genetranscription, ArrowType.ACTIVATE, orangeArrow, false, false));
		}
		
		numberOfMoleculesTwo = 0;
		numberOfMoleculesFour = 0;
		strandTwo = null;
		strandFour = null;
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelSix() {
		int placeHolder = generator.nextInt(3);
		if (placeHolder == 0){
			numberOfMoleculesOne = 3;
			numberOfMoleculesThree = 1;
			centerTitle = patched;
			strandOne = new StartingArrow(0, false, redInhibitor, ArrowType.INHIBIT);
			strandThree = new StartingArrow(2, true, blueInhibitor, ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[0], hedgehog,  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[3], smo, ArrowType.ACTIVATE, redArrow, false, false));
			listOne.add(new CurrentBall(0, balls[3], gli, ArrowType.ACTIVATE, redArrow, false, false));
			listOne.add(new CurrentBall(0, balls[3], genetranscription, ArrowType.ACTIVATE, redArrow, false, false));
		} else if (placeHolder == 1) {
			numberOfMoleculesOne = 2;
			numberOfMoleculesThree = 2;
			centerTitle = smo;
			strandOne = new StartingArrow(0, false, redArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, blueInhibitor, ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[0], patched,  ArrowType.INHIBIT, blueInhibitor, true, true));
			listThree.add(new CurrentBall(2, balls[0], hedgehog, null, null, false, false));
			listOne.add(new CurrentBall(0, balls[3], gli, ArrowType.ACTIVATE, redArrow, false, false));
			listOne.add(new CurrentBall(0, balls[3], genetranscription, ArrowType.ACTIVATE, redArrow, false, false));
		} else if (placeHolder == 2) {
			numberOfMoleculesOne = 1;
			numberOfMoleculesThree = 3;
			centerTitle = gli;
			strandOne = new StartingArrow(0, false, redArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, blueArrow, ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[0], smo,  ArrowType.INHIBIT, blueInhibitor, true, true));
			listThree.add(new CurrentBall(2, balls[0], patched, ArrowType.INHIBIT, blueInhibitor, true, false));
			listThree.add(new CurrentBall(2, balls[0], hedgehog, null, null, true, false));
			listOne.add(new CurrentBall(0, balls[3], genetranscription, ArrowType.ACTIVATE, redArrow, false, false));
		}
		
		numberOfMoleculesTwo = 0;
		numberOfMoleculesFour = 0;
		strandTwo = null;
		strandFour = null;
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelSeven() {
		int placeHolder = generator.nextInt(4);
		if (placeHolder == 0){
			numberOfMoleculesOne = 4;
			numberOfMoleculesThree = 1;
			centerTitle = frizzled;
			strandOne = new StartingArrow(0, false, blueArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, greenArrow, ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[1], wnt,  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[0], lrp, ArrowType.INHIBIT, blueInhibitor, false, false));
			listOne.add(new CurrentBall(0, balls[0], apc, ArrowType.INHIBIT, blueInhibitor, false, false));
			listOne.add(new CurrentBall(0, balls[0], betacatenin, ArrowType.ACTIVATE, blueArrow, false, false));
			listOne.add(new CurrentBall(0, balls[0], genetranscription, null, null, false, false));
		} else if (placeHolder == 1) {
			numberOfMoleculesOne = 3;
			numberOfMoleculesThree = 2;
			centerTitle = lrp;
			strandOne = new StartingArrow(0, false, blueInhibitor, ArrowType.INHIBIT);
			strandThree = new StartingArrow(2, true, greenArrow, ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[1], frizzled,  ArrowType.ACTIVATE, greenArrow, true, true));
			listThree.add(new CurrentBall(2, balls[1], wnt, null, null, true, false));
			listOne.add(new CurrentBall(0, balls[0], apc, ArrowType.INHIBIT, blueInhibitor, false, false));
			listOne.add(new CurrentBall(0, balls[0], betacatenin, ArrowType.ACTIVATE, blueArrow, false, false));
			listOne.add(new CurrentBall(0, balls[0], genetranscription, null, null, false, false));
		} else if (placeHolder == 2) {
			numberOfMoleculesOne = 2;
			numberOfMoleculesThree = 3;
			centerTitle = apc;
			strandOne = new StartingArrow(0, false, blueInhibitor, ArrowType.INHIBIT);
			strandThree = new StartingArrow(2, true, greenInhibitor, ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], lrp,  ArrowType.ACTIVATE, greenArrow, true, true));
			listThree.add(new CurrentBall(2, balls[1], frizzled, ArrowType.ACTIVATE, greenArrow, true, false));
			listThree.add(new CurrentBall(2, balls[1], wnt, null, null, true, false));
			listOne.add(new CurrentBall(0, balls[0], betacatenin, ArrowType.ACTIVATE, blueArrow, false, false));
			listOne.add(new CurrentBall(0, balls[0], genetranscription, null, null, false, false));
		} else if (placeHolder == 3) {
			numberOfMoleculesOne = 1;
			numberOfMoleculesThree = 4;
			centerTitle = betacatenin;
			strandOne = new StartingArrow(0, false, blueArrow, ArrowType.ACTIVATE);
			strandThree = new StartingArrow(2, true, greenInhibitor, ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], apc,  ArrowType.INHIBIT, greenInhibitor, true, true));
			listThree.add(new CurrentBall(2, balls[1], lrp,  ArrowType.ACTIVATE, greenArrow, true, true));
			listThree.add(new CurrentBall(2, balls[1], frizzled,  ArrowType.ACTIVATE, greenArrow, true, true));
			listThree.add(new CurrentBall(2, balls[1], wnt,  ArrowType.ACTIVATE, greenArrow, true, true));
			listOne.add(new CurrentBall(2, balls[0], genetranscription, null, null, false, true));
		}
		
		numberOfMoleculesTwo = 0;
		numberOfMoleculesFour = 0;
		strandTwo = null;
		strandFour = null;
		
		score = 0;
		progress = 0;
	}
		
	private void getImages() throws IOException {
		URL url = new URL(getCodeBase(), "CannonGameSpriteSheet.png");
		finalSprites = ImageIO.read(url);
		
		blueArrow = finalSprites.getSubimage(23, 0, 16, 16);
		blueInhibitor = finalSprites.getSubimage(1024, 0, 19, 18);
		greenArrow = finalSprites.getSubimage(39, 0, 16, 16);
		greenInhibitor = finalSprites.getSubimage(1043, 0, 19, 18);
		redArrow = finalSprites.getSubimage(115, 0, 16, 16);
		redInhibitor = finalSprites.getSubimage(1102, 0, 19, 19);
		orangeArrow = finalSprites.getSubimage(99, 0, 16, 16);
		orangeInhibitor = finalSprites.getSubimage(1083, 0, 19, 18);
		highArrow = finalSprites.getSubimage(55, 0, 22, 23);
		lowArrow = finalSprites.getSubimage(77, 0, 22, 23);
		lowInhibitor = finalSprites.getSubimage(1062, 0, 21, 22);
		
		for (int i = 0; i < 3; i++) {
			balls[i] = finalSprites.getSubimage(2486 + 48*i, 0, 48, 48);
		}
		balls[3] = finalSprites.getSubimage(2630, 0, 47, 48);
		
		for (int i = 0; i < 4; i++) {
			cannons[i] = finalSprites.getSubimage(268 + 84*i, 0, 84, 84);
		}
		
		apcoli = finalSprites.getSubimage(0, 0, 23, 42);
		adaptorProteins = finalSprites.getSubimage(131, 0, 37, 31);
		apoptosis = finalSprites.getSubimage(168, 0, 40, 20);
		bax = finalSprites.getSubimage(208, 0, 27, 21);
		betacatenin = finalSprites.getSubimage(235, 0, 33, 29);
		caspase = finalSprites.getSubimage(604, 0, 39, 21);
		cytc = finalSprites.getSubimage(720, 0, 28, 37);
		dnadamage = finalSprites.getSubimage(748, 0, 31, 32);
		dnareplication = finalSprites.getSubimage(779, 0, 39, 32);
		delta = finalSprites.getSubimage(818, 0, 33, 18);
		frizzled = finalSprites.getSubimage(898, 0, 35, 16);
		genetranscription = finalSprites.getSubimage(933, 0, 39, 29);
		gli = finalSprites.getSubimage(972, 0, 19, 25);
		hedgehog = finalSprites.getSubimage(991, 0, 33, 37);
		lrp = finalSprites.getSubimage(1121, 0, 31, 25);
		mapkinaserelay = finalSprites.getSubimage(1152, 0, 34, 39);
		nodal = finalSprites.getSubimage(1186, 0, 37, 19);
		notch = finalSprites.getSubimage(1223, 0, 35, 19);
		orc = finalSprites.getSubimage(1258, 0, 34, 21);
		patched = finalSprites.getSubimage(1292, 0, 38, 16);
		ras = finalSprites.getSubimage(1330, 0, 30, 21);
		rasactprotein = finalSprites.getSubimage(1360, 0, 38, 39);
		rtks = finalSprites.getSubimage(1398, 0, 35, 18);
		rb = finalSprites.getSubimage(1433, 0, 20, 21);
		replicationproteins = finalSprites.getSubimage(1453, 0, 40, 28);
		smads = finalSprites.getSubimage(1493, 0, 38, 18);
		scdkscyclin = finalSprites.getSubimage(1531, 0, 43, 30);
		sckd = finalSprites.getSubimage(1574, 0, 36, 17);
		smo = finalSprites.getSubimage(1610, 0, 32, 25);
		tgfbeta = finalSprites.getSubimage(1642, 0, 37, 29);
		wnt = finalSprites.getSubimage(1679, 0, 31, 21);
		cdc6 = finalSprites.getSubimage(2410, 0, 36, 18);
		mitogen = finalSprites.getSubimage(2446, 0, 40, 22);
		p21 = finalSprites.getSubimage(2677, 0, 22, 30);
		p53 = finalSprites.getSubimage(2699, 0, 27, 30);
		apc = finalSprites.getSubimage(3671, 0, 35, 24);
		anaphase = finalSprites.getSubimage(3706, 0, 31, 29);
		cohesin = finalSprites.getSubimage(3737, 0, 39, 15);
		mcdk = finalSprites.getSubimage(3776, 0, 39, 17);
		mcyclin = finalSprites.getSubimage(3815, 0, 30, 38);
		separase = finalSprites.getSubimage(3845, 0, 34, 29);
		securins = finalSprites.getSubimage(3879, 0, 38, 16);
		
		centerMolecule = finalSprites.getSubimage(643, 0, 77, 79);	
		shooterImg = finalSprites.getSubimage(851, 0, 47, 79);
		progressBar = finalSprites.getSubimage(2902, 0, 69, 39);
		background = finalSprites.getSubimage(1710, 0, 700, 600);
		greyBorder = finalSprites.getSubimage(2971, 0, 700, 600);
		pauseButton = finalSprites.getSubimage(2726, 0, 88, 42);
		pauseButtonPressed = finalSprites.getSubimage(2814, 0, 88, 42);
		
		
		url = new URL(getCodeBase(), "simplificationImage_default.png");
		BufferedImage simplificationSprites = ImageIO.read(url);
		
		for (int i = 0; i < 4; i++) {
			rips[i][0] = simplificationSprites.getSubimage(0 + 969*i, 0, 87, 86);
			rips[i][1] = simplificationSprites.getSubimage(87 + 969*i, 0, 87, 86);
			strips[i][0] = simplificationSprites.getSubimage(174 + 969*i, 0, 85, 86);
			strips[i][1] = simplificationSprites.getSubimage(259 + 969*i, 0, 119, 121);
			strips[i][2] = simplificationSprites.getSubimage(378 + 969*i, 0, 157, 159);
			strips[i][3] = simplificationSprites.getSubimage(535 + 969*i, 0, 197, 198);
			strips[i][4] = simplificationSprites.getSubimage(732 + 969*i, 0, 237, 238);
		}
		
		url = new URL(getCodeBase(), "interfacesprites_default.png");
		BufferedImage interfaceSprites = ImageIO.read(url);
		
		cell2cell = interfaceSprites.getSubimage(0, 0, 311, 221);
		cell2cellpressed = interfaceSprites.getSubimage(311, 0, 311, 221);
		cellcycle = interfaceSprites.getSubimage(622, 0, 310, 220);
		cellcyclepressed = interfaceSprites.getSubimage(932, 0, 310, 220);
		chooseown = interfaceSprites.getSubimage(1242, 0, 470, 80);
		chooseownpressed = interfaceSprites.getSubimage(1712, 0, 470, 80);
		menu = interfaceSprites.getSubimage(2182, 0, 150, 81);
		menupressed = interfaceSprites.getSubimage(2332, 0, 150, 81);
		missionBackground = interfaceSprites.getSubimage(2482, 0, 700, 600);
		tutorial = interfaceSprites.getSubimage(3182, 0, 150, 80);
		tutorialpressed = interfaceSprites.getSubimage(3332, 0, 150, 80);
		balls[4] = interfaceSprites.getSubimage(3482, 0, 48, 49);
		
		url = new URL(getCodeBase(), "moreInterface_default.png");
		BufferedImage iSprites = ImageIO.read(url);
		
		APCButton = iSprites.getSubimage(0, 0, 210, 60);
		backButton = iSprites.getSubimage(210, 0, 60, 60);
		backPressed = iSprites.getSubimage(270, 0, 60, 60);
		btpButton = iSprites.getSubimage(330, 0, 90, 50);
		bluePause = iSprites.getSubimage(420, 0, 470, 290);
		chooseBackground = iSprites.getSubimage(890, 0, 700, 600);
		dnButton = iSprites.getSubimage(1590, 0, 210, 60);
		hedgehogButton = iSprites.getSubimage(1800, 0, 210, 60);
		helpButton = iSprites.getSubimage(2010, 0, 91, 50);
		levelPressed = iSprites.getSubimage(2101, 0, 101, 100); 
		pathwayPressed = iSprites.getSubimage(2202, 0, 210, 60);
		playButton = iSprites.getSubimage(2412, 0, 90, 50);
		playPressed = iSprites.getSubimage(2502, 0, 90, 50);
		redPressed = iSprites.getSubimage(2592, 0, 90, 50);
		redPause = iSprites.getSubimage(2682, 0, 470, 290);
		restartButton  = iSprites.getSubimage(3152, 0, 91, 50);
		scdkButton = iSprites.getSubimage(3243, 0, 210, 61);
		smBackButton = iSprites.getSubimage(3453, 0, 60, 40);
		smBackPressed = iSprites.getSubimage(3513, 0, 60, 40);
		tgfButton = iSprites.getSubimage(3573, 0, 210, 61);
		wntButton = iSprites.getSubimage(3783, 0, 210, 60);
		lvl1 = iSprites.getSubimage(3993, 0, 101, 100);
		lvl2 = iSprites.getSubimage(4094, 0, 101, 100);
		lvl3 = iSprites.getSubimage(4195, 0, 101, 100);
		p53button = iSprites.getSubimage(4296, 0, 210, 61);
		
		url = new URL(getCodeBase(), "tutorialSprites_default.png");
		BufferedImage tutorialSprites = ImageIO.read(url);
		
		missionAccomplished = tutorialSprites.getSubimage(90, 0, 470, 290);
		onwards = tutorialSprites.getSubimage(2273, 0, 90, 50);
		replay = tutorialSprites.getSubimage(1358, 0, 91, 51);
	}
	
	private BufferedImage missionAccomplished, onwards, replay;
	
	private ArrayList<Button> chooseYourOwnAdventureButtons = new ArrayList<Button>();
	private ArrayList<Button> pathwayMissionButtons = new ArrayList<Button>();
	private ArrayList<Button> pauseScreenButtons = new ArrayList<Button>();
	private ArrayList<Button> levelButtons = new ArrayList<Button>();
	private ArrayList<Button> missionButtons = new ArrayList<Button>();

	private void createButtons() {
		Button lvlOneBut = new Button(165, 254, lvl1, levelPressed, -1);
		Button lvlTwoBut = new Button(300, 254, lvl2, levelPressed, -2);
		Button lvlThreeBut = new Button(436, 254, lvl3, levelPressed, -3);
		Button smBackBut = new Button(144, 378, smBackButton, smBackPressed, 15);
		levelButtons.add(lvlOneBut); levelButtons.add(lvlTwoBut);
		levelButtons.add(lvlThreeBut); levelButtons.add(smBackBut);

		
		Button apcBut = new Button(75, 420, APCButton, pathwayPressed, 1);
		Button scdkBut = new Button(75, 333, scdkButton, pathwayPressed, 2);
		Button p53But  = new Button(75, 245, p53button, pathwayPressed, 3);
		Button dnBut = new Button(415, 245, dnButton, pathwayPressed, 4);
		Button tgfBut = new Button(415, 333, tgfButton, pathwayPressed, 5);
		Button hedgehogBut = new Button(415, 420, hedgehogButton, pathwayPressed, 6);
		Button wntBut = new Button(415, 508, wntButton, pathwayPressed, 7);
		Button backBut = new Button(32, 508, backButton, backPressed, 0);
		chooseYourOwnAdventureButtons.add(apcBut); chooseYourOwnAdventureButtons.add(scdkBut);
		chooseYourOwnAdventureButtons.add(p53But); chooseYourOwnAdventureButtons.add(hedgehogBut);
		chooseYourOwnAdventureButtons.add(dnBut); chooseYourOwnAdventureButtons.add(wntBut);
		chooseYourOwnAdventureButtons.add(tgfBut); chooseYourOwnAdventureButtons.add(backBut);
		
		Button cellToCellBut = new Button(360, 237, cell2cell, cell2cellpressed, 4);
		Button cellCycleBut = new Button(30, 237, cellcycle, cellcyclepressed, 1);
		Button menuBut = new Button(30, 490, menu, menupressed, 0);
		Button tutorialBut = new Button(275, 132, tutorial, tutorialpressed, 1);
		Button chooseOwnBut = new Button(200, 490, chooseown, chooseownpressed, 11);
		pathwayMissionButtons.add(cellToCellBut); pathwayMissionButtons.add(cellCycleBut);
		pathwayMissionButtons.add(menuBut); pathwayMissionButtons.add(tutorialBut);
		pathwayMissionButtons.add(chooseOwnBut);
		
		
		Button backToPathwaysBut  = new Button(144, 372, btpButton, playPressed, 0);
		Button helpBut = new Button(252, 372, helpButton, playPressed, 35);
		Button playBut  = new Button(360, 372, playButton, playPressed, 30);
		Button restartBut = new Button(467, 372, restartButton, playPressed, 20);
		pauseScreenButtons.add(backToPathwaysBut); pauseScreenButtons.add(restartBut);
		pauseScreenButtons.add(helpBut); pauseScreenButtons.add(playBut);
		
		Button backtoMenuBut = new Button(144, 372, backButton, null, 0);
		Button replayBut = new Button(310, 372, replay, null, 20);
		Button onwardsBut = new Button(467, 372, onwards, null, 25);
		missionButtons.add(replayBut); missionButtons.add(onwardsBut);
		missionButtons.add(backtoMenuBut);
		
		
	}
	
	
	
	private BufferedImage APCButton, backButton, backPressed,
						  btpButton, bluePause, chooseBackground, 
						  dnButton, hedgehogButton, helpButton, levelPressed,
						  pathwayPressed, playButton, playPressed,
						  redPressed, redPause, restartButton, 
						  scdkButton, smBackButton, smBackPressed,
						  tgfButton, wntButton, lvl1, lvl2, lvl3, p53button;
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void main() {
		
	}
	
	public void destroy() {
		running = false;
		mainLoop = null;
		ballExistence = false;
	}
	
	@Override
	public void run() {
		while(running && !gameOver) {
			if (!pause && playing && currentLevel > 0 && !simplifying) {
				moveEraser();
				moveCurrentBall();
				setCountdown();
				checkCollisions();
				repaint();
				try {
					Thread.sleep(45);
				} catch (InterruptedException e) {}
			}
			if (simplifying) {
				checkSimplificationProgress();	
			}
			if (!playing) {
				repaint();
			}
			
		}
	}
	
	public void setCountdown() {
		if (!ballExistence) countdown--;
		if (countdown == 0) {
			fireBall();
			countdown = 50;
		}
	}
	
	public void moveEraser() {
		if (eraserFired) {
			shooter.moveUp();
		}
		if (shooter.getPos().y < -70) {
			eraserFired = false;
			shooter.setyPos();
		}
	}
	
	public void moveCurrentBall() {
		if (ballExistence) {
			currentBall.move();
			shooter.setxPos(currentBall.getX());
		} 
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

	
	@Override public void paint(final Graphics g) {
		activateAntiAliasing(bufferGraphics);
		bufferGraphics.setFont(hosFont);
		drawBackground();
		if (!simplifying) drawCannons();
		drawCentralMolecule();
		drawMoleculesInPlay();
		if (!simplifying) drawEraser();
		if (!simplifying) drawProgressBar();
		drawCurrentBall();
		
		if (!playing) drawMenu();
		if (pause) drawPauseScreen();
		if (pickDifficulty && !playing) drawDifficultyScreen();
		if (playing) bufferGraphics.drawImage(greyBorder, 0, 0, this);
	
		g.drawImage(offscreen, 0, 0, this);
	}
	
	private void drawDifficultyScreen() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawRect(0, 0, width, height);
		bufferGraphics.fillRect(0, 0, width, height);
		bufferGraphics.drawImage(redPause, 115, 155, this);
		hosFont = hosFont.deriveFont(34.0f);
		bufferGraphics.setFont(hosFont);
		bufferGraphics.setColor(new Color(51, 51, 51));
		bufferGraphics.drawString("Pick Difficulty", 144, 216);
		
		for (int i = 0; i < levelButtons.size(); i++) {
			Button b = levelButtons.get(i);
			if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
			else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
		}
		
	}
	
	private void drawPauseScreen() {
		bufferGraphics.drawImage(redPause, 115, 155, this);
		hosFont = hosFont.deriveFont(34.0f);
		bufferGraphics.setFont(hosFont);
		bufferGraphics.setColor(new Color(51, 51, 51));
		bufferGraphics.drawString(getTitleString(), 144, 216);
		
		for (int i = 0; i < pauseScreenButtons.size(); i++) {
			Button b = pauseScreenButtons.get(i);
			if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
			else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
		}
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
		bufferGraphics.drawImage(background, 0, 0, this);
		if (pause) {
			bufferGraphics.drawImage(pauseButtonPressed, 14, 548, this);
		} else { 
			bufferGraphics.drawImage(pauseButton, 14, 548, this);
		}
		hosFont = hosFont.deriveFont(34.0f);
		bufferGraphics.setFont(hosFont);
		FontMetrics fontMetrics = bufferGraphics.getFontMetrics(hosFont);
		bufferGraphics.setColor(Color.RED);
		bufferGraphics.drawString("" + score, 95 - fontMetrics.stringWidth("" + score), 94);
	}
	
	private void drawEraser() {	
		Point p = shooter.getPos();
		bufferGraphics.drawImage(shooterImg, p.x, p.y, this);
	}
	
	private void drawProgressBar() {
		int heightProg = getHeightOfBar();
		for (int i = 0; i < progress; i++) {
			bufferGraphics.drawImage(progressBar, 623, 515 - heightProg * (i+1), progressBar.getWidth(this), heightProg, this);
		}
	}
	
	
	private BufferedImage[][] strips = new BufferedImage[4][5];
	private BufferedImage[][] rips = new BufferedImage[4][2];

	
	private void checkSimplificationProgress(){
		if (inPlayOne.size() > 1) {
			rotateToPosition(4);
		} else if (inPlayTwo.size() > 1) {
			rotateToPosition(6);
		} else if (inPlayThree.size() > 1) {
			rotateToPosition(0);
		} else if (inPlayFour.size() > 1) {
			rotateToPosition(2);
		} else {
			//end Level
		}
	}
	
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
	}
	
	private int interfaceNumber = 0;
	
	private void drawMenu() {
	
		if (interfaceNumber == 0) {
			bufferGraphics.drawImage(missionBackground, 0, 0, this);
			for (int i = 0; i < pathwayMissionButtons.size(); i++) {
				Button b = pathwayMissionButtons.get(i);
				if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
				else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
			}
		} else if (interfaceNumber == 11) {
			bufferGraphics.drawImage(chooseBackground, 0, 0, this);
			for (int i = 0; i < chooseYourOwnAdventureButtons.size(); i++) {
				Button b = chooseYourOwnAdventureButtons.get(i);
				if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
				else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
			}
		} else if (interfaceNumber == 12) {
			bufferGraphics.drawImage(missionAccomplished, 115, 155, this);
			hosFont = hosFont.deriveFont(18.0f);
			bufferGraphics.setFont(hosFont);
			FontMetrics fontMetrics = bufferGraphics.getFontMetrics(hosFont);
			bufferGraphics.setColor(new Color(51, 51, 51));
			String scoreString = "Score: " + score;
			String completionString = "Completion bonus: +50";
			String totalScoreString = "Total R&D points earned = " + (score+50);
			bufferGraphics.drawString(scoreString, 489 - fontMetrics.stringWidth(scoreString), 258);
			bufferGraphics.drawString(completionString, 489 - fontMetrics.stringWidth(completionString), 286);
			bufferGraphics.drawString(totalScoreString, 489 - fontMetrics.stringWidth(totalScoreString), 314);

			for (int i = 0; i < missionButtons.size(); i++) {
				Button b = missionButtons.get(i);
				bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
			}
		}		
	}
	
	private boolean checkArrayNumber(int ballStrand) {
		switch (ballStrand) {
		case 0: if (inPlayOne.size() > 0) return true; else break;
		case 1: if (inPlayTwo.size() > 0) return true; else break;
		case 2: if (inPlayThree.size() > 0) return true; else break;
		case 3: if (inPlayFour.size() > 0) return true; else break;
		}
		return false;
	}
	
	private void drawCurrentBall() {
		if (currentBall != null && !simplifying) {
			if (difficulty > 1 && (checkArrayNumber(currentBall.strand) || currentLevel > 3)) bufferGraphics.drawImage(balls[4], currentBall.getX(), currentBall.getY(), this);
			else bufferGraphics.drawImage(currentBall.getImage(), currentBall.getX(), currentBall.getY(), this);
			bufferGraphics.drawImage(currentBall.name, currentBall.getX() + 24 - currentBall.name.getWidth()/2, currentBall.getY() + 24 - currentBall.name.getHeight()/2, this);
		}
	}
	
	private void drawCannons() {
		bufferGraphics.drawImage(cannons[2], 106, 9, this);
		bufferGraphics.drawImage(cannons[0], 106, 510, this);
		bufferGraphics.drawImage(cannons[3], 610, 9, this);
		bufferGraphics.drawImage(cannons[1], 610, 510, this);
	}
	
	private void drawCentralMolecule() {
		if (rotation%2 == 0) {
			bufferGraphics.drawImage(centerMolecule, 363, 263, this);	
		} else {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(45), 363 + centerMolecule.getWidth()/2, 263 + centerMolecule.getHeight()/2);
			affineTransform.translate(363, 263);
			Graphics2D g2d = (Graphics2D) bufferGraphics;
			g2d.drawImage(centerMolecule, affineTransform, this);	
		}
		
		int textHeight = 40;
		int textWidth  = 40;
		bufferGraphics.drawImage(centerTitle, 363 + textWidth/2, 263 + textHeight/2 + 5, this);
	}
	
	private void drawMoleculesInPlay() {
		myfont = new Font("Times New Roman", Font.PLAIN, 10);
		bufferGraphics.setFont(myfont);
		
		for (int i = 0; i < inPlayOne.size(); i++) {
			CurrentBall b = inPlayOne.get(i);
			if (!b.simplifiedBall) {
				bufferGraphics.drawImage(b.getImage(), b.getX(), b.getY(), this);
				bufferGraphics.drawImage(b.name, b.getX() + 24 - b.name.getWidth()/2, b.getY() + 24 - b.name.getHeight()/2, this);
			}
		}
		for (int i = 0; i < inPlayTwo.size(); i++) {
			CurrentBall b = inPlayTwo.get(i);
			if (!b.simplifiedBall) {
				bufferGraphics.drawImage(b.getImage(), b.getX(), b.getY(), this);
				bufferGraphics.drawImage(b.name, b.getX() + 24 - b.name.getWidth()/2, b.getY() + 24 - b.name.getHeight()/2, this);
			}
		}
		for (int i = 0; i < inPlayThree.size(); i++) {
			CurrentBall b = inPlayThree.get(i);
			if (!b.simplifiedBall) {
				bufferGraphics.drawImage(b.getImage(), b.getX(), b.getY(), this);
				bufferGraphics.drawImage(b.name, b.getX() + 24 - b.name.getWidth()/2, b.getY() + 24 - b.name.getHeight()/2, this);
			}
		}
		for (int i = 0; i < inPlayFour.size(); i++) {
			CurrentBall b = inPlayFour.get(i);
			if (!b.simplifiedBall) {
				bufferGraphics.drawImage(b.getImage(), b.getX(), b.getY(), this);
				bufferGraphics.drawImage(b.name, b.getX() + 24 - b.name.getWidth()/2, b.getY() + 24 - b.name.getHeight()/2, this);
			}
			
		}		
		
		if (strandOne != null) strandOne.paintArrow((Graphics2D) bufferGraphics, this);
		if (strandTwo != null) strandTwo.paintArrow((Graphics2D) bufferGraphics, this);
		if (strandThree != null) strandThree.paintArrow((Graphics2D) bufferGraphics, this);
		if (strandFour != null) strandFour.paintArrow((Graphics2D) bufferGraphics, this);
		
		for (int i = 0; i < inPlayOne.size(); i++) {
			CurrentBall b = inPlayOne.get(i);
			if (!b.simplifiedArrow) b.paintArrow((Graphics2D) bufferGraphics, this);
		}
		for (int i = 0; i < inPlayTwo.size(); i++) {
			CurrentBall b = inPlayTwo.get(i);
			if (!b.simplifiedArrow) b.paintArrow((Graphics2D) bufferGraphics, this);
		}
		for (int i = 0; i < inPlayThree.size(); i++) {
			CurrentBall b = inPlayThree.get(i);
			if (!b.simplifiedArrow) b.paintArrow((Graphics2D) bufferGraphics, this);
		}
		for (int i = 0; i < inPlayFour.size(); i++) {
			CurrentBall b = inPlayFour.get(i);
			if (!b.simplifiedArrow) b.paintArrow((Graphics2D) bufferGraphics, this);
		}
	}
	
	private int getHeightOfBar() {
		int totalMols = numberOfMoleculesOne + numberOfMoleculesTwo + numberOfMoleculesThree + numberOfMoleculesFour;
		if (totalMols > 0) return 400 / (totalMols);
		return -100;
	}
	
	public void start() {
		
	}
	
	public void stop() {
		   
	}
	
	@Override
	public void keyPressed(KeyEvent evt) {
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_SPACE && !simplifying) {
			if (currentBall != null) {
				eraserFired = true;
				currentBall.dx = 0;
				currentBall.dy = 0;
			}
		}
		if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) && !simplifying) {
			rotation = (rotation + 7)%8;
			if (rotation%2 == 1) cannon = (cannon + 1)%4;
			rotate();
		}
		if (key == KeyEvent.VK_P && !simplifying) {
			pause = !pause;
		}
		if (key == KeyEvent.VK_R) {
			repaint();
		}
		if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) && !simplifying) {
			rotation = (rotation + 1)%8;
			if (rotation%2 == 0) cannon = (cannon + 3)%4;
			rotate();
			
		}
		if (key == KeyEvent.VK_X) {
			
		}
		if (key == KeyEvent.VK_A) {
			
		}
		repaint();
	}
	
	public void rotate() {
		if (strandOne != null) setStartingArrowPosition(strandOne, 6);
		if (strandTwo != null) setStartingArrowPosition(strandTwo, 0);
		if (strandThree != null) setStartingArrowPosition(strandThree, 2);
		if (strandFour != null) setStartingArrowPosition(strandFour, 4);

		for (int i = 0; i < inPlayOne.size(); i++) {
			CurrentBall b = inPlayOne.get(i);
			setBallPosition(i, 6, b);
		}
		for (int i = 0; i < inPlayTwo.size(); i++) {
			CurrentBall b = inPlayTwo.get(i);
			setBallPosition(i, 0, b);
		}
		for (int i = 0; i < inPlayThree.size(); i++) {
			CurrentBall b = inPlayThree.get(i);
			setBallPosition(i, 2, b);
		}
		for (int i = 0; i < inPlayFour.size(); i++) {
			CurrentBall b = inPlayFour.get(i);
			setBallPosition(i, 4, b);
		}
	}

	private void setStartingArrowPosition(StartingArrow strandArrow, int change) {
		if ((rotation+change)%8 == 2) {
			strandArrow.setPos(414, 273);
			strandArrow.setImage(0);
		}
		if ((rotation+change)%8 == 3) {
			strandArrow.setPos(424, 292);
			strandArrow.setImage(1);

		}
		if ((rotation+change)%8 == 4) {
			strandArrow.setPos(415, 315);
			strandArrow.setImage(2);
	
		}
		if ((rotation+change)%8 == 5) {
			strandArrow.setPos(392, 325);
			strandArrow.setImage(3);

		}
		if ((rotation+change)%8 == 6) {
			strandArrow.setPos(370, 313);
			strandArrow.setImage(4);	
		}
		if ((rotation+change)%8 == 7) {
			strandArrow.setPos(360, 292);
			strandArrow.setImage(5);
			
		}
		if ((rotation+change)%8 == 0) {
			strandArrow.setPos(372, 272);
			strandArrow.setImage(6);
			
		}
		if ((rotation+change)%8 == 1) {
			strandArrow.setPos(392, 264);
			strandArrow.setImage(7);
		}

	}
	
	private void setBallPosition(int i, int change, CurrentBall b) {
		if ((rotation+change)%8 == 2) {
			b.setPos(416 + 37*i, 239 - 37*i);
			b.setImage(0);
			b.setArrowPos(b.getX() + 35, b.getY() - 2);
		}
		if ((rotation+change)%8 == 3) {
			b.setPos(434 + 52*i, 277);
			b.setImage(1);
			b.setArrowPos(b.getX() + 42, b.getY() + 15);
		}
		if ((rotation+change)%8 == 4) {
			b.setPos(418 + 37*i, 316 + 37*i);
			b.setImage(2);
			b.setArrowPos(b.getX() + 33, b.getY() + 34);
		}
		if ((rotation+change)%8 == 5) {
			b.setPos(377, 334 + 52*i);
			b.setImage(3);
			b.setArrowPos(b.getX() + 15, b.getY() + 42);
		}
		if ((rotation+change)%8 == 6) {
			b.setPos(335 - 37*i, 315 + 37*i);
			b.setImage(4);
			b.setArrowPos(b.getX() - 2, b.getY() + 33);
		}
		if ((rotation+change)%8 == 7) {
			b.setPos(320 - 52*i, 277);
			b.setImage(5);
			b.setArrowPos(b.getX() - 10, b.getY() + 15);
		}
		if ((rotation+change)%8 == 0) {
			b.setPos(338 - 37*i, 236 - 37*i);
			b.setImage(6);
			b.setArrowPos(b.getX() - 3, b.getY() - 3);
		}
		if ((rotation+change)%8 == 1) {
			b.setPos(377, 220 - 52*i);
			b.setImage(7);
			b.setArrowPos(b.getX() + 15, b.getY() - 10);
		}	
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
		
	private void fireBall() {
		if (setCannon == 0) {
			ballExistence = true;
			cannon = (400-(rotation/2))%4;
		}
		if (setCannon == 1) {
			ballExistence = true;
			currentBall.setMove(2);
			cannon = (401-(rotation/2))%4;

		}
		if (setCannon == 2) {
			ballExistence = true;
			currentBall.setMove(4);
			cannon = (402-(rotation/2))%4;
		}
		if (setCannon == 3) {
			ballExistence = true;
			currentBall.setMove(3);
			cannon = (403-(rotation/2))%4;
		}
	}
	
	private void increaseArray() {
		switch(strand) {
		case 0: arr1++; inPlayOne.add(currentBall); break; 
		case 1: arr2++; inPlayTwo.add(currentBall); break;
		case 2: arr3++; inPlayThree.add(currentBall); break;
		case 3: arr4++; inPlayFour.add(currentBall); break;
		}
	}
	
	private void decreaseArray(int collisionStrand, CurrentBall b) {
		switch(collisionStrand) {
		case 0: arr1--; inPlayOne.remove(b); break;
		case 1: arr2--; inPlayTwo.remove(b); break;
		case 2: arr3--; inPlayThree.remove(b); break;
		case 3: arr4--; inPlayFour.remove(b); break;
		}
	}
	/*
	private void simplifyArray(CurrentBall b) {
		switch(b.strand) {
		case 0: inPlayOne.remove(b); break;
		case 1: inPlayTwo.remove(b); break;
		case 2: inPlayThree.remove(b); break;
		case 3: inPlayFour.remove(b); break;
		}
	}*/
	
	private void checkPreviousBall(Rectangle prevRect, Rectangle ballRec, CurrentBall previousBall) {
		if (ballRec.intersects(prevRect)) {
			if (previousBall.getStrand() != currentBall.getStrand()) {
				decreaseArray(previousBall.getStrand(), previousBall);
				progress--;
				currentBall.dx = 0;
				currentBall.dy = 10;
			} else {
				if (progress < 11) progress++;
				score += 50;
				increaseArray();
				rotate();
				repaint();
				getNextBall();
			}
		}
	}
	
	private void checkStartingArrow(Rectangle arrowRect, Rectangle ballRec, int arrowStrand) {
		if (ballRec.intersects(arrowRect)) {
			if (currentBall.getStrand() != arrowStrand) {
				currentBall.dx = 0;
				currentBall.dy = 10;
			} else if (currentBall.dy != 10) {
				if (progress < 11) progress++;
				score += 25;
				increaseArray();
				ballExistence = false;
				rotate();
				repaint();
				getNextBall();
			}
		}
	}
	
	public void checkCollisions() {
		CurrentBall previousBall;
		Rectangle ballRec = null;
		
		if (ballExistence && currentBall.dy != 10) {
			ballRec = currentBall.getBounds();
			Rectangle centerRect = new Rectangle(373, 272, centerMolecule.getWidth(null) - 20, centerMolecule.getHeight(null) - 20);
			Rectangle startingArrowRect;
			if (ballExistence && rotation % 2 == 0) {			
				if (arr1 > 0) {
					previousBall = inPlayOne.get(inPlayOne.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (strandOne != null){
					startingArrowRect = strandOne.getBounds();
					checkStartingArrow(ballRec, startingArrowRect, 0);
				}
				if (arr2 > 0) {
					previousBall = inPlayTwo.get(inPlayTwo.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (strandTwo != null) {
						startingArrowRect = strandTwo.getBounds();
						checkStartingArrow(ballRec, startingArrowRect, 1);
				}
				if (arr3 > 0) {
					previousBall = inPlayThree.get(inPlayThree.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (strandThree != null) {
					startingArrowRect = strandThree.getBounds();
					checkStartingArrow(ballRec, startingArrowRect, 2);
				}
				if (arr4 > 0) {
					previousBall = inPlayFour.get(inPlayFour.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (strandFour != null){
					startingArrowRect = strandFour.getBounds();
					checkStartingArrow(ballRec, startingArrowRect, 3);
				}				
			}			
			if (eraserFired && ballExistence) {
				if (shooter.getBounds().intersects(ballRec)) {
					rotate();
					repaint();
					getNextBall();
				}
			}
			if (centerRect.intersects(ballRec)) {
				getNextBall();
			}
			
		}
		if (ballExistence) {
			if (currentBall.getY() > 680 || currentBall.getY() < 10) {
				getNextBall();
			}
		}
	}	
	
	private void getNextBall() {
		ballExistence = false;
		boolean newBall = false;
		while (!newBall) {
			strand = generator.nextInt(4);
			setCannon = generator.nextInt(4);
			
			if (difficulty > 2) {
				int prob = generator.nextInt(10);
				if (prob < 2 && listFive.size() > 0) {
					strand = 4;
				}
			}
			
			switch (strand) {
			case 0: if (arr1 < numberOfMoleculesOne) {currentBall = listOne.get(arr1); newBall = true;} 
				break;
			case 1: if (arr2 < numberOfMoleculesTwo) {currentBall = listTwo.get(arr2); newBall = true;}
				break;
			case 2: if (arr3 < numberOfMoleculesThree) {currentBall = listThree.get(arr3); newBall = true;}
				break;
			case 3: if (arr4 < numberOfMoleculesFour) {currentBall = listFour.get(arr4); newBall = true;}
				break;
			case 4: currentBall = listFive.get(0); newBall = true; break;
			}
			
			if (newBall) {
				setCurrentBallPosition();
			}
			
			if (arr1 == numberOfMoleculesOne && arr2 == numberOfMoleculesTwo && arr3 == numberOfMoleculesThree && arr4 == numberOfMoleculesFour) {
				newBall = true;	
				ballExistence = false;
				rotateToPosition(4);
				//simplifying = true;
				interfaceNumber = 12;
				playing = false;
				
			}
		}
	}
	
	
	
	private void setCurrentBallPosition() {
		currentBall.dx = 5; currentBall.dy = 5;
		switch (setCannon) {
		case 0: currentBall.setPos(118, 18); break;
		case 1: currentBall.setPos(635, 18); break;
		case 2: currentBall.setPos(635, 535); break;
		case 3: currentBall.setPos(118, 535); break;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {		
		//if (!playing) {
			checkToSetUpLevel(arg0);
		//}
		if (simplifying) {
			
		}
	}
	

		
	private void checkToSetUpLevel(MouseEvent arg0) {
		if (pickDifficulty) {
			for (int i = 0; i < levelButtons.size(); i++) {
				Button b = levelButtons.get(i);
				if (b.getBounds().contains(arg0.getPoint())) setUpInterfaceOrLevels(b.target);
			}
			if (currentLevel > 0 && currentLevel < 8 && !pickDifficulty) {
				setUpLists(currentLevel);
				getNextBall();
				playing = true;
				rotate();
				repaint();
			}
		} else if (pause) {
			for (int i = 0; i < pauseScreenButtons.size(); i++) {
				Button b = pauseScreenButtons.get(i);
				if (b.getBounds().contains(arg0.getPoint())) setUpInterfaceOrLevels(b.target);
			} 
		}
			else if (interfaceNumber == 0) {
			for (int i = 0; i < pathwayMissionButtons.size(); i++) {
				Button b = pathwayMissionButtons.get(i);
				if (b.getBounds().contains(arg0.getPoint())) setUpInterfaceOrLevels(b.target);
			}
		} else if (interfaceNumber == 11) {
			for (int i = 0; i < chooseYourOwnAdventureButtons.size(); i++) {
				Button b = chooseYourOwnAdventureButtons.get(i);
				if (b.getBounds().contains(arg0.getPoint())) setUpInterfaceOrLevels(b.target);
			}
		} else if (interfaceNumber == 12) {
			for (int i = 0; i < missionButtons.size(); i++) {
				Button b = missionButtons.get(i);
				if (b.getBounds().contains(arg0.getPoint())) setUpInterfaceOrLevels(b.target);
			}
		}
		
	}
	
	private boolean pickDifficulty = false;
	
	private void setUpInterfaceOrLevels(int number) {
		interfaceNumber = number;
		if (number > 0 && number < 8) currentLevel = number;
		switch(number) {
		case -3: difficulty = 3; pickDifficulty = false; break;
		case -2: difficulty = 2; pickDifficulty = false; break;
		case -1: difficulty = 1; pickDifficulty = false; break;
		case 0: interfaceNumber = 0; break;
		case 1: case 2: case 3: case 4: 
		case 5: case 6: case 7:	pickDifficulty = true; break;
		case 10: break; //playTutorial 
		case 11: interfaceNumber = 11; break;
		case 15: pickDifficulty = false; break;
		case 20: pickDifficulty = true; break;
		case 25: pickDifficulty = true; currentLevel++; break;
		case 30: pause = false;
		}
	}
	
	
	
	
	/*
	private ArrowType checkSimplificationRules(CurrentBall b) {
		ArrowType aType = null;
		ArrowType bType = b.simplifyType;
		ArrowType cType = null;
		
		if (simpleIndex == 0) {
			switch(b.strand) {
			case 0: aType = strandOne.arrowType; break;
			case 1: aType = strandTwo.arrowType; break;
			case 2: aType = strandThree.arrowType; break;
			case 3: aType = strandFour.arrowType; break;
			}
		} else {
			switch(b.strand) {
			case 0: cType = inPlayOne.get(simpleIndex-1).simplifyType; break;
			case 1: cType = inPlayTwo.get(simpleIndex-1).simplifyType; break;
			case 2: cType = inPlayThree.get(simpleIndex-1).simplifyType; break;
			case 3: cType = inPlayFour.get(simpleIndex-1).simplifyType; break;
			}
		}
		
		if (bType == null) {
			if (cType != null) return cType;
			return aType;
		}
		
		if (cType != null) {
			if ((cType == ArrowType.ACTIVATE && bType == ArrowType.INHIBIT) || (cType == ArrowType.INHIBIT && bType == ArrowType.ACTIVATE)) {
				return ArrowType.INHIBIT;
				
			} else {
				return ArrowType.ACTIVATE;
			} 
		} else {
			if ((aType == ArrowType.ACTIVATE && bType == ArrowType.INHIBIT) || (aType == ArrowType.INHIBIT && bType == ArrowType.ACTIVATE)) {
				return ArrowType.INHIBIT;

			} else {
				return ArrowType.ACTIVATE;
			}
		}
	}
	
	private void changeArrows(CurrentBall b) {
		CurrentBall c = null;
		StartingArrow a = null;
		ArrowType aType = null;
		ArrowType bType = null;
		ArrowType cType = null;
		
		if (simpleIndex == 0) {
			switch(b.strand) {
			case 0: a = strandOne; break;
			case 1: a = strandTwo; break;
			case 2: a = strandThree; break;
			case 3: a = strandFour; break;
			}
		} else {
			switch(b.strand) {
			case 0: c = inPlayOne.get(simpleIndex-1); break;
			case 1: c = inPlayTwo.get(simpleIndex-1); break;
			case 2: c = inPlayThree.get(simpleIndex-1); break;
			case 3: c = inPlayFour.get(simpleIndex-1); break;
			}
		}
		
		if (c != null) {
			if ((cType == ArrowType.ACTIVATE && bType == ArrowType.INHIBIT) || (cType == ArrowType.INHIBIT && bType == ArrowType.ACTIVATE)) {
				c.simplifyType = ArrowType.INHIBIT;
				switch(b.strand) {
				case 0: c.arrowImages = blueInhibitor; break;
				case 1: c.arrowImages = orangeInhibitor; break;
				case 2: c.arrowImages = redInhibitor; break;
				case 3: c.arrowImages = greenInhibitor; break;
				}
			} else {
				c.simplifyType = ArrowType.ACTIVATE;
				switch(b.strand) {
				case 0: c.arrowImages = blueArrow; break;
				case 1: c.arrowImages = orangeArrow; break;
				case 2: c.arrowImages = redArrow; break;
				case 3: c.arrowImages = greenArrow; break;
				}
			} 
		} else {
			if ((aType == ArrowType.ACTIVATE && bType == ArrowType.INHIBIT) || (aType == ArrowType.INHIBIT && bType == ArrowType.ACTIVATE)) {
				a.arrowType = ArrowType.INHIBIT;
				switch(b.strand) {
				case 0: a.arrowImages = blueInhibitor; break;
				case 1: a.arrowImages = orangeInhibitor; break;
				case 2: a.arrowImages = redInhibitor; break;
				case 3: a.arrowImages = greenInhibitor; break;
				}
			} else {
				a.arrowType = ArrowType.ACTIVATE;
				switch(b.strand) {
				case 0: a.arrowImages = blueArrow; break;
				case 1: a.arrowImages = orangeArrow; break;
				case 2: a.arrowImages = redArrow; break;
				case 3: a.arrowImages = greenArrow; break;
				}
			}
		}
		
	}
	
	private void getObjectAt(int x, int y) {
		for (int i = 0; i < inPlayOne.size(); i++) {
			CurrentBall b = inPlayOne.get(i);
			setSimpleBall(b, x, y, i);
		}
		for (int i = 0; i < inPlayTwo.size(); i++) {
			CurrentBall b = inPlayTwo.get(i);
			setSimpleBall(b, x, y, i);	
		}
		for (int i = 0; i < inPlayThree.size(); i++) {
			CurrentBall b = inPlayThree.get(i);
			setSimpleBall(b, x, y, i);	
		}
		for (int i = 0; i < inPlayFour.size(); i++) {
			CurrentBall b = inPlayFour.get(i);
			setSimpleBall(b, x, y, i);
		}
	}
	
	private void setSimpleBall(CurrentBall b, int x, int y, int i) {
		if (b.getBounds().contains(x, y)) {
			simplifyBall = b;
			simpleIndex = i;
		}
	}
*/
	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
}