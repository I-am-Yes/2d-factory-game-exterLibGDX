package core.world.chunk;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import core.world.map.MapGenerator;
import data.map.MapConfig;
import data.map.asset.FloorType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

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
     * Samples the floor type at a given tile position.
     *
     * @param tileX The x-coordinate of the tile.
     * @param tileY The y-coordinate of the tile.
     * @return The sample floor type at the specified tile position.
     */
    public FloorType sampleFloorAt(int tileX, int tileY) {
        int chunkX = Math.floorDiv(tileX, Chunk.SIZE);
        int chunkY = Math.floorDiv(tileY, Chunk.SIZE);
        Chunk loadedChunk = getLoadedChunk(chunkX, chunkY);
        if (loadedChunk != null) {
            int localX = getLocalX(tileX);
            int localY = getLocalY(tileY);
            return loadedChunk.floors[localX][localY];
        }

        return MapGenerator.generateFloorAt(tileX, tileY, mapConfig);
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
     * Unloads chunks outside a given radius from the center chunk.
     *
     * @param centerChunkX The x-coordinate of the center chunk.
     * @param centerChunkY The y-coordinate of the center chunk.
     * @param keepRadius   The radius of the area to keep chunks for.
     */
    public void unloadOutsideChunk(
        int centerChunkX,
        int centerChunkY,
        int keepRadius,
        Consumer<Chunk> onUnload
    ) {
        Iterator<Map.Entry<ChunkPos, Chunk>> iterator =
            chunks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ChunkPos, Chunk> entry =
                iterator.next();

            ChunkPos pos = entry.getKey();
            Chunk chunk = entry.getValue();

            // Keep modified chunks until chunk saving exists.
            if (chunk.dirty) {
                continue;
            }

            boolean outside =
                Math.abs(pos.x() - centerChunkX) > keepRadius
                    || Math.abs(pos.y() - centerChunkY) > keepRadius;

            if (outside) {
                onUnload.accept(chunk);
                iterator.remove();
            }
        }
    }

    /**
     * Loads chunks around a given tile position.
     *
     * @param centerTileX The x-coordinate of the center tile.
     * @param centerTileY The y-coordinate of the center tile.
     * @param radius      The radius of the area to load chunks for.
     * @param maxNewChunks The maximum number of new chunks to load.
     * @return true if all chunks were loaded, false otherwise.
     */
    public boolean loadChunksAroundTile(int centerTileX, int centerTileY, int radius, int maxNewChunks) {
        if (maxNewChunks <= 0) {
            throw new IllegalArgumentException(
                "maxNewChunks must be positive"
            );
        }
        int centerChunkX = Math.floorDiv(centerTileX, Chunk.SIZE);
        int centerChunkY = Math.floorDiv(centerTileY, Chunk.SIZE);
        int loaded = 0;
        // Load from the center outward.
        for (int ring = 0; ring <= radius; ring++) {
            for (int offsetX = -ring; offsetX <= ring; offsetX++) {
                for (int offsetY = -ring; offsetY <= ring; offsetY++) {
                    if (Math.max(Math.abs(offsetX), Math.abs(offsetY)) != ring) {
                        continue;
                    }
                    int chunkX = centerChunkX + offsetX;
                    int chunkY = centerChunkY + offsetY;

                    if (getLoadedChunk(chunkX, chunkY) != null) {
                        continue;
                    }

                    getOrCreateChunk(chunkX, chunkY);
                    loaded++;

                    if (loaded >= maxNewChunks) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the number of visible tiles based on the camera's viewport and zoom level.
     *
     * @param camera   The camera to use for determining visibility.
     * @param tileSize The size of each tile.
     * @return The number of visible tiles.
     */
    public long getVisibleTileCount(
        OrthographicCamera camera,
        float tileSize
    ) {
        float halfWidth =
            camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight =
            camera.viewportHeight * camera.zoom * 0.5f;

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

        long visibleWidth = (long) maxTileX - minTileX;
        long visibleHeight = (long) maxTileY - minTileY;

        return visibleWidth * visibleHeight;
    }

    /**
     * Gets the number of visible chunks based on the camera's viewport and zoom level.
     * @param camera
     * @param tileSize
     * @return
     */
    public long getVisibleChunkCount(OrthographicCamera camera, float tileSize) {
        float chunkWorldSize = Chunk.SIZE * tileSize;
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        int minChunkX = MathUtils.floor((camera.position.x - halfWidth) / chunkWorldSize);
        int maxChunkX = MathUtils.floor((camera.position.x + halfWidth) / chunkWorldSize);
        int minChunkY = MathUtils.floor((camera.position.y - halfHeight) / chunkWorldSize);
        int maxChunkY = MathUtils.floor((camera.position.y + halfHeight) / chunkWorldSize);

        long visibleWidth = (long) maxChunkX - minChunkX + 1L;
        long visibleHeight = (long) maxChunkY - minChunkY + 1L;
        return visibleWidth * visibleHeight;
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

    public Chunk getLoadedChunk(int chunkX, int chunkY) {
        return chunks.get(new ChunkPos(chunkX, chunkY));
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
