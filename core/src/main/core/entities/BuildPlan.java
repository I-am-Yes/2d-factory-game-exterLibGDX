package core.entities;

import Data.map.FloorType;

public class BuildPlan {

    public int x, y;
    public FloorType floorType;
    public float progress;
    public boolean initialized;

    public BuildPlan(int x, int y, FloorType floorType) {
        this.x = x;
        this.y = y;
        this.floorType = floorType;
        this.progress = 0f;
        this.initialized = false;
    }

    public boolean isDone() {
        return progress >= 1f;
    }

}
