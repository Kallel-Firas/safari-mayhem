public class Grass extends Land {
    private int cost = 50;
    private int food_left = 1;

    public int getFoodLeft() {
        return food_left;
    }

    public void setFoodLeft(int food_left) {
        this.food_left = food_left;
    }
}