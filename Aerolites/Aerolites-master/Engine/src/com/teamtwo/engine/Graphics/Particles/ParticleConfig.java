package com.teamtwo.engine.Graphics.Particles;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

/**
 * A class used to configure a particle
 */
public class ParticleConfig {

    /** The starting position of the particle, default = (0, 0) */
    public Vector2f position;
    /** The speed of the particle, default = 0 */
    public float speed;
    /** The minimum angle to generate between, in degrees, default = 0 */
    public float minAngle;
    /** The maximum angle to generate between, in degrees, default = 0 */
    public float maxAngle;
    /** The rotational speed of the particles, in degrees per second, default = 0 */
    public float rotationalSpeed;

    /** The point count of the display shape, use 0 for circle, default = 0 */
    public int pointCount;
    /** The texture to apply to the particle, will not be used if null, default = null */
    public Texture texture;
    /** Whether the particle will fade out or disappear, default = false */
    public boolean fadeOut;

    /** The colours transition through, up to 8 colours are supported */
    public final Color[] colours;

    /** The beginning size of the particle, default = 5 */
    public float startSize;
    /** The ending size of the particle, default = 5 */
    public float endSize;

    /** The minimum length of a particles life, in seconds, default = 0.1 */
    public float minLifetime;
    /** The maximum length of a particles life, in seconds, default = 1 */
    public float maxLifetime;


    /**
     * Instantiates a default particle configuration
     */
    public ParticleConfig() {
        // Positional
        position = new Vector2f(0, 0);
        speed = 0;
        minAngle = 0;
        maxAngle = 0;
        rotationalSpeed = 0;

        // Display
        pointCount = 0;
        texture = null;
        fadeOut = false;

        // Colour
        colours = new Color[8];
        colours[0] = Color.WHITE;

        // Size
        startSize = 5;
        endSize = 5;

        // Lifetime
        minLifetime = 0.1f;
        maxLifetime = 1;
    }

    /**
     * Constructs a new LauncherConfig, copying the values from the one given
     * @param config The {@link ParticleConfig} to copy values from
     */
    ParticleConfig(ParticleConfig config) {
        // Positional
        position = new Vector2f(config.position.x, config.position.y);
        speed = config.speed;
        minAngle = config.minAngle;
        maxAngle = config.maxAngle;
        rotationalSpeed = config.rotationalSpeed;

        // Display
        pointCount = config.pointCount;
        texture = config.texture;
        fadeOut = config.fadeOut;

        // Colour
        colours = new Color[8];
        for(int i = 0; i < config.colours.length; i++) {
            colours[i] = config.colours[i] == null ? null : new Color(config.colours[i], 255);
        }

        // Size
        startSize = config.startSize;
        endSize = config.endSize;

        // Lifetime
        minLifetime = config.minLifetime;
        maxLifetime = config.maxLifetime;
    }
}
