package com.teamtwo.aerolites;

import com.teamtwo.engine.Input.InputProcessor;
import com.teamtwo.engine.Utilities.Debug.Debug;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Joystick;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

public class ExampleInput implements InputProcessor {

    public Vector2f position;
    public Keyboard.Key lastKeyPressed;
    public Keyboard.Key lastKeyReleased;

    private Joystick.Axis prevAxis;

    public boolean mouseDown;

    public ExampleInput() {
        position = new Vector2f(0, 0);
        lastKeyPressed = null;
        lastKeyReleased = null;
        prevAxis = null;
        mouseDown = false;
    }

    public void keyPressed(Keyboard.Key key) { lastKeyPressed = key; }

    public void keyReleased(Keyboard.Key key) { lastKeyReleased = key; }

    public void mouseButtonPressed(Mouse.Button button, Vector2i position) { mouseDown = true; }

    public void mouseButtonReleased(Mouse.Button button, Vector2i position) { mouseDown = false; }

    public void mouseWheelMoved(int amount) {}

    public void mouseMoved(Vector2i position) { this.position = new Vector2f(position); }

    public void controllerConnected(int id) {
        Joystick.Identification name = Joystick.getIdentification(id);

        System.out.println("[Controller " + id + " Connected]");
        System.out.println(" -- Name: " + name.name);
        System.out.printf(" -- Vendor ID: 0x%04x\n", name.vendorId);
        System.out.printf(" -- Product ID: 0x%04x\n", name.productId);
        System.out.println(" -- Total UIButton Count: " + Joystick.getButtonCount(id));
    }

    public void controllerDisconnected(int id) {
        System.out.println("Controller Disconnected!");
    }

    public void controllerButtonPressed(int id, int button) {
        System.out.println("Controller " + id + " Pressed UIButton " + button);
    }

    public void controllerButtonReleased(int id, int button) {
       // System.out.println("Controller " + id + " Released UIButton " + button);
    }

    public void controllerAxisMoved(int id, Joystick.Axis axis, float position) {
        if(Math.abs(position) < 50f) return;

        if(axis == null) {
            Debug.log(Debug.LogLevel.WARNING, "Axis is null!");
        }
        else if(axis != prevAxis) {
            System.out.println("Controller " + id + " moved " + axis.toString());
            prevAxis = axis;
        }
    }
}
