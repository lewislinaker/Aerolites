package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.aerolites.Utilities.Score;
import com.teamtwo.engine.Input.Controllers.Button;
import com.teamtwo.engine.Input.Controllers.ControllerState;
import com.teamtwo.engine.Input.Controllers.Controllers;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

/**
 * @author Matthew Threlfall
 */
public class LevelOver extends State {

    public enum Stage {
        GameOver,
        Scores
    }

    private boolean win;

    private PlayState background;

    private Stage current;
    private float backgroundPos;
    private float backgroundYMovement;
    private int playerCount;

    private float playerInfoSize;
    private String bannerMessage;

    private Player[] players;
    private ControllerState[] previousStates;
    private boolean previousSpace;

    private boolean wasShooting;

    public LevelOver(GameStateManager gsm, PlayState background, boolean win) {
        super(gsm);
        this.background = background;

        this.win = win;

        current = Stage.GameOver;
        backgroundPos = 0;
        backgroundYMovement = 0;
        previousSpace = false;
        playerInfoSize = 0;
        if(win)
            bannerMessage = "Level Cleared!";
        else
            bannerMessage = "Game Over";

        players = background.getPlayers();
        playerCount = players.length;
        previousStates = new ControllerState[playerCount];
        for(int i = 0; i < playerCount; i++) {
            if(players[i].isController()) {
                previousStates[i] = Controllers.getState(players[i].getControllerNumber());
            }
        }

        previousSpace = Keyboard.isKeyPressed(Keyboard.Key.SPACE);

        wasShooting = previousSpace;
        for(ControllerState state : previousStates) {
            if(state == null) continue;
            wasShooting |= state.button(Button.A);
        }

        ContentManager.instance.getMusic("Hexagon").stop();
        ContentManager.instance.getMusic("Pascal").stop();
        ContentManager.instance.getMusic("PlayMusic").setVolume(Options.MUSIC_VOLUME);
        ContentManager.instance.getMusic("PlayMusic").play();
    }

    @Override
    public void update(float dt) {
        background.update(dt);
        if(Keyboard.isKeyPressed(Keyboard.Key.ESCAPE))
            game.getEngine().close();
        switch (current) {
            case GameOver:
                if(backgroundPos < 1920) {
                    backgroundPos += 4000 * dt;
                }
                else if(backgroundPos > 1920) {
                    backgroundPos = 1920;
                }
                break;
            case Scores:
                if(backgroundPos < 1920 * 2) {
                    backgroundPos += 4000 * dt;
                }
                else if(backgroundPos > 1920 * 2) {
                    backgroundPos = 1920 * 2;
                }
                if(backgroundPos == 1920 * 2){
                    if(backgroundYMovement < 400)
                        backgroundYMovement += 4000 * dt;
                    else if(backgroundYMovement > 400)
                        backgroundYMovement = 400;
                }

                if(backgroundYMovement == 400){
                    if(playerInfoSize < 700)
                        playerInfoSize += 3000 * dt;
                    else if(playerInfoSize > 700){
                        playerInfoSize = 700;
                    }
                }
        }

        for(int i = 0; i < playerCount; i++) {
            Player player = players[i];
            if(player.isController()) {
                ControllerState state = Controllers.getState(player.getControllerNumber());
                if(!state.button(Button.A) && previousStates[i].button(Button.A) && !wasShooting) {
                    if(current == Stage.GameOver) {
                        current = Stage.Scores;
                    }
                    else {
                        if(win) {
                            for(Player score : players) {
                                score.getScore().newLevel();
                            }
                        }
                        else {
                            for(Player score : players) {
                                score.getScore().reset();
                            }
                        }

                        gsm.popState();
                    }
                }
                previousStates[i] = state;
            }
            else if(!Keyboard.isKeyPressed(Keyboard.Key.SPACE) && previousSpace && !wasShooting) {
                if(current == Stage.GameOver) {
                    current = Stage.Scores;
                }
                else {
                    if(win) {
                        for(Player score : players) {
                            score.getScore().newLevel();
                        }
                    }
                    else {
                        for(Player score : players) {
                            score.getScore().reset();
                        }
                    }

                    gsm.popState();
                }
            }

            previousSpace = Keyboard.isKeyPressed(Keyboard.Key.SPACE);

            boolean other = previousSpace;
            for(ControllerState prev : previousStates) {
                if(prev == null) continue;
                other |= prev.button(Button.A);
            }

            wasShooting &= other;
        }
    }

