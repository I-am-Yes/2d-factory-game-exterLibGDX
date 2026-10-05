package core.render;

import arcane.ApplicationListener;
import arcane.graphics.Artist;
import arcane.utils.Queue;

import core.utils.RenderUtils;
import core.assets.textures.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanEntity;

import static arcane.Cores.*;
import static core.app.Vars.*;

public class PlanRenderer implements ApplicationListener {

    private final float tileSize = world.getTileSize();

    private Queue<PlanBuilder<?>> planRenderQueue;

    @Override
    public void update() {
        updateQueue();

        renderPlan();
    }

    @Override
    public void dispose() {
        planRenderQueue.clear();
    }

    private void renderPlan() {
        if (planRenderQueue == null) return;
        for (int i = 0; i < planRenderQueue.size; i++) {
            PlanBuilder<?> currentPlanBuilder = planRenderQueue.get(i);

            for (int j = 0; j < currentPlanBuilder.getPlanQueue().size; j++) {
                PlanEntity<?> planEntity = currentPlanBuilder.getPlanEntityAt(j);
                renderPlanEntity(planEntity, tileSize, planEntity.getGhostType().getState().getColor());
            }
        }
    }

    private <Type extends AssetType> void renderPlanEntity(PlanEntity<Type> planEntity, float tileSize, Color color) {
        TiledMapTile tiledTile = assets.getTile(planEntity.getGhostType().getSourceType());
        TextureRegion region = tiledTile.getTextureRegion();

        float rotation =  RenderUtils.getRotationDegree(planEntity.getDirection());

        Artist.DrawBatch(batch, region,
            planEntity.getX() * tileSize, planEntity.getY() * tileSize,
            tileSize / 2, tileSize / 2, tileSize, tileSize,
            1f, 1f, rotation, color
        );
    }

    private void updateQueue() {
        planRenderQueue = planManager.getPlanRenderQueue();
    }

}
