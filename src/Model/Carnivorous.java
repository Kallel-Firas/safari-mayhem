package Model;

public abstract class Carnivorous extends Animal {
    private boolean eatsHerbivores;
    private boolean needsWaterFoodInterval;

    public Carnivorous(int id, String name, int age, float hunger_change, float thirst_change, boolean isLeader, int lifespan, int visionRadius) {
        super(id, name, age, hunger_change, thirst_change, isLeader, lifespan, visionRadius);
    }

    public int getGroupSize() {
        return 0;
    }

    public void setGroupSize(int groupSize) {
    }
}