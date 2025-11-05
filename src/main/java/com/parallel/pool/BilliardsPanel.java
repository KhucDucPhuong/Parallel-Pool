package com.parallel.pool; 

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class BilliardsPanel extends JPanel {
    private final List<Ball> balls = new ArrayList<>();
    private ScheduledExecutorService gameLoopExecutor;
    private ScheduledExecutorService timerExecutor; 
    
    private volatile boolean gameEnded = false; 
    
    private final int HOLE_RADIUS = 20; 
    private Point holePosition;
    private long startTime;
    private int GAME_DURATION_SECONDS = 20; 
    private int TABLE_WIDTH;
    private int TABLE_HEIGHT;
    private int initialBallCount;
    private int mapId; 
    private Integer firstBallInHole = null; 
    
    private int currentScore = 0; // ĐIỂM SỐ HIỆN TẠI (POINTS)
    private int scoreGoal = 0;    // ĐIỂM MỤC TIÊU
    
    private MainMenu mainMenu; 

    private static final int CELL_SIZE = Ball.RADIUS * 4; 
    private int numCols;
    private int numRows;
    private List<Ball>[][] grid; 

    // CONSTRUCTOR
    public BilliardsPanel(int width, int height, int ballCount, int timeLimit, MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.initialBallCount = ballCount;
        this.GAME_DURATION_SECONDS = timeLimit; 
        
        setupControlButtons();
    }
    
    public void setTimeLimit(int timeLimit) {
        this.GAME_DURATION_SECONDS = timeLimit;
    }
    
    public void setInitialBallCount(int count) {
        this.initialBallCount = count;
    }

    private void setupControlButtons() {
        setLayout(new BorderLayout()); 
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setPreferredSize(new Dimension(800, 40)); 
        
        JButton restartBtn = new JButton("♻ Restart");
        restartBtn.addActionListener(e -> loadMap(this.mapId)); 
        controlPanel.add(restartBtn);

        JButton exitMenuBtn = new JButton("🚪 Exit to Menu");
        exitMenuBtn.addActionListener(e -> exitToMenu());
        controlPanel.add(exitMenuBtn);
        
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void exitToMenu() {
        gameEnded = true; 
        
        if (gameLoopExecutor != null) {
            gameLoopExecutor.shutdownNow(); 
        }
        if (timerExecutor != null) {
            timerExecutor.shutdownNow(); 
        }
        
        for (Ball ball : balls) {
            ball.stopMoving();
        }
        removeAll();
        revalidate();
        repaint();
        mainMenu.showMenu();
    }
    
    public void loadMap(int mapId) {
        gameEnded = false; 
        
        if (gameLoopExecutor != null) {
            gameLoopExecutor.shutdownNow();
        }
        if (timerExecutor != null) {
            timerExecutor.shutdownNow();
        }
        
        for (Ball ball : balls) {
            ball.stopMoving();
        }
        
        balls.clear();
        this.mapId = mapId;
        this.firstBallInHole = null; 
        this.currentScore = 0; 
        this.scoreGoal = 0; 

        switch (mapId) {
            case 1: 
                this.TABLE_WIDTH = 800;
                this.TABLE_HEIGHT = 600;
                this.holePosition = new Point(TABLE_WIDTH / 2, TABLE_HEIGHT / 2);
                initializeBalls(this.initialBallCount); 
                this.scoreGoal = 20; 
                break;
            case 2: 
                this.TABLE_WIDTH = 600;
                this.TABLE_HEIGHT = 400;
                this.holePosition = new Point(50, 50); 
                initializeBalls(this.initialBallCount); 
                this.scoreGoal = 50; 
                break;
            case 3: 
                this.TABLE_WIDTH = 800;
                this.TABLE_HEIGHT = 500; 
                this.holePosition = new Point(TABLE_WIDTH / 2, TABLE_HEIGHT / 2);
                initializeBalls(this.initialBallCount); 
                this.scoreGoal = 100; 
                break;
        }
        
        setupGrid(); 
        
        setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT + 40)); 
        setBackground(new Color(0, 100, 0));
        revalidate();
        repaint();
        startGameLoop();
    }
    
    private void setupGrid() {
        numCols = (int) Math.ceil((double) TABLE_WIDTH / CELL_SIZE);
        numRows = (int) Math.ceil((double) TABLE_HEIGHT / CELL_SIZE);
        grid = new ArrayList[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    private void initializeBalls(int count) {
        int actualCount = Math.min(count, 300); 

        Random rand = new Random();
        for (int i = 1; i <= actualCount; i++) {
            int x = rand.nextInt(TABLE_WIDTH - 4 * Ball.RADIUS) + 2 * Ball.RADIUS;
            int y = rand.nextInt(TABLE_HEIGHT - 4 * Ball.RADIUS) + 2 * Ball.RADIUS;
            
            Ball newBall = new Ball(x, y, i, TABLE_WIDTH, TABLE_HEIGHT);
            balls.add(newBall);
        }
        
        for (Ball ball : balls) {
             Executors.newSingleThreadExecutor().execute(ball);
        }
    }

    private void startGameLoop() {
        startTime = System.currentTimeMillis();
        
        if (gameLoopExecutor != null && !gameLoopExecutor.isShutdown()) {
             gameLoopExecutor.shutdownNow();
        }
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
             timerExecutor.shutdownNow();
        }
        
        gameLoopExecutor = Executors.newSingleThreadScheduledExecutor();
        timerExecutor = Executors.newSingleThreadScheduledExecutor();

        gameLoopExecutor.scheduleAtFixedRate(() -> {
            updateGame();
            repaint();
        }, 0, 16, TimeUnit.MILLISECONDS);

        timerExecutor.schedule(() -> {
            stopGame();
        }, GAME_DURATION_SECONDS, TimeUnit.SECONDS); 
    }

    private void updateGame() {
        updateGrid(); 
        
        for (Ball ball : balls) {
            ball.updatePosition(); 
        }

        handleBallCollisions();

        handleHoleLogic();
    }
    
    private void updateGrid() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j].clear();
            }
        }
        
        for (Ball ball : balls) {
            int col = (int) (ball.getX() / CELL_SIZE);
            int row = (int) (ball.getY() / CELL_SIZE);
            
            if (row >= 0 && row < numRows && col >= 0 && col < numCols) {
                grid[row][col].add(ball);
            }
        }
    }

    private void handleBallCollisions() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                
                checkCollisionInCell(grid[r][c]);

                if (c + 1 < numCols) {
                    checkCollisionBetweenCells(grid[r][c], grid[r][c + 1]);
                }
                
                if (r + 1 < numRows) {
                    checkCollisionBetweenCells(grid[r][c], grid[r + 1][c]);
                    
                    if (c + 1 < numCols) {
                        checkCollisionBetweenCells(grid[r][c], grid[r + 1][c + 1]);
                    }
                    
                    if (c - 1 >= 0) {
                        checkCollisionBetweenCells(grid[r][c], grid[r + 1][c - 1]);
                    }
                }
            }
        }
    }
    
    private void checkCollisionInCell(List<Ball> cellBalls) {
        for (int i = 0; i < cellBalls.size(); i++) {
            for (int j = i + 1; j < cellBalls.size(); j++) {
                resolveCollision(cellBalls.get(i), cellBalls.get(j));
            }
        }
    }
    
    private void checkCollisionBetweenCells(List<Ball> listA, List<Ball> listB) {
        for (Ball ballA : listA) {
            for (Ball ballB : listB) {
                resolveCollision(ballA, ballB);
            }
        }
    }

    private void resolveCollision(Ball ballA, Ball ballB) {
        double dx = ballB.getX() - ballA.getX();
        double dy = ballB.getY() - ballA.getY();
        double distance = Math.hypot(dx, dy);

        if (distance < ballA.getRadius() + ballB.getRadius()) {
            
            double overlap = (ballA.getRadius() + ballB.getRadius()) - distance;
            double angle = Math.atan2(dy, dx);
            
            ballA.x -= (overlap / 2) * Math.cos(angle);
            ballA.y -= (overlap / 2) * Math.sin(angle);
            ballB.x += (overlap / 2) * Math.cos(angle);
            ballB.y += (overlap / 2) * Math.sin(angle);

            double v1x = ballA.getVX(); double v1y = ballA.getVY();
            double v2x = ballB.getVX(); double v2y = ballB.getVY();

            double nx = dx / distance;
            double ny = dy / distance;

            double p = 2 * (nx * (v1x - v2x) + ny * (v1y - v2y)) / 2;
            
            ballA.setVX(v1x - p * nx);
            ballA.setVY(v1y - p * ny);
            ballB.setVX(v2x + p * nx);
            ballB.setVY(v2y + p * ny);
        }
    }
    
    private void handleHoleLogic() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            double dx = ball.getX() - holePosition.x;
            double dy = ball.getY() - holePosition.y;
            double distance = Math.hypot(dx, dy);

            if (distance < HOLE_RADIUS - ball.getRadius()/2) {
                if (firstBallInHole == null) {
                    firstBallInHole = ball.getNumber();
                }
                
                // Cập nhật điểm: SỬ DỤNG GIÁ TRỊ ĐIỂM THỰC TẾ
                currentScore += ball.getScoreValue(); 
                
                System.out.println("Ball " + ball.getNumber() + " fell into the hole. Value: " + ball.getScoreValue());
                ball.stopMoving(); 
                iterator.remove(); 
            }
        }
    }

    private void stopGame() {
        if (gameEnded) {
            return;
        }
        
        gameEnded = true; 

        if (gameLoopExecutor != null) {
            gameLoopExecutor.shutdownNow();
        }
        if (timerExecutor != null) {
            timerExecutor.shutdownNow();
        }
        
        for (Ball ball : balls) {
            ball.stopMoving();
        }
        
        final int finalScore = currentScore; 
        final int finalGoal = scoreGoal;
        final int finalMapId = this.mapId;
        
        boolean isWinner = finalScore >= finalGoal;
        
        String title;
        String message;
        Object[] options = new Object[]{"Exit to Menu", "Play Again"}; // Giữ nguyên options
        
        if (isWinner) {
             title = "🎉 WINNER! Challenge Completed";
             message = "Congratulations! You reached the score goal!";
        } else {
             title = "Game Over";
             message = "Time is up! You did not reach the score goal.";
        }
        
        message += "\n==================================";
        message += "\nScore Goal: " + finalGoal;
        message += "\nYOUR FINAL SCORE: " + finalScore + " points.";
        // ĐÃ XÓA Balls Remaining
        message += "\nTime Limit: " + GAME_DURATION_SECONDS + "s";
        message += "\n==================================";
        
        final String finalMessage = message;
        
        SwingUtilities.invokeLater(() -> {
             int choice = JOptionPane.showOptionDialog(
                 this, 
                 finalMessage, 
                 title, 
                 JOptionPane.YES_NO_OPTION, 
                 JOptionPane.INFORMATION_MESSAGE, 
                 null, 
                 options, 
                 "Exit to Menu"
             );
             
             if (choice == JOptionPane.YES_OPTION) { 
                 exitToMenu(); 
             } else { 
                 loadMap(finalMapId); 
             }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // --- VẼ BÀN BI-A VÀ BÓNG ---
        
        // Vẽ nền bàn bi-a (bắt đầu từ 40px xuống)
        g.setColor(new Color(0, 100, 0));
        g.fillRect(0, 40, TABLE_WIDTH, TABLE_HEIGHT); 

        // Vẽ lỗ
        g.setColor(Color.BLACK);
        g.fillOval(holePosition.x - HOLE_RADIUS, holePosition.y - HOLE_RADIUS + 40, 2 * HOLE_RADIUS, 2 * HOLE_RADIUS);
        
        // Vẽ bóng
        synchronized (balls) {
            for (Ball ball : balls) {
                g.setColor(ball.getColor()); 
                g.fillOval((int)ball.x - Ball.RADIUS, (int)ball.y - Ball.RADIUS + 40, 2 * Ball.RADIUS, 2 * Ball.RADIUS);
                
                g.setColor(Color.WHITE); 
                g.drawString(String.valueOf(ball.getNumber()), (int)ball.x - 4, (int)ball.y + 4 + 40); 
            }
        }
        
        // --- HIỂN THỊ HUD TRÊN BÀN BI-A (Đã được sắp xếp và loại bỏ Balls Remaining) ---
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = GAME_DURATION_SECONDS - (elapsed / 1000);
        
        g.setColor(Color.WHITE); 
        g.setFont(new Font("Arial", Font.BOLD, 16)); 
        
        int yTime = 55;
        int yScore = 75;
        
        // 1. Time Remaining (Góc trên bên trái)
        g.drawString("Time Remaining: " + remaining + "s", 10, yTime); 
        
        // 2. Score (Bên dưới Time) - HIỂN THỊ POINTS
        g.drawString("Score: " + currentScore + " / " + scoreGoal, 10, yScore);
        
        // 3. Map ID (Góc trên bên phải)
        String mapName = "Map: " + mapId;
        if (mapId == 1) mapName += " (Center Hole)";
        if (mapId == 2) mapName += " (Corner Hole)";
        if (mapId == 3) mapName += " (Center Hole)";
        
        int mapX = TABLE_WIDTH - g.getFontMetrics().stringWidth(mapName) - 10;
        g.drawString(mapName, mapX, yTime);
    }
}