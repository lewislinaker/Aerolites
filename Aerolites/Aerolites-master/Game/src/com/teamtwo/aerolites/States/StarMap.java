package com.teamtwo.aerolites.States;

import com.teamtwo.aerolites.Utilities.InputType;
import com.teamtwo.aerolites.Utilities.LevelConfig;
import com.teamtwo.aerolites.Utilities.LevelOverMessage;
import com.teamtwo.aerolites.Utilities.Score;
import com.teamtwo.engine.Input.Controllers.*;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Observer;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.MathUtil;
import com.teamtwo.engine.Utilities.State.GameStateManager;
import com.teamtwo.engine.Utilities.State.State;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;

import java.util.ArrayList;

public class StarMap extends State implements Observer {

    private class Node {
        private int phase;
        private LevelConfig.Difficulty difficulty;
        private CircleShape display;

        private Node(LevelConfig.Difficulty difficulty, Vector2f position, int phase) {
            this.difficulty = difficulty;
            display = new CircleShape(starSize, 4);
            display.setOrigin(starSize, starSize);
            display.setPosition(position.x + MathUtil.randomFloat(-30,70), position.y + MathUtil.randomFloat(-70,70));

            this.phase = phase;


            switch (difficulty) {
                case Easy:
                    display.setFillColor(new Color(93, 249, 72));
                    break;
                case Medium:
                    display.setFillColor(new Color(244, 131, 66));
                    break;
                case Hard:
                    display.setFillColor(new Color(244, 75, 66));
                    break;
            }
        }
    }

    private InputType[] players;
    private Score[] scores;

    private RectangleShape background;

    private Node current;
    private int lastPhase;
    private CircleShape selection;
    private boolean complete;

    private boolean prevPress;
    private ControllerState prevState;

    private ArrayList<Node> prevNodes;

    private InputType defaultInput;

    private boolean firstLevel;

    private boolean generated;
    private ArrayList<Node> stars;
    private float starSize;
    private boolean escape;
    private boolean circle;

    private Font font;

    private LevelConfig config;

    private boolean gameComplete;

    public StarMap(GameStateManager gsm, InputType[] players) {
        super(gsm);

        font = ContentManager.instance.loadFont("Ubuntu", "Ubuntu.ttf");

        firstLevel = true;
        scores = null;

        background = new RectangleShape(WORLD_SIZE);
        background.setPosition(Vector2f.ZERO);
        background.setTexture(ContentManager.instance.getTexture("Space"));

        this.players = players;
        defaultInput = players[0];

        stars = new ArrayList<>();
        prevNodes = new ArrayList<>();
        generated = false;
        lastPhase = -1;

        complete = false;

        prevState = Controllers.getState(PlayerNumber.One);

        generate();


        selection = new CircleShape(starSize * 2, 4);
        selection.setPosition(current.display.getPosition());
        selection.setFillColor(Color.TRANSPARENT);
        selection.setOutlineColor(Color.WHITE);
        selection.setOutlineThickness(5f);
        selection.setOrigin(starSize * 2, starSize * 2);
        gameComplete = false;
    }

    private void generate() {
        //if(generated) return;
      //  generated = true;

        int hardCount = 2;

        starSize = 16;

        stars.clear();

        stars.add(
                new Node(LevelConfig.Difficulty.Easy, new Vector2f(80, WORLD_SIZE.y / 2f), 0)
        );

        int difMin = 0, difMax = 2;

        int starCount = 4;
        for(int pass = 0; pass < 3; pass++) {
            switch (pass) {
                case 1:
                case 2:
                    difMin = 0;
                    difMax = 3;
                    break;
            }

            if(starCount == 2) starCount = 4;
            starCount = MathUtil.randomInt(2, starCount);
            for(int i = 0; i < starCount; i++) {
                LevelConfig.Difficulty difficulty = LevelConfig.Difficulty.values()[MathUtil.randomInt(difMin, difMax)];
                Vector2f position = new Vector2f(
                        (WORLD_SIZE.x / 4f) * (pass + 1),
                        (WORLD_SIZE.y / (float) (starCount + 1)) * (i + 1)
                );

                if(hardCount == 0) {
                    difficulty = LevelConfig.Difficulty.values()[MathUtil.randomInt(difMin, 2)];
                }
                else if(difficulty == LevelConfig.Difficulty.Hard) {
                    hardCount--;
                }

                stars.add(new Node(difficulty, position, pass + 1));
            }
        }

        stars.add(
                new Node(LevelConfig.Difficulty.Hard, new Vector2f(WORLD_SIZE.x -
                        80 - (starSize * 2),  WORLD_SIZE.y / 2f), 4)
        );

        current = stars.get(0);

    }

