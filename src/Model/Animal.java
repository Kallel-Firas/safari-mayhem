package Model;

import java.util.List;

public abstract class Animal extends Entity {
    // Basic properties
    private int id;
    private String name;
    private int age;
    private boolean alive = true;
    private int lifespan;
    private boolean isLeader = false;

    // Movement and vision properties

    private int visionRadius;
    private int groupSize;
    public List<int[]> blockList;

    // Resource tracking
    private List<int[]> water_locations;
    private List<int[]> food_locations;

    // Needs properties
    int hunger_meter;
    int thirst_meter;
    private float hunger_change;
    private float thirst_change;
    private boolean canReproduce = true;

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

    // Core methods
    public void Update() {
        setAge(getAge()+1);
        if(age>=lifespan|| thirst_meter>=100 || hunger_meter>=100) {
            alive = false;
        }
    }

    public void Move(int x, int y,List<int[]> blockList) {  //  removed the merge and added the list to the parameter blockList
        for( int[] block : blockList) {
            //here we should but the graphic part where the animal moves to this block
        }
        setCurrentX(x);
        setCurrentY(y);
    }

    public void Eat() {
        this.hunger_meter=0;
    }

    public void Sleep() {
        // sleep method makes the animal do the sleep animation
    }

    public void Drink() {
        this.thirst_meter = 0;
    }

    public boolean Search(String target) {
        return false;
    }

    public boolean Reproduce(Animal partner) {
        return false;
    }




    // Getters and setters for basic properties
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    // Getters and setters for needs
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

    public float getHungerChange() {
        return hunger_change;
    }

    public void setHungerChange(float hunger_change) {
        if (this.age > 2) {
            this.hunger_change = hunger_change * 2;
        } else {
            this.hunger_change = hunger_change;
        }
    }

    public float getThirstChange() {
        return thirst_change;
    }

    public void setThirstChange(float thirst_change) {
        if (this.age > 2) {
            this.thirst_change = thirst_change * 2;
        } else {
            this.thirst_change = thirst_change;
        }
    }

    // Getters and setters for other properties
    public int getVisionRadius() {
        return visionRadius;
    }

    public void setVisionRadius(int visionRadius) {
        this.visionRadius = visionRadius;
    }

    public boolean isCanReproduce() {
        return canReproduce;
    }

    public void setCanReproduce(boolean canReproduce) {
        this.canReproduce = canReproduce;
    }
}