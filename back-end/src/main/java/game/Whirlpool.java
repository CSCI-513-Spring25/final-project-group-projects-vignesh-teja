package game;

public class Whirlpool extends SeaMonsterComponent implements WhirlpoolComponent {
    private int x, y;
    private SeaMonsterGroup parentGroup;
    private Whirlpool exitWhirlpool;

    public Whirlpool(int x, int y, SeaMonsterGroup parentGroup) {
        // Sets up a whirlpool with coordinates and links it to a monster group.
        this.x = x;
        this.y = y;
        this.parentGroup = parentGroup;
    }

    public void setExitWhirlpool(Whirlpool exitWhirlpool) {
        // Links this whirlpool to another for teleportation.
        this.exitWhirlpool = exitWhirlpool;
    }

    @Override
    public int[] getLocation() {
        // Returns the whirlpool's current coordinates.
        return new int[]{x, y};
    }

    @Override
    public void move() {
        // Does nothing since whirlpools are stationary.
    }

    @Override
    public void setLocation(int x, int y) {
        // Updates the whirlpool's position.
        this.x = x;
        this.y = y;
    }

    @Override
    public void applyEffect(ShipComponent ship) {
        // Teleports the ship to the linked whirlpool if the ship is on this spot.
        int[] shipLoc = ship.getCurrentLocation();
        int[] whirlpoolLoc = getLocation();
        if (shipLoc[0] == whirlpoolLoc[0] && shipLoc[1] == whirlpoolLoc[1]) {
            if (exitWhirlpool != null) {
                int[] exitLoc = exitWhirlpool.getLocation();
                ship.setLocation(exitLoc[0], exitLoc[1]);
            }
        }
    }

}