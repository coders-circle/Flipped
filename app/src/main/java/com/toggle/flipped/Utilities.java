package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static Shape createChainShape(String points) {
        ChainShape shape = new ChainShape();
        List<Vector2> vertices = PhysicsUtilities.parsePoints(points, true, 0, 0);
        shape.createChain(vertices.toArray(new Vector2[vertices.size()]));
        return shape;
    }

    public static Vector2 getCenter(Vector2 p0, Vector2 p1) {
        return new Vector2((p0.x+p1.x)/2, (p0.y+p1.y)/2);
    }

    public static List<Vector2> getPoints(List<Vector2> path, float length, boolean meters) {
        float factor = 1;
        if (meters)
            factor = PhysicsSystem.METERS_PER_PIXEL;

        length *= factor;
        List<Vector2> result = new ArrayList<>();
        result.add(path.get(0).scl(factor));

        for (int i=0; i<path.size()-1; ++i) {
            Vector2 p0 = result.get(result.size() - 1);
            Vector2 p1 = path.get(i+1).scl(factor);

            float dx = p1.x - p0.x;
            float dy = p1.y - p0.y;

            float d = (float)Math.sqrt(dx * dx + dy * dy);
            int np = (int)Math.floor(d / length);

            float stepX = dx / np;
            float stepY = dy / np;

            float x = p0.x;
            float y = p0.y;
            for (int j=0; j<np; ++j) {
                x += stepX;
                y += stepY;
                result.add(new Vector2(x, y));
            }
        }

        return result;
    }
}
