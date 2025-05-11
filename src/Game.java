import View.*;
import Model.*;
import javax.swing.*;
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

    public Game(Difficulty difficulty, Safari safari) {
        // Initialize the calendar with today's date
        this.calendar = this.StartingDate = Calendar.getInstance();
        this.days = 0;
        //setting the difficulty
        this.difficulty = difficulty;
        this.safari = safari;
    }

    private void updateCalendar(int hours, int days) {
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.DAY_OF_YEAR, days);
    }

    public void FastForward(int hours) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));
            this.hour %= 24;
        }

    }

    public void FastForward(int hours, int days) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));
            this.hour %= 24;
        }
        this.days += days;
        updateCalendar(0, (days));
    }


    public boolean gameOver() {
        int monthsPassed = calendar.get(Calendar.MONTH) - StartingDate.get(Calendar.MONTH)
                + (calendar.get(Calendar.YEAR) - StartingDate.get(Calendar.YEAR)) * 12;

        switch (difficulty) {
            case EASY:
                return monthsPassed >= 3;
            case MEDIUM:
                return monthsPassed >= 6;
            case HARD:
                return monthsPassed >= 9;
            default:
                return false;
        }
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
            Timer timer = new Timer(15000, null);
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

    public void FastForward(int hours, int days, int weeks) {
        this.hour += hours;
        if (this.hour >= 24) {
            this.days += (hours / 24);
            updateCalendar(hours % 24, (hours / 24));

            hour %= 24;
        }
        this.days += days + (7 * weeks);
        updateCalendar(0, days + (7 * weeks));


    }

    public static void main(String[] args) {
        WelcomeScreen welcomeScreen = new WelcomeScreen();

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