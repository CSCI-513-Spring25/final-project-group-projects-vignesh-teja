package game;

// This interface defines a factory for creating PirateShip instances
public interface PirateShipFactory {

    // Method to create a pirate ship, given a reference to the Columbus ship and initial coordinates
    PirateShip createPirateShip(Ship ccs, int startX, int startY);
}
