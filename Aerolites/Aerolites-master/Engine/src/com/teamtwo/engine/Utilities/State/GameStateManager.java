package com.teamtwo.engine.Utilities.State;

import com.teamtwo.engine.Game;
import com.teamtwo.engine.Utilities.Interfaces.Renderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;

import java.util.Stack;

/**
 * A Class which allows different sections of the game to be loaded without removing others
 * @author James Bulman
 */
public class GameStateManager implements Updateable, Renderable {

    /**
     * The Game used for State construction
     */
    public final Game game;

    // A Stack of States which allowing for level loading etc.
    private Stack<State> states;

    /**
     * Creates a new Game State Manager
     * @param game The Game instance used to get information for the States
     */
    public GameStateManager(Game game) {
        this.game = game;
        states = new Stack<>();
    }

    /**
     * Pushes the given State to the top
     * @param state The State to push to the top
     */
    public void addState(State state) {
        states.push(state);
    }

    /**
     * Removes the active State and then pushes the State given to the top
     * @param state The State to push to the top
     */
    public void setState(State state) {
        popState();
        addState(state);
    }

    /**
     * Removes the active State and disposes it
     */
    public void popState() {
        State s = states.pop();
        if(s != null) s.dispose();
    }

    /**
     * Updates the active State
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) { if(!states.isEmpty()) states.peek().update(dt); }

    /**
     * Renders the active State
     */
    public void render() { if(!states.isEmpty()) states.peek().render(); }
}
