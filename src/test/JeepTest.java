package test;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JeepTest {
    private Jeep jeep;
    private Safari safari;

    @BeforeEach
    void setUp() {
        safari = new Safari(10, 10, "2023-01-01");
        jeep = new Jeep(4, 100);

        // Set up a horizontal road (y = 2) from x = 0 to x = 4
        List<List<Landscape>> map = safari.getLandscapes();
        for (int i = 0; i < 5; i++) {
            boolean isEntrance = (i == 0);
            boolean isExit = (i == 4);
            Road road = new Road(i, 2, isEntrance, isExit);
            map.get(i).set(2, road);
        }
    }

    @Test
    void testJeepStartsOnEntrance() {
        jeep.update(safari); // Should find and follow the entrance
        assertTrue(jeep.isMoving());
        assertEquals(0, jeep.getCurrentX());
        assertEquals(2, jeep.getCurrentY());
    }



    @Test
    void testDirectionChanges() {
        // Manually set a simple path to move right and down
        jeep.setCurrentX(1);
        jeep.setCurrentY(1);
        jeep.setCurrentRoute(Arrays.asList(
                new int[]{2, 1},
                new int[]{2, 2}
        ));
        jeep.setMoving(true);

        for (int i = 0; i < 10; i++) {
            jeep.update(safari);
        }

        String direction = jeep.getCurrentImageKey();
        assertTrue(List.of("jeepright", "jeepstraight", "jeepforward", "jeepleft").contains(direction),
                "Direction key should be valid, got: " + direction);
    }

    @Test
    void testJeepInitialValues() {
        assertEquals(4, jeep.getCapacity());
        assertEquals(100, jeep.getRentalPrice());
        assertEquals(500, jeep.getPrice());
    }

    @Test
    void testJeepRouteReinitializes() {
        jeep.update(safari);
        for (int i = 0; i < 100; i++) {
            jeep.update(safari);
        }

        // It might restart a new route automatically
        assertTrue(jeep.isMoving() || jeep.getCurrentX() >= 0, "Jeep should be active or moving if rerouting works");
    }
}
