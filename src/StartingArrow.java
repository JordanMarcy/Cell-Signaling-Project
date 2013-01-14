import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class StartingArrow {
	private Point position = new Point(0, 0);
	private int strand;
	private int rotation;
	private boolean flip;
	private BufferedImage arrowImage;
	private ArrowType arrowType;
	
	public Point getPosition() {return position;}
	public int getStrand() {return strand;}
	public BufferedImage getArrowImage() {return arrowImage;}
	public ArrowType getArrowType() {return arrowType;}
	public Rectangle getBounds() {return new Rectangle(position.x, position.y, arrowImage.getWidth(null), arrowImage.getHeight(null));}

	public StartingArrow(int strand, boolean flip, BufferedImage imgs, ArrowType arrowType) {
		this.strand = strand;	
		this.flip = flip;
		arrowImage = imgs;
		this.arrowType = arrowType;
	}
	
	public void setPos(int x, int y) {
		position.x = x;
		position.y = y;
	}
	
	public void paintArrow(Graphics2D g2d, Game game) {
		if (arrowImage != null) {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(rotation*45), position.x + arrowImage.getWidth()/2, position.y + arrowImage.getHeight()/2);
			affineTransform.translate(position.x, position.y);
			g2d.drawImage(arrowImage, affineTransform, game);
		}
	}
	
	public void setImage(int num) {
		if (flip) rotation = (num + 4)%8;
		else rotation = num;
	}
	
}
