package com.teamtwo.aerolites.Entities;

import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.system.Vector2f;

/**
 * @author Matthew Threlfall
 */
public class Bullet extends Entity {

    private static final Vector2f[] vertices = new Vector2f[] {
            new Vector2f(-7.5f, 0), new Vector2f(0f, -5),
            new Vector2f(7.5f, 0), new Vector2f(0, 30)
    };

    // How long the bullet has been alive and how long it can be alive
    private float lifeTime;
    private final float totalLifeTime;

    // The owner of the bullet
    private Type owner;

    // Whether the bullet has hit an asteroid
    private boolean asteroid;
    // Whether the bullet has hit an enemy
    private boolean enemy;

    public Bullet(float lifeTime, Vector2f position, Type owner, float angle, World world) {
        this.owner = owner;
        totalLifeTime = lifeTime;
        this.lifeTime = 0;

        asteroid = false;
        enemy = false;

        BodyConfig config = new BodyConfig();

        config.shape = new Polygon(vertices);

        config.density = 0.001f;

        Color colour = Color.WHITE;
        switch (owner) {
            case Bullet:
                colour = Color.YELLOW;
                config.category = CollisionMask.BULLET;
                config.mask = CollisionMask.AI;
                break;
            case EnemyBullet:
                colour = Color.RED;
                config.category = CollisionMask.ENEMY_BULLET;
                config.mask = CollisionMask.PLAYER;
                break;
        }

        config.mask |= (CollisionMask.ASTEROID | CollisionMask.PASCALBOSS | CollisionMask.QUADTRON);

        body = world.createBody(config);

        body.setVelocity(new Vector2f(0, -350));
        body.rotateVelocity(angle);
        body.setTransform(position,angle);

        body.setData(this);

        body.registerObserver(this, Message.Type.Collision);

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(colour);



        maxSpeed = 350f;
        onScreen = true;
        alive = true;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lifeTime += dt;
        if(lifeTime > totalLifeTime) {
            onScreen = false;
            alive = false;
        }
        limitSpeed();
    }

    @Override
    public void receiveMessage(Message message) {
        if(message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;

            Entity.Type typeA = (Type) cm.getBodyA().getData().getType();
            Entity.Type typeB = (Type) cm.getBodyB().getData().getType();

            alive = false;

            if(owner == Type.EnemyBullet) {
                if(typeA == Type.StandardAI || typeB == Type.StandardAI) {
                    alive = true;
                }
                else {
                    alive = false;
                    onScreen = false;
                }
            }
            else {
                asteroid = typeA == Type.Asteroid || typeB == Type.Asteroid;

                enemy = typeA == Type.StandardAI || typeB == Type.StandardAI;
                enemy |= typeA == Type.Swamer || typeB == Type.Swamer;
                enemy |= typeA == Type.SwamerBase || typeB == Type.SwamerBase;
                enemy |= typeA == Type.Hexaboss || typeB == Type.Hexaboss;
                enemy |= typeA == Type.PascalBoss || typeB == Type.PascalBoss;
                enemy |= typeA == Type.Quadtron || typeB == Type.Quadtron;
            }
        }
    }



    @Override
    protected void checkOffScreen() {
        switch (owner) {
            case Bullet:
                super.checkOffScreen();
                break;
            case EnemyBullet:
                if(this.onScreen) {
                    Vector2f pos = body.getTransform().getPosition();

                    onScreen = pos.x < -offScreenAllowance.x || pos.x > State.WORLD_SIZE.x + offScreenAllowance.x;
                    onScreen |= pos.y < -offScreenAllowance.y || pos.y > State.WORLD_SIZE.y + offScreenAllowance.y;
                    onScreen = !onScreen;
                }
                break;
        }
    }

    public Type getType() { return owner; }

    public boolean isAlive() { return alive; }

    public boolean hasHit() { return enemy || asteroid; }

    public boolean hitAsteroid() {
        return asteroid;
    }

    public boolean hitEnemy() {
        return enemy;
    }


}
