package com.teamtwo.engine.Utilities.Interfaces;

import org.jsfml.graphics.RenderWindow;

public interface EntityRenderable {

    /**
     * Runs once per frame, use to render entities to the screen
     * @param renderer The {@link RenderWindow} to draw the entity to
     */
    void render(RenderWindow renderer);
}
