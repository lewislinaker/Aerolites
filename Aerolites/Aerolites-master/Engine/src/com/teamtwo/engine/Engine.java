package com.teamtwo.engine;

import com.teamtwo.engine.Input.InputHandler;
import com.teamtwo.engine.Input.InputProcessor;
import com.teamtwo.engine.Launcher.LauncherConfig;
import com.teamtwo.engine.Utilities.Debug.Debug;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.Interfaces.Renderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.JSFML;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shader;
import org.jsfml.internal.JSFMLError;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2i;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * A Class which handles the Engine backend, this will update, draw and handle input
 * @author James Bulman
 */
public class Engine implements Updateable, Renderable, Disposable {

    /** The Name of the operating system */
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    /** Whether or not the game is running on Windows */
    public static final boolean WINDOWS = OS_NAME.contains("windows");
    /** Whether or not the game is running on Linux */
    public static final boolean LINUX = OS_NAME.contains("linux");
    /** Whether or not the game is running on macOS */
    public static final boolean MAC_OS = OS_NAME.contains("mac");

    // The JSFML version which the engine requires
    private static final String JSFML_VERSION = "2.2-J7";

    // The Game instance in use
    private Game game;
    // The constant Game time, in seconds
    private float deltaTime;

    // Whether or not the Engine has been disposed
    private boolean isDisposed;
    // Whether or not the engine should close
    private boolean shouldClose;

    // The main Window of the Engine
    private RenderWindow window;

    // The Keyboard handler the engine is currently using
    private InputProcessor input;

    // The fps of the game
    private int fps;

    /**
     * Creates an Runnable Engine which will run the main Game loop
     * @param game The Game instance used for updating and rendering
     * @param config The Window configuration
     * @see LauncherConfig
     */
    public Engine(Game game, LauncherConfig config) {

        if(!JSFML_VERSION.equals(JSFML.VERSION_STRING)) {
            throw new JSFMLError("Error: the Engine requires JSFML version \'"
                + JSFML_VERSION + "\'. Found \'" + JSFML.VERSION_STRING + "\'");
        }

        // Creates a new Window from the configuration provided
        window = new RenderWindow(new VideoMode(config.width, config.height), config.title, config.style, new ContextSettings(8));
        window.setKeyRepeatEnabled(false);

        // Clamp the FPS Limit as there aren't many screens which support > 144 Hz
        // The input lag frame rate issue seems to have fixed itself, more testing will take place
        config.fpsLimit = (int)MathUtil.clamp(config.fpsLimit, -1, 144);

        // Checks the FPS Limit
        // Works out either to use V-Sync or a custom frame timing
        if(config.fpsLimit <= 0) {
            window.setVerticalSyncEnabled(true);
            int fps = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
            deltaTime = 1.0f / (fps != 0 ? (float)fps : 60f);
        }
        else {
            window.setVerticalSyncEnabled(false);
            window.setFramerateLimit(config.fpsLimit);
            deltaTime = 1.0f / (float)config.fpsLimit;
        }

        if(Debug.DEBUG) printDebugInfo();

        // Sets the Input Handling to a Default Handler
        input = new InputHandler();

        // Move the window to the top left corner if there is not title-bar
        if(config.style == WindowStyle.NONE) {
            window.setPosition(new Vector2i(0, 0));
        }

        // Set content directory
        setContentDir(config.contentRoot);

        // Initialises the Game
        this.game = game;
        game.setEngine(this);
        game.initialise();

        // Shouldn't close and start main loop if instructed to do so
        shouldClose = false;
        fps = 0;
        if(config.autoStart) mainLoop();
    }

    /**
     * Runs a continuous while loop until the window closes<br>
     *     Will call {@link Engine#update(float dt)} and {@link Engine#render()} once per frame and handle input
     */
    public void mainLoop() {

        // Starts a clock to see how much time has passed
        float accumulator = 0;
        Clock clock = new Clock();

        // Begins the Main loop
        while (!shouldClose) {
            // Advances the accumulator
            float elapsed = clock.getElapsedTime().asSeconds();
            accumulator += elapsed;
            clock.restart();

            fps = Math.round(1 / elapsed);

            // Clamps the accumulator
            accumulator = MathUtil.clamp(accumulator, 0, 0.2f);

            // If the accumulator has passed the specified delta time
            // Then update the game
            while (accumulator >= deltaTime) {
                accumulator -= deltaTime;
                update(deltaTime);
            }

            // Render the game
            render();

            // Poll for events
            for (Event event : window.pollEvents()) {
                switch (event.type) {
                    // Window Events
                    case RESIZED:
                        SizeEvent sizeEvent = event.asSizeEvent();
                        game.resize(sizeEvent.size.x, sizeEvent.size.y);
                        break;
                    case GAINED_FOCUS:
                        game.resume();
                        break;
                    case LOST_FOCUS:
                        game.pause();
                        break;
                    case CLOSED:
                        close();
                        break;

                    // Keyboard Events
                    case KEY_PRESSED:
                        input.keyPressed(event.asKeyEvent().key);
                        break;
                    case KEY_RELEASED:
                        input.keyReleased(event.asKeyEvent().key);
                        break;

                    // Mouse Events
                    case MOUSE_BUTTON_PRESSED:
                        MouseButtonEvent mouseButtonEvent = event.asMouseButtonEvent();
                        input.mouseButtonPressed(mouseButtonEvent.button, mouseButtonEvent.position);
                        break;
                    case MOUSE_BUTTON_RELEASED:
                        mouseButtonEvent = event.asMouseButtonEvent();
                        input.mouseButtonReleased(mouseButtonEvent.button, mouseButtonEvent.position);
                        break;
                    case MOUSE_WHEEL_MOVED:
                        input.mouseWheelMoved(event.asMouseWheelEvent().delta);
                        break;
                    case MOUSE_MOVED:
                        input.mouseMoved(event.asMouseEvent().position);
                        break;

                    // Controller events
                    case JOYSTICK_BUTTON_PRESSED:
                        JoystickButtonEvent jsButtonEvent = event.asJoystickButtonEvent();
                        input.controllerButtonPressed(jsButtonEvent.joystickId, jsButtonEvent.button);
                        break;
                    case JOYSTICK_BUTTON_RELEASED:
                        jsButtonEvent = event.asJoystickButtonEvent();
                        input.controllerButtonReleased(jsButtonEvent.joystickId, jsButtonEvent.button);
                        break;
                    case JOYSTICK_MOVED:
                        JoystickMoveEvent jsMoveEvent = event.asJoystickMoveEvent();
                        input.controllerAxisMoved(jsMoveEvent.joystickId,
                                jsMoveEvent.joyAxis, jsMoveEvent.position);
                        break;
                    case JOYSTICK_CONNECTED:
                        input.controllerConnected(event.asJoystickEvent().joystickId);
                        break;
                    case JOYSTICK_DISCONNECTED:
                        input.controllerDisconnected(event.asJoystickEvent().joystickId);
                        break;
                }
            }
        }

        dispose();
    }

