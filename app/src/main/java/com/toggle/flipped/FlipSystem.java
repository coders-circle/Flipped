package com.toggle.flipped;

import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;

public class FlipSystem extends com.toggle.katana2d.System {

    public static class Mirror {
        public int nextWorldId;
    }

    // Flip-items are items that are processed by this system.
    // Example: Mirror, HyperObjects
    public static class FlipItem implements Component {
        public enum FlipItemType { MIRROR, HYPEROBJECT }

        public FlipItem(FlipItemType type) { this.type = type; }

        public FlipItemType type;
        public Object data;
    }

    private static Bot mBotComponent;   // Bot component of player, consists the ground fixture to test collision with
    // private static Camera mCamera;
    private Level mLevel;

    public FlipSystem(/*Camera camera, */Level level) {
        super(new Class[] {FlipItem.class});
        // mCamera = camera;
        mLevel = level;
    }

    public void setPlayer(Entity player) {
        mBotComponent = player.get(Bot.class);
    }

    @Override
    public void update(float dt) {
        for (Entity e: mEntities) {
            FlipItem f = e.get(FlipItem.class);
            PhysicsBody b = e.get(PhysicsBody.class);

            if (f.type == FlipItem.FlipItemType.MIRROR) {
                // For every mirror, check collision with player and change world
                boolean colliding = false;
                for (PhysicsBody.Collision c : b.collisions) {
                    if (c.otherFixture == mBotComponent.groundFixture) {
                        colliding = true;
                        break;
                    }
                }

                if (colliding)
                {
                    Mirror data = (Mirror)f.data;
                    mLevel.changeWorld(data.nextWorldId);
                }
                    //mCamera.angle = f.targetAngle;
            }
        }
    }
}
