package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Herd<T extends Animal> {
    private final Random Random = new Random();
    private List<T> animalList;
    private boolean isSleeping = false;
    private boolean thirsty;
    private boolean hungry;
    private int[] discoveredWaterLocation = null;
    private int[] discoveredFoodLocation = null;
    private List<List<Landscape>> landscapeList;
    private int thirstMeter;
    private int hungerMeter;
    private final int thirstRate = 2;
    private final int hungerRate = 1; // Changed from 0 to 1 to enable hunger mechanics
    private List<Entity> entities = new ArrayList<>();
    private boolean isMoving = false;
    private int[] moveToLocation = null;
    private int restingTime = 0; // Counter for how long the herd has been resting
    private final int maxRestingTime = 50; // Maximum time to rest before moving again
    private Class<? extends Entity> food;
    private Safari safari;

    public Herd(List<List<Landscape>> landscapeList, Safari safari) {
        this.animalList = new ArrayList<>();
        this.landscapeList = landscapeList;
        thirstMeter = 100;
        thirsty = false;
        hungerMeter = 100;
        hungry = false;
        this.safari = safari;
    }

    public void updateLandscape(List<List<Landscape>> landscapeList) {
        this.landscapeList = landscapeList;
    }

    public void updateEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<T> getAnimalList() {
        return animalList;
    }

    public void addAnimal(T animal) {
        animalList.add(animal);
    }

    public void removeAnimal(T animal) {
        animalList.remove(animal);
    }

    public void generatePopulation(String animalClass) {
        if (animalClass.equals("Sheep")) {
            generateSheeps();
            food = Vegetation.class;
        } else if (animalClass.equals("Cheetah")) {
            generateCheetahs();
            food = Herbivorous.class;
        } else if (animalClass.equals("Lion")) {
            generateLions();
            food = Herbivorous.class;
        } else if (animalClass.equals("Elephant")) {
            generateElephants();
            food = Vegetation.class;
        }
    }

    private void generateSheeps() {
        int herdX = (int) (Math.random() * (50 - 3));
        int herdY = (int) (Math.random() * (50 - 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Sheep sheep = new Sheep(i, "Sheep" + i, true, herdX + i, herdY + j);
                addAnimal((T) sheep);
            }
        }
    }

    private void generateCheetahs() {
        int herdX = (int) (Math.random() * (50 - 3));
        int herdY = (int) (Math.random() * (50 - 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cheetah cheetah = new Cheetah(i, "Cheetah" + i, true, herdX + i, herdY + j);
                addAnimal((T) cheetah);
            }
        }
    }

    public int getThirstMeter(){
        return thirstMeter;
    }
    public int getHungerMeter(){
        return hungerMeter;
    }

    private void generateLions() {
        int herdX = (int) (Math.random() * (50 - 3));
        int herdY = (int) (Math.random() * (50 - 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Lion lion = new Lion(i, "Lion" + i, true, herdX + i, herdY + j);
                addAnimal((T) lion);
            }
        }
    }

    private void generateElephants() {
        int herdX = (int) (Math.random() * (50 - 3));
        int herdY = (int) (Math.random() * (50 - 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Elephant elephant = new Elephant(i, "Elephant" + i, true, herdX + i, herdY + j);
                addAnimal((T) elephant);
            }
        }
    }

    public void update() {
        if (animalList.isEmpty()) {
            return; // No animals to update
        }

        // Update thirst and hunger
        thirstMeter -= thirstRate;
        hungerMeter -= hungerRate;

        if (thirstMeter <= 30) {
            thirsty = true;
        }

        if (hungerMeter <= 30) {
            hungry = true;
        }

        // Handle death from thirst or hunger
        Iterator<T> iterator = animalList.iterator();
        while (iterator.hasNext()) {
            T animal = iterator.next();
            // Check if animal has died from thirst or hunger
            if (!animal.isAlive() || thirstMeter <= 0 || hungerMeter <= 0) {
                // Remove from map
                safari.removeEntityAt(animal.getCurrentX(), animal.getCurrentY(), Animal.class);
                // Remove from animal list
                iterator.remove();
                System.out.println(animal.getName() + " has died from " + 
                    (!animal.isAlive() ? "old age" : (thirstMeter <= 0 ? "thirst" : "hunger")));
            }
        }

        // Reset meters if all animals died
        if (animalList.isEmpty()) {
            thirstMeter = 100;
            hungerMeter = 100;
            return;
        }

        // Check for reproduction opportunities
        if (animalList.size() >= 2) {
            // Try to find a pair of animals that can reproduce
            for (int i = 0; i < animalList.size(); i++) {
                T animal1 = animalList.get(i);
                if (!animal1.isCanReproduce()) continue;

                for (int j = i + 1; j < animalList.size(); j++) {
                    T animal2 = animalList.get(j);
                    if (!animal2.isCanReproduce()) continue;

                    // Check if animals are close enough to reproduce (within 1 tile)
                    int dx = Math.abs(animal1.getCurrentX() - animal2.getCurrentX());
                    int dy = Math.abs(animal1.getCurrentY() - animal2.getCurrentY());
                    if (dx <= 1 && dy <= 1) {
                        // Try to reproduce
                        boolean canReproduce = ((Animal)animal1).Reproduce((Animal)animal2) || 
                                             ((Animal)animal2).Reproduce((Animal)animal1);
                        
                        if (canReproduce) {
                            // Find a valid position for the offspring
                            int[] offspringPos = findValidOffspringPosition(animal1.getCurrentX(), animal1.getCurrentY());
                            if (offspringPos != null) {
                                // Create and add the offspring
                                String speciesName = animal1.getClass().getSimpleName();
                                String offspringName = speciesName + "Baby" + safari.getNextAnimalId();
                                Animal offspring = animal1.createOffspring(
                                    safari.getNextAnimalId(),
                                    offspringName,
                                    offspringPos[0],
                                    offspringPos[1]
                                );
                                if (offspring != null) {
                                    addAnimal((T) offspring);
                                    safari.addAnimal(offspring);
                                    // Set reproduction cooldown for both parents
                                    animal1.setCanReproduce(false);
                                    animal1.setLastReproductionTime(animal1.getAge());
                                    animal2.setCanReproduce(false);
                                    animal2.setLastReproductionTime(animal2.getAge());
                                    // Display reproduction message in chat
                                    System.out.println("Reproduction successful! A new " + speciesName + " named " + 
                                        offspringName + " was born at position (" + offspringPos[0] + ", " + 
                                        offspringPos[1] + ")");
                                }
                            }
                        }
                    }
                }
            }
        }

        // Update each animal and check for death
        iterator = animalList.iterator();
        while (iterator.hasNext()) {
            T animal = iterator.next();
            animal.Update();
            
            // Check if animal has died of old age
            if (animal.getAge() >= animal.getLifespan()) {
                // Remove from map and animal list
                safari.removeEntityAt(animal.getCurrentX(), animal.getCurrentY(), Animal.class);
                iterator.remove();
                System.out.println("Animal " + animal.getName() + " has died of old age.");
            }
        }

        // Always check for resources in vision radius first if thirsty or hungry
        if (thirsty || hungry) {
            boolean resourceFound = checkForResourcesInVision();
            if (resourceFound) {
                return; // Resources found and being approached, no need for further movement
            }
        }

        // Check if any animal has reached the destination
        if (isMoving && moveToLocation != null) {
            boolean allArrived = false;
            for (T animal : animalList) {
                if (animal.getCurrentX() == moveToLocation[0] && animal.getCurrentY() == moveToLocation[1]) {
                    allArrived = true;
                    break;
                }
            }

            if (allArrived) {
                isMoving = false;
                System.out.println("Herd is starting to rest.");
                isSleeping = true;
                System.out.println("Herd has arrived at destination.");
            }
        }

        // Continue movement if the herd is currently moving
        if (isMoving && moveToLocation != null) {
            System.out.println("Herd still moving towards: " + moveToLocation[0] + ", " + moveToLocation[1]);
            MoveTo(moveToLocation[0], moveToLocation[1]);
            return;
        }

        // Handle different states
        if (!isSleeping && !thirsty && !hungry) {
            // If well-fed and not sleeping, start resting
            // Move to random location first
            chooseNewLocation();
            MoveTo(moveToLocation[0], moveToLocation[1]);
            if (restingTime == 0) {
                restingTime = 1;
            }
            // If already resting, increment resting time
            else if (restingTime < maxRestingTime) {
                restingTime++;
            }
            // If rested enough, choose a new location to move to
            else {
                System.out.println("Herd is done resting and will move to a new location.");
                chooseNewLocation();
                restingTime = 0;
            }
        }
        // If sleeping but now thirsty or hungry, wake up and search for resources
        else if (isSleeping && (thirsty || hungry)) {
            System.out.println("Herd woke up due to hunger or thirst.");
            isSleeping = false;
            restingTime = 0;

            if (thirsty) {
                SearchForWater();
            } else if (hungry) {
                SearchForFood();
            }
        }
        // If not sleeping and thirsty or hungry, search for resources
        else if (!isSleeping && (thirsty || hungry)) {
            if (thirsty) {
                SearchForWater();
            } else if (hungry) {
                SearchForFood();
            }
        }
    }

    // Add a new method to check for resources in vision radius
    private boolean checkForResourcesInVision() {
        // First check if we can drink or eat already
        if (thirsty && CanDrink()) {
            thirstMeter = 100;
            thirsty = false;
            for (T animal : animalList) {
                animal.Drink();
            }
            System.out.println("Herd found water and is drinking.");
            chooseNewLocation();
            return true;
        }

        if (hungry && CanEat()) {
            hungerMeter = 100;
            hungry = false;
            for (T animal : animalList) {
                animal.Eat();
            }
            System.out.println("Herd found food and is eating.");
            chooseNewLocation();
            return true;
        }

        // Check for water in vision radius if thirsty
        if (thirsty) {
            for (T animal : animalList) {
                for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                    for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                        int checkX = animal.getCurrentX() + i;
                        int checkY = animal.getCurrentY() + j;

                        if (InsideMap(checkX, checkY) && inRange(checkX, checkY) &&
                                landscapeList.get(checkX).get(checkY) instanceof Water) {
                            discoveredWaterLocation = new int[]{checkX, checkY};
                            System.out.println("Herd spotted water during movement at: (" +
                                    discoveredWaterLocation[0] + ", " + discoveredWaterLocation[1] + ")");
                            MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                            isMoving = true;
                            moveToLocation = discoveredWaterLocation;
                            return true;
                        }
                    }
                }
            }
        }

        // Check for food in vision radius if hungry
        if (hungry) {
            for (T animal : animalList) {
                for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                    for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                        int checkX = animal.getCurrentX() + i;
                        int checkY = animal.getCurrentY() + j;

                        if (InsideMap(checkX, checkY) && inRange(checkX, checkY)) {
                            // Check for appropriate food sources
                            for (Entity entity : entities) {
                                if ((isHerbivoreHerd() && entity instanceof Vegetation) ||
                                        (isCarnivoreHerd() && entity instanceof Herbivorous)) {
                                    if (entity.getCurrentX() == checkX && entity.getCurrentY() == checkY) {
                                        discoveredFoodLocation = new int[]{checkX, checkY};
                                        System.out.println("Herd spotted food during movement at: (" +
                                                discoveredFoodLocation[0] + ", " + discoveredFoodLocation[1] + ")");
                                        MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                                        isMoving = true;
                                        moveToLocation = discoveredFoodLocation;
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false; // No resources found in vision radius
    }

    // Choose a new random location for the herd to move to
    private void chooseNewLocation() {
        // Get the current center of the herd
        int avgX = 0, avgY = 0;
        for (T animal : animalList) {
            avgX += animal.getCurrentX();
            avgY += animal.getCurrentY();
        }
        avgX /= animalList.size();
        avgY /= animalList.size();
        
        // Choose a new location that is a reasonable distance away (between 10-20 tiles)
        int newX, newY;
        int attempts = 0;
        do {
            double angle = Math.random() * 2 * Math.PI; // Random direction
            double distance = 10 + Math.random() * 10; // Random distance between 10-20
            
            newX = avgX + (int)(Math.cos(angle) * distance);
            newY = avgY + (int)(Math.sin(angle) * distance);
            
            // Keep location within map bounds
            newX = Math.max(0, Math.min(49, newX));
            newY = Math.max(0, Math.min(49, newY));
            
            attempts++;
        } while (!isValidDestination(newX, newY) && attempts < 10);
        
        if (attempts >= 10) {
            // Fallback to a simple random location if we couldn't find a good one
            newX = Random.nextInt(50);
            newY = Random.nextInt(50);
        }
        
        System.out.println("Herd is moving to new location: (" + newX + ", " + newY + ")");
        moveToLocation = new int[]{newX, newY};
        isMoving = true;
        MoveTo(newX, newY);
    }

    // Check if a location is valid for the herd to move to
    private boolean isValidDestination(int x, int y) {
        // Check if the location is water (can't rest on water)
        if (x >= 0 && x < 50 && y >= 0 && y < 50 &&
                !(landscapeList.get(x).get(y) instanceof Dirt)) {
            return false;
        }
        
        // Make sure there's enough space for the herd (check nearby tiles)
        int validTiles = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (x + i >= 0 && x + i < 50 && y + j >= 0 && y + j < 50 && 
                    !(landscapeList.get(x + i).get(y + j) instanceof Water)) {
                    validTiles++;
                }
            }
        }
        
        return validTiles >= animalList.size(); // Ensure there are enough non-water tiles for all animals
    }

    private void MoveTo(int x, int y) {
        if (isExtinct()){
            return;
        }
        for (T animal : animalList) {
            if (animal.getCurrentX() != x || animal.getCurrentY() != y) {
                animal.Move(x, y, landscapeList, entities);
            }
        }
    }

    public boolean CanDrink() {
        for (T animal : animalList) {
            int x = animal.getCurrentX();
            int y = animal.getCurrentY();

            int[][] directions = {
                    {1, 0},  // right
                    {-1, 0}, // left
                    {0, 1},  // down
                    {0, -1}  // up
            };

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isWithinBounds(newX, newY) && isWaterAt(newX, newY)) {
                    replaceWithDirt(newX, newY);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWaterAt(int x, int y) {
        return landscapeList.get(x).get(y) instanceof Water;
    }

    private void replaceWithDirt(int x, int y) {
        landscapeList.get(x).set(y, new Dirt());
    }

    boolean isEntityThere(int x, int y, Class<? extends Entity> entityClass) {
        for (Entity entity : entities) {
            if (entity.getCurrentX() == x && entity.getCurrentY() == y
                    && entityClass.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean CanEat() {
        for (T animal : animalList) {
            int x = animal.getCurrentX();
            int y = animal.getCurrentY();

            // Define the four adjacent positions (right, left, down, up)
            int[][] directions = {
                    {1, 0},  // right
                    {-1, 0}, // left
                    {0, 1},  // down
                    {0, -1}  // up
            };

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isWithinBounds(newX, newY) && isEntityThere(newX, newY, food)) {
                    removeEntityAt(newX, newY, food);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < 50 && y >= 0 && y < 50;
    }

    private void removeEntityAt(int x, int y, Class<? extends Entity> entityClass) {
        safari.removeEntityAt(x, y, entityClass);
    }

    private boolean inRange(int x, int y) {
        int xDiff, yDiff;
        for (T animal : animalList) {
            xDiff = animal.getCurrentX() - x;
            yDiff = animal.getCurrentY() - y;
            if (xDiff * xDiff + yDiff * yDiff <= animal.getVisionRadius() * animal.getVisionRadius()) {
                return true;
            }
        }
        return false;
    }

    private boolean InsideMap(int x, int y) {
        return x >= 0 && x < 50 && y >= 0 && y < 50;
    }

    public boolean isExtinct() {
        return getAnimalList().isEmpty();
    }

    private void SearchForWater() {
        isMoving = true;
        moveToLocation = null;

        // First check if any animal can drink already
        if (CanDrink()) {
            thirstMeter = 100;
            thirsty = false;
            for (T animal : animalList) {
                animal.Drink();
            }
            System.out.println("Herd found water and is drinking.");
            chooseNewLocation();
            return;
        }

        // Use previously discovered water if available
        if (discoveredWaterLocation != null) {
            if (!inRange(discoveredWaterLocation[0], discoveredWaterLocation[1])) {
                // Move toward the water source
                System.out.println("Herd is moving to previously discovered water at: (" +
                        discoveredWaterLocation[0] + ", " + discoveredWaterLocation[1] + ")");
                MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                moveToLocation = discoveredWaterLocation;
                return;
            } else if (inRange(discoveredWaterLocation[0], discoveredWaterLocation[1])
                    && !(landscapeList.get(discoveredWaterLocation[0]).get(discoveredWaterLocation[1]) instanceof Water)) {
                // Water source has changed or dried up
                System.out.println("Water source has changed, searching for new water.");
                discoveredWaterLocation = null;
            } else if (inRange(discoveredWaterLocation[0], discoveredWaterLocation[1])
                    && landscapeList.get(discoveredWaterLocation[0]).get(discoveredWaterLocation[1]) instanceof Water) {
                // Water is in range and still exists, move to it
                System.out.println("Herd is approaching visible water.");
                MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                moveToLocation = discoveredWaterLocation;
                return;
            }
        }

        // Search for water in each animal's vision radius
        boolean foundWater = false;
        for (T animal : animalList) {
            for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                    if (InsideMap(animal.getCurrentX() + i, animal.getCurrentY() + j)
                            && inRange(animal.getCurrentX() + i, animal.getCurrentY() + j)
                            && landscapeList.get(animal.getCurrentX() + i).get(animal.getCurrentY() + j) instanceof Water) {
                        discoveredWaterLocation = new int[]{animal.getCurrentX() + i, animal.getCurrentY() + j};
                        System.out.println("Herd discovered water at: (" + discoveredWaterLocation[0] + ", " + discoveredWaterLocation[1] + ")");
                        MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                        moveToLocation = discoveredWaterLocation;
                        foundWater = true;
                        break;
                    }
                }
                if (foundWater) break;
            }
            if (foundWater) break;
        }

        // If no water is found, move to a random location to explore
        if (!foundWater) {
            int randomX = (int) (Math.random() * 50);
            int randomY = (int) (Math.random() * 50);

            // Try to find a valid position
            int attempts = 0;
            while ((!InsideMap(randomX, randomY) || !isValidDestination(randomX, randomY)) && attempts < 10) {
                randomX = (int) (Math.random() * 50);
                randomY = (int) (Math.random() * 50);
                attempts++;
            }

            System.out.println("No water found. Herd is exploring randomly at: (" + randomX + ", " + randomY + ")");
            moveToLocation = new int[]{randomX, randomY};
            MoveTo(randomX, randomY);
        }
    }

    private void SearchForFood() {
        isMoving = true;

        // First check if any animal can eat already
        if (CanEat()) {
            hungerMeter = 100;
            hungry = false;
            for (T animal : animalList) {
                animal.Eat();
            }
            System.out.println("Herd found food and is eating.");
            chooseNewLocation();
            return;
        }

        // Use previously discovered food source if available
        if (discoveredFoodLocation != null) {
            if (!inRange(discoveredFoodLocation[0], discoveredFoodLocation[1])) {
                // Move toward the food source
                System.out.println("Herd is moving to previously discovered food at: (" +
                        discoveredFoodLocation[0] + ", " + discoveredFoodLocation[1] + ")");
                MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                moveToLocation = discoveredFoodLocation;
                return;
            } else {
                // Check if food is still there
                boolean foodExists = false;
                for (Entity entity : entities) {
                    if ((isHerbivoreHerd() && entity instanceof Vegetation) ||
                            (isCarnivoreHerd() && entity instanceof Animal && !(entity instanceof Carnivorous))) {
                        if (entity.getCurrentX() == discoveredFoodLocation[0] &&
                                entity.getCurrentY() == discoveredFoodLocation[1]) {
                            foodExists = true;
                            break;
                        }
                    }
                }

                if (!foodExists) {
                    // Food source is gone
                    System.out.println("Food source has been depleted, searching for new food.");
                    discoveredFoodLocation = null;
                } else {
                    // Food is in range and still exists, move to it
                    System.out.println("Herd is approaching visible food.");
                    MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                    moveToLocation = discoveredFoodLocation;
                    return;
                }
            }
        }

        // Search for food in each animal's vision radius
        boolean foundFood = false;
        for (T animal : animalList) {
            for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                    int checkX = animal.getCurrentX() + i;
                    int checkY = animal.getCurrentY() + j;

                    if (InsideMap(checkX, checkY) && inRange(checkX, checkY)) {
                        // Check for appropriate food sources
                        for (Entity entity : entities) {
                            if ((isHerbivoreHerd() && entity instanceof Vegetation) ||
                                    (isCarnivoreHerd() && entity instanceof Animal && !(entity instanceof Carnivorous))) {
                                if (entity.getCurrentX() == checkX && entity.getCurrentY() == checkY) {
                                    discoveredFoodLocation = new int[]{checkX, checkY};
                                    System.out.println("Herd discovered food at: (" + discoveredFoodLocation[0] + ", " + discoveredFoodLocation[1] + ")");
                                    MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                                    moveToLocation = discoveredFoodLocation;
                                    foundFood = true;
                                    break;
                                }
                            }
                        }
                        if (foundFood) break;
                    }
                }
                if (foundFood) break;
            }
            if (foundFood) break;
        }

        // If no food is found, move to a random location to explore
        if (!foundFood) {
            int randomX = (int) (Math.random() * 50);
            int randomY = (int) (Math.random() * 50);

            // Try to find a valid position
            int attempts = 0;
            while ((!InsideMap(randomX, randomY) || !isValidDestination(randomX, randomY)) && attempts < 10) {
                randomX = (int) (Math.random() * 50);
                randomY = (int) (Math.random() * 50);
                attempts++;
            }

            System.out.println("No food found. Herd is exploring randomly at: (" + randomX + ", " + randomY + ")");
            moveToLocation = new int[]{randomX, randomY};
            MoveTo(randomX, randomY);
        }
    }
    
    // Helper methods to determine herd type
    private boolean isHerbivoreHerd() {
        if (animalList.isEmpty()) return false;
        return animalList.get(0) instanceof Herbivorous;
    }
    
    private boolean isCarnivoreHerd() {
        if (animalList.isEmpty()) return false;
        return animalList.get(0) instanceof Carnivorous;
    }

    // Helper method to find a valid position for offspring
    private int[] findValidOffspringPosition(int parentX, int parentY) {
        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},  // Adjacent positions
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal positions
        };

        // Try each direction
        for (int[] dir : directions) {
            int newX = parentX + dir[0];
            int newY = parentY + dir[1];

            // Check if position is valid and empty
            if (isWithinBounds(newX, newY) && 
                !(landscapeList.get(newX).get(newY) instanceof Water) &&
                emptySpace(newX, newY)) {
                return new int[]{newX, newY};
            }
        }
        return null;
    }

    // Helper method to check if a space is empty
    private boolean emptySpace(int x, int y) {
        for (Entity entity : entities) {
            if (entity.getCurrentX() == x && entity.getCurrentY() == y) {
                return false;
            }
        }
        return true;
    }
}