    @Override
    public void render() {
        background.render();
        switch(current) {
            case GameOver:
                Text gameOverText = new Text(bannerMessage, ContentManager.instance.getFont("Ubuntu"), 120);

                RectangleShape box = new RectangleShape();
                box.setPosition(0,WORLD_SIZE.y/2-90);
                box.setSize(new Vector2f(backgroundPos,180f));
                box.setFillColor(new Color(255,0,0, 200));
                window.draw(box);

                box = new RectangleShape();
                box.setPosition(0,WORLD_SIZE.y/2+90);
                box.setSize(new Vector2f(backgroundPos,60));
                box.setFillColor(new Color(122,0,0, 230));
                window.draw(box);

                gameOverText.setStyle(Text.BOLD);
                gameOverText.setColor(Color.WHITE);

                FloatRect gameOverBounds = gameOverText.getLocalBounds();
                gameOverText.setOrigin(gameOverBounds.width / 2, gameOverBounds.height);
                gameOverText.setPosition(backgroundPos - WORLD_SIZE.x / 2, WORLD_SIZE.y / 2);
                window.draw(gameOverText);

                break;
            case Scores:
                gameOverText = new Text(bannerMessage, ContentManager.instance.getFont("Ubuntu"), 120);

                box = new RectangleShape();
                box.setPosition(0, WORLD_SIZE.y / 2 - 90 - backgroundYMovement);
                box.setSize(new Vector2f(backgroundPos,180f));
                box.setFillColor(new Color(255,0,0, 200));
                window.draw(box);

                box = new RectangleShape();
                box.setPosition(0, WORLD_SIZE.y / 2 + 90 - backgroundYMovement);
                box.setSize(new Vector2f(backgroundPos, 60 + backgroundYMovement * 1.8f));
                box.setFillColor(new Color(122, 0, 0, 230));
                window.draw(box);

                gameOverText.setStyle(Text.BOLD);
                gameOverText.setColor(Color.WHITE);

                gameOverBounds = gameOverText.getLocalBounds();
                gameOverText.setOrigin(gameOverBounds.width / 2, gameOverBounds.height);
                gameOverText.setPosition(backgroundPos - WORLD_SIZE.x / 2, WORLD_SIZE.y / 2 - backgroundYMovement);
                window.draw(gameOverText);

                Text scores = new Text("Scoreboard", ContentManager.instance.getFont("Ubuntu"),120);
                FloatRect scoresRect = scores.getLocalBounds();
                scores.setOrigin(scoresRect.width/2,scoresRect.height);
                scores.setPosition(backgroundPos - WORLD_SIZE.x*1.5f, WORLD_SIZE.y / 2 - backgroundYMovement);
                window.draw(scores);

                float playerWidth = (1920-(90 + 40*(playerCount)))/(playerCount);
                int font = 36;
                if(playerCount > 5) {
                    font = 16;
                }
                else if(playerCount > 3)
                    font = 28;
                for(int i = 0; i < playerCount; i++){
                    box = new RectangleShape();
                    box.setPosition(30+40*(i+1)+playerWidth*i,WORLD_SIZE.y / 2 - backgroundYMovement + 100);
                    box.setSize(new Vector2f(playerWidth,playerInfoSize));
                    box.setFillColor(new Color(90, 0, 0, 240));
                    window.draw(box);

                    Player player = players[i];
                    int number = player.getNumber().ordinal() + 1;

                    Score score = player.getScore();
                    score.roundValues();

                    if(playerInfoSize == 700) {
                        Text text = new Text("Player " + number, ContentManager.instance.getFont("Ubuntu"), 64);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 110);
                        window.draw(text);

                        text = new Text("Score: "+ score.getScore(), ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 190);
                        window.draw(text);

                        text = new Text("Asteroids Destroyed: " +score.getAsteroids(), ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 230);
                        window.draw(text);

                        text = new Text("Enemies Killed: " + score.getEnemies(), ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 270);
                        window.draw(text);

                        text = new Text("Time Survived: " + score.getTimeAlive() +"s", ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 310);
                        window.draw(text);

                        text = new Text("Bullets Fired: " + score.getBulletsFired(), ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 350);
                        window.draw(text);

                        text = new Text("Bullets Missed: " + score.getBulletsMissed(), ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 390);
                        window.draw(text);

                        text = new Text("Accuracy: " + score.getAccuracy() + "%", ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 430);
                        window.draw(text);

                        text = new Text("Time Spent Boosting: " + score.getTimeBoosting() + "s", ContentManager.instance.getFont("Ubuntu"), font);
                        text.setOrigin(0, 0);
                        text.setPosition(30 + 40 * (i + 1) + playerWidth * i + 10, WORLD_SIZE.y / 2 - backgroundYMovement + 470);
                        window.draw(text);
                    }
                }
                break;
        }
    }

    @Override
    public void dispose() {

    }
}
