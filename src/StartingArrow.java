import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class StartingArrow {
	public int x, y;
	public int strand;
	private int rotation;
	private boolean flip;
	public BufferedImage arrowImages;
	public ArrowType arrowType;
	
	public StartingArrow(int strand, boolean flip, BufferedImage imgs, ArrowType arrowType) {
		this.strand = strand;	
		this.flip = flip;
		arrowImages = imgs;
		this.arrowType = arrowType;
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void paintArrow(Graphics2D g2d, Game game) {
		if (arrowImages != null) {
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(rotation*45), x + arrowImages.getWidth()/2, y + arrowImages.getHeight()/2);
			affineTransform.translate(x, y);
			g2d.drawImage(arrowImages, affineTransform, game);
		}
	}
	
	public void setImage(int num) {
		if (flip) {
			rotation = (num + 4)%8;
		} else {
			rotation = num;
		}
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, arrowImages.getWidth(null), arrowImages.getHeight(null));
	}
}
