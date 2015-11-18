package com.toggle.katana2d.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;
import java.util.List;

public class PhysicsUtilities {
    public static boolean inside(Vec2 cp1, Vec2 cp2, Vec2 p) {
        return (cp2.x - cp1.x)*(p.y - cp1.y) > (cp2.y - cp1.y)*(p.x - cp1.x);
    }

    public static Vec2 intersection(Vec2 cp1, Vec2 cp2, Vec2 s, Vec2 e) {
        Vec2 dc = new Vec2(cp1.x-cp2.x, cp1.y-cp2.y);
        Vec2 dp = new Vec2(s.x-e.x, s.y-e.y);
        float n1 = cp1.x*cp2.y-cp1.y*cp2.x;
        float n2 = s.x*e.y-s.y*e.x;
        float n3 = 1.0f / (dc.x*dp.y-dc.y*dp.x);
        return new Vec2((n1*dp.x - n2*dc.x)*n3, (n1*dp.y-n2*dc.y)*n3);
    }

    public static List<Vec2> getIntersectionOfFixtures(Fixture fA, Fixture fB) {
        if (fA.getShape().getType() != ShapeType.POLYGON ||
                fB.getShape().getType() != ShapeType.POLYGON)
            return null;

        PolygonShape polyA = (PolygonShape)fA.getShape();
        PolygonShape polyB = (PolygonShape)fB.getShape();

        List<Vec2> output = new ArrayList<>();
        for (int i=0; i<polyA.getVertexCount(); ++i)
            output.add(fA.getBody().getWorldPoint(polyA.getVertex(i)));

        List<Vec2> clip = new ArrayList<>();
        for (int i=0; i<polyB.getVertexCount(); ++i)
            clip.add(fB.getBody().getWorldPoint(polyB.getVertex(i)));

        Vec2 cp1 = clip.get(clip.size()-1);
        for (int j=0; j<clip.size(); ++j) {
            Vec2 cp2 = clip.get(j);
            if (output.isEmpty()) {
                return null;
            }
            List<Vec2> input = output;
            output = new ArrayList<>();
            Vec2 s = input.get(input.size()-1);
            for (int i=0; i<input.size(); ++i) {
                Vec2 e = input.get(i);
                if (inside(cp1, cp2, e)) {
                    if (!inside(cp1, cp2, s)) {
                        output.add(intersection(cp1, cp2, s, e));
                    }
                    output.add(e);
                }
                else if (inside(cp1, cp2, s))
                    output.add(intersection(cp1, cp2, s, e));
                s=e;
            }
            cp1=cp2;
        }

        if (output.size() == 0)
            return null;
        return output;
    }

    public static class CentroidResult {
        public Vec2 centroid;
        public float area;
    }

    public static CentroidResult getCentroid(List<Vec2> points) {
        int count = points.size();
        if (count < 3)
            return null;

        Vec2 c = new Vec2(0, 0);
        float area = 0;

        Vec2 pRef = new Vec2(0, 0);
        for (int i=0; i<count; ++i) {
            Vec2 p2 = points.get(i);
            Vec2 p3 = (i+1 < count)?points.get(i+1):points.get(0);

            Vec2 e1 = p2.sub(pRef);
            Vec2 e2 = p3.sub(pRef);

            float D = Vec2.cross(e1, e2);
            float tarea = D * 0.5f;
            area += tarea;

            c = c.add(pRef.add(p2.add(p3)).mul(tarea * inv3));
        }

        if (area > Settings.EPSILON)
            c = c.mul(1.0f/area);
        else
            area = 0;

        CentroidResult result = new CentroidResult();
        result.centroid = c;
        result.area = area;
        return result;
    }

    public final static float inv3 = 1.0f/3.0f;

}
