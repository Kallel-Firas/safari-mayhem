package Model;

public class Dirt extends Land {
    public Dirt(int x, int y) {
        super(x, y);
    }

    public int getCost() {
        return cost;
    }

    private int cost = 0;
}
