import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class CurrentBall {
	private int strand; //Denotes which set of balls this one belongs to
	private Point position = new Point();
	private Point arrowPosition = new Point(); //Position of the ball's arrow
	private Point posChange = new Point(4, 4); //Movement of the ball
	private BufferedImage ballImage;
	private BufferedImage arrowImage;
	private BufferedImage nameImage;
	private String arrowColor; //Used to get the correct color of arrow if the type of arrow changes
	private ArrowType simplifyType; //Denotes type of arrow
	
	private int rotation;
	private boolean flip;
	
	//Getters
	public int getStrand() {return strand;}
	public Point getPosition() {return position;}
	public Point getPosChange() {return posChange;}
	public Point getArrowPosition() {return arrowPosition;}
	public BufferedImage getBallImage() {return ballImage;}
	public BufferedImage getNameImage() {return nameImage;}
	public BufferedImage getArrowImage() {return arrowImage;}
	public Rectangle getBounds() {return new Rectangle(position.x, position.y, ballImage.getWidth(null), ballImage.getHeight(null));}	
	public ArrowType getSimplifyType() {return simplifyType;}
	public String getArrowColor() {return arrowColor;}
	public boolean isFlip() {return flip;}
	
	//Setters
	public void setPos(int x, int y) {position = new Point(x, y);}
	public void setPosChange(int x, int y) {posChange = new Point(x, y);}
	public void setArrowImage(BufferedImage arrowImg) {arrowImage = arrowImg;}
	public void setArrowPos(int x, int y) {arrowPosition = new Point(x, y);}
	public void setSimplifyType(ArrowType sType) {simplifyType = sType;}
	public void setMove(int type) {
		if (type == 1 || type == 2) posChange.x *= -1;
		if (type == 3 || type == 2) posChange.y *= -1;
	}
	public void setArrowImageRotation(int number) {
		if (flip) rotation = (number+4)%8;
		else rotation = number;
	}
	
	public void move() {
		position.x += posChange.x;
		position.y += posChange.y;
	}
	
	public CurrentBall(int strand, BufferedImage ballImage, BufferedImage nameImage, ArrowType simpleType, BufferedImage arrowImage, boolean flip, String color) {
		this.strand = strand;
		this.ballImage = ballImage;
		this.nameImage = nameImage;
		this.simplifyType = simpleType;
		this.arrowImage = arrowImage;
		this.flip = flip;
		arrowColor = color;
	}
	
	//Allows for the cloning of a ball.
	public CurrentBall(CurrentBall ball) {
		this.strand = ball.getStrand();
		this.ballImage = ball.getBallImage();
		this.nameImage = ball.getNameImage();
		this.simplifyType = ball.getSimplifyType();
		this.arrowImage = ball.getArrowImage();
		this.flip = ball.isFlip();
		this.arrowColor = ball.getArrowColor();
		this.position = ball.getPosition();
	}
	

	
	//Paints the arrow by first rotating the image according to the arrowRotation
	public void paintArrow(Graphics2D g2d, Game game) {
		if (arrowImage != null) {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(rotation*45), arrowPosition.x + arrowImage.getWidth()/2, arrowPosition.y + arrowImage.getHeight()/2);
			affineTransform.translate(arrowPosition.x, arrowPosition.y);
			g2d.drawImage(arrowImage, affineTransform, game);
		}
	}
	

		
}
