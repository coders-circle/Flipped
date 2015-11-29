package com.toggle.flipped;


import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;

public class FlipSystem extends com.toggle.katana2d.System implements ContactListener {


    // Flip-items are items that are processed by this system.
    // Example: Mirror, HyperObjects
    public static class Mirror implements Component {

        public Mirror() {}

        public String nextWorld;
        public String exitMirror;
    }

    private Bot mBotComponent;   // Bot component of player, consists the ground fixture to test collision with
    // private static Camera mCamera;
    private Level mLevel;

    public FlipSystem(/*Camera camera, */Level level) {
        super(new Class[] {Mirror.class, PhysicsBody.class});
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
        Mirror f = ((Entity)me.getUserData()).get(Mirror.class);
        if (other == mBotComponent.groundFixture) {
            mLevel.changeWorld(f);
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
