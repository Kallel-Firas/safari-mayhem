package Model;

public class Elephant extends Herbivorous {
    public Elephant(int id, String name, boolean isLeader) {
        super(id, name, 1, 0.4F/24, 0.35F/24, isLeader, 100*24, 35);
    }

    public Elephant(int id, String name, boolean isLeader, int currentX, int currentY) {
        super(id, name, 1, 0.4F/24, 0.35F/24, isLeader, 100*24, 35);
       this.setCurrentX(currentX);
       this.setCurrentY(currentY);
    }

    public void Sleep() {
    }

    public void Eat() {
        this.hunger_meter=0;
    }
    public void Drink() {
        this.thirst_meter = 0;
    }


    public boolean Reproduce(Animal partner) {// edited the Reporduce method to
        if(getAge()>24*20 && partner.getAge()>24*20& isCanReproduce() & partner.isCanReproduce())
            return partner instanceof Elephant;
        return false;
    }
}