package com.parallel.pool;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private JFrame mainFrame; 
    private JComboBox<String> mapSelector;
    private JSpinner ballCountSpinner;
    private JSpinner timeLimitSpinner; 

    public MainMenu(JFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10)); 
        
        // Cài đặt giao diện tiêu đề
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 220, 220));
        JLabel titleLabel = new JLabel("🎱 PARALLEL POOL CHALLENGE 🎱", SwingConstants.CENTER); 
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Tạo Form Panel (Grid 3x2)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 15, 15)); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font controlFont = new Font("Arial", Font.PLAIN, 16);

        // 1. Chọn Map (JComboBox) - ĐÃ ĐỔI TÊN HIỂN THỊ
        mapSelector = new JComboBox<>(new String[]{
            "Map 1: Goal 20 Points (Center Hole)", // Loại bỏ số bóng cố định
            "Map 2: Goal 50 Points (Corner Hole)", // Loại bỏ số bóng cố định
            "Map 3: Goal 100 Points (Performance Test)" 
        });
        mapSelector.setFont(controlFont);
        
        JLabel mapLabel = new JLabel("Select Map (Goal):");
        mapLabel.setFont(labelFont);
        formPanel.add(mapLabel);
        formPanel.add(mapSelector);

        // 2. Chọn Số lượng Bóng (JSpinner) - SẼ DÙNG SỐ NÀY CHO TẤT CẢ CÁC MAP
        SpinnerModel ballModel = new SpinnerNumberModel(8, 1, 300, 1);
        ballCountSpinner = new JSpinner(ballModel);
        ballCountSpinner.setFont(controlFont);
        
        JLabel ballLabel = new JLabel("Number of Balls:");
        ballLabel.setFont(labelFont);
        formPanel.add(ballLabel);
        formPanel.add(ballCountSpinner);
        
        // 3. Chọn Thời gian Game (JSpinner)
        SpinnerModel timeModel = new SpinnerNumberModel(20, 10, 300, 10); 
        timeLimitSpinner = new JSpinner(timeModel);
        timeLimitSpinner.setFont(controlFont);

        JLabel timeLabel = new JLabel("Time Limit (seconds):");
        timeLabel.setFont(labelFont);
        formPanel.add(timeLabel);
        formPanel.add(timeLimitSpinner);
        
        add(formPanel, BorderLayout.CENTER);

        // Tạo Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        
        JButton startBtn = new JButton("▶ Start Simulation");
        styleButton(startBtn, new Color(50, 150, 50));
        startBtn.addActionListener(e -> startGame());
        
        JButton exitBtn = new JButton("❌ Exit"); 
        styleButton(exitBtn, new Color(150, 50, 50));
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(startBtn);
        buttonPanel.add(exitBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
    }

    private void startGame() {
        int mapIndex = mapSelector.getSelectedIndex() + 1;
        int ballCount = (int) ballCountSpinner.getValue(); // Lấy số lượng bóng được chọn
        int timeLimit = (int) timeLimitSpinner.getValue(); 

        // ĐÃ BỎ LOGIC GHI ĐÈ SỐ BÓNG CHO MAP 1 VÀ MAP 2. 
        // Số lượng bóng sẽ luôn là 'ballCount' được chọn.
        
        mainFrame.getContentPane().removeAll();

        BilliardsPanel gamePanel = new BilliardsPanel(800, 600, ballCount, timeLimit, this); 
        
        gamePanel.setInitialBallCount(ballCount);
        gamePanel.setTimeLimit(timeLimit); 
        gamePanel.loadMap(mapIndex); 

        mainFrame.add(gamePanel, BorderLayout.CENTER);
        
        mainFrame.setTitle("Parallel Pool Challenge - Simulation"); 
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.pack();
    }
    
    public void showMenu() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(this);
        mainFrame.setTitle("Parallel Pool Challenge - Main Menu"); 
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.pack();
    }
}