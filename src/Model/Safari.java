package Model;

import java.util.List;
import java.util.ArrayList;

public class Safari {
    List<Animal> AnimalList;
    private List<Jeep> jeeps;
    private List<Poacher> poachers;
    private List<Ranger> rangers;
    private List<int[]> blockList = new ArrayList<>();


    public  List<List<Landscape>> getLandscapes() {// added this getter
        return landscapes;
    }

    private List<Object> animals;
    private List<List<Landscape>> landscapes;
    public Safari(int difficultyLevel, int speedMode, String startingDate) {

        this.jeeps = new ArrayList<>();
        this.AnimalList = new ArrayList<>();
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
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            Animal animal = new Elephant(i, "Elephant" + i, false, x, y);
            AnimalList.add(animal);
        }
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            Animal animal = new Lion(i, "Lion" + i, false, x, y);
            AnimalList.add(animal);
        }
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            Animal animal = new Cheetah(i, "Cheetah" + i, false, x, y);
            AnimalList.add(animal);
        }
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            Animal animal = new Sheep(i, "Sheep" + i, false, x, y);
            AnimalList.add(animal);
        }
    }

    public void setAnimalList(List<Animal> animalList) {
        AnimalList = animalList;
    }
    public List<Animal> getAnimalList() {return AnimalList;}
    public void addJeep(Jeep jeep) { jeeps.add(jeep);}
    public void addPoacher(Poacher poacher) { poachers.add(poacher);}
    public void addRanger(Ranger ranger) { rangers.add(ranger);}
    public void addAnimals(Animal animal) {AnimalList.add(animal);}

    // add a public method to get all the entities (Animals, Poachers, Rangers, Jeeps)
    public List<Object> getEntities() {
        List<Object> entities = new ArrayList<>();
        entities.addAll(AnimalList);
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
                return null; // Return null instead of throwing exception
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
        int m = landscapes.get(0).size();
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
            for (int i = Math.max(0, currentX - radius); i <= Math.min(n - 1, currentX + radius); i++) {
                for (int j = Math.max(0, currentY - radius); j <= Math.min(m - 1, currentY + radius); j++) {
                    Landscape landscape = landscapes.get(i).get(j);
                    if (landscape instanceof Tree || landscape instanceof Grass || landscape instanceof Bush) {
                        double distance = Math.sqrt(Math.pow(currentX - i, 2) + Math.pow(currentY - j, 2));
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestFood = new int[]{i, j};
                        }
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
        for(Animal animal : AnimalList) {
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