package Model;

public class Ranger extends Entity {
    private int salary;

    public Ranger(int salary) {
        this.salary = salary;
    }

    public void eliminatePredator() {
        System.out.println("Model.Ranger is eliminating a predator.");

    }

    public void protectFromPoachers(Poacher poacher) {
        if (poacher.isVisible()) {
            System.out.println("Model.Ranger is eliminating a poacher.");
        } else {
            System.out.println("No poachers detected nearby.");
        }
    }

    public void patrolSafari() {
        System.out.println("Model.Ranger is patrolling the safari.");
    }
}
