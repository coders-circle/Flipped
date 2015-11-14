package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import java.util.List;

public class WindSystem extends com.toggle.katana2d.System{
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
        shape.setAsBox(md, wd, new Vec2(md, wd), 0);
        wind.sensor = b.createSensor(shape);
    }

    @Override
    public void update(double dt) {
        for (Entity entity: mEntities) {
            WindSource wind = entity.get(WindSource.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            if (wind.active) {
                Vec2 direction = b.body.getWorldVector(new Vec2(1, 0));
                float windforce = wind.force;

                for (PhysicsBody.Collision c : b.collisions) {
                    Body otherBody = c.otherFixture.getBody();
                    if (c.myFixture == wind.sensor && otherBody.getType() != BodyType.STATIC) {
                        // For each non-static body in the wind sensor area, apply some force on it.

                        // Suppose that not all of a body is inside the wind area
                        // Then force needs to be applies in partial area only (this can for e.g. give rotational effect).

                        // Get the boundary points that are inside the wind area
                        List<Vec2> points = PhysicsUtilities.getIntersectionOfFixtures(c.myFixture, c.otherFixture);
                        if (points != null) {
                            // Find area and centroid of the points
                            PhysicsUtilities.CentroidResult result = PhysicsUtilities.getCentroid(points);

                            // Apply force that is proportional to the intersecting area
                            if (result != null && result.area > 0) {
                                float force = windforce * result.area;
                                otherBody.applyForce(direction.mul(force), result.centroid);
                            }
                        }
                    }
                }
            }
        }
    }
}
