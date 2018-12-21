package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.Entities.AI.Hexaboss;
import com.teamtwo.aerolites.Entities.AI.PascalBoss;
import com.teamtwo.aerolites.Entities.AI.Quadtron;
import com.teamtwo.aerolites.Entities.AI.StandardAI;
import com.teamtwo.aerolites.Entities.AI.*;
import com.teamtwo.aerolites.Entities.Asteroid;
import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.aerolites.Entities.Powerup;
import com.teamtwo.aerolites.Utilities.InputType;
import com.teamtwo.aerolites.Utilities.LevelConfig;
import com.teamtwo.aerolites.Utilities.LevelOverMessage;
import com.teamtwo.aerolites.Utilities.Score;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Input.Controllers.PlayerNumber;
import com.teamtwo.engine.Messages.Listener;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Observer;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.audio.Music;
import org.jsfml.audio.SoundSource;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.teamtwo.aerolites.Entities.Entity.Type.*;

/**
 * @author Matthew Threlfall
 */
public class PlayState extends State implements Listener {

    private static boolean contentLoaded = false;
    private static int bossIndex = 0;

    private class Timers {
        private float asteroidRate;
        private float asteroid;

        private float swarmerRate;
        private float swarmer;

        private float aiRate;
        private float ai;

        private float bossRate;
        private float boss;

        private void advance(float dt) {
            asteroid += dt;
            swarmer += dt;
            ai += dt;
            boss += dt;
        }

        private boolean spawnAsteroid() { return asteroid >= asteroidRate; }
        private boolean spawnSwarmer() { return swarmer >= swarmerRate; }
        private boolean spawnAI() { return ai >= aiRate; }
        private boolean spawnBoss() { return boss >= bossRate - 6; }
    }

    private boolean onState;

    private RectangleShape background;

    private World world;
    private ArrayList<Entity> entities;

    private Player[] players;

    private Entity[] bosses;
    private boolean bossSpawned;

    // The configuration for the level
    private LevelConfig config;
    private Timers timer;

    private boolean gameOver;

    //asteroid Particles
    private ArrayList<ParticleEmitter> debris;
    private ArrayList<Float> debrisTimer;

    // Messages
    private HashMap<Message.Type, List<Observer>> observers;

    public PlayState(GameStateManager gsm, LevelConfig config) { this(gsm, config, null); }

    /**
     * Creates a new level from the configuration provided
     * @param gsm the game state manager for the entire game
     * @param config The configuration for the level
     */
    public PlayState(GameStateManager gsm, LevelConfig config, Score[] playerScores) {
        super(gsm);

        this.config = config;

        onState = true;
        bossSpawned = false;

        // Load content and then play the level music
        loadContent(config.textured);
        Music bgm = ContentManager.instance.getMusic("PlayMusic");
        bgm.setVolume(Options.MUSIC_VOLUME);
        bgm.play();

        gameOver = false;

        world = new World(Vector2f.ZERO);

        entities = new ArrayList<>();

        int playerCount = 0;
        while (config.players[playerCount] != null) {
            playerCount++;
            if(playerCount == config.players.length) break;
        }

        players = new Player[playerCount];
        int controllerNumber = 0;
        for(int i = 0; i < playerCount; i++) {
            if(config.players[i] == InputType.Controller) {
                players[i] = new Player(world, PlayerNumber.values()[i], controllerNumber++);
            }
            else {
                players[i] = new Player(world, PlayerNumber.values()[i]);
            }
        }

        if(playerScores != null) {
            for(int i = 0; i < players.length; i++) {
                players[i].setScore(playerScores[i]);
            }
        }

        timer = new Timers();

        timer.asteroidRate = config.difficulty.asteroid / (1.8f * playerCount);
        timer.swarmerRate = config.difficulty.swarmer / (float) playerCount;
        timer.aiRate = config.difficulty.ai / (float) playerCount;
        timer.bossRate = config.difficulty.boss;

        background = new RectangleShape(State.WORLD_SIZE);
        background.setPosition(0, 0);
        background.setTexture(ContentManager.instance.getTexture("Space"));

        observers = new HashMap<>();

        debris = new ArrayList<>();
        debrisTimer = new ArrayList<>();
    }

