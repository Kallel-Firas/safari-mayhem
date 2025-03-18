package Model;

public abstract class Herbivorous extends Animal {
    private boolean eatsPlants;
    private boolean needsWaterFoodInterval;

    public boolean Eat(int x, int y) {
        return false;
    }

    public int getGroupSize() {
        return 0;
    }

    public void setGroupSize(int groupSize) {
    }
}