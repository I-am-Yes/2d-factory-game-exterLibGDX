package core.world.map;

import data.map.FloorDefinition;
import data.map.asset.FloorType;
import data.map.MapConfig;
import data.map.MapGenMode;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import core.assets.AssetsHandler;

import java.util.Random;


public class MapGenerator {

    public static FloorType generateFloorAt(int tileX, int tileY, MapConfig mapConfig) {

        if (mapConfig.mode == MapGenMode.RANDOM) {
            return pickRandomFloor(new Random(mixSeed(mapConfig.seed, tileX, tileY)), mapConfig);
        }
        // Infinite maps cannot use finite-map edge falloff.
        // NOISE, ISLAND, and PLAIN use absolute world coordinates.
        if (mapConfig.mode == MapGenMode.NOISE
        ) {
            return pickNoiseFloor(tileX, tileY, mapConfig);
        }

        if (mapConfig.mode == MapGenMode.ISLAND) {
            return pickIslandFloor(tileX, tileY, mapConfig);
        }

        if (mapConfig.mode == MapGenMode.PLAIN) {
            return pickPlainFloor(tileX, tileY, mapConfig);
        }

        return findDefaultFloor(mapConfig);
    }

    private static long mixSeed(long seed, int x, int y) {
        long value = seed;
        value ^= 0x9E3779B97F4A7C15L * x;
        value ^= 0xC2B2AE3D27D4EB4FL * y;

        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;

        return value ^ (value >>> 31);
    }

    public static FloorType[][] generateTiledMap(MapConfig mapConfig) {
        if (mapConfig.mode == MapGenMode.ISLAND || mapConfig.mode == MapGenMode.PLAIN) {
            return generateIslandMap(mapConfig);
        }

        FloorType[][] grid = new FloorType[mapConfig.width][mapConfig.height];
        Random random = new Random(mapConfig.seed);

        for (int x = 0; x < mapConfig.width; x++) {
            for (int y = 0; y < mapConfig.height; y++) {
                if (mapConfig.mode == MapGenMode.NOISE) {
                    grid[x][y] = pickNoiseFloor(x, y, mapConfig);
                }
                else if (mapConfig.mode == MapGenMode.RANDOM) {
                    grid[x][y] = pickRandomFloor(random, mapConfig);




                } else {
                    grid[x][y] = findDefaultFloor(mapConfig);
                }
            }
        }

        return grid;
    }

    private static FloorType[][] generateIslandMap(MapConfig mapConfig) {
        int w = mapConfig.width;
        int h = mapConfig.height;
        boolean plain = mapConfig.mode == MapGenMode.PLAIN;

        int heightStep = plain ? mapConfig.lakeStepSize : mapConfig.islandStepSize;

        SampleGenerator noise1 = new SampleGenerator(w, h, heightStep, mapConfig.seed);
        SampleGenerator noise2 = new SampleGenerator(w, h, heightStep, mapConfig.seed + 1);

        SampleGenerator mnoise1 = new SampleGenerator(w, h, mapConfig.rockStepSize, mapConfig.seed + 2);
        SampleGenerator mnoise2 = new SampleGenerator(w, h, mapConfig.rockStepSize, mapConfig.seed + 3);
        SampleGenerator mnoise3 = new SampleGenerator(w, h, mapConfig.rockStepSize, mapConfig.seed + 4);

        // Plain: lake seed points in low ground
        java.util.ArrayList<int[]> lakeSeeds = new java.util.ArrayList<>();
        if (plain) {
            int lakeSpacing = Math.max(16, heightStep / 2);
            for (int sy = 0; sy < h; sy += lakeSpacing) {
                for (int sx = 0; sx < w; sx += lakeSpacing) {
                    int ci = sx + sy * w;
                    double val = sampleHeight(noise1, noise2, ci, sx, sy, w, h, mapConfig);
                    if (val < mapConfig.lakeSeedLevel) {
                        lakeSeeds.add(new int[] { sx, sy });
                    }
                }
            }
        }

        // Rock seeds
        java.util.ArrayList<int[]> rockSeeds = new java.util.ArrayList<>();
        int rockSpacing = mapConfig.rockChunkSize;
        for (int sy = 0; sy < h; sy += rockSpacing) {
            for (int sx = 0; sx < w; sx += rockSpacing) {
                int ci = sx + sy * w;
                double val = sampleHeight(noise1, noise2, ci, sx, sy, w, h, mapConfig);
                double mval = sampleRockMask(mnoise1, mnoise2, mnoise3, ci);

                if (val > mapConfig.rockElevation && mval < mapConfig.rockDetailMax) {
                    rockSeeds.add(new int[] { sx, sy });
                }
            }
        }

        FloorType[][] grid = new FloorType[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;
                double val = sampleHeight(noise1, noise2, i, x, y, w, h, mapConfig);

                if (plain) {
                    if (val < mapConfig.lakeSeaLevel
                        && isWithinRadius(x, y, lakeSeeds, mapConfig.lakeChunkSize)) {
                        grid[x][y] = FloorType.WATER;
                        continue;
                    }
                } else if (val < mapConfig.seaLevel) {
                    grid[x][y] = FloorType.WATER;
                    continue;
                }

                if (val > mapConfig.rockElevation
                    && isWithinRadius(x, y, rockSeeds, mapConfig.rockMaxChunkSize)) {
                    grid[x][y] = FloorType.ROCK;
                } else {
                    grid[x][y] = FloorType.GRASS;
                }
            }
        }

