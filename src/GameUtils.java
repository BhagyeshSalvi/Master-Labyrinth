import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameUtils {
   

    public static Map<String, Integer> getTileFrequencies() {
        Map<String, Integer> tileFrequencies = new LinkedHashMap<>();

        // Specify the maximum count for each tile
        tileFrequencies.put("Pictures/GridCell/brick_cross.png", 2);    // Cross
        tileFrequencies.put("Pictures/GridCell/hallway_vert.png", 8);  // Vertical
        tileFrequencies.put("Pictures/GridCell/hallway_horiz.png", 7); // Horizontal
        tileFrequencies.put("Pictures/GridCell/brick_NE.png", 4);      // Northeast corner
        tileFrequencies.put("Pictures/GridCell/brick_NW.png", 4);      // Northwest corner
        tileFrequencies.put("Pictures/GridCell/brick_SE.png", 4);      // Southeast corner
        tileFrequencies.put("Pictures/GridCell/brick_SW.png", 4);      // Southwest corner
        tileFrequencies.put("Pictures/GridCell/brick_Teast.png", 4);   // T-east
        tileFrequencies.put("Pictures/GridCell/brick_Twest.png", 4);   // T-west
        tileFrequencies.put("Pictures/GridCell/brick_Tnorth.png", 4);  // T-north
        tileFrequencies.put("Pictures/GridCell/brick_Tsouth.png", 4);  // T-south

        return tileFrequencies;
    }

    public static Map<String, String> getTileTypeMapping() {
    Map<String, String> tileTypeMapping = new HashMap<>();

    // Map image paths to tile types
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



    public static Map<String, Boolean> generateTokenPaths() {
    Map<String, Boolean> tokenPaths = new LinkedHashMap<>(); // Maintain insertion order
    
    // Add regular tokens (false means not a magic card)
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

    tokenPaths.put(normalizePath("Pictures/MG1.png"), true);
    tokenPaths.put(normalizePath("Pictures/MG2.png"), true);
    tokenPaths.put(normalizePath("Pictures/MG3.png"), true);

    
    return tokenPaths;
}

private static String normalizePath(String path) {
    return path.trim().replace("\\", "/").toLowerCase(); // Trim spaces, unify separators, and lowercase
}

}
