
/**
 * Name - Bhagyesh Salvi
 * Number - 041103856
 * Java Application Programming UI Assignment
 * 
 * This class provides utility functions for the game, including tile configuration, 
 * token generation, path normalization, and helper methods for connections and mappings.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameUtils {

    /**
     * Provides a mapping of tile image paths to their maximum frequency allowed on
     * the board.
     * 
     * @return a Map where keys are image paths and values are the maximum counts
     *         for each tile type.
     */
    public static Map<String, Integer> getTileFrequencies() {
        Map<String, Integer> tileFrequencies = new LinkedHashMap<>();

        tileFrequencies.put("Pictures/GridCell/brick_cross.png", 2);
        tileFrequencies.put("Pictures/GridCell/hallway_vert.png", 8);
        tileFrequencies.put("Pictures/GridCell/hallway_horiz.png", 7);
        tileFrequencies.put("Pictures/GridCell/brick_NE.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_NW.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_SE.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_SW.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_Teast.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_Twest.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_Tnorth.png", 4);
        tileFrequencies.put("Pictures/GridCell/brick_Tsouth.png", 4);

        return tileFrequencies;
    }

    /**
     * Provides a mapping of tile image paths to their corresponding tile types.
     * 
     * @return a Map where keys are image paths and values are tile type strings.
     */
    public static Map<String, String> getTileTypeMapping() {
        Map<String, String> tileTypeMapping = new HashMap<>();

        tileTypeMapping.put("Pictures/GridCell/brick_cross.png", "cross");
        tileTypeMapping.put("Pictures/GridCell/hallway_vert.png", "vertical");
        tileTypeMapping.put("Pictures/GridCell/hallway_horiz.png", "horizontal");
        tileTypeMapping.put("Pictures/GridCell/brick_NE.png", "corner_NE");
        tileTypeMapping.put("Pictures/GridCell/brick_NW.png", "corner_NW");
        tileTypeMapping.put("Pictures/GridCell/brick_SE.png", "corner_SE");
        tileTypeMapping.put("Pictures/GridCell/brick_SW.png", "corner_SW");
        tileTypeMapping.put("Pictures/GridCell/brick_Teast.png", "T_east");
        tileTypeMapping.put("Pictures/GridCell/brick_Twest.png", "T_west");
        tileTypeMapping.put("Pictures/GridCell/brick_Tnorth.png", "T_north");
        tileTypeMapping.put("Pictures/GridCell/brick_Tsouth.png", "T_south");

        return tileTypeMapping;
    }

    /**
     * Provides a mapping of tile types to their connection directions.
     * 
     * @return a Map where keys are tile types and values are lists of connection
     *         directions.
     */
    public static Map<String, List<String>> getTileConnections() {
        Map<String, List<String>> tileConnections = new HashMap<>();

        tileConnections.put("cross", Arrays.asList("north", "south", "east", "west"));
        tileConnections.put("horizontal", Arrays.asList("west", "east"));
        tileConnections.put("vertical", Arrays.asList("north", "south"));
        tileConnections.put("corner_NE", Arrays.asList("north", "east"));
        tileConnections.put("corner_NW", Arrays.asList("north", "west"));
        tileConnections.put("corner_SE", Arrays.asList("south", "east"));
        tileConnections.put("corner_SW", Arrays.asList("south", "west"));
        tileConnections.put("T_north", Arrays.asList("north", "east", "west"));
        tileConnections.put("T_south", Arrays.asList("south", "east", "west"));
        tileConnections.put("T_east", Arrays.asList("east", "north", "south"));
        tileConnections.put("T_west", Arrays.asList("west", "north", "south"));
        tileConnections.put("default", Arrays.asList("west", "east"));

        return tileConnections;
    }

    /**
     * Generates a mapping of token image paths to their type.
     * 
     * @return a Map where keys are token image paths and values are booleans
     *         indicating
     *         whether the token is a magic card (true) or a regular token (false).
     */
    public static Map<String, Boolean> generateTokenPaths() {
        Map<String, Boolean> tokenPaths = new LinkedHashMap<>(); // Maintain insertion order

        // Add regular tokens
        tokenPaths.put("Pictures/Token/gold_1.png", false);
        tokenPaths.put("Pictures/Token/gold_3.png", false);
        tokenPaths.put("Pictures/Token/gold_4.png", false);
        tokenPaths.put("Pictures/Token/gold_5.png", false);
        tokenPaths.put("Pictures/Token/gold_6.png", false);
        tokenPaths.put("Pictures/Token/gold_7.png", false);
        tokenPaths.put("Pictures/Token/gold_8.png", false);
        tokenPaths.put("Pictures/Token/gold_9.png", false);
        tokenPaths.put("Pictures/Token/gold_11.png", false);
        tokenPaths.put("Pictures/Token/gold_12.png", false);
        tokenPaths.put("Pictures/Token/gold_13.png", false);
        tokenPaths.put("Pictures/Token/gold_15.png", false);
        tokenPaths.put("Pictures/Token/gold_16.png", false);

        // Add magic cards
        tokenPaths.put(normalizePath("Pictures/MG1.png"), true);
        tokenPaths.put(normalizePath("Pictures/MG2.png"), true);
        tokenPaths.put(normalizePath("Pictures/MG3.png"), true);

        return tokenPaths;
    }

    /**
     * Normalizes a file path by trimming spaces, unifying separators, and
     * converting to lowercase.
     * 
     * @param path the file path to normalize
     * @return the normalized file path
     */
    private static String normalizePath(String path) {
        return path.trim().replace("\\", "/").toLowerCase();
    }
}
