package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.Entities.AI.StandardAI;
import com.teamtwo.aerolites.Entities.AI.Swarmer;
import com.teamtwo.aerolites.Entities.Asteroid;
import com.teamtwo.aerolites.Entities.Entity;
import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.aerolites.UI.UIButton;
import com.teamtwo.aerolites.Utilities.InputType;
import com.teamtwo.engine.Input.Controllers.*;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.Disposable;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import java.util.ArrayList;


/**
 * @author Tijan Weir
 */
public class MainMenu extends State {

    private class Transition {
        private Text[] selected;
        private UIButton current;
        private Vector2f offset;

        private boolean transition;
        private float transitionTimer;

        private Transition(int fontSize) {
            Font font = ContentManager.instance.getFont("Ubuntu");

            offset = new Vector2f(fontSize / 2f, (fontSize / 5f) - 1);

            selected = new Text[2];
            selected[0] = new Text("[", font, fontSize);
            selected[0].setStyle(TextStyle.BOLD);
            selected[0].setPosition(Vector2f.sub(buttons[0].getPosition(), offset));

            selected[1] = new Text("]", font, fontSize);
            selected[1].setStyle(TextStyle.BOLD);
            float width = buttons[0].getText().getLocalBounds().width;
            float height = buttons[0].getText().getLocalBounds().height;
            selected[1].setPosition(Vector2f.add(buttons[0].getPosition(), new Vector2f(width + offset.x, -offset.y)));

            current = buttons[0];

            transition = false;
            transitionTimer = 0;
        }

        private void transition(float dt) {
            if(!transition) return;

            transitionTimer += dt;
            float x = MathUtil.lerp(selected[0].getPosition().x, current.getPosition().x - offset.x,
                    transitionTimer / timeToSwitch);

            float y = MathUtil.lerp(selected[0].getPosition().y, current.getPosition().y - offset.y,
                    transitionTimer / timeToSwitch);

            selected[0].setPosition(x, y);

            float width = current.getText().getLocalBounds().width;
            x = MathUtil.lerp(selected[1].getPosition().x, current.getPosition().x + width + offset.x,
                    transitionTimer / timeToSwitch);
            y = MathUtil.lerp(selected[1].getPosition().y, current.getPosition().y - offset.y,
                    transitionTimer / timeToSwitch);

            selected[1].setPosition(x, y);

            if(transitionTimer >= timeToSwitch) {
                transitionTimer = 0;
                transition = false;
            }
        }
    }

    private static final float timeToSwitch = 1f;

    private UIButton[] buttons;
    private Text title;
    private ControllerState prevState;
    private Transition selection;

    private boolean selected;
    private int controllerIndex;

    private boolean prevMouse;
    private boolean prevEscape;

    private RectangleShape background;

    private World world;
    private ArrayList<Entity> entities;
    private float accumulator, spawn;
    private int swarmerCount;

    public MainMenu(GameStateManager gsm) {
        super(gsm);

        ContentManager.instance.loadFont("Ubuntu", "Ubuntu.ttf");
        ContentManager.instance.loadTexture("Asteroid", "Asteroid.png");
        Texture bg = ContentManager.instance.loadTexture("Space", "Stars.png");

        background = new RectangleShape(State.WORLD_SIZE);
        background.setPosition(0, 0);
        background.setTexture(bg);

        title = new Text("Aerolites", ContentManager.instance.getFont("Ubuntu"), 100);
        title.setPosition(State.WORLD_SIZE.x / 2f - title.getLocalBounds().width / 2f, 55f);
        title.setStyle(TextStyle.BOLD);
        title.setColor(Color.WHITE);

        buttons = new UIButton[4];
        String[] labels = new String[] { "Singleplayer", "Multiplayer", "Options", "Credits" };
        for(int i = 0; i < buttons.length; i++) {
            buttons[i] = new UIButton(State.WORLD_SIZE.x / 2f, 310 + (i * 120), labels[i], 45);
        }

        world = new World(Vector2f.ZERO);
        entities = new ArrayList<>();

        prevMouse = false;

        spawn = 1.2f;
        accumulator = 0;
        swarmerCount = 0;

        selection = new Transition(45);

        prevState = Controllers.getState(PlayerNumber.One);
        prevEscape = false;
    }

