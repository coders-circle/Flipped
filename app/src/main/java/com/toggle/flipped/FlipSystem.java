package com.toggle.flipped;


import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;

/*import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;*/

public class FlipSystem extends com.toggle.katana2d.System implements ContactListener {

    public static class Mirror {
        public String nextWorld;
    }

    // Flip-items are items that are processed by this system.
    // Example: Mirror, HyperObjects
    public static class FlipItem implements Component {
        public enum FlipItemType { MIRROR, HYPEROBJECT }

        public FlipItem(FlipItemType type) { this.type = type; }

        public FlipItemType type;
        public Object data;
    }

    private Bot mBotComponent;   // Bot component of player, consists the ground fixture to test collision with
    // private static Camera mCamera;
    private Level mLevel;

    public FlipSystem(/*Camera camera, */Level level) {
        super(new Class[] {FlipItem.class, PhysicsBody.class});
        // mCamera = camera;
        mLevel = level;
    }

    public void setPlayer(Entity player) {
        mBotComponent = player.get(Bot.class);
    }

    @Override
    public void onEntityAdded(Entity entity) {
        entity.get(PhysicsBody.class).contactListener = this;
    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        FlipItem f = ((Entity)me.getUserData()).get(FlipItem.class);
        if (f.type == FlipItem.FlipItemType.MIRROR) {
            if (other == mBotComponent.groundFixture) {
                Mirror data = (Mirror)f.data;
                mLevel.changeWorld(data.nextWorld);
            }
        }
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
