public class Pond extends WaterSource {
    private int price;

    public Pond(int radius) {
        if (radius < 8) {
            this.price = (int) Math.pow(radius, 5);
        } else {
            this.price = (int) Math.pow(radius, 4);
        }
    }
}