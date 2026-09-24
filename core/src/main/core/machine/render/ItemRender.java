package core.machine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.assets.AssetsHandler;
import core.machine.state.ItemType;
import core.machine.utils.Item;
import core.world.World;


public class ItemRender {
    private final World world;
    private final float tileSize;

    public static final float ITEM_SIZE = 0.8f;

    public ItemRender(World world) {
        this.world = world;

        this.tileSize = world.getTileSize();
    }

    public void drawBatch(SpriteBatch batch, Item item, AssetsHandler assets) {
        if (item == null) return;
        drawBatch(batch, item.type, item.visualX, item.visualY, assets);
    }

    public void drawBatch(SpriteBatch batch, ItemType type,
                          float visualX, float visualY, AssetsHandler assets
    ) {
        if (type == null) return;

        TextureRegion region = assets.getRegion(type);
        float itemSize = tileSize * ITEM_SIZE;
        float drawX = visualX * tileSize;
        float drawY = visualY * tileSize;
        batch.draw(region,
            drawX - itemSize * 0.5f,
            drawY - itemSize * 0.5f,
            itemSize, itemSize
        );

    }

}
