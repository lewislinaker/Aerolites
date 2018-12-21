package com.teamtwo.engine.Physics;

import org.jsfml.system.Vector2f;

/**
 * A class used to initialise a {@link RigidBody}
 */
public class BodyConfig {

    /** The shape used to represent the body */
    public Polygon shape;
    /** The position the body should start at, default = (0, 0) */
    public Vector2f position;

    /** The starting linear velocity of the body, default = (0, 0) */
    public Vector2f velocity;
    /** The starting angular velocity of the body, default = 0 */
    public float angularVelocity;

    /** How bouncy the body should be, default = 0.2 */
    public float restitution;
    /** The density of the body, default = 0.5 */
    public float density;
    /** The mask of bits which can collide with this body */
    public int mask;
    /** The mask of bits which this body can collide with */
    public int category;

    /**
     * Creates a default body configuration
     */
    public BodyConfig() {
        // Positional
        shape = null;
        position = Vector2f.ZERO;

        // Movement
        velocity = Vector2f.ZERO;
        angularVelocity = 0;

        // Material
        restitution = 0.2f;
        density = 0.5f;

        // Other
        mask = 0xFFFF;
        category = 0x0001;
    }

}
