package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.toggle.katana2d.Emitter;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static void createStick(Scene scene, World world, Entity stick, JSONObject components) throws JSONException {
        Game game = scene.getGame();
        stick.add(new Sprite(game.getRenderer().addTexture(new float[]{1, 1, 0, 1}, 16, 4), -1.6f));
        JSONObject transformation = components.getJSONObject("Transformation");
        stick.add(new Transformation((float) transformation.getDouble("Translate-X"),
                (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
        PhysicsBody b = new PhysicsBody(world, BodyDef.BodyType.DynamicBody, stick, new PhysicsBody.Properties(1, 0.2f, 0.1f));
        stick.add(b);

        /*Filter d = b.body.getFixtureList().get(0).getFilterData();
        d.groupIndex = ExplosionSystem.NON_EXPLOSIVE_GROUP;
        b.body.getFixtureList().get(0).setFilterData(d);*/

        PolygonShape fireShape = new PolygonShape();
        fireShape.setAsBox(4 * PhysicsSystem.METERS_PER_PIXEL, 4 * PhysicsSystem.METERS_PER_PIXEL, new Vector2(10 * PhysicsSystem.METERS_PER_PIXEL, 0), 0);
        b.createSensor(fireShape);

        Carriable c = new Carriable();
        c.position = new Vector2(156, 136);
        c.angle = -30;
        for (int i = 0; i < 8; ++i) {
            c.positions.add(new Vector2(1628 - 1500, 256));
            c.angles.add(0f);
        }
        c.positions.add(new Vector2(128, 5128 - 266));
        c.angles.add(10f);
        c.positions.add(new Vector2(128, 5128 - 266));
        c.angles.add(10f);
        c.positions.add(new Vector2(638 - 500, 488 - 266));
        c.angles.add(15f);
        c.positions.add(new Vector2(890 - 750, 474 - 266));
        c.angles.add(10f);
        c.positions.add(new Vector2(1126 - 1000, 456 - 266));
        c.angles.add(-7f);
        c.positions.add(new Vector2(1368 - 1250, 440 - 266));
        c.angles.add(-10f);
        c.positions.add(new Vector2(1368 - 1250, 440 - 266));
        c.angles.add(-20f);
        c.positions.add(new Vector2(1368 - 1250, 440 - 266));
        c.angles.add(-20f);

        c.position.x *= 24f / 143;
        c.position.y *= 48f / 274;
        c.position.x -= 12;
        c.position.y -= 24;

        float xFactor = 48f / 250;
        float yFactor = 48f / 266;

        for (int i = 0; i < 16; ++i) {
            c.positions.get(i).x *= xFactor;
            c.positions.get(i).y *= yFactor;
            c.positions.get(i).x -= 12;
            c.positions.get(i).y -= 24;
        }


        stick.add(c);

        Entity emitter = new Entity();
        emitter.add(new Transformation(0, 0, -90));
        emitter.add(new Emitter(game.getRenderer(), 300, game.getRenderer().mFuzzyTextureId, 3, 100, new float[]{180f / 255, 80f / 255, 10f / 255, 1}, new float[]{0, 0, 0, 0}));
        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 16;
        e.var_size = 5;
        e.var_angle = 70;
        e.speed = 10;
        e.var_speed = 5;
        e.accel_x = 20;
        e.additiveBlend = true;
        e.emissionRate = 0;
        scene.addEntity(emitter);

        Burner burner = new Burner(emitter);
        stick.add(burner);

        burner.source = false;
        burner.fullLife = 12;
    }


    public static void createFire(Scene scene, World world, Entity stick, JSONObject components) throws JSONException {
        Game game = scene.getGame();
        stick.add(new Sprite(game.getRenderer().addTexture(new float[]{1, 1, 0, 1}, 10, 10), -1.6f));
        JSONObject transformation = components.getJSONObject("Transformation");
        stick.add(new Transformation((float) transformation.getDouble("Translate-X"),
                (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
        PhysicsBody b = new PhysicsBody(world, BodyDef.BodyType.StaticBody, stick, new PhysicsBody.Properties(0, 0, 0));
        stick.add(b);

        /*PolygonShape fireShape = new PolygonShape();
        fireShape.setAsBox(24 * PhysicsSystem.METERS_PER_PIXEL, 24 * PhysicsSystem.METERS_PER_PIXEL);
        b.createSensor(fireShape);*/

        Entity emitter = new Entity();
        emitter.add(new Transformation(0, 0, -90));
        emitter.add(new Emitter(game.getRenderer(), 300, game.getRenderer().mFuzzyTextureId, 3, 100, new float[]{180f / 255, 80f / 255, 10f / 255, 1}, new float[]{0, 0, 0, 0}));
        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 20;
        e.var_size = 5;
        e.var_angle = 70;
        e.speed = 10;
        e.var_speed = 5;
        e.accel_x = 20;
        e.additiveBlend = true;
        scene.addEntity(emitter);

        Burner burner = new Burner(emitter);
        stick.add(burner);

        burner.source = true;
        burner.position = 0;
        burner.sourceWidth = 24 * PhysicsSystem.METERS_PER_PIXEL;
        burner.sourceHeight = 24 * PhysicsSystem.METERS_PER_PIXEL;
    }
}
