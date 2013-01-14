import java.awt.Point;
import java.awt.Rectangle;



public class Shooter {
	private int xPos = 300, yPos = 530;
	
	public Shooter() {
		xPos = 300;
	}
	
	public void setxPos(int ballX) {
		xPos = ballX;
	}
	
	public void setyPos() {
		yPos = 530;
	}
	
	public void moveUp() {
		yPos -= 20;
	}
	
	public Point getPos() {
		return new Point(xPos, yPos);
	}
	
	public Rectangle getBounds() {
		return new Rectangle(xPos, yPos+30, 47, 49);
	}
	
}
