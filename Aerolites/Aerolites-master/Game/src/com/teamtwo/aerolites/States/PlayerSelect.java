package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.aerolites.UI.Slider;
import com.teamtwo.aerolites.Utilities.InputType;
import com.teamtwo.engine.Graphics.Particles.ParticleConfig;
import com.teamtwo.engine.Graphics.Particles.ParticleEmitter;
import com.teamtwo.engine.Input.Controllers.Button;
import com.teamtwo.engine.Input.Controllers.ControllerState;
import com.teamtwo.engine.Input.Controllers.Controllers;
import com.teamtwo.engine.Input.Controllers.PlayerNumber;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;


/**
 * @author Tijan Weir
 */
public class PlayerSelect extends State {

    private static final Color[] colours = new Color[] {
            new Color(61, 64, 255), new Color(255, 228, 94), new Color(123, 255, 94),
            new Color(124, 255, 189), new Color(124, 235, 255), new Color(244, 75, 66),
            new Color(204, 86, 255), new Color(255, 107, 210)
    };

    private RectangleShape background;

    private InputType[] types;
    private int playerCount;
    private boolean keyboardTaken;

    private ParticleEmitter[] emitters;

    private ControllerState[] prevStates;
    private boolean prevEscape;
    private boolean prevSpace;

    private boolean ready;
    private float readyTimer;

    private Font font;

    private Slider readyUp;

    public PlayerSelect(GameStateManager gsm, InputType input) {
        super(gsm);

        keyboardTaken = input == InputType.Keyboard;
        types = new InputType[8];
        types[0] = input;

        playerCount = 1;

        background = new RectangleShape(WORLD_SIZE);
        background.setTexture(ContentManager.instance.getTexture("Space"));

        ready = false;
        Vector2f position = new Vector2f((WORLD_SIZE.x / 2f) - 400f, 875f);
        readyUp = new Slider("Not Ready", 45, position, new Vector2f(800, 80f));
        readyUp.setValue(100);

        prevStates = new ControllerState[8];
        for(PlayerNumber player : PlayerNumber.values()) {
            prevStates[player.ordinal()] = Controllers.getState(player);
        }
        prevEscape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
        prevSpace = Keyboard.isKeyPressed(Keyboard.Key.SPACE);

        font = ContentManager.instance.getFont("Ubuntu");

        ParticleConfig config = new ParticleConfig();

        config.minAngle = -15;
        config.maxAngle = 15;
        config.speed = -70;
        config.rotationalSpeed = 40;

        config.pointCount = 3;

        config.startSize = 14;
        config.endSize = 4;

        config.minLifetime = 1.5f;
        config.maxLifetime = 3;

        emitters = new ParticleEmitter[8];

        Color[] jetColours = new Color[] {
                new Color(62, 162, 255), new Color(155, 61, 255),
                new Color(255, 148, 94),  new Color(201, 255, 94),
                new Color(204, 255, 94), new Color(94, 255, 145),
                new Color(124, 255, 124),  new Color(124, 255, 255),
                new Color(124, 255, 209), new Color(124, 170, 255),
                new Color(244, 66, 146), new Color(244, 164, 66),
                new Color(120, 86, 255), new Color(255, 86, 221),
                new Color(225, 107, 255),  new Color(255, 107, 137)
        };

        float x = WORLD_SIZE.x / 4f;
        for(int i = 0; i < 8; i++) {
            config.position = new Vector2f(290 + (x * (i % 4)), 355f + ((i / 4) * 300f));
            config.colours[0] = jetColours[i * 2];
            config.colours[1] = jetColours[(i * 2) + 1];
            emitters[i] = new ParticleEmitter(config, 40, 400);
        }
    }

