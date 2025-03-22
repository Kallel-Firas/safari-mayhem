package Model;

import java.util.List;

public abstract class Animal {
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

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
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

    public String getName() {
        return name;
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

    public int getVisionRadius() {
        return visionRadius;
    }

    public void setVisionRadius(int visionRadius) {
        this.visionRadius = visionRadius;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
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
        this.hunger_meter=0;

    }

    public void Sleep() {
        // sleep method makes the animal do the sleep animation
    }

    public void Move(int x, int y,List<int[]> blockList) {  //  removed the merge and added the list to the parameter blockList
        for( int[] block : blockList) {
            //here we should but the graphic part where the animal moves to this block
        }
        setCurrentX(x);
        setCurrentY(y);
    }

    public void Drink() {
        this.thirst_meter = 0;
    }

    public boolean Reproduce(Animal partner) {

        return false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void Update() {
        setAge(getAge()+1);
        setHungerMeter(getHungerMeter() + (int)(100*getHungerChange()));
        setThirstMeter(getThirstMeter() +  (int)(100*getHungerChange()));

        if(age>=lifespan) {
            alive = false;
        }


    }

    public boolean Search(String target) {
        return false;
    }

    public int getId() {
        return id;
    }

    public boolean isLeader() {
        return isLeader;
    }
}
