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
import core.world.chunk.ChunkRenderDetail;
import core.world.chunk.meshes.ChunkMeshBuilder;
import core.world.chunk.meshes.MeshPart;
import data.map.asset.AssetType;
import data.map.asset.GhostType;

public class ChunkRenderCache implements Disposable {

    private final Array<MeshPart> floorMeshes = new Array<>();
    private final Array<MeshPart> buildingMeshes = new Array<>();
    private final Array<MeshPart> ghostMeshes = new Array<>();

    private static final float WHITE = Color.WHITE.toFloatBits();

    private ChunkRenderDetail builtDetail;
    private long builtRevision = Long.MIN_VALUE;

    public void rebuild(Chunk chunk, AssetsHandler assets, float tileSize, ChunkRenderDetail detail) {

        clearMeshes();

        int tileStep = Math.min(detail.getTileStep(), Chunk.SIZE);

        buildLayerMeshes(chunk, chunk.floors, null, assets, tileSize, tileStep, WHITE, floorMeshes);
        // Only display individual buildings and ghosts at full detail.
        if (detail == ChunkRenderDetail.EXTREME
            || detail == ChunkRenderDetail.HIGHEST
            || detail == ChunkRenderDetail.HIGH
        ) {
            buildLayerMeshes(
                chunk,
                chunk.buildings,
                chunk.buildingDirection,
                assets,
                tileSize,
                1,
                WHITE,
                buildingMeshes
            );
            buildGhostMeshes(chunk, assets, tileSize);
        }

        builtRevision = chunk.getRenderRevision();
        builtDetail = detail;
    }

    public boolean needsRebuild(Chunk chunk, ChunkRenderDetail detail) {
        return builtRevision !=
            chunk.getRenderRevision() || builtDetail != detail;
    }

    public void drawFloors(ShaderProgram shader) {
        drawLayerMeshes(floorMeshes, shader);
    }

    public void drawBuildings(ShaderProgram shader) {
        drawLayerMeshes(buildingMeshes, shader);
    }

    public void drawGhosts(ShaderProgram shader) {
        drawLayerMeshes(ghostMeshes, shader);
    }

    private void drawLayerMeshes(Array<MeshPart> meshes, ShaderProgram shader) {
        for (int i = 0; i < meshes.size; i++) {
            meshes.get(i).draw(shader);
        }
    }

    private void buildGhostMeshes(
        Chunk chunk, AssetsHandler assets, float tileSize
    ) {
        ObjectMap<Texture, ChunkMeshBuilder> meshBuilders =
            new ObjectMap<>();

        for (int localX = 0; localX < Chunk.SIZE; localX++) {
            for (int localY = 0; localY < Chunk.SIZE; localY++) {
                GhostType<AssetType> ghost =
                    chunk.ghosts[localX][localY];

                if (ghost == null) {
                    continue;
                }

                float packedColor =
                    ghost.getState()
                        .getColor()
                        .toFloatBits();

                addTileToBuilder(
                    meshBuilders,
                    ghost.getSourceType(),
                    assets,
                    worldX(chunk, localX, tileSize),
                    worldY(chunk, localY, tileSize),
                    chunk.ghostDirection[localX][localY],
                    tileSize,
                    packedColor
                );
            }
        }

        finishMeshBuilders(meshBuilders, ghostMeshes);
    }

    private <T extends AssetType> void buildLayerMeshes(
        Chunk chunk,
        T[][] layer,
        Direction[][] directions,
        AssetsHandler assets,
        float tileSize,
        int tileStep,
        float packedColor,
        Array<MeshPart> output
    ) {
        ObjectMap<Texture, ChunkMeshBuilder> meshBuilders = new ObjectMap<>();
        for (int localX = 0; localX < Chunk.SIZE; localX += tileStep) {
            for (int localY = 0; localY < Chunk.SIZE; localY += tileStep) {
                int sampleX = Math.min(localX + tileStep / 2, Chunk.SIZE - 1);
                int sampleY = Math.min(localY + tileStep / 2, Chunk.SIZE - 1);
                T type = layer[sampleX][sampleY];
                if (type == null) continue;

                Direction direction =
                    directions == null ? Direction.EAST : directions[sampleX][sampleY];

                if (direction == null) {
                    direction = Direction.EAST;
                }

                addTileToBuilder(
                    meshBuilders, type, assets,
                    worldX(chunk, localX, tileSize),
                    worldY(chunk, localY, tileSize),
                    direction,
                    tileSize * tileStep, packedColor
                );
            }
        }

        finishMeshBuilders(meshBuilders, output);
    }

    private void addTileToBuilder(
        ObjectMap<Texture, ChunkMeshBuilder> meshBuilders,
        AssetType type, AssetsHandler assets,
        float worldX, float worldY, Direction direction,
        float tileSize, float packedColor
    ) {
        TiledMapTile tile = assets.getTile(type);

        if (tile == null || tile.getTextureRegion() == null) {
            return;
        }

        TextureRegion region = tile.getTextureRegion();
        Texture texture = region.getTexture();

        ChunkMeshBuilder builder =
            meshBuilders.get(texture);

        if (builder == null) {
            builder = new ChunkMeshBuilder(texture);
            meshBuilders.put(texture, builder);
        }

        builder.add(
            region,
            worldX,
            worldY,
            direction,
            tileSize,
            packedColor
        );
    }

    private float worldX(Chunk chunk, int localX, float tileSize) {
        return (chunk.chunkX * Chunk.SIZE + localX)
            * tileSize;
    }

    private float worldY(Chunk chunk, int localY, float tileSize) {
        return (chunk.chunkY * Chunk.SIZE + localY)
            * tileSize;
    }

    private void finishMeshBuilders(
        ObjectMap<Texture, ChunkMeshBuilder> meshBuilders,
        Array<MeshPart> output
    ) {
        for (ChunkMeshBuilder builder : meshBuilders.values()) {
            if (!builder.isEmpty()) {
                output.add(builder.build());
            }
        }
    }

    private void clearMeshes() {
        disposeMeshes(floorMeshes);
        disposeMeshes(buildingMeshes);
        disposeMeshes(ghostMeshes);
    }

    private void disposeMeshes(Array<MeshPart> meshes) {
        for (int i = 0; i < meshes.size; i++) {
            meshes.get(i).dispose();
        }
        meshes.clear();
    }


    @Override
    public void dispose() {
        clearMeshes();
    }
}
