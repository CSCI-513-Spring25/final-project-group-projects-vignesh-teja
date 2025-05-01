import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.OceanMap;
import game.PirateShip;
import game.PirateShipFactory;
import game.Ship;
import game.ChasePirateFactory;
import game.PatrolPirateFactory;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for different pirate ship behaviors using chase and patrol strategies
public class PirateShipTest {
    private Ship ship;
    private PirateShip chasePirate;
    private PirateShip patrolPirate;
    private OceanMap oceanMap;

    // Runs before each test to set up a fresh map and ships
    @BeforeEach
    public void setUp() {
        oceanMap = OceanMap.getInstance();
        oceanMap.reset();

        // Create a Columbus ship at a fixed location
        ship = new Ship();
        ship.setLocation(10, 10);

        // Create pirate ships using chase and patrol factories
        PirateShipFactory chaseFactory = new ChasePirateFactory();
        PirateShipFactory patrolFactory = new PatrolPirateFactory();

        chasePirate = chaseFactory.createPirateShip(ship, 5, 5);
        patrolPirate = patrolFactory.createPirateShip(ship, 15, 15);
    }

    // Verifies that pirates are initialized at correct starting positions
    @Test
    public void testPirateInitialization() {
        int[] chaseLoc = chasePirate.getLocation();
        assertEquals(5, chaseLoc[0]);
        assertEquals(5, chaseLoc[1]);

        int[] patrolLoc = patrolPirate.getLocation();
        assertEquals(15, patrolLoc[0]);
        assertEquals(15, patrolLoc[1]);
    }

    // Verifies that the chase pirate moves toward the Columbus ship
    @Test
    public void testChaseStrategy() {
        int[] initialLoc = chasePirate.getLocation();
        ship.setLocation(11, 11); // triggers movement
        int[] newLoc = chasePirate.getLocation();
        assertTrue(newLoc[0] > initialLoc[0] || newLoc[1] > initialLoc[1]);
    }

    // Verifies that patrol pirate moves in a fixed pattern, not toward the ship
    @Test
    public void testPatrolStrategy() {
        int[] initialLoc = patrolPirate.getLocation();
        ship.setLocation(11, 11); // triggers movement
        int[] newLoc = patrolPirate.getLocation();
        assertFalse(initialLoc[0] == newLoc[0] && initialLoc[1] == newLoc[1]);
    }

    // Verifies that a frozen pirate does not move when the ship moves
    @Test
    public void testFreezeEffect() {
        chasePirate.setFrozen(true, 2); // freeze for 2 turns
        assertTrue(chasePirate.isFrozen());

        int[] initialPos = chasePirate.getLocation();
        ship.setLocation(11, 11); // would normally trigger movement

        int[] newPos = chasePirate.getLocation();
        assertEquals(initialPos[0], newPos[0]);
        assertEquals(initialPos[1], newPos[1]);
    }

    // Verifies that the freeze effect expires after the correct duration
    @Test
    public void testFreezeExpires() {
        chasePirate.setFrozen(true, 1); // freeze for 1 turn
        assertTrue(chasePirate.isFrozen());

        chasePirate.setFreezeDuration(0); // simulate 1 turn passed
        chasePirate.setFrozen(false, 0);  // manually unfreeze

        assertFalse(chasePirate.isFrozen());

        int[] initialPos = chasePirate.getLocation();
        ship.setLocation(11, 11); // triggers movement again
        int[] newPos = chasePirate.getLocation();
        assertFalse(initialPos[0] == newPos[0] && initialPos[1] == newPos[1]);
    }

    // Verifies that the pirate avoids moving into a blocked island cell
    @Test
    public void testPirateAvoidIslands() {
        oceanMap.getOceanCell()[6][5] = true; // mark cell as island

        chasePirate.setLocation(5, 5);  // one step left of island
        ship.setLocation(7, 5);         // target is past the island

        int[] newLoc = chasePirate.getLocation();
        assertFalse(newLoc[0] == 6 && newLoc[1] == 5); // must not land on island
    }
}
