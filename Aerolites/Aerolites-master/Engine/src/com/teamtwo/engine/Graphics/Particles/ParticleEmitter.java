package com.teamtwo.engine.Graphics.Particles;

import com.teamtwo.engine.Utilities.Debug.Debug;
import com.teamtwo.engine.Utilities.Interfaces.EntityRenderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;

/**
 * A class which will emit a given particles from a given configuration at the rate specified
 * @author James Bulman
 */
public class ParticleEmitter implements Updateable, EntityRenderable {

    /** Whether the emitter is active or not */
    private boolean active;
    /** Whether the emitter is paused or not, the emitter will still render while paused unlike inactive */
    private boolean pause;

    /** The pool of particles which contains the maximum number of active particles */
    private Particle[] particlePool;
    /** The current active particle count */
    private int particleCount;

    /** The rate at which particles are emitted, in particles per second */
    private float emissionRate;
    /** An accumulator used to emit the correct amount of particles */
    private float accumulator;

    /** The configuration to initialise particles with */
    private ParticleConfig config;

    // Debug stuff
    private CircleShape emitter;

    /**
     * Creates a new Particle Emitter
     * @param defaultConfig The configuration to make particles with
     * @param emissionRate The emission rate of the emitter, in particles per second
     * @param maxParticles The max particles which can be active at once
     */
    public ParticleEmitter(ParticleConfig defaultConfig, float emissionRate, int maxParticles) {
        // Initialise the particle pool
        particlePool = new Particle[maxParticles];
        for(int i = 0; i < maxParticles; i++) {
            particlePool[i] = new Particle();
        }

        particleCount = 0;

        // Store the config
        config = new ParticleConfig(defaultConfig);

        // Set the emission rate
        this.emissionRate = emissionRate;

        // Reset the accumulator and set as active
        accumulator = 0;
        active = true;
        pause = false;

        // Initialise the debug shape
        emitter = new CircleShape(5f);
        emitter.setOrigin(5f, 5f);
        emitter.setPosition(config.position);
        emitter.setFillColor(Color.RED);
    }

    /**
     * Updates all of the active particles for a single frame
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        if((!active && particleCount == 0) || pause) return;

        // Work out the particles per second
        float rate = 1f / emissionRate;
        accumulator += dt;

        // Add particles until the pool is full or surpass the emission rate
        while (active && particleCount != particlePool.length && accumulator > rate) {
            addParticle();
            accumulator -= rate;
        }

        // Update all of the active particles
        for(int i = 0; i < particleCount; i++) {
            Particle current = particlePool[i];

            current.update(dt);

            // Swap any dead particles out
            if(!current.isAlive()) {
                particlePool[i] = particlePool[particleCount - 1];
                particlePool[particleCount - 1] = current;
                particleCount--;
            }
        }
    }

    /**
     * Renders all of the particles to the screen
     * @param renderer The {@link RenderWindow} to draw the entity to
     */
    public void render(RenderWindow renderer) {
        if(!active && particleCount == 0) return;

        // Draw all of the active particles
        for(int i = 0; i < particleCount; i++) {
            particlePool[i].render(renderer);
        }

        // Draw the debug emitter
        if(Debug.DEBUG) {
            emitter.setPosition(config.position);
            renderer.draw(emitter);
        }
    }

    /**
     * Initialises a new particle and sets it as active within the pool
     */
    private void addParticle() {
        if(particleCount == particlePool.length) return;

        Particle p = particlePool[particleCount];
        p.initialise(config);

        particleCount++;
    }

    /**
     * Sets all of the active particles to dead and resets the particle count to 0
     */
    public void reset() {
        for(int i = 0; i < particleCount; i++) {
            particlePool[i].kill();
        }

        particleCount = 0;
    }

    /**
     * Gets whether or not the emitter is active
     * @return True if the emitter is active, otherwise false
     */
    public boolean isActive() { return active; }

    /**
     * Gets whether the emitter has been paused or not
     * @return True if the emitter is paused, otherwise False
     */
    public boolean isPaused() { return pause; }

    /**
     * Gets the {@link ParticleConfig} used to initialise particles
     * @return The configuration
     */
    public ParticleConfig getConfig() { return config; }

    /**
     * Gets the emission rate of the emitter
     * @return The emission rate
     */
    public float getEmissionRate() { return emissionRate; }

    /**
     * Sets whether or not the emitter is active
     * @param active True for the emitter to be active, False for the emitter to be inactive
     */
    public void setActive(boolean active) {
        if(!active) accumulator = 0;
        this.active = active;
    }

    /**
     * Sets whether or not the emitter is paused, the emitter will still render while paused
     * @param pause True to pause the emitter, False to un-pause it
     */
    public void setPause(boolean pause) {
        if(pause) accumulator = 0;
        this.pause = pause;
    }

    /**
     * Sets the emission rate of the emitter
     * @param emissionRate The new emission rate, in particles per second
     */
    public void setEmissionRate(float emissionRate) { this.emissionRate = emissionRate; }
}
