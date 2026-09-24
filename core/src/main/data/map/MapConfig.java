package data.map;


import com.badlogic.gdx.utils.Array;

public class MapConfig {

    public long seed = 12345L;
    public int width = 1024 * 8;
    public int height = 512 * 8;

    //this is force set tile pixel size.
    public int tilePixel = 32; //pixel size for one tile x^2

    //change modes
    public MapGenMode mode = MapGenMode.NOISE;

    public String defaultFloorId = "sand";

    // island mode — height noise + edge falloff → land surrounded by ocean
    public float seaLevel = -0.5f;          // height below this becomes water (Notch scale, roughly -2..+2)
    public float islandFalloff = 20f;       // pushes map edges toward water; lower = bigger landmass
    public int islandStepSize = 32;         // diamond-square step; higher = smoother, larger land shapes
    public float beachHeight = 0.08f;       // unused by generator today; kept for future elevation-band beach
    public int beachWidth = 4;              // sand ring thickness (tiles) around water shores

    // rock hills — round capped blobs on high ground (island + plain)
    public float rockElevation = 0.4f;      // minimum land height before rock can appear
    public float rockDetailMax = -1.7f;     // rock noise threshold; lower = more rock patches
    public int rockStepSize = 64;           // rock noise smoothness; higher = bigger rock zones
    public int rockChunkSize = 4;           // spacing between rock seed points
    public int rockMaxChunkSize = 8;        // max radius (tiles) of each rock blob

    // plain mode — mostly grass, inland lakes only (no edge ocean)
    public float lakeSeaLevel = -0.75f;     // tile height below this can become lake water
    public float lakeSeedLevel = -0.85f;    // only deeper lows spawn a lake center (fewer lakes)
    public int lakeStepSize = 32;           // lake height noise step; higher = broader depressions
    public int lakeChunkSize = 4;          // max lake radius in tiles from each seed (round lakes)


    // shared noise layers for elevation & moisture
    public float elevationScale = 0.015f;
    public float moistureScale = 0.0025f;
    public long moistureSeedOffset = 999L;

    //elevation bands highs for mountains (i guess, it should be)
    public float marbleElevation = 0.88f;
    public float marbleMaxElevation = 0.9f;
    public float marbleMinElevation = 0.6f;

    public float stoneElevation = 0.62f;
    public float stoneMaxElevation = 0.72f;
    public float stoneMinElevation = 0.5f;

    //moisture bands, wet vs dry things, low/mid elevation
    public float rockMoisture = 0.58f;



    public Array<FloorDefinition> floorDefinitions = new Array<>();

    public static MapConfig createDefault() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.defaultFloorId = "sand";

        mapConfig.floorDefinitions.add(floors("sand",   "Sand",   0, 0.06f, 0.00f, true,  0f));
        mapConfig.floorDefinitions.add(floors("rock",   "Rock",  10, 0.10f, 0.55f, true,  0.08f));
        mapConfig.floorDefinitions.add(floors("stone",  "Stone", 20, 0.12f, 0.65f, true,  0.5f));
        mapConfig.floorDefinitions.add(floors("marble", "Marble",30, 0.14f, 0.75f, true,  0.05f));

        return mapConfig;
    }

    public static MapConfig createPresetMap(PresetMap presetMap) {
        MapConfig mapConfig = createDefault();
        mapConfig.mode = MapGenMode.ISLAND;
//        mapConfig.width = 512;
//        mapConfig.height = 512;

        switch (presetMap) {

            case BIG_CONTINENT:
                // One huge island: weak edge falloff lets land reach corners, slightly favor land over water.
                mapConfig.seaLevel = 0.58f;       // more land than default (-0.5)
                mapConfig.islandFalloff = 2f;     // low = edges stay land longer → single big continent
                mapConfig.islandStepSize = 2;     // smooth coastlines, large terrain features
                mapConfig.beachWidth = 1;          // wide sandy shores
                break;

            case MEDIUM_CONTINENT:
                // Balanced island: default Notch-style falloff and land/water ratio.
                mapConfig.seaLevel = -0.5f;
                mapConfig.islandFalloff = 20f;
                mapConfig.islandStepSize = 32;
                mapConfig.beachWidth = 4;
                break;

            case SMALL_CONTINENT:
                // Compact island: strong edge falloff + higher sea level → small land blob in the middle.
                mapConfig.seaLevel = -0.4f;        // higher = more ocean
                mapConfig.islandFalloff = 30f;     // high = water eats map edges quickly
                mapConfig.islandStepSize = 32;
                mapConfig.beachWidth = 3;
                break;

            case ARCHIPELAGO:
                // Many tiny islands: strong falloff + bumpy noise → scattered small land chunks.
                mapConfig.seaLevel = -0.45f;
                mapConfig.islandFalloff = 25f;
                mapConfig.islandStepSize = 16;     // smaller step = bumpier, broken-up land
                mapConfig.beachWidth = 2;
                break;

            case PLAIN:
                // Open grassland with small scattered lakes; no ocean at map borders.
                mapConfig.mode = MapGenMode.PLAIN;
                mapConfig.islandFalloff = 0f;      // must stay 0 — disables edge ocean
                mapConfig.lakeStepSize = 128;       // medium depressions → pond-sized basins
                mapConfig.lakeSeedLevel = 0.3f;   // strict: only deep spots start a lake, lakeSeedLevel must be lower than lakeSeaLevel
                mapConfig.lakeSeaLevel = 0.34f;     // shallow fill around each seed
                mapConfig.lakeChunkSize = 256;      // small round lakes
                mapConfig.beachWidth = 1;
                mapConfig.rockElevation = 0.65f;   // rocky hills only on higher ground
                break;

            case LARGE_LAKE:
                // Mostly grass with one or few big inland lakes; still no border ocean.
                mapConfig.mode = MapGenMode.PLAIN;
                mapConfig.islandFalloff = 0f;
                mapConfig.lakeStepSize = 512;       // broad smooth basins → large water bodies
                mapConfig.lakeSeedLevel = 0.32f;  // relaxed: more lake centers
                mapConfig.lakeSeaLevel = 0.34f;  // higher = more tiles become water
                mapConfig.lakeChunkSize = 2048;      // large lake radius
                mapConfig.beachWidth = 2;
                mapConfig.rockElevation = 0.6f;
                break;
        }

        return mapConfig;
    }

    private static FloorDefinition floors(String id, String folder, int priority, float noiseScale,
                                          float thresHold, boolean autoTile, float randomChance) {

        FloorDefinition floorDefinition = new FloorDefinition(id, folder, priority, noiseScale, thresHold, autoTile);
        floorDefinition.randomChance = randomChance;
        return floorDefinition;
    }

    public FloorDefinition getFloor(String id) {
        for (FloorDefinition floorDefinition : floorDefinitions) {
            if (floorDefinition.id.equals(id)) return  floorDefinition;
        }
        return null;
    }

    public long getMapSeed(MapConfig mapConfig) {
        return mapConfig.seed;
    }

    public int getTilePixel() {
        return tilePixel;
    }

}
