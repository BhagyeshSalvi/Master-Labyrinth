/**
 * Name- Bhagyesh Salvi
 * Number- 041103856
 * Java Application programming UI Assignment
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.*;

/**
 * 
 * @author Admin
 * This Class contains all the method which make the up of the assignment
 *
 */
public class MainGame {
    
    private GameBoard gameBoard;
    private GameController gameController;
    private JPanel insertPanel; 
    private JLayeredPane layeredPane; 
    private JPanel gridPanel;    
    private Map<Point, JLabel> tokenMap = new HashMap<>(); // To track tokens by their grid position
    private JLabel[] playerStarLabels;
    private Map<String, JLabel> magicalComponentLabels;

    
    
    // Tile currentInsertTile = new Tile("default", new ImageIcon("Pictures/GridCell/hallway_vert.png").getImage());

    public MainGame(){
        Player[] players = {
            new Player("Player 1", new Point(4, 4), Color.RED),
            new Player("Player 2", new Point(4, 6), Color.BLUE),
            new Player("Player 3", new Point(6, 4), Color.GREEN),
            new Player("Player 4", new Point(6, 6), Color.YELLOW)
        };
        GameBoard gameBoard = new GameBoard(9, players);
        this.gameController = new GameController(gameBoard); // Initialize GameController
        
    }
    
    public MainGame(GameController gameController){
        this.gameController=gameController;
    }
	
	
	/**
	 * This is main method calls createAndShow game which has all method calls
	 * @param args
	 */
    public static void main(String[] args) {
        // Launch the game
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainGame().createAndShowGame();
        });
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

        // Initialize players
        Player[] players = {
            new Player("Player 1", new Point(4, 4), Color.RED),
            new Player("Player 2", new Point(4, 6), Color.BLUE),
            new Player("Player 3", new Point(6, 4), Color.GREEN),
            new Player("Player 4", new Point(6, 6), Color.YELLOW)
        };

        // Initialize the game board
        gameBoard = new GameBoard(9, players);
        this.gameController = new GameController(gameBoard,this);


        JFrame frame = createFrame();

        // Create a layered pane to hold background and grid
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1280, 750)); // Set size of the game window

        // Add background and grid panel
        layeredPane.add(createBackgroundPanel(), Integer.valueOf(0));  // Background at layer 0
       // layeredPane.add(createGridPanel(), Integer.valueOf(1));  // Grid on top at layer 1
       JLayeredPane layeredGridWithPlayers = createGridWithPlayersAndTokens();
       layeredPane.add(layeredGridWithPlayers, Integer.valueOf(1)); // Add the grid with players


        JPanel chatPanel = createChatPanel();
        chatPanel.setBounds(970, 490, 300, 250);  // Positioning the chat panel in the bottom-right corner
        layeredPane.add(chatPanel, Integer.valueOf(2));  // Chatbox on top at layer 2

       
        // Create and add the logo panel
        JPanel logoPanel = createLogoPanel();
        logoPanel.setBounds(0, 0, 1280, 100);  // Position the logo at the top of the screen
        layeredPane.add(logoPanel, Integer.valueOf(3));  // Logo on top at layer 3

            // Add magical components panel
        JPanel magicalComponentsPanel = createMagicalComponentsPanel();
        magicalComponentsPanel.setBounds(970, 300, 300, 150); // Position between chatPanel and gameAreaPanel
        layeredPane.add(magicalComponentsPanel, Integer.valueOf(3));
        
        // Create and add the game area panel
        JPanel gameAreaPanel = createGameAreaPanel();
        gameAreaPanel.setBounds(970, 50, 300, 200);
        layeredPane.add(gameAreaPanel, Integer.valueOf(3)); // Add game area at layer 3

        // Create and add the insert panel
        // Create the Tile object for the insert panel
        String initialImagePath = "Pictures/GridCell/hallway_horiz.png"; // Initial image path for the InsertPanel
        insertPanel = createInsertPanel(initialImagePath);
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
        frame.setFocusable(true);
        frame.requestFocusInWindow();


        // Setup key listeners
    GameController controller = new GameController(gameBoard, this); // Pass game board and view
    setupKeyListeners(frame, controller);
    }


    

    public void setController(GameController controller) {
        this.gameController = controller;
    }
    
    
    public JPanel getGridPanel() {
        return gridPanel;
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

    private boolean isInteractivePosition(int row, int col) {
        // Check side cells: left and right edges
        if ((row == 2 || row == 4 || row == 6) && (col == 0 || col == 8)) {
            return true;
        }
    
        // Check side cells: top and bottom edges
        if ((col == 2 || col == 4 || col == 6) && (row == 0 || row == 8)) {
            return true;
        }
    
        return false; // Not an interactive position
    }
    
    
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        gridPanel.setBounds(50, 50, 650, 650); // Adjust size for 9x9 grid
    
        GridBagConstraints gbc = new GridBagConstraints();
        Dimension cellSize = new Dimension(60, 60); // Each cell's size
    
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
    
                Tile tile = gameBoard.getTile(row, col); // Fetch the Tile object from GameBoard
    
                if (tile != null) {
                    // Check if the tile is at an interactive position
                    boolean isInteractive = isInteractivePosition(row, col);
                    JLayeredPane cellPane = isInteractive
                            ? createInteractiveInsertLayeredPane(tile.getImagePath(), row, col, cellSize)
                            : createLayeredPane(tile.getImagePath(), cellSize);
                    gridPanel.add(cellPane, gbc);
                } else {
                    // Empty corner cells
                    JLayeredPane emptyCell = createLayeredPane(null, cellSize);
                    gridPanel.add(emptyCell, gbc);
                }
            }
        }
        gridPanel.setFocusable(true);
        return gridPanel;
    }
    

