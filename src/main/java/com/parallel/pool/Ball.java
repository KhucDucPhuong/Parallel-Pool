package com.parallel.pool;

import java.awt.Color;
import java.util.Random;

public class Ball implements Runnable {
    
    public static final int RADIUS = 10; 

    public double x, y;
    public double vx, vy;
    
    private final Color color;
    private final int number;
    private volatile boolean isMoving = true;
    private final int panelWidth;
    private final int panelHeight;
    private final int scoreValue; 
    
    // CONSTRUCTOR: Tăng tốc độ ban đầu
    public Ball(double x, double y, int number, int panelWidth, int panelHeight) {
        this.x = x;
        this.y = y;
        this.number = number;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.color = generateRandomColor();
        
        // TĂNG TỐC ĐỘ BAN ĐẦU: Từ 5 đến 8 (trước đây là 2 đến 5)
        Random rand = new Random();
        double angle = rand.nextDouble() * 2 * Math.PI;
        double initialSpeed = 5 + rand.nextDouble() * 3; // Tốc độ ngẫu nhiên từ 5 đến 8
        
        this.vx = initialSpeed * Math.cos(angle);
        this.vy = initialSpeed * Math.sin(angle);
        
        this.scoreValue = number; 
    }

    // --- GETTERS ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVX() { return vx; }
    public double getVY() { return vy; }
    public int getNumber() { return number; }
    public int getRadius() { return RADIUS; }
    public Color getColor() { return color; } 

    public int getScoreValue() {
        return scoreValue;
    }
    
    // --- KHÁC ---
    public void setVX(double vx) { this.vx = vx; }
    public void setVY(double vy) { this.vy = vy; }
    
    public void stopMoving() {
        this.isMoving = false;
        this.vx = 0;
        this.vy = 0;
    }

    // Cập nhật vị trí
    public void updatePosition() {
        if (!isMoving) return;

        x += vx;
        y += vy;

        // GIẢM MA SÁT (Tăng độ bền tốc độ): 0.998 (trước đây là 0.995)
        double friction = 0.998; 
        vx *= friction;
        vy *= friction;

        // Giữ nguyên logic dừng nếu tốc độ quá nhỏ
        if (Math.hypot(vx, vy) < 0.1) {
            vx = 0;
            vy = 0;
        }

        // Kiểm tra va chạm tường
        if (x - RADIUS < 0) {
            x = RADIUS;
            vx = -vx;
        } else if (x + RADIUS > panelWidth) {
            x = panelWidth - RADIUS;
            vx = -vx;
        }

        if (y - RADIUS < 0) {
            y = RADIUS;
            vy = -vy;
        } else if (y + RADIUS > panelHeight) {
            y = panelHeight - RADIUS;
            vy = -vy;
        }
    }
    
    @Override
    public void run() {
        // Logic thread (không cần thêm gì)
    }
    
    private Color generateRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }
}