        applyCoastSand(grid, w, h, mapConfig.beachWidth);
        return grid;
    }

    private static double sampleHeight(
        SampleGenerator noise1, SampleGenerator noise2,
        int i, int x, int y, int w, int h, MapConfig mapConfig
    ) {
        double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
        if (mapConfig.mode == MapGenMode.ISLAND) {
            val = applyFalloff(val, x, y, w, h, mapConfig.islandFalloff);
        }
        return val;
    }

    private static double sampleRockMask(
        SampleGenerator mnoise1, SampleGenerator mnoise2, SampleGenerator mnoise3, int i
    ) {
        double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
        return Math.abs(mval - mnoise3.values[i]) * 3 - 2;
    }

    private static boolean isWithinRadius(int x, int y, java.util.ArrayList<int[]> seeds, int maxRadius) {
        if (seeds.isEmpty() || maxRadius <= 0) return false;

        int maxRadiusSq = maxRadius * maxRadius;
        for (int[] seed : seeds) {
            int dx = x - seed[0];
            int dy = y - seed[1];
            if (dx * dx + dy * dy <= maxRadiusSq) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWithinRockBlob(int x, int y, java.util.ArrayList<int[]> seeds, int maxRadius) {
        int maxRadiusSq = maxRadius * maxRadius;

        for (int[] seed : seeds) {
            int dx = x - seed[0];
            int dy = y - seed[1];
            if (dx * dx + dy * dy <= maxRadiusSq) {
                return true;
            }
        }
        return false;
    }

    private static double applyFalloff(double val, int x, int y, int w, int h, float falloff) {
        double xd = x / (w - 1.0) * 2 - 1;
        double yd = y / (h - 1.0) * 2 - 1;
        if (xd < 0) xd = -xd;
        if (yd < 0) yd = -yd;
        double dist = Math.max(xd, yd);
        dist = dist * dist * dist * dist;
        dist = dist * dist * dist * dist;
        return val + 1 - dist * falloff;
    }

    private static void applyCoastSand(FloorType[][] grid, int w, int h, int width) {
        FloorType[][] copy = new FloorType[w][h];
        for (int x = 0; x < w; x++) {
            System.arraycopy(grid[x], 0, copy[x], 0, h);
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (copy[x][y] != FloorType.GRASS) continue;

                int dist = distanceToWater(copy, w, h, x, y);
                if (dist >= 1 && dist <= width) {
                    grid[x][y] = FloorType.SAND;
                }
            }
        }
    }

    private static int distanceToWater(FloorType[][] grid, int w, int h, int sx, int sy) {
        int max = 8;
        for (int d = 1; d <= max; d++) {
            for (int x = sx - d; x <= sx + d; x++) {
                for (int y = sy - d; y <= sy + d; y++) {
                    if (x < 0 || y < 0 || x >= w || y >= h) continue;
                    if (grid[x][y] == FloorType.WATER) return d;
                }
            }
        }
        return max + 1;
    }

    public static float remap01(float value, float min, float max) {
        if (max <=  min) return 0.5f;
        return (value - min) / (max - min);
    }

    private static FloorType pickRandomFloor(Random random, MapConfig mapConfig) {
        float roll = random.nextFloat();

        // highest priority first: marble → stone → rock
        for (int priority = 999; priority >= 0; priority--) {
            for (FloorDefinition floor : mapConfig.floorDefinitions) {
                if (!floor.enabled) continue;
                if (floor.priority != priority) continue;
                if (floor.randomChance <= 0f) continue;

                if (roll < floor.randomChance) {
                    return FloorType.fromId(floor.id);
                }
                roll -= floor.randomChance;
            }
        }

        return findDefaultFloor(mapConfig);
    }

    public static FloorType pickPlainFloor(int x, int y, MapConfig mapConfig) {
        float lakeScale = 1f / Math.max(1, mapConfig.lakeStepSize);

        float lakeNoise = NoiseHelper.fractalNoise2D(
            mapConfig.seed + 12_345L,
            x * lakeScale,
            y * lakeScale,
            4,
            0.5f
        );

        if (lakeNoise < mapConfig.lakeSeedLevel) {
            return FloorType.WATER;
        }

        if (lakeNoise < mapConfig.lakeSeaLevel) {
            return FloorType.SAND;
        }

        float elevation = sampleElevation(x, y, mapConfig);
        float moisture = sampleMoisture(x, y, mapConfig);

        if (elevation >= mapConfig.rockElevation
            && moisture >= mapConfig.rockMoisture) {
            return FloorType.ROCK;
        }

        return FloorType.GRASS;
    }

    public static FloorType pickNoiseFloor(int x, int y, MapConfig mapConfig) {
        float elevation = sampleElevation(x, y, mapConfig);
        float moisture  = sampleMoisture(x, y, mapConfig);
        return pickBiome(elevation, moisture, mapConfig);
    }

    public static FloorType pickIslandFloor(int x, int y, MapConfig mapConfig) {
        return pickIslandBiome(sampleElevation(x, y, mapConfig), mapConfig);
    }

    public static FloorType pickIslandBiome(float elevation, MapConfig mapConfig) {
        if (elevation < mapConfig.seaLevel) return FloorType.WATER;
        if (elevation < mapConfig.seaLevel + mapConfig.beachHeight) return FloorType.SAND;
        return FloorType.GRASS;
    }


    public static void applyToLayer(TiledMapTileLayer layer, FloorType[][] grid, AssetsHandler assets) {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                TiledMapTile tile = assets.getTile(grid[x][y]);

                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                layer.setCell(x, y, cell);

            }
        }
    }

    private static FloorType findDefaultFloor(MapConfig mapConfig) {
        FloorDefinition defaultFloor = mapConfig.getFloor(mapConfig.defaultFloorId);

        if (defaultFloor != null && defaultFloor.enabled) {
            return FloorType.fromId(defaultFloor.id);
        }

        //if fall, somehow idk
        for (FloorDefinition floor : mapConfig.floorDefinitions) {
            if (floor.enabled) return FloorType.fromId(floor.id);
        }
        //again, if it's somehow really fall to set default floor tile, then it will be sand.. YEAH SAND!!
        return FloorType.SAND;
    }

    public static float sampleElevation(int x, int y, MapConfig mapConfig) {
        float nx = x * mapConfig.elevationScale;
        float ny = y * mapConfig.elevationScale;
        return NoiseHelper.fractalNoise2D(mapConfig.seed, nx, ny, 4, 0.5f);
    }

    public static float sampleMoisture(int x, int y, MapConfig mapConfig) {
        float nx = x * mapConfig.moistureScale;
        float ny = y * mapConfig.moistureScale;
        return NoiseHelper.fractalNoise2D(
            mapConfig.seed + mapConfig.moistureSeedOffset, nx, ny, 4, 0.5f);
    }

    public static FloorType pickBiome(float elevation, float moisture, MapConfig mapConfig) {
        //high elevation first for mountains
        if (elevation >= mapConfig.marbleElevation) return FloorType.MARBLE;
        if (elevation >= mapConfig.stoneElevation && elevation < mapConfig.stoneMaxElevation) return FloorType.STONE;

        //lower elevation for moisturessss
        if (moisture >= mapConfig.rockMoisture) return FloorType.ROCK;

        return FloorType.SAND;
    }

}
