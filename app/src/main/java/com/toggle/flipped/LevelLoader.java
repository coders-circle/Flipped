package com.toggle.flipped;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// A class to handle level loading from json
public class LevelLoader {
    private JSONObject data;
    private CustomLoader mCustomLoader;     // custom loader to handle loading of specific sprites/entities
    public HashMap<String, Entity> mCurrentEntities = new HashMap<>();  // entities being currently added to a world
    public Scene mCurrentScene;     // scene where entities are currently being added to

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
                if (!mCustomLoader.loadSprite(game, key.toLowerCase(), jsonSprite)) {
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
            mCurrentEntities.clear();
            mCurrentScene = scene;
            JSONObject world = data.getJSONObject("worlds").getJSONObject(worldName);
            JSONObject entities = world.getJSONObject("entities");

            // add each entity
            Iterator<String> keys = entities.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Entity e = new Entity();
                mCurrentEntities.put(key.toLowerCase(), e);
            }

            // load components for entity
            keys = entities.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonEntity = entities.getJSONObject(key);

                // if custom-loader doesn't handle this entity,
                // load the entity by adding components
                Entity e = mCurrentEntities.get(key.toLowerCase());
                if (!mCustomLoader.loadEntity(e, boxWorld, key.toLowerCase(), jsonEntity)) {
                    JSONObject jsonComponents = jsonEntity.getJSONObject("components");

                    Iterator<String> ckeys = jsonComponents.keys();
                    while (ckeys.hasNext()) {
                        String ckey = ckeys.next();

                        // again if custom loader doesn't handle this type of component, then we add it ourself
                        if (!mCustomLoader.addComponent(boxWorld, e, ckey, jsonComponents.getJSONObject(ckey), jsonComponents))
                            addComponent(boxWorld, e, ckey, jsonComponents.getJSONObject(ckey), jsonComponents);
                    }
                }
                scene.addEntity(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addComponent(World world, Entity entity, String compName, JSONObject component, JSONObject components) throws JSONException {
        Game game = mCurrentScene.getGame();
        switch (compName) {
            case "Sprite": {
                float scaleX = 1, scaleY = 1;
                if (components.has("Transformation")) {
                    scaleX = (float) components.getJSONObject("Transformation").getDouble("Scale-X");
                    scaleY = (float) components.getJSONObject("Transformation").getDouble("Scale-Y");
                }
                Sprite sc = new Sprite(game.textureManager.get(component.getString("Sprite")),
                        (float)component.optDouble("Z-Order", -1));
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
                            Vector2 center;
                            String spr = components.getJSONObject("Sprite").getString("Sprite");
                            center = new Vector2(
                                    -(game.textureManager.get(spr).originX-0.5f)*(float)jsonShape.getDouble("width") * scaleX,
                                    -(game.textureManager.get(spr).originY-0.5f)*(float)jsonShape.getDouble("height") * scaleY);

                            shape = new PolygonShape();
                            ((PolygonShape) shape).setAsBox(
                                    (float) jsonShape.getDouble("width") / 2 * PhysicsSystem.METERS_PER_PIXEL * scaleX,
                                    (float) jsonShape.getDouble("height") / 2 * PhysicsSystem.METERS_PER_PIXEL * scaleY,
                                    center.scl(PhysicsSystem.METERS_PER_PIXEL), 0);
                            break;

                        case "circle":
                            shape = new CircleShape();
                            shape.setRadius((float) jsonShape.getDouble("radius") * PhysicsSystem.METERS_PER_PIXEL * Math.max(scaleX, scaleY));
                            break;

                        default:
                            shape = new ChainShape();
                            spr = components.getJSONObject("Sprite").getString("Sprite");
                            center = new Vector2(
                                    (game.textureManager.get(spr).originX-0.5f)*(float)sprite.getDouble("width"),
                                    (game.textureManager.get(spr).originY-0.5f)*(float)sprite.getDouble("height"));


                            float offsetX = (float) sprite.getDouble("width") / 2 + center.x;
                            float offsetY = (float) sprite.getDouble("height") / 2 + center.y;
                            List<Vector2> vertices = PhysicsUtilities.parsePoints(jsonShape.getString("points"), true, offsetX, offsetY);
                            vertices.add(new Vector2(vertices.get(0)));
                            PhysicsUtilities.scale(vertices, scaleX, scaleY);
                            ((ChainShape) shape).createChain(vertices.toArray(new Vector2[vertices.size()]));

                            break;

                    }

                    // Finally create the rigid body
                    BodyDef.BodyType bodyType;
                    if (component.getString("Type").equals("Static"))
                        bodyType = BodyDef.BodyType.StaticBody;
                    else if (component.getString("Type").equals("Dynamic"))
                        bodyType = BodyDef.BodyType.DynamicBody;
                    else
                        bodyType = BodyDef.BodyType.KinematicBody;

                    entity.add(new PhysicsBody(world, bodyType, entity, shape, new PhysicsBody.Properties(
                            (float) component.getDouble("Density"), (float) component.getDouble("Friction"), (float) component.getDouble("Restitution")
                    )));
                }
            }
            break;
        }
    }

}
