package Model;

public class Cheetah extends Carnivorous {


    public Cheetah(int id, String name, boolean isLeader) {
        super(id, name, 1, 0.2F / 24, 0.3F / 24, isLeader, 70 * 24, 99);
    }

    public Cheetah(int id, String name, boolean isLeader, int currentX, int currentY) {
        super(id, name, 1, 0.4F / 24, 0.35F / 24, isLeader, 100 * 24, 7);
        this.setCurrentX(currentX);
        this.setCurrentY(currentY);
        this.setReproductionCooldown(24 * 3);
    }

    public boolean Eat(int x, int y) {
        return false;
    }

    public void Sleep() {
    }

    public void Drink() {
    }

    public boolean Reproduce(Animal partner) {
        if (getAge() > 24 * 9 && partner.getAge() > 24 * 9 && isCanReproduce() && partner.isCanReproduce()) {
            return partner instanceof Cheetah;
        }
        return false;
    }
}