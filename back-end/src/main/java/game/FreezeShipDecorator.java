package game;
import java.util.List;
public class FreezeShipDecorator extends PirateShipDecorator {
    private int freezeUses;
    private int freezeDuration;
    private List<PirateShip> pirates;
    public FreezeShipDecorator(ShipComponent ship, List<PirateShip> pirates) {
        // Initializes the decorator with freeze ability for the ship.
        super(ship);
        this.freezeUses = 2;
        this.freezeDuration = 0;
        this.pirates = pirates;
        System.out.println("FreezeShipDecorator initialized with 2 uses.");
    }
    public void useFreeze() {
        // Freezes all pirates for a duration if freeze uses are available.
        if (freezeUses > 0) {
            freezeUses--;
            freezeDuration = 3;
            for (PirateShip pirate : pirates) {
                pirate.setFrozen(true, freezeDuration);
                int[] loc = pirate.getLocation();
                System.out.println("Pirate at (" + loc[0] + "," + loc[1] + ") frozen for 3 turns.");
            }
            System.out.println("Freeze activated! Remaining uses: " + freezeUses);
        } else {
            System.out.println("No freeze uses remaining!");
        }
    }
    public void updateFreeze() {
        // Reduces freeze duration and unfreezes pirates when done.
        if (freezeDuration > 0) {
            freezeDuration--;
            System.out.println("Freeze duration: " + freezeDuration + " turns remaining.");
            for (PirateShip pirate : pirates) {
                pirate.setFreezeDuration(freezeDuration);
                int[] loc = pirate.getLocation();
                if (freezeDuration == 0) {
                    pirate.setFrozen(false, 0);
                    System.out.println("Pirate at (" + loc[0] + "," + loc[1] + ") no longer frozen.");
                } else {
                    System.out.println("Pirate at (" + loc[0] + "," + loc[1] + ") frozen for " + freezeDuration + " more turns.");
                }
            }
        }
    }
    public int getFreezeUses() {
        // Returns remaining freeze uses.
        return freezeUses;
    }
    public boolean isFreezeAvailable() {
        // Checks if freeze ability can still be used.
        return freezeUses > 0;
    }
}