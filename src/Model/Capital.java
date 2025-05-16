package Model;

import java.io.Serializable;

public class Capital implements Serializable {
    private int balance;
    private int dailyRevenue;
    private int touristCount;
    private static final int DAILY_CAPITAL = 50000;
    private static final int TOURIST_PER_ANIMAL = 5;
    private static final int POACHER_BONUS = 50;

    public Capital() {
        this.balance = 1000; // Starting balance
        this.dailyRevenue = 0;
        this.touristCount = 0;
    }

    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    public void purchaseItem(int amount) {
        if (canAfford(amount)) {
            balance -= amount;
        }
    }

    public void addDailyCapital() {
        balance += DAILY_CAPITAL;
    }

    public void payRangerSalary(int salary) {
        balance -= salary;
    }

    public void addPoacherBonus() {
        balance += POACHER_BONUS;
    }

    public void updateTouristCount(int animalCount) {
        touristCount = animalCount / TOURIST_PER_ANIMAL;
    }

    public int getBalance() {
        return balance;
    }

    public int getTouristCount() {
        return touristCount;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}