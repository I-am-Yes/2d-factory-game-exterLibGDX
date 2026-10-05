package core.utils;

import arcane.graphics.Artist;
import core.machine.utils.Item;

import static arcane.Cores.*;
import static core.app.Vars.*;

public final class ArtistEx {
    private static final float tileSize = world.getTileSize();
    private static final float itemSize = tileSize * ITEM_SIZE;

    public static void DrawItem(Item item) {
        if (item != null) {
            DrawItem(item, item.visualX, item.visualY, itemSize);
        }
    }

    public static void DrawItem(Item item, float x, float y) {
        DrawItem(item, x, y, itemSize);
    }

    public static void DrawItem(Item item, float x, float y, float size) {
        if (item == null) return;
        Artist.DrawBatch(batch,
            assets.getRegion(item.type),
            x * tileSize - size / 2,
            y * tileSize - size / 2,
            size, size, null
        );
    }


}
