//package core.world.chunk.save;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.files.FileHandle;
//import core.world.chunk.Chunk;
//import core.world.chunk.save.ChunkStorage;
//import data.map.asset.*;
//
//import java.io.*;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;
//
//public final class FileChunkStorage implements ChunkStorage {
//
//    private static final int MAGIC = 0x43484B31; // CHK1
//    private static final int VERSION = 1;
//
//    private static final byte NO_GHOST = 0;
//    private static final byte FLOOR_GHOST = 1;
//    private static final byte BUILDING_GHOST = 2;
//
//    private final FileHandle directory;
//
//    public FileChunkStorage(FileHandle directory) {
//        this.directory = directory;
//        directory.mkdirs();
//    }
//
//    @Override
//    public boolean save(Chunk chunk) {
//        FileHandle file = fileFor(chunk.chunkX, chunk.chunkY);
//
//        try (
//            OutputStream raw = file.write(false);
//            BufferedOutputStream buffered = new BufferedOutputStream(raw);
//            GZIPOutputStream compressed = new GZIPOutputStream(buffered);
//            DataOutputStream output = new DataOutputStream(compressed)
//        ) {
//            output.writeInt(MAGIC);
//            output.writeInt(VERSION);
//            output.writeInt(chunk.chunkX);
//            output.writeInt(chunk.chunkY);
//            output.writeInt(Chunk.SIZE);
//
//            for (int x = 0; x < Chunk.SIZE; x++) {
//                for (int y = 0; y < Chunk.SIZE; y++) {
//                    writeNullableEnum(output, chunk.floors[x][y]);
//                    writeNullableEnum(output, chunk.buildings[x][y]);
//                    writeGhost(output, chunk.ghosts[x][y]);
//                }
//            }
//
//            return true;
//        } catch (Exception exception) {
//            Gdx.app.error(
//                "FileChunkStorage",
//                "Failed to save chunk " + chunk.chunkX + ", " + chunk.chunkY,
//                exception
//            );
//            return false;
//        }
//    }
//
//    @Override
//    public boolean loadInto(Chunk chunk) {
//        FileHandle file = fileFor(chunk.chunkX, chunk.chunkY);
//
//        if (!file.exists()) {
//            return false;
//        }
//
//        // Read into a temporary chunk so a damaged file cannot
//        // partially overwrite the active chunk.
//        Chunk loaded = new Chunk(chunk.chunkX, chunk.chunkY);
//
//        try (
//            InputStream raw = file.read();
//            BufferedInputStream buffered = new BufferedInputStream(raw);
//            GZIPInputStream compressed = new GZIPInputStream(buffered);
//            DataInputStream input = new DataInputStream(compressed)
//        ) {
//            int magic = input.readInt();
//            int version = input.readInt();
//            int savedChunkX = input.readInt();
//            int savedChunkY = input.readInt();
//            int savedChunkSize = input.readInt();
//
//            if (magic != MAGIC) {
//                throw new IOException("Invalid chunk file");
//            }
//
//            if (version != VERSION) {
//                throw new IOException(
//                    "Unsupported chunk version: " + version
//                );
//            }
//
//            if (savedChunkX != chunk.chunkX || savedChunkY != chunk.chunkY) {
//                throw new IOException("Chunk coordinates do not match");
//            }
//
//            if (savedChunkSize != Chunk.SIZE) {
//                throw new IOException(
//                    "Chunk size does not match: " + savedChunkSize
//                );
//            }
//
//            for (int x = 0; x < Chunk.SIZE; x++) {
//                for (int y = 0; y < Chunk.SIZE; y++) {
//                    loaded.floors[x][y] =
//                        readNullableEnum(input, FloorType.class);
//
//                    loaded.buildings[x][y] =
//                        readNullableEnum(input, BuildingType.class);
//
//                    loaded.ghosts[x][y] = readGhost(input);
//                }
//            }
//
//            for (int x = 0; x < Chunk.SIZE; x++) {
//                System.arraycopy(
//                    loaded.floors[x],
//                    0,
//                    chunk.floors[x],
//                    0,
//                    Chunk.SIZE
//                );
//
//                System.arraycopy(
//                    loaded.buildings[x],
//                    0,
//                    chunk.buildings[x],
//                    0,
//                    Chunk.SIZE
//                );
//
//                System.arraycopy(
//                    loaded.ghosts[x],
//                    0,
//                    chunk.ghosts[x],
//                    0,
//                    Chunk.SIZE
//                );
//            }
//
//            chunk.dirty = false;
//            chunk.renderDirty = true;
//
//            return true;
//        } catch (Exception exception) {
//            Gdx.app.error(
//                "FileChunkStorage",
//                "Failed to load chunk "
//                    + chunk.chunkX + ", " + chunk.chunkY,
//                exception
//            );
//
//            return false;
//        }
//    }
//
//    private FileHandle fileFor(int chunkX, int chunkY) {
//        return directory.child(
//            "chunk_" + chunkX + "_" + chunkY + ".bin.gz"
//        );
//    }
//
//    private static void writeNullableEnum(
//        DataOutputStream output,
//        Enum<?> value
//    ) throws IOException {
//        output.writeBoolean(value != null);
//
//        if (value != null) {
//            output.writeUTF(value.name());
//        }
//    }
//
//    private static void writeGhost(
//        DataOutputStream output,
//        GhostType<? extends AssetType> ghost
//    ) throws IOException {
//        if (ghost == null) {
//            output.writeByte(NO_GHOST);
//            return;
//        }
//
//        AssetType source = ghost.getSourceType();
//
//        if (source instanceof FloorType floor) {
//            output.writeByte(FLOOR_GHOST);
//            output.writeUTF(floor.name());
//        } else if (source instanceof BuildingType building) {
//            output.writeByte(BUILDING_GHOST);
//            output.writeUTF(building.name());
//        } else {
//            throw new IOException(
//                "Unsupported ghost source: " + source.getClass().getName()
//            );
//        }
//
//        output.writeUTF(ghost.getState().name());
//    }
//
//    private static <E extends Enum<E>> E readNullableEnum(
//        DataInputStream input,
//        Class<E> enumType
//    ) throws IOException {
//        if (!input.readBoolean()) {
//            return null;
//        }
//
//        return readEnum(input, enumType);
//    }
//
//    private static <E extends Enum<E>> E readEnum(
//        DataInputStream input,
//        Class<E> enumType
//    ) throws IOException {
//        String name = input.readUTF();
//
//        try {
//            return Enum.valueOf(enumType, name);
//        } catch (IllegalArgumentException exception) {
//            throw new IOException(
//                "Unknown " + enumType.getSimpleName() + ": " + name,
//                exception
//            );
//        }
//    }
//
//    private static GhostType<AssetType> readGhost(
//        DataInputStream input
//    ) throws IOException {
//        int sourceKind = input.readUnsignedByte();
//
//        if (sourceKind == NO_GHOST) {
//            return null;
//        }
//
//        AssetType source = switch (sourceKind) {
//            case FLOOR_GHOST ->
//                readEnum(input, FloorType.class);
//
//            case BUILDING_GHOST ->
//                readEnum(input, BuildingType.class);
//
//            default ->
//                throw new IOException(
//                    "Unknown ghost source kind: " + sourceKind
//                );
//        };
//
//        GhostType<AssetType> ghost = new GhostType<>(source);
//        ghost.setState(readEnum(input, GhostState.class));
//
//        return ghost;
//    }
//}
