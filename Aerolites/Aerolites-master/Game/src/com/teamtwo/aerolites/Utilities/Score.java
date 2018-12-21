package com.teamtwo.aerolites.Utilities;

import com.teamtwo.engine.Utilities.MathUtil;

/**
 * A class to hold all of the scoring values
 * @author Matthew Threlfall
 */
public class Score {

    // The players score
    private int score;
    private int totalScore;

    // How many asteroids destroyed
    private int asteroids;
    private int totalAsteroids;

    // How many enemies killed
    private int enemies;
    private int totalEnemies;

    // How many bullets the player has fired
    private int bulletsFired;
    private int totalBulletsFired;

    // How many bullets the player has missed
    private int bulletsMissed;
    private int totalBulletsMissed;

    // How long the player has been alive
    private float timeAlive;
    private float totalTimeAlive;

    // How long the player has been boosting
    private float timeBoosting;
    private float totalTimeBoosting;

    /**
     * Constructs an empty score object
     */
    public Score() {

        score = 0;
        totalScore = 0;

        asteroids = 0;
        totalAsteroids = 0;

        enemies = 0;
        totalEnemies = 0;

        bulletsFired = 0;
        totalBulletsFired = 0;

        bulletsMissed = 0;
        totalBulletsMissed = 0;

        timeAlive = 0;
        totalTimeAlive = 0;

        timeBoosting = 0;
        totalTimeBoosting = 0;

    }

    /**
     * Resets all of the level score parameters
     */
    public void reset() {
        score = 0;

        asteroids = 0;
        enemies = 0;

        bulletsFired = 0;
        bulletsMissed = 0;

        timeAlive = 0;
        timeBoosting = 0;
    }


    /**
     * Starts a new level, updating totals and resetting values
     */
    public void newLevel() {

        roundValues();
        updateTotals();
        reset();
    }

    /**
     * Adds all of the current level values to the totals
     */
    private void updateTotals() {
        totalScore += score;

        totalAsteroids += asteroids;
        totalEnemies += enemies;

        totalBulletsFired += bulletsFired;
        totalBulletsMissed += bulletsMissed;

        totalTimeAlive += timeAlive;
        totalTimeBoosting += totalTimeBoosting;


        System.out.println("Total Score: " + totalScore + " Score: " + score);
    }

    /**
     * Increments the number of asteroids destroyed by one and increases the score
     */
    public void asteroidDestroyed() {
        asteroids++;
        score += 50;
    }

    /**
     * Increments the number of enemies killed by one and increases the score
     */
    public void enemyKilled() {
        enemies++;
        score += 80;
    }

    /**
     * Adds the given amount of time to the current alive time
     * @param dt The amount of time to add
     */
    public void incrementTimeAlive(float dt) {
        timeAlive += dt;
    }

    /**
     * Adds the given amount of time to the current boosting time
     * @param dt The amount of time to add
     */
    public void incrementTimeBoosting(float dt) {
        timeBoosting += dt;
    }

    /**
     * Increments the current number of bullets fired
     */
    public void bulletFired() {
        bulletsFired++;
    }

    /**
     * Increments the current number of bullets missed
     */
    public void bulletMissed() {
        bulletsMissed++;
    }

    /**
     * Rounds the time alive and time boosting to three decimal places
     */
    public void roundValues() {
        timeAlive = MathUtil.round(timeAlive, 3);
        timeBoosting = MathUtil.round(timeBoosting, 3);
    }

    /**
     * Gets the accuracy of the player on the current level
     * @return The accuracy of the current level
     */
    public float getAccuracy() {
        float accuracy = 0;
        if(bulletsFired > 0) {
            accuracy = MathUtil.round(((bulletsFired - bulletsMissed)
                    / (float) bulletsFired) * 100, 2);
        }
        return accuracy;
    }

    /**
     * Gets the total accuracy over all levels played
     * @return The overall accuracy
     */
    public float getTotalAccuracy() {
        float accuracy = 0;
        if(totalBulletsFired > 0) {
            accuracy = MathUtil.round(((totalBulletsFired - totalBulletsMissed)
                    / (float) totalBulletsFired) * 100, 2);
        }
        return accuracy;
    }

    /**
     * Gets the score of the current level
     * @return The score
     */
    public int getScore() { return score; }

    /**
     * Gets the total score over all levels played
     * @return The total score
     */
    public int getTotalScore() { return totalScore; }

    /**
     * Gets the number of asteroids destroyed on the current level
     * @return The number of asteroids destroyed
     */
    public int getAsteroids() { return asteroids; }

    /**
     * Gets the total number of asteroids destroyed over all levels played
     * @return The total number of asteroids destroyed
     */
    public int getTotalAsteroids() { return totalAsteroids; }

    /**
     * Gets the number of enemies killed on the current level
     * @return The number of enemies killed
     */
    public int getEnemies() { return enemies; }

    /**
     * Gets the total number of enemies killed over all levels played
     * @return The total number of enemies played
     */
    public int getTotalEnemies() { return totalEnemies; }

    /**
     * Gets the number of bullets fired on the current level
     * @return The number of bullets fired
     */
    public int getBulletsFired() { return bulletsFired; }

    /**
     * Gets the total number of bullets fired over all levels played
     * @return The total number of bullets fired
     */
    public int getTotalBulletsFired() { return totalBulletsFired; }

    /**
     * Gets the number of bullets missed on the current level
     * @return The number of bullets missed
     */
    public int getBulletsMissed() { return bulletsMissed; }

    /**
     * Gets the total number of bullets missed over all levels played
     * @return The total number of bullets missed
     */
    public int getTotalBulletsMissed() { return totalBulletsMissed; }

    /**
     * Gets the time the player was alive for on the current level
     * @return The time the player was alive
     */
    public float getTimeAlive() { return timeAlive; }

    /**
     * Gets the total time the player was alive for over all levels played
     * @return The total time the player was alive
     */
    public float getTotalTimeAlive() { return totalTimeAlive; }

    /**
     * Gets the time the player spent boosting on the current level
     * @return The time spent boosting
     */
    public float getTimeBoosting() { return timeBoosting; }

    /**
     * Gets the total time the player spent boosting over all levels played
     * @return The total time spent boosting
     */
    public float getTotalTimeBoosting() { return totalTimeBoosting; }
}
