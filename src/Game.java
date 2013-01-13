import java.applet.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

public class Game extends Applet implements KeyListener, MouseListener, Runnable {
	
	private static final long serialVersionUID = 1;	
	private static final int width = 700, height = 600;
	private Thread mainLoop;
	private boolean running = true;
	private int countdown = 50;
	
	private Graphics bufferGraphics;
	
	private Image offscreen;	
	private HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	private BufferedImage[] cannons = new BufferedImage[4];
	private BufferedImage[] balls = new BufferedImage[5];
		
	private StartingArrow[] startingArrowStrand = new StartingArrow[4];
	private Shooter shooter;
	private CurrentBall currentBall;
	private Font hosFont;
	
	private Random generator = new Random();
	private int currentLevel = 0;
	private int difficulty = 1;
	private int cannon = 0, setCannon = 0, rotation = 0, strand = 0;
	private int score = 0, progress = 0;
	private int[] arr = new int[4];
	private int numberOfMolecules[] = new int[4];
	
	
	private BufferedImage centerTitle;
	private ArrayList<CurrentBall> listOne = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listTwo = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listThree = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listFour = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> listFive = new ArrayList<CurrentBall>();
	
	private ArrayList<CurrentBall> inPlayOne = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayTwo = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayThree = new ArrayList<CurrentBall>();
	private ArrayList<CurrentBall> inPlayFour = new ArrayList<CurrentBall>();
	
	private ArrayList<Button> chooseYourOwnAdventureButtons = new ArrayList<Button>();
	private ArrayList<Button> pathwayMissionButtons = new ArrayList<Button>();
	private ArrayList<Button> pauseScreenButtons = new ArrayList<Button>();
	private ArrayList<Button> levelButtons = new ArrayList<Button>();
	private ArrayList<Button> missionButtons = new ArrayList<Button>();
	
	private BufferedImage[][] strips = new BufferedImage[4][5];
	private BufferedImage[][] rips = new BufferedImage[4][2];

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
		arr[0] = 0; arr[1] = 0; arr[2] = 0; arr[3] = 0;
		
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		score = 0;
		progress = 0;
		listFive.add(new CurrentBall(5, balls[4], imageMap.get("apcoli"), null, null, false, false));
		
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
		numberOfMolecules[0] = 1;
		numberOfMolecules[1] = 1;
		numberOfMolecules[2] = 4;
		numberOfMolecules[3] = 0;
						
		centerTitle = imageMap.get("apc");
		
