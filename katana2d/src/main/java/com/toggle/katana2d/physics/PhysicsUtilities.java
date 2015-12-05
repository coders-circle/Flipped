package com.toggle.katana2d.physics;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhysicsUtilities {
    public static boolean inside(Vector2 cp1, Vector2 cp2, Vector2 p) {
        return (cp2.x - cp1.x)*(p.y - cp1.y) > (cp2.y - cp1.y)*(p.x - cp1.x);
    }

    public static Vector2 intersection(Vector2 cp1, Vector2 cp2, Vector2 s, Vector2 e) {
        Vector2 dc = new Vector2(cp1.x-cp2.x, cp1.y-cp2.y);
        Vector2 dp = new Vector2(s.x-e.x, s.y-e.y);
        float n1 = cp1.x*cp2.y-cp1.y*cp2.x;
        float n2 = s.x*e.y-s.y*e.x;
        float n3 = 1.0f / (dc.x*dp.y-dc.y*dp.x);
        return new Vector2((n1*dp.x - n2*dc.x)*n3, (n1*dp.y-n2*dc.y)*n3);
    }

    public static List<Vector2> getIntersectionOfFixtures(Fixture fA, Fixture fB) {
        if (fA.getShape().getType() != Shape.Type.Polygon ||
                fB.getShape().getType() != Shape.Type.Polygon)
            return null;

        PolygonShape polyA = (PolygonShape)fA.getShape();
        PolygonShape polyB = (PolygonShape)fB.getShape();

        List<Vector2> output = new ArrayList<>();
        for (int i=0; i<polyA.getVertexCount(); ++i) {
            Vector2 v = new Vector2();
            polyA.getVertex(i, v);
            output.add(fA.getBody().getWorldPoint(v));
        }

        List<Vector2> clip = new ArrayList<>();
        for (int i=0; i<polyB.getVertexCount(); ++i) {
            Vector2 v = new Vector2();
            polyB.getVertex(i, v);
            clip.add(fB.getBody().getWorldPoint(v));
        }

        Vector2 cp1 = clip.get(clip.size()-1);
        for (int j=0; j<clip.size(); ++j) {
            Vector2 cp2 = clip.get(j);
            if (output.isEmpty()) {
                return null;
            }
            List<Vector2> input = output;
            output = new ArrayList<>();
            Vector2 s = input.get(input.size()-1);
            for (int i=0; i<input.size(); ++i) {
                Vector2 e = input.get(i);
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

    public static List<Vector2> parsePoints(String string, boolean meters, float offsetX, float offsetY) {
        // parse the points and create vertices, also convert from pixels to meters if necessary
        // TODO: This needs to be verified

        // Regex matching to get every x, y
        Pattern pattern = Pattern.compile("(\\-?\\d+\\.?\\d*),?\\s*(\\-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(string);

        List<Vector2> vertices = new ArrayList<>();
        while (matcher.find()) {
            float x = Float.parseFloat(matcher.group(1)) - offsetX;
            float y = Float.parseFloat(matcher.group(2)) - offsetY;

            if (meters) {
                x *= PhysicsSystem.METERS_PER_PIXEL;
                y *= PhysicsSystem.METERS_PER_PIXEL;
            }

            vertices.add(new Vector2(x, y));
        }
        return vertices;
    }

    public static void scale(List<Vector2> points, float scaleX, float scaleY) {
        for (Vector2 p: points) {
            p.x *= scaleX;
            p.y *= scaleY;
        }
    }

    public static class CentroidResult {
        public Vector2 centroid;
        public float area;
    }

    public static CentroidResult getCentroid(List<Vector2> points) {
        int count = points.size();
        if (count < 3)
            return null;

        Vector2 c = new Vector2(0, 0);
        float area = 0;

        Vector2 pRef = new Vector2(0, 0);
        for (int i=0; i<count; ++i) {
            Vector2 p2 = points.get(i);
            Vector2 p3 = (i+1 < count)?points.get(i+1):points.get(0);

            Vector2 e1 = p2.sub(pRef);
            Vector2 e2 = p3.sub(pRef);

            float D = e1.crs(e2);
            float tarea = D * 0.5f;
            area += tarea;

            c = c.add(pRef.add(p2.add(p3))).scl(tarea*inv3);
        }

        if (area > 0.00001f)
            c.scl(1f/area);
        else
            area = 0;

        CentroidResult result = new CentroidResult();
        result.centroid = c;
        result.area = area;
        return result;
    }

    public final static float inv3 = 1.0f/3.0f;

}
