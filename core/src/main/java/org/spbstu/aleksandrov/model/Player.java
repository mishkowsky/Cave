package org.spbstu.aleksandrov.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.spbstu.aleksandrov.model.entities.Bonus;
import org.spbstu.aleksandrov.model.entities.Platform;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private int highScore;
    private int balance;
    private int currentScore = 0;
    private Platform currentPlatform;
    private final Map<Bonus.Type, Integer> inventory = new HashMap<>();
    private final Map<Bonus.Type, Boolean> currentBonuses = new HashMap<>();
    private final Preferences prefs = Gdx.app.getPreferences("Cave_User_Data");

    public Player() {

        highScore = prefs.getInteger("HighScore", 0);
        balance = prefs.getInteger("Balance", 0);
        for (Bonus.Type type : Bonus.Type.values()) {
            inventory.put(type, prefs.getInteger(type.toString(), 0));
            currentBonuses.put(type, false);
        }

        //changeCurrentBonus(Bonus.Type.FUEL);
    }

    public void addScore(int delta) {
        currentScore += delta;
        if (currentScore > highScore) {
            highScore = currentScore;
            prefs.putInteger("HighScore", highScore);
            prefs.flush();
        }
    }

    public void changeBalance(int delta) {
        balance += delta;
        prefs.putInteger("Balance", balance);
        prefs.flush();
    }

    public void editInventory(Bonus.Type type, int delta) {
        int i = inventory.get(type);
        inventory.put(type, i + delta);
        prefs.putInteger(type.toString(), i + delta);
        prefs.flush();
    }

    public void changeCurrentBonus(Bonus.Type type) {
        boolean update = !currentBonuses.get(type);
        currentBonuses.put(type, update);
    }

    public int getHighScore() {
        return highScore;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public Platform getCurrentPlatform() {
        return currentPlatform;
    }

    public void setCurrentPlatform(Platform currentPlatform) {
        this.currentPlatform = currentPlatform;
    }

    public Map<Bonus.Type, Integer> getInventory() {
        return inventory;
    }

    public Map<Bonus.Type, Boolean> getCurrentBonuses() {
        return currentBonuses;
    }
}
