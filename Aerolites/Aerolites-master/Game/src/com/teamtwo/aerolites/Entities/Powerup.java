package com.teamtwo.aerolites.Entities;

import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * @author Tijan Weir
 */
public class Powerup extends Entity {

    private float lifeTime;
    private final float maxLifeTime;
    int[] PowerUpType = new int[3];

    private Type type;

    //PowerUps 0 = shield, 1 = life, 2 = increase shoot speed

    public Powerup(Type type, float lifeTime, Vector2f position, World world) {

        maxLifeTime = lifeTime;
        this.lifeTime = 0;
        onScreen = true;

        this.type = type;

        BodyConfig config = new BodyConfig();

        //Can change the shape here
        Vector2f shape[] = new Vector2f[4];
        shape[0] = new Vector2f(0f, 0);
        shape[1] = new Vector2f(0f, 30);
        shape[2] = new Vector2f(30f, 30);
        shape[3] = new Vector2f(30, 0);

        this.offScreenAllowance = new Vector2f(0, 0);

        config.shape = new Polygon(shape);
        config.position = position;
        config.density = 0.001f;
        config.category = CollisionMask.POWERUP;
        config.mask = CollisionMask.PLAYER;
        config.angularVelocity = MathUtil.randomFloat(-1.2f, 1.2f);

        body = world.createBody(config);
        body.registerObserver(this, Message.Type.Collision);
        body.setData(this);

        display = new ConvexShape(body.getShape().getVertices());

        display.setFillColor(Color.WHITE);
        switch (type) {
            case Shield:
                display.setFillColor(Color.CYAN);
                break;
            case ShotSpeed:
                display.setFillColor(Color.RED);
                break;
            case Life:
                display.setFillColor(Color.GREEN);
                break;
        }

    }

    @Override
    public void receiveMessage(Message message) {
        if(message.getType() == Message.Type.Collision) {
            onScreen = false;
        }
    }

    @java.lang.Override
    public Entity.Type getType() {
        return type;
    }

    public int[] getPowerUpType() {
        return PowerUpType;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lifeTime += dt;
        if (lifeTime > maxLifeTime) {
            onScreen = false;
        }
    }



}
