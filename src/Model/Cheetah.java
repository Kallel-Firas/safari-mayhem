package Model;

public class Cheetah extends Carnivorous {


    public Cheetah(int id, String name, boolean isLeader) {
        super(id, name, 1, 0.2F, 0.3F, isLeader, 70, 99);
    }

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