		startingArrowStrand[0] = new StartingArrow(0, true,  imageMap.get("blueArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[1] = new StartingArrow(1, false,  imageMap.get("greenInhibitor"), ArrowType.INHIBIT);
		startingArrowStrand[2] = new StartingArrow(2, false,  imageMap.get("redInhibitor"), ArrowType.INHIBIT);
		startingArrowStrand[3] = null;

		
		listOne.add(new CurrentBall(0, balls[0], imageMap.get("mcdk"), null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[1], imageMap.get("mcyclin"), null, null, false, false));
		
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("securins"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, true));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("separase"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, false));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("cohesin"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false, true));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("anaphase"), null, null, false, true));

	}
	
	private void setUpLevelTwo() {
		numberOfMolecules[0] = 5;
		numberOfMolecules[1] = 3;
		numberOfMolecules[2] = 1;
		numberOfMolecules[3] = 0;
		
		startingArrowStrand[0] = new StartingArrow(0, false,  imageMap.get("redArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[1] = new StartingArrow(1, false,  imageMap.get("blueArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[2] = new StartingArrow(2, true,  imageMap.get("orangeArrow"), ArrowType.ACTIVATE);;
		startingArrowStrand[3] = null;
		
		score = 0;
		progress = 0;
		centerTitle = imageMap.get("p53");
		
		arr[0] = 0; arr[1] = 0; arr[2] = 0; arr[3] = 0;
		
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear(); 
	
		listOne.add(new CurrentBall(0, balls[3],  imageMap.get("bax"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, true));
		listOne.add(new CurrentBall(0, balls[3],  imageMap.get("cytc"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, true));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("adaptorProteins"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, true));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("caspase"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false, false));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("apoptosis"), null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("p21"), ArrowType.INHIBIT,  imageMap.get("blueInhibitor"), false, true));
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("scdkscyclin"), ArrowType.ACTIVATE,  imageMap.get("blueArrow"), false, true));
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("dnareplication"), null, null, false, true));

		listThree.add(new CurrentBall(2, balls[2], imageMap.get("dnadamage"), null, null, false, false));
	}
	
	private void setUpLevelThree() {
		numberOfMolecules[0] = 5;
		numberOfMolecules[1] = 3;
		numberOfMolecules[2] = 3;
		numberOfMolecules[3] = 3;
		
		startingArrowStrand[0] = new StartingArrow(0, true,  imageMap.get("greenArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[1] = new StartingArrow(1, true,  imageMap.get("redArrow"), ArrowType.INHIBIT);
		startingArrowStrand[2] = new StartingArrow(2, false,  imageMap.get("orangeInhibitor"), ArrowType.INHIBIT);
		startingArrowStrand[3] = new StartingArrow(3, false,  imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
		
		score = 0;
		progress = 0;
		centerTitle = imageMap.get("sckd");
		
		arr[0] = 0; arr[1] = 0; arr[2] = 0; arr[3] = 0;

		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("mapkinaserelay"),  ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("ras"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("rasactprotein"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("rtks"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true, false));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("mitogen"), null, null, false, false));
		
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("p21"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true, true));
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("p53"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true, false));
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("dnadamage"),  null, null, false, false));

		listThree.add(new CurrentBall(2, balls[2], imageMap.get("rb"), ArrowType.INHIBIT,imageMap.get("orangeInhibitor"), false, true));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("replicationproteins"), ArrowType.ACTIVATE, imageMap.get("orangeArrow"), false, false));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("dnareplication"), null, null, false, false));

		listFour.add(new CurrentBall(3, balls[0], imageMap.get("cdc6"), ArrowType.INHIBIT,imageMap.get("blueInhibitor"), false, true));
		listFour.add(new CurrentBall(3, balls[0], imageMap.get("orc"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, false));	
		listFour.add(new CurrentBall(3, balls[0], imageMap.get("dnareplication"), null, null, false, false));	

	}
	
	private void setUpLevelFour() {
		
		numberOfMolecules[0] = 1;
		numberOfMolecules[1] = 0;
		numberOfMolecules[2] = 1;
		numberOfMolecules[3] = 0;
		centerTitle = imageMap.get("notch");
		startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("greenArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[1] = null;
		startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("orangeArrow"), ArrowType.ACTIVATE);
		startingArrowStrand[3] = null;	
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("genetranscription"),  null, null, false, true));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("delta"), null, null, false, false));
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelFive() {
		int placeHolder = generator.nextInt(2);
		if (placeHolder == 0){
			numberOfMolecules[0] = 2;
			numberOfMolecules[2] = 1;
			centerTitle = imageMap.get("tgfbeta");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("orangeArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("nodal"),  null, null, true, false));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("smads"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, true));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("genetranscription"), null, null, false, false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("smads");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("orangeArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("tgfbeta"), ArrowType.ACTIVATE,imageMap.get("redArrow"), true, false));
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("nodal"),  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("genetranscription"), ArrowType.ACTIVATE, imageMap.get("orangeArrow"), false, false));
		}
		
		numberOfMolecules[1] = 0;
		numberOfMolecules[3] = 0;
		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelSix() {
		int placeHolder = generator.nextInt(3);
		if (placeHolder == 0){
			numberOfMolecules[0] = 3;
			numberOfMolecules[2] = 1;
			centerTitle = imageMap.get("patched");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("redInhibitor"), ArrowType.INHIBIT);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"),  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("smo"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 2;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("smo");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("patched"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true, true));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"), null, null, false, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
		} else if (placeHolder == 2) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 3;
			centerTitle = imageMap.get("gli");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("blueArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("smo"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true, true));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("patched"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), true, false));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"), null, null, true, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false, false));
		}
		
		numberOfMolecules[1] = 0;
		numberOfMolecules[3] = 0;
		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;
		
		score = 0;
		progress = 0;
	}
	
	private void setUpLevelSeven() {
		int placeHolder = generator.nextInt(4);
		if (placeHolder == 0){
			numberOfMolecules[0] = 4;
			numberOfMolecules[2] = 1;
			centerTitle = imageMap.get("frizzled");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("blueArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"),  null, null, true, true));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("lrp"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("apc"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, false));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false, false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 3;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("lrp");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"), null, null, true, false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("apc"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false, false));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false, false));
		} else if (placeHolder == 2) {
			numberOfMolecules[0] = 2;
			numberOfMolecules[2] = 3;
			centerTitle = imageMap.get("apc");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"), ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, false));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"), null, null, true, false));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false, false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false, false));
		} else if (placeHolder == 3) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 4;
			centerTitle =  imageMap.get("betacatenin");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("blueArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("apc"),  ArrowType.INHIBIT, imageMap.get("greenInhibitor"), true, true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true, true));
			listOne.add(new CurrentBall(2, balls[0], imageMap.get("genetranscription"), null, null, false, true));
		}
		
		numberOfMolecules[1] = 0;
		numberOfMolecules[3] = 0;
		startingArrowStrand[1] = null;
		startingArrowStrand[3] = null;
		
		score = 0;
		progress = 0;
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
		
		for (int i = 0; i < 3; i++) {
			balls[i] = finalSprites.getSubimage(2486 + 48*i, 0, 48, 48);
		}
		balls[3] = finalSprites.getSubimage(2630, 0, 47, 48);
		
		for (int i = 0; i < 4; i++) {
			cannons[i] = finalSprites.getSubimage(268 + 84*i, 0, 84, 84);
		}
		
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
		imageMap.put("replicationproteins", finalSprites.getSubimage(1453, 0, 40, 28));
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
		imageMap.put("securins", finalSprites.getSubimage(3879, 0, 38, 16));
		
		imageMap.put("centerMolecule", finalSprites.getSubimage(643, 0, 77, 79));	
		imageMap.put("shooterImg", finalSprites.getSubimage(851, 0, 47, 79));
		imageMap.put("progressBar", finalSprites.getSubimage(2902, 0, 69, 39));
		imageMap.put("background", finalSprites.getSubimage(1710, 0, 700, 600));
		imageMap.put("greyBorder", finalSprites.getSubimage(2971, 0, 700, 600));
		imageMap.put("pauseButton", finalSprites.getSubimage(2726, 0, 88, 42));
		imageMap.put("pauseButtonPressed", finalSprites.getSubimage(2814, 0, 88, 42));
		
		
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
		balls[4] = interfaceSprites.getSubimage(3482, 0, 48, 49);
		
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
		
		imageMap.put("missionAccomplished", tutorialSprites.getSubimage(90, 0, 470, 290));
		imageMap.put("onwards", tutorialSprites.getSubimage(2273, 0, 90, 50));
		imageMap.put("replay", tutorialSprites.getSubimage(1358, 0, 91, 51));
	}
	
	private void createButtons() {
		Button lvlOneBut = new Button(165, 254,  imageMap.get("lvl1"),  imageMap.get("levelPressed"), -1);
		Button lvlTwoBut = new Button(300, 254,  imageMap.get("lvl2"),  imageMap.get("levelPressed"), -2);
		Button lvlThreeBut = new Button(436, 254,  imageMap.get("lvl3"),  imageMap.get("levelPressed"), -3);
		Button smBackBut = new Button(144, 378,  imageMap.get("smBackButton"),  imageMap.get("smBackPressed"), 15);
		levelButtons.add(lvlOneBut); levelButtons.add(lvlTwoBut);
		levelButtons.add(lvlThreeBut); levelButtons.add(smBackBut);

		
		Button apcBut = new Button(75, 420, imageMap.get("APCButton"), imageMap.get("pathwayPressed"), 1);
		Button scdkBut = new Button(75, 333,  imageMap.get("scdkButton"), imageMap.get("pathwayPressed"), 2);
		Button p53But  = new Button(75, 245,  imageMap.get("p53button"), imageMap.get("pathwayPressed"), 3);
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
		Button menuBut = new Button(30, 490, imageMap.get("menu"), imageMap.get("menupressed"), 0);
		Button tutorialBut = new Button(275, 132, imageMap.get("tutorial"), imageMap.get("tutorialpressed"), 1);
		Button chooseOwnBut = new Button(200, 490, imageMap.get("chooseown"), imageMap.get("chooseownpressed"), 11);
		pathwayMissionButtons.add(cellToCellBut); pathwayMissionButtons.add(cellCycleBut);
		pathwayMissionButtons.add(menuBut); pathwayMissionButtons.add(tutorialBut);
		pathwayMissionButtons.add(chooseOwnBut);
		
		
		Button backToPathwaysBut  = new Button(144, 372, imageMap.get("btpButton"),  imageMap.get("playPressed"), 0);
		Button helpBut = new Button(252, 372,  imageMap.get("helpButton"),  imageMap.get("playPressed"), 35);
		Button playBut  = new Button(360, 372,  imageMap.get("playButton"),  imageMap.get("playPressed"), 30);
		Button restartBut = new Button(467, 372,  imageMap.get("restartButton"),  imageMap.get("playPressed"), 20);
		pauseScreenButtons.add(backToPathwaysBut); pauseScreenButtons.add(restartBut);
		pauseScreenButtons.add(helpBut); pauseScreenButtons.add(playBut);
		
		Button backtoMenuBut = new Button(144, 372, imageMap.get("backButton"), null, 0);
		Button replayBut = new Button(310, 372,  imageMap.get("replay"), null, 20);
		Button onwardsBut = new Button(467, 372,  imageMap.get("onwards"), null, 25);
		missionButtons.add(replayBut); missionButtons.add(onwardsBut);
		missionButtons.add(backtoMenuBut);
		
		
	}
	
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
		if (playing) bufferGraphics.drawImage(imageMap.get("greyBorder"), 0, 0, this);
		g.drawImage(offscreen, 0, 0, this);
	}
	
	private void drawDifficultyScreen() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawRect(0, 0, width, height);
		bufferGraphics.fillRect(0, 0, width, height);
		bufferGraphics.drawImage( imageMap.get("redPause"), 115, 155, this);
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
		bufferGraphics.drawImage( imageMap.get("redPause"), 115, 155, this);
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
			bufferGraphics.drawImage( imageMap.get("missionBackground"), 0, 0, this);
			for (int i = 0; i < pathwayMissionButtons.size(); i++) {
				Button b = pathwayMissionButtons.get(i);
				if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
				else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
			}
		} else if (interfaceNumber == 11) {
			bufferGraphics.drawImage( imageMap.get("chooseBackground"), 0, 0, this);
			for (int i = 0; i < chooseYourOwnAdventureButtons.size(); i++) {
				Button b = chooseYourOwnAdventureButtons.get(i);
				if (b.pressed) bufferGraphics.drawImage(b.buttonPressed, b.xPos, b.yPos, this);
				else bufferGraphics.drawImage(b.button, b.xPos, b.yPos, this);
			}
		} else if (interfaceNumber == 12) {
			bufferGraphics.drawImage( imageMap.get("missionAccomplished"), 115, 155, this);
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
			bufferGraphics.drawImage(imageMap.get("centerMolecule"), 363, 263, this);	
		} else {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(45), 363 + imageMap.get("centerMolecule").getWidth()/2, 263 + imageMap.get("centerMolecule").getHeight()/2);
			affineTransform.translate(363, 263);
			Graphics2D g2d = (Graphics2D) bufferGraphics;
			g2d.drawImage(imageMap.get("centerMolecule"), affineTransform, this);	
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
		
		if (startingArrowStrand[0] != null) startingArrowStrand[0].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[1] != null) startingArrowStrand[1].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[2] != null) startingArrowStrand[2].paintArrow((Graphics2D) bufferGraphics, this);
		if (startingArrowStrand[3] != null) startingArrowStrand[3].paintArrow((Graphics2D) bufferGraphics, this);
		
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
		int totalMols = numberOfMolecules[0] + numberOfMolecules[1] + numberOfMolecules[2] + numberOfMolecules[3];
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
		if (startingArrowStrand[0] != null) setStartingArrowPosition(startingArrowStrand[0], 6);
		if (startingArrowStrand[1] != null) setStartingArrowPosition(startingArrowStrand[1], 0);
		if (startingArrowStrand[2] != null) setStartingArrowPosition(startingArrowStrand[2], 2);
		if (startingArrowStrand[3] != null) setStartingArrowPosition(startingArrowStrand[3], 4);

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
		case 0: arr[0]++; inPlayOne.add(currentBall); break; 
		case 1: arr[1]++; inPlayTwo.add(currentBall); break;
		case 2: arr[2]++; inPlayThree.add(currentBall); break;
		case 3: arr[3]++; inPlayFour.add(currentBall); break;
		}
	}
	
	private void decreaseArray(int collisionStrand, CurrentBall b) {
		switch(collisionStrand) {
		case 0: arr[0]--; inPlayOne.remove(b); break;
		case 1: arr[1]--; inPlayTwo.remove(b); break;
		case 2: arr[2]--; inPlayThree.remove(b); break;
		case 3: arr[3]--; inPlayFour.remove(b); break;
		}
	}
	
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
			Rectangle centerRect = new Rectangle(373, 272, imageMap.get("centerMolecule").getWidth(null) - 20, imageMap.get("centerMolecule").getHeight(null) - 20);
			Rectangle startingArrowRect;
			if (ballExistence && rotation % 2 == 0) {			
				if (arr[0] > 0) {
					previousBall = inPlayOne.get(inPlayOne.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (startingArrowStrand[0] != null){
					startingArrowRect = startingArrowStrand[0].getBounds();
					checkStartingArrow(ballRec, startingArrowRect, 0);
				}
				if (arr[1] > 0) {
					previousBall = inPlayTwo.get(inPlayTwo.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (startingArrowStrand[1] != null) {
						startingArrowRect = startingArrowStrand[1].getBounds();
						checkStartingArrow(ballRec, startingArrowRect, 1);
				}
				if (arr[2] > 0) {
					previousBall = inPlayThree.get(inPlayThree.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (startingArrowStrand[2] != null) {
					startingArrowRect = startingArrowStrand[2].getBounds();
					checkStartingArrow(ballRec, startingArrowRect, 2);
				}
				if (arr[3] > 0) {
					previousBall = inPlayFour.get(inPlayFour.size()-1);
					Rectangle prevRect = new Rectangle(previousBall.getX(), previousBall.getY(), 35, 35);
					checkPreviousBall(prevRect, ballRec, previousBall);
				} else if (startingArrowStrand[3] != null){
					startingArrowRect = startingArrowStrand[3].getBounds();
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
			case 0: if (arr[0] < numberOfMolecules[0]) {currentBall = listOne.get(arr[0]); newBall = true;} 
				break;
			case 1: if (arr[1] < numberOfMolecules[1]) {currentBall = listTwo.get(arr[1]); newBall = true;}
				break;
			case 2: if (arr[2] < numberOfMolecules[2]) {currentBall = listThree.get(arr[2]); newBall = true;}
				break;
			case 3: if (arr[3] < numberOfMolecules[3]) {currentBall = listFour.get(arr[3]); newBall = true;}
				break;
			case 4: currentBall = listFive.get(0); newBall = true; break;
			}
			
			if (newBall) {
				setCurrentBallPosition();
			}
			
			if (arr[0] == numberOfMolecules[0] && arr[1] == numberOfMolecules[1] && arr[2] == numberOfMolecules[2] && arr[3] == numberOfMolecules[3]) {
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