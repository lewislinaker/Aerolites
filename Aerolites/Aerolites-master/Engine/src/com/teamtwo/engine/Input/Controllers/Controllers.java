package com.teamtwo.engine.Input.Controllers;

import org.jsfml.window.Joystick;

/**
 * A class which is used to construct all of the controller instances
 */
public class Controllers {

    public enum Type {
        PS3(0x054c, 0x0268),
        PS4(0x054c, 0x09cc),
        Xbox_360(0x045e, 0x028e),
        Xbox_One(0x045e, 0x02dd | 0x02ff),
        Xbox_Elite(0x045e, 0x02e3),
        Unknown(0, 0);

        private int vendorID;
        private int productID;

        Type(int vendorID, int productID) {
            this.vendorID = vendorID;
            this.productID = productID;
        }

        static Type value(int vendorID, int productID) {
            for(Type type : values()) {
                if((type.productID & productID) == productID) {
                    if((type.vendorID & vendorID) == vendorID) {
                        return type;
                    }
                }
            }
            return Unknown;
        }
    }


    private static final Controllers instance = new Controllers();

    private Controller[] controllers;

    private Controllers() {
        controllers = new Controller[Joystick.JOYSTICK_COUNT];
        for(int i = 0; i < Joystick.JOYSTICK_COUNT; i++) {
            controllers[i] = new Controller(PlayerNumber.values()[i]);
        }
    }

    /**
     * Gets whether or not the specified player is connected
     * @param player The player to test for connection
     * @return True if the controller is connected, otherwise false
     */
    public static boolean isConnected(PlayerNumber player) {
        return instance.controllers[player.ordinal()].isConnected();
    }

    /**
     * Gets the current state of a specified controller
     * @param player The controller number
     * @return The button, trigger and thumbstick state of the controller
     */
    public static ControllerState getState(PlayerNumber player) {
        instance.controllers[player.ordinal()].setConnected(Joystick.isConnected(player.ordinal()));
        return new ControllerState(instance.controllers[player.ordinal()]);
    }
}
