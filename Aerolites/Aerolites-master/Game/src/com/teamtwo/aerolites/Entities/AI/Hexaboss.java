package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.Bullet;
import com.teamtwo.aerolites.Entities.CollisionMask;
import com.teamtwo.aerolites.States.Options;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.audio.Music;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;

import java.util.ArrayList;


/**
 * @author Matthew Threlfall
 */
public class Hexaboss extends AI implements Disposable {

    /**
     * The Different attack states the Hexaboss can be in
     */
    private enum AttackPattern {
        /** A sinusoidal wave pattern */
        SpinOne,
        /** Three faces on one half */
        SpinTwo,
        /** Three alternating faces */
        Triforce,
        /** No turning, switching periodically between faces */
        StandOne,
        /** Pause, don't attack */
        Wait
    }

    /**
     * A class which contains all of the attack pattern variables
     */
    private class Attack {
        private float warningTime;
        private float timeBetweenShots;
        private float attackTime;
        private int[] faces;
        private float turnSpeed;

        private Vector3f[] bulletPositions;

        private AttackPattern pattern;

        private Attack(AttackPattern pattern) {
            this.pattern = pattern;

            switch (pattern) {
                case SpinOne:
                    warningTime = 2f;
                    timeBetweenShots = 0.25f;
                    attackTime = 12f;
                    faces = new int[] { 0, 1, 3, 4 };
                    turnSpeed = (MathUtil.PI / 8f);
                    break;
                case SpinTwo:
                    warningTime = 2f;
                    timeBetweenShots = 0.18f;
                    attackTime = 12f;
                    faces = new int[] { 0, 1, 2 };
                    turnSpeed =  (MathUtil.PI / 6f) * (MathUtil.randomInt(0, 2) == 0 ? -1f : 1f);
                    break;
                case StandOne:
                    warningTime = 2f;
                    timeBetweenShots = 0.25f;
                    attackTime = 18f;
                    turnSpeed = 0;
                    faces = new int[] { 2, 3, 5, 0 };
                    break;
                case Triforce:
                    warningTime = 4f;
                    timeBetweenShots = 0.18f;
                    attackTime = 12f;
                    faces = new int[] { 0, 2, 4 };
                    turnSpeed = (MathUtil.PI / 5f);
                    break;
                case Wait:
                    warningTime = 0;
                    timeBetweenShots = 0;
                    attackTime = 1f;
                    faces = new int[0];
                    turnSpeed = -(MathUtil.PI / 8f) * MathUtil.randomInt(-4, 4);
                    break;
            }

            bulletPositions = new Vector3f[faces.length * 9];
        }
    }

    /**
     * The vertices which make up the Hexaboss
     */
    private static final Vector2f[] vertices = new Vector2f[] {
            new Vector2f(-8 * 16, -25 * 9), new Vector2f(8 * 16, -25 * 9),
            new Vector2f(16 * 16, 0), new Vector2f(8 * 16, 25 * 9),
            new Vector2f(-8 * 16, 25 * 9), new Vector2f(-16 * 16, 0)
    };

    private static final float timeToSpawn = 5f;

    private float angle;
    private float cooldown;

    private float lives;
    private float totalLives;

    private ParticleEmitter damage;

    private Attack attack;
    private AttackPattern prevAttack;

    //music stuff
    private boolean inPlace;

    private float timeRunning;
    private float lastHit;
    private float warnTimer;

    private ArrayList<Bullet> bullets;

