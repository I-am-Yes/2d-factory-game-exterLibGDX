package core.world.chunk;

public enum ChunkRenderDetail {
    EXTREME(1, 4),
    HIGHEST(2, 8),
    HIGH(4, 16),
    MEDIUM(8, 32),
    LOW(16, 64),
    VERY_LOW(32, 128),
    POTATO(64, 256),
    ULTIMATE_POTATO(128, 512),
    IMPOSSIBLE(256, 1024),
    IMMERSIVELY_UNBELIEVABLE(512, 2048),
    ULTRAMODERN_POTATOES(1024, 4096),
    INSTINCT(2048, 8192),
    TRANSCENDENTAL_POTATO(4096, 16384),
    COSMIC_CRISIS(8192, 32768),
    REALITY_BREAKER(16384, 65536),
    ABSOLUTE_MELTDOWN(32768, 131072),
    QUANTUM_POTATO(65536, 262144),
    UNIVERSAL_OVERLOAD(131072, 524288),
    DIMENSIONAL_COLLAPSE(262144, 1048576),
    INFINITE_TURBO(524288, 2097152),
    GODLIKE_POTATO(1048576, 4194304),
    BEYOND_COMPREHENSION(2097152, 8388608),
    ETERNAL_LAG(4194304, 16777216),
    THE_FINAL_FRAME(8388608, 33554432),

    ;
    private final int tileStep;
    private final int rebuildBudget;

    ChunkRenderDetail(int tileStep, int rebuildBudget) {
        this.tileStep = tileStep;
        this.rebuildBudget = rebuildBudget;
    }

    public int getTileStep() {
        return tileStep;
    }

    public int getRebuildBudget() {
        return rebuildBudget;
    }

}