// Create a layered pane for all cells
private JLayeredPane createLayeredPane(String imagePath, Dimension cellSize) {
    JLayeredPane pane = new JLayeredPane();
    pane.setPreferredSize(cellSize);

    JLabel imageLabel = new JLabel();
    imageLabel.setBounds(0, 0, cellSize.width, cellSize.height);

    if (imagePath != null) {
        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath)
                .getImage().getScaledInstance(cellSize.width, cellSize.height, Image.SCALE_SMOOTH));
        imageLabel.setIcon(icon);
    }

    pane.add(imageLabel, JLayeredPane.DEFAULT_LAYER); // Add image to default layer
    return pane;
}

// Create an interactive layered pane for insertion cells
private JLayeredPane createInteractiveInsertLayeredPane(String imagePath, int row, int col, Dimension cellSize) {
    JLayeredPane pane = createLayeredPane(imagePath, cellSize);

    // Add metadata for interaction
    pane.putClientProperty("row", row);
    pane.putClientProperty("col", col);

    // Add MouseListener to handle clicks
    pane.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int clickedRow = (int) pane.getClientProperty("row");
            int clickedCol = (int) pane.getClientProperty("col");

            if (gameController != null) {
                gameController.handleInsertClick(clickedRow, clickedCol);
            } else {
                System.err.println("GameController is not initialized!");
            }
        }
    });

    return pane;
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

        // Add Change Language menu at the end
        JMenu changeLanguageMenu = new JMenu("Change Language");

        menuBar.add(Box.createHorizontalGlue()); // Push "Change Language" to the right
        menuBar.add(changeLanguageMenu);
    


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
        chatPanel.setPreferredSize(new Dimension(150, 200)); // Size for chat panel
        chatPanel.setOpaque(false); // Transparent background
    
        // Text area to display chat messages
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false); // Users shouldn't edit chat history
        chatArea.setLineWrap(true);  // Wrap text to the next line
        chatArea.setWrapStyleWord(true);
        chatArea.setForeground(Color.WHITE); // Set text color to white
        chatArea.setFont(new Font("Arial", Font.BOLD, 14)); // Set text to bold
        chatArea.setOpaque(false); // Transparent background for text area
    
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(false); // Transparent scroll pane
        scrollPane.getViewport().setOpaque(false); // Transparent viewport
    
        // Text field for typing messages
        JTextField messageField = new JTextField();
        messageField.setForeground(Color.BLACK); // Set text color to white
        messageField.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text for input field
        //messageField.setOpaque(false); // Transparent background
       // messageField.setBorder(BorderFactory.createEmptyBorder()); // Remove border
    
        // Button to send the message
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12)); // Bold text for button
        sendButton.setForeground(Color.WHITE); // Set button text to white
        sendButton.setOpaque(false); // Transparent background for button
        sendButton.setContentAreaFilled(false); // Remove content fill for transparency
        sendButton.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Add white border
    
        // Action listener for the send button
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                chatArea.append("Player: " + message + "\n"); // Append message to chat area
                messageField.setText(""); // Clear the input field
            }
            //regaining focus after hiting enter key
            SwingUtilities.getWindowAncestor(chatPanel).requestFocusInWindow();
        });
    
        // Allow sending messages with the Enter key
        messageField.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                chatArea.append("Player: " + message + "\n");
                messageField.setText("");
            }

            //regainng focus after hiting enter key
            SwingUtilities.getWindowAncestor(chatPanel).requestFocusInWindow();
        });
    
        // Panel to hold the message input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setOpaque(false); // Transparent background for input panel
    
        // Add components to chat panel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
    
        return chatPanel; // Return the transparent chat panel
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
        int[] starCounts = {0, 5, 2, 4}; // Initial star counts for each player
        playerStarLabels = new JLabel[playerNames.length]; // Array to store star labels
    
        for (int i = 0; i < playerNames.length; i++) {
            JPanel playerPanel = new JPanel(); // Panel for each player
            playerPanel.setOpaque(false); // Make each player's panel transparent
            playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Aligns content to the left
    
            JLabel nameLabel = new JLabel(playerNames[i]);
            nameLabel.setForeground(Color.WHITE); // Set text color to white
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 24)); // Increase text size
    
            // Create star labels
            JLabel starLabel = new JLabel(); // Initialize empty star label
            starLabel.setForeground(Color.LIGHT_GRAY); // Set star color to light gray
            starLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font size
            updateStars(starLabel, starCounts[i]); // Set initial stars
    
            playerStarLabels[i] = starLabel; // Store reference to the star label
    
            playerPanel.add(nameLabel); // Add player name to panel
            playerPanel.add(starLabel); // Add stars to panel
    
            gameAreaPanel.add(playerPanel); // Add player panel to game area panel
        }
    
        return gameAreaPanel; // Return the complete game area panel
    }
    
    private void updateStars(JLabel starLabel, int count) {
        StringBuilder stars = new StringBuilder();
        for (int j = 0; j < count; j++) {
            stars.append("\u2605 "); // Append stars
        }
        starLabel.setText(stars.toString()); // Update the label with stars

        // Ensure the font supports Unicode
        starLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 20)); // Use a compatible font
        starLabel.setForeground(Color.YELLOW); // Optional: Set a star-like color
    }
    public void updatePlayerStars(int playerIndex, int newStarCount) {
        if (playerIndex < 0 || playerIndex >= playerStarLabels.length) {
            System.err.println("Invalid player index: " + playerIndex);
            return;
        }
    
        JLabel starLabel = playerStarLabels[playerIndex]; // Get the star label for the player
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < newStarCount; i++) {
            stars.append("\u2605 "); // Add stars dynamically
        }
        starLabel.setText(stars.toString()); // Update the label
        System.out.println("Updated Player " + (playerIndex + 1) + " stars to " + newStarCount);
        // Ensure the font supports Unicode
        starLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 20)); 
        starLabel.setForeground(Color.YELLOW); 
    }

    private static String normalizePath(String path) {
        return path.trim().replace("\\", "/").toLowerCase(); // Trim spaces, unify separators, and lowercase
    }
    


    public void collectTokenIfPresent(int playerIndex, Point position) {
        JLabel tokenLabel = tokenMap.get(position);
        if (tokenLabel == null) {
            System.err.println("No token found at position: " + position);
            return;
        }
    
        String tokenPath = ((ImageIcon) tokenLabel.getIcon()).getDescription();
        System.out.println("magic label ke upar" + tokenPath);
        Map<String, Boolean> tokenPaths = GameUtils.generateTokenPaths();
        boolean isMagicCard = tokenPaths.getOrDefault(tokenPath, true);
        JLabel magicalLabel = magicalComponentLabels.get(tokenPath);
        System.out.println("Magic Label Retrieved: " + magicalLabel);

        if (isMagicCard) {

            if (magicalLabel != null) {
                Icon grayscaleIcon = Tile.createGrayscaleIcon(tokenPath, 80, 80); 

                if (grayscaleIcon != null) {
                    magicalLabel.setIcon(grayscaleIcon);
                } else {
                    System.err.println("Failed to create grayscale icon for: " + tokenPath);
                }
                magicalLabel.setText("Collected");
                magicalLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                magicalLabel.setVerticalTextPosition(SwingConstants.CENTER);
                magicalLabel.setFont(new Font("Arial", Font.BOLD, 17));
                magicalLabel.setForeground(Color.black);
                magicalLabel.getParent().revalidate();
                magicalLabel.getParent().repaint();
                System.out.println("Updated magic card: " + tokenPath);
            } else {
                System.err.println("No corresponding magic card found for: " + tokenPath);
            }
        }
    
        if (layeredPane.isAncestorOf(tokenLabel)) {
            tokenLabel.getParent().remove(tokenLabel);
            System.out.println("Token removed from layeredPane.");
        }
        tokenMap.remove(position);
    
    
        Player currentPlayer = gameBoard.getPlayers()[playerIndex];
        currentPlayer.collectStar();
        updatePlayerStars(playerIndex, currentPlayer.getStarsCollected());
    
        layeredPane.revalidate();
        layeredPane.repaint();
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
private JPanel createInsertPanel(String initialImagePath) {
    if (initialImagePath == null || initialImagePath.isEmpty()) {
        initialImagePath = "Pictures/GridCell/hallway_horiz.png"; // Default placeholder
    }

    System.out.println("Initializing InsertPanel Tile with imagePath: " + initialImagePath);
    // Create the Tile object
    Tile currentInsertTile = new Tile("default", initialImagePath);
    if (currentInsertTile.getImagePath() == null || currentInsertTile.getImage() == null) {
        System.err.println("Error: InsertPanel Tile initialized with invalid image.");
    }
    // Create the InsertPanel
    JPanel insertPanel = new JPanel(new BorderLayout());
    insertPanel.setPreferredSize(new Dimension(80, 120)); // Adjust panel size
    insertPanel.setOpaque(false);

    // Create JLabel to hold the tile image
    JLabel tileImageLabel = new JLabel();
    tileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    tileImageLabel.setPreferredSize(new Dimension(65, 65));

    // Load and set the initial image dynamically
    updateTileImageLabel(tileImageLabel, currentInsertTile);

    // Add the JLabel to the center of the panel
    insertPanel.add(tileImageLabel, BorderLayout.CENTER);
    insertPanel.putClientProperty("currentTile", currentInsertTile);

    // Add the Rotate button
    JButton rotateButton = new JButton("Rotate");
    rotateButton.setPreferredSize(new Dimension(80, 35));
    rotateButton.setFont(new Font("Arial", Font.BOLD, 17));
    rotateButton.setForeground(Color.WHITE);
    rotateButton.setOpaque(false);
    rotateButton.setContentAreaFilled(false);
    rotateButton.setBorderPainted(false);
    rotateButton.addActionListener(e -> {
        Tile currentTile = (Tile) insertPanel.getClientProperty("currentTile");
        // Rotate the tile and update its visual representation
        currentTile.rotateClockwise();
        updateTileImageLabel(tileImageLabel, currentTile);
        gridPanel.revalidate();
        gridPanel.repaint();
        SwingUtilities.getWindowAncestor(gridPanel).requestFocusInWindow();
    });
    insertPanel.add(rotateButton, BorderLayout.SOUTH);

    rotateButton.setFocusable(false); // Prevent the button from stealing focus
    // Store the Tile object and JLabel in the InsertPanel for later access
    insertPanel.putClientProperty("tileImageLabel", tileImageLabel);
    return insertPanel;
}

private void updateTileImageLabel(JLabel label, Tile tile) {
    if (tile.getImageIcon() != null) {
        label.setIcon(tile.getImageIcon()); // Use the latest ImageIcon
    } else {
        System.err.println("Error: Tile image is null or invalid.");
        label.setIcon(null); // Clear the label if the image is invalid
    }
}




private static final Point[] PLAYER_START_POSITIONS = {
    new Point(4, 4), // Player 1 starts at (row 4, column 4)
    new Point(4, 6), // Player 2 starts at (row 4, column 6)
    new Point(6, 4), // Player 3 starts at (row 6, column 4)
    new Point(6, 6)  // Player 4 starts at (row 6, column 6)
};

private JLabel[] playerLabels; // Array to store player JLabels
private static final int cellSize = 60; // Size of a grid cell
private static final int playerSize = 25; // Size of a player label


// Define starting positions for players in grid coordinates (row, col)
private JLayeredPane createGridWithPlayersAndTokens() {
    // Layered pane to hold grid, player pieces, and tokens
    JLayeredPane layeredGridPane = new JLayeredPane();
    layeredGridPane.setPreferredSize(new Dimension(650, 650)); // Match the grid size
    layeredGridPane.setBounds(50, 50, 650, 650);

    // Create the grid panel
    gridPanel = createGridPanel();
    gridPanel.setBounds(0, 0, 650, 650); // Align grid panel within the layered pane

    // Add the grid panel to the bottom layer
    layeredGridPane.add(gridPanel, Integer.valueOf(0)); // Grid is layer 0

    // Initialize player labels array
    playerLabels = new JLabel[PLAYER_START_POSITIONS.length];

    // Add player pieces
    for (int i = 0; i < PLAYER_START_POSITIONS.length; i++) {
        Point position = PLAYER_START_POSITIONS[i];
        JLabel playerLabel = createPlayerLabel(i + 1, playerSize);

        // Calculate player position
        int x = position.y * cellSize + (cellSize - playerSize) / 2;
        int y = position.x * cellSize + (cellSize - playerSize) / 2;
        playerLabel.setBounds(x, y, playerSize, playerSize);

        layeredGridPane.add(playerLabel, Integer.valueOf(1)); // Add player to layer 1
        playerLabels[i] = playerLabel; // Store reference to player label
    }

    // Define the square pattern for token positions
    List<Point> tokenPositions = Arrays.asList(
        new Point(3, 3), new Point(3, 4), new Point(3, 5), new Point(3, 6), new Point(3, 7),
        new Point(4, 7), new Point(5, 7), new Point(6, 7), new Point(7, 7),
        new Point(7, 6), new Point(7, 5), new Point(7, 4), new Point(7, 3),
        new Point(6, 3), new Point(5, 3), new Point(4, 3)
    );

    // Generate token paths with their identifiers
    Map<String, Boolean> tokenPaths = GameUtils.generateTokenPaths(); // Map of token paths and magic card flag
    List<Map.Entry<String, Boolean>> tokenList = new ArrayList<>(tokenPaths.entrySet());
    Collections.shuffle(tokenList); // Shuffle the tokens

    // Add tokens in the randomized order
    int tokenSize = 20; // Token size (smaller than players)
    for (int i = 0; i < tokenPositions.size(); i++) {
        Point position = tokenPositions.get(i); // Get token position
        Map.Entry<String, Boolean> tokenEntry = tokenList.get(i); // Get token path and magic flag
        String tokenPath = tokenEntry.getKey();
       

        JLabel tokenLabel = createTokenLabel(tokenPath, tokenSize);

       
        // Calculate token position relative to the grid cell size (60x60)
        int x = position.y * cellSize + (cellSize - tokenSize) / 2; // Center the token horizontally
        int y = position.x * cellSize + (cellSize - tokenSize) / 2; // Center the token vertically
        tokenLabel.setBounds(x, y, tokenSize, tokenSize); // Set position and size of the token

        layeredGridPane.add(tokenLabel, Integer.valueOf(1)); // Add tokens on layer 1
        tokenMap.put(position, tokenLabel); // Track the token in the map
    }

    return layeredGridPane;
}




private JLabel createPlayerLabel(int playerNumber, int size) {
    // Use different images/colors for each player
    String[] playerColors = {"red", "blue", "green", "yellow"};
    String imagePath = "Pictures/PlayerPiece/" + playerColors[playerNumber - 1] + ".png";
    ImageIcon playerIcon = new ImageIcon(imagePath);

    // Scale the image
    Image scaledImage = playerIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
    JLabel playerLabel = new JLabel(new ImageIcon(scaledImage));
    playerLabel.setOpaque(false); // Transparent background
    return playerLabel;
}




private JLabel createTokenLabel(String tokenPath, int tokenSize) {
    ImageIcon tokenIcon = new ImageIcon(tokenPath);
    tokenIcon.setDescription(tokenPath); // Set the description to the token path

    // Resize the icon to fit the token size
    Image scaledImage = tokenIcon.getImage().getScaledInstance(tokenSize, tokenSize, Image.SCALE_SMOOTH);
    tokenIcon = new ImageIcon(scaledImage);
    tokenIcon.setDescription(tokenPath); // Ensure the description remains after resizing

    JLabel tokenLabel = new JLabel(tokenIcon);
    tokenLabel.setHorizontalAlignment(SwingConstants.CENTER);
    tokenLabel.setVerticalAlignment(SwingConstants.CENTER);

    return tokenLabel;
}



private JPanel createMagicalComponentsPanel() {
    // Initialize the map to track magical components
    magicalComponentLabels = new HashMap<>();

    // Create a panel for the magical components
    JPanel magicalPanel = new JPanel();
    magicalPanel.setLayout(new GridLayout(1, 3, 10, 10)); // 1 row, 3 columns, with spacing
    magicalPanel.setOpaque(false); // Transparent background

    // Magical component file paths
    String[] componentPaths = {
        "Pictures/MG1.png",
        "Pictures/MG2.png",
        "Pictures/MG3.png",
    };

    for (String componentPath : componentPaths) {
        try {
            // Load the image
            ImageIcon componentIcon = new ImageIcon(componentPath);
            Image scaledImage = componentIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);

            // Add image to a JLabel
            JLabel componentLabel = new JLabel(new ImageIcon(scaledImage));
            componentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            componentLabel.setVerticalAlignment(SwingConstants.CENTER);
            System.out.println("Added magic label for: " + componentPath);


            // Store label reference in the map
            magicalComponentLabels.put(normalizePath(componentPath), componentLabel);

            // Add the label to the panel
            magicalPanel.add(componentLabel);
        } catch (Exception e) {
            System.err.println("Error loading magical component image: " + componentPath);
            // Add a placeholder label for missing or failed images
            JLabel errorLabel = new JLabel("Error");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setVerticalAlignment(SwingConstants.CENTER);
            magicalPanel.add(errorLabel);
        }
    }

    return magicalPanel;
}