    public Hexaboss(World world, int lives) {

        this.lives = lives;
        totalLives = lives;

        onScreen = true;
        inPlace = false;

        BodyConfig config = new BodyConfig();

        config.category = CollisionMask.HEXABOSS;
        config.mask = CollisionMask.ALL & (~CollisionMask.ENEMY_BULLET);

        config.shape = new Polygon(vertices);
        config.position = new Vector2f(State.WORLD_SIZE.x / 2, -250);

        body = world.createBody(config);
        body.setData(this);
        body.registerObserver(this, Message.Type.Collision);

        offScreenAllowance = new Vector2f(250, 250);

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(Color.CYAN);

        cooldown = 0;
        lastHit = 0;

        warnTimer = 0;
        timeRunning = 0;

        attack = new Attack(AttackPattern.Wait);
        prevAttack = AttackPattern.Wait;

        bullets = new ArrayList<>();

        ParticleConfig pConfig = new ParticleConfig();

        pConfig.minAngle = 0;
        pConfig.maxAngle = 360;
        pConfig.speed = 100;
        pConfig.rotationalSpeed = 80;
        pConfig.pointCount = 6;
        pConfig.fadeOut = true;
        pConfig.startSize = 20;
        pConfig.endSize = 12;
        pConfig.minLifetime = 1.5f;
        pConfig.maxLifetime = 3;

        pConfig.colours[0] = Color.YELLOW;
        pConfig.colours[1] = Color.RED;

        pConfig.position = body.getTransform().getPosition();

        damage = new ParticleEmitter(pConfig, 300, 600);
    }

    private void updateParticles() {
        damage.getConfig().position = body.getTransform().getPosition();
        float lifeRatio = 1 - (lives / totalLives);
        float invLifeRatio = (totalLives / lives);

        damage.getConfig().endSize = MathUtil.lerp(12, 1, lifeRatio);
        damage.getConfig().speed = MathUtil.clamp(100f * invLifeRatio, 100, 300);
        damage.getConfig().maxLifetime = MathUtil.lerp(3, 1.5f, lifeRatio);
        damage.getConfig().minLifetime = MathUtil.lerp(1.5f, 0.3f, lifeRatio);
        damage.setEmissionRate(300 * invLifeRatio * invLifeRatio);

        damage.getConfig().colours[0] = MathUtil.lerpColour(Color.GREEN, Color.RED, lifeRatio);
        damage.getConfig().colours[1] = MathUtil.lerpColour(Color.CYAN, Color.RED, lifeRatio);
    }

