public interface Commons {
    // Window settings
    int WIDTH = 800;
    int HEIGHT = 600;
    int BOTTOM_EDGE = HEIGHT - 10;
    
    // Game settings
    int PERIOD = 10;  // Timer delay in milliseconds
    int N_OF_BRICKS_PER_ROW = 10;
    int N_OF_ROWS = 5;
    int N_OF_BRICKS = N_OF_BRICKS_PER_ROW * N_OF_ROWS;
    
    // Initial positions
    int INIT_PADDLE_X = WIDTH / 2 - 50;
    int INIT_PADDLE_Y = BOTTOM_EDGE - 20;
    int INIT_BALL_X = WIDTH / 2 - 5;
    int INIT_BALL_Y = INIT_PADDLE_Y - 20;
    
    // Dimensions
    int BRICK_WIDTH = 70;
    int BRICK_HEIGHT = 20;
    int PADDLE_WIDTH = 100;
    int PADDLE_HEIGHT = 10;
    int BALL_SIZE = 10;
    
    // Colors (for fallback shapes)
    java.awt.Color[] BRICK_COLORS = {
        new java.awt.Color(200, 0, 0),    // Red
        new java.awt.Color(0, 200, 0),    // Green
        new java.awt.Color(0, 0, 200),    // Blue
        new java.awt.Color(200, 200, 0),  // Yellow
        new java.awt.Color(200, 0, 200)   // Purple
    };
}