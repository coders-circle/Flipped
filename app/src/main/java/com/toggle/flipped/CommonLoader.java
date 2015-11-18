package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.json.JSONObject;

import java.util.List;

public class CommonLoader implements CustomLoader {
    @Override
    public boolean loadSprite(Game game, String spriteName, JSONObject sprite) {
        spriteName = spriteName.toLowerCase();
        switch (spriteName) {
            case "player":
                return true;
        }
        return false;
    }

    @Override
    public boolean loadEntity(Scene scene, World world, String entityName, JSONObject entity) {
        try {
            entityName = entityName.toLowerCase();
            JSONObject components = entity.getJSONObject("components");
            switch (entityName) {
                case "player":
                    JSONObject transformation = components.getJSONObject("Transformation");
                    Entity player = new BotCreator(scene.getGame(), world).createBot("player",
                            (float) transformation.getDouble("Translate-X"),
                            (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle"));
                    player.add(new Player());
                    scene.addEntity(player);
                    return true;

                case "ground":
                    Entity ground = new Entity();
                    Shape shape = createChainShape(components.getJSONObject("Path").getString("Points"));
                    ground.add(new Transformation(0, 0, 0));
                    ground.add(new PhysicsBody(world, BodyType.STATIC, ground, shape, new PhysicsBody.Properties(0)));
                    scene.addEntity(ground);
                    return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static Shape createChainShape(String points) {
        ChainShape shape = new ChainShape();
        List<Vec2> vertices = Utilities.parsePoints(points, 0, 0);
        shape.createChain(vertices.toArray(new Vec2[vertices.size()]), vertices.size());
        return shape;
    }
}
