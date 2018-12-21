package com.teamtwo.engine.Utilities.Interfaces;

/**
 * An interface to show that an object can be updated
 */
public interface Updateable {

    /**
     * Should be run when the object needs to be updated
     * @param dt The amount of time passed since last frame
     */
    void update(float dt);
}
