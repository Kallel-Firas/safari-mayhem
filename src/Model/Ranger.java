package Model;

import java.util.List;

public class Ranger extends Entity{
    private int salary;
    private static final int DETECTION_RADIUS = 8; // Rangers can detect poachers within 8 tiles
    private static final int ELIMINATION_RADIUS = 2; // Rangers can eliminate poachers within 2 tiles

    public Ranger(int salary) {
        this.salary = salary;
    }
    public void eliminatePredator() {
        System.out.println("Ranger is eliminating a predator.");
    }

    public void protectFromPoachers(Poacher poacher) {
        if (isInProximity(poacher)) {
            System.out.println("Ranger is eliminating a poacher.");
        } else {
            System.out.println("No poachers detected nearby.");
        }
    }

    public void patrolSafari() {
        System.out.println("Ranger is patrolling the safari.");
    }

    public void eliminatePredator(Animal predator) {
        System.out.println("Ranger is eliminating a " + predator.getClass().getSimpleName() + ".");
    }

    public void protectFromPoachers(List<Poacher> poachers) {
        for (Poacher poacher : poachers) {
            if (isInProximity(poacher)) {
                // Check if poacher is within elimination radius
                if (isInEliminationRange(poacher)) {
                    System.out.println("Ranger eliminated a poacher!");
                    poacher.setEscaping(true); // Make poacher try to escape
                } else {
                    // Move towards poacher if they're detected but not in range
                    moveTowardsPoacher(poacher);
                }
            }
        }
    }

    private void moveTowardsPoacher(Poacher poacher) {
        int dx = poacher.getCurrentX() - getCurrentX();
        int dy = poacher.getCurrentY() - getCurrentY();
        
        // Move one step closer to the poacher
        if (Math.abs(dx) > Math.abs(dy)) {
            setCurrentX(getCurrentX() + Integer.signum(dx));
        } else {
            setCurrentY(getCurrentY() + Integer.signum(dy));
        }
    }

    boolean isInProximity(Poacher poacher) {
        int dx = poacher.getCurrentX() - getCurrentX();
        int dy = poacher.getCurrentY() - getCurrentY();
        return dx * dx + dy * dy <= DETECTION_RADIUS * DETECTION_RADIUS;
    }

    private boolean isInEliminationRange(Poacher poacher) {
        int dx = poacher.getCurrentX() - getCurrentX();
        int dy = poacher.getCurrentY() - getCurrentY();
        return dx * dx + dy * dy <= ELIMINATION_RADIUS * ELIMINATION_RADIUS;
    }

    public void paySalary() {
        System.out.println("Paying salary to ranger: " + salary);
    }
}
