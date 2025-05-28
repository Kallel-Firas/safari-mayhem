import View.*;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class Game {
    private Calendar calendar;
    final Calendar StartingDate;

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

        // Setting the difficulty
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




    public static void main(String[] args) {
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScreen.setLocationRelativeTo(null);
        welcomeScreen.setVisible(true);
    }

}