/**
 * Name- Bhagyesh Salvi
 * Number- 041103856
 * Java Application programming UI Assignment
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Admin
 * This Class contains all the method which make the up of the assignment
 *
 */
public class MainGame {
	
	
	/**
	 * This is main method calls createAndShow game which has all method calls
	 * @param args
	 */
    public static void main(String[] args) {
        new MainGame().createAndShowGame();
    }

    
    /**
     * Initializes and displays the game window, setting up the user interface components.
     * 
     * This method creates a JFrame and uses a JLayeredPane to layer multiple panels for 
     * the game, including the background, grid, logo, game area, insert panel, and chat box.
     * 
     * It performs the following actions:
     * <ul>
     *   <li>Creates a JFrame for the game window.</li>
     *   <li>Sets up a layered pane to manage the layout of different components.</li>
     *   <li>Adds a background panel at the bottom layer.</li>
     *   <li>Places a grid panel on top of the background.</li>
     *   <li>Adds a chat panel in the bottom-right corner of the window.</li>
     *   <li>Creates and positions a logo panel at the top of the window.</li>
     *   <li>Sets up a game area panel for gameplay elements.</li>
     *   <li>Adds an insert panel for inputting grid elements.</li>
     *   <li>Creates a menu bar and adds it to the frame.</li>
     * </ul>
     * 
     * The frame is then packed to fit the components, centered on the screen, and made 
     * non-resizable. Finally, the frame is made visible.
     */
    public void createAndShowGame() {
        JFrame frame = createFrame();

        // Create a layered pane to hold background and grid
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1280, 750)); // Set size of the game window

        // Add background and grid panel
        layeredPane.add(createBackgroundPanel(), Integer.valueOf(0));  // Background at layer 0
       // layeredPane.add(createGridPanel(), Integer.valueOf(1));  // Grid on top at layer 1
       JLayeredPane layeredGridWithPlayers = createGridWithPlayers();
       layeredPane.add(layeredGridWithPlayers, Integer.valueOf(1)); // Add the grid with players


        JPanel chatPanel = createChatPanel();
        chatPanel.setBounds(970, 490, 300, 250);  // Positioning the chat panel in the bottom-right corner
        layeredPane.add(chatPanel, Integer.valueOf(2));  // Chatbox on top at layer 2

        // Create and add the logo panel
        JPanel logoPanel = createLogoPanel();
        logoPanel.setBounds(0, 0, 1280, 100);  // Position the logo at the top of the screen
        layeredPane.add(logoPanel, Integer.valueOf(3));  // Logo on top at layer 3

        // Create and add the game area panel
        JPanel gameAreaPanel = createGameAreaPanel();
        gameAreaPanel.setBounds(970, 50, 200, 200);
        layeredPane.add(gameAreaPanel, Integer.valueOf(3)); // Add game area at layer 3

        // Create and add the insert panel
        JPanel insertPanel = createInsertPanel();
        insertPanel.setBounds(640, 450, 200, 150);
        layeredPane.add(insertPanel, Integer.valueOf(3));  // Insert panel above the game area

        JMenuBar menubar = createMenuBar();
        frame.setJMenuBar(menubar);

        // Add the layered pane to the frame and display
        frame.add(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }


    /**
     * Creates the main game frame for the Labyrinth game.
     * 
     * This method initializes a new JFrame with the title "Labyrinth Game with Specific Image Distribution".
     * It sets the default close operation to exit the application when the frame is closed.
     * 
     * @return the newly created JFrame instance for the game.
     */
    private JFrame createFrame() {
        JFrame frame = new JFrame("Labyrinth Game with Specific Image Distribution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }


    /**
     * Creates a background panel with a custom background image.
     * 
     * This method initializes a JPanel and overrides its `paintComponent` method to draw a background image.
     * The image is loaded from the specified path and scaled to fit the panel's dimensions.
     * 
     * @return the newly created JPanel instance that serves as the background for the game.
     */
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
        backgroundPanel.setBounds(0, 0, 1280, 750);  // Set size of background panel
        return backgroundPanel;
    }

    
    /**
     * Creates the grid panel representing the maze.
     * 
     * This method initializes a JPanel using a GridBagLayout to arrange cells in a 7x7 grid.
     * Each cell is a JPanel containing an image, which is randomly selected from a generated list 
     * of images based on specified frequencies. The grid panel has a white border and is 
     * transparent to allow the background to show through.
     * 
     * @return the newly created JPanel that represents the maze grid.
     */
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);  // Transparent grid to show background
        gridPanel.setBounds(100, 100, 458, 458);  // Position the grid panel

        Border border = BorderFactory.createLineBorder(Color.WHITE, 1); // White border with 1px thickness
        gridPanel.setBorder(border);

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

    



    
    /**
 * Creates a single grid cell with an image specified by an ImageIcon.
 * 
 * This method initializes a JPanel that represents a cell in the grid. 
 * The cell displays an image from the provided ImageIcon, scaled to fit 
 * the specified cell size. The cell is transparent to show the background.
 * 
 * @param icon the ImageIcon to be displayed in the cell.
 * @param cellSize the preferred size of the cell.
 * @return the newly created JPanel representing a grid cell containing the image.
 */
