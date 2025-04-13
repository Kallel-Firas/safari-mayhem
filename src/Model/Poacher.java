package Model;

import java.util.List;
import java.util.Random;

public class Poacher extends Entity {
    private static final int DETECTION_RADIUS = 7; // Poachers can detect animals within 7 tiles
    private static final int HUNTING_RADIUS = 1; // Poachers can only hunt animals within 1 tile
    private static final double HUNTING_SUCCESS_RATE = 0.3; // 30% chance of success
    private int hoursOnMap = 0;
    private boolean hasCapturedAnimal = false;
    private boolean escaping = false;
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

    public void setTargetX(float x) {
        this.targetX = x;
    }

    public void setTargetY(float y) {
        this.targetY = y;
    }

    public boolean isEscaping() {
        return escaping;
    }

    public void setEscaping(boolean escaping) {
        this.escaping = escaping;
        if (escaping) {
            targetAnimal = null; // Stop hunting when escaping
        }
    }

    public void update(Safari safari) {
        if (escaping) {
            moveTowardsEdge(safari);
            if (isAtEdge(safari)) {
                safari.removePoacher(this);
                return;
            }
        } else {
            hoursOnMap++;
            
            // Only check time limit if not actively pursuing a target
            if (hoursOnMap >= 6 && !hasCapturedAnimal && targetAnimal == null) {
                escaping = true;
                return;
            }

            // Check for nearby rangers
            boolean rangerNearby = safari.getRangers().stream()
                .anyMatch(r -> Math.abs(r.getCurrentX() - getCurrentX()) <= 5 && 
                             Math.abs(r.getCurrentY() - getCurrentY()) <= 5);
            
            if (rangerNearby) {
                // Stop moving and wait for ranger
                targetX = getCurrentX();
                targetY = getCurrentY();
                return;
            }

            if (!hasCapturedAnimal) {
                if (targetAnimal == null || !isInDetectionRange(targetAnimal)) {
                    findNewTarget(safari);
                }
                
                if (targetAnimal != null) {
                    moveTowardsTarget(safari);
                } else {
                    wander(safari);
                }
            }
        }
    }

    private void findNewTarget(Safari safari) {
        List<Animal> nearbyAnimals = safari.getAnimalList().stream()
            .filter(animal -> isInDetectionRange(animal))
            .collect(java.util.stream.Collectors.toList());

        if (!nearbyAnimals.isEmpty()) {
            // Find the closest animal
            targetAnimal = nearbyAnimals.stream()
                .min((a1, a2) -> {
                    double dist1 = distanceToAnimal(a1);
                    double dist2 = distanceToAnimal(a2);
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);

            if (targetAnimal != null) {
                targetX = targetAnimal.getCurrentX();
                targetY = targetAnimal.getCurrentY();
                System.out.println("Poacher found new target animal at (" + targetX + "," + targetY + ")");
            }
        } else {
            targetAnimal = null;
        }
    }

    private double distanceToAnimal(Animal animal) {
        int dx = animal.getCurrentX() - getCurrentX();
        int dy = animal.getCurrentY() - getCurrentY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void moveTowardsTarget(Safari safari) {
        if (targetAnimal != null) {
            targetX = targetAnimal.getCurrentX();
            targetY = targetAnimal.getCurrentY();
            
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
            
            // Attempt to hunt as soon as we're in range
            if (isInHuntingRange(targetAnimal)) {
                attemptHunt(safari);
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
        
        // Check for water
        if (safari.getLandscapes().get(x).get(y) instanceof Water) {
            return false;
        }
        
        // Check for vegetation
        if (safari.getVegetationList().stream()
            .anyMatch(v -> v.getCurrentX() == x && v.getCurrentY() == y)) {
            return false;
        }
        
        // Check for other poachers
        if (safari.getPoachers().stream()
            .anyMatch(p -> p != this && p.getCurrentX() == x && p.getCurrentY() == y)) {
            return false;
        }
        
        return true;
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
                System.out.println("Poacher successfully hunted an animal!");
                
                // Remove animal from its herd
                for (Herd herd : safari.getHerdList()) {
                    if (herd.getAnimalList().contains(targetAnimal)) {
                        herd.getAnimalList().remove(targetAnimal);
                        System.out.println("Removed animal from herd");
                        break;
                    }
                }
                
                // Remove from safari's animal list
                safari.getAnimalList().remove(targetAnimal);
                System.out.println("Removed animal from safari list");
                
                hasCapturedAnimal = true;
                targetAnimal = null;
                
                // Start escaping after successful hunt
                escaping = true;
            } else {
                System.out.println("Poacher failed to hunt the animal");
                // Keep trying to hunt the same animal
            }
        }
    }

    private boolean isInDetectionRange(Animal animal) {
        int dx = animal.getCurrentX() - getCurrentX();
        int dy = animal.getCurrentY() - getCurrentY();
        return dx * dx + dy * dy <= DETECTION_RADIUS * DETECTION_RADIUS;
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

