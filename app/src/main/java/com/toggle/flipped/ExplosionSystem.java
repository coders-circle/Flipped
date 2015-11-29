package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;

public class ExplosionSystem extends com.toggle.katana2d.System {

    public static class Explosive implements Component {
        public List<Body> particles = new ArrayList<>();    // air particles that are "exploded"
        public int numParticles = 32;
        public float lifeSpan = 5;      // 5 seconds
        public boolean isExploding = false;
        public float blastPower = 30;
        public float densityPerParticle = 80/numParticles;
        public int numRays = 32;
    }

    private World mWorld;
    private Game mGame;
    public ExplosionSystem(World world, Game game) {
        super(new Class[]{Explosive.class, Transformation.class});
        mWorld = world;
        mGame = game;
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Explosive e = entity.get(Explosive.class);
            Transformation t = entity.get(Transformation.class);

            if (e.isExploding) {
                e.lifeSpan -= dt;
                if (e.lifeSpan <= 0) {
                    // kill the particles
                    for (Body b: e.particles)
                        b.getWorld().destroyBody(b);

                    // reset the explosive
                    e.particles.clear();
                    e.isExploding = false;
                    e.lifeSpan = 5;
                }
                else if (e.particles.size() == 0) {
                    // create the particles
                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.density = e.densityPerParticle;
                    fixtureDef.friction = 0.0f;
                    fixtureDef.restitution = 0.99f; // high restitution
                    fixtureDef.filter.groupIndex = -1;  // particles never collide with each other
                    fixtureDef.shape = new CircleShape();
                    fixtureDef.shape.setRadius(3 * PhysicsSystem.METERS_PER_PIXEL);

                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.DynamicBody;
                    bodyDef.position.x = t.x * PhysicsSystem.METERS_PER_PIXEL;
                    bodyDef.position.y = t.y * PhysicsSystem.METERS_PER_PIXEL;
                    bodyDef.fixedRotation = true;
                    bodyDef.bullet = true;
                    //bodyDef.linearDamping = 3.5f;

                    float randomAngle = (float)Math.random() * 90;
                    for (int i=0; i<e.numParticles; ++i) {
                        float angle = (float)Math.toRadians((float)i/e.numRays * 360) + randomAngle;

                        Vector2 direction = new Vector2((float)Math.sin(angle), (float)Math.cos(angle));
                        bodyDef.linearVelocity.x = e.blastPower * direction.x;
                        bodyDef.linearVelocity.y = e.blastPower * direction.y;

                        Body b = mWorld.createBody(bodyDef);
                        b.createFixture(fixtureDef);
                        e.particles.add(b);
                    }
                }
            }
            else {  // if not exploding then explode at touch
                TouchInputData inputData = mGame.getTouchInputData();
                Camera camera = mGame.getRenderer().getCamera();
                if (inputData.pointers.size() > 0) {
                    for (int i=0; i<inputData.pointers.size(); ++i) {
                        TouchInputData.Pointer p = inputData.pointers.valueAt(i);

                        if (new Vector2(p.x+camera.x-t.x, p.y+camera.y-t.y).len() < 10) {
                            e.isExploding = true;
                            break;
                        }
                    }
                }
            }
        }
    }
}
