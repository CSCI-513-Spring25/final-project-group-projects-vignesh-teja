package game;

// Factory for creating pirate ships that use the ChaseStrategy
public class ChasePirateFactory implements PirateShipFactory {

    // Creates a pirate ship with the Chase strategy assigned
    @Override
    public PirateShip createPirateShip(Ship ccs, int startX, int startY) {
        PirateShip pirate = new PirateShip(ccs, startX, startY, "Chase");
        pirate.setStrategy(new ChaseStrategy());
        return pirate;
    }
}
