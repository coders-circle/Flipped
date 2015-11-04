package com.toggle.flipped;

import com.toggle.katana2d.Entity;

public class EnemyAISystem extends com.toggle.katana2d.System {

    public EnemyAISystem() {
        super(new Class[] { Enemy.class, Bot.class });
    }

    @Override
    public void update(double dt) {
        for (Entity entity: mEntities) {
            Enemy e = entity.get(Enemy.class);
            Bot b = entity.get(Bot.class);

            // First perform according to current state
            /*
            Case idle:
                b.ActionState = Nothing
                b.MotionState = Idle
                (or may be roam around randomly)

            Case chase:
                b.direction = (player.x < this.x) ? left : right
                b.ActionState = Nothing
                b.MotionState = Moving

            Case attack:
                b.direction = (player.x < this.x) ? left : right
                b.ActionState = Fight
                b.MotionState = Idle

                // bot control system will control what will happen while fighting
                // like changing sprite, health of other entity etc.
             */

            // Next update state

            /*
            Case idle:
                // if first entity intersected by raycast from this to player is player itself
                // then we can see the player
                if (raycast_intersect(this.position, player.position) == player)
                    state = chase
            Case chase:
                If (side_sensor_entity == player)
                    state = attack

            Case attack:
                if (side_sensor_entity != player)
                    state = chase
                else if (player.die)
                    state = idle
             */
        }
    }

}
