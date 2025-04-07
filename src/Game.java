import View.*;
import Model.*;
import javax.swing.*;
import java.util.Calendar;
import java.util.Random;

public class Game {
    // Time fields
    private Calendar calendar;
    private final Calendar startingDate;
    private int days;
    private int hour;

    // Game state
    private final Difficulty difficulty;
    private final Safari safari;
    private boolean isRunning = false;
    private final Random random = new Random();

    // Timers for animation and game logic
    private Timer animationTimer;
    private Timer logicTimer;

    // Observer pattern - can be used with any rendering class
    private Renderable renderTarget;

    public interface Renderable {
        void repaint();
        void showGameOver();
    }

    public Game(Difficulty difficulty, Safari safari) {
        this.calendar = Calendar.getInstance();
        this.startingDate = (Calendar) calendar.clone();
        this.days = 0;
        this.hour = 0;
        this.difficulty = difficulty;
        this.safari = safari;

        initializeTimers();
    }

    public void setRenderTarget(Renderable renderTarget) {
        this.renderTarget = renderTarget;
    }
    private void initializeTimers() {
        // Animation timer (60 FPS)
        final int FPS = 60;
        final int FRAME_TIME = 1000 / FPS;

        animationTimer = new Timer(FRAME_TIME, e -> {
            updateAnimalAnimations();
            if (renderTarget != null) {
                renderTarget.repaint();
            }
        });

        // Game logic timer (once every second)
        logicTimer = new Timer(1000, e -> {
            fastForward(1); // Advance one hour in-game time
            updateGameLogic();

            if (gameOver()) {
                stopGame();
                if (renderTarget != null) {
                    renderTarget.showGameOver();
                }
            }
        });
    }

    private void updateAnimalAnimations() {
        for (Animal animal : safari.getAnimalList()) {
            if (animal.isAlive() && animal.hasTarget()) {
                smoothMoveTowardsTarget(animal);
            }
        }
    }

    private void smoothMoveTowardsTarget(Animal animal) {
        // Get current position and target
        double currentVisualX = animal.getVisualX();
        double currentVisualY = animal.getVisualY();
        int targetX = animal.getTargetX();
        int targetY = animal.getTargetY();

        // Calculate distance to target
        double dx = targetX - currentVisualX;
        double dy = targetY - currentVisualY;
        double distance = Math.sqrt(dx*dx + dy*dy);

        // If we're close enough to target, snap to it and complete movement
        if (distance < 0.1) {
            animal.setCurrentX(targetX);
            animal.setCurrentY(targetY);
            animal.setVisualX(targetX);
            animal.setVisualY(targetY);
            animal.clearTarget();

            // Check if this is a food/water source and consume
            checkAndConsumeResource(animal, targetX, targetY);
            return;
        }

        // Adjust speed for smoother movement at 60 FPS
        // Lower values = smoother movement
        double speed = 0.02; // Reduced from 0.05 for smoother movement at 60 FPS

        // Calculate movement increment
        double moveX = (dx / distance) * speed;
        double moveY = (dy / distance) * speed;

        // Update visual position for smooth rendering
        animal.setVisualX(currentVisualX + moveX);
        animal.setVisualY(currentVisualY + moveY);
    }

    private void checkAndConsumeResource(Animal animal, int x, int y) {
        // Check if animal reached food or water and consume
        if (animal.getHungerMeter() > 40) {
            if (animal instanceof Herbivorous) {
                Landscape landscape = safari.getLandscapes().get(x).get(y);
                if (landscape instanceof Tree || landscape instanceof Grass || landscape instanceof Bush) {
                    ((Herbivorous)animal).Eat(landscape);
                }
            } else if (animal instanceof Carnivorous) {
                for (Animal prey : safari.getAnimalList()) {
                    if (prey instanceof Herbivorous && prey.isAlive() &&
                            prey.getCurrentX() == x && prey.getCurrentY() == y) {
                        animal.Eat();
                        prey.setHungerMeter(100); // Kill the prey
                        break;
                    }
                }
            }
        }

        if (animal.getThirstMeter() > 40) {
            Landscape landscape = safari.getLandscapes().get(x).get(y);
            if (landscape instanceof Water) {
                animal.Drink();
            }
        }
    }

