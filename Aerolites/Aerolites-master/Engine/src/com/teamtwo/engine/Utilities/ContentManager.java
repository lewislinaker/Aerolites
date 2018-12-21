package com.teamtwo.engine.Utilities;

import com.teamtwo.engine.Utilities.Debug.Debug;
import org.jsfml.audio.Music;
import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Texture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * A Well-defined location for all content to be loaded into, this prevents extra memory from being used when using content
 * @author James Bulman
 */
public class ContentManager {

    /**
     * Singleton instance which allows the user to load content
     */
    public static final ContentManager instance;
    static { instance = new ContentManager(); }

    // Maps a String name onto a Texture
    private HashMap<String, Texture> textures;
    // Maps a String name onto a font
    private HashMap<String, Font> fonts;
    // Maps a String name onto a sound
    private HashMap<String, Sound> sounds;
    // Maps a String name onto a piece of music
    private HashMap<String, Music> music;


    /**
     * Private constructor to prevent other instances from being created
     */
    private ContentManager() {
        textures = new HashMap<>();
        fonts = new HashMap<>();
        sounds = new HashMap<>();
        music = new HashMap<>();
    }

    /**
     * Retrieve a loaded Texture from the Content Manager
     * @param name The name associated to the Texture to retrieve
     * @return The Texture if it is loaded, otherwise throws an {@link IllegalArgumentException}
     */
    public Texture getTexture(String name) {
        // Checks to make sure the name has been loaded
        if(!textures.containsKey(name)) {
            throw new IllegalArgumentException("Error: Unknown Texture: " + name);
        }

        // Returns the Texture associated
        return textures.get(name);
    }

    /**
     * Retrieves a loaded Font from the Content Manager
     * @param name The name associated to the Font to retrieve
     * @return The Font if it has been loaded, otherwise throws an {@link IllegalArgumentException}
     */
    public Font getFont(String name) {
        // Checks to make sure the name has been loaded
        if(!fonts.containsKey(name)) {
            throw new IllegalArgumentException("Error: Unknown Font: " + name);
        }

        // Returns the Font associated
        return fonts.get(name);
    }

    /**
     * Retrieves a loaded Sound from the Content Manager
     * @param name The name associated with the Sound to retrieve
     * @return The Sound if it has been loaded, otherwise throws an {@link IllegalArgumentException}
     */
    public Sound getSound(String name) {
        // Checks to make sure the name has been loaded
        if(!sounds.containsKey(name)) {
            throw new IllegalArgumentException("Error: Unknown Sound: " + name);
        }

        // Returns the Sound associated
        return sounds.get(name);
    }

    /**
     * Retrieves a loaded piece of Music from the Content Manager
     * @param name The name associated with the Music to retrieve
     * @return The Music if it has been loaded, otherwise throws an {@link IllegalArgumentException}
     */
    public Music getMusic(String name) {
        // Checks to make sure the name has been loaded
        if(!music.containsKey(name)) {
            throw new IllegalArgumentException("Error: Unknown Music: " + name);
        }

        // Returns the Music associated
        return music.get(name);
    }

    /**
     * Loads a Texture from the given filename into the Content Manager
     * @param name The name to associate to the Texture for retrieving it
     * @param filename The name of the file which contains the texture, including extension
     * @return The Texture which was loaded
     */
    public Texture loadTexture(String name, String filename) {
        // Makes sure a duplicate name has not been user
        // Stops loading if it has
        if(textures.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Texture with the name \"" + name + "\" has already been loaded");
            return textures.get(name);
        }

        // Creates a new Texture
        Texture t = new Texture();
        try {
            // Tries to load if from the filename provided
            t.loadFromFile(Paths.get(System.getProperty("user.dir") + File.separator + "Textures" + File.separator + filename));
        }
        catch (IOException ex) {
            // If it fails the exit
            System.err.println("Error: Failed to load Texture: " + filename);
            ex.printStackTrace();
            System.exit(-1);
        }

