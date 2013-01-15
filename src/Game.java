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
	private int countdown = 50;
	
	private Graphics bufferGraphics;
	private Image offscreen;	
	
	private HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	private BufferedImage[] cannons = new BufferedImage[4];
	private BufferedImage[] balls = new BufferedImage[5];
	private BufferedImage[][] strips = new BufferedImage[4][5];
	private BufferedImage[][] rips = new BufferedImage[4][2];
	private Font hosFont;
	
	private StartingArrow[] startingArrowStrand = new StartingArrow[4];
	private Shooter shooter;
	private CurrentBall currentBall;
	
	
	private Random generator = new Random();
	private int currentLevel = 0;
	private int difficulty = 1;
	private int setCannon = 0, rotation = 0, strand = 0;
	private int score = 0, progress = 0;
	private int numberOfMolecules[] = new int[4];
	private int interfaceNumber = 0;
	
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
	private ArrayList<Button> tutorialButtons = new ArrayList<Button>();
	private ArrayList<Button> helpButtons = new ArrayList<Button>();

	
	private boolean ballExistence = false, eraserFired = false, simplifying = false;
	private boolean pause = false, help = false, playing = false, gameOver = false;
	private boolean pickDifficulty = false, tutorial = false;
	
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
		
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		score = 0;
		progress = 0;
		listFive.add(new CurrentBall(5, balls[4], imageMap.get("apcoli"), null, null, false));
		
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

		
		listOne.add(new CurrentBall(0, balls[0], imageMap.get("mcdk"), null, null, false));
		
		listTwo.add(new CurrentBall(1, balls[1], imageMap.get("mcyclin"), null, null, false));
		
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("securins"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("separase"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("cohesin"), ArrowType.INHIBIT,  imageMap.get("redInhibitor"), false));
		listThree.add(new CurrentBall(2, balls[3], imageMap.get("anaphase"), null, null, false));

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
				
		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear(); 
	
		listOne.add(new CurrentBall(0, balls[3],  imageMap.get("bax"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false));
		listOne.add(new CurrentBall(0, balls[3],  imageMap.get("cytc"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("adaptorProteins"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("caspase"), ArrowType.ACTIVATE,  imageMap.get("redArrow"), false));
		listOne.add(new CurrentBall(0, balls[3], imageMap.get("apoptosis"), null, null, false));
		
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("p21"), ArrowType.INHIBIT,  imageMap.get("blueInhibitor"), false));
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("scdkscyclin"), ArrowType.ACTIVATE,  imageMap.get("blueArrow"), false));
		listTwo.add(new CurrentBall(1, balls[0], imageMap.get("dnareplication"), null, null, false));

		listThree.add(new CurrentBall(2, balls[2], imageMap.get("dnadamage"), null, null, false));
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
		

		listOne.clear(); listTwo.clear(); listThree.clear(); listFour.clear();
		inPlayOne.clear(); inPlayTwo.clear(); inPlayThree.clear(); inPlayFour.clear();
		
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("mapkinaserelay"),  ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("ras"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("rasactprotein"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("rtks"), ArrowType.ACTIVATE,  imageMap.get("greenArrow"), true));
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("mitogen"), null, null, false));
		
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("p21"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true));
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("p53"),  ArrowType.ACTIVATE,imageMap.get("redArrow"), true));
		listTwo.add(new CurrentBall(1, balls[3], imageMap.get("dnadamage"),  null, null, false));

		listThree.add(new CurrentBall(2, balls[2], imageMap.get("rb"), ArrowType.INHIBIT,imageMap.get("orangeInhibitor"), false));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("replicationproteins"), ArrowType.ACTIVATE, imageMap.get("orangeArrow"), false));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("dnareplication"), null, null, false));

		listFour.add(new CurrentBall(3, balls[0], imageMap.get("cdc6"), ArrowType.INHIBIT,imageMap.get("blueInhibitor"), false));
		listFour.add(new CurrentBall(3, balls[0], imageMap.get("orc"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false));	
		listFour.add(new CurrentBall(3, balls[0], imageMap.get("dnareplication"), null, null, false));	

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
		listOne.add(new CurrentBall(0, balls[1], imageMap.get("genetranscription"),  null, null, false));
		listThree.add(new CurrentBall(2, balls[2], imageMap.get("delta"), null, null, false));
		
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
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("nodal"),  null, null, true));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("smads"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("genetranscription"), null, null, false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("smads");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("orangeArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("tgfbeta"), ArrowType.ACTIVATE,imageMap.get("redArrow"), true));
			listThree.add(new CurrentBall(2, balls[3], imageMap.get("nodal"),  null, null, true));
			listOne.add(new CurrentBall(0, balls[2], imageMap.get("genetranscription"), ArrowType.ACTIVATE, imageMap.get("orangeArrow"), false));
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
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"),  null, null, true));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("smo"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 2;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("smo");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("patched"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"), null, null, false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("gli"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
		} else if (placeHolder == 2) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 3;
			centerTitle = imageMap.get("gli");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("redArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true,imageMap.get("blueArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("smo"),  ArrowType.INHIBIT,imageMap.get("blueInhibitor"), true));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("patched"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), true));
			listThree.add(new CurrentBall(2, balls[0], imageMap.get("hedgehog"), null, null, true));
			listOne.add(new CurrentBall(0, balls[3], imageMap.get("genetranscription"), ArrowType.ACTIVATE,imageMap.get("redArrow"), false));
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
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"),  null, null, true));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("lrp"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("apc"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false));
		} else if (placeHolder == 1) {
			numberOfMolecules[0] = 3;
			numberOfMolecules[2] = 2;
			centerTitle = imageMap.get("lrp");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenArrow"), ArrowType.ACTIVATE);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"), null, null, true));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("apc"), ArrowType.INHIBIT, imageMap.get("blueInhibitor"), false));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false));
		} else if (placeHolder == 2) {
			numberOfMolecules[0] = 2;
			numberOfMolecules[2] = 3;
			centerTitle = imageMap.get("apc");
			startingArrowStrand[0] = new StartingArrow(0, false, imageMap.get("blueInhibitor"), ArrowType.INHIBIT);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"), ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"), null, null, true));
			listOne.add(new CurrentBall(0, balls[0],  imageMap.get("betacatenin"), ArrowType.ACTIVATE,imageMap.get("blueArrow"), false));
			listOne.add(new CurrentBall(0, balls[0], imageMap.get("genetranscription"), null, null, false));
		} else if (placeHolder == 3) {
			numberOfMolecules[0] = 1;
			numberOfMolecules[2] = 4;
			centerTitle =  imageMap.get("betacatenin");
			startingArrowStrand[0] = new StartingArrow(0, false,imageMap.get("blueArrow"), ArrowType.ACTIVATE);
			startingArrowStrand[2] = new StartingArrow(2, true, imageMap.get("greenInhibitor"), ArrowType.INHIBIT);
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("apc"),  ArrowType.INHIBIT, imageMap.get("greenInhibitor"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("lrp"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("frizzled"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listThree.add(new CurrentBall(2, balls[1], imageMap.get("wnt"),  ArrowType.ACTIVATE, imageMap.get("greenArrow"), true));
			listOne.add(new CurrentBall(2, balls[0], imageMap.get("genetranscription"), null, null, false));
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
		imageMap.put("gotitButton", tutorialSprites.getSubimage(0, 0, 90, 50));
		imageMap.put("missionAccomplished", tutorialSprites.getSubimage(90, 0, 470, 290));
		imageMap.put("pathwayComplete", tutorialSprites.getSubimage(560, 0, 354, 100));
		imageMap.put("pathwaySimplified", tutorialSprites.getSubimage(914, 0, 354, 100));
		imageMap.put("quitLevelButton", tutorialSprites.getSubimage(1268, 0, 90, 50));
		imageMap.put("tutorialPageOne", tutorialSprites.getSubimage(1449, 0, 470, 290));
		imageMap.put("tutorialPageTwo", tutorialSprites.getSubimage(1919, 0, 354, 202));
		imageMap.put("onwards", tutorialSprites.getSubimage(2273, 0, 90, 50));
		imageMap.put("replay", tutorialSprites.getSubimage(1358, 0, 91, 51));
	}
	
	private void createButtons() {
		Button lvlOneBut = new Button(165, 254,  imageMap.get("lvl1"),  imageMap.get("levelPressed"), -1);
		Button lvlTwoBut = new Button(300, 254,  imageMap.get("lvl2"),  imageMap.get("levelPressed"), -2);
		Button lvlThreeBut = new Button(436, 254,  imageMap.get("lvl3"),  imageMap.get("levelPressed"), -3);
		Button smBackBut = new Button(144, 378,  imageMap.get("smBackButton"),  imageMap.get("smBackPressed"), 0);
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
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void main() {
		
	}
	
	public void destroy() {
		mainLoop = null;
		ballExistence = false;
	}
	
	@Override
	public void run() {
		while(!gameOver) {
			if (!pause && playing && currentLevel > 0 && !simplifying) {
				rotate();
				moveEraser();
				moveCurrentBall();
				setCountdown();
				checkCollisions();
				repaint();
				try {
					Thread.sleep(45);
				} catch (InterruptedException e) {}
			} else if (simplifying) {
				checkSimplificationProgress();	
			} else {
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
			shooter.setxPos(currentBall.getPosition().x);
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
		drawBackground();
		drawCentralMolecule();
		drawMoleculesInPlay();
		if (!simplifying) drawCannons();
		if (!simplifying) drawEraser();
		if (!simplifying) drawProgressBar();
		drawCurrentBall();	
		if (!playing) drawMenu();
		if (pause) drawPauseScreen();
		if (pause && help) drawHelpScreen();
		if (tutorial && !playing) drawTutorialScreen();
		if (pickDifficulty && !playing) drawDifficultyScreen();
		if (playing) bufferGraphics.drawImage(imageMap.get("greyBorder"), 0, 0, this);
		g.drawImage(offscreen, 0, 0, this);
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
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawRect(0, 0, width, height); bufferGraphics.fillRect(0, 0, width, height);
		if (simplifying) bufferGraphics.drawImage(imageMap.get("tutorialPageTwo"), 115, 155, this);
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
			bufferGraphics.drawString("Score: " + score, 489 - fontMetrics.stringWidth("Score: " + score), 258);
			bufferGraphics.drawString("Completion bonus: +50", 489 - fontMetrics.stringWidth("Completion bonus: +50"), 286);
			bufferGraphics.drawString("Total R&D points earned = " + (score+50), 489 - fontMetrics.stringWidth("Total R&D points earned = " + (score+50)), 314);

			drawButtonsFromList(missionButtons);
		}		
	}
	
	private void drawButtonsFromList(ArrayList<Button> buttonList) {
		for (int i = 0; i < buttonList.size(); i++) {
			Button b = buttonList.get(i);
			if (b.isPressed()) bufferGraphics.drawImage(b.getButtonPressed(), b.getPosition().x, b.getPosition().y, this);
			else bufferGraphics.drawImage(b.getButton(), b.getPosition().x, b.getPosition().y, this);
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
			if (difficulty > 1 && (checkArrayNumber(currentBall.getStrand()) || currentLevel > 3)) bufferGraphics.drawImage(balls[4], currentBall.getPosition().x, currentBall.getPosition().y, this);
			else bufferGraphics.drawImage(currentBall.getBallImage(), currentBall.getPosition().x, currentBall.getPosition().y, this);
			bufferGraphics.drawImage(currentBall.getNameImage(), currentBall.getPosition().x + 24 - currentBall.getNameImage().getWidth()/2, currentBall.getPosition().y + 24 - currentBall.getNameImage().getHeight()/2, this);
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

		bufferGraphics.drawImage(centerTitle, 383, 288, this);
	}
	
	private void drawMoleculesFromList(ArrayList<CurrentBall> currentBallList) {
		for (int i = 0; i < currentBallList.size(); i++) {
			CurrentBall b = currentBallList.get(i);
			bufferGraphics.drawImage(b.getBallImage(), b.getPosition().x, b.getPosition().y, this);
			bufferGraphics.drawImage(b.getNameImage(), b.getPosition().x + 24 - b.getNameImage().getWidth()/2, b.getPosition().y + 24 - b.getNameImage().getHeight()/2, this);
		}
	}
	
	private void drawArrowsFromList(ArrayList<CurrentBall> currentBallList) {
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
		int totalMols = numberOfMolecules[0] + numberOfMolecules[1] + numberOfMolecules[2] + numberOfMolecules[3];
		if (totalMols > 0) return 400 / (totalMols);
		return -100;
	}
	
	@Override
	public void keyPressed(KeyEvent evt) {
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_SPACE && !simplifying && ballExistence) {
			if (currentBall != null) {
				eraserFired = true;
				currentBall.setPosChange(0, 0);
			}
		} else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) && !simplifying) {
			rotation = (rotation + 7)%8;
			rotate();
		} else if (key == KeyEvent.VK_P && !simplifying) {
			pause = !pause;
		} else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) && !simplifying) {
			rotation = (rotation + 1)%8;
			rotate();		
		} else if (key == KeyEvent.VK_X) {
			
		} else if (key == KeyEvent.VK_A) {
			
		}
		repaint();
	}
	
	public void rotate() {
		if (startingArrowStrand[0] != null) setStartingArrowPosition(startingArrowStrand[0], 6);
		if (startingArrowStrand[1] != null) setStartingArrowPosition(startingArrowStrand[1], 0);
		if (startingArrowStrand[2] != null) setStartingArrowPosition(startingArrowStrand[2], 2);
		if (startingArrowStrand[3] != null) setStartingArrowPosition(startingArrowStrand[3], 4);
		
		repositionBallsInArray(inPlayOne, 6);
		repositionBallsInArray(inPlayTwo, 0);
		repositionBallsInArray(inPlayThree, 2);
		repositionBallsInArray(inPlayFour, 4);
	}
	
	private void repositionBallsInArray(ArrayList<CurrentBall> inPlay, int strandPosition) {
		for (int i = 0; i < inPlay.size(); i++) {
			CurrentBall b = inPlay.get(i);
			setBallPosition(i, strandPosition, b);
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
			b.setArrowPos(b.getPosition().x + 35, b.getPosition().y - 2);
		}
		if ((rotation+change)%8 == 3) {
			b.setPos(434 + 52*i, 277);
			b.setImage(1);
			b.setArrowPos(b.getPosition().x + 42, b.getPosition().y + 15);
		}
		if ((rotation+change)%8 == 4) {
			b.setPos(418 + 37*i, 316 + 37*i);
			b.setImage(2);
			b.setArrowPos(b.getPosition().x + 33, b.getPosition().y + 34);
		}
		if ((rotation+change)%8 == 5) {
			b.setPos(377, 334 + 52*i);
			b.setImage(3);
			b.setArrowPos(b.getPosition().x + 15, b.getPosition().y + 42);
		}
		if ((rotation+change)%8 == 6) {
			b.setPos(335 - 37*i, 315 + 37*i);
			b.setImage(4);
			b.setArrowPos(b.getPosition().x - 2, b.getPosition().y + 33);
		}
		if ((rotation+change)%8 == 7) {
			b.setPos(320 - 52*i, 277);
			b.setImage(5);
			b.setArrowPos(b.getPosition().x - 10, b.getPosition().y + 15);
		}
		if ((rotation+change)%8 == 0) {
			b.setPos(338 - 37*i, 236 - 37*i);
			b.setImage(6);
			b.setArrowPos(b.getPosition().x - 3, b.getPosition().y - 3);
		}
		if ((rotation+change)%8 == 1) {
			b.setPos(377, 220 - 52*i);
			b.setImage(7);
			b.setArrowPos(b.getPosition().x + 15, b.getPosition().y - 10);
		}	
	}
	
	private void fireBall() {
		ballExistence = true;
		currentBall.setMove(setCannon);
	}
	
	private void increaseArray() {
		switch(strand) {
		case 0: inPlayOne.add(currentBall); break; 
		case 1: inPlayTwo.add(currentBall); break;
		case 2: inPlayThree.add(currentBall); break;
		case 3: inPlayFour.add(currentBall); break;
		}
	}
	
	private void decreaseArray(int collisionStrand, CurrentBall b) {
		switch(collisionStrand) {
		case 0: inPlayOne.remove(b); break;
		case 1: inPlayTwo.remove(b); break;
		case 2: inPlayThree.remove(b); break;
		case 3: inPlayFour.remove(b); break;
		}
	}
	
	private void checkPreviousBall(Rectangle prevRect, Rectangle ballRec, CurrentBall previousBall) {
		if (ballRec.intersects(prevRect)) {
			if (previousBall.getStrand() != currentBall.getStrand()) {
				decreaseArray(previousBall.getStrand(), previousBall);
				progress--;
				currentBall.setPosChange(0, 10);
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
				currentBall.setPosChange(0, 10);
			} else if (currentBall.getPosChange().y != 10) {
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
	
	private void checkLastInStrandCollision(ArrayList<CurrentBall> inPlay, int strandNum) {
		if (inPlay.size() > 0) {
			CurrentBall previousBall = inPlay.get(inPlay.size()-1);
			Rectangle prevRect = new Rectangle(previousBall.getPosition().x, previousBall.getPosition().y, 35, 35);
			checkPreviousBall(prevRect, currentBall.getBounds(), previousBall);
		} else if (startingArrowStrand[strandNum] != null){
			Rectangle startingArrowRect = startingArrowStrand[strandNum].getBounds();
			checkStartingArrow(currentBall.getBounds(), startingArrowRect, strandNum);
		}
	}
	
	private void checkCollisions() {		
		if (ballExistence && currentBall.getPosChange().y != 10) {
			if (ballExistence && rotation % 2 == 0) {
				checkLastInStrandCollision(inPlayOne, 0);
				checkLastInStrandCollision(inPlayTwo, 1);
				checkLastInStrandCollision(inPlayThree, 2);
				checkLastInStrandCollision(inPlayFour, 3);				
			}			
			if (eraserFired && ballExistence) {
				if (shooter.getBounds().intersects(currentBall.getBounds())) {
					currentBall.setPosChange(4, 4);
					repaint();
					getNextBall();
				}
			}
			Rectangle centerRect = new Rectangle(373, 272, imageMap.get("centerMolecule").getWidth(null) - 20, imageMap.get("centerMolecule").getHeight(null) - 20);
			if (centerRect.intersects(currentBall.getBounds())) getNextBall();
		}
		if (ballExistence) {
			if (currentBall.getPosition().y > 680 || currentBall.getPosition().y < 10) {
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
			case 0: if (inPlayOne.size() < numberOfMolecules[0]) {currentBall = listOne.get(inPlayOne.size()); newBall = true;} 
				break;
			case 1: if (inPlayTwo.size() < numberOfMolecules[1]) {currentBall = listTwo.get(inPlayTwo.size()); newBall = true;}
				break;
			case 2: if (inPlayThree.size() < numberOfMolecules[2]) {currentBall = listThree.get(inPlayThree.size()); newBall = true;}
				break;
			case 3: if (inPlayFour.size() < numberOfMolecules[3]) {currentBall = listFour.get(inPlayFour.size()); newBall = true;}
				break;
			case 4: currentBall = listFive.get(0); newBall = true; break;
			}
			
			if (newBall) {
				setCurrentBallPosition();
			}
			if (inPlayOne.size() == numberOfMolecules[0] && inPlayTwo.size() == numberOfMolecules[1] && inPlayThree.size() == numberOfMolecules[2] && inPlayFour.size() == numberOfMolecules[3]) {
				newBall = true;	
				ballExistence = false;
				rotateToPosition(4);
				interfaceNumber = 12;
				playing = false;			
			}
		}
	}
	
	private void setCurrentBallPosition() {
		currentBall.setPosChange(4, 4);
		switch (setCannon) {
		case 0: currentBall.setPos(118, 18); break;
		case 1: currentBall.setPos(635, 18); break;
		case 2: currentBall.setPos(635, 535); break;
		case 3: currentBall.setPos(118, 535); break;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {		
		checkToSetUpLevel(arg0);
	}
	
	private void checkIfButtonPressed(ArrayList<Button> buttonList, Point p) {
		for (int i = 0; i < buttonList.size(); i++) {
			Button b = buttonList.get(i);
			if (b.getBounds().contains(p)) setUpInterfaceOrLevels(b.getTarget());
		}
	}
		
	private void checkToSetUpLevel(MouseEvent arg0) {
		if (pickDifficulty) {
			checkIfButtonPressed(levelButtons, arg0.getPoint());
		} else if (tutorial) { 
			checkIfButtonPressed(tutorialButtons, arg0.getPoint());
		} else if (pause) {
			checkIfButtonPressed(pauseScreenButtons, arg0.getPoint());
		} else if (help) {
			checkIfButtonPressed(helpButtons, arg0.getPoint());
		} else if (interfaceNumber == 0) {
			checkIfButtonPressed(pathwayMissionButtons, arg0.getPoint());
		} else if (interfaceNumber == 11) {
			checkIfButtonPressed(chooseYourOwnAdventureButtons, arg0.getPoint());
		} else if (interfaceNumber == 12) {
			checkIfButtonPressed(missionButtons, arg0.getPoint());
		} else if (playing) {
			if (new Rectangle(14, 548, 88, 42).contains(arg0.getPoint())) {
				pause = !pause;
			}
		}	
	}
	
	private void setUpInterfaceOrLevels(int number) {
		interfaceNumber = number;
		if (number < 0) {
			difficulty = -number; 
			pickDifficulty = false; 
			setUpLists(currentLevel);
		}
		if (number > 0 && number < 8) {
			currentLevel = number;
			pickDifficulty = true;
		}
		switch(number) {
		case 0: pickDifficulty = false; tutorial = false; pause = false; playing = false; help = false; break;
		case 10: tutorial = true; break;  
		case 20: pickDifficulty = true; playing = false; break;
		case 25: pickDifficulty = true; currentLevel++; break;
		case 30: pause = false; help = false; break;
		case 35: tutorial = true;
		case 40: help = true;
		}
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