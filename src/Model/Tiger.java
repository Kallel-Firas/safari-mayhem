package Model;

public class Tiger extends Carnivorous {
    public Tiger(int id, String name,  boolean isLeader) {
        super(id, name,1, 0.25F, 0.25F, isLeader, 70,60);
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

        if(getAge()>25 && partner.getAge()>25){
            return partner instanceof Tiger;

        }
        return false;
    }
}