package Model;

import java.util.List;
import java.util.ArrayList;

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


    public  List<List<Landscape>> getLandscapes() {// added this getter
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
        Herd<Elephant> elephantHerd = new Herd<>(landscapes);
        elephantHerd.generatePopulation("Elephant");
        Herd<Lion> lionHerd = new Herd<>(landscapes);
        lionHerd.generatePopulation("Lion");
        Herd<Cheetah> cheetahHerd = new Herd<>(landscapes);
        cheetahHerd.generatePopulation("Cheetah");
        Herd<Sheep> sheepHerd = new Herd<>(landscapes);
        sheepHerd.generatePopulation("Sheep");
        herdList.add(elephantHerd);
        herdList.add(lionHerd);
        herdList.add(cheetahHerd);
        herdList.add(sheepHerd);

        // Add some vegetation to the landscape
        for (int i = 0; i < 10; i++) {
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
    public void addJeep(Jeep jeep) { jeeps.add(jeep);}
    public void addPoacher(Poacher poacher) { poachers.add(poacher);}
    public void addRanger(Ranger ranger) { rangers.add(ranger);}

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
    Animal createAnimalInstance(Animal animal) {
        switch (animal.getClass().getSimpleName()) {
            case "Elephant":
                return new Elephant(animal.getId()*3, animal.getName()+animal.getId(), animal.isLeader(),animal.getCurrentX(),animal.getCurrentY());
            case "Tiger":
                return new Lion(animal.getId()*3, animal.getName()+animal.getId(), animal.isLeader(),animal.getCurrentX(),animal.getCurrentY());
            case "Cheetah":
                return new Cheetah(animal.getId()*3, animal.getName()+animal.getId(), animal.isLeader(),animal.getCurrentX(),animal.getCurrentY());
            case "Sheep":
                return new Sheep(animal.getId()*3, animal.getName()+animal.getId(), animal.isLeader(),animal.getCurrentX(),animal.getCurrentY());
            default:
                throw new IllegalArgumentException("Unknown animal class: " + animal.getClass().getSimpleName());
        }
    }
    public boolean pointChecker(int x,int y){
        List<List<Landscape>> map=getLandscapes();
        List<Landscape> columns=map.get(x);
        if(x>=0 && x<=map.size()-1 && y>=0 && y<=columns.size()-1 && columns.get(y) instanceof Land && ! (columns.get(y) instanceof  Road)){
            return true;
        }
        return false;
    }
    public boolean pathChecker(List<int[]> blockList){
        for(int[] block:blockList){
            if(!pointChecker(block[0],block[1])){
                return false;
            }
        }
        return true;
    }

    public int[] search(int currentX, int currentY, int radius, FoodType foodType) {
        List<List<Landscape>> landscapes = getLandscapes();
        int n = landscapes.size();
        int m = landscapes.getFirst().size();
        int[] closestFood = null;
        double closestDistance = Double.MAX_VALUE;

        if (foodType == FoodType.WATER) {
            for (int i = Math.max(0, currentX - radius); i <= Math.min(n - 1, currentX + radius); i++) {
                for (int j = Math.max(0, currentY - radius); j <= Math.min(m - 1, currentY + radius); j++) {
                    Landscape landscape = landscapes.get(i).get(j);
                    if (landscape instanceof Water) {
                        double distance = Math.sqrt(Math.pow(currentX - i, 2) + Math.pow(currentY - j, 2));
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestFood = new int[]{i, j};
                        }
                    }
                }
            }
        } else if (foodType == FoodType.LEAF) {
            for (Vegetation vegetation : vegetationList) {
                if (vegetation instanceof Tree || vegetation instanceof Bush || vegetation instanceof Grass) {
                    int vegetationX = vegetation.getCurrentX();
                    int vegetationY = vegetation.getCurrentY();
                    double distance = Math.sqrt(Math.pow(currentX - vegetationX, 2) + Math.pow(currentY - vegetationY, 2));
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestFood = new int[]{vegetationX, vegetationY};
                    }
                }
            }
        } else {
            for (Animal animal : getAnimalList()) {
                if (animal instanceof Sheep || animal instanceof Elephant) {
                    int animalX = animal.getCurrentX();
                    int animalY = animal.getCurrentY();
                    double distance = Math.sqrt(Math.pow(currentX - animalX, 2) + Math.pow(currentY - animalY, 2));
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestFood = new int[]{animalX, animalY};
                    }
                }
            }
        }
        return closestFood;
    }
    public void Update(){
        for(Animal animal : getAnimalList()) {
            if (animal.isAlive()) {
                animal.Update();
                Animal randomAnimal = null;
                while (randomAnimal == null) {
                    randomAnimal = getAnimalList().stream()
                            .filter(a -> a.getClass().equals(animal.getClass()))
                            .findAny()
                            .orElse(null);
                }


                if (animal.Reproduce(randomAnimal)) { // we have to check if the animals are in each others rads
                    getAnimalList().add(createAnimalInstance(animal));
                    animal.setCanReproduce(false);
                    randomAnimal.setCanReproduce(false);
                    animal.setHungerMeter(animal.getHungerMeter()+ 15);
                    randomAnimal.setHungerMeter(animal.getHungerMeter()+ 15);

                } else {
                    animal.setCanReproduce(true);
                }
                if (animal.getHungerMeter() > 40) {
                    FoodType foodType = (animal instanceof Sheep || animal instanceof Elephant) ? FoodType.LEAF : FoodType.MEAT;
                    int[] foodIndex = search(animal.getCurrentX(), animal.getCurrentY(), animal.getVisionRadius(), foodType);

                    if (foodIndex != null) {
                        moveTowardsTarget(animal, foodIndex[0], foodIndex[1],blockList);
                        if (animal.getCurrentX() == foodIndex[0] && animal.getCurrentY() == foodIndex[1]) {
                            animal.Eat();
                        }
                    }
                }

                // Handle Thirst and Movement (fixed code structure)
                if (animal.getThirstMeter() > 40) {
                    int[] waterIndex = search(animal.getCurrentX(), animal.getCurrentY(), animal.getVisionRadius(), FoodType.WATER);
                    if (waterIndex != null) {
                        moveTowardsTarget(animal, waterIndex[0], waterIndex[1],blockList);
                        if (animal.getCurrentX() == waterIndex[0] && animal.getCurrentY() == waterIndex[1]) {
                            animal.Drink();
                        }
                    }
                }

            }
        }
    }

    private void moveTowardsTarget(Animal animal, int targetX, int targetY,List<int[]> blockList) {
        int currentX = animal.getCurrentX();
        int currentY = animal.getCurrentY();

        int dx = targetX - currentX;
        int dy = targetY - currentY;

        int stepX = Integer.compare(dx, 0); 
        int stepY = Integer.compare(dy, 0);

        if (Math.abs(dx) > Math.abs(dy)) {
            stepY = 0;
        } else {
            stepX = 0;
        }

        int newX = currentX + stepX;
        int newY = currentY + stepY;

        if (pointChecker(newX, newY)) {
            animal.Move(newX, newY,blockList);
        }
    }
    public boolean isPointInsideRadius(int centerX, int centerY, int pointX, int pointY, int radius) {
        double distance = Math.sqrt(Math.pow(centerX - pointX, 2) + Math.pow(centerY - pointY, 2));
        return distance <= radius;
    }
}