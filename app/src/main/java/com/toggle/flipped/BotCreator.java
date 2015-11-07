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

public class BotCreator {
    private Game mGame;
    private World mWorld;

    public BotCreator(Game game, World world) {
        mGame = game;
        mWorld = world;
    }

    public GLSprite getSprite(JSONObject sprite) throws JSONException {
        String spriteName = sprite.getString("file");
        if (mGame.spriteManager.has(spriteName))
            return mGame.spriteManager.get(spriteName);

        Texture spriteTex = mGame.getRenderer().addTexture(Utilities.getResourceId(mGame.getActivity(), "drawable", spriteName));
        GLSprite glSprite = new GLSprite(mGame.getRenderer(), spriteTex, (float) sprite.getDouble("width"), (float) sprite.getDouble("height"));
        mGame.spriteManager.add(spriteName, glSprite);
        return glSprite;
    }

    public Sprite.SpriteSheetData getSpriteSheet(JSONObject sheet) throws JSONException {
        Sprite.SpriteSheetData sheetData = new Sprite.SpriteSheetData();
        sheetData.imgWidth = (float)sheet.getDouble("width");
        sheetData.imgHeight = (float)sheet.getDouble("height");
        sheetData.index = sheet.optInt("index", 0);
        sheetData.numCols = sheet.optInt("cols", 5);
        sheetData.numRows = sheet.optInt("rows", 2);
        sheetData.numImages = sheet.optInt("images", sheetData.numRows * sheetData.numCols);
        return sheetData;
    }

    public Entity createBot(String type, float x, float y, float angle) {
        type = type.toLowerCase();
        String jsonFile = "bot_" + type;
        Entity entity = new Entity();

        try {
            JSONObject json = new JSONObject(Utilities.getRawFileText(mGame.getActivity(),
                    Utilities.getResourceId(mGame.getActivity(), "raw", jsonFile)));

            entity.add(new Transformation(x, y, angle));
            entity.add(new Sprite(getSprite(json.getJSONObject("walk_sprite"))));
            entity.add(new PhysicsBody(mWorld, BodyType.DYNAMIC, entity, new PhysicsBody.Properties(1f, 0f, 0f, false, true)));
            entity.add(new Player());
            entity.add(new Bot());

            Bot bot = entity.get(Bot.class);

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
