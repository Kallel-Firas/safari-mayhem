package Model;

import java.util.ArrayList;
import java.util.List;

public class Safari {
    List<Herd> herdList;
    //private int difficultyLevel;   //all of these attributes are in the game section
    //private int speedMode;
    //private String startingDate;
    private List<Jeep> jeeps;
    private List<Poacher> poachers;
    private List<Ranger> rangers;
    private List<int[]> blockList = new ArrayList<>();
    private List<Vegetation> vegetationList = new ArrayList<>();
    private List<List<Landscape>> landscapes;


    public List<List<Landscape>> getLandscapes() {// added this getter
        return landscapes;
    }

    public Safari(int difficultyLevel, int speedMode, String startingDate) {
        //this.difficultyLevel = difficultyLevel;
        //this.speedMode = speedMode;
        //this.startingDate = startingDate;
        this.jeeps = new ArrayList<>();
        this.herdList = new ArrayList<>();
        this.poachers = new ArrayList<>();
        this.rangers = new ArrayList<>();
        // populate landscape
        this.landscapes = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            List<Landscape> column = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                column.add(new Dirt());
            }
            landscapes.add(column);
        }
        // Add some water to the landscape
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            // Add a pond, not just one tile.
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (x + j >= 0 && x + j < 50 && y + k >= 0 && y + k < 50) {
                        landscapes.get(x + j).set(y + k, new Water());
                    }
                }
            }
        }
        // Add some Animals to the landscape
        Herd<Elephant> elephantHerd = new Herd<>(landscapes, this);
        elephantHerd.generatePopulation("Elephant");
        Herd<Lion> lionHerd = new Herd<>(landscapes, this);
        lionHerd.generatePopulation("Lion");
        Herd<Cheetah> cheetahHerd = new Herd<>(landscapes, this);
        cheetahHerd.generatePopulation("Cheetah");
        Herd<Sheep> sheepHerd = new Herd<>(landscapes, this);
        sheepHerd.generatePopulation("Sheep");
        herdList.add(elephantHerd);
        herdList.add(lionHerd);
        herdList.add(cheetahHerd);
        herdList.add(sheepHerd);

        // Add some vegetation to the landscape
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            double randomValue = Math.random();
            if (randomValue < 0.3) {
                vegetationList.add(new Tree(x, y));
            } else if (randomValue < 0.7) {
                vegetationList.add(new Bush(x, y));
            } else {
                vegetationList.add(new Grass(x, y));
            }
        }
    }


    public List<Animal> getAnimalList() {
        List<Animal> animals = new ArrayList<>();
        for (Herd herd : herdList) {
            animals.addAll(herd.getAnimalList());
        }
        return animals;
    }

    public void addJeep(Jeep jeep) {
        jeeps.add(jeep);
    }

    public void addPoacher(Poacher poacher) {
        poachers.add(poacher);
    }

    public void addRanger(Ranger ranger) {
        rangers.add(ranger);
    }

    // add a public method to get all the entities (Animals, Poachers, Rangers, Jeeps)
    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        for (Herd herd : herdList) {
            entities.addAll(herd.getAnimalList());
        }
        entities.addAll(vegetationList);
        entities.addAll(poachers);
        entities.addAll(rangers);
        entities.addAll(jeeps);
        return entities;
    }


    public void UpdateSafari() {

    }

    public void setRangerList(List<Ranger> rangers) {
        this.rangers = rangers;
    }

    public void setPoacherList(List<Poacher> poachers) {
        this.poachers = poachers;
    }

    public void setJeepList(List<Jeep> jeeps) {
        this.jeeps = jeeps;
    }

    /*
    public int getSpeedMode() { return speedMode;}

    public void setSpeedMode(int speedMode) { this.speedMode = speedMode;}

    public String getDate() { return startingDate; }

    public void setDate(String date) { this.startingDate = date;}

    public void FastForward(String action) { System.out.println("Fast forwarding: " + action);}
     */



    public void Update() {
        List<Herd> deadHerds = new ArrayList<>();
        for (Herd herd : herdList) {
            if (herd.isExtinct()){
                deadHerds.add(herd);
                continue;
            }
            herd.updateLandscape(landscapes);
            herd.updateEntities(getEntities());
            herd.update();
        }
        for (Herd herd : deadHerds) {
            herdList.remove(herd);
        }
    }

    public void removeEntityAt(int x, int y, Class <? extends Entity> entityClass) {
        if (Animal.class.isAssignableFrom(entityClass)) {
            for (Herd herd : herdList){
                for (Object animal : herd.getAnimalList()) {
                    if (((Animal)animal).getCurrentX() == x && ((Animal)animal).getCurrentY() == y) {
                        herd.removeAnimal((Animal) animal);
                        return;
                    }
                }
            }
        }
        else {
            for (int i = 0; i < vegetationList.size(); i++) {
                if (vegetationList.get(i).getCurrentX() == x && vegetationList.get(i).getCurrentY() == y) {
                    vegetationList.remove(i);
                    return;
                }
            }
        }
    }

    public List<Ranger> getRangers() {
        return rangers;
    }

    public List<Poacher> getPoachers() {
        return poachers;
    }

    public List<Vegetation> getVegetationList() {
        return vegetationList;
    }

    public List<Herd> getHerdList() {
        return herdList;
    }

    public void removePoacher(Poacher poacher) {
        poachers.remove(poacher);
    }
}