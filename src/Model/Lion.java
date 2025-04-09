package Model;

public class Lion extends Carnivorous {
    public Lion(int id, String name,  boolean isLeader) {
        super(id, name,1, 0.25F/24, 0.25F/24, isLeader, 70*24,60);
    }

    public Lion(int id, String name, boolean isLeader, int currentX, int currentY) {
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
        //to make it that if both animals are above a certain age they can reproduce
        if(getAge()>24*25 && partner.getAge()>24*25 & isCanReproduce() & partner.isCanReproduce())
            return partner instanceof Lion;
        return false;
    }
}