package com.teamtwo.engine.Input.Controllers;

import com.teamtwo.engine.Engine;
import org.jsfml.window.Joystick;

/**
 * A class which represents a single mapped controller
 */
class Controller {

    private static final int[] ps3Buttons;
    private static final int[] ps4Buttons;
    private static final int[] xboxButtons;

    private static final int[] ps3Triggers;
    private static final Joystick.Axis[] ps4Triggers;
    private static final Joystick.Axis[] xboxTriggers;

    private static final int[] ps3Dpad;
    private static final Joystick.Axis[] ps4Dpad;
    private static final Joystick.Axis[] xboxDpad;

    private static final Joystick.Axis[] ps3Thumbsticks;
    private static final Joystick.Axis[] ps4Thumbsticks;
    private static final Joystick.Axis[] xboxThumbsticks;

    /* Controller mapping initialisation */
    static {
        if(Engine.WINDOWS) {
            ps4Buttons = new int[] {
                    1, 2, 0, 3,
                    4, 10,
                    5, 11,
                    9, 8, 12
            };

            ps4Triggers = new Joystick.Axis[] { Joystick.Axis.V, Joystick.Axis.U };

            ps4Thumbsticks = new Joystick.Axis[] { Joystick.Axis.X, Joystick.Axis.Y, Joystick.Axis.Z, Joystick.Axis.R };

            ps4Dpad = new Joystick.Axis[] {
                    Joystick.Axis.POV_Y, Joystick.Axis.POV_Y,
                    Joystick.Axis.POV_X, Joystick.Axis.POV_X
            };

            ps3Buttons = null;
            ps3Dpad = null;
            ps3Thumbsticks = null;
            ps3Triggers = null;

            xboxButtons = new int[] {
                    0, 1, 2, 3,
                    4, 8,
                    5, 9,
                    7, 6, 12
            };

            xboxDpad = new Joystick.Axis[] {
                    Joystick.Axis.POV_Y, Joystick.Axis.POV_Y,
                    Joystick.Axis.POV_X, Joystick.Axis.POV_X
            };
            xboxThumbsticks = new Joystick.Axis[] {
                    Joystick.Axis.X, Joystick.Axis.Y,
                    Joystick.Axis.U, Joystick.Axis.R
            };
            xboxTriggers = new Joystick.Axis[] {
                    Joystick.Axis.Z, Joystick.Axis.Z
            };
        }
        else if(Engine.LINUX) {
            ps3Buttons = new int[] {
                    14, 13, 15, 12,
                    10, 1,
                    11, 2,
                    3, 0, 16
            };

            ps4Buttons = new int[] {
                    1, 2, 0, 3,
                    4, 10,
                    5, 11,
                    9, 8, 12
            };

            xboxButtons = new int[] {
                    0, 1, 2, 3,
                    4, 9,
                    5, 10,
                    7, 6, 8
            };

            ps3Triggers = new int[] { 8, 9 };
            ps4Triggers = new Joystick.Axis[] { Joystick.Axis.U, Joystick.Axis.V };
            xboxTriggers = new Joystick.Axis[] { Joystick.Axis.Z, Joystick.Axis.R };

            ps3Dpad = new int[] { 4, 6, 7, 5 };
            ps4Dpad = new Joystick.Axis[] {
                    Joystick.Axis.POV_Y, Joystick.Axis.POV_Y,
                    Joystick.Axis.POV_X, Joystick.Axis.POV_X
            };
            xboxDpad = new Joystick.Axis[] {
                    Joystick.Axis.POV_Y, Joystick.Axis.POV_Y,
                    Joystick.Axis.POV_X, Joystick.Axis.POV_X
            };

            ps3Thumbsticks = new Joystick.Axis[] { Joystick.Axis.X, Joystick.Axis.Y, Joystick.Axis.Z, Joystick.Axis.R };
            ps4Thumbsticks = new Joystick.Axis[] { Joystick.Axis.X, Joystick.Axis.Y, Joystick.Axis.Z, Joystick.Axis.R };
            xboxThumbsticks = new Joystick.Axis[] { Joystick.Axis.X, Joystick.Axis.Y, Joystick.Axis.U, Joystick.Axis.V };
        }
        else if(Engine.MAC_OS) {
            throw new IllegalStateException("Error: Unfortunately macOS is not supported");
        }
        else {
            throw new IllegalStateException("Error: \"" + Engine.OS_NAME + "\" is not supported");
        }
    }

    /** Whether or not the controller is connected */
    private boolean connected;
    /** The Controller Number */
    PlayerNumber player;
    /** The type of controller */
    protected Controllers.Type type;

    /** The numbers which correspond to the matching buttons */
    int[] buttons;

    /** The Axes which correspond to the Dpad */
    Joystick.Axis[] dpad;
    /** The player which correspond to the Dpad for PS3 controllers */
    int[] intDpad;

    /** The axes which correspond to the thumbsticks */
    Joystick.Axis[] thumbsticks;

    /** The axes which correspond to the triggers */
    Joystick.Axis[] triggers;
    /** The numbers which correspond to the triggers for PS3 controllers */
    int[] intTriggers;

    Controller(PlayerNumber player) {
        this.player = player;
        connected = false;
    }

    boolean isConnected() { return connected; }

    void setConnected(boolean connected) {
        if(connected && !this.connected) {
            Joystick.Identification id = Joystick.getIdentification(player.ordinal());
            type = Controllers.Type.value(id.vendorId, id.productId);
            switch (type) {
                case PS3:
                    if(Engine.WINDOWS) {
                        connected = false;
                        type = null;
                    }
                    else {
                        buttons = ps3Buttons;
                        intTriggers = ps3Triggers;
                        thumbsticks = ps3Thumbsticks;
                        intDpad = ps3Dpad;
                    }
                    break;
                case PS4:
                    buttons = ps4Buttons;
                    triggers = ps4Triggers;
                    thumbsticks = ps4Thumbsticks;
                    dpad = ps4Dpad;
                    break;
                case Xbox_360:
                case Xbox_One:
                case Xbox_Elite:
                    buttons = xboxButtons;
                    triggers = xboxTriggers;
                    thumbsticks = xboxThumbsticks;
                    dpad = xboxDpad;
                    break;
                case Unknown:
                    connected = false;
                    break;
            }
        }
        else if(!connected) {
            type = null;
        }

        this.connected = connected;
    }
}