private JPanel createCellPanel(ImageIcon icon, Dimension cellSize) {
    JPanel cell = new JPanel(new BorderLayout());
    cell.setPreferredSize(cellSize);
    cell.setOpaque(false);  // Transparent cell to show background

    // Scale the image to fit the cell size
    Image scaledImage = icon.getImage().getScaledInstance(cellSize.width, cellSize.height, Image.SCALE_SMOOTH);
    ImageIcon scaledIcon = new ImageIcon(scaledImage);

    // Add the scaled image to the JLabel
    JLabel imageLabel = new JLabel(scaledIcon);
    cell.add(imageLabel, BorderLayout.CENTER);

    return cell;
}

/**
 * Creates a single grid cell with an image specified by a path.
 * 
 * This method initializes a JPanel that represents a cell in the grid. 
 * The cell displays an image from the provided image path, scaled to fit 
 * the specified cell size. The cell is transparent to show the background.
 * 
 * @param imagePath the path to the image to be displayed in the cell.
 * @param cellSize the preferred size of the cell.
 * @return the newly created JPanel representing a grid cell containing the image.
 */
private JPanel createCellPanel(String imagePath, Dimension cellSize) {
    ImageIcon originalIcon = new ImageIcon(imagePath);
    return createCellPanel(originalIcon, cellSize);
}



    /**
     * Generates a list of image paths with specified frequencies for the game grid.
     * 
     * This method constructs a list containing the paths to various images, each added according 
     * to a specific frequency. The images represent different components of the labyrinth layout 
     * and are utilized to populate the grid cells. The method ensures that the images are added 
     * in the correct quantities to match the intended design of the game.
     * 
     * @return a List of String containing image paths according to their specified frequencies.
     */
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
        List<String> imageList = new ArrayList<String>();

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
            imageList.add(imagePaths[4]);  //northwest
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
            imageList.add(imagePaths[9]);  //T-north
        }
        for (int i = 0; i < 4; i++) {
            imageList.add(imagePaths[10]);  //T-south
        }

        return imageList;
    }


    /**
     * Creates the menu bar for the game interface.
     * 
     * This method constructs a JMenuBar containing three menus: 
     * "File", "Game", and "Help". Each menu includes several menu items 
     * relevant to the game functionality. The "File" menu includes options 
     * for creating a new game, opening an existing game, saving the game, 
     * and exiting the application. The "Game" menu includes options for 
     * starting, pausing, undoing moves, and ending the game. The "Help" 
     * menu provides options for learning and viewing information about the game.
     * 
     * @return a JMenuBar containing the game menus and their respective items.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create the File menu and its items
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New"));
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        fileMenu.addSeparator();  // Adds a separator laine
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


    /**
     * Creates the chat panel for the game interface.
     * 
     * This method constructs a JPanel that serves as a chat area, allowing
     * players to view chat messages and input their own messages. The panel
     * includes a non-editable JTextArea for displaying chat history, a JTextField
     * for user input, and a JButton to send the message. The panel is designed
     * to have a transparent background, enabling the underlying game graphics to be visible.
     * 
     * @return a JPanel containing the chat area, message input field, and send button.
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(150, 200));  // Size for chat panel

        // Text area to display chat messages 
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);  // Users shouldn't edit chat history
        chatArea.setLineWrap(true);   // Wrap text to the next line
        chatArea.setWrapStyleWord(true);
        chatArea.setForeground(Color.BLACK);  // Set text color to black
        chatArea.setBackground(new Color(0, 0, 0, 0)); // Make background transparent
        JScrollPane scrollPane = new JScrollPane(chatArea);  // Add scroll bar for the chat area
        scrollPane.setOpaque(false);  // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false);  // Make viewport (where text is visible) transparent

        // Text field for typing messages
        JTextField messageField = new JTextField();
        messageField.setOpaque(false);  // Make text field transparent
        messageField.setForeground(Color.BLACK);  // Set text color to black
        messageField.setBackground(new Color(0, 0, 0, 0)); // Make background transparent
        messageField.setBorder(BorderFactory.createEmptyBorder()); // Remove border

        // Button to send the message
        JButton sendButton = new JButton("Send");

        // Panel to hold the message input and send button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.setOpaque(false);

        // Add the scrollPane and inputPanel to the chatPanel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        return chatPanel;
    }


    /**
     * Creates the logo panel for the game interface.
     * 
     * This method constructs a JPanel that displays the game logo at the 
     * center of the panel. The logo is loaded from a specified image file,
     * and the panel is set to be transparent, allowing the background to be visible 
     * through it. The logo serves as a branding element for the game.
     * 
     * @return a JPanel containing the game logo centered within it.
     */
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setOpaque(false);  // Make the logo panel transparent

        // Load the logo image
        ImageIcon logoIcon = new ImageIcon("Pictures/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);

        // Center the logo in the panel
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        return logoPanel;
    }

    /**
     * Creates the game area panel that displays player information.
     *
     * This method constructs a JPanel containing information for four players,
     * including their names and star counts. Each player's information is 
     * displayed in a separate panel, arranged vertically. The background is 
     * set to be transparent to allow for visual effects or backgrounds to show through.
     * 
     * @return a JPanel representing the game area, displaying player names and star counts.
     */
    private JPanel createGameAreaPanel() {
        JPanel gameAreaPanel = new JPanel();
        gameAreaPanel.setLayout(new GridLayout(4, 1)); // Vertical layout for 4 players
        gameAreaPanel.setOpaque(false); // Make the background transparent if desired

        // Player names and star counts
        String[] playerNames = {"Player 1", "Player 2", "Player 3", "Player 4"};
        int[] starCounts = {3, 5, 2, 4}; // star counts for each player for only gui

        for (int i = 0; i < playerNames.length; i++) {
            JPanel playerPanel = new JPanel(); // Panel for each player
            playerPanel.setOpaque(false); // Make each player's panel transparent
            playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Aligns content to the left

            JLabel nameLabel = new JLabel(playerNames[i]);
            nameLabel.setForeground(Color.WHITE); // Set text color to white
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 24)); // Increase text size

            // Create star labels
            StringBuilder stars = new StringBuilder();
            for (int j = 0; j < starCounts[i]; j++) {
                stars.append("★ "); // Append stars
            }
            JLabel starLabel = new JLabel(stars.toString());
            starLabel.setForeground(Color.lightGray); // Set star color to yellow
            // starLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Increase star text size

            playerPanel.add(nameLabel); // Add player name to panel
            playerPanel.add(starLabel); // Add stars to panel

            gameAreaPanel.add(playerPanel); // Add player panel to game area panel
        }

        return gameAreaPanel; // Return the complete game area panel
    }


    /**
 * Creates a panel for inserting a piece in the game.
 *
 * This method constructs a JPanel that allows users to select a position
 * for inserting a cell image and includes "Insert" and "Rotate" buttons.
 * The panel is transparent, and its contents are arranged vertically. The 
 * panel features a label indicating the action, a cell image, and the buttons.
 *
 * @return a JPanel configured for inserting a piece, containing a label,
 *         an image, and the buttons.
 */
