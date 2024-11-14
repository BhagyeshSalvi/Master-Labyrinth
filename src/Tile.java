import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Tile {
    private String type; // Type of the tile (e.g., straight, corner, T-junction)
    private int rotation; // Current rotation angle (0, 90, 180, 270)
    private boolean isMovable; // Whether the tile can be moved
    private Image image; // Graphical representation of the tile
    private String imagePath;
    private int row;
    private int col;
    private ImageIcon imageIcon;
            
                public Tile(String type, String imagePath) {
                        this.type = type;
                        loadImage(imagePath);
                        this.rotation = 0; // Default rotation is 0
                        this.isMovable = true; // Default to movable
                    }
                    public Tile(String type, Image image) {
                        this.type = type;
                        this.image = image;
                    }
                
     public Tile(String imagePath, int row, int col, boolean isMovable) {
        this.imagePath = imagePath;
        this.row = row;
        this.col = col;
        this.isMovable = isMovable;
        loadImage();
        }

        void loadImage(String imagePath) {
            try {
                if (imagePath != null) {
                    this.image = new ImageIcon(imagePath).getImage();
                    this.imagePath = imagePath; // Ensure the path is stored
                } else {
                    System.err.println("Image path is null for tile type: " + type);
                    this.image = null; // Handle missing image case
                }
            } catch (Exception e) {
                System.err.println("Failed to load image for tile path: " + imagePath);
                this.image = null; // Fallback or placeholder
            }
        }
        

        public boolean hasValidImage() {
            return image != null && imagePath != null;
        }
        
        public void debugTile() {
            System.out.println("Tile Debug - Type: " + type + ", Image Path: " +
                (imagePath != null ? imagePath : "No Path") +
                ", Image: " + (image != null ? "Loaded" : "Null"));
        }
        
    

    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    // Rotate the tile clockwise
    public void rotateClockwise() {
        rotation = (rotation + 90) % 360; // Increment rotation and wrap around at 360
        image = rotateImage(image, 90); // Update the graphical representation
    }

    // Helper method to rotate the image
    private Image rotateImage(Image img, int angle) {
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), img.getWidth(null) / 2.0, img.getHeight(null) / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    private void loadImage() {
        try {
            Image image = new ImageIcon(imagePath).getImage();
            imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Error loading image for tile: " + imagePath);
            imageIcon = null; // Mark as invalid if loading fails
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRotation() {
        return rotation;
    }

    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        if (image == null) {
            System.err.println("Error: Cannot set null image for tile.");
            return;
        }
        this.image = image;
    
        // If an image path is associated, reload it to ensure consistency
        if (imagePath != null && !imagePath.isEmpty()) {
            this.imageIcon = new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        } else {
            System.out.println("Warning: No imagePath associated with this tile. Using provided image.");
            this.imageIcon = new ImageIcon(image);
        }
    }
    

    public String getType() {
        return type;
    }

    public void reloadImage() {
        if (imagePath != null) {
            loadImage(imagePath);
        } else {
            System.err.println("Cannot reload image: No image path set.");
        }
    }

    public void setImage(ImageIcon tempIcon) {
        this.imageIcon = tempIcon; // Update the image
    }
    
    
}
