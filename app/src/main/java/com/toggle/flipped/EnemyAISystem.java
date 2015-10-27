package com.toggle.flipped;

import com.toggle.katana2d.Entity;

public class EnemyAISystem extends com.toggle.katana2d.System {

    public EnemyAISystem() {
        super(new Class[] { Enemy.class, Bot.class });
    }

    @Override
    public void update(double dt) {
        for (Entity e: mEntities) {
            Enemy p = e.get(Enemy.class);
            Bot b = e.get(Bot.class);

            // use AI to update bot 'b'
        }
    }

}
