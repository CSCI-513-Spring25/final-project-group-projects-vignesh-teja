package game;
public interface ShipComponent {
    int[] getCurrentLocation();
    // Retrieves the current position of the ship.
    void setLocation(int newX, int newY);
    // Updates the ship's position to new coordinates.
}