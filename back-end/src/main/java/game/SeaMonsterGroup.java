package game;

import java.util.ArrayList;
import java.util.List;

// Composite class to manage a group of sea monsters or whirlpools
public class SeaMonsterGroup extends SeaMonsterComponent {
    private List<SeaMonsterComponent> components = new ArrayList<>();
    private int minX, maxX, minY, maxY;
    private boolean movementEnabled = true;

    public SeaMonsterGroup(int minX, int maxX, int minY, int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    // Adds a sea monster or other component to the group
    public void add(SeaMonsterComponent component) {
        components.add(component);
    }

    // Enables or disables movement for all components
    public void enableMovement(boolean enable) {
        this.movementEnabled = enable;
    }

    // Returns the location of the first component in the group
    @Override
    public int[] getLocation() {
        return components.isEmpty() ? new int[]{0, 0} : components.get(0).getLocation();
    }

    // Moves all components in the group if movement is enabled
    @Override
    public void move() {
        if (movementEnabled) {
            for (SeaMonsterComponent component : components) {
                component.move();
            }
        }
    }

    // Not used for groups
    @Override
    void setLocation(int x, int y) {}

    // Returns only the sea monsters from the group
    public List<SeaMonsterComponent> getMonsters() {
        List<SeaMonsterComponent> monsters = new ArrayList<>();
        for (SeaMonsterComponent component : components) {
            if (component instanceof SeaMonster) {
                monsters.add(component);
            }
        }
        return monsters;
    }

    // Returns only the whirlpool components from the group
    @Override
    public List<SeaMonsterComponent> getWhirlpools() {
        List<SeaMonsterComponent> whirlpools = new ArrayList<>();
        for (SeaMonsterComponent component : components) {
            if (component instanceof WhirlpoolComponent) {
                whirlpools.add(component);
            }
        }
        return whirlpools;
    }

    // Checks whether a point lies within this group's boundaries
    public boolean contains(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
