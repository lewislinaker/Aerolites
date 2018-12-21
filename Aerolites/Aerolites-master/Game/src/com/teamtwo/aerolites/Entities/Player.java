package com.teamtwo.aerolites.Entities;

import com.teamtwo.aerolites.Utilities.Score;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Input.Controllers.*;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.CircleShape;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import java.util.ArrayList;

/**
 * A class to represent one player
 * @author Matthew Threlfall
 */
public class Player extends Entity implements Disposable {

    // #### Static Begin ####
    // Various constants which are the same across players

    // The base vertices which make up the player ship shape
    public static final Vector2f[] vertices = new Vector2f[] {
            new Vector2f(0, -15), new Vector2f(15, 30),
            new Vector2f(0, 35), new Vector2f(-15, 30)
    };

    // Constant values for movement
    private static final float rotationSpeed = MathUtil.PI * 1.2f;
    private static final float forceFromJet = 100000f;
    // The delay between shots


    // #### Static End ####

    private float timeBetweenShots = 0.15f;
    private float powerUpTime;

    // Whether or not the Player is controlled via a controller
    private boolean controller;
    // The Player ID & Controller ID if needed
    private PlayerNumber player;
    private PlayerNumber controllerNumber;

    private CircleShape[] shields;

    // The amount of lives the player has left
    private int lives;

    // The particle emitter for the jet engine
    private ParticleEmitter jet;

    private ArrayList<Bullet> bullets;
    private float shootCooldown;

    private Color defaultColour;

    private float immuneTime;
    private boolean hasShield;

    //Scoring
    private Score score;

    public Player(World world, PlayerNumber player) { this(world, player, -1); }

    public Player(World world, PlayerNumber player, int controllerNumber) {
        this.player = player;
        if(controllerNumber >= 0) {
            this.controllerNumber = PlayerNumber.values()[controllerNumber];
        }

        controller = controllerNumber >= 0;

        if(this.controllerNumber == null) controller = false;

        lives = 2;
        alive = true;

        immuneTime = 0;
        hasShield = false;

        score = new Score();

        BodyConfig config = new BodyConfig();

        config.category = CollisionMask.PLAYER;
        config.mask = (CollisionMask.ALL & ~CollisionMask.BULLET);

        config.position = new Vector2f(State.WORLD_SIZE.x / 2, State.WORLD_SIZE.y / 2);
        config.shape = new Polygon(vertices);

        config.restitution = 0.4f;
        config.velocity = new Vector2f(0,0);
        config.angularVelocity = 0;

        config.density = 0.6f;

        body = world.createBody(config);
        body.setData(this);
        body.registerObserver(this, Message.Type.Collision);

        display = new ConvexShape(body.getShape().getVertices());
        display.setTexture(ContentManager.instance.getTexture("Player"));

        offScreenAllowance = new Vector2f(15, 15);

        // Generating the particle configuration for the jet stream
        ParticleConfig pConfig = new ParticleConfig();

        pConfig.minAngle = 0;
        pConfig.maxAngle = 0;
        pConfig.speed = 70;
        pConfig.rotationalSpeed = 40;
        pConfig.pointCount = 3;
        pConfig.fadeOut = false;
        pConfig.startSize = 14;
        pConfig.endSize = 4;
        pConfig.minLifetime = 1.5f;
        pConfig.maxLifetime = 3;

        pConfig.position = body.getTransform().getPosition();

        display.setTexture(ContentManager.instance.getTexture("Player"));

        jet = new ParticleEmitter(pConfig, 40f, 400);

        defaultColour = Color.WHITE;
        setColours();
        display.setFillColor(defaultColour);

        bullets = new ArrayList<>();

        shields = new CircleShape[2];

        shields[0] = new CircleShape(48, 4);
        shields[0].setOrigin(48, 48);
        shields[0].setFillColor(Color.TRANSPARENT);
        shields[0].setOutlineColor(Color.WHITE);
        shields[0].setOutlineThickness(3f);

        shields[1] = new CircleShape(48, 4);
        shields[1].setOrigin(48, 48);
        shields[1].setFillColor(Color.TRANSPARENT);
        shields[1].setOutlineColor(defaultColour);
        shields[1].setOutlineThickness(3f);
    }

