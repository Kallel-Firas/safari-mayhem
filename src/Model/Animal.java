package Model;

import java.util.List;

/**
 * Abstract base class for all animals in the safari simulation.
 * Handles basic animal properties and behaviors such as hunger, thirst,
 * movement, reproduction, and lifecycle management.
 */
public abstract class Animal extends Entity {
    // Basic identification
    private String name;
    private int id;
    private int age;

    // Survival attributes
    private float hunger_change;
    private float thirst_change;
    private int hunger_meter;
    private int thirst_meter;
    private int lifespan;
    private boolean alive = true;

    // Group behavior
    private boolean isLeader = false;
    private int groupSize;
    private int visionRadius;

    // Reproduction
    private boolean canReproduce = true;

    // Movement and positioning
    private int currentX, currentY;
    public List<int[]> blockList; // Blocked positions for pathfinding
    private List<int[]> water_locations;
    private List<int[]> food_locations;

    // Smooth movement system
    private int targetX, targetY;          // Target grid position
    private boolean hasTarget = false;     // Whether animal is currently moving to a target
    private double visualX, visualY;       // Visual position for smooth animation (floating point)

    /**
     * Sets a target position for the animal to move toward.
     * Initializes the visual position to the current grid position.
     *
     * @param x The target x-coordinate
     * @param y The target y-coordinate
     */
    public void setTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
        this.hasTarget = true;
        // Initialize visual positions to current grid positions
        this.visualX = this.currentX;
        this.visualY = this.currentY;
    }

    /**
     * Checks if the animal is currently moving toward a target.
     *
     * @return true if the animal has a movement target
     */
    public boolean hasTarget() {
        return hasTarget;
    }

    /**
     * Clears the current movement target.
     * Called when an animal reaches its destination.
     */
    public void clearTarget() {
        hasTarget = false;
    }

    /**
     * @return The target x-coordinate
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * @return The target y-coordinate
     */
    public int getTargetY() {
        return targetY;
    }

    /**
     * @return The current visual x-position for smooth rendering
     */
    public double getVisualX() {
        return visualX;
    }

    /**
     * @return The current visual y-position for smooth rendering
     */
    public double getVisualY() {
        return visualY;
    }

    /**
     * Updates the visual x-position for smooth animation.
     *
     * @param visualX The new visual x-position
     */
    public void setVisualX(double visualX) {
        this.visualX = visualX;
    }

    /**
     * Updates the visual y-position for smooth animation.
     *
     * @param visualY The new visual y-position
     */
    public void setVisualY(double visualY) {
        this.visualY = visualY;
    }

    /**
     * @return The current x-coordinate in the grid
     */
    @Override
    public int getCurrentX() {
        return currentX;
    }

    /**
     * @return The current y-coordinate in the grid
     */
    @Override
    public int getCurrentY() {
        return currentY;
    }

    /**
     * Sets the current y-coordinate in the grid.
     *
     * @param currentY The new y-coordinate
     */
    @Override
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    /**
     * Sets the current x-coordinate in the grid.
     *
     * @param currentX The new x-coordinate
     */
    @Override
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    /**
     * @return Whether the animal can reproduce
     */
    public boolean isCanReproduce() {
        return canReproduce;
    }

    /**
     * Sets whether the animal can reproduce.
     *
     * @param canReproduce The reproduction ability status
     */
    public void setCanReproduce(boolean canReproduce) {
        this.canReproduce = canReproduce;
    }

    /**
     * Constructor for creating an animal with all necessary parameters.
     *
     * @param id The unique identifier
     * @param name The name of the animal
     * @param age The age of the animal
     * @param hunger_change The rate of hunger increase per hour
     * @param thirst_change The rate of thirst increase per hour
     * @param isLeader Whether the animal is a group leader
     * @param lifespan The maximum age the animal can reach (in hours)
     * @param visionRadius How far the animal can see for food/water/mates
     */
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

    /**
     * @return The animal's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The animal's current age
     */
    public int getAge() {
        return age;
    }

    /**
     * Updates the animal's age.
     *
     * @param age The new age value
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return The animal's hunger change rate
     */
    public float getHungerChange() {
        return hunger_change;
    }

    /**
     * Sets the hunger change rate with age adjustment.
     * Older animals (age > 2) have doubled hunger rates.
     *
     * @param hunger_change The base hunger change rate
     */
    public void setHungerChange(float hunger_change) {
        if (this.age > 2) {
            this.hunger_change = hunger_change * 2;
        } else {
            this.hunger_change = hunger_change;
        }
    }

    /**
     * @return The animal's thirst change rate
     */
    public float getThirstChange() {
        return thirst_change;
    }

    /**
     * Sets the thirst change rate with age adjustment.
     * Older animals (age > 2) have doubled thirst rates.
     *
     * @param thirst_change The base thirst change rate
     */
    public void setThirstChange(float thirst_change) {
        if (this.age > 2) {
            this.thirst_change = thirst_change * 2;
        } else {
            this.thirst_change = thirst_change;
        }
    }

    /**
     * @return The animal's vision radius
     */
    public int getVisionRadius() {
        return visionRadius;
    }

    /**
     * Sets the animal's vision radius.
     *
     * @param visionRadius The new vision radius
     */
    public void setVisionRadius(int visionRadius) {
        this.visionRadius = visionRadius;
    }

    /**
     * Sets whether the animal is a leader.
     *
     * @param isLeader The leadership status
     */
    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    /**
     * @return The current hunger level
     */
    public int getHungerMeter() {
        return hunger_meter;
    }

    /**
     * Sets the hunger level.
     *
     * @param hunger_meter The new hunger level
     */
    public void setHungerMeter(int hunger_meter) {
        this.hunger_meter = hunger_meter;
    }

    /**
     * @return The current thirst level
     */
    public int getThirstMeter() {
        return thirst_meter;
    }

    /**
     * Sets the thirst level.
     *
     * @param thirst_meter The new thirst level
     */
    public void setThirstMeter(int thirst_meter) {
        this.thirst_meter = thirst_meter;
    }

    /**
     * Animal eats food, resetting its hunger meter.
     */
    public void Eat() {
        this.hunger_meter = 0;
    }

    /**
     * Makes the animal sleep (animation).
     */
    public void Sleep() {
        // sleep method makes the animal do the sleep animation
    }

    /**
     * Moves the animal to a new position.
     *
     * @param x The new x-coordinate
     * @param y The new y-coordinate
     * @param blockList List of blocked positions
     */
    public void Move(int x, int y, List<int[]> blockList) {
        for (int[] block : blockList) {
            // Here we should add the graphic part where the animal moves to this block
        }
        setCurrentX(x);
        setCurrentY(y);
    }

    /**
     * Animal drinks water, resetting its thirst meter.
     */
    public void Drink() {
        this.thirst_meter = 0;
    }

    /**
     * Attempts reproduction with another animal.
     * To be implemented by specific animal subclasses.
     *
     * @param partner The potential reproduction partner
     * @return true if reproduction is successful
     */
    public boolean Reproduce(Animal partner) {
        return false;
    }

    /**
     * @return Whether the animal is alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Updates the animal's state for one hour of game time.
     * Increases age, hunger, and thirst levels.
     * Checks if the animal has reached its lifespan.
     */
    public void Update() {
        setAge(getAge() + 1);
        setHungerMeter(getHungerMeter() + (int)(100 * getHungerChange()));
        setThirstMeter(getThirstMeter() + (int)(100 * getThirstChange()));

        if (age >= lifespan || hunger_meter >= 100 || thirst_meter >= 100) {
            alive = false;
        }
    }

    /**
     * Searches for a target near the animal.
     *
     * @param target The type of target to search for
     * @return true if target was found
     */
    public boolean Search(String target) {
        return false;
    }

    /**
     * @return The animal's unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return Whether the animal is a leader
     */
    public boolean isLeader() {
        return isLeader;
    }
}