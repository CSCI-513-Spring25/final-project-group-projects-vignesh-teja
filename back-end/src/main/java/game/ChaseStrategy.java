package game;

// Movement strategy where the pirate moves directly toward the Columbus ship
public class ChaseStrategy implements MovementStrategy {

    // Moves the pirate one step closer to the Columbus ship if the target cell is free
    @Override
    public void move(PirateShip pirate, int shipX, int shipY) {
        int[] loc = pirate.getLocation();
        int x = loc[0], y = loc[1];
        OceanMap oceanMap = OceanMap.getInstance();
        int newX = x, newY = y;

        if (x < shipX) newX++;
        else if (x > shipX) newX--;
        if (y < shipY) newY++;
        else if (y > shipY) newY--;

        if (newX >= 0 && newX < oceanMap.getDimension() && newY >= 0 && newY < oceanMap.getDimension()
            && !oceanMap.getOceanCell()[newX][newY]) {
            pirate.setLocation(newX, newY);
        }
    }
}
