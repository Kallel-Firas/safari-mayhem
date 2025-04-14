package test;

import Model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoadTest {
    private Road road;
    private final int testX = 10;
    private final int testY = 20;

    @BeforeEach
    public void setUp() {
        // Create a road for testing
        road = new Road(testX, testY, false, false);
    }

    @Test
    public void testRoadInitialization() {
        assertEquals(testX, road.getX());
        assertEquals(testY, road.getY());
        assertFalse(road.isEntrance());
        assertFalse(road.isExit());
        assertEquals(250, road.getPrice());
        assertEquals("road1", road.getImageKey()); // Default image key
    }

    @Test
    public void testSetImageKey() {
        String newImageKey = "road2";
        road.setImageKey(newImageKey);
        assertEquals(newImageKey, road.getImageKey());
    }

    @Test
    public void testSetEntrance() {
        assertFalse(road.isEntrance());
        road.setEntrance(true);
        assertTrue(road.isEntrance());
    }

    @Test
    public void testSetExit() {
        assertFalse(road.isExit());
        road.setExit(true);
        assertTrue(road.isExit());
    }

    @Test
    public void testEntranceExitRoad() {
        // Create a road that is both entrance and exit
        Road entranceExitRoad = new Road(5, 5, true, true);

        assertTrue(entranceExitRoad.isEntrance());
        assertTrue(entranceExitRoad.isExit());
        assertEquals(250, entranceExitRoad.getPrice()); // Price should remain the same
    }
}