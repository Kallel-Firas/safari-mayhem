package Model;

public class Elephant extends Herbivorous {
    public Elephant(int id, String name, boolean isLeader) {
        super(id, name, 1, 0.4F, 0.35F, isLeader, 100, 35);
    }

    public boolean Eat(int x, int y) {
        return false;
    }

    public void Sleep() {
    }

    public void Drink() {
    }

    public boolean Reproduce(Animal partner) {// edited the Reporduce method to
        //to make it that if both animals are above a certain age they can reproduce

        if(getAge()>20 && partner.getAge()>20){
            return partner instanceof Elephant;

        }
        return false;
    }
}