public class Tree extends Land {
    private int cost = 300;
    private int food_left = 7;
    private boolean isGrown = true;

    public int getFoodLeft() {
        return food_left;
    }

    public void setFoodLeft(int food_left) {
        this.food_left = food_left;
    }
}