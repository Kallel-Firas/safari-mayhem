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
    final Calendar StartingDate  ;
    Random random = new Random();

    final Difficulty difficulty;
    Safari safari;
    public Game(Difficulty difficulty,Safari safari) {
        // Initialize the calendar with today's date
        this.calendar =this.StartingDate= Calendar.getInstance();
        this.days = 0;
        //setting the difficulty
        this.difficulty = difficulty;
        this.safari=safari;
    }
    private void updateCalendar(int hours, int days) {
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.DAY_OF_YEAR, days);
    }
    public void FastForward(int hours){
        this.hour+=hours;
        if (this.hour>=24){
            this.days+=(hours/24);
            updateCalendar(hours%24,(hours/24));
            this.hour%=24;
        }

    }
    public void FastForward(int hours,int days){
        this.hour+=hours;
        if (this.hour>=24){
            this.days+=(hours/24);
            updateCalendar(hours%24,(hours/24));
            this.hour%=24;
        }
        this.days+=days;
        updateCalendar(0,(days));
    }
    public boolean pointChecker(int x,int y){
        List<List<Landscape>> map=safari.getLandscapes();
        List<Landscape> columns=map.get(x);
        if(x>=0 && x<=map.size()-1 && y>=0 && y<=columns.size()-1 && columns.get(y) instanceof Land && ! (columns.get(y) instanceof  Road)){
            return true;
        }
        return false;
    }
    public boolean pathChecker(List<int[]> blockList){
        for(int[] block:blockList){
            if(!pointChecker(block[0],block[1])){
                return false;
            }
        }
        return true;
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
    public void gameloop() {
        int hoursPassed = 0;

        while (!gameOver()) {
            try {
                // Wait for 15 seconds
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Fast forward one hour
            FastForward(1);
            hoursPassed++;

            // Check if a full day has passed
            if (hoursPassed >= 24) {
                hoursPassed = 0;

                // Update each animal's state
                for (Animal animal : safari.getAnimalList()) {
                    // Increment age
                    animal.setAge(animal.getAge() + 1);

                    // Increase hunger and thirst levels
                    animal.setHungerMeter(animal.getHungerMeter() + (int)(100*animal.getHungerChange()));
                    animal.setThirstMeter(animal.getThirstMeter() +  (int)(100*animal.getHungerChange()));

                    // Check if the animal needs to eat
                    if (animal.getHungerMeter() > 65) {
                        if (animal.Search("food")) {
                            animal.Eat();
                        } else {
                            // Move closer to food
                            List<int[]> pathToFood = ShortestPath.findShortestPath(
                                safari.getLandscapes(),
                                new int[]{animal.getCurrentX(), animal.getCurrentY()},
                                new int[]{/* coordinates of the nearest food source */}
                            );
                            if (!pathToFood.isEmpty()) {
                                animal.Move(pathToFood.get(0)[0], pathToFood.get(0)[1], pathToFood);
                            }
                        }
                    }

                    // Move and sleep
                    int randomX= random.nextInt(0,safari.getLandscapes().size()-1);

                    int randomY= random.nextInt(0,safari.getLandscapes().get(randomX).size()-1);
                    List<int[]> movementPath = ShortestPath.findShortestPath(
                            safari.getLandscapes(),
                            new int[]{animal.getCurrentX(), animal.getCurrentY()},
                            new int[]{randomX,randomY}
                    );
                    while(!pathChecker(movementPath)){
                        randomX= random.nextInt(0,safari.getLandscapes().size()-1);

                        randomY= random.nextInt(0,safari.getLandscapes().get(randomX).size()-1);
                        movementPath = ShortestPath.findShortestPath(
                                safari.getLandscapes(),
                                new int[]{animal.getCurrentX(), animal.getCurrentY()},
                                new int[]{randomX,randomY}
                        );
                    }
                    animal.Move(randomX, randomY,movementPath);
                    animal.Sleep();

                    // Check if the animal can reproduce
                    /*
                    if (animal.getAge() >= ) {
                        Animal partner = findPartner(animal);
                        if (partner != null && animal.Reproduce(partner)) {
                            // Handle reproduction logic
                        }
                    }
                    */
                }
            }
        }
    }
    public void FastForward(int hours,int days, int weeks){
        this.hour+=hours;
        if (this.hour>=24){
            this.days+=(hours/24);
            updateCalendar(hours%24,(hours/24));

            hour%=24;
        }
        this.days+=days+(7*weeks);
        updateCalendar(0,days+(7*weeks));


    }
    public static void main(String[] args) {
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        /*
        JFrame frame = new JFrame("Model.Safari Mayhem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        View view = new View();
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        */
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