package Model;

public class Pond extends WaterSource {
    private int price;

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public Pond(int x, int y,int radius) {
        super(x,y,radius);
        if (radius < 8) {
            this.price = (int) Math.pow(radius, 5);
        } else {
            this.price = (int) Math.pow(radius, 4);
        }
    }

}