package core.render;

import core.helper.RenderUtils;
import data.map.asset.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.assets.AssetsHandler;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanEntity;
import core.entities.plan.PlanManager;
import core.world.World;

public class PlanRenderer<T extends AssetType> {
    private final World world;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetsHandler assetsHandler;
    private final PlanManager planManager;

    private final float tileSize;

    private Queue<PlanBuilder<?>> planRenderQueue;

    public PlanRenderer(World world, Viewport viewport, SpriteBatch spriteBatch, AssetsHandler assetsHandler, PlanManager planManager) {
        this.world = world;
        this.viewport = viewport;
        this.spriteBatch = spriteBatch;
        this.assetsHandler = assetsHandler;
        this.planManager = planManager;

        this.tileSize = world.getTileSize();
    }

    public void update() {
        updatePlanRenderQueue();
    }

    public void render() {

    }

    public void drawBatch() {
        renderPlan(planRenderQueue);
    }

    public void dispose() {}



    private void renderPlan(Queue<PlanBuilder<?>> planRenderQueue) {
        if (planRenderQueue == null) return;
        float oldColor = spriteBatch.getPackedColor();
        for (int i = 0; i < planRenderQueue.size; i++) {
            PlanBuilder<?> currentPlanBuilder = planRenderQueue.get(i);

            for (int j = 0; j < currentPlanBuilder.getPlanQueue().size; j++) {
                PlanEntity<?> planEntity = currentPlanBuilder.getPlanEntityAt(j);
                renderPlanEntity(planEntity, tileSize, planEntity.getGhostType().getState().getColor());
            }
        }

        spriteBatch.setPackedColor(oldColor);
    }

    private <Type extends AssetType> void renderPlanEntity(PlanEntity<Type> planEntity, float tileSize, Color color) {
        TiledMapTile tiledTile = assetsHandler.getTile(planEntity.getGhostType().getSourceType());
        TextureRegion region = tiledTile.getTextureRegion();
        spriteBatch.setColor(color);
        float rotation =  RenderUtils.getRotationDegree(planEntity.getDirection());
        spriteBatch.draw(region,
            planEntity.getX() * tileSize, planEntity.getY() * tileSize,
            tileSize / 2, tileSize / 2, tileSize, tileSize,
            1f, 1f, rotation
        );
    }

    private void updatePlanRenderQueue() {
        planRenderQueue = planManager.getPlanRenderQueue();
    }

}
