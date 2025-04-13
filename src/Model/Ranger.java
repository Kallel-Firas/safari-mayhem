package Model;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Ranger extends Entity {
    private int salary;
    private static final int DETECTION_RADIUS = 5; // Rangers can detect poachers within 5 tiles
    private static final int ELIMINATION_RADIUS = 1; // Rangers can eliminate poachers within 1 tile
    private static final int PATROL_RADIUS = 15; // Rangers patrol within this radius
    private static final int MAX_TIME_PRESENT = 10; // Rangers can stay for 10 hours
    private static final int BLOCKS_PER_DIRECTION = 3; // Number of blocks to move in one direction
    private float moveSpeed = 0.5f; // Normal speed
    private float chaseSpeed = 1.0f; // Speed when chasing poachers
    private float targetX;
    private float targetY;
    private boolean hasRepelledPoacher = false;
    private Poacher currentTarget = null;
    private Random random = new Random();
    private int timePresent = 0;
    private int blocksMovedInDirection = 0;
    private Direction currentDirection = Direction.NORTH;

    private enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public Ranger(int salary) {
        this.salary = salary;
        this.targetX = getCurrentX();
        this.targetY = getCurrentY();
    }

    public int getSalary() {
        return salary;
    }

    public boolean hasRepelledPoacher() {
        boolean result = hasRepelledPoacher;
        hasRepelledPoacher = false; // Reset the flag
        return result;
    }

    public void update(Safari safari) {
        timePresent++;

        if (timePresent >= MAX_TIME_PRESENT) {
            return; // Ranger has reached their time limit
        }

        // Always search for poachers
        searchForPoachers(safari);

        // If we have a target, move towards it
        if (currentTarget != null) {
            moveTowardsPoacher(currentTarget, safari);

            // If we're next to the poacher, eliminate them
            if (isInEliminationRange(currentTarget)) {
                System.out.println("Ranger caught a poacher!");
                safari.removePoacher(currentTarget);
                hasRepelledPoacher = true;
                currentTarget = null;
            }
        } else {
            // If no target, continue patrolling
            patrol(safari);
        }
    }

    private void searchForPoachers(Safari safari) {
        // Find the nearest poacher within detection radius
        Optional<Poacher> nearestPoacher = safari.getPoachers().stream()
            .filter(poacher -> isInProximity(poacher) && !poacher.isEscaping())
            .min((p1, p2) -> {
                double dist1 = distanceTo(p1);
                double dist2 = distanceTo(p2);
                return Double.compare(dist1, dist2);
            });

        if (nearestPoacher.isPresent()) {
            currentTarget = nearestPoacher.get();
            System.out.println("Ranger found a poacher at distance: " + distanceTo(currentTarget));
        }
    }

    private void patrol(Safari safari) {
        if (blocksMovedInDirection >= BLOCKS_PER_DIRECTION) {
            // Choose a new random direction
            Direction[] directions = Direction.values();
            currentDirection = directions[random.nextInt(directions.length)];
            blocksMovedInDirection = 0;
        }

        // Calculate target based on current direction
        switch (currentDirection) {
            case NORTH:
                targetX = getCurrentX();
                targetY = getCurrentY() - 1;
                break;
            case SOUTH:
                targetX = getCurrentX();
                targetY = getCurrentY() + 1;
                break;
            case EAST:
                targetX = getCurrentX() + 1;
                targetY = getCurrentY();
                break;
            case WEST:
                targetX = getCurrentX() - 1;
                targetY = getCurrentY();
                break;
        }

        // Ensure target is within map bounds
        targetX = Math.max(0, Math.min(safari.getLandscapes().size() - 1, targetX));
        targetY = Math.max(0, Math.min(safari.getLandscapes().get(0).size() - 1, targetY));

        // Move towards target
        if (moveTowardsTarget(safari, moveSpeed)) {
            blocksMovedInDirection++;
        } else {
            // If movement is blocked, choose a new direction
            blocksMovedInDirection = BLOCKS_PER_DIRECTION;
        }
    }

    private boolean moveTowardsTarget(Safari safari, float speed) {
        float dx = targetX - getCurrentX();
        float dy = targetY - getCurrentY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;

            float newX = getCurrentX() + dx * speed;
            float newY = getCurrentY() + dy * speed;

            if (isValidPosition(safari, (int) newX, (int) newY)) {
                setCurrentX((int) newX);
                setCurrentY((int) newY);
                return true;
            }
        }
        return false;
    }

    private void moveTowardsPoacher(Poacher poacher, Safari safari) {
        targetX = poacher.getCurrentX();
        targetY = poacher.getCurrentY();
        moveTowardsTarget(safari, chaseSpeed);
    }

    private double distanceTo(Poacher poacher) {
        int dx = poacher.getCurrentX() - getCurrentX();
        int dy = poacher.getCurrentY() - getCurrentY();
        return Math.sqrt(dx * dx + dy * dy);
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
