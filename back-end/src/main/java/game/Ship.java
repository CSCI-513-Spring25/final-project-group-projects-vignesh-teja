package game;

import java.util.Observable;

public class Ship extends Observable implements ShipComponent {
    // Initial coordinates for the ship, starting at the center of the 20x20 map (10,10).
    private int x = 10, y = 10;

    // Returns the current location of the ship
    // This is used to track the ship's position.
    @Override
    public int[] getCurrentLocation() {
        return new int[]{x, y};
    }

    // Updates the ship's location to new coordinates, with boundary and checking the collision
    // The method ensures the ship stays within the map and doesn't move onto islands.
    // It is also notifies observers only if the position actually changes and also avoiding unnecessary updates.
    @Override
    public void setLocation(int newX, int newY) {
        OceanMap oceanMap = OceanMap.getInstance();
        if (newX >= 0 && newX < oceanMap.getDimension() && newY >= 0 && newY < oceanMap.getDimension() && !oceanMap.getOceanCell()[newX][newY]) {
            if (x != newX || y != newY) {
                this.x = newX;
                this.y = newY;
                setChanged();
                notifyObservers(new int[]{x, y});
            }
        }
    }
}