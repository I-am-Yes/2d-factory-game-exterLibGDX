package core.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.assets.AssetsHandler;
import core.machine.utils.Item;
import core.world.World;

import static core.machine.ItemRender.ITEM_SIZE;

public final class Artist {
    public World world;
    public static AssetsHandler assets;
    public SpriteBatch batch;
    private static float tileSize;

    private static boolean initialized = false;

    public Artist(World world, AssetsHandler assets, SpriteBatch batch) {
        this.world = world;
        this.assets = assets;
        this.batch = batch;
        tileSize = world.getTileSize();

        initialized = true;
    }
    public Artist createArtist(World world, AssetsHandler assets, SpriteBatch batch) {
        return new Artist(world, assets, batch);
    }

    public static void Draw() {

    }

    public static void DrawItem(SpriteBatch batch, Item item) {
        if (!initialized) {
            throw new IllegalStateException(
                "Artist has not been initialized"
            );
        }
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