    @Override
    public void update(float dt) {
        cooldown += dt;
        lastHit += dt;

        damage.update(dt);
        updateParticles();

        if(lastHit > 0.03f) {
            display.setFillColor(MathUtil.lerpColour(Color.CYAN, Color.RED, 1 - (lives / totalLives)));
        }

        if(lives < 0) {
            onScreen = false;
            alive = false;
        }

        if(body.getTransform().getPosition().y >= State.WORLD_SIZE.y / 2f) {
            if(!inPlace) {
                inPlace = true;
                Music hexagon = ContentManager.instance.getMusic("Hexagon");
                hexagon.play();
                hexagon.setVolume(Options.MUSIC_VOLUME);
                hexagon.setLoop(true);
                ContentManager.instance.getMusic("PlayMusic").stop();
            }

            body.setVelocity(Vector2f.ZERO);
            body.setTransform(new Vector2f(State.WORLD_SIZE.x / 2f, State.WORLD_SIZE.y / 2f), angle);

            pickPattern(dt);
            attack(dt);
        }
        else {
            body.applyForce(new Vector2f(0, 5000000));
            float fadeout = MathUtil.lerp(Options.MUSIC_VOLUME, 0, (cooldown / timeToSpawn));
            ContentManager.instance.getMusic("PlayMusic").setVolume(fadeout);
        }

        if(shooting) {
            float bodyAngle = body.getTransform().getAngle();
            for(Vector3f state : attack.bulletPositions) {
                Bullet bullet = new Bullet(10f, MathUtil.toVector2f(state), Type.EnemyBullet,
                        bodyAngle + state.z, body.getWorld());

                bullet.setMaxSpeed(250);
                bullets.add(bullet);
            }
            shooting = false;
        }

        for(int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if(bullet.isOnScreen()) {
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
    }

    private void pickPattern(float dt) {
        timeRunning += dt;

        if(timeRunning > attack.attackTime + attack.warningTime) {

            timeRunning = 0;

            if(attack.pattern != AttackPattern.Wait) {
                prevAttack = attack.pattern;
                float warn = attack.warningTime;
                attack = new Attack(AttackPattern.Wait);
                attack.warningTime = warn;
            }
            else {
                warnTimer = 0;
                int pattern = MathUtil.randomInt(0, 4);
                while (pattern == prevAttack.ordinal()) {
                    pattern = MathUtil.randomInt(0, 4);
                }

                attack = new Attack(AttackPattern.values()[pattern]);
            }
        }
        else if(attack.pattern == AttackPattern.StandOne) {
            if(timeRunning > 12f) {
                attack.faces = new int[] { 0, 1, 3, 4 };
                attack.warningTime = 6f;
            }
            else if(timeRunning > 6f) {
               attack.faces = new int[] { 1, 2, 4, 5 };
               attack.warningTime = 4f;
            }
        }
    }

    private void attack(float dt) {

        if(attack.pattern != AttackPattern.StandOne) warnTimer += dt;

        for (int i = 0; i < attack.faces.length; i++) {

            int face = attack.faces[i];

            Vector2f pointOne = body.getShape().getTransformed()[(face + 1) % 6];
            Vector2f pointTwo = body.getShape().getTransformed()[face];

            Vector2f dir = MathUtil.normalise(Vector2f.sub(pointTwo, pointOne));
            Vector2f nor = new Vector2f(-dir.y, dir.x);

            float faceAngle = face * 60f * MathUtil.DEG_TO_RAD;
            pointOne = Vector2f.add(pointOne, Vector2f.mul(dir, 2f));

            for(int j = 0; j < 9; j++) {
                Vector2f pos = Vector2f.add(pointOne, Vector2f.mul(dir, 31 * j));
                pos = Vector2f.add(pos, Vector2f.mul(nor, 20));

                attack.bulletPositions[(i * 9) + j] = new Vector3f(pos.x, pos.y, faceAngle);
            }
        }

        if(attack.pattern == AttackPattern.StandOne && attack.warningTime > warnTimer) {
            warnTimer += dt;
        }
        else if(cooldown > attack.timeBetweenShots && attack.warningTime < warnTimer) {
            shooting = true;
            cooldown = 0;
        }

        if(attack.pattern == AttackPattern.SpinOne) {
            angle += attack.turnSpeed * dt * MathUtil.sin(timeRunning * 5f) * 2;
        }
        else {
            angle += attack.turnSpeed * dt;
        }
    }

    @Override
    public Type getType() { return Type.Hexaboss; }

    @Override
    public void receiveMessage(Message message) {
        if (message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            boolean hit = cm.getBodyA().getData().getType() == Type.Bullet
                    || cm.getBodyB().getData().getType() == Type.Bullet;

            if(hit) {
                lives--;
                display.setFillColor(Color.WHITE);
                lastHit = 0;
            }
        }
    }

    @Override
    public void render(RenderWindow renderer) {
        damage.render(renderer);

        super.render(renderer);

        if(attack.warningTime > warnTimer) {
            CircleShape warning;
            for(Vector3f position : attack.bulletPositions) {
                warning = new CircleShape(4f);
                warning.setFillColor(Color.RED);
                warning.setPosition(MathUtil.toVector2f(position));
                renderer.draw(warning);
            }
        }

        for(Bullet bullet : bullets) {
            bullet.render(renderer);
        }
    }

    @Override
    public void dispose() {
        for(Bullet bullet : bullets) {
            body.getWorld().removeBody(bullet.getBody());
        }

        bullets.clear();
    }

    public void setShooting(boolean shooting) { this.shooting = shooting; }

    public boolean isAlive() { return alive; }
}
