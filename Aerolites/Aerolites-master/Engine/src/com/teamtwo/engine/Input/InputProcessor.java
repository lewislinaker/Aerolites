package com.teamtwo.engine.Input;

import org.jsfml.system.Vector2i;
import org.jsfml.window.Joystick;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

/**
 * An interface used to handle all types of user input
 * @author James Bulman
 */
public interface InputProcessor {

    // Keyboard Methods

    /**
     * Called when a key on the keyboard is pressed
     * @param key The {@link Keyboard.Key} which was pressed
     */
    void keyPressed(Keyboard.Key key);

    /**
     * Called when a key on the keyboard is released
     * @param key The {@link Keyboard.Key} which was released
     */
    void keyReleased(Keyboard.Key key);

    // Mouse Methods

    /**
     * Called when a button on the mouse is pressed
     * @param button The {@link Mouse.Button} which was pressed
     * @param position The position of the mouse on the screen when the button was pressed
     */
    void mouseButtonPressed(Mouse.Button button, Vector2i position);

    /**
     * Called when a button on the mouse is released
     * @param button The {@link Mouse.Button} which was released
     * @param position The position of the mouse on the screen when the button was released
     */
    void mouseButtonReleased(Mouse.Button button, Vector2i position);

    /**
     * Called when the mouse wheel is scrolled
     * @param amount The mouse that the scroll wheel was moved by
     */
    void mouseWheelMoved(int amount);

    /**
     * Called when the mouse is moved
     * @param position The new position of the mouse on the screen
     */
    void mouseMoved(Vector2i position);

    // Controller Methods

    /**
     * Called when a controller is connected
     * @param id The numeric ID of the controller connected
     */
    void controllerConnected(int id);

    /**
     * Called when a controller is disconnected
     * @param id The numeric ID of the controller disconnected
     */
    void controllerDisconnected(int id);

    /**
     * Called when a button on the controller is pressed
     * @param id The numeric ID of the controller
     * @param button The button which was pressed
     */
    void controllerButtonPressed(int id, int button);

    /**
     * Called when a button on the controller is released
     * @param id The numeric ID of the controller
     * @param button The button which was released
     */
    void controllerButtonReleased(int id, int button);

    /**
     * Called when an axis on the controller moved
     * @param id The numeric ID of the controller
     * @param axis The {@link Joystick.Axis} which was moved
     * @param position The new position of the axis
     */
    void controllerAxisMoved(int id, Joystick.Axis axis, float position);

}
