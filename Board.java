import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Board extends JPanel {
    private Timer timer;
    private Timer randomizeTimer;
    private Ball ball;
    private Paddle paddle;
    private Brick[][] bricks;
    private boolean inGame = true;
    private int score = 0;
    private int lives = 3;
    private Random random = new Random();
    private static final int RANDOMIZE_DELAY = 20000; // 20 seconds

    private static final Color[] BRICK_PATTERNS = {
        new Color(255, 0, 0),    // Red
        new Color(0, 255, 0),    // Green
        new Color(0, 0, 255),    // Blue
        new Color(255, 255, 0),  // Yellow
        new Color(255, 0, 255),  // Magenta
        new Color(0, 255, 255),  // Cyan
        new Color(255, 165, 0),  // Orange
        new Color(128, 0, 128)   // Purple
    };
    
    public Board() {
        initBoard();
        setupRandomizeTimer();
    }
    
    private void initBoard() {
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
        addKeyListener(new TAdapter());
        gameInit();
    }

    private void setupRandomizeTimer() {
        randomizeTimer = new Timer(RANDOMIZE_DELAY, e -> {
            if (inGame) {
                randomizeCurrentBricks();
            }
        });
        randomizeTimer.start();
    }
    
    private void gameInit() {
        bricks = new Brick[Commons.N_OF_ROWS][Commons.N_OF_BRICKS_PER_ROW];
        ball = new Ball();
        paddle = new Paddle();
        initBricks();
        
        timer = new Timer(Commons.PERIOD, new GameCycle());
        timer.start();
    }
    
    private void initBricks() {
        int patternType = random.nextInt(4);
        
        switch (patternType) {
            case 0:
                createRandomColorPattern();
                break;
            case 1:
                createCheckerboardPattern();
                break;
            case 2:
                createRandomGapsPattern();
                break;
            case 3:
                createWavePattern();
                break;
        }
    }

    private void createRandomColorPattern() {
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                Color randomColor = BRICK_PATTERNS[random.nextInt(BRICK_PATTERNS.length)];
                int x = j * (Commons.BRICK_WIDTH + 1) + 30;
                int y = i * (Commons.BRICK_HEIGHT + 1) + 50;
                bricks[i][j] = new Brick(x, y, randomColor, random.nextInt(3) + 1);
            }
        }
    }

    private void createCheckerboardPattern() {
        Color color1 = BRICK_PATTERNS[random.nextInt(BRICK_PATTERNS.length)];
        Color color2 = BRICK_PATTERNS[random.nextInt(BRICK_PATTERNS.length)];
        
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                int x = j * (Commons.BRICK_WIDTH + 1) + 30;
                int y = i * (Commons.BRICK_HEIGHT + 1) + 50;
                if ((i + j) % 2 == 0) {
                    bricks[i][j] = new Brick(x, y, color1, 1);
                } else {
                    bricks[i][j] = new Brick(x, y, color2, 2);
                }
            }
        }
    }

    private void createRandomGapsPattern() {
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                int x = j * (Commons.BRICK_WIDTH + 1) + 30;
                int y = i * (Commons.BRICK_HEIGHT + 1) + 50;
                if (random.nextFloat() < 0.8f) {
                    Color randomColor = BRICK_PATTERNS[random.nextInt(BRICK_PATTERNS.length)];
                    bricks[i][j] = new Brick(x, y, randomColor, 1);
                } else {
                    bricks[i][j] = new Brick(x, y, Color.BLACK, 0);
                    bricks[i][j].setDestroyed(true);
                }
            }
        }
    }

    private void createWavePattern() {
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                double wave = Math.sin(j * 0.5) * 20;
                int x = j * (Commons.BRICK_WIDTH + 1) + 30;
                int y = i * (Commons.BRICK_HEIGHT + 1) + 50 + (int)wave;
                
                int colorIndex = (i + (int)(wave / 20)) % BRICK_PATTERNS.length;
                if (colorIndex < 0) colorIndex += BRICK_PATTERNS.length;
                
                bricks[i][j] = new Brick(x, y, BRICK_PATTERNS[colorIndex], 
                              Math.abs((int)(wave / 10)) + 1);
            }
        }
    }

    private void randomizeCurrentBricks() {
        int remainingBricks = 0;
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                if (!bricks[i][j].isDestroyed()) {
                    remainingBricks++;
                }
            }
        }

        if (remainingBricks > 0) {
            initBricks();
            int bricksToDestroy = (Commons.N_OF_ROWS * Commons.N_OF_BRICKS_PER_ROW) - remainingBricks;
            while (bricksToDestroy > 0) {
                int i = random.nextInt(Commons.N_OF_ROWS);
                int j = random.nextInt(Commons.N_OF_BRICKS_PER_ROW);
                if (!bricks[i][j].isDestroyed()) {
                    bricks[i][j].setDestroyed(true);
                    bricksToDestroy--;
                }
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (inGame) {
            drawObjects(g2d);
        } else {
            gameFinished(g2d);
        }
    }
    
    private void drawObjects(Graphics2D g2d) {
        ball.draw(g2d);
        paddle.draw(g2d);
        
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                if (!bricks[i][j].isDestroyed()) {
                    bricks[i][j].draw(g2d);
                }
            }
        }
        
        drawScore(g2d);
        drawLives(g2d);
    }
    
    private void drawScore(Graphics2D g2d) {
        String scoreText = "Score: " + score;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        g2d.drawString(scoreText, 10, 20);
    }
    
    private void drawLives(Graphics2D g2d) {
        String livesText = "Lives: " + lives;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        g2d.drawString(livesText, Commons.WIDTH - 100, 20);
    }
    
    private void gameFinished(Graphics2D g2d) {
        String message = "Game Over - Score: " + score;
        if (victory()) {
            message = "Victory! Score: " + score;
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(message);
        g2d.drawString(message, (Commons.WIDTH - msgWidth) / 2, Commons.HEIGHT / 2);
        
        String restartMsg = "Press SPACE to play again";
        g2d.setFont(new Font("Verdana", Font.PLAIN, 18));
        fm = g2d.getFontMetrics();
        msgWidth = fm.stringWidth(restartMsg);
        g2d.drawString(restartMsg, (Commons.WIDTH - msgWidth) / 2, Commons.HEIGHT / 2 + 30);
    }
    
    private boolean victory() {
        for (int i = 0; i < Commons.N_OF_ROWS; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW; j++) {
                if (!bricks[i][j].isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
            
            if (!inGame && e.getKeyCode() == KeyEvent.VK_SPACE) {
                restartGame();
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
    }
    
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }
    
    private void doGameCycle() {
        ball.move();
        paddle.move();
        checkCollision();
        repaint();
    }
    
    private void checkCollision() {
        if (ball.getY() > Commons.BOTTOM_EDGE) {
            lives--;
            if (lives <= 0) {
                stopGame();
            } else {
                ball.reset();
                paddle.reset();
                initBricks();
                randomizeTimer.restart();
            }
            return;
        }
        
        // Paddle collision
        if (ball.getBounds().intersects(paddle.getBounds())) {
            // Ensure the ball doesn't get stuck in the paddle
            ball.setY(paddle.getY() - ball.getHeight());
            
            // Calculate angle based on where ball hits the paddle
            double relativeIntersectX = (paddle.getX() + (paddle.getWidth() / 2.0)) - 
                                      (ball.getX() + (ball.getWidth() / 2.0));
            double normalizedRelativeIntersectionX = relativeIntersectX / (paddle.getWidth() / 2.0);
            
            ball.reverseY();
        }
        
        // Brick collision
        boolean collisionDetected = false;
        
        for (int i = 0; i < Commons.N_OF_ROWS && !collisionDetected; i++) {
            for (int j = 0; j < Commons.N_OF_BRICKS_PER_ROW && !collisionDetected; j++) {
                if (!bricks[i][j].isDestroyed() && ball.getBounds().intersects(bricks[i][j].getBounds())) {
                    // Get the collision bounds
                    int ballLeft = ball.getX();
                    int ballRight = ballLeft + ball.getWidth();
                    int ballTop = ball.getY();
                    int ballBottom = ballTop + ball.getHeight();
                    
                    int brickLeft = bricks[i][j].getX();
                    int brickRight = brickLeft + bricks[i][j].getWidth();
                    int brickTop = bricks[i][j].getY();
                    int brickBottom = brickTop + bricks[i][j].getHeight();
                    
                    // Calculate overlap on each side
                    int overlapLeft = ballRight - brickLeft;
                    int overlapRight = brickRight - ballLeft;
                    int overlapTop = ballBottom - brickTop;
                    int overlapBottom = brickBottom - ballTop;
                    
                    // Find the smallest overlap
                    int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), 
                                            Math.min(overlapTop, overlapBottom));
                    
                    // Adjust ball position and direction based on collision side
                    if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                        ball.reverseX();
                        // Adjust x position to prevent sticking
                        if (minOverlap == overlapLeft) {
                            ball.setX(brickLeft - ball.getWidth());
                        } else {
                            ball.setX(brickRight);
                        }
                    } else {
                        ball.reverseY();
                        // Adjust y position to prevent sticking
                        if (minOverlap == overlapTop) {
                            ball.setY(brickTop - ball.getHeight());
                        } else {
                            ball.setY(brickBottom);
                        }
                    }
                    
                    // Handle brick hit
                    if (bricks[i][j].hit()) {
                        score += bricks[i][j].getScore();
                    }
                    
                    collisionDetected = true;
                    break;
                }
            }
        }
    }
    
    private void stopGame() {
        inGame = false;
        timer.stop();
        randomizeTimer.stop();
    }
    
    private void restartGame() {
        score = 0;
        lives = 3;
        inGame = true;
        
        ball.reset();
        paddle.reset();
        initBricks();
        
        timer.start();
        randomizeTimer.restart();
        requestFocus();
    }
}