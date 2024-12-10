import java.awt.Color;
import java.awt.Graphics2D;

public class Ball extends Sprite {
    private double dx;
    private double dy;
    private static final double INITIAL_SPEED = 3.0;
    private static final double MAX_SPEED = 7.0;
    private static final Color BALL_COLOR = Color.WHITE;
    
    public Ball() {
        super(Commons.INIT_BALL_X, Commons.INIT_BALL_Y, 
              Commons.BALL_SIZE, Commons.BALL_SIZE, BALL_COLOR);
        initBall();
    }
    
    private void initBall() {
        double angle = Math.toRadians(45 + Math.random() * 90); // Random angle between 45 and 135 degrees
        dx = INITIAL_SPEED * Math.cos(angle);
        dy = -INITIAL_SPEED * Math.sin(angle);
    }
    
    public void move() {
        x += dx;
        y += dy;
        
        // Wall collisions
        if (x <= 0 || x >= Commons.WIDTH - width) {
            dx = -dx;
        }
        
        if (y <= 0) {
            dy = -dy;
        }
    }
    
    public void reverseY() {
        dy = -dy;
        increaseSpeed();
    }
    
    public void reverseX() {
        dx = -dx;
        increaseSpeed();
    }
    
    private void increaseSpeed() {
        double speed = Math.sqrt(dx * dx + dy * dy);
        if (speed < MAX_SPEED) {
            dx *= 1.05;
            dy *= 1.05;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval(x, y, width, height);
    }
    
    public void reset() {
        x = Commons.INIT_BALL_X;
        y = Commons.INIT_BALL_Y;
        initBall();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}