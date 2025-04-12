package Model;

public class Sheep extends Herbivorous {
    public Sheep(int id, String name, boolean isLeader, int currentX, int currentY) {
        super(id, name, 1, 0.4F / 24, 0.35F / 24, isLeader, 100 * 24, 7);
        this.setCurrentX(currentX);
        this.setCurrentY(currentY);
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

        if (getAge() > 24 * 15 && partner.getAge() > 24 * 15 & isCanReproduce() & partner.isCanReproduce()) {
            return partner instanceof Sheep;

        }
        return false;
    }
}