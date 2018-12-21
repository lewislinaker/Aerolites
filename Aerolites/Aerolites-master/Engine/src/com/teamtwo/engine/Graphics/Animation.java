package com.teamtwo.engine.Graphics;

import com.teamtwo.engine.Utilities.Interfaces.EntityRenderable;
import com.teamtwo.engine.Utilities.Interfaces.Updateable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

/**
 * A Class which will animate a sprite sheet
 */
public class Animation implements Updateable, EntityRenderable {

    /** Whether or not the animation is playing */
    private boolean playing;

    /** The texture source of the animation */
    private Texture texture;

    /** The position of the animation on the screen */
    private Vector2f position;
    /** Whether to flip the animation in the x direction */
    private boolean flipX;
    /** Whether to flip the animation in the y direction */
    private boolean flipY;
    /** The amount to rotate the animation by, in degrees */
    private float angle;

    /** The width of a frame in the animation */
    public final int width;
    /** The height of a frame in the animation */
    public final int height;

    /** The number of rows the sprite sheet has */
    private int rows;
    /** The number of columns the sprite sheet has */
    private int columns;

    /** The total number of frames the animation has*/
    public final int totalFrames;
    /** The frame which the animation is currently at */
    private int currentFrame;
    /** How long each frame should last, in seconds */
    private float timePerFrame;

    /** How long has passed since the last frame, in seconds */
    private float accumulator;

    /** The scale of the sprite*/
    private Vector2f scale;

    /**
     * Creates an animation from the given texture key, with a time per frame of 0.1 seconds
     * @param texture The texture to represent the animation
     * @param rows The number of rows in the animation sheet
     * @param columns The number of columns in the animation sheet
     */
    public Animation(Texture texture, int rows, int columns) { this(texture, rows, columns, 0.1f); }

    /**
     * Creates an animation from the given texture key, with a time per frame as given
     * @param texture The texture to represent the animation
     * @param rows The number of rows in the animation sheet
     * @param columns The number of columns in the animation sheet
     * @param timePerFrame The time each frame lasts for, in seconds
     */
    public Animation(Texture texture, int rows, int columns, float timePerFrame) {
        // Get the texture from the content manager
        this.texture = texture;

        // Set the number of rows and columns
        this.rows = rows;
        this.columns = columns;

        // Calculate the total frames and set to beginning of the animation
        totalFrames = rows * columns;
        currentFrame = 0;
        this.timePerFrame = timePerFrame;

        // Work out the width and height of the animation
        width = texture.getSize().x / columns;
        height = texture.getSize().y / rows;

        // Sets the position to (0, 0)
        position = new Vector2f(0, 0);

        // Sets playing
        playing = true;

        flipX = flipY = false;
        angle = 0;

        // Resets the time accumulator
        accumulator = 0;

        scale = new Vector2f(1,1);
    }

    /**
     * Updates the animation and moves it onto the next frame when necessary
     * @param dt The amount of time passed since last frame
     */
    public void update(float dt) {
        // Return if not playing
        if(!playing) return;

        // Advance the amount of time since last frame
        accumulator += dt;
        if(accumulator >= timePerFrame) {
            // Advance frame
            currentFrame++;

            // If end of animation, loop back to beginning
            if(currentFrame == totalFrames)
                currentFrame = 0;

            // Reset time since last frame
            accumulator = 0;
        }
    }

    /**
     * Draws the animation, at its position and on its current frame, to the screen
     * @param renderer The {@link RenderWindow} to draw the entity to
     */
    public void render(RenderWindow renderer) {

        // Create a sprite and set its position
        Sprite sprite = new Sprite(texture);
        sprite.setScale(scale);
        sprite.setOrigin(width / 2, height / 2);
        sprite.setPosition(position);
        sprite.setRotation(angle);

        if(flipX) sprite.setScale(-1, 1);
        if(flipY) sprite.setScale(sprite.getScale().x, -1);

        // Work out UV coordinates within the sprite sheet
        int row = (int)(((float) currentFrame) / ((float) columns));
        int col = currentFrame % columns;

        // Draw to screen
        sprite.setTextureRect(new IntRect(col * width, row * height, width, height));
        renderer.draw(sprite);
    }

    /**
     * Gets the current position of the animation
     * @return The position
     */
    public Vector2f getPosition() { return position; }

    /**
     * Gets how long each frame lasts for, in seconds
     * @return The time each frame lasts for
     */
    public float getTimePerFrame() { return timePerFrame; }

    /**
     * Gets whether or not the animation is currently playing
     * @return True if the animation is playing, otherwise False
     */
    public boolean isPlaying() { return playing; }

    /**
     * Whether or not the animation is flipped in the x direction
     * @return True if it is flipped, otherwise False
     */
    public boolean isFlippedX() { return flipX; }

    /**
     * Whether or not the animation is flipped in the y direction
     * @return True if it is flipped, otherwise False
     */
    public boolean isFlippedY() { return flipY; }

    /**
     * Gets the current rotation of the animation
     * @return The angle which the animation is rotated by, in degrees
     */
    public float getRotation() { return angle; }

    /**
     * Sets the position to the parameters given
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     */
    public void setPosition(float x, float y) { setPosition(new Vector2f(x, y)); }

    /**
     * Sets the position to the one given
     * @param position The new position to set
     */
    public void setPosition(Vector2f position) { this.position = position; }

    /**
     * Sets whether or not the animation should be flipped in the x direction
     * @param flipX True to flip in the x direction, otherwise False
     */
    public void setFlippedX(boolean flipX) { this.flipX = flipX; }

    /**
     * Sets whether or not the animation should be flipped in the y direction
     * @param flipY True to flip in the y direction, otherwise false
     */
    public void setFlippedY(boolean flipY) { this.flipY = flipY; }

    /**
     * Rotates the animation by the given angle
     * @param degrees The amount to rotate by, in degrees
     */
    public void rotate(float degrees) {
        angle += degrees;

        // Keep it between 0 and 360
        if(angle > 360) {
            angle -= 360;
        }
        else if(angle < 0) {
            angle += 360;
        }
    }

    /**
     * Sets the rotation of the sprite to the given angle
     * @param degrees The angle to set the rotation to, in degrees
     */
    public void setRotation(float degrees) {
        angle = degrees;

        // Keep it between 0 and 360
        if(this.angle > 360) {
            int m = (int)angle % 360;
            angle -= (m * 360);

        }
        else if(this.angle < 0) {
            int m = (int)angle % 360;
            angle += (m * 360);
        }

    }

    /**
     * Sets whether or not the animation should play
     * @param playing True to play the animation, False to stop the animation
     */
    public void setPlaying(boolean playing) { this.playing = playing; }

    /**
     * Sets how long each frame lasts for to the time given
     * @param timePerFrame The time each frame should last for, in seconds
     */
    public void setTimePerFrame(float timePerFrame) { this.timePerFrame = timePerFrame; }

    public void setScale(float xSize, float ySize){
        float x, y;
        x = xSize / width;
        y = ySize / height;
        scale = new Vector2f(x, y);
    }
}
