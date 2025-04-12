package Model;

import java.util.Random;

public class Poacher extends Entity {
    private boolean isVisible;
    private boolean hasCapturedAnimal;
    private int hoursSinceLastHunt = 0;
    private final int HUNT_INTERVAL = 5;

    public Poacher(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void passTime(int hours) {
        hoursSinceLastHunt += hours;
        if (hoursSinceLastHunt >= HUNT_INTERVAL) {
            boolean success = attemptHunt();
            if (success) {
                hasCapturedAnimal = true;
            } else {
                hasCapturedAnimal = false;
            }
            hoursSinceLastHunt = 0;
        }
    }

    public boolean attemptHunt() {
        Random rand = new Random();
        return rand.nextBoolean();
    }

    public boolean captureAnimal() {
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
