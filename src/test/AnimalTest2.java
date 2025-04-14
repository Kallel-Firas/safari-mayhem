package test;

import Model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class AnimalTest2 {
    private Lion lion;
    private Elephant elephant;
    private Sheep sheep;
    private Cheetah cheetah;
    private List<List<Landscape>> landscapes;
    private List<Entity> entities;

    @BeforeEach
    public void setUp() {
        // Create test animals
        lion = new Lion(1, "TestLion", false, 5, 5);
        elephant = new Elephant(2, "TestElephant", false, 10, 10);
        sheep = new Sheep(3, "TestSheep", false, 15, 15);
        cheetah = new Cheetah(4, "TestCheetah", false, 20, 20);

        // Create landscape grid
        landscapes = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            List<Landscape> row = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                row.add(new Dirt());
            }
            landscapes.add(row);
        }

        // Add some water
        landscapes.get(7).set(7, new Water());
        landscapes.get(12).set(12, new Water());

        // Create entities list
        entities = new ArrayList<>();
        entities.add(lion);
        entities.add(elephant);
        entities.add(sheep);
        entities.add(cheetah);
    }

    @Test
    public void testAnimalInitialization() {
        assertEquals(1, lion.getAge());
        assertEquals(5, lion.getCurrentX());
        assertEquals(5, lion.getCurrentY());
        assertTrue(lion.isCanReproduce());
    }

    @Test
    public void testMove() {
        // Test basic movement
        int initialX = lion.getCurrentX();
        int initialY = lion.getCurrentY();

        // Move toward a specific location
        lion.Move(initialX + 5, initialY, landscapes, entities);

        // Lion should have moved in x direction
        assertTrue(lion.getCurrentX() > initialX || lion.getCurrentX() < initialX);

        // Reset and test y direction
        lion.setCurrentX(initialX);
        lion.setCurrentY(initialY);

        lion.Move(initialX, initialY + 5, landscapes, entities);
        assertTrue(lion.getCurrentY() > initialY || lion.getCurrentY() < initialY);
    }

    @Test
    public void testMoveToOccupiedSpace() {
        // Place an animal at a specific location
        int targetX = 8, targetY = 8;
        Sheep blockingSheep = new Sheep(99, "BlockingSheep", false, targetX, targetY);
        entities.add(blockingSheep);

        // Place lion adjacent to sheep
        lion.setCurrentX(targetX - 1);
        lion.setCurrentY(targetY);

        // Try to move to sheep's location
        lion.Move(targetX + 1, targetY, landscapes, entities);

        // Lion should not be at sheep's exact location
        assertFalse(lion.getCurrentX() == targetX && lion.getCurrentY() == targetY);
    }

    @Test
    public void testAvoidWater() {
        // Place lion next to water
        lion.setCurrentX(6);
        lion.setCurrentY(7);

        // Try to move into water
        lion.Move(8, 7, landscapes, entities);

        // Lion should not be on water
        assertFalse(landscapes.get(lion.getCurrentX()).get(lion.getCurrentY()) instanceof Water);
    }

    @Test
    public void testUpdate() {
        int initialAge = lion.getAge();

        // Update lion
        lion.Update();

        // Age should increase
        assertEquals(initialAge + 1, lion.getAge());
    }

    @Test
    public void testEatAndDrink() {
        // Test eating
        lion.Eat();
        assertEquals(0, lion.getHungerMeter());

        // Test drinking
        lion.Drink();
        assertEquals(0, lion.getThirstMeter());
    }

    @Test
    public void testLionReproduce() {
        // Test with same species but too young
        Lion youngLion = new Lion(5, "YoungLion", false, 6, 6);
        youngLion.setAge(20); // Age is less than required 24*25
        assertFalse(lion.Reproduce(youngLion));

        // Test with same species and old enough
        Lion matureLion = new Lion(6, "MatureLion", false, 7, 7);
        matureLion.setAge(24 * 26); // Age is more than required
        lion.setAge(24 * 26);
        assertTrue(lion.Reproduce(matureLion));

        // Test with different species
        assertFalse(lion.Reproduce(elephant));

        // Test with canReproduce set to false
        lion.setCanReproduce(false);
        assertFalse(lion.Reproduce(matureLion));
    }

    @Test
    public void testElephantReproduce() {
        // Test with same species but too young
        Elephant youngElephant = new Elephant(7, "YoungElephant", false, 8, 8);
        youngElephant.setAge(20); // Age is less than required 24*20
        assertFalse(elephant.Reproduce(youngElephant));

        // Test with same species and old enough
        Elephant matureElephant = new Elephant(8, "MatureElephant", false, 9, 9);
        matureElephant.setAge(24 * 21); // Age is more than required
        elephant.setAge(24 * 21);
        assertTrue(elephant.Reproduce(matureElephant));

        // Test with different species
        assertFalse(elephant.Reproduce(lion));

        // Test with canReproduce set to false
        elephant.setCanReproduce(false);
        assertFalse(elephant.Reproduce(matureElephant));
    }

    @Test
    public void testSheepReproduce() {
        // Test with same species but too young
        Sheep youngSheep = new Sheep(9, "YoungSheep", false, 16, 16);
        youngSheep.setAge(20); // Age is less than required 24*15
        assertFalse(sheep.Reproduce(youngSheep));

        // Test with same species and old enough
        Sheep matureSheep = new Sheep(10, "MatureSheep", false, 17, 17);
        matureSheep.setAge(24 * 16); // Age is more than required
        sheep.setAge(24 * 16);
        assertTrue(sheep.Reproduce(matureSheep));

        // Test with different species
        assertFalse(sheep.Reproduce(lion));

        // Test with canReproduce set to false
        sheep.setCanReproduce(false);
        assertFalse(sheep.Reproduce(matureSheep));
    }

    @Test
    public void testCheetahReproduce() {
        // Test with same species but too young
        Cheetah youngCheetah = new Cheetah(11, "YoungCheetah", false, 21, 21);
        youngCheetah.setAge(20); // Age is less than required 24*30
        assertFalse(cheetah.Reproduce(youngCheetah));

        // Test with same species and old enough
        Cheetah matureCheetah = new Cheetah(12, "MatureCheetah", false, 22, 22);
        matureCheetah.setAge(24 * 31); // Age is more than required
        cheetah.setAge(24 * 31);
        assertTrue(cheetah.Reproduce(matureCheetah));

        // Test with different species
        assertFalse(cheetah.Reproduce(lion));

        // Test with canReproduce set to false
        cheetah.setCanReproduce(false);
        assertFalse(cheetah.Reproduce(matureCheetah));
    }

    // Custom getter/setter methods for testing
    @Test
    public void testGettersAndSetters() {
        // Test setter and getter for hunger meter
        lion.setHungerMeter(50);
        assertEquals(50, lion.getHungerMeter());

        // Test setter and getter for thirst meter
        lion.setThirstMeter(75);
        assertEquals(75, lion.getThirstMeter());

        // Test setter and getter for canReproduce
        lion.setCanReproduce(false);
        assertFalse(lion.isCanReproduce());

        // Test getter for visionRadius
        assertTrue(lion.getVisionRadius() > 0);

        // Test getter for hungerChange
        assertTrue(lion.getHungerChange() > 0);
    }
}