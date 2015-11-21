package com.toggle.flipped;

import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static Shape createChainShape(String points) {
        ChainShape shape = new ChainShape();
        List<Vec2> vertices = com.toggle.katana2d.Utilities.parsePoints(points, true, 0, 0);
        shape.createChain(vertices.toArray(new Vec2[vertices.size()]), vertices.size());
        return shape;
    }

    public static Vec2 getCenter(Vec2 p0, Vec2 p1) {
        return new Vec2((p0.x+p1.x)/2, (p0.y+p1.y)/2);
    }

    public static List<Vec2> getPoints(List<Vec2> path, float length, boolean meters) {
        float factor = 1;
        if (meters)
            factor = PhysicsSystem.METERS_PER_PIXEL;

        length *= factor;
        List<Vec2> result = new ArrayList<>();
        result.add(path.get(0).mul(factor));

        for (int i=0; i<path.size()-1; ++i) {
            Vec2 p0 = result.get(result.size() - 1);
            Vec2 p1 = path.get(i+1).mul(factor);

            float dx = p1.x - p0.x;
            float dy = p1.y - p0.y;

            float d = (float)Math.sqrt(dx * dx + dy * dy);
            float np = (float)Math.ceil(d / length);

            float stepX = dx / np;
            float stepY = dy / np;

            float x = p0.x;
            float y = p0.y;
            for (int j=0; j<np; ++j) {
                x += stepX;
                y += stepY;
                result.add(new Vec2(x, y));
            }
        }

        return result;
    }
}
