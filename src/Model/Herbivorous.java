package Model;

public abstract class Herbivorous extends Animal {
    private boolean eatsPlants;
    private boolean needsWaterFoodInterval;

    public Herbivorous(int id, String name, int age, float hunger_change, float thirst_change, boolean isLeader, int lifespan, int visionRadius) {
        super(id, name, age, hunger_change, thirst_change, isLeader, lifespan, visionRadius);
    }

    // Example fix for Herbivorous.eat
    public boolean Eat(Landscape landscape) {
        if (landscape instanceof Bush) {
            Bush bush = (Bush) landscape;
            if (bush.getFoodLeft() > 0) {
                bush.setFoodLeft(bush.getFoodLeft(), -1);
                setHungerMeter(0);
                return true;
            }
        } else if (landscape instanceof Tree || landscape instanceof Grass) {
            setHungerMeter(0);
            if(landscape instanceof Tree) {
                Tree tree = (Tree) landscape;
                if (tree.getFoodLeft() > 0) {
                    tree.setFoodLeft(tree.getFoodLeft(), -1);
                }
            }
            return true;
        }
        return false;
    }

    public int getGroupSize() {
        return 0;
    }

    public void setGroupSize(int groupSize) {
    }
}