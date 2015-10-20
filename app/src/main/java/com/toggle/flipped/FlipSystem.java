package com.toggle.flipped;

import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;

public class FlipSystem extends com.toggle.katana2d.System {

    // Flip-items are items that are processed by this system.
    // Example: Mirror, HyperObjects
    public static class FlipItem implements Component {
        public enum FlipItemType { MIRROR, HYPEROBJECT }

        public FlipItem(FlipItemType type) { this.type = type; }

        public FlipItemType type;
        public float targetAngle = 180; // target angle for mirror
    }

    private static PlayerControlSystem.Player mPlayerComponent;
    private static Camera mCamera;

    public FlipSystem(Camera camera) {
        super(new Class[] {FlipItem.class});
        mCamera = camera;
    }

    public void setPlayer(Entity player) {
        mPlayerComponent = player.get(PlayerControlSystem.Player.class);
    }

    @Override
    public void update(double dt) {
        for (Entity e: mEntities) {
            FlipItem f = e.get(FlipItem.class);
            PhysicsBody b = e.get(PhysicsBody.class);

            if (f.type == FlipItem.FlipItemType.MIRROR) {
                // For every mirror
                // If current camera angle is not the target angle
                // Then check for collision with player's foot (groundFixture)
                // and set angle to target angle on collision.
                
                if (mCamera.angle != f.targetAngle) {
                    boolean colliding = false;
                    for (PhysicsBody.Collision c : b.collisions) {
                        if (c.otherFixture == mPlayerComponent.groundFixture) {
                            colliding = true;
                            break;
                        }
                    }

                    if (colliding)
                        mCamera.angle = f.targetAngle;
                }
            }
        }
    }
}
