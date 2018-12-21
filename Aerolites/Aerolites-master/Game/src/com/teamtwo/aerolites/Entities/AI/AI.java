package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.engine.Messages.Message;

/**
 * A class to represent the base AI
 * @author Matthew Threlfall
 */
public abstract class AI extends Entity {

    protected boolean shooting;

    /**
     *
     */
    public AI() { shooting = false; }

    /**
     * Whether or not the AI is currently isShooting
     * @return True if the AI is isShooting, otherwise false
     */
    public boolean isShooting() {
        return shooting;
    }

    /**
     * Change whether or not the AI is isShooting
     * @param shooting True for the AI to shoot, otherwise false
     */
    protected void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    /**
     * Used to receive specific types of messages
     * @param message The message sent
     */
    public abstract void receiveMessage(Message message);

    @Override
    public abstract Type getType();
}
