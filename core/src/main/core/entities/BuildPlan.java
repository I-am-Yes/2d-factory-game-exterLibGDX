package core.entities;

import Data.map.FloorType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Queue;
import core.BlockAssets;
import core.Player;
import core.World;
import core.event.GameEvent;

public class BuildPlan {
    private World world;

    public int x, y;
    public FloorType floorType;
    public float progress;
    public boolean initialized;
    private final Queue<BuildPlan> buildQueue = new Queue<>();
    private float buildSpeed = 200f;
    private float buildCounter = 0f;
    private boolean updateBuilding = true;

    public BuildPlan(int x, int y, FloorType floorType) {
        this.x = x;
        this.y = y;
        this.floorType = floorType;
        this.progress = 0f;
        this.initialized = false;
    }

    public void update(float delta) {
        updateBuildingLogic(delta);
    }

    public void addBuildPlan(BuildPlan buildPlan) {
        for (int i = 0; i < buildQueue.size; i++) {
            BuildPlan existing = buildQueue.get(i);
            if (existing.x == buildPlan.x && existing.y == buildPlan.y) {
                buildQueue.removeIndex(i);
                break;
            }
        }
        buildQueue.addLast(buildPlan);
    }

    public void updateBuildingLogic(float delta) {
        if (!updateBuilding || buildQueue.size == 0) return;

        buildCounter += delta;

        while (buildCounter >= 1f / buildSpeed && buildQueue.size > 0) {
            buildCounter -= 1f / buildSpeed;

            BuildPlan current = buildQueue.first();
            current.progress += delta * buildSpeed;

            if (current.isDone()) {
                if (world != null && current.floorType != null) {
                    GameEvent.BlockPlaceRequest.fire(current.x, current.y, current.floorType);
                }
                buildQueue.removeFirst();
            }
        }
    }

    public void renderBuildQueue(World world, Player player, SpriteBatch batch, BlockAssets blockAssets) {
        if (player == null) return;

        Color oldColor = batch.getColor();
        float tile = world.getTileSize();

        for (BuildPlan plan : getBuildQueue()) {
            if (!world.isInBounds(plan.x, plan.y)) continue;

            TiledMapTile tiled = blockAssets.getTile(plan.floorType);
            if (tiled == null) continue;

            TextureRegion region = tiled.getTextureRegion();

            float alpha = 0.3f + (plan.progress * 0.4f);
            batch.setColor(0.5f, 1f, 0.5f, alpha);
            batch.draw(region, plan.x * tile, plan.y * tile, tile, tile);
        }

        batch.setColor(oldColor);
    }

    public void setUpdateBuilding(boolean updateBuilding) {
        this.updateBuilding = updateBuilding;
    }

    public void clearBuildingQueue() {
        buildQueue.clear();
    }

    public boolean isBuilding() {
        return buildQueue.size > 0;
    }

    public Queue<BuildPlan> getBuildQueue() {
        return buildQueue;
    }

    public boolean isDone() {
        return progress >= 1f;
    }

}
