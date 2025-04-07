package Model;

import java.util.ArrayList;
import java.util.List;

public class Herd<T extends Animal> {
    private List<T> animalList;
    private boolean isSleeping = false;
    private boolean thirsty;
    private boolean hungry;
    private int[] discoveredWaterLocation = null;
    private int[] discoveredFoodLocation = null;
    private List<List<Landscape>> landscapeList;
    private int thirstMeter;
    private int hungerMeter;
    private final int thirstRate = 5;
    private final int hungerRate = 7;

    public Herd(List<List<Landscape>> landscapeList) {
        this.animalList = new ArrayList<>();
        this.landscapeList = new ArrayList<>();
        thirstMeter = 100;
        thirsty = false;
        hungerMeter = 100;
        hungry = false;
    }

    public List<T> getAnimalList() {
        return animalList;
    }
    public void addAnimal(T animal) {
        animalList.add(animal);
    }

    public void removeAnimal(T animal) {
        animalList.remove(animal);
    }

    public void generatePopulation(String animalClass) {
            if (animalClass.equals("Sheep")) {
                generateSheeps();
            } else if (animalClass.equals("Cheetah")) {
                generateCheetahs();
            } else if (animalClass.equals("Lion")) {
                generateLions();
            } else if (animalClass.equals("Elephant")) {
                generateElephants();
            }
    }

    private void generateSheeps() {
        int herdX = (int) (Math.random()*(50-3));
        int herdY = (int) (Math.random()*(50-3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Sheep sheep = new Sheep(i, "Sheep" + i, true, herdX + i, herdY + j);
                addAnimal((T) sheep);
            }
        }
    }

    private void generateCheetahs() {
        int herdX = (int) (Math.random()*(50-3));
        int herdY = (int) (Math.random()*(50-3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cheetah cheetah = new Cheetah(i, "Cheetah" + i, true, herdX + i, herdY + j);
                addAnimal((T) cheetah);
            }
        }
    }
    private void generateLions() {
        int herdX = (int) (Math.random()*(50-3));
        int herdY = (int) (Math.random()*(50-3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Lion lion = new Lion(i, "Lion" + i, true, herdX + i, herdY + j);
                addAnimal((T) lion);
            }
        }
    }
    private void generateElephants() {
        int herdX = (int) (Math.random()*(50-3));
        int herdY = (int) (Math.random()*(50-3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Elephant elephant = new Elephant(i, "Elephant" + i, true, herdX + i, herdY + j);
                addAnimal((T) elephant);
            }
        }
    }

    public void update(){
        thirstMeter -= thirstRate;
        hungerMeter -= hungerRate;
        if (thirstMeter <= 30) {
            thirsty = true;
        }
        if (hungerMeter <= 30) {
            hungry = true;
        }
        if (isSleeping && !thirsty && !hungry) {
            return;
        }
        if (isSleeping && (thirsty || hungry)) {
            isSleeping = false;
        } else if (!isSleeping && (thirsty || hungry)) {
            SearchForFoodAndWater();
            isSleeping = true;
        }
    }

    private void MoveTo(int x, int y) {
        for (T animal : animalList) {
            animal.Move(x, y, new ArrayList<>());
        }
    }

    private boolean CanDrink() {
        for (T animal : animalList) {
            if (animal.getCurrentX() + 1 < 50
                && landscapeList.get(animal.getCurrentX() + 1).get(animal.getCurrentY()) instanceof Water) {
                    return true;
            }
            if (animal.getCurrentX() - 1 >= 0
                && landscapeList.get(animal.getCurrentX() - 1).get(animal.getCurrentY()) instanceof Water) {
                    return true;
            }
            if (animal.getCurrentY() + 1 < 50
                && landscapeList.get(animal.getCurrentX()).get(animal.getCurrentY() + 1) instanceof Water) {
                    return true;
            }
            if (animal.getCurrentY() - 1 >= 0
                && landscapeList.get(animal.getCurrentX()).get(animal.getCurrentY() - 1) instanceof Water) {
                    return true;
            }
            return false;
        }
        return false;
    }

    private boolean in_range(int x, int y) {
        int xDiff, yDiff;
        for (T animal : animalList) {
            xDiff = animal.getCurrentX() - x;
            yDiff = animal.getCurrentY() - y;
            if (xDiff*xDiff + yDiff*yDiff <= animal.getVisionRadius()*animal.getVisionRadius()) {
                return true;
            }
        }
        return false;
    }

    private boolean InsideMap(int x, int y) {
        return x >= 0 && x < 50 && y >= 0 && y < 50;
    }

    private void SearchForFoodAndWater() {
       if (thirsty) {
           if (CanDrink()){
               thirstMeter = 100;
                thirsty = false;
                for (T animal : animalList) {
                     if (landscapeList.get(animal.getCurrentX()).get(animal.getCurrentY()) instanceof Water) {
                          animal.Drink();
                          return;
                     }
                }
           }
           if (discoveredWaterLocation != null){
               if (!in_range(discoveredWaterLocation[0], discoveredWaterLocation[1])) {
                   MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                   return;
               } else if (in_range(discoveredWaterLocation[0], discoveredWaterLocation[1])
                       && !(landscapeList.get(discoveredWaterLocation[0]).get(discoveredWaterLocation[1]) instanceof Water)) {
                   discoveredWaterLocation = null;
               } else if (in_range(discoveredWaterLocation[0], discoveredWaterLocation[1])
                       && landscapeList.get(discoveredWaterLocation[0]).get(discoveredWaterLocation[1]) instanceof Water) {
                    MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                    return;
               }
           }
           for (T animal : animalList) {
               for (int i = -animal.getVisionRadius(); i <= animal.getVisionRadius(); i++) {
                   for (int j = -animal.getVisionRadius(); j <= animal.getVisionRadius(); j++) {
                       if (InsideMap(animal.getCurrentX()+i, animal.getCurrentY()+j)
                               && in_range(animal.getCurrentX() + i, animal.getCurrentY() + j)
                               && landscapeList.get(animal.getCurrentX() + i).get(animal.getCurrentY() + j) instanceof Water) {
                           discoveredWaterLocation = new int[]{animal.getCurrentX() + i, animal.getCurrentY() + j};
                           MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);
                           return;
                       }
                   }
               }

           }
        }
        else if (hungry) {
            // Logic to search for food
        }
    }
}
