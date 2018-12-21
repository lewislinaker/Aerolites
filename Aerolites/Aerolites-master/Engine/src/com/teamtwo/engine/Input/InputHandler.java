package com.teamtwo.engine.Input;

import org.jsfml.system.Vector2i;
import org.jsfml.window.Joystick;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

/**
 * A class which implements methods from {@link InputProcessor} which can be extended from when you do not need all methods from the Input Processor interface
 */
public class InputHandler implements InputProcessor {

    // Keyboard Methods

    /**
     * Called when a key on the keyboard is pressed - no default implementation
     * @param key The {@link Keyboard.Key} which was pressed
     */
    public void keyPressed(Keyboard.Key key) {}

    /**
     * Called when a key on the keyboard is released - no default implementation
     * @param key The {@link Keyboard.Key} which was released
     */
    public void keyReleased(Keyboard.Key key) {}

    // Mouse Methods

    /**
     * Called when a button on the mouse is pressed - no default implementation
     * @param button The {@link Mouse.Button} which was pressed
     * @param position The position of the mouse on the screen when the button was pressed
     */
    public void mouseButtonPressed(Mouse.Button button, Vector2i position) {}

    /**
     * Called when a button on the mouse is released - no default implementation
     * @param button The {@link Mouse.Button} which was released
     * @param position The position of the mouse on the screen when the button was released
     */
    public void mouseButtonReleased(Mouse.Button button, Vector2i position) {}

    /**
     * Called when the mouse wheel is scrolled - no default implementation
     * @param amount The mouse that the scroll wheel was moved by
     */
    public void mouseWheelMoved(int amount) {}

    /**
     * Called when the mouse is moved - no default implementation
     * @param position The new position of the mouse on the screen
     */
    public void mouseMoved(Vector2i position) {}

    // Controller Methods

    /**
     * Called when a controller is connected - no default implementation
     * @param id The numeric ID of the controller connected
     */
    public void controllerConnected(int id) {}

    /**
     * Called when a controller is disconnected - no default implementation
     * @param id The numeric ID of the controller disconnected
     */
    public void controllerDisconnected(int id) {}

    /**
     * Called when a button on the controller is pressed - no default implementation
     * @param id The numeric ID of the controller
     * @param button The button which was pressed
     */
    public void controllerButtonPressed(int id, int button) {}

    /**
     * Called when a button on the controller is released - no default implementation
     * @param id The numeric ID of the controller
     * @param button The button which was released
     */
    public void controllerButtonReleased(int id, int button) {}

    /**
     * Called when an axis on the controller moved - no default implementation
     * @param id The numeric ID of the controller
     * @param axis The {@link Joystick.Axis} which was moved
     * @param position The new position of the axis
     */
    public void controllerAxisMoved(int id, Joystick.Axis axis, float position) {}

}