    public void update(float dt) {

        /*
         *
         * Check for Alive players
         *  -- If no alive players push the Level Over state
         * Check difficulty
         *  -- Easy: Check if time has expired, if not spawn entities
         *  -- Medium: Check if time has expired, if not spawn entities
         *  -- Hard: Check if time has reached the boss time, if not spawn entities, otherwise spawn the boss
         * Check the boss spawn
         *  -- If spawned, make sure if it is still alive, if not push the Level Over state
         *  -- If not spawned -> perform Hard Difficulty check
         * Update all entities
         *  -- If the entity is no longer alive, remove its RigidBody and delete it
         *  -- If asteroid destroyed, split and spawn power ups
         * Update bosses if they have been spawned
         * Update the physics world
         *
         */

        int alive = 0;
        for(Player player : players) {
            if(player.isAlive()) {
                alive++;
                break;
            }
        }

        if(alive == 0 && onState) {
            onState = false;

            LevelOverMessage message = new LevelOverMessage(players, false);
            postMessage(message);

            if(bossSpawned) {
                bossIndex--;
            }

            gameOver = false;
            onState = false;
            gsm.setState(new LevelOver(gsm, this, false));
        }

        if(alive == 0) {
            gameOver = true;
        }
        if(timer.spawnBoss()) {
            switch (config.difficulty) {
                case Easy:
                case Medium:
                    gameOver = true;
                    break;
                case Hard:
                    if(timer.boss > timer.bossRate - 6 && timer.boss  < timer.bossRate)
                        bossWipeout();
                    else if(!bossSpawned) {
                        switch (1) {
                            case 0:
                                bosses = new Entity[1];
                                bosses[0] = new Hexaboss(world, Hexaboss.lives * players.length);
                                break;
                            case 1:
                                bosses = new Entity[2];
                                bosses[0] = new PascalBoss(world, PascalBoss.lives * players.length, false);
                                bosses[1] = new PascalBoss(world, PascalBoss.lives * players.length, true);
                                break;
                            case 2:
                                bosses = new Entity[1];
                                bosses[0] = new Quadtron(world, Quadtron.lives * players.length);
                                break;
                            default:
                                System.out.println("Error: Boss index exceeded!");
                                throw new IllegalStateException("BOSS ERROR");
                        }

                        bossSpawned = true;
                        bossIndex++;
                    }
                    break;
            }
        }



        if(gameOver && onState) {
            System.out.println("Level Over!");

            for(Player player : players) {
                world.removeBody(player.getBody());
                player.setAlive(false);
            }

            LevelOverMessage message = new LevelOverMessage(players, true);
            postMessage(message);

            gameOver = false;
            onState = false;
            gsm.setState(new LevelOver(gsm, this, true));
        }

        for(Player player : players) {
            player.update(dt);
            if(!player.isAlive()) {
                world.removeBody(player.getBody());
                player.dispose();
            }
        }

        if(bossSpawned) {
            alive = 0;
            for(Entity boss : bosses) {

                if(!boss.isAlive()) {
                    world.removeBody(boss.getBody());
                    Disposable d = (Disposable) boss;
                    d.dispose();
                }
                else {
                    boss.update(dt);
                    alive++;
                }
            }

            if(alive == 0) {
                gameOver = true;
            }
        }
        timer.advance(dt);
        if(!bossSpawned && timer.boss < timer.bossRate - 6) {

            if (timer.spawnAsteroid()) {
                entities.add(new Asteroid(world));
                timer.asteroidRate = MathUtil.clamp(0.99f * timer.asteroidRate, 0.6f, 8f);
                timer.asteroid = 0;
            }

            if (timer.spawnSwarmer()) {
                entities.add(new SwarmerBase(world));
                timer.swarmerRate = MathUtil.clamp(0.99f * timer.swarmerRate, 4f, 33f);
                timer.swarmer = 0;
            }

            if (timer.spawnAI()) {
                entities.add(new StandardAI(world));
                timer.ai = 0;
            }
        }

        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if(!entity.isOnScreen()) {
                ParticleConfig config = new ParticleConfig();
                config.endSize = 1;
                config.startSize = MathUtil.randomInt(7, 12);
                config.maxAngle = 0;
                config.minAngle = 360;
                config.maxLifetime = 3;
                config.minLifetime = 3f;
                config.pointCount = 3;
                config.position = entity.getBody().getTransform().getPosition();
                config.rotationalSpeed = MathUtil.randomInt(-20,20);
                config.speed = MathUtil.randomInt(40, 40);
                config.colours[0] = new Color(255,153,0);
                config.colours[1] = Color.RED;
                config.colours[2] = Color.RED;
                debris.add(new ParticleEmitter(config,10000f, MathUtil.randomInt(4,20)));
                debrisTimer.add(0f);
                world.removeBody(entity.getBody());
                if(entity instanceof Disposable) {
                    Disposable d = (Disposable) entity;
                    d.dispose();
                }


                entities.remove(entity);
                i--;
            }
            else {
                entity.update(dt);

                switch (entity.getType()) {
                    case Asteroid:
                        updateAsteroid((Asteroid) entity);
                        break;
                    case SwamerBase:
                        SwarmerBase base = (SwarmerBase) entity;
                        base.findTarget(players);

                        if(base.shouldSplit()) {
                            entities.remove(base);
                            world.removeBody(base.getBody());
                            i--;

                            int swarmer = MathUtil.randomInt(4, 8);
                            for (int j = 0; j < swarmer; j++) {
                                entities.add(new Swarmer(world, base.getBody().getTransform().getPosition()));
                            }
                        }
                        break;
                    case Swamer:
                        Swarmer swarmer = (Swarmer) entity;
                        swarmer.findTarget(players);
                        break;
                    case StandardAI:
                        StandardAI ai = (StandardAI) entity;
                        ai.findTarget(entities, players);
                        break;

                }
            }
        }

