package Model;

public class Grass extends Vegetation {
    public Grass(int x, int y) {
        super(x, y);
    }
    private int cost = 50;
    private int food_left = 1;
    public int getCost() {
        return cost;
    }
    public int getFoodLeft() {
        return food_left;
    }
    public void setFoodLeft(int change) {
        this.food_left += change;
    }

}