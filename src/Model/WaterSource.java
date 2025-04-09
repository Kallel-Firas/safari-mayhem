package Model;

public abstract class WaterSource extends Landscape {
    private int radius;

    public WaterSource(int x,int y,int radius) {
        super(x,y);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
