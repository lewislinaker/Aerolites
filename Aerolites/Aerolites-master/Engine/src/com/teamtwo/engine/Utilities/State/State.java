package com.teamtwo.engine.Utilities.State;

import com.teamtwo.engine.Game;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.Interfaces.Renderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

/**
 * The base for a State to be used within the {@link GameStateManager}
 * @author James Bulman
 */
public abstract class State implements Renderable, Updateable, Disposable {

    /** The Game instance used to get information for this State */
    protected Game game;
    /** The {@link GameStateManager} this State belongs to */
    protected GameStateManager gsm;

    /** The Window used for rendering */
    protected RenderWindow window;

    /** The View used for moving the Camera */
    protected View view;

    /** A Vector2 used for converting mouse coordinates from screen to world */
    protected Vector2i mouse;

    public static final Vector2f WORLD_SIZE = new Vector2f(1920, 1080);


    /**
     * Creates a new State
     * @param gsm The {@link GameStateManager} which this State belongs to
     */
    public State(GameStateManager gsm) {

        // Sets the game to the Game instance in the Game State Manager
        game = gsm.game;
        // Stores a reference to the Game State Manager
        this.gsm = gsm;

        // Gets the Window from the Game
        window = game.getWindow();

        // Creates a new View and applies it
        view = new View(new Vector2f(0, 0), WORLD_SIZE);
        view.move(WORLD_SIZE.x/2, WORLD_SIZE.y/2);


        // Initialises the Vector to 0, 0
        mouse = new Vector2i(0, 0);
        window.setView(view);
    }

    /**
     * Runs once per frame, used to update the entire State
     * @param dt The amount of time passed since last frame
     */
    public abstract void update(float dt);

    /**
     * Runs once per frame, used to render the entire State
     */
    public abstract void render();

    /**
     * Runs once the State is removed from the {@link GameStateManager}, used to delete unused objects
     */
    public abstract void dispose();

}
