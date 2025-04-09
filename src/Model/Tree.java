package Model;

public class Tree extends Vegetation {
    public Tree(int x, int y) {
        super(x, y);
    }
    private int cost = 300;
    private int food_left = 8;
    private boolean isGrown = true;
    public int getFoodLeft() {
        return food_left;
    }
    public void setFoodLeft(int change) {
        this.food_left += change;
    }

}