        for(int i = 0; i < debrisTimer.size(); i++) {
            debrisTimer.set(i, debrisTimer.get(i)+dt);
            debris.get(i).update(dt);
            if(debrisTimer.get(i) > 3f) {
                debrisTimer.remove(i);
                debris.remove(i);
                i--;
            }
        }

        // Update the physics world
        world.update(dt);
    }


    public int updateAsteroid(Asteroid a) {
        int index = entities.indexOf(a);

        Vector2f pos = a.getBody().getTransform().getPosition();

        if(a.shouldExplode()) {

            int powerUp = MathUtil.randomInt(1, 75);
            if(powerUp == 1) {
                powerUp = MathUtil.randomInt(1, 4);
                Entity.Type type;
                switch (powerUp) {
                    case 1:
                        type = Shield;
                        break;
                    case 2:
                        type = Life;
                        break;
                    case 3:
                        type = ShotSpeed;
                        break;
                    default:
                        type = null;
                }


                entities.add(new Powerup(type, 20, pos, world));
            }


            if(a.getBody().getShape().getRadius() / 2 > 15) {
                Vector2f position = a.getBody().getTransform().getPosition();
                Vector2f velocity = new Vector2f(a.getBody().getVelocity().x * 1.2f, a.getBody().getVelocity().y * 1.2f);
                float radius = a.getShape().getRadius() * MathUtil.randomFloat(0.5f, 0.8f);

                Asteroid a1 = new Asteroid(world, position, velocity, radius);
                radius = a.getShape().getRadius() * MathUtil.randomFloat(0.5f, 0.8f);
                Asteroid a2 = new Asteroid(world, position, Vector2f.neg(velocity), radius);

                entities.add(a1);
                entities.add(a2);
            }
            ParticleConfig config = new ParticleConfig();
            config.endSize = 1;
            config.startSize = MathUtil.randomInt(7, 12);
            config.maxAngle = 0;
            config.minAngle = 360;
            config.maxLifetime = 3;
            config.minLifetime = 3f;
            config.pointCount = 3;
            config.position = a.getBody().getTransform().getPosition();
            config.rotationalSpeed = MathUtil.randomInt(-20,20);
            config.speed = MathUtil.randomInt(40, 40);
            config.colours[0] = Color.WHITE;
            config.colours[1] = Color.RED;
            config.colours[2] = Color.RED;
            debris.add(new ParticleEmitter(config,10000f, MathUtil.randomInt(4,20)));
            debrisTimer.add(0f);

            ContentManager.instance.getSound("Explode_" + MathUtil.randomInt(1, 4)).play();
            world.removeBody(a.getBody());
            entities.remove(index);
            index--;
        }
        return index;
    }

    public void bossWipeout() {
        for(int i = 0; i < entities.size(); i++) {
            if(MathUtil.randomInt(0, 35) == 0) {
                Entity e  = entities.get(i);
                if(e.getType() == Entity.Type.Asteroid && e.getBody().getShape().getRadius() / 2 > 15) {
                    Asteroid a = (Asteroid) e;
                    Vector2f position = e.getBody().getTransform().getPosition();
                    Vector2f velocity = new Vector2f(e.getBody().getVelocity().x, e.getBody().getVelocity().y);
                    float radius = a.getShape().getRadius() * MathUtil.randomFloat(0.5f, 0.8f);

                    Asteroid a1 = new Asteroid(world, position, velocity, radius);
                    radius = a.getShape().getRadius() * MathUtil.randomFloat(0.5f, 0.8f);
                    Asteroid a2 = new Asteroid(world, position, Vector2f.neg(velocity), radius);

                    entities.add(a1);
                    entities.add(a2);
                    ParticleConfig config = new ParticleConfig();
                    config.endSize = 1;
                    config.startSize = MathUtil.randomInt(7, 12);
                    config.maxAngle = 0;
                    config.minAngle = 360;
                    config.maxLifetime = 3;
                    config.minLifetime = 3f;
                    config.pointCount = 3;
                    config.position = a.getBody().getTransform().getPosition();
                    config.rotationalSpeed = MathUtil.randomInt(-20,20);
                    config.speed = MathUtil.randomInt(40, 40);
                    config.colours[0] = Color.WHITE;
                    config.colours[1] = Color.RED;
                    config.colours[2] = Color.RED;
                    debris.add(new ParticleEmitter(config,10000f, MathUtil.randomInt(4,20)));
                    debrisTimer.add(0f);
                }
                world.removeBody(e.getBody());
                entities.remove(i);
                ContentManager.instance.getSound("Explode_" + MathUtil.randomInt(1, 4)).play();
                i--;
            }
        }
    }

    @Override
    public void render() {

        window.setTitle("FPS: " + game.getEngine().getFramerate());

        window.draw(background);

        for(ParticleEmitter pe: debris) {
            pe.render(window);
        }

        for(Entity entity : entities) {
            entity.render(window);
        }

        for(Player player : players) {
            player.render(window);
            if(player.isAlive()) {
                int number = player.getNumber().ordinal();
                Text text = new Text("Player " + (number + 1), ContentManager.instance.getFont("Ubuntu"), 28);
                text.setStyle(TextStyle.BOLD);
                text.setPosition(15, 25 + (number * 60));
                window.draw(text);

                for(int i = 0; i < player.getLives() + 1; i++) {
                    ConvexShape shape = new ConvexShape(player.getBody().getShape().getVertices());
                    shape.setPosition(150 + (i * 30), 50 + (number * 60));
                    shape.setFillColor(player.getDefaultColour());
                    window.draw(shape);
                }
            }
        }

        Text text = new Text("Time Survived " + MathUtil.round(timer.boss, 2) + "s",
                ContentManager.instance.getFont("Ubuntu"), 28);

        text.setStyle(TextStyle.BOLD);
        text.setPosition(15, 25 + ((players.length) * 60));
        window.draw(text);

        if(bossSpawned) {
            for(Entity boss : bosses) {
                if(boss.isAlive()) boss.render(window);
            }
        }



        boolean showText = timer.boss > timer.bossRate - 6 && !bossSpawned
                && MathUtil.round(timer.boss % 1f, 0) == 0
                && config.difficulty == LevelConfig.Difficulty.Hard;

        if(showText) {

            text = new Text("Danger! Boss Approaching!", ContentManager.instance.getFont("Ubuntu"), 36);
            text.setStyle(Text.BOLD | TextStyle.UNDERLINED);
            text.setColor(Color.RED);
            FloatRect screenRect = text.getLocalBounds();
            text.setOrigin(screenRect.width / 2, 0);
            text.setPosition(State.WORLD_SIZE.x / 2, 40);
            window.draw(text);

            if(ContentManager.instance.getSound("Alert").getStatus() == SoundSource.Status.STOPPED) {
                ContentManager.instance.getSound("Alert").play();
            }
        }

       world.render(window);
    }


    @Override
    public void registerObserver(Observer observer, Message.Type type) {
        if(observers.containsKey(type)) {
            observers.get(type).add(observer);
        }
        else {
            ArrayList<Observer> list = new ArrayList<>();
            list.add(observer);
            observers.put(type, list);
        }
    }

    @Override
    public boolean removeObserver(Observer observer, Message.Type type) {
        if(!observers.containsKey(type)) return false;

        List<Observer> list = observers.get(type);
        if(list == null) return false;

        boolean result = list.remove(observer);

        if(list.isEmpty()) {
            observers.remove(type);
        }

        return result;
    }

    @Override
    public void postMessage(Message message) {
        if(!observers.containsKey(message.getType())) return;

        List<Observer> list = observers.get(message.getType());
        if(list == null) return;

        for(Observer observer : list) {
            observer.receiveMessage(message);
        }
    }

    @Override
    public void dispose() {}

    private void loadContent(boolean textured) {
        if(contentLoaded) return;
        contentLoaded = true;

        // Load Textures

        ContentManager.instance.loadTexture("Asteroid", "Asteroid.png");
        ContentManager.instance.loadTexture("Player", "Player.png");



        ContentManager.instance.loadTexture("Space", "Stars.png");

        ContentManager.instance.loadTexture("Pascal1", "LargeEnemy1.png");
        ContentManager.instance.loadTexture("Pascal2", "LargeEnemy2.png");

        // Load Fonts
        ContentManager.instance.loadFont("Ubuntu","Ubuntu.ttf");

        // Load Sounds
        ContentManager.instance.loadSound("Pew", "pew.wav");
        ContentManager.instance.loadSound("Explode_1", "explode.wav");
        ContentManager.instance.loadSound("Explode_2", "explode2.wav");
        ContentManager.instance.loadSound("Explode_3", "explode3.wav");
        ContentManager.instance.loadSound("Alert", "alert.wav");
        ContentManager.instance.getSound("Alert").setVolume(50f);

        // Load Music
        ContentManager.instance.loadMusic("PlayMusic", "Music.wav");
        ContentManager.instance.loadMusic("Hexagon", "Focus.ogg");
        ContentManager.instance.loadMusic("Pascal", "Pascal.ogg");
        ContentManager.instance.loadMusic("Quad", "Quad.ogg");
    }

    public Player[] getPlayers() { return players; }
}
