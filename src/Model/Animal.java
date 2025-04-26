package Model;

import java.util.List;

public abstract class Animal extends Entity {
    private String name;
    private int id;
    private int age;
    private float hunger_change;
    private float thirst_change;
    private boolean isLeader = false;
    private boolean canReproduce = true;
    private int hunger_meter;
    private int thirst_meter;
    private int lifespan;
    private int groupSize;
    private int visionRadius;
    private List<int[]> water_locations;
    private List<int[]> food_locations;
    private boolean alive = true;
    private int currentX, currentY;
    private boolean isVisible;
    private boolean hasLocationChip;
    private int lastSeenX;
    private int lastSeenY;
    private String species;
    private int speed;
    private int size;
    private int rarity;
    private int value;
    private int dangerLevel;
    private String imagePath;

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public boolean isCanReproduce() {
        return canReproduce;
    }

    public void setCanReproduce(boolean canReproduce) {
        this.canReproduce = canReproduce;
    }

    public Animal(int id, String name, int age, float hunger_change, float thirst_change, boolean isLeader, int lifespan, int visionRadius) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.hunger_change = hunger_change;
        this.thirst_change = thirst_change;
        this.isLeader = isLeader;
        this.lifespan = lifespan;
        this.visionRadius = visionRadius;
    }

    public Animal(String name, String species, int x, int y, int speed, int size, int rarity, int value, int dangerLevel, String imagePath) {
        this.name = name;
        this.species = species;
        this.currentX = x;
        this.currentY = y;
        this.speed = speed;
        this.size = size;
        this.rarity = rarity;
        this.value = value;
        this.dangerLevel = dangerLevel;
        this.imagePath = imagePath;
        this.isVisible = false;
        this.hasLocationChip = false;
        this.lastSeenX = x;
        this.lastSeenY = y;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHungerChange() {
        return hunger_change;
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    public int getHungerMeter() {
        return hunger_meter;
    }

    public void setHungerMeter(int hunger_meter) {
        this.hunger_meter = hunger_meter;
    }

    public int getThirstMeter() {
        return thirst_meter;
    }

    public void setThirstMeter(int thirst_meter) {
        this.thirst_meter = thirst_meter;
    }

    public void Eat() {
        this.hunger_meter = 0;
    }

    private boolean emptySpace(int x, int y, List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity.getCurrentX() == x && entity.getCurrentY() == y && entity instanceof Animal) {
                return false;
            }
        }
        return true;
    }

    public void Move(int x, int y, List<List<Landscape>> map, List<Entity> entities) {
        // If already at destination, don't move
        if (currentX == x && currentY == y) {
            return;
        }

        // Calculate direction to target
        int xDirection = 0;
        int yDirection = 0;

        if (currentX < x) xDirection = 1;
        else if (currentX > x) xDirection = -1;

        if (currentY < y) yDirection = 1;
        else if (currentY > y) yDirection = -1;

        // Decide whether to move in X or Y direction first (random)
        boolean moveInXFirst = Math.random() < 0.5;

        // Try primary movement direction
        if (moveInXFirst && xDirection != 0) {
            // Try to move in X direction
            int newX = currentX + xDirection;
            if (isValidMove(newX, currentY, map, entities)) {
                currentX = newX;
                animateMovement(newX, currentY);
                return;
            }
        } else if (!moveInXFirst && yDirection != 0) {
            // Try to move in Y direction
            int newY = currentY + yDirection;
            if (isValidMove(currentX, newY, map, entities)) {
                currentY = newY;
                animateMovement(currentX, newY);
                return;
            }
        }

        // Try secondary movement direction if primary failed
        if (moveInXFirst && yDirection != 0) {
            // Try to move in Y direction
            int newY = currentY + yDirection;
            if (isValidMove(currentX, newY, map, entities)) {
                currentY = newY;
                animateMovement(currentX, newY);
                return;
            }
        } else if (!moveInXFirst && xDirection != 0) {
            // Try to move in X direction
            int newX = currentX + xDirection;
            if (isValidMove(newX, currentY, map, entities)) {
                currentX = newX;
                animateMovement(newX, currentY);
                return;
            }
        }

        // If we couldn't move directly toward the target, try a random adjacent move
        // This helps animals get around obstacles
        int[][] adjacentOffsets = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        // Shuffle the offsets to try them in random order
        for (int i = adjacentOffsets.length - 1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int[] temp = adjacentOffsets[i];
            adjacentOffsets[i] = adjacentOffsets[j];
            adjacentOffsets[j] = temp;
        }

        // Try each adjacent position in random order
        for (int[] offset : adjacentOffsets) {
            int newX = currentX + offset[0];
            int newY = currentY + offset[1];

            if (isValidMove(newX, newY, map, entities)) {
                currentX = newX;
                currentY = newY;
                animateMovement(newX, newY);
                return;
            }
        }

        // If we get here, the animal couldn't move at all this turn
        System.out.println("Animal at (" + currentX + "," + currentY + ") is stuck and cannot move toward (" + x + "," + y + ")");
    }

    // Helper method to check if a move is valid
    private boolean isValidMove(int x, int y, List<List<Landscape>> map, List<Entity> entities) {
        // Check bounds
        if (x < 0 || x >= map.size() || y < 0 || y >= map.get(0).size()) {
            return false;
        }

        // Check for water
        if (map.get(x).get(y) instanceof Water) {
            return false;
        }

        // Check for other animals (using existing emptySpace method)
        return emptySpace(x, y, entities);
    }

    public void Drink() {
        thirst_meter = 0;
    }

    public void Update() {
        setAge(getAge() + 1);
        if (age >= lifespan) {
            alive = false;
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
        if (visible) {
            lastSeenX = currentX;
            lastSeenY = currentY;
        }
    }

    public boolean hasLocationChip() {
        return hasLocationChip;
    }

    public void setHasLocationChip(boolean hasLocationChip) {
        this.hasLocationChip = hasLocationChip;
    }

    public int getLastSeenX() {
        return lastSeenX;
    }

    public int getLastSeenY() {
        return lastSeenY;
    }

    public String getSpecies() {
        return species;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSize() {
        return size;
    }

    public int getRarity() {
        return rarity;
    }

    public int getValue() {
        return value;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public String getImagePath() {
        return imagePath;
    }
}
