package test;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RangerTest {
    private Ranger ranger;
    private Safari safari;

    @BeforeEach
    void setUp() {
        ranger = new Ranger(100);
        ranger.setCurrentX(5);
        ranger.setCurrentY(5);
        safari = new Safari(1, 1, "2023-01-01");
    }

    @Test
    void testDetectPoacher() {
        Poacher poacher = new Poacher(7, 7); // Within detection radius
        safari.addPoacher(poacher);
        ranger.update(safari);
        assertTrue(ranger.hasTarget());
    }

    @Test
    void testPatrolMovement() {
        int initialX = ranger.getCurrentX();
        int initialY = ranger.getCurrentY();
        ranger.update(safari);
        assertTrue(ranger.getCurrentX() != initialX || ranger.getCurrentY() != initialY);
    }

    @Test
    void testChasePoacher() {
        Poacher poacher = new Poacher(6, 6);
        safari.addPoacher(poacher);
        ranger.update(safari);
        // Should move toward poacher
        assertTrue(Math.abs(ranger.getCurrentX() - 6) <= 1 &&
                Math.abs(ranger.getCurrentY() - 6) <= 1);
    }
    @Test
    void testEliminatePoacher() {
        Poacher poacher = new Poacher(5, 6);
        safari.addPoacher(poacher);
        ranger.update(safari);
        // Should eliminate poacher
        assertTrue(ranger.hasRepelledPoacher());
    }
}