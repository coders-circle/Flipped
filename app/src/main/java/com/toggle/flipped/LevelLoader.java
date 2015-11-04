package com.toggle.flipped;

import android.content.Context;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelLoader {
    private JSONObject data;

    public void load(Game game, String json) {
        try {
            data = new JSONObject(json);

            // load all sprites
            JSONObject sprites = data.getJSONObject("sprites");
            Iterator<String> keys = sprites.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonSprite = sprites.getJSONObject(key);

                Texture spriteTex = game.getRenderer().addTexture(getResourceId(game.getActivity(), key, "drawable"));
                GLSprite sprite = new GLSprite(game.getRenderer(), spriteTex, (float)jsonSprite.getDouble("width"), (float)jsonSprite.getDouble("height"));
                game.spriteManager.add(key, sprite);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadWorld(String worldName, Scene scene, World boxWorld) {
        try {
            JSONObject world = data.getJSONObject("worlds").getJSONObject(worldName);
            JSONObject entities = world.getJSONObject("entities");
            Iterator<String> keys = entities.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonEntity = entities.getJSONObject(key);

                Entity e = new Entity();
                JSONObject jsonComponents = jsonEntity.getJSONObject("components");

                Iterator<String> ckeys = jsonComponents.keys();
                while (ckeys.hasNext()) {
                    String ckey = ckeys.next();
                    addComponent(scene.getGame(), boxWorld, e, ckey, jsonComponents.getJSONObject(ckey), jsonComponents);
                }

                scene.addEntity(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addComponent(Game game, World world, Entity entity, String compName, JSONObject component, JSONObject components) throws JSONException {
        switch (compName) {
            case "Sprite":
                entity.add(new Sprite(game.spriteManager.get(component.getString("Sprite"))));
                break;
            case "Transformation":
                entity.add(new Transformation((float) component.getDouble("Translate-X"),
                        (float) component.getDouble("Translate-Y"), (float) component.getDouble("Angle")));
                break;
            case "RigidBody":
                if (components.has("Sprite")) {
                    JSONObject jsonShape =
                            data.getJSONObject("sprites").getJSONObject(components.getJSONObject("Sprite").getString("Sprite"))
                                    .getJSONObject("shape");

                    Shape shape;
                    String type = jsonShape.getString("type");
                    switch (type) {
                        case "box":
                            shape = new PolygonShape();
                            ((PolygonShape) shape).setAsBox((float) jsonShape.getDouble("width") / 2 * PhysicsSystem.METERS_PER_PIXEL,
                                    (float) jsonShape.getDouble("height") / 2 * PhysicsSystem.METERS_PER_PIXEL);
                            break;
                        case "circle":
                            shape = new CircleShape();
                            shape.setRadius((float) jsonShape.getDouble("radius") * PhysicsSystem.METERS_PER_PIXEL);
                            break;
                        default:
                            shape = new PolygonShape();

                            Pattern pattern = Pattern.compile("(\\d|\\.)+,\\s*(\\d|\\.)+");
                            Matcher matcher = pattern.matcher(jsonShape.getString("points"));

                            List<Vec2> vertices = new ArrayList<>();
                            while (matcher.find()) {
                                float x = Float.parseFloat(matcher.group(1)) * PhysicsSystem.METERS_PER_PIXEL;
                                float y = Float.parseFloat(matcher.group(2)) * PhysicsSystem.METERS_PER_PIXEL;
                                vertices.add(new Vec2(x, y));
                            }

                            ((PolygonShape) shape).set((Vec2[]) vertices.toArray(), vertices.size());
                            break;
                    }

                    BodyType bodyType;
                    if (component.getString("Type").equals("Static"))
                        bodyType = BodyType.STATIC;
                    else if (component.getString("Type").equals("Dynamic"))
                        bodyType = BodyType.DYNAMIC;
                    else
                        bodyType = BodyType.KINEMATIC;

                    entity.add(new PhysicsBody(world, bodyType, entity, shape, new PhysicsBody.Properties(
                            (float) component.getDouble("Density"), (float) component.getDouble("Friction"), (float) component.getDouble("Restitution")
                    )));
                }
                break;
        }
    }

    public static int getResourceId(Context context, String pVariableName, String pResourcename)
    {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
