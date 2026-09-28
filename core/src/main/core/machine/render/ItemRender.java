package core.machine.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.machine.state.ItemType;
import core.machine.utils.Item;

import static core.app.Vars.*;

public class ItemRender {
    private final float tileSize;
    private final float itemSize;

    public static final float ITEM_SIZE = 0.8f;

    public ItemRender() {
        this.tileSize = world.getTileSize();
        this.itemSize = tileSize * ITEM_SIZE;
    }

    public void drawBatch(Item item) {
        if (item == null) return;
        drawBatch(item.type, item.visualX, item.visualY);
    }

    public void drawBatch(ItemType type, float visualX, float visualY) {
        if (type == null) return;

        TextureRegion region = assets.getRegion(type);
        float drawX = visualX * tileSize;
        float drawY = visualY * tileSize;
        batch.draw(region,
            drawX - itemSize * 0.5f,
            drawY - itemSize * 0.5f,
            itemSize, itemSize
        );

    }

}
