package Model;

import java.util.List;
import java.util.Random;

public class Poacher extends Entity {
    private static final int DETECTION_RADIUS = 7; // Poachers can detect animals within 7 tiles
    private static final int HUNTING_RADIUS = 3; // Poachers can hunt animals within 3 tiles
    private static final double HUNTING_SUCCESS_RATE = 0.3; // 30% chance of success
    private int hoursOnMap = 0;
    private int restHours = 0;
    private boolean hasCapturedAnimal = false;
    private boolean escaping = false;
    private float targetX;
    private float targetY;
    private float moveSpeed = 0.5f; // Speed for 60 FPS movement
    private Random random = new Random();
    private Animal targetAnimal = null;
    private boolean isResting = false;

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
            
            // Check if poacher has been on map for 18 hours
            if (hoursOnMap >= 18) {
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

            if (isResting) {
                restHours++;
                if (restHours >= 2) {
                    isResting = false;
                    restHours = 0;
                }
                return;
            }

            if (!hasCapturedAnimal) {
                if (targetAnimal == null || !isInDetectionRange(targetAnimal)) {
                    findNewTarget(safari);
                }
                
                if (targetAnimal != null) {
                    moveTowardsTarget(safari);
                    // Check if we've moved our vision radius
                    if (hasMovedVisionRadius()) {
                        isResting = true;
                    }
                } else {
                    wander(safari);
                }
            }
        }
    }

    private boolean hasMovedVisionRadius() {
        int dx = getCurrentX() - (int)targetX;
        int dy = getCurrentY() - (int)targetY;
        return Math.sqrt(dx*dx + dy*dy) >= DETECTION_RADIUS;
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
            // Update target position in case animal moved
            targetX = targetAnimal.getCurrentX();
            targetY = targetAnimal.getCurrentY();
            
            // Calculate direction to target
            int dx = (int)targetX - getCurrentX();
            int dy = (int)targetY - getCurrentY();
            
            // If we're already at the target, don't move
            if (dx == 0 && dy == 0) {
                return;
            }
            
            // Try to move in the direction of the target
            int newX = getCurrentX();
            int newY = getCurrentY();
            
            // Move in the direction with the larger difference
            if (Math.abs(dx) > Math.abs(dy)) {
                newX += (int)Math.signum(dx);
            } else {
                newY += (int)Math.signum(dy);
            }
            
            // If the primary move is invalid, try the other direction
            if (!isValidPosition(safari, newX, newY)) {
                if (Math.abs(dx) > Math.abs(dy)) {
                    newX = getCurrentX();
                    newY += (int)Math.signum(dy);
                } else {
                    newY = getCurrentY();
                    newX += (int)Math.signum(dx);
                }
            }
            
            // If we found a valid position, move there
            if (isValidPosition(safari, newX, newY)) {
                setCurrentX(newX);
                setCurrentY(newY);
                animateMovement(newX, newY);
            }
            
            // Only attempt to hunt if we're within hunting range
            if (isInHuntingRange(targetAnimal)) {
                attemptHunt(safari);
            }
        }
    }

    private void wander(Safari safari) {
        // Randomly change direction every few updates
        if (random.nextInt(100) < 5) {
            int newX = getCurrentX() + random.nextInt(3) - 1; // -1, 0, or 1
            int newY = getCurrentY() + random.nextInt(3) - 1; // -1, 0, or 1
            
            // Ensure target is within bounds
            newX = Math.max(0, Math.min(safari.getLandscapes().size() - 1, newX));
            newY = Math.max(0, Math.min(safari.getLandscapes().get(0).size() - 1, newY));
            
            if (isValidPosition(safari, newX, newY)) {
                setCurrentX(newX);
                setCurrentY(newY);
                animateMovement(newX, newY);
            }
        }
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
        int targetX, targetY;
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

        // Move one step towards the target edge
        int dx = targetX - getCurrentX();
        int dy = targetY - getCurrentY();
        
        int newX = getCurrentX();
        int newY = getCurrentY();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            newX += (int)Math.signum(dx);
        } else {
            newY += (int)Math.signum(dy);
        }
        
        if (isValidPosition(safari, newX, newY)) {
            setCurrentX(newX);
            setCurrentY(newY);
            animateMovement(newX, newY);
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