    private void setColours() {
        // These can be tweaked but they'll do for now

        switch (player) {
            case One:
                defaultColour = new Color(61, 64, 255);
                jet.getConfig().colours[0] = new Color(62, 162, 255);
                jet.getConfig().colours[1] = new Color(155, 61, 255);
                break;
            case Two:
                defaultColour = new Color(255, 228, 94);
                jet.getConfig().colours[0] = new Color(255, 148, 94);
                jet.getConfig().colours[1] = new Color(201, 255, 94);
                break;
            case Three:
                defaultColour = new Color(123, 255, 94);
                jet.getConfig().colours[0] = new Color(204, 255, 94);
                jet.getConfig().colours[1] = new Color(94, 255, 145);
                break;
            case Four:
                defaultColour = new Color(124, 255, 189);
                jet.getConfig().colours[0] = new Color(124, 255, 124);
                jet.getConfig().colours[1] = new Color(124, 255, 255);
                break;
            case Five:
                defaultColour = new Color(124, 235, 255);
                jet.getConfig().colours[0] = new Color(124, 255, 209);
                jet.getConfig().colours[1] = new Color(124, 170, 255);
                break;
            case Six:
                defaultColour = new Color(244, 75, 66);
                jet.getConfig().colours[0] = new Color(244, 66, 146);
                jet.getConfig().colours[1] = new Color(244, 164, 66);
                break;
            case Seven:
                defaultColour = new Color(204, 86, 255);
                jet.getConfig().colours[0] = new Color(120, 86, 255);
                jet.getConfig().colours[1] = new Color(255, 86, 221);
                break;
            case Eight:
                defaultColour = new Color(255, 107, 210);
                jet.getConfig().colours[0] = new Color(225, 107, 255);
                jet.getConfig().colours[1] = new Color(255, 107, 137);
                break;
        }
        jet.getConfig().colours[2] = defaultColour;
    }

    /**
     * Handles the input for the player
     * @param dt The time since the last frame, in seconds
     */
    private void handleInput(float dt) {

        boolean boost, shouldShoot;
        float rotate = 0;

        // Get whether the player is boosting,
        if(controller) {
            // Store the current controller state
            ControllerState state = Controllers.getState(controllerNumber);

            // Get the button states for boost, shoot and rotate
            boost = state.button(Button.RB);
            shouldShoot = state.button(Button.A);
            rotate = (state.thumbstick(Thumbstick.Left).x / 100f) * rotationSpeed;
        }
        else {
            // Get the keyboard state for boost, shoot and rotate
            boost = Keyboard.isKeyPressed(Keyboard.Key.W);
            shouldShoot = Keyboard.isKeyPressed(Keyboard.Key.SPACE);
            rotate -= Keyboard.isKeyPressed(Keyboard.Key.A) ? rotationSpeed : 0;
            rotate += Keyboard.isKeyPressed(Keyboard.Key.D) ? rotationSpeed : 0;
        }

        // Apply movement based off values yielded above
        if(boost) {
            Vector2f force = body.getTransform().applyRotation(new Vector2f(0, -forceFromJet));
            body.applyForce(force);

            score.incrementTimeBoosting(dt);

            jet.getConfig().position = body.getTransform().apply(new Vector2f(0, 15));

            jet.getConfig().maxAngle = -body.getTransform().getAngle() * MathUtil.RAD_TO_DEG - 60;
            jet.getConfig().minAngle = -body.getTransform().getAngle() * MathUtil.RAD_TO_DEG - 120;
        }
        jet.setActive(boost);
        body.setAngularVelocity(rotate);

        // If the player should shoot check if the can
        if(shouldShoot) {
            if(shootCooldown > timeBetweenShots) {
                shootCooldown = 0;

                Vector2f position = body.getShape().getTransformed()[0];

                Bullet bullet = new Bullet(2, position, Entity.Type.Bullet,
                        body.getTransform().getAngle(), body.getWorld());

                bullets.add(bullet);

                score.bulletFired();

                ContentManager.instance.getSound("Pew").play();
            }
        }

        shields[0].rotate(-65 * dt);
        shields[1].rotate(65 * dt);
    }

