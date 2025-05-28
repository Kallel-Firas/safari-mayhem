package test;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HerdTest {
    private Herd<Sheep> herd;
    private List<List<Landscape>> landscapes;
    private Safari safari;

    @BeforeEach
    void setUp() {
        safari = new Safari(3, 1, "2023-01-01");
        landscapes = safari.getLandscapes();
        herd = new Herd<>(landscapes, safari);
        herd.generatePopulation("Sheep");
    }

    @Test
    void testGeneratePopulationCreatesAnimals() {
        assertFalse(herd.getAnimalList().isEmpty());
        assertEquals(9, herd.getAnimalList().size()); // 3x3 grid
    }

    @Test
    void testUpdateThirstAndHunger() {
        int initialThirst = herd.getThirstMeter();
        int initialHunger = herd.getHungerMeter();
        herd.update();
        assertTrue(herd.getThirstMeter() < initialThirst);
        assertTrue(herd.getHungerMeter() < initialHunger);
    }

    @Test
    void testCanDrinkWhenNearWater() {
        // Add water near the herd
        int x = herd.getAnimalList().get(0).getCurrentX() + 1;
        int y = herd.getAnimalList().get(0).getCurrentY();
        landscapes.get(x).set(y, new Water());

        assertTrue(herd.CanDrink());
    }


}