public void updatePlayerPosition(int playerIndex, Point newPosition) {
    JLabel playerLabel = playerLabels[playerIndex]; // Get the player's JLabel

    // Calculate new position in the grid
    int x = newPosition.y * cellSize + (cellSize - playerSize) / 2;
    int y = newPosition.x * cellSize + (cellSize - playerSize) / 2;

    // Update the label's position
    playerLabel.setBounds(x, y, playerSize, playerSize);
    playerLabel.repaint(); // Refresh the UI
}

//Key listener to move player
private void setupKeyListeners(JFrame frame, GameController controller) {
    frame.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            String direction = null;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    direction = "up";
                    break;
                case KeyEvent.VK_DOWN:
                    direction = "down";
                    break;
                case KeyEvent.VK_LEFT:
                    direction = "left";
                    break;
                case KeyEvent.VK_RIGHT:
                    direction = "right";
                    break;
            }
            if (direction != null) {
                controller.handleMovement(0, direction); // Assuming Player 0 for now
            }
        }
    });
}

//dialog box to show invalid move
public void showInvalidMoveDialog() {
    JOptionPane.showMessageDialog(
        null,
        "You cannot move to that tile. It's occupied or out of bounds!",
        "Invalid Move",
        JOptionPane.WARNING_MESSAGE
    );
}



public JPanel getInsertPanel() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'getInsertPanel'");
    return insertPanel;
}





    
}
