package test;
import Model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    private Animal animal;
    private List<List<Landscape>> map;
    private List<Entity> entities;

    @BeforeEach
    void setUp() {
        // Using a concrete subclass (Sheep) to test Animal functionality
        animal = new Sheep(1, "TestSheep", false, 5, 5);
        map = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Landscape> row = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                row.add(new Dirt());
            }
            map.add(row);
        }
        entities = new ArrayList<>();
    }

    @Test
    void testMoveToTarget() {
        int previousX = animal.getCurrentX();
        int previousY = animal.getCurrentY();
        animal.Move(7, 7, map, entities);
        int xDiff = Math.abs(animal.getCurrentX() - previousX);
        int yDiff = Math.abs(animal.getCurrentY() - previousY);
        assertEquals(1, xDiff + yDiff);
    }

    @Test
    void testMoveBlockedByWater() {
        // Add water at (6,5)
        map.get(6).set(5, new Water());
        animal.Move(7, 5, map, entities);
        // Should find alternative path
        assertTrue(animal.getCurrentX() != 5 || animal.getCurrentY() != 5);
    }

    @Test
    void testMoveBlockedByOtherAnimal() {
        // Add another animal at (6,5)
        entities.add(new Sheep(2, "BlockingSheep", false, 6, 5));
        animal.Move(7, 5, map, entities);
        // Should find alternative path
        assertTrue(animal.getCurrentX() != 5 || animal.getCurrentY() != 5);
    }

    @Test
    void testUpdateIncreasesAge() {
        int initialAge = animal.getAge();
        animal.Update();
        assertEquals(initialAge + 1, animal.getAge());
    }

    @Test
    void testDrinkResetsThirst() {
        animal.setThirstMeter(50);
        animal.Drink();
        assertEquals(0, animal.getThirstMeter());
    }

    @Test
    void testEatResetsHunger() {
        animal.setHungerMeter(50);
        animal.Eat();
        assertEquals(0, animal.getHungerMeter());
    }
}