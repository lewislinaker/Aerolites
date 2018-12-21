package com.teamtwo.aerolites.Entities;

import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Observer;
import com.teamtwo.engine.Physics.RigidBody;
import com.teamtwo.engine.Utilities.Interfaces.EntityRenderable;
import com.teamtwo.engine.Utilities.Interfaces.Typeable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

/**
 * @author Matthew Threlfall
 */
public abstract class Entity implements EntityRenderable, Updateable, Observer, Typeable<Entity.Type> {

    public enum Type {
        Player,
        Asteroid,
        Bullet,
        EnemyBullet,
        StandardAI,
        Swamer,
        Shield,
        Life,
        ShotSpeed,
        SwamerBase,
        Hexaboss(360),
        PascalBoss(90),
        Quadtron(120);

        public final int lives;

        Type(int lives) { this.lives = lives; }
        Type() { lives = 1; }
    }

    /** The physics body which represents the Entity */
    protected RigidBody body;
    /** How far the entity is allowed off screen before it is no longer considered */
    protected Vector2f offScreenAllowance;
    /** The Convex Shape to be used for rendering */
    protected ConvexShape display;


    protected boolean onScreen;
    protected boolean alive;
    protected float maxSpeed;

    protected Entity() {
        onScreen = true;
        alive = true;
        offScreenAllowance = new Vector2f(Vector2i.ZERO);
        maxSpeed = 200;
    }

    @Override
    public void render(RenderWindow renderer) {
        display.setPosition(body.getTransform().getPosition());
        display.setRotation(body.getTransform().getAngle() * MathUtil.RAD_TO_DEG);
        renderer.draw(display);
    }

    @Override
    public void update(float dt) {
        checkOffScreen();
        limitSpeed();
    }

    protected void checkOffScreen() {
        Vector2f pos = body.getTransform().getPosition();

        float x = pos.x;
        x += (pos.x < -offScreenAllowance.x) ? State.WORLD_SIZE.x : 0;
        x -= (pos.x > State.WORLD_SIZE.x + offScreenAllowance.x) ? State.WORLD_SIZE.x : 0;

        float y = pos.y;
        y += (pos.y < -offScreenAllowance.y) ? State.WORLD_SIZE.y : 0;
        y -= (pos.y > State.WORLD_SIZE.y + offScreenAllowance.y) ? State.WORLD_SIZE.y : 0;

        body.setTransform(new Vector2f(x, y), body.getTransform().getAngle());
    }

    protected void limitSpeed() {
        float x = body.getVelocity().x;
        float y = body.getVelocity().y;
        x = MathUtil.clamp(x, -maxSpeed, maxSpeed);
        y = MathUtil.clamp(y, -maxSpeed, maxSpeed);
        body.setVelocity(new Vector2f(x, y));
    }

    @Override
    public abstract void receiveMessage(Message message);

    @Override
    public abstract Type getType();

    public boolean isOnScreen() { return onScreen; }
    public RigidBody getBody() { return body; }
    public float getMaxSpeed() { return maxSpeed; }

    public void setMaxSpeed(float speed){ maxSpeed = speed; }
    public boolean isAlive(){ return alive; }

    public void setOnScreen(boolean onscreen) { onScreen = onscreen; }
}
