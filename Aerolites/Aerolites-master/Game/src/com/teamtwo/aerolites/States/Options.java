package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.UI.Slider;
import com.teamtwo.aerolites.UI.UIButton;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

/**
 * @author James Bulman
 */
//TODO resolution options
public class Options extends State {

    public static float MUSIC_VOLUME = 50f;
    public static float SFX_VOLUME = 50f;

    private static final String[] resolutionStrings = new String[] {
            "640x360", "960x540", "1280x720", "1366x768", "1600x900", "1920x1080"
    };

    private Slider[] sliders;
    private int currentRes;
    private Text resText;
    private UIButton resButton;

    private boolean prevEscape;
    private boolean prevClick;

    public Options(GameStateManager gsm) {
        super(gsm);
        Font font = ContentManager.instance.getFont("Ubuntu");

        sliders = new Slider[2];

        sliders[0] = new Slider("Music Volume", 25, new Vector2f(140, 360), new Vector2f(State.WORLD_SIZE.x - 240, 80));
        sliders[1] = new Slider("SFX Volume", 25, new Vector2f(140, 460), new Vector2f(State.WORLD_SIZE.x - 240, 80));

        sliders[0].setValue(MUSIC_VOLUME/100f);
        System.out.println(MUSIC_VOLUME/100f);
        sliders[1].setValue(SFX_VOLUME/100f);

        String res = window.getSize().x + "x" + window.getSize().y;
        currentRes = 0;
        for(int i = 0; i < resolutionStrings.length; i++) {
            if(res.equals(resolutionStrings[i])) {
                currentRes = i;
            }
        }

        if(currentRes == 0) window.setSize(new Vector2i(640, 360));

        resText = new Text("Current Resolution: " + resolutionStrings[currentRes], font, 120);

        prevEscape = false;
        prevClick = false;

        resButton = new UIButton(State.WORLD_SIZE.x/2,580,"Resolution: " + resolutionStrings[currentRes], 45);

    }


    public void update(float dt) {

        mouse = Mouse.getPosition(window);
        Vector2f pos =  window.mapPixelToCoords(mouse);

        for(Slider slider : sliders) {
            slider.checkValue(pos);
        }

        MUSIC_VOLUME = sliders[0].getValue() * 100f;
        SFX_VOLUME = sliders[1].getValue() * 100f;

        float width = resText.getLocalBounds().width;
        resText.setPosition((State.WORLD_SIZE.x / 2f) - (width / 2f), 40f);

        if(!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && prevEscape) {
            gsm.popState();
        }
        resButton.checkInBox(pos);
        if(resButton.isClicked() &&  Mouse.isButtonPressed(Mouse.Button.LEFT) && !prevClick) {
            prevClick = true;
            currentRes++;
            if(currentRes>5)
                currentRes = 0;
            resButton.setTitle("Resolution: " + resolutionStrings[currentRes]);
            String res = resolutionStrings[currentRes];
            String[] sizes = res.split("x");
            window.setSize(new Vector2i(Integer.parseInt(sizes[0]),Integer.parseInt(sizes[1])));
        }

        resText.setString("Current Resolution: " + resolutionStrings[currentRes]);

        prevEscape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
        prevClick = Mouse.isButtonPressed(Mouse.Button.LEFT);
    }

    public void render() {

        window.draw(resText);

        for(Slider slider : sliders) {
            slider.render(window);
        }
        resButton.render(window);
    }

    public void dispose() {}
}
