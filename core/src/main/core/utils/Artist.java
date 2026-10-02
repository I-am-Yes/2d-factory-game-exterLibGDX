package core.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.vars.Cores;
import core.machine.utils.Item;

//TODO: migrate this to Vars
import static core.app.vars.Cores.batch;
import static core.app.vars.Cores.shape;
import static core.app.vars.Vars.*;

public final class Artist {
    private static final float tileSize = world.getTileSize();
    private static final float itemSize = tileSize * ITEM_SIZE;


    public static void screenClear(Viewport viewport) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
    }

    public static void Draw() {
    }

    public static void DrawItem(Item item) {
        if (item != null) DrawItem(item, item.visualX, item.visualY, itemSize, batch);
    }

    public static void DrawItem(Item item, SpriteBatch batch) {
        if (item != null) DrawItem(item, item.visualX, item.visualY, itemSize, batch);
    }

    public static void DrawItem(Item item, float x, float y) {
        DrawItem(item, x, y, itemSize, batch);
    }

    public static void DrawItem(Item item, float x, float y, float size) {
        DrawItem(item, x, y, size, batch);
    }

    public static void DrawItem(Item item, float x, float y, float size, SpriteBatch batch) {
        if (item == null) return;
        batch.begin();
        batch.draw(Cores.assets.getRegion(item.type),
            x * tileSize - size / 2,
            y * tileSize - size / 2,
            size, size);
        batch.end();
    }

    public static void DrawBatch(SpriteBatch batch, TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, Color color) {
        float oldColor = batch.getPackedColor();
        if (region == null) return;
        batch.setColor(color == null ? Color.WHITE : color);
        batch.begin();
        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        batch.end();
        batch.setPackedColor(oldColor);
    }

    public static void DrawSprite(Sprite sprite, float x, float y, float sizeX, float sizeY) {
        if (sprite == null) return;
        DrawSprite(sprite, x, y, sizeX, sizeY, batch);
    }

    public static void DrawSprite(Sprite sprite, float x, float y, float sizeX, float sizeY, SpriteBatch batch) {
        if (sprite == null || batch == null) return;
        batch.begin();
        sprite.setSize(sizeX, sizeY);
        sprite.setPosition(x, y);
        sprite.draw(batch);
        batch.end();

    }
    public static void DrawRect( float x, float y, float width, float height, Color color) {
        DrawRect(shape, x, y, width, height, color);
    }

    public static void DrawRect(ShapeRenderer shape, float x, float y, float width, float height, Color color) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(color == null ? Color.WHITE : color);
        shape.rect(x, y, width, height);
        shape.end();
    }

    public static void DrawLine(float x, float y, float x2, float y2, float thickness, Color color) {
        DrawLine(shape, x, y, x2, y2, thickness, color);
    }

    public static void DrawLine(ShapeRenderer shape, float x, float y, float x2, float y2, float thickness, Color color) {
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(color == null ? Color.WHITE : color);
        shape.rectLine(x, y, x2, y2, thickness);
        shape.end();
    }

}
