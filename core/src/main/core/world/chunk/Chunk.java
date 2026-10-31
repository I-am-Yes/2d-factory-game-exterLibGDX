package core.world.chunk;

import core.machine.state.Direction;
import core.assets.textures.AssetType;
import core.assets.textures.BuildingType;
import core.assets.textures.FloorType;
import core.assets.textures.GhostType;

public class Chunk {

    public static final int SIZE = 32; // Size of the chunk in blocks (16x16)

    public final int chunkX;
    public final int chunkY;

    public final FloorType[][] floors = new FloorType[SIZE][SIZE];
    public final BuildingType[][] buildings = new BuildingType[SIZE][SIZE];

    @SuppressWarnings("unchecked")
    public final GhostType<AssetType>[][] ghosts = (GhostType<AssetType>[][]) new GhostType[SIZE][SIZE];

    public final Direction[][] buildingDirection = new Direction[SIZE][SIZE];
    public final Direction[][] ghostDirection = new Direction[SIZE][SIZE];

    public boolean dirty;
    private long renderRevision = 1L;

    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    public long getRenderRevision() {
        return renderRevision;
    }

    public void markRenderDirty() {
        renderRevision++;
    }
}