    /**
     * This is called once per frame, used to perform any updates required
     *
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        accumulator += dt;

        if(accumulator > spawn) {
            accumulator = 0;
            int type = MathUtil.randomInt(0, 10);
            switch (type) {
                case 0:
                    entities.add(new StandardAI(world));
                    break;
                case 1:
                    float x = MathUtil.randomFloat(0, WORLD_SIZE.x);
                    float y = MathUtil.randomFloat(0, WORLD_SIZE.y);
                    entities.add(new Swarmer(world, new Vector2f(x, y)));
                    swarmerCount++;
                    break;
                default:
                    entities.add(new Asteroid(world));
                    break;
            }
        }

        for(int i = 0; i < entities.size(); i ++) {
            Entity entity = entities.get(i);
            if(!entity.isOnScreen()) {
                world.removeBody(entity.getBody());
                if(entity instanceof Disposable) ((Disposable) entity).dispose();
                entities.remove(i);
                i--;
            }
            else if(entity.getType() == Entity.Type.Swamer && swarmerCount > 7) {
                world.removeBody(entity.getBody());
                entities.remove(i);
                i--;
                swarmerCount--;
            }
            else {
                switch (entity.getType()) {
                    case StandardAI:
                        ((StandardAI) entity).findTarget(entities, new Player[0]);
                        entity.update(dt);
                        break;
                    case Swamer:
                    case Asteroid:
                        entity.update(dt);
                        break;
                }
            }
        }

        world.update(dt);

        //checks if the mouse is inside a box

        Vector2f pos = window.mapPixelToCoords(Mouse.getPosition(window));
        for(int i = 0; i < buttons.length; i++) {
            UIButton button = buttons[i];
            if(button.isClicked()) {
                if(button != selection.current) {
                    selection.transition = true;
                    selection.transitionTimer = 0;
                    selection.current = button;

                    controllerIndex = i;
                }

                selected = Mouse.isButtonPressed(Mouse.Button.LEFT) && !prevMouse;
            }

            button.checkInBox(pos);
        }

        ControllerState state = Controllers.getState(PlayerNumber.One);

        if(state.thumbstick(Thumbstick.Left).y < -25 && Math.abs(prevState.thumbstick(Thumbstick.Left).y) < 25) {
            controllerIndex = controllerIndex - 1 < 0 ? 3 : controllerIndex - 1;
            selection.current = buttons[controllerIndex];
            selection.transition = true;
            selection.transitionTimer = 0;
        }
        else if(state.thumbstick(Thumbstick.Left).y > 25 && Math.abs(prevState.thumbstick(Thumbstick.Left).y) < 25) {
            controllerIndex = controllerIndex + 1 > 3 ? 0 : controllerIndex + 1;
            selection.current = buttons[controllerIndex];
            selection.transition = true;
            selection.transitionTimer = 0;
        }

        boolean controller = !state.button(Button.A) && prevState.button(Button.A);
        selected |= controller;

        InputType input = controller ? InputType.Controller : InputType.Keyboard;
        if(selected) {
            switch(buttons[controllerIndex].getLabel()) {
                case "Singleplayer":
                    gsm.addState(new StarMap(gsm, new InputType[] { input }));
                    break;
                case "Multiplayer":
                    gsm.addState(new PlayerSelect(gsm, input));
                    break;
                case "Options":
                    gsm.addState(new Options(gsm));
                    break;
                case "Credits":
                    gsm.addState(new CreditState(gsm));
                    break;
            }
        }

        if(!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && prevEscape) {
            game.getEngine().close();
        }
        else if(!state.button(Button.B) && prevState.button(Button.B)) {
            game.getEngine().close();
        }

        // Store the previous input to prevent multiple runs
        prevMouse = Mouse.isButtonPressed(Mouse.Button.LEFT);
        prevEscape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
        prevState = state;

        selected = false;
        controller = false;

        selection.transition(dt);
    }

    public void render() {
        window.draw(background);
        window.draw(title);

        for(Entity entity : entities) {
            entity.render(window);
        }

        for(UIButton button : buttons) {
            button.render(window);
        }

        for(Text text : selection.selected) {
            window.draw(text);
        }
    }

    @Override
    public void dispose() {

    }

}
