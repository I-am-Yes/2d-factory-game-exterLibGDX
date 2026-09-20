package core.world.chunk.cache;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import core.assets.AssetsHandler;
import core.machine.state.Direction;
import core.world.chunk.Chunk;
import core.world.chunk.ChunkManager;
import core.world.chunk.ChunkRenderDetail;
import core.world.chunk.meshes.ChunkMeshBuilder;
import core.world.chunk.meshes.MeshPart;
import data.map.asset.FloorType;

public final class ChunkOverviewCache implements Disposable {

    private static final float WHITE = Color.WHITE.toFloatBits();

    private final Array<MeshPart> floorMeshes = new Array<>();

    private boolean dirty = true;

    public boolean isDirty() {
        return dirty;
    }

    public void invalidate() {
        dirty = true;
    }

    public void rebuild(
        int minChunkX, int maxChunkX, int minChunkY, int maxChunkY,
        ChunkRenderDetail detail, ChunkManager chunkManager,
        AssetsHandler assets, float tileSize
    ) {
        clearMeshes();

        ObjectMap<Texture, ChunkMeshBuilder> builders = new ObjectMap<>();

        int chunkStep = Math.max(1, detail.getTileStep() / Chunk.SIZE);
        int startChunkX = Math.floorDiv(minChunkX, chunkStep) * chunkStep;
        int startChunkY = Math.floorDiv(minChunkY, chunkStep) * chunkStep;

        int groupTileSize = chunkStep * Chunk.SIZE;
        float groupWorldSize = groupTileSize * tileSize;

        for (int chunkX = startChunkX;
             chunkX <= maxChunkX;
             chunkX += chunkStep) {

            for (int chunkY = startChunkY; chunkY <= maxChunkY; chunkY += chunkStep) {

                int sampleTileX = chunkX * Chunk.SIZE + groupTileSize / 2;
                int sampleTileY = chunkY * Chunk.SIZE + groupTileSize / 2;

                FloorType floor = chunkManager.sampleFloorAt(sampleTileX, sampleTileY);

                addFloorSample(floor,
                    chunkX * Chunk.SIZE * tileSize, chunkY * Chunk.SIZE * tileSize,
                    groupWorldSize, assets, builders
                );
            }
        }

        for (ChunkMeshBuilder builder : builders.values()) {
            if (!builder.isEmpty()) {
                floorMeshes.add(builder.build());
            }
        }

        dirty = false;
    }

    public void draw(ShaderProgram shader) {
        for (int i = 0; i < floorMeshes.size; i++) {
            floorMeshes.get(i).draw(shader);
        }
    }

    private void addFloorSample(
        FloorType floor,
        float worldX,
        float worldY,
        float worldSize,
        AssetsHandler assets,
        ObjectMap<Texture, ChunkMeshBuilder> builders
    ) {
        if (floor == null) {
            return;
        }

        TiledMapTile tile = assets.getTile(floor);

        if (tile == null || tile.getTextureRegion() == null) {
            return;
        }

        TextureRegion region = tile.getTextureRegion();
        Texture texture = region.getTexture();

        ChunkMeshBuilder builder = builders.get(texture);

        if (builder != null && !builder.canAddQuad()) {
            floorMeshes.add(builder.build());
            builder = null;
        }

        if (builder == null) {
            builder = new ChunkMeshBuilder(texture);
            builders.put(texture, builder);
        }

        builder.add(region, worldX, worldY, null, worldSize, WHITE);
    }

    private void clearMeshes() {
        for (int i = 0; i < floorMeshes.size; i++) {
            floorMeshes.get(i).dispose();
        }

        floorMeshes.clear();
    }

    @Override
    public void dispose() {
        clearMeshes();
    }
}
