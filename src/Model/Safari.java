package Model;

import java.util.List;
import java.util.ArrayList;

public class Safari {
    List<Animal> AnimalList;
    //private int difficultyLevel;   //all of these attributes are in the game section
    //private int speedMode;
    //private String startingDate;
    private List<Jeep> jeeps;
    private List<Poacher> poachers;
    private List<Ranger> rangers;

    public  List<List<Landscape>> getLandscapes() {// added this getter
        return landscapes;
    }

    private List<Object> animals;
    private List<List<Landscape>> landscapes;
    public Safari(int difficultyLevel, int speedMode, String startingDate) {
        //this.difficultyLevel = difficultyLevel;
        //this.speedMode = speedMode;
        //this.startingDate = startingDate;
        this.jeeps = new ArrayList<>();
        this.AnimalList = new ArrayList<>();
        this.poachers = new ArrayList<>();
        this.rangers = new ArrayList<>();
    }

    public void setAnimalList(List<Animal> animalList) {
        AnimalList = animalList;
    }
    public List<Animal> getAnimalList() {return AnimalList;}
    public void addJeep(Jeep jeep) { jeeps.add(jeep);}
    public void addPoacher(Poacher poacher) { poachers.add(poacher);}
    public void addRanger(Ranger ranger) { rangers.add(ranger);}
    public void addAnimals(Animal animal) {AnimalList.add(animal);}


    public void UpdateSafari(){

    }
    public void setRangerList(List<Ranger> rangers) { this.rangers = rangers;}

    public void setPoacherList(List<Poacher> poachers) { this.poachers = poachers;}

    public void setJeepList(List<Jeep> jeeps) {this.jeeps = jeeps;}

    /*
    public int getSpeedMode() { return speedMode;}

    public void setSpeedMode(int speedMode) { this.speedMode = speedMode;}

    public String getDate() { return startingDate; }

    public void setDate(String date) { this.startingDate = date;}

    public void FastForward(String action) { System.out.println("Fast forwarding: " + action);}

     */
}