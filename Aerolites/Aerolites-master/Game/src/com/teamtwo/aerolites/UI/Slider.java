package com.teamtwo.aerolites.UI;

import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.EntityRenderable;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;

/**
 * @author James Bulman
 */
public class Slider implements EntityRenderable {

    private float value;

    private String title;
    private Text text;
    private RectangleShape[] display;
    private Vector2f size;

    public Slider(String text, int fontSize, Vector2f position, Vector2f size) {

        title = text;
        this.text = new Text(text, ContentManager.instance.getFont("Ubuntu"));

        display = new RectangleShape[2];

        display[0] = new RectangleShape(size);
        display[0].setPosition(position);
        display[0].setFillColor(Color.RED);

        display[1] = new RectangleShape(size);
        display[1].setPosition(position);
        display[1].setOutlineColor(new Color(74, 74, 74));
        display[1].setOutlineThickness(6);
        display[1].setFillColor(Color.TRANSPARENT);


        this.size = size;

        value = 0.5f;
        display[0].setSize(new Vector2f(size.x * value, size.y));

        this.text.setString(title);
        float width = this.text.getLocalBounds().width;
        this.text.setPosition(position.x + (size.x / 2f) - (width / 2f), position.y);
    }


    public void render(RenderWindow renderer) {
        for(RectangleShape shape : display) {
            renderer.draw(shape);
        }
        renderer.draw(text);
    }

    public void checkValue(Vector2f mouse) {
        Vector2f pos = display[1].getPosition();
        Vector2f size = display[1].getSize();
        if(mouse.x > pos.x && mouse.x < pos.x + size.x) {
            if(mouse.y > pos.y && mouse.y < pos.y + size.y) {

                if(Mouse.isButtonPressed(Mouse.Button.LEFT)) {
                    value = Math.abs(mouse.x - pos.x) / size.x;

                    display[0].setSize(new Vector2f(size.x * value, size.y));

                    text.setString(title + " - " + (int)(value * 100f) + "%");
                }
            }
        }
    }

    public float getValue() { return value; }

    public void setValue(float value) {
        this.value = MathUtil.clamp(value, 0, 1);
        display[0].setSize(new Vector2f(size.x * this.value, size.y));
    }

    public void setTitle(String title) {
        this.title = title;
        text.setString(title);
        Vector2f pos = display[0].getPosition();
        text.setPosition((pos.x + (display[1].getSize().x / 2f)) - (text.getLocalBounds().width / 2f), pos.y);
    }

    public void setColour(Color colour) { display[0].setFillColor(colour); }
}
