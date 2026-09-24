package core.world.chunk.save;

import core.world.chunk.Chunk;

public interface ChunkStorage {

    // Applies saved data to an already-generated chunk.
    // Returns false when no saved file exists or loading fails.
    boolean loadInto(Chunk chunk);

    // Returns true only when the chunk was saved successfully.
    boolean save(Chunk chunk);
}
