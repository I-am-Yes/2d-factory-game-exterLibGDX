package core.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.app.cores.GameCore;
import core.machine.utils.Item;

//TODO: migrate this to Vars
import static core.machine.render.ItemRender.ITEM_SIZE;
import static core.app.Vars.*;


public final class Artist extends GameCore {
    private static final float tileSize = world.getTileSize();


    public static void Draw() {
    }

    public static void DrawItem(Item item) {
        if (item == null) return;

        TextureRegion region = assets.getRegion(item.type);
        float itemSize = tileSize * ITEM_SIZE;
        float drawX = item.visualX * tileSize;
        float drawY = item.visualY * tileSize;
        batch.draw(region,
            drawX - itemSize * 0.5f,
            drawY - itemSize * 0.5f,
            itemSize, itemSize
        );
    }


}
