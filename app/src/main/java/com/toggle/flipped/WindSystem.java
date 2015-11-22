package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

/*import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;*/

import java.util.List;

public class WindSystem extends com.toggle.katana2d.System implements ContactListener {
    public WindSystem() {
        super(new Class[]{WindSource.class, PhysicsBody.class});
    }

    @Override
    public void onEntityAdded(Entity entity) {
        WindSource wind = entity.get(WindSource.class);
        PhysicsBody b = entity.get(PhysicsBody.class);

        // Create a sensor in the required area where the wind needs to blown
        PolygonShape shape = new PolygonShape();
        float md = wind.max_distance/2 * PhysicsSystem.METERS_PER_PIXEL;
        float wd = wind.width/2 * PhysicsSystem.METERS_PER_PIXEL;
        shape.setAsBox(md, wd, new Vector2(md, wd), 0);
        wind.sensor = b.createSensor(shape);

        b.contactListener = this;
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            WindSource wind = entity.get(WindSource.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            if (wind.active) {
                Vector2 direction = b.body.getWorldVector(new Vector2(1, 0));
                float windforce = wind.force;

                for (Fixture otherFixture : wind.bodies) {
                    Body otherBody = otherFixture.getBody();
                    if (otherBody.getType() != BodyDef.BodyType.StaticBody) {
                        // For each non-static body in the wind sensor area, apply some force on it.

                        // Suppose that not all of a body is inside the wind area
                        // Then force needs to be applies in partial area only (this can for e.g. give rotational effect).

                        // Get the boundary points that are inside the wind area
                        List<Vector2> points = PhysicsUtilities.getIntersectionOfFixtures(wind.sensor, otherFixture);
                        if (points != null) {
                            // Find area and centroid of the points
                            PhysicsUtilities.CentroidResult result = PhysicsUtilities.getCentroid(points);

                            // Apply force that is proportional to the intersecting area
                            if (result != null && result.area > 0) {
                                float force = windforce * result.area;
                                otherBody.applyForce(direction.scl(force), result.centroid, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        WindSource source = ((Entity)me.getUserData()).get(WindSource.class);
        if (me == source.sensor)
            source.bodies.add(other);
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
        WindSource source = ((Entity)me.getUserData()).get(WindSource.class);
        if (me == source.sensor)
            source.bodies.remove(other);
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
