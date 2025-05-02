package game;

import java.util.Random;

// Represents a single sea monster with random movement in a defined area
public class SeaMonster extends SeaMonsterComponent {
    private int x, y;
    private int minX, maxX, minY, maxY;

    public SeaMonster(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        this.x = startX;
        this.y = startY;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    // Returns the current location of the sea monster
    @Override
    public int[] getLocation() {
        return new int[]{x, y};
    }

    // Moves the sea monster randomly within its allowed area and if the cell is unblocked
    @Override
    void move() {
        Random rand = new Random();
        OceanMap oceanMap = OceanMap.getInstance();
        int newX = x + rand.nextInt(3) - 1;
        int newY = y + rand.nextInt(3) - 1;

        if (newX >= 0 && newX < oceanMap.getDimension() && newX >= minX && newX <= maxX &&
            newY >= 0 && newY < oceanMap.getDimension() && newY >= minY && newY <= maxY &&
            !oceanMap.getOceanCell()[newX][newY]) {
            x = newX;
            y = newY;
        }
    }

    // Sets the sea monster’s location
    @Override
    void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
