package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.Bullet;
import com.teamtwo.aerolites.Entities.CollisionMask;
import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.Debug.Debug;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;

/**
 * @author Matthew Threlfall
 */
public class StandardAI extends AI implements Disposable {

    private static final Vector2f[] vertices = new Vector2f[] {
            new Vector2f(0, 0),
            new Vector2f(20, 40), new Vector2f(10, 45),
            new Vector2f(-10, 45), new Vector2f(-20, 40),
    };

    private static final float planExecuteTime = 0.1f;
    private static final float forceFromJet = 3000;
    private static final float rotationSpeed = MathUtil.PI * 40;

    private float planTime;
    private ParticleEmitter jet;
    private Entity target;

    private ArrayList<Bullet> bullets;

    private final float shootTime;
    private float shootCooldown;

    public StandardAI(World world) {
        onScreen = true;
        shootCooldown = 0;
        shootTime = 0.6f;
        BodyConfig config = new BodyConfig();

        config.category = CollisionMask.STANDARD_AI;
        config.mask = CollisionMask.ALL & (~CollisionMask.ENEMY_BULLET);

        config.shape = new Polygon(vertices);

        float x, y, angle;
        int screenSide = MathUtil.randomInt(0,4);
        switch(screenSide) {
            case 0:
                x = MathUtil.randomFloat(0, State.WORLD_SIZE.x);
                y = 0;
                angle = MathUtil.PI;
                break;
            case 1:
                x = MathUtil.randomFloat(0, State.WORLD_SIZE.x);
                y = 1080;
                angle = 0;
                break;
            case 2:
                x = 0;
                y = MathUtil.randomFloat(0, State.WORLD_SIZE.y);
                angle = -MathUtil.PI / 2f;
                break;
            case 3:
                x = 1920;
                y = MathUtil.randomFloat(0, State.WORLD_SIZE.y);
                angle = MathUtil.PI / 2f;
                break;
            default:
                Debug.log(Debug.LogLevel.WARNING, "incorrect side placement value (expected 0 - 3 received " + screenSide + ")");
                x = 0;
                y = 0;
                angle = 0;
        }

        config.position = new Vector2f(x,y);

        // Create the body, set its data and register it to receive collision messages
        body = world.createBody(config);
        body.setData(this);
        body.registerObserver(this, Message.Type.Collision);

        body.setTransform(body.getTransform().getPosition(), angle);

        bullets = new ArrayList<>();

        planTime = 0;

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(Color.RED);

        // Create the particle configuration and emitter
        ParticleConfig pConfig = new ParticleConfig();

        pConfig.minAngle = 0;
        pConfig.maxAngle = 0;
        pConfig.speed = 70;
        pConfig.rotationalSpeed = 40;
        pConfig.pointCount = 6;
        pConfig.colours[0] = Color.RED;
        pConfig.colours[1] = Color.YELLOW;
        pConfig.colours[2] = Color.YELLOW;
        pConfig.fadeOut = true;
        pConfig.startSize = 8;
        pConfig.endSize = 4;
        pConfig.minLifetime = 1.5f;
        pConfig.maxLifetime = 3;

        pConfig.position = body.getTransform().getPosition();
        jet = new ParticleEmitter(pConfig, 40f, 400);
    }


    @Override
    public void update(float dt) {
        setShooting(false);
        jet.getConfig().position = body.getTransform().apply(new Vector2f(0, 15));
        jet.setActive(true);

        jet.getConfig().maxAngle = -body.getTransform().getAngle()*MathUtil.RAD_TO_DEG - 60;
        jet.getConfig().minAngle = -body.getTransform().getAngle()*MathUtil.RAD_TO_DEG -120;
        jet.update(dt);
        planTime += dt;
        shootCooldown += dt;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if(bullet.isAlive()) {
                bullet.update(dt);
            }
            else {
                if(!bullets.remove(bullet)) {
                    throw new Error("Error: Failed to remove bullet");
                }

                body.getWorld().removeBody(bullet.getBody());

                i--;
            }
        }

