package game;

import java.util.List;

// Abstract base class for sea monsters and groups (composite pattern)
public abstract class SeaMonsterComponent {
    public abstract int[] getLocation();
    abstract void move();
    abstract void setLocation(int x, int y);

    // Returns a list of whirlpool components, if applicable
    public List<SeaMonsterComponent> getWhirlpools() {
        return null;
    }
}
