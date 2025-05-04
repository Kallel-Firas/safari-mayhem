package Model;

public class Lion extends Carnivorous {
    public Lion(int id, String name, boolean isLeader) {
        super(id, name, 1, 0.25F / 24, 0.25F / 24, isLeader, 70 * 24, 60);
        this.setReproductionCooldown(24 * 2); // 2 days cooldown
    }

    public Lion(int id, String name, boolean isLeader, int currentX, int currentY) {
        super(id, name, 1, 0.25F / 24, 0.25F / 24, isLeader, 70 * 24, 60);
        this.setCurrentX(currentX);
        this.setCurrentY(currentY);
        this.setReproductionCooldown(24 * 2); // 2 days cooldown
    }

    public boolean Eat(int x, int y) {
        return false;
    }

    public void Sleep() {
    }

    public void Drink() {
    }

    public boolean Reproduce(Animal partner) {
        // Lions can reproduce at age 25 days with 2 days cooldown
        if (getAge() > 24 * 25 && partner.getAge() > 24 * 25 && 
            isCanReproduce() && partner.isCanReproduce()) {
            return partner instanceof Lion;
        }
        return false;
    }
}