package Model;

public class Bush extends Vegetation {
    public Bush(int x, int y) {
        super(x, y);
    }
    private int cost = 100;
    private int food_left = 3;
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
