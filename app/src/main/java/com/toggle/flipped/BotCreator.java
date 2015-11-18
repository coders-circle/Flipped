package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.json.JSONException;
import org.json.JSONObject;


// Create a bot from corresponding json file
// The json file is expected to be in the 'raw' directory
// and must have filename in the form bot_<bot_type>.json.
public class BotCreator {
    private Game mGame;
    private World mWorld;

    public BotCreator(Game game, World world) {
        mGame = game;
        mWorld = world;
    }

    // Get the sprite from a json sprite data
    // If the sprite already exists, return it, else create new and return it.
    public GLSprite getSprite(JSONObject sprite) throws JSONException {
        String spriteName = sprite.getString("file");
        if (mGame.spriteManager.has(spriteName))
            return mGame.spriteManager.get(spriteName);

        Texture spriteTex = mGame.getRenderer().addTexture(Utilities.getResourceId(mGame.getActivity(), "drawable", spriteName));
        GLSprite glSprite = new GLSprite(mGame.getRenderer(), spriteTex, (float) sprite.getDouble("width"), (float) sprite.getDouble("height"));
        mGame.spriteManager.add(spriteName, glSprite);
        return glSprite;
    }

    // Get sprite sheet data from json data
    public Sprite.SpriteSheetData getSpriteSheet(JSONObject sheet) throws JSONException {
        Sprite.SpriteSheetData sheetData = new Sprite.SpriteSheetData();
        sheetData.index = sheet.optInt("index", 0);
        sheetData.numCols = sheet.optInt("cols", 5);
        sheetData.numRows = sheet.optInt("rows", 2);
        sheetData.numImages = sheet.optInt("images", sheetData.numRows * sheetData.numCols);

        float wref = (float)sheet.optDouble("width-ref", 1);
        float href = (float)sheet.optDouble("height-ref", 1);

        if (sheet.has("width"))
            sheetData.imgWidth = (float)sheet.getDouble("width") / wref;
        else
            sheetData.imgWidth = 1f/sheetData.numCols;

        if (sheet.has("height"))
            sheetData.imgHeight = (float)sheet.getDouble("height") / href;
        else
            sheetData.imgHeight = 1f/sheetData.numRows;

        return sheetData;
    }

    // Create a new bot of type 'type'
    // The bot_'type'.json file must exists;
    public Entity createBot(String type, float x, float y, float angle) {
        type = type.toLowerCase();
        String jsonFile = "bot_" + type;
        Entity entity = new Entity();

        try {
            JSONObject json = new JSONObject(Utilities.getRawFileText(mGame.getActivity(),
                    Utilities.getResourceId(mGame.getActivity(), "raw", jsonFile)));

            // First create the bot entity with required components
            entity.add(new Transformation(x, y, angle));
            entity.add(new Sprite(getSprite(json.getJSONObject("walk_sprite"))));
            entity.add(new PhysicsBody(mWorld, BodyType.DYNAMIC, entity, new PhysicsBody.Properties(1f, 0f, 0f, false, true)));
            entity.add(new Bot());

            Bot bot = entity.get(Bot.class);

            // For the 'bot' components, set the sprites and sprite sheets
            // for different actions as given in the json file.
            bot.sprWalk = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("idle_sprite"))
                bot.sprIdle = getSprite(json.getJSONObject("idle_sprite"));
            else
                bot.sprIdle = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("jump_sprite"))
                bot.sprJump = getSprite(json.getJSONObject("jump_sprite"));
            else
                bot.sprJump = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("push_sprite"))
                bot.sprPush = getSprite(json.getJSONObject("push_sprite"));
            else
                bot.sprPush = getSprite(json.getJSONObject("walk_sprite"));

            Sprite.SpriteSheetData stand, walk, push, jump;

            walk = getSpriteSheet(json.getJSONObject("walk_sheet"));

            if (json.has("idle_sheet"))
                stand = getSpriteSheet(json.getJSONObject("idle_sheet"));
            else
                stand = getSpriteSheet(json.getJSONObject("walk_sheet"));
            stand.animationSpeed = 0;
            entity.get(Sprite.class).spriteSheetData = stand;

            if (json.has("jump_sheet"))
                jump = getSpriteSheet(json.getJSONObject("jump_sheet"));
            else
                jump = getSpriteSheet(json.getJSONObject("walk_sheet"));
            jump.animationSpeed = 0;

            if (json.has("push_sheet"))
                push = getSpriteSheet(json.getJSONObject("push_sheet"));
            else
                push = getSpriteSheet(json.getJSONObject("walk_sheet"));

            bot.ssdIdle = stand;
            bot.ssdWalk = walk;
            bot.ssdJump = jump;
            bot.ssdPush = push;

            entity.get(Sprite.class).spriteSheetData = stand;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }
}
