package Model;

import java.util.List;
import java.util.ArrayList;

public class Safari {
    private List<Herd> herdList;
    private List<Jeep> jeeps;
    private List<Poacher> poachers;
    private List<Ranger> rangers;
    private List<Vegetation> vegetationList;

    private List<List<Landscape>> landscapes;
    private List<int[]> blockList;

    public Safari(int difficultyLevel, int speedMode, String startingDate) {
        initializeLists();
        createLandscape();
        populateAnimals();
        addVegetation();
    }

    private void initializeLists() {
        jeeps = new ArrayList<>();
        herdList = new ArrayList<>();
        poachers = new ArrayList<>();
        rangers = new ArrayList<>();
        blockList = new ArrayList<>();
        vegetationList = new ArrayList<>();
    }

    private void createLandscape() {
        landscapes = new ArrayList<>();
        createEmptyLandscape();
        addWaterBodies();
    }

    private void createEmptyLandscape() {
        for (int i = 0; i < 50; i++) {
            List<Landscape> column = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                column.add(new Dirt(i,j));
            }
            landscapes.add(column);
        }
    }

    private void addWaterBodies() {
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            createPond(x, y);
        }
    }

    private void createPond(int x, int y) {
        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
                if (x + j >= 0 && x + j < 50 && y + k >= 0 && y + k < 50) {
                    landscapes.get(x + j).set(y + k, new Water(x,y,1));
                }
            }
        }
    }

    private void populateAnimals() {
        Herd<Elephant> elephantHerd = new Herd<>(landscapes);
        Herd<Lion> lionHerd = new Herd<>(landscapes);
        Herd<Cheetah> cheetahHerd = new Herd<>(landscapes);
        Herd<Sheep> sheepHerd = new Herd<>(landscapes);

        elephantHerd.generatePopulation("Elephant");
        lionHerd.generatePopulation("Lion");
        cheetahHerd.generatePopulation("Cheetah");
        sheepHerd.generatePopulation("Sheep");

        herdList.add(elephantHerd);
        herdList.add(lionHerd);
        herdList.add(cheetahHerd);
        herdList.add(sheepHerd);
    }

    private void addVegetation() {
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            addRandomVegetation(x, y);
        }
    }

    private void addRandomVegetation(int x, int y) {
        double randomValue = Math.random();
        if (randomValue < 0.3) {
            vegetationList.add(new Tree(x, y));
        } else if (randomValue < 0.7) {
            vegetationList.add(new Bush(x, y));
        } else {
            vegetationList.add(new Grass(x, y));
        }
    }

    // Update methods
    public void Update() {
        for(Animal animal : getAnimalList()) {
            if (animal.isAlive()) {
                updateAnimal(animal);
            }
        }
    }

    private void updateAnimal(Animal animal) {
        animal.Update();
        handleReproduction(animal);
        handleHunger(animal);
        handleThirst(animal);
    }

    private void handleReproduction(Animal animal) {
        Animal randomAnimal = getAnimalList().stream()
                .filter(a -> a.getClass().equals(animal.getClass()))
                .findAny()
                .orElse(null);

        if (randomAnimal != null && animal.Reproduce(randomAnimal)) {
            processReproduction(animal, randomAnimal);
        } else {
            animal.setCanReproduce(true);
        }
    }

    private void processReproduction(Animal animal, Animal partner) {
        getAnimalList().add(createAnimalInstance(animal));
        animal.setCanReproduce(false);
        partner.setCanReproduce(false);
        animal.setHungerMeter(animal.getHungerMeter() + 15);
        partner.setHungerMeter(partner.getHungerMeter() + 15);
    }

    private void handleHunger(Animal animal) {
        if (animal.getHungerMeter() > 40) {
            FoodType foodType = (animal instanceof Sheep || animal instanceof Elephant) ?
                    FoodType.LEAF : FoodType.MEAT;
            moveToFood(animal, foodType);
        }
    }

    private void handleThirst(Animal animal) {
        if (animal.getThirstMeter() > 40) {
            int[] waterIndex = search(animal.getCurrentX(), animal.getCurrentY(),
                    animal.getVisionRadius(), FoodType.WATER);
            if (waterIndex != null) {
                moveTowardsTarget(animal, waterIndex[0], waterIndex[1], blockList);
                if (animal.getCurrentX() == waterIndex[0] &&
                        animal.getCurrentY() == waterIndex[1]) {
                    animal.Drink();
                }
            }
        }
    }

    private void moveToFood(Animal animal, FoodType foodType) {
        int[] foodIndex = search(animal.getCurrentX(), animal.getCurrentY(),
                animal.getVisionRadius(), foodType);
        if (foodIndex != null) {
            moveTowardsTarget(animal, foodIndex[0], foodIndex[1], blockList);
            if (animal.getCurrentX() == foodIndex[0] &&
                    animal.getCurrentY() == foodIndex[1]) {
                animal.Eat();
            }
        }
    }

    // Movement and search methods
    private int[] search(int centerX, int centerY, int visionRadius, FoodType foodType) {
        // Search in a square area around the center point
        for (int x = centerX - visionRadius; x <= centerX + visionRadius; x++) {
            for (int y = centerY - visionRadius; y <= centerY + visionRadius; y++) {
                // Check if point is within bounds and within circular vision radius
                if (x >= 0 && x < landscapes.size() &&
                        y >= 0 && y < landscapes.get(0).size() &&
                        isPointInsideRadius(centerX, centerY, x, y, visionRadius)) {

                    // Check for the requested resource type
                    switch (foodType) {
                        case WATER:
                            if (landscapes.get(x).get(y) instanceof Water) {
                                return new int[]{x, y};
                            }
                            break;

                        case LEAF:
                            // Check vegetation list for plants at this location
                            for (Vegetation vegetation : vegetationList) {
                                if (vegetation.getCurrentX() == x &&
                                        vegetation.getCurrentY() == y) {
                                    return new int[]{x, y};
                                }
                            }
                            break;

                        case MEAT:
                            // Look for herbivorous animals
                            for (Animal animal : getAnimalList()) {
                                if (animal.getCurrentX() == x &&
                                        animal.getCurrentY() == y &&
                                        animal instanceof Herbivorous &&
                                        animal.isAlive()) {
                                    return new int[]{x, y};
                                }
                            }
                            break;
                    }
                }
            }
        }
        return null; // Return null if no suitable resource is found
    }
    private void moveTowardsTarget(Animal animal, int targetX, int targetY,
                                   List<int[]> blockList) {
        int currentX = animal.getCurrentX();
        int currentY = animal.getCurrentY();
        int[] newPosition = calculateNextStep(currentX, currentY, targetX, targetY);

        if (pointChecker(newPosition[0], newPosition[1])) {
            animal.Move(newPosition[0], newPosition[1], blockList);
        }
    }

    private int[] calculateNextStep(int currentX, int currentY, int targetX, int targetY) {
        int dx = targetX - currentX;
        int dy = targetY - currentY;
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);

        if (Math.abs(dx) > Math.abs(dy)) {
            stepY = 0;
        } else {
            stepX = 0;
        }

        return new int[]{currentX + stepX, currentY + stepY};
    }

    // Helper methods
    public boolean pointChecker(int x, int y) {
        List<List<Landscape>> map = getLandscapes();
        if (x < 0 || x >= map.size() || y < 0 || y >= map.get(x).size()) {
            return false;
        }
        Landscape landscape = map.get(x).get(y);
        return landscape instanceof Land && !(landscape instanceof Road);
    }

    public boolean pathChecker(List<int[]> blockList) {
        return blockList.stream().allMatch(block -> pointChecker(block[0], block[1]));
    }

    public boolean isPointInsideRadius(int centerX, int centerY, int pointX,
                                       int pointY, int radius) {
        double distance = Math.sqrt(Math.pow(centerX - pointX, 2) +
                Math.pow(centerY - pointY, 2));
        return distance <= radius;
    }

    // Animal creation
    Animal createAnimalInstance(Animal animal) {
        switch (animal.getClass().getSimpleName()) {
            case "Elephant":
                return new Elephant(animal.getId()*3, animal.getName()+animal.getId(),
                        animal.isLeader(), animal.getCurrentX(),
                        animal.getCurrentY());
            case "Tiger":
                return new Lion(animal.getId()*3, animal.getName()+animal.getId(),
                        animal.isLeader(), animal.getCurrentX(),
                        animal.getCurrentY());
            case "Cheetah":
                return new Cheetah(animal.getId()*3, animal.getName()+animal.getId(),
                        animal.isLeader(), animal.getCurrentX(),
                        animal.getCurrentY());
            case "Sheep":
                return new Sheep(animal.getId()*3, animal.getName()+animal.getId(),
                        animal.isLeader(), animal.getCurrentX(),
                        animal.getCurrentY());
            default:
                throw new IllegalArgumentException("Unknown animal class: " +
                        animal.getClass().getSimpleName());
        }
    }

    // Getters and setters
    public List<List<Landscape>> getLandscapes() {
        return landscapes;
    }

    public List<Animal> getAnimalList() {
        List<Animal> animals = new ArrayList<>();
        for (Herd herd : herdList) {
            animals.addAll(herd.getAnimalList());
        }
        return animals;
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.addAll(getAnimalList());
        entities.addAll(vegetationList);
        entities.addAll(poachers);
        entities.addAll(rangers);
        entities.addAll(jeeps);
        return entities;
    }

    public void addJeep(Jeep jeep) { jeeps.add(jeep); }
    public void addPoacher(Poacher poacher) { poachers.add(poacher); }
    public void addRanger(Ranger ranger) { rangers.add(ranger); }
    public void setRangerList(List<Ranger> rangers) { this.rangers = rangers; }
    public void setPoacherList(List<Poacher> poachers) { this.poachers = poachers; }
    public void setJeepList(List<Jeep> jeeps) { this.jeeps = jeeps; }
    public void UpdateSafari() { }

    public void addAnimal(Animal animal) {
        // Find the appropriate herd for this animal type
        for (Herd herd : herdList) {
            if (herd.getAnimalList().isEmpty() ||
                    herd.getAnimalList().get(0).getClass() == animal.getClass()) {
                herd.getAnimalList().add(animal);
                return;
            }
        }
        // If no matching herd found, create a new one
        Herd newHerd = new Herd<>(landscapes);
        newHerd.getAnimalList().add(animal);
        herdList.add(newHerd);
    }

    private int nextAnimalId = 1;
    private int getNextAnimalId() {
        return nextAnimalId++;
    }

    public void addVegetation(Vegetation vegetation) {
        vegetationList.add(vegetation);
    }

    public void setLandscape(int x, int y, Landscape landscape) {
        if (x >= 0 && x < landscapes.size() && y >= 0 && y < landscapes.get(0).size()) {
            landscapes.get(x).set(y, landscape);
        }
    }
}