import View.*;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Game {
    private Calendar calendar;
    private int days;
    private int hour;
    final Calendar StartingDate;
    Random random = new Random();

    private final Difficulty difficulty;
    Safari safari;

    // Winning condition tracking
    private int consecutiveGoodMonths = 0;
    private int lastCheckedMonth = -1;

    // Thresholds for winning
    private final int requiredVisitors;
    private final int requiredHerbivores;
    private final int requiredCarnivores;
    private final int requiredCapital;
    private final int monthsToWin;

    public Game(Difficulty difficulty, Safari safari) {
        // Initialize the calendar with today's date
        this.calendar = this.StartingDate = Calendar.getInstance();
        this.days = 0;
        this.hour = 0;

        // Setting the difficulty
        this.difficulty = difficulty;
        this.safari = safari;

        // Configure win condition thresholds based on difficulty
        switch (difficulty) {
            case EASY:
                requiredVisitors = 50;
                requiredHerbivores = 15;
                requiredCarnivores = 10;
                requiredCapital = 10000;
                monthsToWin = 3;
                break;
            case MEDIUM:
                requiredVisitors = 100;
                requiredHerbivores = 25;
                requiredCarnivores = 15;
                requiredCapital = 15000;
                monthsToWin = 6;
                break;
            case HARD:
                requiredVisitors = 150;
                requiredHerbivores = 35;
                requiredCarnivores = 20;
                requiredCapital = 20000;
                monthsToWin = 12;
                break;
            default:
                requiredVisitors = 50;
                requiredHerbivores = 15;
                requiredCarnivores = 10;
                requiredCapital = 10000;
                monthsToWin = 3;
                break;
        }
    }
    private void updateCalendar(int hours, int days) {
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.DAY_OF_YEAR, days);

        // Check if month has changed to update win condition tracking
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth != lastCheckedMonth) {
            lastCheckedMonth = currentMonth;

            // Check if all conditions are met this month
            if (meetAllThresholds()) {
                consecutiveGoodMonths++;
            } else {
                consecutiveGoodMonths = 0; // Reset counter if conditions not met
            }
        }
    }

    public void FastForward(int hours) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));
            this.hour %= 24;
        }
        checkGameStatus();
    }

    public void FastForward(int hours, int days) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));
            this.hour %= 24;
        }
        this.days += days;
        updateCalendar(0, days);
        checkGameStatus();
    }

    public void FastForward(int hours, int days, int weeks) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));
            hour %= 24;
        }
        this.days += days + (7 * weeks);
        updateCalendar(0, days + (7 * weeks));
        checkGameStatus();
    }

    // Check if all thresholds are met for the win condition
    private boolean meetAllThresholds() {
        int visitorCount = safari.getTouristCount();
        int capital = safari.getBalance();

        // Count herbivores and carnivores
        int herbivoreCount = 0;
        int carnivoreCount = 0;

        for (Animal animal : safari.getAnimalList()) {
            if (animal instanceof Elephant || animal instanceof Sheep) {
                herbivoreCount++;
            } else if (animal instanceof Lion || animal instanceof Cheetah) {
                carnivoreCount++;
            }
        }

        return visitorCount >= requiredVisitors &&
                herbivoreCount >= requiredHerbivores &&
                carnivoreCount >= requiredCarnivores &&
                capital >= requiredCapital;
    }

    // Check if the player has won
    public boolean hasWon() {
        return consecutiveGoodMonths >= monthsToWin;
    }

    // Check if the player has lost
    public boolean hasLost() {
        // Bankruptcy check
        if (safari.getBalance() < 0) {
            return true;
        }

        // Animal extinction check
        return safari.getAnimalList().isEmpty();
    }

    public boolean gameOver() {
        return hasWon() || hasLost();
    }

    // Get the reason why the game ended
    public String getGameOverReason() {
        if (hasWon()) {
            return "Congratulations! You've won the game by maintaining a successful safari for " +
                    monthsToWin + " consecutive months!";
        } else if (safari.getBalance() < 0) {
            return "Game Over! You've gone bankrupt.";
        } else if (safari.getAnimalList().isEmpty()) {
            return "Game Over! All of your animals have become extinct.";
        }
        return "Game Over!";
    }

    // Check current game status and handle game over if needed
    public void checkGameStatus() {
        if (gameOver()) {
            String message = getGameOverReason();
            JOptionPane.showMessageDialog(null, message,
                    hasWon() ? "Victory!" : "Game Over",
                    hasWon() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            // Return to main menu
            exitToMainMenu();
        }
    }

    private void exitToMainMenu() {
        // Close any existing game screens
        for (Window window : Window.getWindows()) {
            if (window instanceof GameScreen) {
                window.dispose();
            }
        }

        // Open welcome screen
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScreen.setLocationRelativeTo(null);
        welcomeScreen.setVisible(true);
    }

    private void payRangers() {
        for (Ranger ranger : safari.getRangers()) {
            ranger.paySalary();
            // Deduct salary from player's funds
        }
    }

    private void handleRangerPoacherInteractions() {
        for (Ranger ranger : safari.getRangers()) {
            ranger.protectFromPoachers(safari.getPoachers());
        }
    }

    public void gameloop() {
        int hoursPassed = 0;

        while (!gameOver()) {
            FastForward(1);
            hoursPassed++;

            // Check if a full day has passed
            if (hoursPassed >= 24) {
                hoursPassed = 0;

                if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                    payRangers();
                }

                handleRangerPoacherInteractions();

                for (Animal animal : safari.getAnimalList()) {
                    // Increment age
                    animal.setAge(animal.getAge() + 1);

                    // Increase hunger and thirst levels
                    animal.setHungerMeter(animal.getHungerMeter() + (int) (100 * animal.getHungerChange()));
                    animal.setThirstMeter(animal.getThirstMeter() + (int) (100 * animal.getHungerChange()));
                }
            }
        }
    }

    public static void main(String[] args) {
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScreen.setLocationRelativeTo(null);
        welcomeScreen.setVisible(true);
    }

    // Getters and setters for calendar
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    // Getters and setters for days
    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}