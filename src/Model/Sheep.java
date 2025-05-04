package Model;

public class Sheep extends Herbivorous {
    public Sheep(int id, String name, boolean isLeader, int currentX, int currentY) {
        super(id, name, 1, 0.4F / 24, 0.35F / 24, isLeader, 100 * 24, 7);
        this.setCurrentX(currentX);
        this.setCurrentY(currentY);
        // Set reproduction cooldown to 1 day
        this.setReproductionCooldown(24);
    }

    public boolean Eat(int x, int y) {
        return false;
    }

    public boolean Reproduce(Animal partner) {
        // Sheep can reproduce at age 3 days with 1 day cooldown
        if (getAge() > 24 * 3 && partner.getAge() > 24 * 3 && isCanReproduce() && partner.isCanReproduce()) {
            return partner instanceof Sheep;
        }
        return false;
    }
}