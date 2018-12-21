package com.teamtwo.aerolites.States;

import com.teamtwo.engine.Input.Controllers.Button;
import com.teamtwo.engine.Input.Controllers.ControllerState;
import com.teamtwo.engine.Input.Controllers.Controllers;
import com.teamtwo.engine.Input.Controllers.PlayerNumber;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Text;
import org.jsfml.window.Keyboard;


/**
 * @author Tijan Weir
 */
public class CreditState extends State {

    private static final float timerPerColour = 0.5f;

    private Text[] names;
    private RectangleShape background;

    private float colourTimer;
    private int currentColour;

    private boolean prevEscape;
    private ControllerState prevState;

    private Color[] colours;

    public CreditState(GameStateManager gsm) {
        super(gsm);
        Font font = ContentManager.instance.getFont("Ubuntu");

        background = new RectangleShape(State.WORLD_SIZE);
        background.setPosition(0, 0);
        background.setTexture(ContentManager.instance.getTexture("Space"));

        colours = new Color[] {
                Color.RED, Color.YELLOW, new Color(255, 45, 195),
                Color.GREEN, new Color(255, 114, 38), Color.MAGENTA,
                Color.CYAN
        };

        names = new Text[6];

        int fontSize = 40;

        names[0] = new Text("Matthew Threfall", font, fontSize);
        names[1] = new Text("James Bulman", font, fontSize);
        names[2] = new Text("Tijan Weir", font, fontSize);
        names[3] = new Text("Ayo Olutobi", font, fontSize);
        names[4] = new Text("Pavlos Anastasiadis", font, fontSize);
        names[5] = new Text("Lewis Linaker", font, fontSize);

        for(int i = 0; i < names.length; i++) {
            names[i].setPosition((State.WORLD_SIZE.x / 2f) - (names[i].getLocalBounds().width / 2f), 100 + (i * 80));
        }

        prevEscape = false;
        prevState = Controllers.getState(PlayerNumber.One);
    }

    /**
     * This is called once per frame, used to perform any updates required
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        ControllerState state = Controllers.getState(PlayerNumber.One);

        if (!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && prevEscape) {
            gsm.popState();
        }
        else if(!state.button(Button.B) && prevState.button(Button.B)) {
            gsm.popState();
        }

        colourTimer += dt;

        float ratio = colourTimer / timerPerColour;

        for(int i = 0; i < names.length; i++) {
            names[i].setColor(MathUtil.lerpColour(colours[(currentColour + i) % 6],
                    colours[(currentColour + i + 1) % 6], ratio));
        }

        if(colourTimer >= timerPerColour) {
            colourTimer = 0;
            currentColour++;
        }

        prevState = state;
        prevEscape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
    }

    public void render() {
        window.draw(background);
        for(Text text : names) { window.draw(text); }
    }

    @Override
    public void dispose() {

    }

}
