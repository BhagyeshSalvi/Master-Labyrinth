
/**
 * Name- Bhagyesh Salvi
 * Number- 041103856
 * Java Application programming UI Assignment
 * 
 * This Class contains the view of the game, it calls all the method that needs to make the GUI.
 */
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.*;

public class MainGame {

    private GameBoard gameBoard;
    private GameController gameController;
    private JPanel insertPanel;
    private JLayeredPane layeredPane;
    private JPanel gameAreaPanel;
    private JPanel gridPanel;
    private Map<Point, JLabel> tokenMap = new HashMap<>(); // To track tokens by their grid position
    private Map<Point, String> tokenData = new LinkedHashMap<>();
    private JLabel[] playerStarLabels = new JLabel[4]; // Default to 4 players
    private Map<String, JLabel> magicalComponentLabels;
    private NetworkManager networkManager;
    private boolean isHost;
    private int assignedPlayerIndex;
    private JLabel starLabel;
    private JTextArea chatArea;
    private JPanel chatPanel;
    private JLabel currentPlayerLabel; // To display the current player's turn
    private JLabel[] playerLabels; // Array to store player JLabels
    private static final int cellSize = 60; // Size of a grid cell
    private static final int playerSize = 25; // Size of a player label

    /**
     * Initializes the MainGame instance, setting up either a host or a client based
     * on the provided parameters.
     *
     * @param isHost    Specifies if this instance is hosting the game. {@code true}
     *                  if hosting, {@code false} if a client.
     * @param gameState The initial game state to initialize the game for the
     *                  client. {@code null} for host initialization.
     */
    public MainGame(boolean isHost, GameState gameState) {
        this.isHost = isHost;
        currentPlayerLabel = new JLabel("Initializing..."); // Initialize the label early
        System.out.println("MainGame constructor called. isHost: " + isHost);
        chatPanel = createChatPanel();

        if (isHost) {
            System.out.println("Host detected. Initializing players and token data...");
            populateTokenData();
            Player[] players = {
                    new Player("Player 1", new Point(3, 3), Color.RED),
                    new Player("Player 2", new Point(3, 5), Color.BLUE),
                    new Player("Player 3", new Point(5, 3), Color.GREEN),
                    new Player("Player 4", new Point(5, 5), Color.YELLOW)
            };

            this.gameBoard = new GameBoard(9, players);
            this.gameController = new GameController(gameBoard, this);
            this.assignedPlayerIndex = 0;
        } else {
            System.out.println("Client detected. Waiting for GameState...");
            if (gameState != null) {
                initializeFromGameState(gameState);
                if (this.gameController == null) {
                    this.gameController = new GameController(this.gameBoard, this);
                    System.out.println("Client: GameController initialized.");
                }

            } else {
                this.gameBoard = null; // Will be set later
                this.gameController = null; // Will be set laters
            }
        }
    }

    /**
     * Initializes the game state from a provided {@link GameState} object.
     * This method updates the game board, game controller, token data, and player
     * labels.
     *
     * @param gameState The {@link GameState} object containing the game board,
     *                  players, current player index,
     *                  and token data to synchronize the client with the host.
     */
    public void initializeFromGameState(GameState gameState) {

        this.gameBoard = new GameBoard(gameState.getTiles(), gameState.getPlayers());
        this.gameController = new GameController(gameBoard, this);
        this.gameController.setCurrentPlayerIndex(gameState.getCurrentPlayerIndex());
        this.assignedPlayerIndex = gameState.getAssignedPlayerIndex();

        Player[] players = gameState.getPlayers();
        playerStarLabels = new JLabel[players.length];

        for (int i = 0; i < players.length; i++) {
            playerStarLabels[i] = new JLabel();
            updateStars(playerStarLabels[i], players[i].getStarsCollected());
        }
        updateTurnLabel(gameState.getCurrentPlayerIndex());

        // Sync tokenData
        tokenData.clear();
        tokenData.putAll(gameState.getTokenData());

        // Populate `tokenMap` for rendering
        this.tokenMap.clear();
        for (Map.Entry<Point, String> entry : tokenData.entrySet()) {
            Point position = entry.getKey();
            String tokenPath = entry.getValue();
            JLabel tokenLabel = createTokenLabel(tokenPath, 20);

            int x = (position.y + 1) * cellSize + (cellSize - 20) / 2;
            int y = (position.x + 1) * cellSize + (cellSize - 20) / 2;
            tokenLabel.setBounds(x, y, 20, 20);

            tokenMap.put(position, tokenLabel);
        }
    }