        // If it succeeds then put it into the hash map
        textures.put(name, t);
        return t;
    }

    /**
     * Loads a Font from the given filename into the Content Manager
     * @param name The name to associate to the Font for retrieving it
     * @param filename The name of the file wich contains the font, including extension
     * @return The Font which was loaded
     */
    public Font loadFont(String name, String filename) {
        // Makes sure a duplicate name has not been used
        // Stops loading if it has
        if(fonts.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Font with the name \"" + name + "\" has already been loaded");
            return fonts.get(name);
        }

        // Creates a new font
        Font f = new Font();
        try {
            // Tries to load it from the filename provided
            f.loadFromFile(Paths.get(System.getProperty("user.dir") + File.separator + "Fonts" + File.separator + filename));
        }
        catch (IOException ex) {
            // If it fails the exit
            System.err.println("Error: Failed to load Font: " + filename);
            ex.printStackTrace();
            System.exit(-1);
        }

        // If it succeeds then put it into the hash map
        fonts.put(name, f);
        return f;
    }

    /**
     * Loads a Sound from the given filename into the Content Manager
     * @param name The name to associate with the Sound for retrieving
     * @param filename The name of the file which contains the sound, including extension
     * @return The Sound which was loaded
     */
    public Sound loadSound(String name, String filename) {
        // Makes sure a duplicate name has not been used
        // Stops loading if it has
        if(sounds.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Sound with the name \"" + name + "\" has already been loaded");
            return sounds.get(name);
        }

        // Creates a new Sound
        Sound s = new Sound();
        try {
            // Loads a SoundBuffer from from the filename provided
            SoundBuffer soundBuffer = new SoundBuffer();
            soundBuffer.loadFromFile(Paths.get(System.getProperty("user.dir")
                    + File.separator + "Sounds" + File.separator + filename));

            s.setBuffer(soundBuffer);
        }
        catch (IOException ex) {
            // If it fails then exit
            System.err.println("Error: Failed to load Sound: " + filename);
            ex.printStackTrace();
            System.exit(-1);
        }

        // If it succeeds then put it into the hash map
        sounds.put(name, s);
        return s;
    }

    /**
     * Loads Music from the given filename into the Content Manager
     * @param name The name to associate with the Music for retrieving
     * @param filename The name of the file which contains the music, including extension
     * @return The piece of Music which was loaded
     */
    public Music loadMusic(String name, String filename) {
        // Makes sure a duplicate name has not been used
        // Stops loading if it has
        if(music.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "Music with the name \"" + name + "\" has already been loaded");
            return music.get(name);
        }

        // Creates new Music
        Music m = new Music();
        try {
            // Tries to load from the filename provided
            m.openFromFile(Paths.get(System.getProperty("user.dir")
                    + File.separator + "Music" + File.separator + filename));
        }
        catch (IOException ex) {
            // If it fails then exit
            System.err.println("Error: Failed to load Music: " + filename);
            ex.printStackTrace();
            System.exit(-1);
        }

        // If it succeeds then put it into the hash map
        music.put(name, m);
        return m;
    }

    /**
     * Unloads the texture associated with the given name
     * @param name The name of the texture to unload
     */
    public void unloadTexture(String name) {
        if(!textures.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Texture with the name \"" + name + "\" was not loaded");
            return;
        }

        textures.remove(name);
    }

    /**
     * Unloads the font associated with the given name
     * @param name The name of the font to unload
     */
    public void unloadFont(String name) {
        if(!fonts.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Font with the name \"" + name + "\" was not loaded");
            return;
        }

        fonts.remove(name);
    }

    /**
     * Unloads the sound associated with the given name
     * @param name The name of the sound to unload
     */
    public void unloadSound(String name) {
        if(!sounds.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "A Sound with the name \"" + name + "\" was not loaded");
            return;
        }

        sounds.remove(name);
    }

    /**
     * Unloads the piece of music associated with the given name
     * @param name The name of the music to unload
     */
    public void unloadMusic(String name) {
        if(!music.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARNING, "Music with the name \"" + name + "\" was not loaded");
            return;
        }

        music.remove(name);
    }
}
