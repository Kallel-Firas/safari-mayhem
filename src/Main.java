import Model.Animal;

import java.util.List;

public class Main {


    private int difficultyLevel;
    private int speedMode;
    private String date;
    private String starting_date;
    private List<Animal> AnimalList;

    public void FastForward(String x) {
        return;
    }

    public int getSpeedMode() {
        return speedMode;
    }

    public void setSpeedMode(int speedMode) {
        this.speedMode = speedMode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Animal> getAnimalList() {
        return AnimalList;
    }
}