    /**
     * Alternative constructor for {@link MainGame}, primarily used for initializing
     * with an existing {@link GameController}.
     *
     * @param gameController The {@link GameController} instance to associate with
     *                       this {@link MainGame}.
     */
    public MainGame(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * The main entry point of the game application.
     * Presents the user with options to play locally, host a game, or join a game.
     *
     * <p>
     * Based on the user's choice, this method initializes the game in one of three
     * modes:
     * <ul>
     * <li><b>Play Locally:</b> Starts a game instance for local play.</li>
     * <li><b>Host a Game:</b> Sets up the game as the host and waits for clients to
     * join.</li>
     * <li><b>Join a Game:</b> Connects to a host and joins the game as a
     * client.</li>
     * </ul>
     * If the user cancels or closes the dialog, the program exits.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            String[] options = { "Play Locally", "Host a Game", "Join a Game" };
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option to start the game:",
                    "Game Setup",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            MainGame game;
            switch (choice) {
                case 0: // Play Locally
                    game = new MainGame(true, null);
                    game.createAndShowGame(game);
                    game.updateUI();
                    break;

                case 1: // Host a Game
                    game = new MainGame(true, null);
                    game.hostGame();
                    break;

                case 2: // Join a Game
                    game = new MainGame(false, null);
                    game.joinGame();
                    break;

                default:
                    System.exit(0); // Exit on cancel or close
            }
        });
    }

    /**
     * Initializes and displays the game window, setting up the user interface
     * components.
     * 
     * This method creates a JFrame and uses a JLayeredPane to layer multiple panels
     * for
     * the game, including the background, grid, logo, game area, insert panel, and
     * chat box.
     * 
     * It performs the following actions:
     * <ul>
     * <li>Creates a JFrame for the game window.</li>
     * <li>Sets up a layered pane to manage the layout of different components.</li>
     * <li>Adds a background panel at the bottom layer.</li>
     * <li>Places a grid panel on top of the background.</li>
     * <li>Adds a chat panel in the bottom-right corner of the window.</li>
     * <li>Creates and positions a logo panel at the top of the window.</li>
     * <li>Sets up a game area panel for gameplay elements.</li>
     * <li>Adds an insert panel for inputting grid elements.</li>
     * <li>Creates a menu bar and adds it to the frame.</li>
     * </ul>
     * 
     * The frame is then packed to fit the components, centered on the screen, and
     * made
     * non-resizable. Finally, the frame is made visible.
     */
    public void createAndShowGame(MainGame mainGame) {
        JFrame frame = mainGame.createFrame();

        // Create a layered pane to hold background and grid
        mainGame.layeredPane = new JLayeredPane();
        mainGame.layeredPane.setPreferredSize(new Dimension(1280, 750)); // Set size of the game window

        // Add background panel
        mainGame.layeredPane.add(mainGame.createBackgroundPanel(), Integer.valueOf(0)); // Background at layer 0

        // Add grid with players and tokens
        JLayeredPane layeredGridWithPlayers = mainGame.createGridWithPlayersAndTokens();
        mainGame.layeredPane.add(layeredGridWithPlayers, Integer.valueOf(1)); // Add the grid with players

        // Add chat panel
        chatPanel = mainGame.createChatPanel();
        chatPanel.setBounds(970, 490, 300, 250); // Positioning the chat panel in the bottom-right corner
        mainGame.layeredPane.add(chatPanel, Integer.valueOf(2)); // Chatbox on top at layer 2

        // Add logo panel
        JPanel logoPanel = mainGame.createLogoPanel();
        logoPanel.setBounds(0, 0, 1280, 100); // Position the logo at the top of the screen
        mainGame.layeredPane.add(logoPanel, Integer.valueOf(3)); // Logo on top at layer 3

        // Add magical components panel
        JPanel magicalComponentsPanel = mainGame.createMagicalComponentsPanel();
        magicalComponentsPanel.setBounds(970, 300, 300, 150); // Position between chatPanel and gameAreaPanel
        mainGame.layeredPane.add(magicalComponentsPanel, Integer.valueOf(3));

        // Add game area panel
        mainGame.gameAreaPanel = mainGame.createGameAreaPanel();
        mainGame.gameAreaPanel.setBounds(970, 50, 300, 200); // Positioning game area panel
        mainGame.layeredPane.add(mainGame.gameAreaPanel, Integer.valueOf(3)); // Add game area at layer 3

        // Add insert panel
        String initialImagePath = "Pictures/GridCell/hallway_horiz.png"; // Initial image for insert panel
        mainGame.insertPanel = mainGame.createInsertPanel(initialImagePath);
        mainGame.insertPanel.setBounds(640, 450, 200, 150); // Position insert panel
        mainGame.layeredPane.add(mainGame.insertPanel, Integer.valueOf(3)); // Insert panel above game area

        // Set up menu bar
        JMenuBar menubar = mainGame.createMenuBar();
        frame.setJMenuBar(menubar);

        // Add the layered pane to the frame and display
        frame.add(mainGame.layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        // Setup key listeners
        mainGame.setupKeyListeners(frame, mainGame.gameController, this);

    }

    /**
     * Sets the game controller for this game instance.
     *
     * @param controller the {@link GameController} to set.
     */
    public void setController(GameController controller) {
        this.gameController = controller;
    }

    /**
     * Retrieves the grid panel representing the game board's grid.
     *
     * @return the {@link JPanel} representing the grid panel.
     */
    public JPanel getGridPanel() {
        return gridPanel;
    }

    /**
     * Checks if the current game instance is running as the host.
     *
     * @return {@code true} if the game instance is the host, {@code false}
     *         otherwise.
     */
    public boolean isHost() {
        return this.isHost;
    }

    /**
     * Retrieves the assigned player index for the current game instance.
     *
     * @return the index of the player assigned to this instance.
     */
    public int getAssignedPlayerIndex() {
        return this.assignedPlayerIndex;
    }

    /**
     * Retrieves the token map that associates positions on the grid with token
     * labels.
     *
     * @return a {@link Map} where keys are {@link Point} objects representing
     *         positions,
     *         and values are {@link JLabel} objects representing tokens.
     */
    public Map<Point, JLabel> getTokenMap() {
        return tokenMap;
    }

    /**
     * Retrieves the layered pane used for managing tokens and other layered UI
     * components.
     *
     * @return the {@link JLayeredPane} for managing layered UI elements.
     */
    public JLayeredPane getLayeredPane() {
        return layeredPane;
    }

    /**
     * Retrieves the network manager responsible for managing network interactions.
     *
     * @return the {@link NetworkManager} instance managing the network.
     */
    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    /**
     * Retrieves the game board object representing the current game state.
     *
     * @return the {@link GameBoard} representing the current game board.
     */
    public GameBoard getGameBoard() {
        return this.gameBoard;
    }

    /**
     * Retrieves the token data for the current game state.
     *
     * This method creates a mapping of grid positions to the file paths of the
     * token images
     * at those positions. It is used for synchronizing token data between game
     * instances.
     *
     * @return a {@link Map} where keys are {@link Point} objects representing
     *         positions on the grid,
     *         and values are {@link String} objects representing the file paths of
     *         the token images.
     */
    public Map<Point, String> getTokenData() {
        Map<Point, String> tokenData = new HashMap<>();
        for (Map.Entry<Point, JLabel> entry : tokenMap.entrySet()) {
            String tokenPath = ((ImageIcon) entry.getValue().getIcon()).getDescription();
            tokenData.put(entry.getKey(), tokenPath);
        }
        return tokenData;
    }

    /**
     * Retrieves the size of each cell in the grid.
     *
     * @return an integer representing the size of a grid cell in pixels.
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Retrieves the map of magical component labels.
     *
     * This method returns a mapping of magical component file paths to their
     * associated {@link JLabel}s.
     * It is used for managing the state and display of magical components in the
     * game.
     *
     * @return a {@link Map} where keys are {@link String} objects representing
     *         magical component file paths,
     *         and values are {@link JLabel} objects representing the UI labels for
     *         those components.
     */
    public Map<String, JLabel> getMagicalComponentLabels() {
        return magicalComponentLabels;
    }

    /**
     * Creates the main game frame for the Labyrinth game.
     * 
     * This method initializes a new JFrame with the title "Labyrinth Game with
     * Specific Image Distribution".
     * It sets the default close operation to exit the application when the frame is
     * closed.
     * 
     * @return the newly created JFrame instance for the game.
     */
    private JFrame createFrame() {
        JFrame frame = new JFrame("Master Labyrinth - Bhagyesh Salvi ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    /**
     * Creates a background panel with a custom background image.
     * 
     * This method initializes a JPanel and overrides its `paintComponent` method to
     * draw a background image.
     * The image is loaded from the specified path and scaled to fit the panel's
     * dimensions.
     * 
     * @return the newly created JPanel instance that serves as the background for
     *         the game.
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
        backgroundPanel.setBounds(0, 0, 1280, 750); // Set size of background panel
        return backgroundPanel;
    }

    /**
     * Checks if a specific position on the grid is an interactive position.
     *
     * Interactive positions are determined by specific rows and columns located on
     * the edges of the grid that allow tile insertion during gameplay.
     *
     * @param row the row index of the position to check.
     * @param col the column index of the position to check.
     * @return {@code true} if the position is interactive, {@code false} otherwise.
     */
    private boolean isInteractivePosition(int row, int col) {
        if ((row == 2 || row == 4 || row == 6) && (col == 0 || col == 8)) {
            return true;
        }

        if ((col == 2 || col == 4 || col == 6) && (row == 0 || row == 8)) {
            return true;
        }

        return false;
    }

    /**
     * Creates the main grid panel for the game.
     *
     * This method initializes a 9x9 grid using a {@link JPanel} with
     * {@link GridBagLayout}.
     * Each cell in the grid is represented by a {@link JLayeredPane}. Interactive
     * positions
     * on the grid are clickable and support tile insertion, while other positions
     * are static.
     *
     * @return a {@link JPanel} representing the 9x9 game grid.
     */
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
                    boolean isInteractive = isInteractivePosition(row, col);
                    JLayeredPane cellPane = isInteractive
                            ? createInteractiveInsertLayeredPane(tile.getImagePath(), row, col, cellSize)
                            : createLayeredPane(tile.getImagePath(), cellSize);
                    gridPanel.add(cellPane, gbc);
                } else {
                    JLayeredPane emptyCell = createLayeredPane(null, cellSize);
                    gridPanel.add(emptyCell, gbc);
                }
            }
        }
        gridPanel.setFocusable(true);
        return gridPanel;
    }

    /**
     * Creates a {@link JLayeredPane} for displaying a grid cell.
     *
     * Each cell in the game grid is represented as a {@link JLayeredPane}
     * containing an image.
     * This method handles both tiles with valid images and empty cells.
     *
     * @param imagePath the path to the image to be displayed in the cell. If
     *                  {@code null}, the cell is empty.
     * @param cellSize  the dimensions of the cell.
     * @return a {@link JLayeredPane} representing the cell.
     */
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

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New"));
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        fileMenu.addSeparator(); // Adds a separator line
        fileMenu.add(new JMenuItem("Exit"));

        // Game Menu
        JMenu gameMenu = new JMenu("Game");

        gameMenu.add(new JMenuItem("Start Game"));
        gameMenu.add(new JMenuItem("Pause"));
        gameMenu.add(new JMenuItem("Undo Move"));
        gameMenu.add(new JMenuItem("End Game"));

        // Help Menu
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
     * to have a transparent background, enabling the underlying game graphics to be
     * visible.
     * 
     * @return a JPanel containing the chat area, message input field, and send
     *         button.
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(150, 200)); // Size for chat panel
        chatPanel.setOpaque(false); // Transparent background

        // Text area to display chat messages
        if (chatArea == null) {

            chatArea = new JTextArea();
            chatArea.setEditable(false); // Users shouldn't edit chat history
            chatArea.setLineWrap(true); // Wrap text to the next line
            chatArea.setWrapStyleWord(true);
            chatArea.setForeground(Color.WHITE); // Set text color to white
            chatArea.setFont(new Font("Arial", Font.BOLD, 14)); // Set text to bold
            chatArea.setOpaque(false); // Transparent background for text area

        }
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(false); // Transparent scroll pane
        scrollPane.getViewport().setOpaque(false); // Transparent viewport
        // Text field for typing messages
        JTextField messageField = new JTextField();
        messageField.setForeground(Color.BLACK); // Set text color to white
        messageField.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text for input field

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
                String formattedMessage = "Player " + (assignedPlayerIndex + 1) + ": " + message;
                chatArea.append(formattedMessage + "\n"); // Display locally
                messageField.setText(""); // Clear the input field

                // Send message over the network
                if (networkManager != null) {
                    String networkMessage = "[ID]#CHAT#" + (assignedPlayerIndex + 1) + ":" + message;
                    if (isHost) {
                        networkManager.broadcastChatMessage(networkMessage);
                    } else {
                        try {
                            networkManager.getClient().sendMessage(networkMessage);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }

                // Log message in the console
                System.out.println("[ID]#CHAT#" + (assignedPlayerIndex + 1) + ":" + message);
            }
            SwingUtilities.getWindowAncestor(chatPanel).requestFocusInWindow();
        });

        // Allow sending messages with the Enter key
        messageField.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                String formattedMessage = "Player " + (assignedPlayerIndex + 1) + ": " + message;
                chatArea.append(formattedMessage + "\n"); // Display locally
                messageField.setText(""); // Clear the input field

                // Send message over the network
                if (networkManager != null) {
                    String networkMessage = "[ID]#CHAT#" + (assignedPlayerIndex + 1) + ":" + message;
                    if (isHost) {
                        networkManager.broadcastChatMessage(networkMessage);
                    } else {
                        try {
                            networkManager.getClient().sendMessage(networkMessage);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }

                // Log message in the console
                System.out.println("[ID]#CHAT#" + (assignedPlayerIndex + 1) + ":" + message);
            }
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

    public JTextArea getChatArea() {
        return chatArea;
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
        logoPanel.setOpaque(false); // Make the logo panel transparent

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
     * This method constructs a {@link JPanel} containing information for up to four
     * players,
     * including their names and current star counts. Each player's details are
     * displayed
     * in a vertical layout with a separate panel. Additionally, a turn indicator is
     * placed
     * at the top of the game area panel to highlight the current player's turn.
     * 
     * The panel's background is set to be transparent, allowing for dynamic visuals
     * or
     * background images to be visible.
     *
     * @return a {@link JPanel} representing the game area with player names, star
     *         counts,
     *         and a turn indicator.
     */
    private JPanel createGameAreaPanel() {
        JPanel gameAreaPanel = new JPanel();
        gameAreaPanel.setLayout(new BorderLayout()); // Use BorderLayout to allow for a turn indicator at the top
        gameAreaPanel.setOpaque(false); // Make the background transparent if desired

        // Create a panel for the turn indicator
        JPanel turnIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        turnIndicatorPanel.setOpaque(false); // Transparent background

        currentPlayerLabel = new JLabel("Current Turn: Player 1"); // Initial label text
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentPlayerLabel.setForeground(Color.YELLOW); // Highlight the text
        turnIndicatorPanel.add(currentPlayerLabel);

        // Create the player panel with stars
        JPanel playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new GridLayout(4, 1)); // Vertical layout for 4 players
        playerInfoPanel.setOpaque(false);

        // Player names and star counts
        String[] playerNames = { "Player 1", "Player 2", "Player 3", "Player 4" };
        int[] starCounts = { 0, 0, 0, 0 }; // Initial star counts for each player
        playerStarLabels = new JLabel[playerNames.length]; // Array to store star labels

        for (int i = 0; i < playerNames.length; i++) {
            JPanel playerPanel = new JPanel(); // Panel for each player
            playerPanel.setOpaque(false); // Transparent background
            playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Aligns content to the left

            JLabel nameLabel = new JLabel(playerNames[i]);
            nameLabel.setForeground(Color.WHITE); // Set text color to white
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 24)); // Increase text size

            // Create star labels
            starLabel = new JLabel(); // Initialize empty star label
            starLabel.setForeground(Color.LIGHT_GRAY); // Set star color to light gray
            starLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font size
            updateStars(starLabel, starCounts[i]); // Set initial stars

            playerStarLabels[i] = starLabel; // Store reference to the star label

            playerPanel.add(nameLabel); // Add player name to panel
            playerPanel.add(starLabel); // Add stars to panel

            playerInfoPanel.add(playerPanel); // Add player panel to the overall panel
        }

        // Add the turn indicator and player info to the main panel
        gameAreaPanel.add(turnIndicatorPanel, BorderLayout.NORTH); // Turn indicator at the top
        gameAreaPanel.add(playerInfoPanel, BorderLayout.CENTER); // Player info in the center

        return gameAreaPanel; // Return the complete game area panel
    }

    /**
     * Updates the turn label to reflect the current player's turn.
     *
     * This method modifies the {@code currentPlayerLabel} to indicate which
     * player's
     * turn it is. The label is updated with the player's index (1-based) to display
     * a message like "Player X's Turn".
     *
     * @param currentPlayerIndex the index of the current player (0-based).
     */
    public void updateTurnLabel(int currentPlayerIndex) {
        currentPlayerLabel.setText("Player " + (currentPlayerIndex + 1) + "'s Turn");
        System.out.println("Updated turn label to: Player " + (currentPlayerIndex + 1));
    }

    /**
     * Updates the turn indicator to visually highlight the current player.
     *
     * This method adjusts the appearance of player star labels to highlight the
     * current player's stars while dimming others. The highlighting is done
     * by changing the text color of the current player's label.
     *
     * @param currentPlayerIndex the index of the current player (0-based).
     */
    public void updateTurnIndicator(int currentPlayerIndex) {
        for (int i = 0; i < playerStarLabels.length; i++) {
            if (i == currentPlayerIndex) {
                playerStarLabels[i].setForeground(Color.YELLOW); // Highlight current player
            } else {
                // playerStarLabels[i].setForeground(Color.LIGHT_GRAY); // Optional: Dim others
            }
        }
    }

    /**
     * Updates the star label to display the given number of stars.
     *
     * This method dynamically creates a string of Unicode star characters
     * (`\u2605`)
     * based on the specified count and updates the provided {@link JLabel}.
     * The label's font is set to "Arial Unicode MS" to ensure proper rendering
     * of the Unicode stars, and the text color is set to yellow to resemble stars.
     *
     * @param starLabel the {@link JLabel} to update with the star count.
     * @param count     the number of stars to display (non-negative).
     */
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

    /**
     * Updates the star count for a specific player.
     *
     * This method updates the star count displayed for a given player by modifying
     * the associated {@link JLabel}. The star count is represented using Unicode
     * star characters (`\u2605`). If the provided player index is invalid, the
     * method
     * logs an error and exits without making changes.
     *
     * @param playerIndex  the index of the player (0-based) whose stars are to be
     *                     updated.
     * @param newStarCount the new star count for the player (non-negative).
     */
    public void updatePlayerStars(int playerIndex, int newStarCount) {
        if (playerIndex < 0 || playerIndex >= playerStarLabels.length) {
            System.err.println("Invalid player index: " + playerIndex);
            return;
        }

        starLabel = playerStarLabels[playerIndex]; // Get the star label for the player
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

    /**
     * Normalizes a file path string for consistent usage.
     *
     * This method trims leading and trailing spaces, replaces backslashes (`\`)
     * with forward slashes (`/`), and converts the string to lowercase. It is
     * useful for standardizing file paths across different platforms.
     *
     * @param path the file path to normalize.
     * @return the normalized file path as a {@link String}.
     */
    private static String normalizePath(String path) {
        return path.trim().replace("\\", "/").toLowerCase(); // Trim spaces, unify separators, and lowercase
    }

    /**
     * Collects a token at the specified position for the current player.
     *
     * This method checks if there is a token at the provided position. If found, it
     * removes
     * the token from the UI and updates the player's star count. If the token is a
     * magic card,
     * it applies a grayscale effect to its corresponding magic card label and
     * updates its status.
     * After processing the token, the game state is broadcasted to all clients if
     * the game is
     * hosted on the server.
     *
     * @param playerIndex the index of the player collecting the token (0-based).
     * @param position    the {@link Point} position on the grid where the token is
     *                    located.
     */
    public void collectTokenIfPresent(int playerIndex, Point position) {
        JLabel tokenLabel = tokenMap.get(position);
        if (tokenLabel == null) {
            System.err.println("No token found at position: " + position);
            return;
        }

        String tokenPath = ((ImageIcon) tokenLabel.getIcon()).getDescription();

        Map<String, Boolean> tokenPaths = GameUtils.generateTokenPaths();
        boolean isMagicCard = tokenPaths.getOrDefault(tokenPath, true);
        JLabel magicalLabel = magicalComponentLabels.get(tokenPath);

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

        if (this.isHost() && this.getNetworkManager() != null) {
            GameState updatedState = new GameState(
                    gameBoard.getTiles(),
                    gameBoard.getPlayers(),
                    gameController.getCurrentPlayerIndex(),
                    this.getTokenData(),
                    this.getAssignedPlayerIndex());
            this.getNetworkManager().broadcastGameState(updatedState);
            System.out.println("Host: Broadcasting updated game state after token collection.");
        }
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
        insertPanel.setPreferredSize(new Dimension(80, 150)); // Adjust panel size to fit two buttons
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

        // Create a panel to hold buttons (for better layout)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.setOpaque(false);

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
        buttonPanel.add(rotateButton);

        // Add the "End Turn" button
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.setPreferredSize(new Dimension(80, 35));
        endTurnButton.setFont(new Font("Arial", Font.BOLD, 17));
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setOpaque(false);
        endTurnButton.setContentAreaFilled(false);
        endTurnButton.setBorderPainted(false);
        endTurnButton.addActionListener(e -> {
            // Call the GameController to switch to the next player
            gameController.nextTurn();

            SwingUtilities.getWindowAncestor(gridPanel).requestFocusInWindow();
        });
        buttonPanel.add(endTurnButton);

        // Add the button panel to the insert panel
        insertPanel.add(buttonPanel, BorderLayout.SOUTH);

        return insertPanel;
    }

    /**
     * Updates the image displayed in a {@link JLabel} based on the provided
     * {@link Tile}.
     *
     * This method sets the icon of the given JLabel to the ImageIcon associated
     * with the Tile.
     * If the Tile has no valid image, the label's icon is cleared, and an error
     * message is logged.
     *
     * @param label the {@link JLabel} to update with the tile's image.
     * @param tile  the {@link Tile} containing the image to set.
     */
    private void updateTileImageLabel(JLabel label, Tile tile) {
        if (tile.getImageIcon() != null) {
            label.setIcon(tile.getImageIcon()); // Use the latest ImageIcon
        } else {
            System.err.println("Error: Tile image is null or invalid.");
            label.setIcon(null); // Clear the label if the image is invalid
        }
    }

    /**
     * Populates the token data with randomized token placements on the game board.
     *
     * This method initializes a predefined set of token positions on the game board
     * and assigns shuffled token paths to each position. It clears any existing
     * token data
     * before populating the new data.
     *
     * The tokens and their corresponding positions are stored in the `tokenData`
     * map,
     * which maps a {@link Point} (position on the board) to the token's image path.
     * The tokens are shuffled to ensure randomness in their placement.
     *
     * The predefined positions are designed for a specific board layout and include
     * various locations around the board's perimeter.
     *
     */
    private void populateTokenData() {
        System.out.println("Populating token data...");
        tokenData.clear();

        List<Point> tokenPositions = Arrays.asList(
                new Point(2, 2), new Point(2, 3), new Point(2, 4), new Point(2, 5), new Point(2, 6),
                new Point(3, 6), new Point(4, 6), new Point(5, 6), new Point(6, 6),
                new Point(6, 5), new Point(6, 4), new Point(6, 3), new Point(6, 2),
                new Point(5, 2), new Point(4, 2), new Point(3, 2));

        List<Map.Entry<String, Boolean>> shuffledTokens = new ArrayList<>(GameUtils.generateTokenPaths().entrySet());
        Collections.shuffle(shuffledTokens);

        for (int i = 0; i < tokenPositions.size(); i++) {
            Point position = tokenPositions.get(i);
            String tokenPath = shuffledTokens.get(i).getKey();
            tokenData.put(position, tokenPath);
        }

        System.out.println("Token data populated: " + tokenData);
    }

    /**
     * Creates a layered pane representing the game grid with players and tokens.
     * 
     * This method initializes the game board's grid and adds player and token
     * elements
     * as separate layers in a {@link JLayeredPane}. The grid is added to the bottom
     * layer,
     * while tokens and players are placed on higher layers for visual clarity.
     * 
     * - Players are positioned based on their starting coordinates and represented
     * by
     * custom-colored player pieces.
     * - Tokens are positioned based on the predefined token data, with their images
     * dynamically loaded and resized.
     * 
     * @return a {@link JLayeredPane} containing the grid, players, and tokens.
     */
    private JLayeredPane createGridWithPlayersAndTokens() {
        JLayeredPane layeredGridPane = new JLayeredPane();
        layeredGridPane.setPreferredSize(new Dimension(650, 650));
        layeredGridPane.setBounds(50, 50, 650, 650);

        gridPanel = createGridPanel();
        gridPanel.setBounds(0, 0, 650, 650);
        layeredGridPane.add(gridPanel, Integer.valueOf(0));

        System.out.println("Initialzing player");
        Player[] players = gameBoard.getPlayers();
        playerLabels = new JLabel[players.length];

        for (int i = 0; i < players.length; i++) {
            Point position = players[i].getPosition();
            JLabel playerLabel = createPlayerLabel(i + 1, playerSize);

            int x = (position.y + 1) * cellSize + (cellSize - playerSize) / 2;
            int y = (position.x + 1) * cellSize + (cellSize - playerSize) / 2;
            playerLabel.setBounds(x, y, playerSize, playerSize);

            layeredGridPane.add(playerLabel, Integer.valueOf(2));
            playerLabels[i] = playerLabel;
        }

        System.out.println("Initializing tokens...");
        System.out.println("Token data: " + tokenData);

        for (Map.Entry<Point, String> entry : tokenData.entrySet()) {
            Point position = entry.getKey();
            String tokenPath = entry.getValue();

            JLabel tokenLabel = createTokenLabel(tokenPath, 20);

            int x = (position.y + 1) * cellSize + (cellSize - 20) / 2;
            int y = (position.x + 1) * cellSize + (cellSize - 20) / 2;
            tokenLabel.setBounds(x, y, 20, 20);

            System.out.println(
                    "Adding token at " + position + " with path " + tokenPath + " -> Pixel (" + x + ", " + y + ")");
            layeredGridPane.add(tokenLabel, Integer.valueOf(1));
            tokenMap.put(position, tokenLabel);
        }

        return layeredGridPane;
    }

    /**
     * Creates a {@link JLabel} representing a player's piece on the game board.
     * 
     * The method dynamically loads an image corresponding to the player's number,
     * scales it to the specified size, and applies it to a {@link JLabel}.
     * 
     * @param playerNumber the player's number (1-based), determining the color of
     *                     the piece.
     * @param size         the size (in pixels) of the player piece.
     * @return a {@link JLabel} displaying the player's piece.
     */
    private JLabel createPlayerLabel(int playerNumber, int size) {
        // Use different images/colors for each player
        String[] playerColors = { "red", "blue", "green", "yellow" };
        String imagePath = "Pictures/PlayerPiece/" + playerColors[playerNumber - 1] + ".png";
        ImageIcon playerIcon = new ImageIcon(imagePath);

        // Scale the image
        Image scaledImage = playerIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        JLabel playerLabel = new JLabel(new ImageIcon(scaledImage));
        playerLabel.setOpaque(false); // Transparent background
        return playerLabel;
    }

    /**
     * Creates a {@link JLabel} representing a token on the game board.
     * 
     * The method loads a token image from the specified file path, resizes it to
     * fit the specified token size, and applies it to a {@link JLabel}. The token's
     * file path is stored as the description of the associated {@link ImageIcon}.
     * 
     * @param tokenPath the file path of the token's image.
     * @param tokenSize the size (in pixels) of the token icon.
     * @return a {@link JLabel} displaying the token.
     */
    public JLabel createTokenLabel(String tokenPath, int tokenSize) {
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

    /**
     * Creates a panel to display magical components in the game.
     * 
     * This method initializes and arranges three magical components represented by
     * images in
     * a grid layout. Each image is loaded, resized, and added to the panel as a
     * {@link JLabel}.
     * If an image fails to load, a placeholder label indicating an error is added
     * instead.
     * 
     * Additionally, references to the labels are stored in a map
     * (`magicalComponentLabels`)
     * for future updates, such as marking a component as "collected."
     * 
     * @return a {@link JPanel} containing the magical components.
     */
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
                JLabel errorLabel = new JLabel("Error");
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                errorLabel.setVerticalAlignment(SwingConstants.CENTER);
                magicalPanel.add(errorLabel);
            }
        }

        return magicalPanel;
    }

    /**
     * Updates the UI position of a player's piece on the game board.
     * 
     * This method calculates the new pixel coordinates of the player's piece
     * based on the given grid position and updates the corresponding {@link JLabel}
     * to reflect the new position on the screen. The label's position is adjusted
     * to center the player icon within the grid cell.
     * 
     * @param playerIndex the index of the player whose position is being updated.
     * @param newPosition the new grid position of the player as a {@link Point}.
     */
    public void updatePlayerPosition(int playerIndex, Point newPosition) {
        JLabel playerLabel = playerLabels[playerIndex];

        // Calculate new position in the grid
        int x = (newPosition.y + 1) * cellSize + (cellSize - playerSize) / 2; // Offset by +1
        int y = (newPosition.x + 1) * cellSize + (cellSize - playerSize) / 2; // Offset by +1

        System.out.println("Updating UI for Player " + playerIndex +
                " to new position: " + newPosition + " (x: " + x + ", y: " + y + ")");

        // Update the label's position
        playerLabel.setBounds(x, y, playerSize, playerSize);

        playerLabel.repaint(); // Refresh the UI
        System.out.println("Player label updated to pixel position: (x: " + x + ", y: " + y + ")");
    }

    /**
     * Determines if the host or client can perform an action based on the turn.
     * 
     * This method ensures that:
     * - The host can only act as Player 1 (with `assignedPlayerIndex == 0`).
     * - In a host-client setup, a player can only act during their turn.
     * 
     * @param assignedPlayerIndex the index of the player assigned to this host or
     *                            client.
     * @param currentPlayerIndex  the index of the player whose turn it is.
     * @param isHost              a boolean indicating whether the player is the
     *                            host.
     * @return {@code true} if the player can act; {@code false} otherwise.
     */
    public boolean canHostAct(int assignedPlayerIndex, int currentPlayerIndex, boolean isHost) {
        if (isHost) {
            // Host can only act as Player 1 (assignedPlayerIndex == 0)
            if (assignedPlayerIndex != 0) {
                System.out.println("Host cannot act as Player " + (currentPlayerIndex + 1));
                return false;
            }
        } else {
            // In host-client, validate `assignedPlayerIndex`
            if (assignedPlayerIndex != currentPlayerIndex) {
                System.out.println("Invalid action: It's not your turn!");
                return false;
            }
        }
        return true; // Player can act
    }

    private boolean isKeyListenerAdded = false;

    /**
     * Sets up key listeners for player movement controls.
     * 
     * This method attaches a {@link KeyListener} to the given {@link JFrame} to
     * handle
     * player movement using the arrow keys. It validates the turn and either sends
     * the move to the host (if the player is a client) or processes the move
     * locally
     * (if the player is the host).
     * 
     * Key mappings:
     * - Arrow keys (`UP`, `DOWN`, `LEFT`, `RIGHT`) are mapped to movement
     * directions.
     * 
     * @param frame      the {@link JFrame} to which the key listener is attached.
     * @param controller the {@link GameController} responsible for handling
     *                   movements.
     * @param mainGame   the {@link MainGame} instance containing game context and
     *                   state.
     */
    private void setupKeyListeners(JFrame frame, GameController controller, MainGame mainGame) {
        if (isKeyListenerAdded)
            return; // Avoid adding the listener multiple times
        isKeyListenerAdded = true;

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String direction = switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> "up";
                    case KeyEvent.VK_DOWN -> "down";
                    case KeyEvent.VK_LEFT -> "left";
                    case KeyEvent.VK_RIGHT -> "right";
                    default -> null;
                };

                if (direction != null) {
                    int currentPlayerIndex = controller.getCurrentPlayerIndex();
                    int assignedPlayerIndex = mainGame.getAssignedPlayerIndex();
                    boolean isHost = mainGame.isHost();

                    System.out.println("Key pressed. Current Player Index: " + currentPlayerIndex +
                            ", Assigned Player Index: " + assignedPlayerIndex);

                    if (currentPlayerIndex != assignedPlayerIndex) {
                        System.out.println("Invalid action: It's not your turn!");
                        return; // Do nothing if it's not the client's turn
                    }

                    if (!isHost) {
                        // Client sends the move to the host
                        mainGame.getNetworkManager().getClient().sendPlayerMove(assignedPlayerIndex, direction);
                    } else {
                        // Host processes its own move
                        try {
                            controller.handleMovement(currentPlayerIndex, direction);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        System.out.println("Key Listener Attached");
    }

    /**
     * Displays a dialog box informing the user of an invalid move.
     * 
     * This method is invoked when a player attempts to move to an invalid tile,
     * such as one that is occupied or out of bounds. The dialog displays a warning
     * message with details about the issue.
     */
    public void showInvalidMoveDialog() {
        JOptionPane.showMessageDialog(
                null,
                "You cannot move to that tile. It's occupied or out of bounds!",
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Retrieves the insert panel used for tile manipulation.
     * 
     * The insert panel is a UI component that displays the currently selected tile
     * and provides the functionality to insert it into the game grid. This method
     * returns the panel for use in other parts of the application.
     * 
     * @return a {@link JPanel} representing the insert panel.
     */
    public JPanel getInsertPanel() {
        return insertPanel;
    }

    /**
     * Hosts a multiplayer game by starting a server.
     * 
     * This method prompts the user to input a port number and initializes the game
     * for hosting. It sets up the token data, creates an initial game state, and
     * starts the host server on the specified port. The network manager is also
     * configured for managing client connections.
     * 
     * If hosting is successful, the game UI is launched, and the server begins
     * listening
     * for client connections.
     */
    public void hostGame() {
        String port = JOptionPane.showInputDialog(null, "Enter port to host the game:", "Host Game",
                JOptionPane.PLAIN_MESSAGE);
        if (port != null && !port.isEmpty()) {
            try {
                int portNumber = Integer.parseInt(port);

                populateTokenData(); // Ensure consistent token data initialization

                GameState initialState = new GameState(
                        gameBoard.getTiles(),
                        gameBoard.getPlayers(),
                        gameController.getCurrentPlayerIndex(),
                        tokenData,
                        assignedPlayerIndex);

                Host host = new Host(portNumber, gameController, gameBoard, this);
                host.startServer(initialState);

                networkManager = new NetworkManager(host, null);

                JOptionPane.showMessageDialog(null, "Hosting game on port: " + port, "Host Game",
                        JOptionPane.INFORMATION_MESSAGE);

                createAndShowGame(this);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error starting host: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Port is required to host a game!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Joins a multiplayer game hosted on a server.
     * 
     * This method prompts the user to input the host's IP address and port number.
     * It establishes a connection to the server, retrieves the game state, and
     * initializes the client-side game. The game controller and network manager
     * are configured for handling updates and interactions during the game.
     * 
     * If the connection is successful, the client UI is launched, and the game
     * state
     * is synchronized with the host.
     */
    public void joinGame() {
        JTextField ipField = new JTextField();
        JTextField portField = new JTextField();

        Object[] fields = {
                "Enter Host IP:", ipField,
                "Enter Host Port:", portField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "Join Game", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String ip = ipField.getText().trim();
            String port = portField.getText().trim();

            if (!ip.isEmpty() && !port.isEmpty()) {
                try {
                    int portNumber = Integer.parseInt(port);

                    Client client = new Client(ip, portNumber, this);

                    // Receive GameState from the host
                    GameState gameState = client.receiveGameState();
                    if (gameState == null) {
                        throw new IOException("Failed to receive game state from host.");
                    }

                    // Initialize the MainGame with the received GameState
                    MainGame joinedGame = new MainGame(false, gameState);

                    // Pass the initialized GameController to the client
                    JTextArea chatArea = joinedGame.getChatArea();
                    client.listenForUpdates(joinedGame.gameController, chatArea);

                    // Set up NetworkManager
                    networkManager = new NetworkManager(null, client);
                    joinedGame.networkManager = networkManager;

                    // Launch the client's game
                    joinedGame.createAndShowGame(joinedGame);

                    JOptionPane.showMessageDialog(null, "Connected to host: " + ip + ":" + port, "Join Game",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to join game: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Host IP and Port are required to join a game!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Synchronizes the client game state with the received game state from the
     * host.
     * 
     * This method updates the game board tiles, player positions, and the current
     * player index
     * based on the provided `GameState` object. It also synchronizes the token data
     * and
     * refreshes the UI to reflect the updated game state.
     * 
     * @param state the `GameState` object containing the updated game state from
     *              the host.
     */
    public void syncState(GameState state) {
        if (state != null) {
            gameBoard.setTiles(state.getTiles()); // Update the board tiles
            gameBoard.setPlayers(state.getPlayers()); // Update the players
            gameController.setCurrentPlayerIndex(state.getCurrentPlayerIndex()); // Update the current player index

            tokenData.clear();
            tokenData.putAll(state.getTokenData());

            // Update the UI
            updateUI();

            System.out.println(
                    "Client: Synchronized GameState. Current turn: Player " + (state.getCurrentPlayerIndex() + 1));
        }
    }

    /**
     * Updates the user interface to reflect the current game state.
     * 
     * This method refreshes the game area panel, grid panel, and other visual
     * components
     * to display the latest game state. It also updates the turn indicator and
     * current
     * player label to highlight the current player's turn.
     */
    public void updateUI() {
        // Update the game area panel to reflect the current state
        gameAreaPanel.removeAll();
        gameAreaPanel.add(createGameAreaPanel()); // Recreate the game area panel
        gameAreaPanel.revalidate();
        gameAreaPanel.repaint();

        // Update the grid or other components if necessary
        gridPanel.revalidate();
        gridPanel.repaint();

        // Update the current player indicator
        updateTurnIndicator(gameController.getCurrentPlayerIndex());
        updateTurnLabel(gameController.getCurrentPlayerIndex());
    }

}
