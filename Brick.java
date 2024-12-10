import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GradientPaint;

public class Brick extends Sprite {
    private int strength;
    private int score;
    
    public Brick(int x, int y, Color color, int strength) {
        super(x, y, Commons.BRICK_WIDTH, Commons.BRICK_HEIGHT, color);
        this.strength = strength;
        this.score = strength * 10; // Score based on brick strength
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        if (!destroyed) {
            // Create gradient effect
            Color lighterColor = new Color(
                Math.min(color.getRed() + 50, 255),
                Math.min(color.getGreen() + 50, 255),
                Math.min(color.getBlue() + 50, 255)
            );
            
            // Create vertical gradient from lighter to normal color
            GradientPaint gradient = new GradientPaint(
                x, y, lighterColor,
                x, y + height, color
            );
            
            // Fill brick with gradient
            g2d.setPaint(gradient);
            g2d.fillRect(x, y, width, height);
            
            // Draw strength indicator for bricks with strength > 1
            if (strength > 1) {
                g2d.setColor(Color.WHITE);
                String str = String.valueOf(strength);
                // Center the text
                int stringWidth = g2d.getFontMetrics().stringWidth(str);
                g2d.drawString(str, 
                             x + (width - stringWidth) / 2, 
                             y + (height + 10) / 2);
            }
            
            // Add a darker border for definition
            g2d.setColor(color.darker());
            g2d.drawRect(x, y, width, height);
        }
    }
    
    public boolean hit() {
        strength--;
        if (strength <= 0) {
            destroyed = true;
            return true; // Returns true if brick is destroyed
        }
        return false;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getStrength() {
        return strength;
    }
    
    public void setStrength(int strength) {
        this.strength = strength;
        this.score = strength * 10;
    }
}