    /**
     * Called once per frame, used to update the Game instance
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        if(isDisposed) throw new IllegalStateException("Error: The game instance has been disposed and therefore cannot be updated");
        game.update(dt);
    }

    /**
     * Called once per frame, used to render the Game instance
     */
    public void render() {
        if(isDisposed) throw new IllegalStateException("Error: The game instance has been disposed and therefore cannot be rendered");
        game.render();
        window.display();
    }

    /**
     * Called at the end of execution, used to dispose the Game instance
     */
    public void dispose() {
        isDisposed = true;
        window.setVisible(false);
        game.dispose();
        window.close();
    }

    /**
     * Generates a content directory at the user.dir and sets the user.dir
     * @param folderName The Name of the Root folder to hold all of the content
     */
    private void setContentDir(String folderName) {

        String dir = System.getProperty("user.dir");
        dir += (File.separator + folderName);
        File contentDir = new File(dir);

        if(!contentDir.exists()) {
            boolean success = contentDir.mkdir();
            if(!success)
                throw new UncheckedIOException("Could not created the Root Content Directory at \'"
                    + dir + "\'", new IOException());
        }

        String[] contentDirs = new String[] { "Textures", "Fonts", "Sounds", "Music" };

        for(String cDir : contentDirs) {
            File f = new File(dir + File.separator + cDir);
            if(!f.exists()) {
                boolean success = f.mkdir();
                if(!success)
                    throw new UncheckedIOException("Could not created the Root Content Directory at \'"
                            + dir + "\'", new IOException());
            }
        }

        System.setProperty("user.dir", dir);
    }

    /**
     * Gets the Window created within the Engine
     * @return The window
     */
    public RenderWindow getWindow() { return window; }

    /**
     * Gets the current {@link InputProcessor} which is being used by the Engine
     * @return The Input Handler in use
     */
    public InputProcessor  getInputHandler() { return input; }

    /**
     * Changes the {@link InputProcessor} to the one specified
     * @param processor The Input Handler to change to
     */
    public void setInputHandler(InputProcessor processor) { this.input = processor; }

    /** Sets the engine to close */
    public void close() { shouldClose = true; }

    /** Prints out some system information */
    private void printDebugInfo() {
        Debug.log("[Debug Information]");
        Debug.log("Operating System: " + System.getProperty("os.name"));
        Debug.log("OpenGL Version: " + window.getSettings().majorVersion
                + "." + window.getSettings().minorVersion);

        if(Shader.isAvailable())
            Debug.log(Debug.LogLevel.DEBUG, "Shaders Available");
        else
            Debug.log(Debug.LogLevel.WARNING, "Shaders Unavailable");

        int availScreens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
        Debug.log("[Screen Information - Available: " + availScreens + "]");
        for(int i = 0; i < availScreens; i++) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[i];
            Debug.log("Screen: " + gd.getIDstring());
            Debug.log(" - Resolution: " + gd.getDisplayMode().getWidth() + "x" + gd.getDisplayMode().getHeight());
            int refreshRate = gd.getDisplayMode().getRefreshRate();
            Debug.log(" - Refresh Rate: " + (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN ? "Undetermined" : refreshRate));
            int bitDepth = gd.getDisplayMode().getBitDepth();
            Debug.log(" - Bit Depth: " + (bitDepth == DisplayMode.BIT_DEPTH_MULTI ? "Multiple Bit Depths Supported" : bitDepth));
            int vram = gd.getAvailableAcceleratedMemory();
            Debug.log(" - Available VRAM: "  + (vram < 0 ? "Undetermined" : vram + " MB"));
        }

        Debug.log("Window Settings: " + window.getSize().x + "x" + window.getSize().y + "_" + Math.round(1 / deltaTime));

        System.out.println();
    }

    /**
     * Gets the current framerate that the Engine is running at
     * @return The framerate of the engine
     */
    public int getFramerate() { return fps; }
}