    private void updateGameLogic() {
        for (Animal animal : safari.getAnimalList()) {
            if (animal.isAlive()) {
                animal.Update();

                // Handle reproduction using your original approach with a switch statement
                if (!animal.hasTarget()) {
                    Animal randomAnimal = safari.getAnimalList().stream()
                            .filter(a -> a.getClass().equals(animal.getClass()) && !a.equals(animal))
                            .findAny()
                            .orElse(null);

                    if (randomAnimal != null && animal.Reproduce(randomAnimal)) {
                        // Use switch statement to create the correct animal type
                        String animalType = animal.getClass().getSimpleName();
                        int newId = animal.getId() * 3;
                        String newName = animal.getName() + animal.getId();
                        boolean isLeader = false;
                        int x = animal.getCurrentX();
                        int y = animal.getCurrentY();

                        Animal newAnimal = null;
                        switch (animalType) {
                            case "Elephant":
                                newAnimal = new Elephant(newId, newName, isLeader, x, y);
                                break;
                            case "Lion":
                                newAnimal = new Lion(newId, newName, isLeader, x, y);
                                break;
                            case "Cheetah":
                                newAnimal = new Cheetah(newId, newName, isLeader, x, y);
                                break;
                            case "Sheep":
                                newAnimal = new Sheep(newId, newName, isLeader, x, y);
                                break;
                        }

                        if (newAnimal != null) {
                            safari.getAnimalList().add(newAnimal);
                        }

                        // Update reproduction state
                        animal.setCanReproduce(false);
                        randomAnimal.setCanReproduce(false);
                        animal.setHungerMeter(animal.getHungerMeter() + 15);
                        randomAnimal.setHungerMeter(randomAnimal.getHungerMeter() + 15);
                    } else {
                        animal.setCanReproduce(true);
                    }
                }

                // Handle hunger if not moving to a target already
                if (animal.getHungerMeter() > 40 && !animal.hasTarget()) {
                    FoodType foodType = (animal instanceof Herbivorous) ? FoodType.LEAF : FoodType.MEAT;
                    int[] foodIndex = safari.search(animal.getCurrentX(), animal.getCurrentY(),
                            animal.getVisionRadius(), foodType);

                    if (foodIndex != null && safari.isPointInsideRadius(
                            animal.getCurrentX(), animal.getCurrentY(),
                            foodIndex[0], foodIndex[1], animal.getVisionRadius())) {
                        animal.setTarget(foodIndex[0], foodIndex[1]);
                    }
                }

                // Handle thirst if not moving to a target already
                if (animal.getThirstMeter() > 40 && !animal.hasTarget()) {
                    int[] waterIndex = safari.search(animal.getCurrentX(), animal.getCurrentY(),
                            animal.getVisionRadius(), FoodType.WATER);

                    if (waterIndex != null && safari.isPointInsideRadius(
                            animal.getCurrentX(), animal.getCurrentY(),
                            waterIndex[0], waterIndex[1], animal.getVisionRadius())) {
                        animal.setTarget(waterIndex[0], waterIndex[1]);
                    }
                }

                // Random movement if no target
                if (!animal.hasTarget() && random.nextDouble() < 0.1) {
                    setRandomTarget(animal);
                }
            }
        }
    }

    private void setRandomTarget(Animal animal) {
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            int randomX = random.nextInt(safari.getLandscapes().size());
            int randomY = random.nextInt(safari.getLandscapes().get(0).size());

            if (safari.pointChecker(randomX, randomY)) {
                animal.setTarget(randomX, randomY);
                break;
            }
            attempts++;
        }
    }

    // Calendar and time management
    private void updateCalendar(int hours, int days) {
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.DAY_OF_YEAR, days);
    }

    public void fastForward(int hours) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (this.hour / 24);
            updateCalendar(this.hour % 24, (this.hour / 24));
            this.hour %= 24;
        } else {
            updateCalendar(hours, 0);
        }
    }

    // Game state management
    public void startGame() {
        if (!isRunning) {
            animationTimer.start();
            logicTimer.start();
            isRunning = true;
        }
    }

    public void stopGame() {
        animationTimer.stop();
        logicTimer.stop();
        isRunning = false;
    }

    public void pauseGame() {
        if (isRunning) {
            animationTimer.stop();
            logicTimer.stop();
            isRunning = false;
        }
    }

    public void resumeGame() {
        if (!isRunning) {
            animationTimer.start();
            logicTimer.start();
            isRunning = true;
        }
    }

    public boolean gameOver() {
        int monthsPassed = calendar.get(Calendar.MONTH) - startingDate.get(Calendar.MONTH)
                + (calendar.get(Calendar.YEAR) - startingDate.get(Calendar.YEAR)) * 12;

        switch (difficulty) {
            case EASY: return monthsPassed >= 3;
            case MEDIUM: return monthsPassed >= 6;
            case HARD: return monthsPassed >= 9;
            default: return false;
        }
    }

    // Getters and setters
    public Calendar getCalendar() {
        return calendar;
    }

    public int getDays() {
        return days;
    }

    public int getHour() {
        return hour;
    }

    public Safari getSafari() {
        return safari;
    }

    public static void main(String[] args) {
        WelcomeScreen welcomeScreen = new WelcomeScreen();
    }
}