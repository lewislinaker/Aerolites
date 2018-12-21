package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.CollisionMask;
import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.engine.Graphics.Animation;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * @author Matthew Threlfall
 */
public class Swarmer extends AI {

    private static final float maxForce = 800;

    private static final Vector2f[] vertices = new Vector2f[] {
            new Vector2f(0,0), new Vector2f(30,0), new Vector2f(15,30)
    };

    private ParticleEmitter jet;
    private Entity target;
    private Animation animation;

    public Swarmer(World world, Vector2f pos) {

        BodyConfig config = new BodyConfig();

        config.category = CollisionMask.SWARMER;
        config.mask = CollisionMask.ALL;

        config.angularVelocity = MathUtil.PI2;

        config.position = pos;

        config.restitution = 1;

        config.shape = new Polygon(vertices);
        config.density = 0.01f;

        body = world.createBody(config);
        body.setData(this);
        body.registerObserver(this, Message.Type.Collision);

        maxSpeed = 130f;
        onScreen = true;

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(new Color(255, 140, 0));

        ParticleConfig pConfig = new ParticleConfig();

        pConfig.minAngle = 0;
        pConfig.maxAngle = 0;
        pConfig.speed = 70;
        pConfig.rotationalSpeed = 40;
        pConfig.pointCount = 0;
        pConfig.colours[0] = Color.YELLOW;
        pConfig.fadeOut = false;
        pConfig.startSize = 5;
        pConfig.endSize = 1;
        pConfig.minLifetime = 0.5f;
        pConfig.maxLifetime = 1;

        pConfig.position = body.getTransform().getPosition();
        jet = new ParticleEmitter(pConfig, 40f, 400);
        //animation = new Animation(ContentManager.instance.getTexture("Swarmer"),1,5,0.5f);
        //animation.setScale(30,30);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        jet.update(dt);
        jet.getConfig().position = body.getShape().getTransformed()[0];

        //animation.update(dt);
        //animation.setPosition(body.getTransform().getPosition());
        //animation.setRotation(body.getTransform().getAngle()*MathUtil.RAD_TO_DEG+180);

        if(target != null) {
            Vector2f position = body.getTransform().getPosition();
            Vector2f targetPos = target.getBody().getTransform().getPosition();

            float degreeBetween = (float) Math.atan2(targetPos.y - position.y, targetPos.x - position.x)
                    + MathUtil.PI / 2;

            float xForce = MathUtil.sin(degreeBetween) * maxForce;
            float yForce = MathUtil.cos(degreeBetween) * -maxForce;
            body.applyForce(new Vector2f(xForce, yForce));
        }
        else {
            body.applyForce(new Vector2f(MathUtil.randomInt(-1, 1) * maxForce, MathUtil.randomInt(-1,1) * maxForce));
        }
    }

    public void findTarget(Player[] players) {
        float lowestDistance = Float.MAX_VALUE;
        target = null;

        for(Player player : players) {
            if(!player.isAlive()) continue;

            Vector2f position = body.getTransform().getPosition();
            Vector2f playerPos = player.getBody().getTransform().getPosition();

            float distanceTo = MathUtil.lengthSq(Vector2f.sub(playerPos, position));

            if (distanceTo < lowestDistance) {
                lowestDistance = distanceTo;
                target = player;
            }
        }
    }

    @Override
    public void render(RenderWindow renderer) {
        jet.render(renderer);
        super.render(renderer);
        //animation.render(window);
    }

    @Override
    public void receiveMessage(Message message) {
        if (message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            if (cm.getBodyB().getData().getType() == Type.Bullet || cm.getBodyA().getData().getType() == Type.Bullet) {
                onScreen = false;
            }
        }
    }

    @Override
    public Type getType() { return Type.Swamer; }
}
