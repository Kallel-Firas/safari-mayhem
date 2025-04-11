package Model;

import java.util.List;
import java.util.Optional;

public class Ranger extends Entity {
    private int salary;
    private static final int DETECTION_RADIUS = 8; // Rangers can detect poachers within 8 tiles
    private static final int ELIMINATION_RADIUS = 2; // Rangers can eliminate poachers within 2 tiles
    private static final int PATROL_RADIUS = 15; // Rangers patrol within this radius
    private float moveSpeed = 0.5f; // Speed for 60 FPS movement
    private float targetX;
    private float targetY;
    private boolean isPatrolling = true;

    public Ranger(int salary) {
        this.salary = salary;
        this.targetX = getCurrentX();
        this.targetY = getCurrentY();
    }

    public void update(Safari safari) {
        // First check for nearby poachers
        Optional<Poacher> nearestPoacher = findNearestPoacher(safari);
        
        if (nearestPoacher.isPresent()) {
            isPatrolling = false;
            Poacher poacher = nearestPoacher.get();
            
            if (isInEliminationRange(poacher)) {
                System.out.println("Ranger eliminated a poacher!");
                poacher.setEscaping(true);
            } else {
                moveTowardsPoacher(poacher, safari);
            }
        } else {
            isPatrolling = true;
            patrol(safari);
        }
    }

    private Optional<Poacher> findNearestPoacher(Safari safari) {
        return safari.getPoachers().stream()
            .filter(poacher -> isInProximity(poacher))
            .min((p1, p2) -> {
                double dist1 = distanceTo(p1);
                double dist2 = distanceTo(p2);
                return Double.compare(dist1, dist2);
            });
    }

    private double distanceTo(Poacher poacher) {
        int dx = poacher.getCurrentX() - getCurrentX();
        int dy = poacher.getCurrentY() - getCurrentY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void patrol(Safari safari) {
        // If reached target or no target set, choose new patrol point
        if (Math.abs(getCurrentX() - targetX) < 0.5 && Math.abs(getCurrentY() - targetY) < 0.5) {
            // Choose a new random point within patrol radius
            double angle = Math.random() * 2 * Math.PI;
            double distance = Math.random() * PATROL_RADIUS;
            targetX = (float) (getCurrentX() + Math.cos(angle) * distance);
            targetY = (float) (getCurrentY() + Math.sin(angle) * distance);
            
            // Ensure target is within map bounds
            targetX = Math.max(0, Math.min(safari.getLandscapes().size() - 1, targetX));
            targetY = Math.max(0, Math.min(safari.getLandscapes().get(0).size() - 1, targetY));
        }
        
        moveTowardsTarget(safari);
    }

    private void moveTowardsPoacher(Poacher poacher, Safari safari) {
        targetX = poacher.getCurrentX();
        targetY = poacher.getCurrentY();
        moveTowardsTarget(safari);
    }

    private void moveTowardsTarget(Safari safari) {
        float dx = targetX - getCurrentX();
        float dy = targetY - getCurrentY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            float newX = getCurrentX() + dx * moveSpeed;
            float newY = getCurrentY() + dy * moveSpeed;
            
            // Try to move in both directions if one is blocked
            if (!isValidPosition(safari, (int) newX, (int) newY)) {
                if (isValidPosition(safari, (int) newX, getCurrentY())) {
                    setCurrentX((int) newX);
                } else if (isValidPosition(safari, getCurrentX(), (int) newY)) {
                    setCurrentY((int) newY);
                }
            } else {
                setCurrentX((int) newX);
                setCurrentY((int) newY);
            }
        }
    }

    private boolean isValidPosition(Safari safari, int x, int y) {
        if (x < 0 || x >= safari.getLandscapes().size() || 
            y < 0 || y >= safari.getLandscapes().get(0).size()) {
            return false;
        }
        
        // Check for water
        if (safari.getLandscapes().get(x).get(y) instanceof Water) {
            return false;
        }
        
        // Check for vegetation
        if (safari.getVegetationList().stream()
            .anyMatch(v -> v.getCurrentX() == x && v.getCurrentY() == y)) {
            return false;
        }
        
        return true;
    }

    public void protectFromPoachers(List<Poacher> poachers) {
        for (Poacher poacher : poachers) {
            if (isInProximity(poacher)) {
                if (isInEliminationRange(poacher)) {
                    System.out.println("Ranger eliminated a poacher!");
                    poacher.setEscaping(true);
                }
            }
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
