package game;

public interface WhirlpoolComponent {
    int[] getLocation();
    // Gets the whirlpool's position on the map.

    void applyEffect(ShipComponent ship);
    // Applies the teleportation effect to the ship.
}