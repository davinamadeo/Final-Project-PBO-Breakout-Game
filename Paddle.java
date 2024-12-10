import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Paddle extends Sprite {
    private int dx;
    private static final int PADDLE_SPEED = 5;
    private static final Color PADDLE_COLOR = new Color(0, 150, 255);
    
    public Paddle() {
        super(Commons.INIT_PADDLE_X, Commons.INIT_PADDLE_Y,
              Commons.PADDLE_WIDTH, Commons.PADDLE_HEIGHT, PADDLE_COLOR);
    }
    
    public void move() {
        x += dx;
        
        if (x <= 0) {
            x = 0;
        }
        
        if (x >= Commons.WIDTH - width) {
            x = Commons.WIDTH - width;
        }
    }
    
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT) {
            dx = -PADDLE_SPEED;
        }
        
        if (key == KeyEvent.VK_RIGHT) {
            dx = PADDLE_SPEED;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
        
        // Add highlight effect
        g2d.setColor(color.brighter());
        g2d.fillRect(x, y, width, 3);
    }
    
    public void reset() {
        x = Commons.INIT_PADDLE_X;
        y = Commons.INIT_PADDLE_Y;
        dx = 0;
    }
}