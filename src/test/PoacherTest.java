package test;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoacherTest {
    private Poacher poacher;
    private Safari safari;

    @BeforeEach
    void setUp() {
        poacher = new Poacher(5, 5);
        safari = new Safari(1, 1, "2023-01-01");
    }

    @Test
    void testDetectAnimal() {
        // Get first animal from safari
        Animal animal = safari.getAnimalList().get(0);
        poacher.update(safari);
        // Should move toward animal
        assertTrue(poacher.getCurrentX() != 5 || poacher.getCurrentY() != 5);
    }

    @Test
    void testEscapeWhenRangerNearby() {
        Ranger ranger = new Ranger(100);
        ranger.setCurrentX(6);
        ranger.setCurrentY(6);
        safari.addRanger(ranger);
        poacher.update(safari);
        assertTrue(poacher.isEscaping());
    }

    @Test
    void testLeaveAfterTimeLimit() {
        for (int i = 0; i < 7; i++) {
            poacher.update(safari);
        }
        assertTrue(poacher.isEscaping());
    }
}