    /**
     * Updates the player by one iteration
     * @param dt The time since the last frame
     */
    public void update(float dt) {
        if(!alive) return;

        // Update the particle emitter
        jet.update(dt);

        shootCooldown += dt;
        score.incrementTimeAlive(dt);

        display.setFillColor(defaultColour);

        if(immuneTime > 0) {
            immuneTime -= dt;
            if (!hasShield && MathUtil.round(immuneTime % 0.3f, 1) == 0) {
                display.setFillColor(Color.WHITE);
            }
        }
        else {
            immuneTime = 0;
            hasShield = false;
        }

        if(powerUpTime > 0) {
            powerUpTime -= dt;
        }
        else{
            powerUpTime = 0;
            timeBetweenShots = 0.15f;
        }

        // Update base
        super.update(dt);

        // Handle input
        handleInput(dt);

        for(int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if(bullet.isAlive()) {
                bullet.update(dt);
            }
            else {
                if(!bullet.hasHit()) {
                    score.bulletMissed();
                }
                else {
                    if (bullet.hitEnemy()) {
                        score.enemyKilled();
                    }
                    else if (bullet.hitAsteroid()) {
                        score.asteroidDestroyed();
                    }
                }

                if(!bullets.remove(bullet)) {
                    throw new Error("Error: Failed to remove bullet");
                }

                body.getWorld().removeBody(bullet.getBody());

                i--;
            }
        }
    }

    /**
     * Draws the player on the screen
     * @param renderer The RenderWindow used to draw
     */
    public void render(RenderWindow renderer) {
        if(!alive) return;

        Vector2f pos = body.getTransform().getPosition();

        display.setPosition(pos);
        display.setRotation(body.getTransform().getAngle() * MathUtil.RAD_TO_DEG);

        // Draw the jet and shape
        jet.render(renderer);
        renderer.draw(display);

        for(Bullet bullet : bullets) {
            bullet.render(renderer);
        }


        if(hasShield) {
            for (CircleShape shield : shields) {
                shield.setPosition(pos);
                renderer.draw(shield);
            }
        }
    }

    /**
     * Used to receive messages when subscribed events occur
     * @param message The message which was sent
     */
    public void receiveMessage(Message message) {
        if(message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            Type typeA = (Entity.Type) cm.getBodyA().getData().getType();
            Type typeB = (Entity.Type) cm.getBodyB().getData().getType();

            Type other = typeA == Type.Player ? typeB : typeA;

            boolean hit = other != Type.Shield && other != Type.Life && other != Type.ShotSpeed && other != Type.Player;

            if(hit) {
                if (immuneTime <= 0) {
                    lives--;
                    if (lives < 0) alive = false;
                    immuneTime = 3;
                }
            }


            switch (other) {
                case Shield:
                    immuneTime = 10;
                    hasShield = true;
                    break;
                case Life:
                    lives++;
                    break;
                case ShotSpeed:
                    timeBetweenShots = 0.07f;
                    powerUpTime = 10;
                    break;
            }
        }
    }

    /**
     * Gets the Player type
     * @return {@link Type#Player}
     */
    public Type getType() { return Type.Player; }

    /**
     * Whether or not the player is alive
     * @return True if the player is alive, otherwise false
     */
    public boolean isAlive() { return alive; }

    public boolean isController() { return controller; }

    /**
     * Gets the number of lives the player has left
     * @return The life count
     */
    public int getLives() { return lives; }

    public PlayerNumber getNumber() { return player; }

    public PlayerNumber getControllerNumber() { return controllerNumber; }

    public Color getDefaultColour() { return defaultColour; }
    public Score getScore() { return score; }

    public void setLives(int lives) { this.lives = lives; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setScore(Score score) { this.score = score; }


    @Override
    public void dispose() {
        for(Bullet bullet : bullets) {
            body.getWorld().removeBody(bullet.getBody());
            score.bulletMissed();
        }
        bullets.clear();
    }
}
