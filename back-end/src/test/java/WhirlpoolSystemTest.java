import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import game.OceanMap;
import game.Ship;
import game.Whirlpool;
import game.SeaMonsterGroup;
import game.FreezeShipDecorator;
import game.PirateShip;
import game.SeaMonster;
import game.ChasePirateFactory;
import game.PatrolPirateFactory;
import game.SeaMonsterComponent;
import game.PirateShipFactory;


public class WhirlpoolSystemTest {
    private OceanMap oceanMap;
    private Ship ship;
    private Whirlpool whirlpoolA;
    private Whirlpool whirlpoolB;

    @BeforeEach
    public void setUp() {
        oceanMap = OceanMap.getInstance();
        oceanMap.reset();

        ship = new Ship();
        ship.setLocation(2, 2);

        whirlpoolA = new Whirlpool(2, 2, null);
        whirlpoolB = new Whirlpool(7, 7, null);

        whirlpoolA.setExitWhirlpool(whirlpoolB);
        whirlpoolB.setExitWhirlpool(whirlpoolA);
    }

    @Test
    public void testShipTeleportsFromAtoB() {
        ship.setLocation(2, 2);
        whirlpoolA.applyEffect(ship);
        int[] loc = ship.getCurrentLocation();
        assertEquals(7, loc[0]);
        assertEquals(7, loc[1]);
    }

    @Test
    public void testShipTeleportsFromBtoA() {
        ship.setLocation(7, 7);
        whirlpoolB.applyEffect(ship);
        int[] loc = ship.getCurrentLocation();
        assertEquals(2, loc[0]);
        assertEquals(2, loc[1]);
    }

    @Test
    public void testShipDoesNotTeleportFromOtherLocation() {
        ship.setLocation(4, 4);
        whirlpoolA.applyEffect(ship);
        int[] loc = ship.getCurrentLocation();
        assertEquals(4, loc[0]);
        assertEquals(4, loc[1]);
    }

    @Test
    public void testShipLocationPersistsWhenNoTeleport() {
        ship.setLocation(3, 3);
        whirlpoolB.applyEffect(ship); // Should do nothing
        int[] loc = ship.getCurrentLocation();
        assertEquals(3, loc[0]);
        assertEquals(3, loc[1]);
    }

    @Test
    public void testTeleportationBackAndForth() {
        ship.setLocation(2, 2);
        whirlpoolA.applyEffect(ship);
        assertArrayEquals(new int[]{7, 7}, ship.getCurrentLocation());

        whirlpoolB.applyEffect(ship);
        assertArrayEquals(new int[]{2, 2}, ship.getCurrentLocation());
    }

    @Test
    public void testTeleportDoesNotCauseExceptionWhenExitNull() {
        // Create new whirlpool without exit
        Whirlpool noExitWhirlpool = new Whirlpool(6, 6, null);
        ship.setLocation(6, 6);
        noExitWhirlpool.applyEffect(ship); // Should not throw
        int[] loc = ship.getCurrentLocation();
        assertEquals(6, loc[0]);
        assertEquals(6, loc[1]); // Ship should not move
    }
}