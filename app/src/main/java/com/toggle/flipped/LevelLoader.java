package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
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

import java.util.Iterator;
import java.util.List;

// A class to handle level loading from json
public class LevelLoader {
    private JSONObject data;
    private CustomLoader mCustomLoader;     // custom loader to handle loading of specific sprites/entities

    public LevelLoader(String json) {
        try {
            data = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load(Game game, CustomLoader customLoader) {
        mCustomLoader = customLoader;
        try {
            // load all sprites
            JSONObject sprites = data.getJSONObject("sprites");
            Iterator<String> keys = sprites.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonSprite = sprites.getJSONObject(key);

                // if custom loader doesn't handle this sprite, just
                // load the sprite with the key as filename
                if (!mCustomLoader.loadSprite(game, key, jsonSprite)) {
                    int spriteTex = Utilities.getResourceId(game.getActivity(), "drawable", key);
                    Texture sprite = game.getRenderer().addTexture(spriteTex, (float) jsonSprite.getDouble("width"), (float) jsonSprite.getDouble("height"));
                    game.textureManager.add(key, sprite);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadWorld(String worldName, Scene scene, World boxWorld) {
        try {
            JSONObject world = data.getJSONObject("worlds").getJSONObject(worldName);
            JSONObject entities = world.getJSONObject("entities");

            // load every entity
            Iterator<String> keys = entities.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonEntity = entities.getJSONObject(key);

                // if custom-loader doesn't handle this entity,
                // load the entity by creating new one and adding components
                if (!mCustomLoader.loadEntity(scene, boxWorld, key, jsonEntity)) {
                    Entity e = new Entity();
                    JSONObject jsonComponents = jsonEntity.getJSONObject("components");

                    Iterator<String> ckeys = jsonComponents.keys();
                    while (ckeys.hasNext()) {
                        String ckey = ckeys.next();
                        addComponent(scene.getGame(), boxWorld, e, ckey, jsonComponents.getJSONObject(ckey), jsonComponents);
                    }

                    scene.addEntity(e);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addComponent(Game game, World world, Entity entity, String compName, JSONObject component, JSONObject components) throws JSONException {
        switch (compName) {
            case "Sprite": {
                float scaleX = 1, scaleY = 1;
                if (components.has("Transformation")) {
                    scaleX = (float) components.getJSONObject("Transformation").getDouble("Scale-X");
                    scaleY = (float) components.getJSONObject("Transformation").getDouble("Scale-Y");
                }
                Sprite sc = new Sprite(game.textureManager.get(component.getString("Sprite")),
                        (float)component.optDouble("Z-Order", 0));
                sc.scaleX = scaleX;
                sc.scaleY = scaleY;
                entity.add(sc);
            }
                break;
            case "Transformation":
                entity.add(new Transformation((float) component.getDouble("Translate-X"),
                        (float) component.getDouble("Translate-Y"), (float) component.getDouble("Angle")));
                break;
            case "RigidBody": {
                // Rigidbody is little complicated,
                // we will need shape data from "Sprite" component, so check if "Sprite" exists
                if (components.has("Sprite")) {

                    // get the shape data and create a shape accordingly
                    JSONObject sprite = data.getJSONObject("sprites").getJSONObject(components.getJSONObject("Sprite").getString("Sprite"));
                    JSONObject jsonShape = sprite.getJSONObject("shape");

                    Shape shape;
                    String type = jsonShape.getString("type");

                    float scaleX = 1, scaleY = 1;
                    if (components.has("Transformation")) {
                        scaleX = (float) components.getJSONObject("Transformation").getDouble("Scale-X");
                        scaleY = (float) components.getJSONObject("Transformation").getDouble("Scale-Y");
                    }

                    // box and circle shapes are easy. polygon is little bit complicated
                    switch (type) {
                        case "box":
                            shape = new PolygonShape();
                            ((PolygonShape) shape).setAsBox(
                                    (float) jsonShape.getDouble("width") / 2 * PhysicsSystem.METERS_PER_PIXEL * scaleX,
                                    (float) jsonShape.getDouble("height") / 2 * PhysicsSystem.METERS_PER_PIXEL * scaleY);
                            break;

                        case "circle":
                            shape = new CircleShape();
                            shape.setRadius((float) jsonShape.getDouble("radius") * PhysicsSystem.METERS_PER_PIXEL * Math.max(scaleX, scaleY));
                            break;

                        default:
                            shape = new PolygonShape();
                            float offsetX = (float) sprite.getDouble("width") / 2;
                            float offsetY = (float) sprite.getDouble("height") / 2;
                            List<Vec2> vertices = Utilities.parsePoints(jsonShape.getString("points"), offsetX, offsetY);
                            Utilities.scale(vertices, scaleX, scaleY);
                            ((PolygonShape) shape).set(vertices.toArray(new Vec2[vertices.size()]), vertices.size());

                            break;

                    }

                    // Finally create the rigid body
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
            }
                break;
        }
    }

}
