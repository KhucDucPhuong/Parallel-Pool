package com.parallel.pool; 

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Đặt tiêu đề thành Menu Chính
            JFrame frame = new JFrame("Billiard Simulation - Main Menu");
            
            // Khởi tạo và hiển thị Menu
            MainMenu mainMenu = new MainMenu(frame);
            frame.add(mainMenu); 
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); 
            frame.pack();
            frame.setVisible(true);
        });
    }
}