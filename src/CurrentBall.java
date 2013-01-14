import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class CurrentBall {
	private int strand;
	private Point position = new Point();
	private Point arrowPosition = new Point();
	private Point posChange = new Point(4, 4);
	private BufferedImage ballImage;
	private BufferedImage arrowImage;
	private BufferedImage nameImage;
	private ArrowType simplifyType;
	
	private int rotation;
	private boolean flip;
	
	public int getStrand() {return strand;}
	public Point getPosition() {return position;}
	public Point getPosChange() {return posChange;}
	public Point getArrowPosition() {return arrowPosition;}
	public BufferedImage getBallImage() {return ballImage;}
	public BufferedImage getNameImage() {return nameImage;}
	public Rectangle getBounds() {return new Rectangle(position.x, position.y, ballImage.getWidth(null), ballImage.getHeight(null));}	
	public ArrowType getSimplifyType() {return simplifyType;}
	
	public CurrentBall(int strand, BufferedImage ballImage, BufferedImage nameImage, ArrowType simpleType, BufferedImage arrowImage, boolean flip) {
		this.strand = strand;
		this.ballImage = ballImage;
		this.nameImage = nameImage;
		this.simplifyType = simpleType;
		this.arrowImage = arrowImage;
		this.flip = flip;
	}
	
	public void setPos(int x, int y) {
		position.x = x;
		position.y = y;
	}
	
	public void setPosChange(int x, int y) {
		position = new Point(x, y);
	}
	
	public void setArrowImage(BufferedImage arrowImg) {
		arrowImage = arrowImg;
	}
	
	public void setArrowPos(int x, int y) {
		arrowPosition.x = x;
		arrowPosition.y = y;
	}
	
	public void setMove(int type) {
		if (type == 1 || type == 2) posChange.x *= -1;
		if (type == 3 || type == 2) posChange.y *= -1;
	}
	
	public void move() {
		position.x += posChange.x;
		position.y += posChange.y;
	}
	
	public void paintArrow(Graphics2D g2d, Game game) {
		if (arrowImage != null) {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(rotation*45), arrowPosition.x + arrowImage.getWidth()/2, arrowPosition.y + arrowImage.getHeight()/2);
			affineTransform.translate(arrowPosition.x, arrowPosition.y);
			g2d.drawImage(arrowImage, affineTransform, game);
		}
	}
	
	public void setImage(int number) {
		if (flip) rotation = (number+4)%8;
		else rotation = number;
	}
		
}
