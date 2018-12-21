package com.teamtwo.aerolites.Utilities;

/**
 * @author Matthew Threlfall
 */
public class LevelConfig {

    /**
     * An enumeration for the difficulty of the level
     */
    public enum Difficulty {
        /** Easy difficulty */
        Easy(8, 33, 45, 60),
        /** Medium difficulty */
        Medium(4, 25, 30, 75),
        /** Hard difficulty */
        Hard(3f, 14, 16, 90);

        public final float asteroid;
        public final float swarmer;
        public final float ai;
        public final float boss;

        Difficulty(float asteroid, float swarmer, float ai, float boss) {
            this.asteroid = asteroid;
            this.swarmer = swarmer;
            this.ai = ai;
            this.boss = boss;
        }

    }

    /** The difficulty of the level, default = {@link Difficulty#Medium} */
    public Difficulty difficulty;

    /**whether or not the game will use the textures or it will run in retro solid colour mode*/
    public boolean textured;

    /** The input types of each player, this also defines how many players,
     * default = 1 player, using {@link InputType#Keyboard} */
    public final InputType[] players;

    /**
     * Constructs a default level configuration
     */
    public LevelConfig() {

        difficulty = Difficulty.Medium;

        players = new InputType[8];
        players[0] = InputType.Keyboard;
    }
}
