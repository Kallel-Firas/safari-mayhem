import java.util.List;

public class Ranger {
    private int salary;

    public Ranger(int salary) {
        this.salary = salary;
    }
    public void eliminatePredator() {
        System.out.println("Ranger is eliminating a predator.");

    }

    public void protectFromPoachers(Poacher poacher) {
        if (poacher.isVisible()) {
            System.out.println("Ranger is eliminating a poacher.");
        } else {
            System.out.println("No poachers detected nearby.");
        }
    }

    public void patrolSafari() {
        System.out.println("Ranger is patrolling the safari.");
    }
}
