package game;

public abstract class PirateShipDecorator implements ShipComponent {
    protected ShipComponent decoratedShip;

    public PirateShipDecorator(ShipComponent ship) {
        // Initializes the decorator with the ship to extend.
        this.decoratedShip = ship;
    }

    @Override
    public int[] getCurrentLocation() {
        // Gets the location from the decorated ship.
        return decoratedShip.getCurrentLocation();
    }

    @Override
    public void setLocation(int newX, int newY) {
        // Updates the location through the decorated ship.
        decoratedShip.setLocation(newX, newY);
    }

}