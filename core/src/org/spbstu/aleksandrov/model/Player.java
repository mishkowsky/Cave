package org.spbstu.aleksandrov.model;

import org.spbstu.aleksandrov.model.entities.Platform;

public class Player {

    private int highScore = 0;
    private int balance = 0;
    private int currentScore = 0;
    private Platform currentPlatform;

    public Player() {

        //TODO load information from preference

    }

    public void addScore(int delta) {
        currentScore += delta;
        if (currentScore > highScore) {
            highScore = currentScore;
            updateData();
        }
    }

    public void increaseBalance() {
        balance += 1;
        updateData();
    }

    public void updateData() {
       //TODO put information in preference
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public Platform getCurrentPlatform() {
        return currentPlatform;
    }

    public void setCurrentPlatform(Platform currentPlatform) {
        this.currentPlatform = currentPlatform;
    }
}
