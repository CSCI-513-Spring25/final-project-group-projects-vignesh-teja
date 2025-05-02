package game;

// Interface for defining different movement strategies for pirate ships
interface MovementStrategy {
    void move(PirateShip pirate, int shipX, int shipY);
}
