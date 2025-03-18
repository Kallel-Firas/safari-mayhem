package Model;

import java.util.Random;

public class Poacher {
    private boolean isVisible;
    private boolean hasCapturedAnimal;

    public Poacher(boolean isVisible) {
        this.isVisible = false;
    }

    public boolean attemptHunt() {
        Random rand = new Random();
        return rand.nextBoolean();

    }

    public boolean captureAnimal() {
        Random rand = new Random();
        hasCapturedAnimal =  rand.nextInt(100) < 60;
        return hasCapturedAnimal;
    }

    public boolean evadeRangers() {
        Random rand = new Random();
        return rand.nextBoolean();

    }

    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;
    }

    public boolean isVisible() {
        return isVisible;
    }
}