package com.toggle.flipped;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.util.List;

public class Utilities {
    public static Shape createChainShape(String points) {
        ChainShape shape = new ChainShape();
        List<Vec2> vertices = com.toggle.katana2d.Utilities.parsePoints(points, 0, 0);
        shape.createChain(vertices.toArray(new Vec2[vertices.size()]), vertices.size());
        return shape;
    }
}
