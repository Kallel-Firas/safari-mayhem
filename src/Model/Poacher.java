package Model;

import java.util.List;
import java.util.Random;

public class Poacher extends Entity {
    private static final int HUNTING_RADIUS = 7;
    private static final double HUNTING_SUCCESS_RATE = 0.3; // 30% chance of success
    private int hoursOnMap = 0;
    private boolean hasCapturedAnimal = false;
    private boolean isEscaping = false;
    private float targetX;
    private float targetY;
    private float moveSpeed = 0.5f; // Speed for 60 FPS movement
    private Random random = new Random();
    private Animal targetAnimal = null;

    public Poacher(int x, int y) {
        setCurrentX(x);
        setCurrentY(y);
        this.targetX = x;
        this.targetY = y;
    }

    public void update(Safari safari) {
        if (isEscaping) {
            moveTowardsEdge(safari);
            if (isAtEdge(safari)) {
                safari.removePoacher(this);
                return;
            }
        } else {
            hoursOnMap++;
            if (hoursOnMap >= 6 || hasCapturedAnimal) {
                isEscaping = true;
                return;
            }

            if (!hasCapturedAnimal) {
                if (targetAnimal == null || !isInHuntingRange(targetAnimal)) {
                    findNewTarget(safari);
                }
                
                if (targetAnimal != null) {
                    moveTowardsTarget(safari);
                    attemptHunt(safari);
                } else {
                    // Wander around if no target found
                    wander(safari);
                }
            }
        }
    }

    private void findNewTarget(Safari safari) {
        List<Animal> nearbyAnimals = safari.getAnimalList().stream()
            .filter(animal -> isInHuntingRange(animal))
            .toList();

        if (!nearbyAnimals.isEmpty()) {
            targetAnimal = nearbyAnimals.get(random.nextInt(nearbyAnimals.size()));
            targetX = targetAnimal.getCurrentX();
            targetY = targetAnimal.getCurrentY();
        } else {
            targetAnimal = null;
        }
    }

    private void moveTowardsTarget(Safari safari) {
        if (targetAnimal == null) return;

        float dx = targetAnimal.getCurrentX() - getCurrentX();
        float dy = targetAnimal.getCurrentY() - getCurrentY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            float newX = getCurrentX() + dx * moveSpeed;
            float newY = getCurrentY() + dy * moveSpeed;
            
            if (isValidPosition(safari, (int) newX, (int) newY)) {
                setCurrentX((int) newX);
                setCurrentY((int) newY);
            }
        }
    }

    private void wander(Safari safari) {
        // Randomly change direction every few updates
        if (random.nextInt(100) < 5) {
            targetX = getCurrentX() + random.nextInt(5) - 2;
            targetY = getCurrentY() + random.nextInt(5) - 2;
            
            // Ensure target is within bounds
            targetX = Math.max(0, Math.min(safari.getLandscapes().size() - 1, targetX));
            targetY = Math.max(0, Math.min(safari.getLandscapes().get(0).size() - 1, targetY));
        }
        
        moveTowardsTarget(safari);
    }

    private void moveTowardsEdge(Safari safari) {
        // Find the nearest edge
        int mapWidth = safari.getLandscapes().size();
        int mapHeight = safari.getLandscapes().get(0).size();
        
        // Calculate distance to each edge
        int distToLeft = getCurrentX();
        int distToRight = mapWidth - getCurrentX() - 1;
        int distToTop = getCurrentY();
        int distToBottom = mapHeight - getCurrentY() - 1;
        
        // Find the closest edge
        int minDist = Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));
        
        // Set target position to the nearest edge
        if (minDist == distToLeft) {
            targetX = 0;
            targetY = getCurrentY();
        } else if (minDist == distToRight) {
            targetX = mapWidth - 1;
            targetY = getCurrentY();
        } else if (minDist == distToTop) {
            targetX = getCurrentX();
            targetY = 0;
        } else {
            targetX = getCurrentX();
            targetY = mapHeight - 1;
        }

        moveTowardsTarget(safari);
    }

    private boolean isValidPosition(Safari safari, int x, int y) {
        if (x < 0 || x >= safari.getLandscapes().size() || 
            y < 0 || y >= safari.getLandscapes().get(0).size()) {
            return false;
        }
        return !(safari.getLandscapes().get(x).get(y) instanceof Water);
    }

    private boolean isAtEdge(Safari safari) {
        int mapWidth = safari.getLandscapes().size();
        int mapHeight = safari.getLandscapes().get(0).size();
        return getCurrentX() <= 0 || getCurrentX() >= mapWidth - 1 ||
               getCurrentY() <= 0 || getCurrentY() >= mapHeight - 1;
    }

    private void attemptHunt(Safari safari) {
        if (targetAnimal != null && isInHuntingRange(targetAnimal)) {
            if (random.nextDouble() < HUNTING_SUCCESS_RATE) {
                // Remove animal from its herd
                for (Herd herd : safari.getHerdList()) {
                    if (herd.getAnimalList().contains(targetAnimal)) {
                        herd.getAnimalList().remove(targetAnimal);
                        break;
                    }
                }
                // Remove from safari's animal list
                safari.getAnimalList().remove(targetAnimal);
                hasCapturedAnimal = true;
                targetAnimal = null;
            }
        }
    }

    private boolean isInHuntingRange(Animal animal) {
        int dx = animal.getCurrentX() - getCurrentX();
        int dy = animal.getCurrentY() - getCurrentY();
        return dx * dx + dy * dy <= HUNTING_RADIUS * HUNTING_RADIUS;
    }

    public int getTimePresent() {
        return hoursOnMap;
    }

    public boolean hasCapturedAnimal() {
        return hasCapturedAnimal;
    }
}

