package com.teamtwo.aerolites.Entities.AI;

import com.teamtwo.aerolites.Entities.CollisionMask;
import com.teamtwo.aerolites.Entities.Player;
import com.teamtwo.engine.Messages.Message;
import com.teamtwo.engine.Messages.Types.CollisionMessage;
import com.teamtwo.engine.Physics.BodyConfig;
import com.teamtwo.engine.Physics.Polygon;
import com.teamtwo.engine.Physics.World;
import com.teamtwo.engine.Utilities.ContentManager;
import com.teamtwo.engine.Utilities.MathUtil;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConvexShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

/**
 * @author Matthew Threlfall
 */
public class SwarmerBase extends AI {

    private Player target;
    private float lowestDistance;
    private boolean split;

    public SwarmerBase(World world) {

        BodyConfig config = new BodyConfig();

        config.category = CollisionMask.SWARMER_BASE;
        config.mask = CollisionMask.ALL;

        int x = 0, y = 0, velocityX = 0, velocityY = 0;
        int screenSide = MathUtil.randomInt(0,4);
        switch(screenSide) {
            case 0:
                x = MathUtil.randomInt(0, 1920);
                y = 0;
                velocityX = MathUtil.randomInt(-30,30);
                velocityY = MathUtil.randomInt(10,100);
                break;
            case 1:
                x = MathUtil.randomInt(0, 1920);
                y = 1080;
                velocityX = MathUtil.randomInt(-30,30);
                velocityY = MathUtil.randomInt(-100,-10);
                break;
            case 2:
                x = 0;
                y = MathUtil.randomInt(0, 1080);
                velocityX = MathUtil.randomInt(10,100);
                velocityY = MathUtil.randomInt(-30,30);
                break;
            case 3:
                x = 1920;
                y = MathUtil.randomInt(0, 1080);
                velocityX = MathUtil.randomInt(-100,-10);
                velocityY = MathUtil.randomInt(-30,30);
                break;
            default:
                System.out.println("WHAT?!");
        }

        config.angularVelocity = MathUtil.randomFloat(0, MathUtil.PI / 4f);
        config.position = new Vector2f(x,y);
        config.velocity = new Vector2f(velocityX,velocityY);

        config.shape = new Polygon(MathUtil.randomFloat(40,45));

        body = world.createBody(config);
        body.setData(this);
        body.registerObserver(this, Message.Type.Collision);

        onScreen = true;
        split = false;

        display = new ConvexShape(body.getShape().getVertices());
        display.setFillColor(Color.WHITE);
        display.setTexture(ContentManager.instance.getTexture("Asteroid"));
    }

    @Override
    public void update(float dt){
        super.update(dt);
        if(target != null && lowestDistance < MathUtil.square(500)) {
            split = true;
        }
    }
    @Override
    public void render(RenderWindow renderer) {

        display.setPosition(body.getTransform().getPosition());
        display.setRotation(body.getTransform().getAngle() * MathUtil.RAD_TO_DEG);

        renderer.draw(display);
    }

    @Override
    public void receiveMessage(Message message) {
        if(message.getType() == Message.Type.Collision) {
            CollisionMessage cm = (CollisionMessage) message;
            if(cm.getBodyB().getData().getType() == Type.Bullet) {
                split = true;
            }
            else if(cm.getBodyA().getData().getType() == Type.Bullet) {
                split = true;
            }
        }
    }

    public void findTarget(Player[] players) {
        lowestDistance = Float.MAX_VALUE;
        for(Player player : players) {
            if(!player.isAlive()) continue;

            Vector2f position = body.getTransform().getPosition();
            Vector2f playerPos = player.getBody().getTransform().getPosition();

            float distanceTo = MathUtil.lengthSq(Vector2f.sub(playerPos, position));
            if (distanceTo < lowestDistance) {
                lowestDistance = distanceTo;
                target = player;
            }
        }
    }

    @Override
    public Type getType() { return Type.SwamerBase; }

    public boolean shouldSplit() { return split; }
}