private JPanel createInsertPanel() {
    JPanel insertPanel = new JPanel(); // Panel for the cell image and button
    insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS)); // Use vertical box layout
    insertPanel.setOpaque(false); // Make the panel transparent 

    // Select position label
    JLabel selectPositionLabel = new JLabel("Select Position");
    selectPositionLabel.setForeground(Color.WHITE); // Set text color
    selectPositionLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Set label text size
    selectPositionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
    insertPanel.add(selectPositionLabel);

    // Add spacing between label and image
    insertPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Vertical spacing of 5 pixels

    // Create and load the image for the cell
    ImageIcon cellImageIcon = new ImageIcon("Pictures/hallway_horiz.png");
    JLabel cellImageLabel = new JLabel(cellImageIcon);
    cellImageLabel.setPreferredSize(new Dimension(65, 65)); // Set image size to 65x65
    cellImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the image
    cellImageLabel.setBorder(new LineBorder(Color.WHITE, 1));

    // Panel to hold "Insert" and "Rotate" buttons side by side
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Centered with spacing
    buttonPanel.setOpaque(false); // Transparent background for the button panel

    // Create the Insert button
    JButton insertButton = new JButton("Insert");
    insertButton.setPreferredSize(new Dimension(100, 30)); // Set button size
    insertButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set button text size
    insertButton.setForeground(Color.WHITE); // Set button text color
    insertButton.setOpaque(false); // Make button transparent
    insertButton.setContentAreaFilled(false); // Remove background fill
    insertButton.setBorderPainted(false); // Remove border painting
    insertButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Create the Rotate button
    JButton rotateButton = new JButton("Rotate");
    rotateButton.setPreferredSize(new Dimension(80, 30)); // Smaller button size
    rotateButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set button text size
    rotateButton.setForeground(Color.WHITE); // Set button text color
    rotateButton.setOpaque(false); // Make button transparent
    rotateButton.setContentAreaFilled(false); // Remove background fill
    rotateButton.setBorderPainted(false); // Remove border painting
    rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Add both buttons to the button panel
    buttonPanel.add(insertButton);
    buttonPanel.add(rotateButton);

    // Add the image and button panel to the insert panel
    insertPanel.add(cellImageLabel);
    insertPanel.add(Box.createRigidArea(new Dimension(0, 4))); // Vertical spacing of 5 pixels
    insertPanel.add(buttonPanel);

    return insertPanel; // Return the complete insert panel
}

