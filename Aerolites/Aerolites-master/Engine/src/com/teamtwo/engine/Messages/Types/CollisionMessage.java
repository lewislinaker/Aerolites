package com.teamtwo.engine.Messages.Types;

import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Physics.RigidBody;
import com.teamtwo.engine.Physics.World;

public class CollisionMessage implements Message {

    private World world;
    private RigidBody bodyA;
    private RigidBody bodyB;

    public CollisionMessage(World world, RigidBody a, RigidBody b) {
        this.world = world;
        bodyA = a;
        bodyB = b;
    }

    /**
     * Collision Type
     * @return {@link Message.Type#Collision}
     */
    public Type getType() { return Type.Collision; }

    public RigidBody getBodyA() { return bodyA; }
    public RigidBody getBodyB() { return bodyB; }
    public World getWorld() { return world; }
}