    /**
     * This is called once per frame, used to perform any updates required
     *
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {

        ControllerState[] states = new ControllerState[8];
        int count = 0;
        for(PlayerNumber player : PlayerNumber.values()) {
            states[count] = Controllers.getState(player);
            count++;
        }

        for(ParticleEmitter emitter : emitters) {
            if(emitter != null) emitter.update(dt);
        }


        if(!ready) {
            int offset = types[0] == InputType.Controller ? 0 : 1;
            for (int i = 1; i < types.length; i++) {
                int cNum = i - offset;

                if (types[i] == null) {
                    if (states[cNum].button(Button.A)) {
                        types[i] = InputType.Controller;
                        playerCount++;
                    }
                    else if (!keyboardTaken && Keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
                       // keyboardTaken = true;
                        types[i] = InputType.Keyboard;
                        offset = 1;
                        playerCount++;
                    }
                }
                else if (types[i] == InputType.Keyboard) {
                    offset = 1;
                    if (Keyboard.isKeyPressed(Keyboard.Key.ESCAPE)) {
                        keyboardTaken = false;
                        types[i] = null;
                        System.arraycopy(types, i + 1, types, i, types.length - 1 - i);
                        i--;
                        offset = 0;
                        playerCount--;
                    }
                }
                else {
                    if (states[cNum].button(Button.B) && !prevStates[cNum].button(Button.B)) {
                        types[i] = null;
                        i--;
                        playerCount--;
                    }
                }
            }

            if(types[0] == InputType.Controller) {
                if(!states[0].button(Button.B) && prevStates[0].button(Button.B)) {
                    gsm.popState();
                }
                else if(states[0].button(Button.A) && !prevStates[0].button(Button.A)) {
                    ready = playerCount > 1;
                }
            }
            else {
                if(!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && prevEscape) {
                    gsm.popState();
                }
                else if(Keyboard.isKeyPressed(Keyboard.Key.SPACE) && !prevSpace) {
                    ready = playerCount > 1;
                }
            }
        }
        else {
            readyTimer += dt;

            if(readyTimer >= 5f) {
                int playerCount = 0;
                for (InputType type : types) {
                    if (type != null) playerCount++;
                }

                InputType[] playerTypes = new InputType[playerCount];
                playerCount = 0;
                for (InputType type : types) {
                    if(type != null) {
                        playerTypes[playerCount] = type;
                        playerCount++;
                    }
                }

                gsm.setState(new StarMap(gsm, playerTypes));
            }
            else if(types[0] == InputType.Controller && !states[0].button(Button.B) && prevStates[0].button(Button.B)) {
                ready = false;
                readyTimer = 0;
                System.out.println("Unreadying!");
            }
            else if(!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && prevEscape) {
                ready = false;
                readyTimer = 0;
                System.out.println("Unreadying!");
            }
        }


        prevEscape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
        prevSpace = Keyboard.isKeyPressed(Keyboard.Key.SPACE);
        prevStates = states;
    }

    public void render() {
        window.draw(background);

        int playerCount = 0;

        Text title = new Text("Multiplayer", font, 90);
        title.setPosition(WORLD_SIZE.x / 2f - title.getLocalBounds().width / 2f, 100f);

        window.draw(title);

        float x = State.WORLD_SIZE.x / 4f;
        for (InputType type : types) {
            if (type != null) {
                emitters[playerCount].render(window);


                ConvexShape shape = new ConvexShape(Player.vertices);
                shape.setPosition(240 + (x * (playerCount % 4)), 350f + ((playerCount / 4) * 300f));
                shape.setFillColor(colours[playerCount]);
                shape.setRotation(90f);
                shape.setOrigin(-3, 85);

                window.draw(shape);

                Text player = new Text("Player " + (playerCount + 1), font, 30);
                player.setPosition(shape.getPosition().x - (player.getLocalBounds().width / 2f),
                        shape.getPosition().y + 50);

                window.draw(player);

                Text input = new Text("Input Type: " + type, font, 30);
                input.setPosition(shape.getPosition().x -
                        (input.getLocalBounds().width / 2f), shape.getPosition().y + 100);
                window.draw(input);



                playerCount++;
            }
        }

        if(!ready) {
            readyUp.setColour(Color.GREEN);
            readyUp.setTitle("Not Ready");
            readyTimer = 0;
        }
        else {
            readyUp.setColour(Color.RED);
            readyUp.setTitle("Ready");
        }

        float val = MathUtil.lerp(1, 0, (readyTimer / 5f));
        readyUp.setValue(val);

        readyUp.render(window);
    }

    public void dispose() {}
}
