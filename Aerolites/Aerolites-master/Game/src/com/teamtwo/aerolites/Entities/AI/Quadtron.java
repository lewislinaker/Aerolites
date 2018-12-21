package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.Bullet;
import com.teamtwo.aerolites.Entities.CollisionMask;
import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.RigidBody;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.audio.SoundSource;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;

import java.util.ArrayList;

/**
 * @author Matthew Threlfall
 */

public class Quadtron extends Entity implements Disposable {

    public enum Faces {
        One,
        Two,
        Three,
        Four,
        All,
        Spin
    }

    private float lives;
    private float totalLives;
    private boolean inPlace;
    private boolean sheildPlaced;
    private RigidBody shield;
    private RigidBody shield2;
    private Faces currentFace;

    private ArrayList<Vector3f> bulletPositions;
    private ArrayList<Bullet> bullets;

    private boolean shooting;
    private float cooldown;
    private float shootTimer;
    private float waitTimer;
    private float fadeout;
    private float angle;
    private float lastHit;
    //TODO make fade out work with lerp

    public Quadtron(World world, int lives) {
        this.lives = lives;
        totalLives = lives;
        onScreen = true;
        inPlace = false;
        sheildPlaced = false;
        shooting = false;
        offScreenAllowance = new Vector2f(170,170);
        angle = MathUtil.PI/4;

        cooldown = 0.7f;
        shootTimer = 0;
        waitTimer = 0;
        fadeout = 100;
        lastHit = 10;

        bullets = new ArrayList<>();
        bulletPositions = new ArrayList<>();

        /** The main boss body*/
        BodyConfig config = new BodyConfig();
        Vector2f verticies[] = new Vector2f[4];
        verticies[0] = new Vector2f(0,0);
        verticies[1] = new Vector2f(360,0);
        verticies[2] = new Vector2f(360,360);
        verticies[3] = new Vector2f(0,360);
        config.shape = new Polygon(verticies);
        currentFace = Faces.One;

        config.position = new Vector2f(State.WORLD_SIZE.x/2, -180);
        config.category = CollisionMask.QUADTRON;
        config.mask = CollisionMask.ALL;
        config.angularVelocity = 0.1205f * MathUtil.PI/2;

        body = world.createBody(config);
        body.setData(this);

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(Color.CYAN);

        BodyConfig configA = new BodyConfig();
        verticies = new Vector2f[4];
        verticies[0] = new Vector2f(0,0);
        verticies[1] = new Vector2f(200,0);
        verticies[2] = new Vector2f(200,200);
        verticies[3] = new Vector2f(0,200);
        configA.shape = new Polygon(verticies);
        configA.mask = CollisionMask.ALL & ~CollisionMask.QUADTRON;

        configA.category = CollisionMask.SHEILD;
        configA.position = new Vector2f(State.WORLD_SIZE.x+180 , -200);
        shield = world.createBody(configA);

        BodyConfig configB = new BodyConfig();
        verticies = new Vector2f[4];
        verticies[0] = new Vector2f(0,0);
        verticies[1] = new Vector2f(200,0);
        verticies[2] = new Vector2f(200,200);
        verticies[3] = new Vector2f(0,200);
        configB.shape = new Polygon(verticies);
        configB.mask = CollisionMask.ALL & ~CollisionMask.QUADTRON;
        configB.category = CollisionMask.SHEILD;
        configB.position = new Vector2f(-180 , -200);
        shield2 = world.createBody(configB);

        shield.setData(this);
        shield2.setData(this);
        shield.registerObserver(this, Message.Type.Collision);
        shield2.registerObserver(this, Message.Type.Collision);
        body.registerObserver(this, Message.Type.Collision);
    }

