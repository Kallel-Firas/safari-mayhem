package Model;

public class Sheep extends Herbivorous {
    public Sheep(int id, String name, boolean isLeader) {
        super(id, name,1, 0.35F, 0.2F, isLeader, 35, 25);
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

        if(getAge()>15 && partner.getAge()>15){
            return partner instanceof Sheep;

        }
        return false;
    }
}