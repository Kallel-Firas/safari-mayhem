package Model;

public class Jeep extends Entity {
    private int price = 2000;
    private int capacity = 4;
    private int rentalPrice;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setRentalPrice(int rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public void setCurrentPassengers(int currentPassengers) {
        this.currentPassengers = currentPassengers;
    }

    public int getCurrentPassengers() {
        return currentPassengers;
    }

    public int getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRentalPrice() {
        return rentalPrice;
    }

    private int currentPassengers;

    public Jeep(int capacity, int rentalPrice) {
        this.capacity = capacity;
        this.rentalPrice = rentalPrice;
    }

    public void navigateRoute() {
    }
}