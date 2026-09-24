package data.map;

public class FloorDefinition {

    public String id;
    public String folder;
    public boolean enabled;

    //noise mode
    public float noiseScale = 0.08f;      // per-floor scale (smaller value = bigger noise)
    public float noiseThreshold = 0.5f;   // adjust number of noise count
    public int priority = 0;              // higher wins when multiple floors match

    //random mode
    public float randomChance = 0f;

    //rendering
    public boolean autoTile = true;

    public FloorDefinition() {}

    public FloorDefinition(String id, String folder, int priority, float noiseScale, float noiseThreshold, boolean autoTile) {
        this.id = id;
        this.folder = folder;
        this.priority = priority;
        this.noiseScale = noiseScale;
        this.noiseThreshold = noiseThreshold;
        this.autoTile = autoTile;
        this.enabled = true;
    }

}
