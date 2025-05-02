package game;
import java.util.Random;
public class OceanMap {
    private static OceanMap instance;
    private boolean[][] oceanCell;
    private int dimension = 20;
    private int islandCount = 15;
    private int[] treasureLocation;
    private OceanMap() {
        // Initializes the map by resetting it.
        reset();
    }
    public static OceanMap getInstance() {
        // Provides the single instance of the map.
        if (instance == null) {
            instance = new OceanMap();
        }
        return instance;
    }
    public void reset() {
        // Clears the map and sets up islands and treasure.
        oceanCell = new boolean[dimension][dimension];
        generateIslands();
        placeTreasure();
    }
    private void generateIslands() {
        // Randomly places islands on the map.
        Random rand = new Random();
        int count = 0;
        while (count < islandCount) {
            int x = rand.nextInt(dimension);
            int y = rand.nextInt(dimension);
            if (!oceanCell[x][y] && !(x == 10 && y == 10)) {
                oceanCell[x][y] = true;
                count++;
            }
        }
    }
    private void placeTreasure() {
        // Places the treasure in a random, valid spot.
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(dimension);
            y = rand.nextInt(dimension);
        } while (oceanCell[x][y] || (x == 10 && y == 10));
        treasureLocation = new int[]{x, y};
    }
    public boolean[][] getOceanCell() {
        // Returns the grid with island positions.
        return oceanCell;
    }
    public int[] getTreasureLocation() {
        // Returns the treasure's coordinates.
        return treasureLocation;
    }
    public int getDimension() {
        // Returns the size of the map.
        return dimension;
    }
}