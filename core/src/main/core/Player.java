package core;

import Data.PlayerData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Queue;
import core.controller.Controller;
import core.entities.BuildPlan;
import core.event.GameEvent;

public class Player {
    private final float PLAYER_WIDTH = PlayerData.getPlayerWidth();
    private final float PLAYER_HEIGHT = PlayerData.getPlayerHeight();
    private final float PLAYER_SPEED = PlayerData.getPlayerSpeed();

    private final Queue<BuildPlan> buildQueue = new Queue<>();
    private float buildSpeed = 200f;
    private float buildCounter = 0f;
    private boolean updateBuilding = true;

    public final Sprite sprite;

    private final World world;
    private final Controller controller;

    public Player(Texture texture, World world, float worldWidth, float worldHeight, Controller controller, BlockAssets assets) {
        this.controller = controller;
        this.world = world;

        sprite = new Sprite(texture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(worldWidth / 2f, worldHeight / 2f);  // spawn in middle of the map
    }

    public void update(float delta, float WorldWidth, float worldHeight, InputHandler input) {
        controller.moveCharacter(getPlayer(), sprite, delta, PLAYER_SPEED, WorldWidth, worldHeight);
        updateBuildingLogic(delta);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
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

    public void clearBuildingQueue() {
        buildQueue.clear();
    }

    public boolean isBuilding() {
        return buildQueue.size > 0;
    }

    public Queue<BuildPlan> getBuildQueue() {
        return buildQueue;
    }



    private void updateBuildingLogic(float delta) {
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

    public void setUpdateBuilding(boolean updateBuilding) {
        this.updateBuilding = updateBuilding;
    }

    public Controller getController() {
        return controller;
    }

    public Player getPlayer() {
        return this;
    }

    public boolean isPlayerMoving() {
        return controller.isPlayerMoving();
    }

    public float getPlayerSpeed() {
        return PLAYER_SPEED;
    }

    public float getPlayerPositionX() {
        return sprite.getX();
    }
    public float getPlayerPositionY() {
        return sprite.getY();
    }

}