    @Override
    public void update(float dt) {
        shootTimer += dt;
        waitTimer += dt;
        lastHit += dt;

        if(lastHit < 0.1f) {
            display.setFillColor(Color.WHITE);
        }
        else {
            display.setFillColor(MathUtil.lerpColour(Color.CYAN, Color.RED, 1 - (lives / totalLives)));
        }

        if(lives < 0){
            onScreen = false;
            alive = false;
        }

        if(inPlace) {
            if(cooldown < shootTimer) {
                bulletPositions.clear();
            }
            body.setVelocity(new Vector2f(0,0));
            body.setTransform(new Vector2f(State.WORLD_SIZE.x/2,State.WORLD_SIZE.y/2),angle);
            switch(currentFace) {
                case One:
                    shield.applyForce(new Vector2f(-350000,350000));
                    shield2.applyForce(new Vector2f(350000,-350000));
                    if(cooldown < shootTimer) {
                        addFirePoints(0, shield);
                        addFirePoints(1, shield);
                        addFirePoints(2, shield2);
                        addFirePoints(3, shield2);
                        shooting = true;
                        shootTimer = 0;
                    }
                    if(shield2.getTransform().getPosition().x > State.WORLD_SIZE.x/2) {
                        currentFace = Faces.Two;
                        shield.setVelocity(new Vector2f(0,0));
                        shield2.setVelocity(new Vector2f(0,0));
                        shield.setTransform(new Vector2f(957,941),MathUtil.PI/4);
                        shield2.setTransform(new Vector2f(962,146),MathUtil.PI/4);
                    }
                    break;
                case Two:
                    shield.applyForce(new Vector2f(-350000,-350000));
                    shield2.applyForce(new Vector2f(350000,350000));
                    if(cooldown<shootTimer) {
                        addFirePoints(3, shield);
                        addFirePoints(2, shield);
                        addFirePoints(1, shield);
                        addFirePoints(1, shield2);
                        addFirePoints(2, shield2);
                        addFirePoints(0, shield2);
                        shooting = true;
                        shootTimer = 0;
                    }
                    if(shield2.getTransform().getPosition().y > State.WORLD_SIZE.y/2) {
                        currentFace = Faces.Three;
                        shield.setVelocity(new Vector2f(0,0));
                        shield2.setVelocity(new Vector2f(0,0));
                        shield.setTransform(new Vector2f(561,540),MathUtil.PI/4);
                        shield2.setTransform(new Vector2f(1358,540),MathUtil.PI/4);
                    }
                    break;
                case Three:
                    shield.applyForce(new Vector2f(350000,-350000));
                    shield2.applyForce(new Vector2f(-350000,350000));
                    if(cooldown<shootTimer) {
                        addFirePoints(2, shield2);
                        addFirePoints(3, shield2);
                        addFirePoints(0, shield);
                        addFirePoints(1, shield);
                        shooting = true;
                        shootTimer = 0;
                    }
                    if(shield2.getTransform().getPosition().x < State.WORLD_SIZE.x/2) {
                        currentFace = Faces.Four;
                        shield.setVelocity(new Vector2f(0,0));
                        shield2.setVelocity(new Vector2f(0,0));
                        shield.setTransform(new Vector2f(962,146),MathUtil.PI/4);
                        shield2.setTransform(new Vector2f(957,941),MathUtil.PI/4);
                    }
                    break;
                case Four:
                    shield.applyForce(new Vector2f(350000,350000));
                    shield2.applyForce(new Vector2f(-350000,-350000));
                    if(cooldown<shootTimer) {
                        addFirePoints(0, shield);
                        addFirePoints(1, shield);
                        addFirePoints(2, shield2);
                        addFirePoints(3, shield2);
                        shooting = true;
                        shootTimer = 0;
                    }
                    if(shield2.getTransform().getPosition().y < State.WORLD_SIZE.y/2) {
                        currentFace = Faces.All;
                        shield.setVelocity(new Vector2f(0,0));
                        shield2.setVelocity(new Vector2f(0,0));
                        shield.setTransform(new Vector2f(1358,540),MathUtil.PI/4);
                        shield2.setTransform(new Vector2f(561,540),MathUtil.PI/4);
                        waitTimer = 0;
                    }
                    break;
                case All:
                    if(cooldown<shootTimer) {
                        if(cooldown < shootTimer){
                            addFirePoints(3, shield);
                            addFirePoints(1, shield);
                            addFirePoints(2, shield);
                            addFirePoints(0, shield);
                            addFirePoints(3, shield2);
                            addFirePoints(1, shield2);
                            addFirePoints(2, shield2);
                            addFirePoints(0, shield2);

                            shooting = true;
                            shootTimer = 0;
                        }
                        shootTimer = 0;
                        if(waitTimer > 5) {
                            currentFace = Faces.Spin;
                            shield.setVelocity(new Vector2f(0,0));
                            shield2.setVelocity(new Vector2f(0,0));
                            shield.setTransform(new Vector2f(1358,540),MathUtil.PI/4);
                            shield2.setTransform(new Vector2f(561,540),MathUtil.PI/4);
                            waitTimer = 0;
                            cooldown = 0.3f;
                        }
                    }
                    break;
                case Spin:
                    angle += MathUtil.PI2/4f*dt;
                    if(cooldown < shootTimer) {
                        addFirePoints(0, body);
                        shooting = true;
                        shootTimer = 0;
                    }
                    if(waitTimer > 12) {
                        currentFace = Faces.One;
                        shield.setVelocity(new Vector2f(0,0));
                        shield2.setVelocity(new Vector2f(0,0));
                        shield.setTransform(new Vector2f(1358,540),MathUtil.PI/4);
                        shield2.setTransform(new Vector2f(561,540),MathUtil.PI/4);
                        cooldown = 0.7f;
                        angle = MathUtil.PI/4f;
                    }
                    break;
            }

            if(shooting) {
                for(Vector3f state : bulletPositions) {
                    Bullet bullet = new Bullet(10f, MathUtil.toVector2f(state),
                            Type.EnemyBullet, angle + state.z, body.getWorld());

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
        else if(body.getTransform().getPosition().y<State.WORLD_SIZE.y/2) {
            body.applyForce(new Vector2f(0,10000000));
            fadeout-=35*dt;
            ContentManager.instance.getMusic("PlayMusic").setVolume(fadeout);
        }
        else if(!sheildPlaced){
            body.setVelocity(new Vector2f(0,0));
            body.setTransform(new Vector2f(State.WORLD_SIZE.x/2,State.WORLD_SIZE.y/2),MathUtil.PI/4);
            shield.setTransform(shield.getTransform().getPosition(),MathUtil.PI/4);
            shield.applyForce(new Vector2f(-1000000,1000000));
            shield2.setTransform(shield2.getTransform().getPosition(),MathUtil.PI/4);
            shield2.applyForce(new Vector2f(1000000,1000000));
            sheildPlaced = shield.getTransform().getPosition().y > State.WORLD_SIZE.y/2;
            if(ContentManager.instance.getMusic("Quad").getStatus() == SoundSource.Status.STOPPED) {
                ContentManager.instance.getMusic("Quad").play();
            }
        }
        else {
            body.setVelocity(new Vector2f(0,0));
            body.setTransform(new Vector2f(State.WORLD_SIZE.x / 2,State.WORLD_SIZE.y / 2),MathUtil.PI / 4);
            shield.setVelocity(new Vector2f(0,0));
            shield.setTransform(new Vector2f(shield.getTransform().getPosition().x,State.WORLD_SIZE.y / 2),
                    MathUtil.PI / 4);
            shield2.setVelocity(new Vector2f(0,0));
            shield2.setTransform(new Vector2f(shield2.getTransform().getPosition().x,State.WORLD_SIZE.y / 2),
                    MathUtil.PI / 4);
            inPlace = true;
            ContentManager.instance.getMusic("PlayMusic").stop();
        }
    }

    @Override
    public void render(RenderWindow renderer) {
        super.render(renderer);
        RectangleShape r = new RectangleShape();
        r.setSize(new Vector2f(200,200));
        r.setRotation(45);
        r.setOrigin(new Vector2f(100,100));
        r.setPosition(shield.getTransform().getPosition());
        r.setFillColor(Color.RED);
        renderer.draw(r);
        r.setPosition(shield2.getTransform().getPosition());
        renderer.draw(r);

        for(Vector3f v : bulletPositions) {

            CircleShape s = new CircleShape();
            s.setFillColor(Color.GREEN);
            s.setPosition(MathUtil.toVector2f(v));
            s.setRadius(5);
            renderer.draw(s);
        }

        for(Bullet bullet : bullets) {
            bullet.render(renderer);
        }
    }

    @Override
    public void receiveMessage(Message message) {
        if (message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            boolean damage = cm.getBodyA().getData().getType() == Type.Bullet || cm.getBodyB().getData().getType() == Type.Bullet;
            damage = damage && (cm.getBodyA() != shield && cm.getBodyB() != shield && cm.getBodyA() != shield2 && cm.getBodyB() != shield);
            if(damage) {
                lives--;
                lastHit = 0;
            }
        }
    }



    private void addFirePoints(int face, RigidBody body) {


        Vector2f pointOne = body.getShape().getTransformed()[face];
        Vector2f pointTwo = body.getShape().getTransformed()[(face + 1) % 4];

        Vector2f dir = MathUtil.normalise(Vector2f.sub(pointTwo, pointOne));
        Vector2f nor = new Vector2f(dir.y, -dir.x);

        float offset = (body == shield || body == shield2) ? 45 : 88;

        float faceAngle = face * 90 * MathUtil.DEG_TO_RAD;

        for(int i = 0; i < 5; i++) {
            Vector2f pos = Vector2f.add(pointOne, Vector2f.mul(dir, offset * i));
            pos = Vector2f.add(pos, Vector2f.mul(nor, 25));
            bulletPositions.add(new Vector3f(pos.x, pos.y, faceAngle));
        }
    }

    @Override
    public void dispose() {
        for(Bullet bullet : bullets) {
            body.getWorld().removeBody(bullet.getBody());
        }

        body.getWorld().removeBody(shield);
        body.getWorld().removeBody(shield2);

        bullets.clear();
    }

    @Override
    public Type getType() {
        return Type.Quadtron;
    }

}
