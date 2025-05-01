package game;

// Movement strategy where the pirate ship patrols in a square loop
public class PatrolStrategy implements MovementStrategy {
    private int direction = 0;  // 0-right, 1-down, 2-left, 3-up

    // Moves the pirate in a fixed patrol pattern, switching direction if blocked
    @Override
    public void move(PirateShip pirate, int shipX, int shipY) {
        int[] loc = pirate.getLocation();
        int x = loc[0], y = loc[1];
        OceanMap oceanMap = OceanMap.getInstance();
        int newX = x, newY = y;

        switch (direction) {
            case 0:
                if (x + 1 < oceanMap.getDimension() && !oceanMap.getOceanCell()[x + 1][y]) newX++;
                else direction = 1;
                break;
            case 1:
                if (y + 1 < oceanMap.getDimension() && !oceanMap.getOceanCell()[x][y + 1]) newY++;
                else direction = 2;
                break;
            case 2:
                if (x - 1 >= 0 && !oceanMap.getOceanCell()[x - 1][y]) newX--;
                else direction = 3;
                break;
            case 3:
                if (y - 1 >= 0 && !oceanMap.getOceanCell()[x][y - 1]) newY--;
                else direction = 0;
                break;
        }

        if (newX != x || newY != y) {
            pirate.setLocation(newX, newY);
        }
    }
}
