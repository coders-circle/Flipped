package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;

public class MoverSystem extends com.toggle.katana2d.System {
    public MoverSystem() {
        super(new Class[]{Mover.class, PhysicsBody.class, Transformation.class});
    }

    @Override
    public void update(float dt) {
        for (Entity e: mEntities) {
            PhysicsBody b = e.get(PhysicsBody.class);
            Transformation t = e.get(Transformation.class);
            Mover m = e.get(Mover.class);

            if (m.moving) {

                float fx = m.finalX, fy = m.finalY, fangle = m.finalAngle;
                if (m.reverse) {
                    fx = m.initialX; fy = m.initialY; fangle = m.initialAngle;
                }

                if (m.type == Mover.Type.LINEAR) {
                    if (Math.abs(t.x - fx) < 8 && Math.abs(t.y - fy) < 8) {
                        Bot bot = m.player.get(Bot.class);
                        if (bot.cameraPos != null && bot.cameraPos.x == fx && bot.cameraPos.y == fy)
                            bot.cameraPos = null;
                        b.body.setLinearVelocity(new Vector2(0, 0));
                        m.moving = false;
                    } else {
                        Bot bot = m.player.get(Bot.class);
                        bot.cameraPos = new Vector2(fx, fy);
                        b.body.setLinearVelocity(m.lVelocity);
                    }
                } else if (m.type == Mover.Type.ANGULAR) {
                    if (Math.abs(t.angle - fangle) < 3) {
                        Bot bot = m.player.get(Bot.class);
                        if (bot.cameraPos != null && bot.cameraPos.x == t.x && bot.cameraPos.y == t.y)
                            bot.cameraPos = null;
                        b.body.setAngularVelocity(0);
                        m.moving = false;
                    } else {
                        Bot bot = m.player.get(Bot.class);
                        bot.cameraPos = new Vector2(t.x, t.y);
                        b.body.setAngularVelocity(m.aVelocity);
                    }
                }
            }

        }
    }
}
