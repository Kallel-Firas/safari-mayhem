package Model;

public class Cheetah extends Carnivorous {
    public boolean Eat(int x, int y) {
        return false;
    }

    public void Sleep() {
    }

    public void Drink() {
    }

    public boolean Reproduce(Animal partner) { // edited the Reporduce method to
        //to make it that if both animals are above a certain age they can reproduce
        if(getAge()>30 && partner.getAge()>30){
            return partner instanceof Cheetah;

        }
        return false;
    }
}