        if(target != null) {
            trackToTarget(target.getBody().getTransform().getPosition(), dt);
            float x = target.getBody().getTransform().getPosition().x;
            float y = target.getBody().getTransform().getPosition().y;
            if(shootCooldown > shootTime) {
                Vector2f pos = body.getShape().getTransformed()[0];

                bullets.add(new Bullet(2, pos, Entity.Type.EnemyBullet,
                        body.getTransform().getAngle(), body.getWorld()));

                shootCooldown = 0;
            }
            float xAI = getBody().getTransform().getPosition().x;
            float yAI = getBody().getTransform().getPosition().y;
            float distanceTo= MathUtil.square(x - xAI) + MathUtil.square(y - yAI);
            if(distanceTo>MathUtil.square(200)){
                Vector2f force = body.getTransform().applyRotation(new Vector2f(0, -forceFromJet * 6));
                body.applyForce(force);
                jet.setActive(true);
            }
            else
                jet.setActive(false);
        }
        else {

            Vector2f force = body.getTransform().applyRotation(new Vector2f(0, -forceFromJet));
            body.applyForce(force);
            body.setAngularVelocity(0);
            jet.setActive(true);
        }
        checkOffScreen();
        super.update(dt);
    }

    private void trackToTarget(Vector2f pos, float dt) {
        float x = body.getTransform().getPosition().x;
        float y = body.getTransform().getPosition().y;

        float degreeBetween =  (float)Math.atan2(pos.y - y, pos.x - x) - body.getTransform().getAngle() + MathUtil.PI/2;
        body.setAngularVelocity(rotationSpeed * dt * MathUtil.normalizeAngle(degreeBetween) / Math.abs(MathUtil.normalizeAngle(degreeBetween)));
    }

    public void findTarget(ArrayList<Entity> entities, Player[] players) {

        if(planTime < planExecuteTime) return;

        planTime = 0;

        float lowestDistance = Float.MAX_VALUE;
        target = null;

        for(Entity entity : entities) {
            if(entity.getType() == Type.Asteroid) {
                Vector2f position = body.getTransform().getPosition();
                Vector2f entityPos = entity.getBody().getTransform().getPosition();

                float distanceTo = MathUtil.lengthSq(Vector2f.sub(entityPos, position));

                if (distanceTo < lowestDistance && distanceTo < MathUtil.square(1200)) {
                    lowestDistance = distanceTo;
                    target = entity;
                }
            }
        }

        for(Player player : players) {
            if(!player.isAlive()) continue;

            Vector2f position = body.getTransform().getPosition();
            Vector2f entityPos = player.getBody().getTransform().getPosition();

            float distanceTo = MathUtil.lengthSq(Vector2f.sub(entityPos, position));

            if (distanceTo < lowestDistance && distanceTo < MathUtil.square(1200)) {
                lowestDistance = distanceTo;
                target = player;
            }
        }
    }

    @Override
    public void render(RenderWindow renderer) {

        jet.render(renderer);

        super.render(renderer);

        for(Bullet bullet : bullets) {
            bullet.render(renderer);
        }
    }

    @Override
    public void receiveMessage(Message message) {
        if (message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            onScreen = cm.getBodyB().getData().getType() == Type.EnemyBullet || cm.getBodyB().getData().getType() == Type.Swamer;
            onScreen |= cm.getBodyA().getData().getType() == Type.EnemyBullet || cm.getBodyA().getData().getType() == Type.Swamer;
            onScreen |= cm.getBodyA().getData().getType() == Type.Player || cm.getBodyB().getData().getType() == Type.Player;
        }
    }

    @Override
    public Type getType() { return Type.StandardAI; }

    @Override
    public void dispose() {
        for(Bullet bullet : bullets) {
            body.getWorld().removeBody(bullet.getBody());
        }

        bullets.clear();
    }
}
