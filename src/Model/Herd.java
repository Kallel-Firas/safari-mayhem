package Model;

import java.util.ArrayList;
import java.util.List;

public class Herd<T extends Animal> {
    private List<T> animalList;
    private boolean isSleeping = false;
    private boolean thirsty = false;
    private boolean hungry = false;

    public Herd() {
        this.animalList = new ArrayList<>();
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
        if (isSleeping && !thirsty && !hungry) {
            return;
        }
        if (isSleeping && (thirsty || hungry)) {
            isSleeping = false;
            SearchForFoodAndWater();
        } else if (!isSleeping && (thirsty || hungry)) {
            MoveTo((int)(Math.random()*50),  (int)(Math.random()*50));
            isSleeping = true;
        }
    }

    private void MoveTo(int x, int y) {
        for (T animal : animalList) {
            animal.Move(x, y, new ArrayList<>());
        }
    }

    private void SearchForFoodAndWater() {
       if (thirsty) {
           if (discoveredWaterLocation != null){
               MoveTo(discoveredWaterLocation[0], discoveredWaterLocation[1]);

           }
        }
        else if (hungry) {
            // Logic to search for food
        }
    }
}
