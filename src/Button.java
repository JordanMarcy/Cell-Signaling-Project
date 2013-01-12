import java.awt.Rectangle;
import java.awt.image.BufferedImage;

	
	
	
	

public class Button {
	public BufferedImage button, buttonPressed;
	public boolean pressed;
	private Rectangle bounds;
	public int xPos, yPos, target;
	
	public Button(int x, int y, BufferedImage b, BufferedImage bp, int targ) {
		xPos = x; yPos = y;
		bounds = new Rectangle(x, y, b.getWidth(), b.getHeight());
		target = targ;
		button = b;
		buttonPressed = bp;
	}
	
	
	
	public Rectangle getBounds() {
		return bounds;
	}
}
