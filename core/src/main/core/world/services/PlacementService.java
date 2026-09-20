package core.world.services;

import core.event.Events;
import core.event.GameEvent;
import core.machine.state.Direction;
import core.world.World;
import core.world.chunk.Chunk;
import core.world.chunk.ChunkManager;
import core.world.chunk.ChunkRenderer;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;
import data.map.asset.FloorType;
import data.map.asset.GhostType;
import java.util.function.Consumer;

public final class PlacementService {

    private final World world;
    private final ChunkManager chunkManager;
    private final ChunkRenderer chunkRenderer;

    private final Consumer<GameEvent.BlockPlaceRequest> blockPlaceListener = this::handlePlacementRequest;

    public PlacementService(World world, ChunkManager chunkManager, ChunkRenderer chunkRenderer) {
        this.world = world;
        this.chunkManager = chunkManager;
        this.chunkRenderer = chunkRenderer;

        Events.on(GameEvent.BlockPlaceRequest.class, blockPlaceListener);
    }

    public boolean placeFloor(int tileX, int tileY, FloorType floorType) {
        if (floorType == null) return false;

        Chunk chunk = chunkManager.getOrCreateChunkForTile(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        if (chunk.floors[localX][localY] == floorType) return false;

        chunk.floors[localX][localY] = floorType;

        chunk.dirty = true;
        chunk.markRenderDirty();
        chunkRenderer.invalidateOverview();
        return true;
    }

    public boolean placeBuilding(
        int tileX, int tileY, Direction direction, BuildingType buildingType
    ) {
        if (buildingType == null || !canPlaceBuildingAt(tileX, tileY)) {
            return false;
        }

        Chunk chunk = chunkManager.getOrCreateChunkForTile(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        chunk.buildings[localX][localY] = buildingType;
        chunk.buildingDirection[localX][localY] =
            direction == null ? Direction.EAST : direction;

        chunk.dirty = true;
        chunk.markRenderDirty();
        return true;
    }

    public boolean placeGhost(
        int tileX, int tileY, Direction direction,
        GhostType<? extends AssetType> ghostType
    ) {
        if (ghostType == null) return false;

        Chunk chunk = chunkManager.getOrCreateChunkForTile(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        @SuppressWarnings("unchecked")
        GhostType<AssetType> storedGhost =
            (GhostType<AssetType>) ghostType;

        chunk.ghosts[localX][localY] = storedGhost;
        chunk.ghostDirection[localX][localY] =
            direction == null ? Direction.EAST : direction;

        chunk.dirty = true;
        chunk.markRenderDirty();
        return true;
    }

    public boolean placeBlock(int tileX, int tileY, Direction direction, AssetType type) {
        if (type instanceof FloorType floorType) {
            return placeFloor(tileX, tileY, floorType);
        }

        if (type instanceof BuildingType buildingType) {
            return placeBuilding(tileX, tileY, direction, buildingType);
        }

        return false;
    }

    public boolean canPlaceBuildingAt(int tileX, int tileY) {
        return world.isWalkable(tileX, tileY)
            && world.isInBounds(tileX, tileY)
            && !world.hasBuildingAt(tileX, tileY);
    }


    private void handlePlacementRequest(GameEvent.BlockPlaceRequest request) {
        if (request.isCancelled()) return;
        if (!world.isWalkable(request.tileX, request.tileY)) {
            request.cancel();
            return;
        }

        //TODO: get a dynamic multi layer checker system.
        //floor tile type already there
        if (world.getFloorAt(request.tileX, request.tileY) == request.type
            || world.getBuildingAt(request.tileX, request.tileY) == request.type) {
            request.cancel();
            return;
        }

        if (placeBlock(request.tileX, request.tileY, request.direction, request.type)) {
            GameEvent.BlockPlaced.fire(request.tileX, request.tileY, request.direction, request.type);
        } else {
            request.cancel();
        }

    }

    public void dispose() {
        Events.remove(GameEvent.BlockPlaceRequest.class, blockPlaceListener);
    }

}
