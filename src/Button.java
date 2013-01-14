import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class Button {
	private BufferedImage button, buttonPressed;
	private boolean pressed;
	private Rectangle bounds;
	private int target;
	private Point position;
	
	public BufferedImage getButton() {return button;}
	public BufferedImage getButtonPressed() {return buttonPressed;}
	public boolean isPressed() {return pressed;}
	public int getTarget() {return target;}
	public Point getPosition() {return position;}
	public Rectangle getBounds() {return bounds;}

	public Button(int x, int y, BufferedImage b, BufferedImage bp, int target) {
		position = new Point(x, y);
		bounds = new Rectangle(x, y, b.getWidth(), b.getHeight());
		this.target = target;
		button = b;
		buttonPressed = bp;
	}
}
