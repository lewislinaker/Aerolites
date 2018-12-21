package com.teamtwo.engine;

import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.Interfaces.Renderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import org.jsfml.graphics.RenderWindow;

/**
 * The base class for a Game instance, the {@link Engine} will use this to update and render your game
 * @author James Bulman
 */
public abstract class Game implements Updateable, Renderable, Disposable {

    /** The Engine instance this Game belongs to */
    protected Engine engine;
    /** The Window used for drawing to */
    protected RenderWindow window;

    /** Default Constructor */
    protected Game() {}

    /**
     * Runs once at the beginning of the game, this should be used for setup
     */
    public abstract void initialise();

    /**
     * Runs once per frame, should be used to update all of your game objects
     * @param dt The amount of time passed since last frame
     */
    public abstract void update(float dt);

    /**
     * Runs once per frame, should be used to draw all of your game objects to the screen
     */
    public abstract void render();

    /**
     * Called when the Window is resized
     * @param width The new width of the Window
     * @param height The new Height of the Window
     */
    public void resize(int width, int height) {}

    /**
     * Runs when the Window loses focus, used to pause the game
     */
    public void pause() {}

    /**
     * Runs when the Window regains focus, used to resume the game
     */
    public void resume() {}

    /**
     * Runs when the game has been finished with, used to destroy disposable objects
     */
    public abstract void dispose();

    /**
     * Gets the Engine instance associated with this game
     * @return The Engine instance
     */
    public final Engine getEngine() { return engine; }

    /**
     * Gets the Window associated with this game
     * @return The Window
     */
    public final RenderWindow getWindow() { return window; }

    /**
     * Sets the Engine instance of the Game<br>
     * @param engine The Engine to associate with the Game
     */
    final void setEngine(Engine engine) {
        this.engine = engine;
        window = engine.getWindow();
    }
}