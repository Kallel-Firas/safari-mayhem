package Model;

import java.util.ArrayList;
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
    private final int thirstRate = 3;
    private final int hungerRate = 2; // Changed from 0 to 1 to enable hunger mechanics
    private List<Entity> entities;
    private boolean isMoving = false;
    private int[] moveToLocation = null;
    private int restingTime = 0; // Counter for how long the herd has been resting
    private final int maxRestingTime = 50; // Maximum time to rest before moving again
    private Class<? extends Entity> food;

    public Herd(List<List<Landscape>> landscapeList) {
        this.animalList = new ArrayList<>();
        this.landscapeList = landscapeList;
        thirstMeter = 100;
        thirsty = false;
        hungerMeter = 100;
        hungry = false;
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
        // Check if any animal has reached the destination
        if (isMoving && moveToLocation != null) {
            boolean allArrived = true;
            for (T animal : animalList) {
                // If any animal is more than 1 tile away from the destination, not all have arrived
                int xDiff = Math.abs(animal.getCurrentX() - moveToLocation[0]);
                int yDiff = Math.abs(animal.getCurrentY() - moveToLocation[1]);
                
                if (xDiff > 2 || yDiff > 2) {
                    allArrived = false;
                    break;
                }
            }
            
            if (allArrived) {
                isMoving = false;
                System.out.println("Herd has arrived at destination.");
            }
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
        if (thirstMeter <= 0 && !animalList.isEmpty()) {
            animalList.removeLast();
            thirstMeter = 10;
        } else if (hungerMeter <= 0 && !animalList.isEmpty()) {
            animalList.removeLast();
            hungerMeter = 10;
        }

        // Continue movement if the herd is currently moving
        if (isMoving && moveToLocation != null) {
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
                System.out.println("Herd is starting to rest.");
                isSleeping = true;
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
            landscapeList.get(x).get(y) instanceof Water) {
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

    private boolean CanDrink() {
        for (T animal : animalList) {
            if (animal.getCurrentX() + 1 < 50
                    && landscapeList.get(animal.getCurrentX() + 1).get(animal.getCurrentY()) instanceof Water) {
                return true;
            }
            if (animal.getCurrentX() - 1 >= 0
                    && landscapeList.get(animal.getCurrentX() - 1).get(animal.getCurrentY()) instanceof Water) {
                return true;
            }
            if (animal.getCurrentY() + 1 < 50
                    && landscapeList.get(animal.getCurrentX()).get(animal.getCurrentY() + 1) instanceof Water) {
                return true;
            }
            if (animal.getCurrentY() - 1 >= 0
                    && landscapeList.get(animal.getCurrentX()).get(animal.getCurrentY() - 1) instanceof Water) {
                return true;
            }
        }
        return false;
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
        // Check if any animal is adjacent to food sources
        for (T animal : animalList) {
            // Check all adjacent tiles
                if (animal.getCurrentX() + 1 < 50 && isEntityThere(animal.getCurrentX() + 1, animal.getCurrentY(), food)){
                    return true;
                }
                if (animal.getCurrentX() - 1 >= 0 && isEntityThere(animal.getCurrentX() - 1, animal.getCurrentY(), food)){
                    return true;
                }
                if (animal.getCurrentY() + 1 < 50 && isEntityThere(animal.getCurrentX(), animal.getCurrentY() + 1, food)){
                    return true;
                }
                if (animal.getCurrentY() - 1 >= 0 && isEntityThere(animal.getCurrentX(), animal.getCurrentY() - 1, food)){
                    return true;
                }
            }
        return false;
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
        // First check if any animal can drink already
        if (CanDrink()) {
            thirstMeter = 100;
            thirsty = false;
            for (T animal : animalList) {
                animal.Drink();
            }
            System.out.println("Herd found water and is drinking.");
            return;
        }
        
        // Use previously discovered water if available
        if (discoveredWaterLocation != null) {
            if (!inRange(discoveredWaterLocation[0], discoveredWaterLocation[1])) {
                // Move toward the water source
                System.out.println("Herd is moving to previously discovered water at: (" + 
                                   discoveredWaterLocation[0] + ", " + discoveredWaterLocation[1] + ")");
                MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
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
                return;
            }
        }
        
        // Search for water in each animal's vision radius
        for (T animal : animalList) {
            for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                    if (InsideMap(animal.getCurrentX() + i, animal.getCurrentY() + j)
                            && inRange(animal.getCurrentX() + i, animal.getCurrentY() + j)
                            && landscapeList.get(animal.getCurrentX() + i).get(animal.getCurrentY() + j) instanceof Water) {
                        discoveredWaterLocation = new int[]{animal.getCurrentX() + i, animal.getCurrentY() + j};
                        System.out.println("Herd discovered water at: (" + discoveredWaterLocation[0] + ", " + discoveredWaterLocation[1] + ")");
                        MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                        return;
                    }
                }
            }
        }
        
        // If no water is found, move to a random location to explore
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
            return;
        }
        
        // Use previously discovered food source if available
        if (discoveredFoodLocation != null) {
            if (!inRange(discoveredFoodLocation[0], discoveredFoodLocation[1])) {
                // Move toward the food source
                System.out.println("Herd is moving to previously discovered food at: (" + 
                                  discoveredFoodLocation[0] + ", " + discoveredFoodLocation[1] + ")");
                MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                return;
            } else {
                // Check if food is still there
                boolean foodExists = false;
                for (Entity entity : entities) {
                    if ((isHerbivoreHerd() && entity instanceof Vegetation) || 
                        (isCarnivoreCerd() && entity instanceof Animal && !(entity instanceof Carnivorous))) {
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
                    return;
                }
            }
        }
        
        // Search for food in each animal's vision radius
        for (T animal : animalList) {
            for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                    int checkX = animal.getCurrentX() + i;
                    int checkY = animal.getCurrentY() + j;
                    
                    if (InsideMap(checkX, checkY) && inRange(checkX, checkY)) {
                        // Check for appropriate food sources
                        for (Entity entity : entities) {
                            if ((isHerbivoreHerd() && entity instanceof Vegetation) || 
                                (isCarnivoreCerd() && entity instanceof Animal && !(entity instanceof Carnivorous))) {
                                if (entity.getCurrentX() == checkX && entity.getCurrentY() == checkY) {
                                    discoveredFoodLocation = new int[]{checkX, checkY};
                                    System.out.println("Herd discovered food at: (" + discoveredFoodLocation[0] + ", " + discoveredFoodLocation[1] + ")");
                                    MoveTo(discoveredFoodLocation[0], discoveredFoodLocation[1]);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // If no food is found, move to a random location to explore
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
    
    // Helper methods to determine herd type
    private boolean isHerbivoreHerd() {
        if (animalList.isEmpty()) return false;
        return animalList.get(0) instanceof Herbivorous;
    }
    
    private boolean isCarnivoreCerd() {
        if (animalList.isEmpty()) return false;
        return animalList.get(0) instanceof Carnivorous;
    }
}