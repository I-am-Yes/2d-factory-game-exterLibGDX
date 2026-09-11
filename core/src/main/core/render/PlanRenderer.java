package core.render;

import core.app.context.GameContext;
import data.map.asset.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
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

    private PlanBuilder<AssetType> planBuilder;
    private Queue<PlanBuilder<?>> planRenderQueue;

    public PlanRenderer(GameContext context) {
        this.world = context.world;
        this.viewport = context.viewport;
        this.spriteBatch = context.spriteBatch;
        this.assetsHandler = context.assets;

        this.tileSize = world.getTileSize();

        this.planManager = context.planManager;
    }

    public void update() {}

    public void render() {

        updatePlanRenderQueue();
        renderPlan(planRenderQueue);

    }

    public void dispose() {}


    private void renderPlan(Queue planRenderQueue) {
        Color oldColor = new Color(spriteBatch.getColor());

        //TODO: placing plan entities on the ghost layer not current world layer
        if (planRenderQueue == null) return;
        for (int i = 0; i < planRenderQueue.size; i++) {
            PlanBuilder<?> currentPlanBuilder = (PlanBuilder<?>) planRenderQueue.get(i);

            for (int j = 0; j < currentPlanBuilder.getPlanQueue().size; j++) {
                PlanEntity<?> planEntity = currentPlanBuilder.getPlanEntityAt(j);
                renderPlanEntity(planEntity, tileSize, planEntity.getGhostType().getState().getColor());
            }
        }
    }

    private <Type extends AssetType> void renderPlanEntity(PlanEntity<Type> planEntity, float tileSize, Color color) {
        TiledMapTile tiledTile = assetsHandler.getTile(planEntity.getGhostType().getSourceType());
        TextureRegion region = tiledTile.getTextureRegion();
        spriteBatch.setColor(color);
        spriteBatch.draw(region, planEntity.getX() * tileSize, planEntity.getY() * tileSize, tileSize, tileSize);
    }

    private void updatePlanRenderQueue() {
        planRenderQueue = planManager.getPlanRenderQueue();
    }

}
