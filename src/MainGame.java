import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.*;

public class MainGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Labyrinth Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create background panel and set the layout
        BackgroundPanel bgPanel = new BackgroundPanel("Pictures/bg.jpg");
        bgPanel.setLayout(new GridBagLayout());  

        // Add components like menu, maze grid, etc.
        // Example: Adding a placeholder label in the center
        
        

        frame.add(bgPanel);
        frame.setSize(800, 600);  // Set preferred size, but avoid setSize() as per your restrictions
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