// Define starting positions for players in grid coordinates (row, col)
private static final Point[] PLAYER_START_POSITIONS = {
    new Point(2, 2), // Player 1
    new Point(2, 4), // Player 2
    new Point(4, 2), // Player 3
    new Point(4, 4)  // Player 4
};

private JLayeredPane createGridWithPlayers() {
    // Layered pane to hold grid and player pieces
    JLayeredPane layeredGridPane = new JLayeredPane();
    layeredGridPane.setPreferredSize(new Dimension(458, 458)); // Same size as the grid
    layeredGridPane.setBounds(100, 100, 458, 458);

    // Create the grid panel
    JPanel gridPanel = createGridPanel();
    gridPanel.setBounds(0, 0, 458, 458); // Align to the top-left of the layered pane

    // Add grid to the bottom layer
    layeredGridPane.add(gridPanel, Integer.valueOf(0)); // Grid is layer 0

    // Add player pieces on the next layer
    for (int i = 0; i < PLAYER_START_POSITIONS.length; i++) {
        Point position = PLAYER_START_POSITIONS[i];
        JLabel playerLabel = createPlayerLabel(i + 1, 20); // 40 is the size of the player image
        int x = position.x * 65 + (65 - 40) / 2; // Calculate x-position
        int y = position.y * 65 + (65 - 40) / 2; // Calculate y-position
        playerLabel.setBounds(x, y, 40, 40); // Set position and size
        layeredGridPane.add(playerLabel, Integer.valueOf(1)); // Add players on layer 1
    }

    return layeredGridPane;
}

private JLabel createPlayerLabel(int playerNumber, int size) {
    // Use different images/colors for each player
    String[] playerColors = {"red", "blue", "green", "yellow"};
    String imagePath = "Pictures/" + playerColors[playerNumber - 1] + ".png";
    ImageIcon playerIcon = new ImageIcon(imagePath);

    // Scale the image
    Image scaledImage = playerIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
    JLabel playerLabel = new JLabel(new ImageIcon(scaledImage));
    playerLabel.setOpaque(false); // Transparent background
    return playerLabel;
}



    

    
}
