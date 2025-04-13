package Model;

public class Dirt extends Land {
    public Dirt() {
        super(0, 0);
    }

    public int getCost() {
        return cost;
    }

    private int cost = 0;
}
