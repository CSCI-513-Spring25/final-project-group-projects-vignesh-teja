package game;
import java.util.Observable;
import java.util.Observer;

public class PirateShip implements Observer {
    private int x, y;
    private MovementStrategy strategy;
    private boolean isFrozen;
    private int freezeDuration;
    private String type;

    public PirateShip(Ship ccs, int startX, int startY, String type) {
        // Sets up the pirate ship with a starting position and type, observing the player's ship.
        this.x = startX;
        this.y = startY;
        this.isFrozen = false;
        this.freezeDuration = 0;
        this.type = type;
        ccs.addObserver(this);
    }

    public String getType() {
        // Returns the pirate ship's type (e.g., Chase or Patrol).
        return type;
    }

    public void setStrategy(MovementStrategy strategy) {
        // Assigns a new movement strategy to the pirate ship.
        this.strategy = strategy;
    }

    public int[] getLocation() {
        // Provides the current position of the pirate ship.
        return new int[]{x, y};
    }

    public void setLocation(int x, int y) {
        // Updates the pirate ship's position and logs the move.
        this.x = x;
        this.y = y;
        System.out.println("PirateShip moved to (" + x + "," + y + ")");
    }

    public void setFrozen(boolean frozen, int duration) {
        // Sets the freeze state and duration for the pirate ship.
        this.isFrozen = frozen;
        this.freezeDuration = duration;
        System.out.println("Pirate at (" + x + "," + y + ") freeze status set to " + frozen + " for " + duration + " turns.");
    }

    public void setFreezeDuration(int duration) {
        // Updates the remaining freeze duration.
        this.freezeDuration = duration;
    }

    public boolean isFrozen() {
        // Checks if the pirate ship is currently frozen.
        return isFrozen;
    }

    @Override
    public void update(Observable o, Object arg) {
        // Moves the pirate ship based on the player's position unless frozen.
        if (isFrozen) {
            System.out.println("Pirate at (" + x + "," + y + ") is frozen, skipping movement.");
            return;
        }
        if (arg instanceof int[]) {
            int[] newLocation = (int[]) arg;
            strategy.move(this, newLocation[0], newLocation[1]);
        }
    }
}