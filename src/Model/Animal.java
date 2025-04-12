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

    public void Move(int x, int y, List<List<Landscape>> map, List<Entity> entities) {  //  removed the merge and added the list to the parameter blockList
        int targetX, targetY;
        if (x != currentX){
            targetX = currentX + Math.abs(x-currentX)/(x-currentX);
            if (!(map.get(targetX).get(currentY) instanceof Water) && emptySpace(targetX, currentY, entities)) {
                currentX = targetX;
                return;
            }
        } if (y != currentY){
            targetY = currentY + Math.abs(y-currentY)/(y-currentY);
            if (!(map.get(currentY).get(targetY) instanceof Water) && emptySpace(currentX, targetY, entities)) {
                currentY = targetY;
                return;
            }
        }
        // if cannot move in x or y direction, try to move in the opposite direction
        double randomValue = Math.random();
        if (randomValue < 0.5) {
            targetX = currentX - 1;
            if (targetX < 0){
                targetX = 2;
            }
        } else {
            targetX = currentX + 1;
            if (targetX > map.size()-1){
                targetX = map.size()-2;
            }
        }

        randomValue = Math.random();
        if (randomValue < 0.5) {
            targetY = currentY - 1;
            if (targetY < 0){
                targetY = 2;
            }
        } else {
            targetY = currentY + 1;
            if (targetY > map.get(0).size()-1){
                targetY = map.get(0).size()-2;
            }
        }

        randomValue = Math.random();
        if (randomValue < 0.5) {
            if (!(map.get(targetX).get(currentY) instanceof Water) && emptySpace(targetX, currentY, entities)) {
                currentX = targetX;
            }
        } else {
            if (!(map.get(currentX).get(targetY) instanceof Water) && emptySpace(currentX, targetY, entities)) {
                currentY = targetY;
            }
        }
    }


    public void Drink() {
        this.thirst_meter = 0;
    }


    public void Update() {
        setAge(getAge() + 1);
        if (age >= lifespan) {
            alive = false;
        }
    }
}
