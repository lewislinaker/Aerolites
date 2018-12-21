package com.teamtwo.engine.Input.Controllers;

import com.teamtwo.engine.Engine;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Joystick;

/**
 * A class which holds the state of a controller
 */
public class ControllerState {

    /** The state of the buttons on the controller */
    private int buttonState;
    /** The state of the triggers on the controller */
    private float[] triggers;
    /** The state of the thumbsticks on the controller */
    private Vector2f[] thumbsticks;

    /**
     * Creates the state of the given controller
     * @param controller The controller to make the state of
     */
    ControllerState(Controller controller) {
        PlayerNumber player = controller.player;
        buttonState = 0;
        if(controller.isConnected()) {
            for(Button button : Button.values()) {
                switch (button) {
                    case DPad_Up:
                    case DPad_Down:
                    case DPad_Left:
                    case DPad_Right:
                        if(controller.type != Controllers.Type.PS3) {
                            buttonState |= getDPadValue(player, button, controller);
                        }
                        else {
                            if(Joystick.isButtonPressed(player.ordinal(), controller.intDpad[button.ordinal() - 11])) {
                                buttonState |= (1 << button.ordinal());
                            }
                        }
                        break;
                    default:
                        int value = controller.buttons[button.ordinal()];
                        if(value >= 0) {
                            if(Joystick.isButtonPressed(player.ordinal(), value)) {
                                buttonState |= (1 << button.ordinal());
                            }
                        }
                        break;
                }
            }

            //  System.out.println(Integer.toBinaryString(buttonState));

            triggers = new float[2];
            if (controller.type != Controllers.Type.PS3) {
                triggers[0] = Joystick.getAxisPosition(player.ordinal(), controller.triggers[0]);
                triggers[1] = Joystick.getAxisPosition(player.ordinal(), controller.triggers[1]);
            }
            else {
                triggers[0] = Joystick.isButtonPressed(player.ordinal(), controller.intTriggers[0]) ? 100 : -100;
                triggers[1] = Joystick.isButtonPressed(player.ordinal(), controller.intTriggers[1]) ? 100 : -100;
            }

            thumbsticks = new Vector2f[2];

            float x = Joystick.getAxisPosition(player.ordinal(), controller.thumbsticks[0]);
            float y = Joystick.getAxisPosition(player.ordinal(), controller.thumbsticks[1]);
            thumbsticks[0] = new Vector2f(x, y);

            x = Joystick.getAxisPosition(player.ordinal(), controller.thumbsticks[2]);
            y = Joystick.getAxisPosition(player.ordinal(), controller.thumbsticks[3]);
            thumbsticks[1] = new Vector2f(x, y);
        }
        else {
            triggers = new float[] { -100, -100 };
            thumbsticks = new Vector2f[] { Vector2f.ZERO, Vector2f.ZERO };
        }
    }

    /**
     * Converts the Dpad axes into a button state
     * @param player The controller number
     * @param button The button which corresponds to the DPad
     * @param controller The Controller instance which is connected
     * @return If the button is pressed the correctly shifted value, otherwise 0
     */
    private int getDPadValue(PlayerNumber player, Button button, Controller controller) {
        if(!button.toString().contains("DPad")) {
            throw new IllegalArgumentException("Error: Supplied button was not part of the DPad");
        }

        Joystick.Axis[] dpad = controller.dpad;

        int value = 0;

        if(Engine.WINDOWS) {
            switch (button) {
                case DPad_Down:
                case DPad_Left:
                    if(Joystick.getAxisPosition(player.ordinal(), dpad[button.ordinal() - 11]) < -50) {
                        value |= (1 << button.ordinal());
                    }
                    break;
                case DPad_Up:
                case DPad_Right:
                    if(Joystick.getAxisPosition(player.ordinal(), dpad[button.ordinal() - 11]) > 50) {
                        value |= (1 << button.ordinal());
                    }
                    break;
            }
        }
        else if(Engine.LINUX) {
            switch(button) {
                case DPad_Up:
                case DPad_Left:
                    if(Joystick.getAxisPosition(player.ordinal(), dpad[button.ordinal() - 11]) < -10) {
                        value |= (1 << button.ordinal());
                    }
                    break;
                case DPad_Down:
                case DPad_Right:
                    if(Joystick.getAxisPosition(player.ordinal(), dpad[button.ordinal() - 11]) > 10) {
                        value |= (1 << button.ordinal());
                    }
                    break;
            }
        }
        else if(Engine.MAC_OS) {
            // TODO macOS Support
            throw new IllegalStateException("Error: macOS is unsupported!");
        }

        return value;
    }

    /**
     * Checks if a button is pressed
     * @param button The button to check
     * @return True if the button is pressed, otherwise false
     */
    public boolean button(Button button) {
        return (buttonState & (1 << button.ordinal())) != 0;
    }

    /**
     * Gets the value of the specified trigger
     * @param trigger The trigger to get the value of
     * @return The floating point value of the trigger
     */
    public float trigger(Trigger trigger) { return triggers[trigger.ordinal()]; }

    /**
     * Gets the position of the specified thumbstick
     * @param thumbstick The thumbstick to get the position of
     * @return The position as a 2D vector where x is the left/ right and y is the up/ down axis
     */
    public Vector2f thumbstick(Thumbstick thumbstick) { return thumbsticks[thumbstick.ordinal()]; }

}
