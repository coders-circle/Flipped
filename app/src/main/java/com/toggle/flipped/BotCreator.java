package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;

import com.badlogic.gdx.physics.box2d.World;
import com.toggle.katana2d.physics.PhysicsSystem;

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
    public Texture getSprite(JSONObject sprite) throws JSONException {
        String spriteName = sprite.getString("file");
        if (mGame.textureManager.has(spriteName))
            return mGame.textureManager.get(spriteName);

        int spriteTex = Utilities.getResourceId(mGame.getActivity(), "drawable", spriteName);
        Texture glSprite = mGame.getRenderer().addTexture(spriteTex, (float) sprite.getDouble("width"), (float) sprite.getDouble("height"));
        glSprite.color = new float[]{209f/255,209f/255,209f/255,1};

        if (sprite.has("origin-x"))
            glSprite.originX = (float)sprite.getDouble("origin-x");
        if (sprite.has("origin-y"))
            glSprite.originY = (float)sprite.getDouble("origin-y");

        mGame.textureManager.add(spriteName, glSprite);
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
    public void createBot(Entity entity, String type, float x, float y, float angle) {
        type = type.toLowerCase();
        String jsonFile = "bot_" + type;

        try {
            JSONObject json = new JSONObject(Utilities.getRawFileText(mGame.getActivity(),
                    Utilities.getResourceId(mGame.getActivity(), "raw", jsonFile)));

            // First create the bot entity with required components
            entity.add(new Transformation(x, y, angle));
            entity.add(new Sprite(getSprite(json.getJSONObject("walk_sprite")), -0.1f));
            entity.add(new PhysicsBody(mWorld, BodyDef.BodyType.DynamicBody, entity, new PhysicsBody.Properties(0.4f, 0f, 0f, true, true)));
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

            if (json.has("climb_sprite"))
                bot.sprClimb = getSprite(json.getJSONObject("climb_sprite"));
            else
                bot.sprClimb = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("pick_sprite"))
                bot.sprPick = getSprite(json.getJSONObject("pick_sprite"));
            else
                bot.sprPick = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("carry_sprite"))
                bot.sprCarry = getSprite(json.getJSONObject("carry_sprite"));
            else
                bot.sprCarry = getSprite(json.getJSONObject("walk_sprite"));

            if (json.has("lever_sprite"))
                bot.sprLever = getSprite(json.getJSONObject("lever_sprite"));
            else
                bot.sprLever = getSprite(json.getJSONObject("walk_sprite"));

            Sprite.SpriteSheetData stand, walk, push, jump, climb, pick, carry, lever;

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

            if (json.has("climb_sheet"))
                climb = getSpriteSheet(json.getJSONObject("climb_sheet"));
            else
                climb = getSpriteSheet(json.getJSONObject("walk_sheet"));
            climb.loop = false;

            if (json.has("pick_sheet"))
                pick = getSpriteSheet(json.getJSONObject("pick_sheet"));
            else
                pick = getSpriteSheet(json.getJSONObject("walk_sheet"));
            pick.loop = false;

            if (json.has("carry_sheet"))
                carry = getSpriteSheet(json.getJSONObject("carry_sheet"));
            else
                carry = getSpriteSheet(json.getJSONObject("walk_sheet"));

            if (json.has("lever_sheet"))
                lever = getSpriteSheet(json.getJSONObject("lever_sheet"));
            else
                lever = getSpriteSheet(json.getJSONObject("walk_sheet"));
            lever.loop = false;

            bot.ssdIdle = stand;
            bot.ssdWalk = walk;
            bot.ssdJump = jump;
            bot.ssdPush = push;
            bot.ssdClimb = climb;
            bot.ssdPick = pick;
            bot.ssdCarry = carry;
            bot.ssdLever = lever;

            bot.climbPositions.add(new Vector2(-26, -5));
            bot.climbPositions.add(new Vector2(-25, -6));
            bot.climbPositions.add(new Vector2(-24, -7));
            bot.climbPositions.add(new Vector2(-23, -8));
            bot.climbPositions.add(new Vector2(-22, -9));
            bot.climbPositions.add(new Vector2(-21, -10));
            bot.climbPositions.add(new Vector2(-20, -12));
            bot.climbPositions.add(new Vector2(-18, -14));
            bot.climbPositions.add(new Vector2(-16, -16));
            bot.climbPositions.add(new Vector2(-14, -18));
            bot.climbPositions.add(new Vector2(-12, -20));
            bot.climbPositions.add(new Vector2(-10, -22));
            bot.climbPositions.add(new Vector2(8, -24));
            bot.climbPositions.add(new Vector2(6, -26));
            bot.climbPositions.add(new Vector2(4, -30));
            bot.climbPositions.add(new Vector2(0, -32));

            for (int i=0; i<16; ++i)
                bot.climbPositions.get(i).scl(PhysicsSystem.METERS_PER_PIXEL);

            entity.get(Sprite.class).spriteSheetData = stand;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