    public void update(float dt) {
        if(gameComplete) {
            gsm.setState(new GameOver(gsm, scores));
        }



        ControllerState state = Controllers.getState(PlayerNumber.One);

        if(defaultInput == InputType.Controller) {
            if (!state.button(Button.A) && prevState.button(Button.A) && !complete) {
                reset(current.difficulty);
                lastPhase = current.phase;

                PlayState playState;
                if(firstLevel) {
                    playState = new PlayState(gsm, config);
                }
                else {
                    playState = new PlayState(gsm, config, scores);
                }

                playState.registerObserver(this, Message.Type.LevelOver);
                gsm.addState(playState);
            }

            if(state.thumbstick(Thumbstick.Left).x > 50
                    && prevState.thumbstick(Thumbstick.Left).x < 50 && complete) {

                complete = false;
                for(Node node : stars) {
                    if(node.phase == current.phase + 1) {
                        prevNodes.add(current);
                        current.display.setRotation(0);
                        current = node;
                        break;
                    }
                }
            }

            if(state.thumbstick(Thumbstick.Left).y < -50
                    && prevState.thumbstick(Thumbstick.Left).y > -50 && !complete) {

                for(Node node : stars) {
                    if(node.phase == current.phase) {
                        int indexNode = stars.indexOf(node);
                        int indexCur = stars.indexOf(current);
                        if(indexNode == indexCur - 1) {
                            current.display.setRotation(0);
                            current = node;
                            break;
                        }
                    }
                }
            }
            else if(state.thumbstick(Thumbstick.Left).y > 50
                    && prevState.thumbstick(Thumbstick.Left).y < 50 && !complete) {

                for(Node node : stars) {
                    if(node.phase == current.phase) {
                        int indexNode = stars.indexOf(node);
                        int indexCur = stars.indexOf(current);

                        if(indexNode == indexCur + 1) {
                            current.display.setRotation(0);
                            current = node;
                            break;
                        }
                    }
                }
            }

            if(!state.button(Button.B) && circle) {
                gsm.popState();
            }
        }
        else {

            mouse = Mouse.getPosition(window);
            boolean pressed = Mouse.isButtonPressed(Mouse.Button.LEFT);
            Vector2f mousePos = window.mapPixelToCoords(mouse);


            //window.setTitle("FPS: " + game.getEngine().getFramerate());

            for (Node node : stars) {
                Vector2f pos = node.display.getPosition();
                pos = Vector2f.sub(pos, mousePos);

                if (MathUtil.lengthSq(pos) <= (starSize * starSize)) {
                    if (pressed && !prevPress && node != current) {
                        if (node.phase == current.phase + 1 && complete) {
                            complete = false;
                            prevNodes.add(current);
                            current.display.setRotation(0);
                            current = node;
                        }
                        else if(node.phase == current.phase && !complete) {
                            current.display.setRotation(0);
                            current = node;
                        }
                    } else if (pressed && !prevPress && node == current && !complete) {
                        prevPress = true;
                        reset(node.difficulty);
                        lastPhase = node.phase;

                        PlayState playState;
                        if(firstLevel) {
                            playState = new PlayState(gsm, config);
                        }
                        else {
                            playState = new PlayState(gsm, config, scores);
                        }

                        playState.registerObserver(this, Message.Type.LevelOver);
                        gsm.addState(playState);
                    }
                }
            }

            if(!Keyboard.isKeyPressed(Keyboard.Key.ESCAPE) && escape) {
                gsm.popState();
            }
        }

        selection.rotate(-35 * dt);
        current.display.rotate(35f * dt);

        escape = Keyboard.isKeyPressed(Keyboard.Key.ESCAPE);
        circle = state.button(Button.B);
        prevPress = Mouse.isButtonPressed(Mouse.Button.LEFT);
        prevState = state;
    }

    public void render() {
        if(gameComplete) return;

        window.draw(background);

        for(int i = 0; i < prevNodes.size(); i++) {
            Vertex[] line = new Vertex[2];
            line[0] = new Vertex(prevNodes.get(i).display.getPosition(), Color.GREEN);

            if(i + 1 < prevNodes.size()) {
                line[1] = new Vertex(prevNodes.get(i + 1).display.getPosition(), Color.GREEN);
            }
            else {
                line[1] = new Vertex(current.display.getPosition(), Color.GREEN);
            }

            window.draw(line, PrimitiveType.LINES);
        }

        for(Node node : stars) {
            if(node == current && complete) {
                int index = stars.indexOf(node) + 1;

                if (index < stars.size()) {
                    Node next = stars.get(index);
                    while (next.phase <= node.phase + 1) {

                        if (next.phase == node.phase) {
                            index++;
                            next = stars.get(index);
                            continue;
                        }

                        Vertex[] line = new Vertex[] {
                                new Vertex(node.display.getPosition()),
                                new Vertex(next.display.getPosition())
                        };

                        window.draw(line, PrimitiveType.LINES);


                        index++;
                        if (index == stars.size()) break;
                        next = stars.get(index);
                    }
                }
            }

            window.draw(node.display);

            Text index = new Text(stars.indexOf(node) + ".", font, 15);
            index.setPosition(Vector2f.add(node.display.getPosition(), new Vector2f(30, 30)));
            window.draw(index);
        }

        selection.setPosition(current.display.getPosition());
        window.draw(selection);
    }

    private void reset(LevelConfig.Difficulty difficulty) {
        config = new LevelConfig();

        config.difficulty = difficulty;
        config.textured = false;
        System.arraycopy(players, 0, config.players, 0, players.length);

    }

    @Override
    public void receiveMessage(Message message) {
        // Just in case
        if(message.getType() == Message.Type.LevelOver) {
            LevelOverMessage levelOver = (LevelOverMessage) message;

            complete = levelOver.isComplete();

            if(complete) {
                System.out.println("Congratulations! You beat the level!");
                firstLevel = false;
                scores = new Score[levelOver.getPlayerCount()];
                for(int i = 0; i < scores.length; i++) {
                    scores[i] = levelOver.getPlayer(i).getScore();
                }
            }
            else {
                System.out.println("Too Bad! Try again");
            }

            if(complete && current == stars.get(stars.size() - 1)) {
                System.out.println("Congrats! you beat the game!");
                gameComplete = true;
            }

        }
    }

    public void dispose() {}
}
