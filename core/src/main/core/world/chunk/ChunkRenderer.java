package core.world.chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import core.assets.AssetsHandler;
import data.map.asset.AssetType;
import data.map.asset.GhostType;

public final class ChunkRenderer {

    private final AssetsHandler assets;

    public ChunkRenderer(AssetsHandler assets) {
        this.assets = assets;

        //TODO: add context to this
        //TODO: convert to a chunk system
    }

    public void drawBatch(SpriteBatch batch, OrthographicCamera camera, Iterable<Chunk> chunks, float tileSize) {

        for (Chunk chunk : chunks) {
            if (isVisible(chunk, camera, tileSize)) {
                drawFloors(batch, chunk, tileSize);
            }
        }
        for (Chunk chunk : chunks) {
            if (isVisible(chunk, camera, tileSize)) {
                drawBuildings(batch, chunk, tileSize);
            }
        }
        for (Chunk chunk : chunks) {
            if (isVisible(chunk, camera, tileSize)) {
                drawGhosts(batch, chunk, tileSize);
            }
        }
    }

    private void drawFloors(SpriteBatch batch, Chunk chunk, float tileSize) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                draw(
                    batch,
                    chunk.floors[x][y],
                    worldX(chunk, x, tileSize),
                    worldY(chunk, y, tileSize),
                    tileSize,
                    Color.WHITE
                );
            }
        }
    }

    private void drawBuildings(SpriteBatch batch, Chunk chunk, float tileSize) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                draw(
                    batch,
                    chunk.buildings[x][y],
                    worldX(chunk, x, tileSize),
                    worldY(chunk, y, tileSize),
                    tileSize,
                    Color.WHITE
                );
            }
        }
    }

    private void drawGhosts(SpriteBatch batch, Chunk chunk, float tileSize) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                GhostType<AssetType> ghost = chunk.ghosts[x][y];

                if (ghost != null) {
                    draw(
                        batch,
                        ghost.getSourceType(),
                        worldX(chunk, x, tileSize),
                        worldY(chunk, y, tileSize),
                        tileSize,
                        ghost.getState().getColor()
                    );
                }
            }
        }
    }

    private float worldX(Chunk chunk, float localX, float tileSize) {
        return (chunk.chunkX * Chunk.SIZE + localX) * tileSize;
    }

    private float worldY(Chunk chunk, float localY, float tileSize) {
        return (chunk.chunkY * Chunk.SIZE + localY) * tileSize;
    }

    private void draw(SpriteBatch batch, AssetType type, float worldX, float worldY, float tileSize, Color color) {
        if (type == null) return;
        TiledMapTile tile = assets.getTile(type);
        if (tile == null) return;
        TextureRegion region = tile.getTextureRegion();

        batch.setColor(color);
        batch.draw(region, worldX, worldY, tileSize, tileSize);
        batch.setColor(Color.WHITE);
    }

    private boolean isVisible(Chunk chunk, OrthographicCamera camera, float tileSize) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        float cameraMinX = camera.position.x - halfWidth;
        float cameraMaxX = camera.position.x + halfWidth;
        float cameraMinY = camera.position.y - halfHeight;
        float cameraMaxY = camera.position.y + halfHeight;

        float chunkMinX = chunk.chunkX * Chunk.SIZE * tileSize;
        float chunkMinY = chunk.chunkY * Chunk.SIZE * tileSize;
        float chunkMaxX = chunkMinX + Chunk.SIZE * tileSize;
        float chunkMaxY = chunkMinY + Chunk.SIZE * tileSize;

        return chunkMaxX >= cameraMinX
            && chunkMinX <= cameraMaxX
            && chunkMaxY >= cameraMinY
            && chunkMinY <= cameraMaxY;
    }

    public void dispose() {

    }

}
