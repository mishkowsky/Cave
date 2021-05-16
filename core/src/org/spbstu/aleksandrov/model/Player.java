package org.spbstu.aleksandrov.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.spbstu.aleksandrov.model.entities.Platform;

public class Player {

    private int highScore;
    private int balance;
    private int currentScore = 0;
    private Platform currentPlatform;
    private final Preferences prefs = Gdx.app.getPreferences("Cave_User_Data");

    public Player() {

        highScore = prefs.getInteger("HighScore", 0);
        balance = prefs.getInteger("Balance", 0);

    }

    public void addScore(int delta) {
        currentScore += delta;
        if (currentScore > highScore) {
            highScore = currentScore;
            prefs.putInteger("HighScore", highScore);
            prefs.flush();
        }
    }

    public void increaseBalance() {
        balance += 1;
        prefs.putInteger("Balance", balance);
        prefs.flush();
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
}
