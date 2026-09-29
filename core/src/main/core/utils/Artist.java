package core.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.app.cores.GameCore;
import core.app.vars.Cores;
import core.machine.utils.Item;

//TODO: migrate this to Vars
import static core.app.vars.Cores.batch;
import static core.app.vars.Vars.*;

public final class Artist extends GameCore {
    private static final float tileSize = world.getTileSize();
    private static final float itemSize = tileSize * ITEM_SIZE;


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

    public static void drawBatch() {
//        drawBatch(batch);
    }

}
