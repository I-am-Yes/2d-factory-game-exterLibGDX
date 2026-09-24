package core.debug.debuggers;

import core.world.map.MapGenerator;
import core.world.map.NoiseHelper;
import data.map.*;
import data.map.asset.FloorType;

public final class MapGenDebugger {

    public MapGenDebugger() {}

    public void printReport(MapConfig mapConfig, FloorType[][] grid) {

        if (grid == null) {
            System.out.println("[MAP_GEN] grid is null");
            return;
        }

        int width = grid.length;
        int height = width > 0 ? grid[0].length : 0;
        int total = width * height;

        System.out.println("========== MAP GEN REPORT ==========");
        System.out.printf("mode=%s seed=%d size=%dx%d tiles=%d%n",
            mapConfig.mode, mapConfig.seed, width, height, total);
        System.out.printf("defaultFloorId=%s floorDefinitions=%d%n",
            mapConfig.defaultFloorId, mapConfig.floorDefinitions.size);

        if (mapConfig.floorDefinitions.size == 0) {
            System.out.println("[MAP_GEN] WARN: floorDefinitions is empty!");
        }

        int[] counts = new int[FloorType.values().length];
        int nullCells = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                FloorType floorType = grid[x][y];
                if (floorType == null) {
                    nullCells++;
                } else {
                    counts[floorType.ordinal()]++;
                }
            }
        }

        for (FloorType floorType : FloorType.values()) {
            int count = counts[floorType.ordinal()];
            if (count == 0) continue;
            System.out.printf("  %-6s %4d (%5.1f%%)%n",
                floorType.name(), count, 100f * count / total);
        }

        if (nullCells > 0) {
            System.out.printf("[MAP_GEN] WARN: null cells=%d%n", nullCells);
        }

        if (total > 0) {
            int max = 0;
            FloorType dominant = null;
            for (FloorType type : FloorType.values()) {
                int c = counts[type.ordinal()];
                if (c > max) {
                    max = c;
                    dominant = type;
                }
            }
            if (max == total) {
                System.out.printf("[MAP_GEN] WARN: 100%% %s — check config / mode / enabled floors%n",
                    dominant);
            }
        }

        printFloorConfig(mapConfig);

        // debug island / plain / noise modes
        if (mapConfig.mode == MapGenMode.ISLAND
            || mapConfig.mode == MapGenMode.PLAIN
            || mapConfig.mode == MapGenMode.NOISE) {

            int sx = width / 2;
            int sy = height / 2;

            if (mapConfig.mode == MapGenMode.PLAIN) {
                System.out.printf("lakeSeedLevel=%.3f lakeSeaLevel=%.3f lakeStepSize=%d lakeChunkSize=%d%n",
                    mapConfig.lakeSeedLevel, mapConfig.lakeSeaLevel,
                    mapConfig.lakeStepSize, mapConfig.lakeChunkSize);
                System.out.printf("sample at center (%d,%d): grid value: %s%n", sx, sy, grid[sx][sy]);
            } else if (mapConfig.mode == MapGenMode.ISLAND) {
                System.out.printf("seaLevel=%.3f islandFalloff=%.1f islandStepSize=%d beachWidth=%d%n",
                    mapConfig.seaLevel, mapConfig.islandFalloff,
                    mapConfig.islandStepSize, mapConfig.beachWidth);
                System.out.printf("sample at center (%d,%d): grid value: %s%n", sx, sy, grid[sx][sy]);
            } else {
                float elevMin = 1f;
                float elevMax = 0f;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        float e = MapGenerator.sampleElevation(x, y, mapConfig);
                        if (e < elevMin) elevMin = e;
                        if (e > elevMax) elevMax = e;
                    }
                }
                System.out.printf("elevation range: %.3f .. %.3f%n", elevMin, elevMax);

                float elevation = MapGenerator.sampleElevation(sx, sy, mapConfig);
                float moisture = MapGenerator.sampleMoisture(sx, sy, mapConfig);
                FloorType expected = MapGenerator.pickBiome(elevation, moisture, mapConfig);

                System.out.printf("sample at center (%d,%d):%n", sx, sy);
                System.out.printf("    elevation=%.3f moisture=%.3f%n", elevation, moisture);
                System.out.printf("    generator pick: %s%n", expected);
                System.out.printf("    grid value:     %s%n", grid[sx][sy]);

                if (expected != grid[sx][sy]) {
                    System.out.println("[MAP_GEN] WARN: grid mismatch at center");
                }
            }
        }

        System.out.println("====================================");
    }

    private static void printFloorConfig(MapConfig config) {
        System.out.println("floor config:");
        for (FloorDefinition floor : config.floorDefinitions) {
            System.out.printf("  id=%-6s enabled=%-5s priority=%2d scale=%.3f threshold=%.2f random=%.2f autotile=%s%n",
                floor.id, floor.enabled, floor.priority,
                floor.noiseScale, floor.noiseThreshold,
                floor.randomChance, floor.autoTile);
        }
    }

    public static void printNoiseAt(int x, int y, MapConfig config) {
        FloorDefinition best = null;

        for (FloorDefinition floor : config.floorDefinitions) {
            if (!floor.enabled) continue;

            float nx = x * floor.noiseScale;
            float ny = y * floor.noiseScale;
            long floorSeed = config.seed ^ floor.id.hashCode();
            float value = NoiseHelper.noise2D(floorSeed, nx, ny);
            boolean pass = value >= floor.noiseThreshold;

            System.out.printf("    %-6s noise=%.3f threshold=%.2f pass=%s priority=%d%n",
                floor.id, value, floor.noiseThreshold, pass, floor.priority);

            if (pass && (best == null || floor.priority > best.priority)) {
                best = floor;
            }
        }

        System.out.printf("    -> winner: %s%n", best != null ? best.id : config.defaultFloorId);
    }

}
