package Model;

public class Elephant extends Herbivorous {
    public boolean Eat(int x, int y) {
        return false;
    }

    public void Sleep() {
    }

    public void Drink() {
    }

    public boolean Reproduce(Animal partner) {
        return partner instanceof Elephant;
    }
}