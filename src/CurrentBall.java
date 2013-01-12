import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class CurrentBall {
	private int xPos, yPos, rotation;
	private Image ballImage;
	public BufferedImage arrowImages;
	public int arrowX, arrowY;
	public int dx = 5, dy = 5;
	public int strand;
	public BufferedImage name;
	public ArrowType simplifyType;
	private boolean flip;
	public boolean simplifiedBall = false;
	public boolean simplifiedArrow = false;
	
	public CurrentBall(int strand, Image img, BufferedImage name, ArrowType simpleType, BufferedImage imgs, boolean opposite, boolean doesNothing) {
		this.strand = strand;
		ballImage = img;
		this.name = name;
		this.simplifyType = simpleType;
		arrowImages = imgs;
		flip = opposite;
	}
	
	public void setPos(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public void setArrowPos(int x, int y) {
		arrowX = x;
		arrowY = y;
	}
	
	public int getStrand() {
		return strand;
	}
	
	public int getX() {
		return xPos;
	}
	
	public int getY() {
		return yPos;
	}
	
	public void setMove(int type) {
		if (type == 2 || type == 4) dx *= -1;
		if (type == 3 || type == 4) dy *= -1;
	}
	
	public void move() {
		setPos(this.getX() + dx, this.getY() +dy);
	}
	
	public Image getImage() {
		return ballImage;
	}
	
	public void paintArrow(Graphics2D g2d, Game game) {
		if (arrowImages != null) {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(rotation*45), arrowX + arrowImages.getWidth()/2, arrowY + arrowImages.getHeight()/2);
			affineTransform.translate(arrowX, arrowY);
			g2d.drawImage(arrowImages, affineTransform, game);
		}
	}
	
	public void setImage(int number) {
		if (flip) {
			rotation = (number+4)%8;
		} else {
			rotation = number;
		}
		
	}
	
	public int getType() {
		return strand;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(xPos, yPos, ballImage.getWidth(null), ballImage.getHeight(null));
	}
	public Rectangle getBounds2() {
		return new Rectangle(xPos, yPos, ballImage.getWidth(null), ballImage.getHeight(null));
	}
	
}
