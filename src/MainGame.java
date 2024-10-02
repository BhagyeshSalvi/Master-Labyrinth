import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainGame {

    public static void main(String[] args) {
        new MainGame().createAndShowGame();
    }

    // Main method to create and display the game
    public void createAndShowGame() {
        JFrame frame = createFrame();

        // Create a layered pane to hold background and grid
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1280, 800)); // Set size of the game window

        // Add background and grid panel
        layeredPane.add(createBackgroundPanel(), Integer.valueOf(0));  // Background at layer 0
        layeredPane.add(createGridPanel(), Integer.valueOf(1));  // Grid on top at layer 1

        JMenuBar menubar = createMenuBar();
        frame.setJMenuBar(menubar);
        

        // Add the layered pane to the frame and display
        frame.add(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Method to create the main game frame
    private JFrame createFrame() {
        JFrame frame = new JFrame("Labyrinth Game with Specific Image Distribution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    // Method to create the background panel
    private JPanel createBackgroundPanel() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw background image
                Image bgImage = new ImageIcon("Pictures/bg.jpg").getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setBounds(0, 0, 1280, 800);  // Set size of background panel
        return backgroundPanel;
    }

    
    // Method to create the grid panel (maze)
    private JPanel createGridPanel() {
    JPanel gridPanel = new JPanel(new GridBagLayout());
    gridPanel.setOpaque(false);  // Transparent grid to show background
    gridPanel.setBounds(50, 50, 700, 700);  // Position the grid panel

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(0, 0, 0, 0);  // Remove spacing between cells

    // Get the list of images based on specified frequencies
    List<String> imageList = generateImageList();
    Collections.shuffle(imageList);  // Randomize image placement

    // Set preferred size for each grid cell
    Dimension cellSize = new Dimension(65, 65);
    int imageIndex = 0;

    // Loop to create 7x7 grid
    for (int row = 0; row < 7; row++) {
        for (int col = 0; col < 7; col++) {
            gbc.gridx = col;
            gbc.gridy = row;

            // Create a JPanel for each cell
            JPanel cell = createCellPanel(imageList.get(imageIndex), cellSize);
            gridPanel.add(cell, gbc);

            imageIndex++;  // Move to the next image in the shuffled list
        }
    }

    return gridPanel;
}


    
   // Method to create a single grid cell with an image
    private JPanel createCellPanel(String imagePath, Dimension cellSize) {
    JPanel cell = new JPanel();
    cell.setPreferredSize(cellSize);
    cell.setOpaque(false);  // Transparent cell to show background

    // Load the image
    ImageIcon originalIcon = new ImageIcon(imagePath);
    Image originalImage = originalIcon.getImage();

    // Scale the image to fit the cell size
    Image scaledImage = originalImage.getScaledInstance(cellSize.width, cellSize.height, Image.SCALE_SMOOTH);
    ImageIcon scaledIcon = new ImageIcon(scaledImage);

    // Add the scaled image to the JLabel
    JLabel imageLabel = new JLabel(scaledIcon);
    cell.add(imageLabel);

    return cell;
}

    // Method to generate the list of images with specific frequencies
    private List<String> generateImageList() {
        String[] imagePaths = {
            "Pictures/brick_cross.png",  // Cross - 2 times 
            "Pictures/brick_cross.png",  // Vertical - 8 times
            "Pictures/hallway_horiz.png",  // Horizontal - 7 times
            "Pictures/brick_NE.png",  // northeast - 4 times
            "Pictures/brick_NW.png",  // northwest - 4 times
            "Pictures/brick_SE.png",  // southeast - 4 times
            "Pictures/brick_SW.png",  // southwest - 4 times
            "Pictures/brick_Teast.png", // T-east - 4 times
            "Pictures/brick_Twest.png", // T-west - 4 times
            "Pictures/brick_Tnorth.png", // T-north - 4 times
            "Pictures/brick_Tsouth.png", // T-south - 4 times
        };

        // Create a list to store images according to their usage frequency
        List<String> imageList = new ArrayList<>();

        // Add images to the list with specified frequencies
        for (int i = 0; i < 2; i++) {
            imageList.add(imagePaths[0]);  // Cross
        }
        for (int i = 0; i < 8; i++) {
            imageList.add(imagePaths[1]);  // Vertical
        }
        for (int i = 0; i < 7; i++) {
            imageList.add(imagePaths[2]);  //Horizontal
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[3]);  //northeast
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[4]);  //nortwest
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[5]);  //southeast
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[6]);  //southwest
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[7]);  //T-east
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[8]);  //T-west
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[9]);  //T-norht
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[10]);  //T-south
        }

        return imageList;
    }
    

    //menu bar
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
    
        // Create the File menu and its items
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New"));
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        fileMenu.addSeparator();  // Adds a separator line
        fileMenu.add(new JMenuItem("Exit"));
    
        // Create the Game menu and its items
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(new JMenuItem("Start Game"));
        gameMenu.add(new JMenuItem("Pause"));
        gameMenu.add(new JMenuItem("Undo Move"));
        gameMenu.add(new JMenuItem("End Game"));
    
        // Create the Help menu and its items
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("Learn"));
        helpMenu.add(new JMenuItem("About"));
    
        // Add the menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
    
        return menuBar;
    }
    
}
