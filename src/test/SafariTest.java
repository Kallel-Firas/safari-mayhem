package test;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SafariTest {
    private Safari safari;

    @BeforeEach
    void setUp() {
        safari = new Safari(1, 1, "2023-01-01");
    }

    @Test
    void testInitializationCreatesAnimals() {
        assertFalse(safari.getAnimalList().isEmpty());
    }

    @Test
    void testInitializationCreatesVegetation() {
        assertFalse(safari.getVegetationList().isEmpty());
    }

    @Test
    void testInitializationCreatesWater() {
        boolean hasWater = false;
        for (List<Landscape> column : safari.getLandscapes()) {
            for (Landscape landscape : column) {
                if (landscape instanceof Water) {
                    hasWater = true;
                    break;
                }
            }
        }
        assertTrue(hasWater);
    }

    @Test
    void testAddAndRemovePoacher() {
        Poacher poacher = new Poacher(5, 5);
        safari.addPoacher(poacher);
        assertTrue(safari.getPoachers().contains(poacher));

        safari.removePoacher(poacher);
        assertFalse(safari.getPoachers().contains(poacher));
    }

    @Test
    void testUpdateAllEntities() {
        safari.Update();
        // Animals should have aged
        assertTrue(safari.getAnimalList().get(0).getAge() > 0);
    }
}
