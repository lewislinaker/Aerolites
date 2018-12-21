package com.teamtwo.aerolites;

import com.teamtwo.aerolites.States.MainMenu;
import com.teamtwo.engine.Game;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import org.jsfml.window.Keyboard;

public class Aerolites extends Game {

    /** A manager for all of the game states */
    private GameStateManager stateManager;

    /** This method is called once before the game begins running */
    public void initialise() {
        stateManager = new GameStateManager(this);
        ContentManager.instance.loadFont("Ubuntu", "Ubuntu.ttf");
        stateManager.addState(new MainMenu(stateManager));
    }

    /**
     * This is called once per frame, used to perform any updates required
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        if(Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && Keyboard.isKeyPressed(Keyboard.Key.LSHIFT))
            engine.close();
        stateManager.update(dt);
    }

    /** This is also called once per frame, used to draw anything needed */
    public void render() {
        window.clear();
        stateManager.render();
    }

    /** This is called at the end of execution to destroy any unwanted objects */
    public void dispose() {}
}
