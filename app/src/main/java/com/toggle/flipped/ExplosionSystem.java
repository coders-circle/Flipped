package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.*;
import com.toggle.katana2d.physics.ContactListener;

import java.util.ArrayList;
import java.util.List;

public class ExplosionSystem extends com.toggle.katana2d.System implements ContactListener, QueryCallback {

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        Explosive e = ((Entity)me.getUserData()).get(Explosive.class);
        Entity otherEntity = (Entity)other.getUserData();
        if (!me.isSensor() && otherEntity.has(Burner.class) && other.isSensor()) {
            if (!e.destroyed && !e.isExploding && otherEntity.get(Burner.class).isBurning) {
                e.explosionTime = 2;
                e.isExploding = true;
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

    public final static int NON_EXPLOSIVE_GROUP = -1;

    @Override
    public boolean reportFixture(Fixture other) {
        Entity otherEntity = (Entity)other.getUserData();
        if (otherEntity.has(Mover.class)) {
            Mover m = otherEntity.get(Mover.class);
            if (m.type == Mover.Type.ANGULAR) {
                m.start(otherEntity.get(Transformation.class), false);
            }
        }
        return true;
    }

    public static class Explosive implements Component {
        public List<Body> particles = new ArrayList<>();    // air particles that are "exploded"
        public int numParticles = 32;
        public float lifeSpan = 5;      // 5 seconds
        public boolean isExploding = false;
        public float blastPower = 30;
        public float densityPerParticle = 80/numParticles;
        public int numRays = 32;

        public Sprite sprite;

        public boolean destroyed = false;
        public float explosionTime = 0;

        public float explosionWidth, explosionHeight;   // in meters
    }

    private World mWorld;
    private Game mGame;
    public ExplosionSystem(World world, Game game) {
        super(new Class[]{Sprite.class, Explosive.class, Transformation.class, PhysicsBody.class, Emitter.class});
        mWorld = world;
        mGame = game;
    }

    @Override
    public void onEntityAdded(Entity entity) {
        PhysicsBody b = entity.get(PhysicsBody.class);
        b.contactListener = this;
        for (Fixture f : b.body.getFixtureList()) {
            Filter d = f.getFilterData();
            d.groupIndex = NON_EXPLOSIVE_GROUP;
            f.setFilterData(d);
        }
        Explosive e = entity.get(Explosive.class);
        /*
        // add explosion sensor
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(e.sprite.texture.width/2 * PhysicsSystem.METERS_PER_PIXEL, e.sprite.texture.height/2 * PhysicsSystem.METERS_PER_PIXEL);
        b.createSensor(shape);*/
        e.explosionWidth = e.sprite.texture.width * PhysicsSystem.METERS_PER_PIXEL;
        e.explosionHeight = e.sprite.texture.height * PhysicsSystem.METERS_PER_PIXEL;
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Explosive e = entity.get(Explosive.class);
            Transformation t = entity.get(Transformation.class);
            PhysicsBody bd = entity.get(PhysicsBody.class);
            Sprite s = entity.get(Sprite.class);
            Emitter emitter = entity.get(Emitter.class);

            if (e.isExploding && !e.destroyed) {
                bd.body.setType(BodyDef.BodyType.StaticBody);
                bd.body.setLinearVelocity(new Vector2(0, 0));
                bd.body.setTransform(bd.body.getPosition(), (float)Math.toRadians(-90));

                if (e.explosionTime > 0)  {
                    emitter.emitNext = true;
                    e.explosionTime -= dt;
                }
                else {
                    emitter.emitNext = false;
                    s.changeSprite(e.sprite.texture, e.sprite.spriteSheetData);

                    Vector2 pos = bd.body.getPosition();
                    bd.body.getWorld().QueryAABB(this,
                            pos.x-e.explosionWidth/2, pos.y-e.explosionHeight/2,
                            pos.x+e.explosionWidth/2, pos.y+e.explosionHeight/2
                    );

                    for (Fixture f: bd.body.getFixtureList()) {
                        if (!f.isSensor())
                            bd.body.destroyFixture(f);
                    }

                    e.lifeSpan -= dt;
                    if (e.lifeSpan <= 0) {
                        // kill the particles
                        for (Body b : e.particles)
                            b.getWorld().destroyBody(b);

                        /* // reset the explosive
                        e.particles.clear();
                        e.isExploding = false;
                        e.lifeSpan = 5;*/

                        // destroy the explosive
                        e.destroyed = true;
                        e.isExploding = false;

                    } else if (e.particles.size() == 0) {
                        // create the particles
                        FixtureDef fixtureDef = new FixtureDef();
                        fixtureDef.density = e.densityPerParticle;
                        fixtureDef.friction = 0.0f;
                        fixtureDef.restitution = 0.99f; // high restitution
                        fixtureDef.filter.groupIndex = NON_EXPLOSIVE_GROUP;  // particles never collide with each other
                        fixtureDef.shape = new CircleShape();
                        fixtureDef.shape.setRadius(3 * PhysicsSystem.METERS_PER_PIXEL);

                        BodyDef bodyDef = new BodyDef();
                        bodyDef.type = BodyDef.BodyType.DynamicBody;
                        bodyDef.position.x = t.x * PhysicsSystem.METERS_PER_PIXEL;
                        bodyDef.position.y = t.y * PhysicsSystem.METERS_PER_PIXEL;
                        bodyDef.fixedRotation = true;
                        bodyDef.bullet = true;
                        //bodyDef.linearDamping = 3.5f;

                        float randomAngle = (float) Math.random() * 90;
                        for (int i = 0; i < e.numParticles; ++i) {
                            float angle = (float) Math.toRadians((float) i / e.numRays * 360) + randomAngle;

                            Vector2 direction = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
                            bodyDef.linearVelocity.x = e.blastPower * direction.x;
                            bodyDef.linearVelocity.y = e.blastPower * direction.y;

                            Body b = mWorld.createBody(bodyDef);
                            Fixture f = b.createFixture(fixtureDef);
                            b.setUserData(entity);
                            f.setUserData(entity);
                            e.particles.add(b);
                        }
                    }
                }
            }
        }
    }

}
