package com.teamtwo.engine.Launcher;

import org.jsfml.window.WindowStyle;

public class LauncherConfig {

    /** The target width of the window, default = 640 */
    public int width;
    /** The target height of the window, default = 360 */
    public int height;

    /** The FPS limit of updating and rendering, set negative to use V-Sync, default = -1 */
    public int fpsLimit;

    /** The title of the window, default = "Untitled" */
    public String title;
    /** The {@link WindowStyle} of the window, default = WindowStyle.TITLEBAR | WindowStyle.CLOSE */
    public int style;

    /** Whether the engine will automatically start its main loop, default = true */
    public boolean autoStart;

    /** The root folder to all of the content for the game, default = "Content" */
    public String contentRoot;

    /**
     * Creates a default configuration
     */
    public LauncherConfig() {
        width = 640;
        height = 360;

        fpsLimit = -1;

        title = "Untitled";
        style = WindowStyle.TITLEBAR | WindowStyle.CLOSE;

        autoStart = true;
        contentRoot = "Content";
    }

}
