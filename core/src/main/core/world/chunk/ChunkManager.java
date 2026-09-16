package core.world.chunk;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import core.world.map.MapGenerator;
import data.map.MapConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ChunkManager {

    //TODO: this chunk and map loader should be a game system structure, maybe

    private final MapConfig mapConfig;
    private final Map<ChunkPos, Chunk> chunks = new HashMap<>();

    public ChunkManager(final MapConfig mapConfig) {
        this.mapConfig = mapConfig;
    }

    public Collection<Chunk> getLoadedChunks() {
        return chunks.values();
    }

    /**
     * Gets the number of loaded chunks.
     *
     * @return The number of loaded chunks.
     */
    public int getChunkCounts() {
        return chunks.size();
    }

    /**
     * Loads chunks around a given tile position.
     *
     * @param centerTileX The x-coordinate of the center tile.
     * @param centerTileY The y-coordinate of the center tile.
     * @param radius      The radius of the area to load chunks for.
     */
    public void loadChunksAroundTile(int centerTileX, int centerTileY, int radius) {
        int centerChunkX = Math.floorDiv(centerTileX, Chunk.SIZE);
        int centerChunkY = Math.floorDiv(centerTileY, Chunk.SIZE);

        for (int chunkX = centerChunkX - radius; chunkX <= centerChunkX + radius; chunkX++) {
            for (int chunkY = centerChunkY - radius; chunkY <= centerChunkY + radius; chunkY++) {
                getOrCreateChunk(chunkX, chunkY);
            }
        }
    }

    /**
     * Gets the number of visible tiles based on the camera's viewport and zoom level.
     *
     * @param camera
     * @param tileSize
     * @return
     */
    public int getVisibleTileCount(
        OrthographicCamera camera,
        float tileSize
    ) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        int minTileX = MathUtils.floor(
            (camera.position.x - halfWidth) / tileSize
        );

        int maxTileX = MathUtils.ceil(
            (camera.position.x + halfWidth) / tileSize
        );

        int minTileY = MathUtils.floor(
            (camera.position.y - halfHeight) / tileSize
        );

        int maxTileY = MathUtils.ceil(
            (camera.position.y + halfHeight) / tileSize
        );

        int visibleWidth = maxTileX - minTileX;
        int visibleHeight = maxTileY - minTileY;

        return visibleWidth * visibleHeight;
    }

    /**
     * Gets the number of visible chunks based on the camera's viewport and zoom level.
     * @param camera
     * @param tileSize
     * @return
     */
    public int getVisibleChunkCount(
        OrthographicCamera camera,
        float tileSize
    ) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        float cameraMinX = camera.position.x - halfWidth;
        float cameraMaxX = camera.position.x + halfWidth;
        float cameraMinY = camera.position.y - halfHeight;
        float cameraMaxY = camera.position.y + halfHeight;

        int visibleCount = 0;

        for (Chunk chunk : chunks.values()) {
            float chunkMinX = chunk.chunkX * Chunk.SIZE * tileSize;
            float chunkMinY = chunk.chunkY * Chunk.SIZE * tileSize;
            float chunkMaxX = chunkMinX + Chunk.SIZE * tileSize;
            float chunkMaxY = chunkMinY + Chunk.SIZE * tileSize;

            boolean visible =
                chunkMaxX >= cameraMinX
                    && chunkMinX <= cameraMaxX
                    && chunkMaxY >= cameraMinY
                    && chunkMinY <= cameraMaxY;

            if (visible) {
                visibleCount++;
            }
        }

        return visibleCount;
    }

    /**
     * Gets the bounds of the loaded map.
     *
     * @return The bounds of the loaded map.
     */
    public LoadedMapBounds getLoadedMapBounds() {
        if (chunks.isEmpty()) {
            return new LoadedMapBounds(0, 0, -1, -1);
        }

        int minChunkX = Integer.MAX_VALUE;
        int minChunkY = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int maxChunkY = Integer.MIN_VALUE;

        for (Chunk chunk : chunks.values()) {
            minChunkX = Math.min(minChunkX, chunk.chunkX);
            minChunkY = Math.min(minChunkY, chunk.chunkY);
            maxChunkX = Math.max(maxChunkX, chunk.chunkX);
            maxChunkY = Math.max(maxChunkY, chunk.chunkY);
        }

        return new LoadedMapBounds(
            minChunkX,
            minChunkY,
            maxChunkX,
            maxChunkY
        );
    }

    public record LoadedMapBounds(
        int minChunkX,
        int minChunkY,
        int maxChunkX,
        int maxChunkY
    ) {
        public int widthInChunks() {
            return maxChunkX >= minChunkX
                ? maxChunkX - minChunkX + 1
                : 0;
        }

        public int heightInChunks() {
            return maxChunkY >= minChunkY
                ? maxChunkY - minChunkY + 1
                : 0;
        }

        public int widthInTiles() {
            return widthInChunks() * Chunk.SIZE;
        }

        public int heightInTiles() {
            return heightInChunks() * Chunk.SIZE;
        }
    }

    public Chunk getOrCreateChunk(int chunkX, int chunkY) {
        ChunkPos pos = new ChunkPos(chunkX, chunkY);
        return chunks.computeIfAbsent(
            pos,
            ignored -> generateChunk(chunkX, chunkY)
        );
    }

    public Chunk getOrCreateChunkForTile(int tileX, int tileY) {
        int chunkX = Math.floorDiv(tileX, Chunk.SIZE);
        int chunkY = Math.floorDiv(tileY, Chunk.SIZE);
        return getOrCreateChunk(chunkX, chunkY);
    }

    public int getLocalX(int tileX) {
        return Math.floorMod(tileX, Chunk.SIZE);
    }

    public int getLocalY(int tileY) {
        return Math.floorMod(tileY, Chunk.SIZE);
    }

    private Chunk generateChunk(int chunkX, int chunkY) {
        Chunk chunk = new Chunk(chunkX, chunkY);

        int worldStartX = chunkX * Chunk.SIZE;
        int worldStartY = chunkY * Chunk.SIZE;

        for (int localX = 0; localX < Chunk.SIZE; localX++) {
            for (int localY = 0; localY < Chunk.SIZE; localY++) {
                int worldX = worldStartX + localX;
                int worldY = worldStartY + localY;

                chunk.floors[localX][localY] =
                    MapGenerator.generateFloorAt(worldX, worldY, mapConfig);
            }
        }

        return chunk;
    }

    private record ChunkPos(int x, int y) {}

}
