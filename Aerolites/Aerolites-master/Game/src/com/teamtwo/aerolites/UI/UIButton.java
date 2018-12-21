package com.teamtwo.aerolites.UI;

import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.Interfaces.EntityRenderable;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.TextStyle;
import org.jsfml.system.Vector2f;


/**
 * @author Tijan Weir
 */
public class UIButton implements EntityRenderable {

    private Text text;
    private Vector2f position;
    private boolean clicked;

    public UIButton(float x, float y, String label, int fontSize) { this(new Vector2f(x, y), label, fontSize); }

    public UIButton(Vector2f position, String label, int fontSize) {

        text = new Text(label, ContentManager.instance.getFont("Ubuntu"), fontSize);
        text.setPosition(position.x - text.getLocalBounds().width / 2f, position.y - text.getLocalBounds().height / 2f);
        text.setColor(Color.GREEN);
        text.setStyle(TextStyle.UNDERLINED | TextStyle.BOLD);

        this.position = new Vector2f(position.x - text.getLocalBounds().width / 2f,
                position.y - text.getLocalBounds().height/ 2f + 10f);

        clicked = false;
    }

    @Override
    public void render(RenderWindow renderer) {
        renderer.draw(text);
    }

    public void checkInBox(Vector2f mouse) {
        clicked = (mouse.x > position.x && mouse.x < (position.x + text.getLocalBounds().width));
        clicked &= (mouse.y > position.y && mouse.y < (position.y + text.getLocalBounds().height));
    }

    public Text getText() { return text; }

    public Vector2f getPosition() { return position; }

    public String getLabel() { return text.getString(); }

    public boolean isClicked() { return clicked; }

    public void setTitle(String title) {
        this.text.setString(title);
    }
}
