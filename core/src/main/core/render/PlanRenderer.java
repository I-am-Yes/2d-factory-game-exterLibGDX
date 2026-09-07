package core.render;

import Data.map.asset.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanEntity;
import core.world.World;

public class PlanRenderer<T extends AssetType> {
    private final World world;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetsHandler assetsHandler;

    private final float tileSize;

    private final Color PLAN_DEFAULT_COLOR = new Color(0.5f, 0.5f, 0.8f, 0.4f);
    private final Color PLAN_ACCEPTED_COLOR = new Color(0.5f, 1f, 0.5f, 0.3f);
    private final Color PLAN_REJECTED_COLOR = new Color(1f, 0.5f, 0.5f, 0.3f);

    private final PlanBuilder<AssetType> planBuilder;

    public PlanRenderer(World world, Viewport viewport, SpriteBatch spriteBatch, PlanBuilder<AssetType> planBuilder, AssetsHandler assetsHandler) {
        this.world = world;
        this.viewport = viewport;
        this.spriteBatch = spriteBatch;
        this.planBuilder = planBuilder;
        this.assetsHandler = assetsHandler;

        this.tileSize = world.getTileSize();


    }

    public void update() {}

    public void render() {

        renderPlan(planBuilder);

    }

    public void dispose() {}


    private <Type extends AssetType> void renderPlan(PlanBuilder<Type> planBuilder) {
        Color oldColor = new Color(spriteBatch.getColor());

        //TODO: placing plan entities on the ghost layer not current world layer
        for (int i = 0; i < planBuilder.getPlanQueue().size; i++) {
            PlanEntity<Type> planEntity = planBuilder.getPlanEntityAt(i);
            renderPlanEntity(planEntity, tileSize, PLAN_DEFAULT_COLOR);
        }
    }

    private <Type extends AssetType> void renderPlanEntity(PlanEntity<Type> planEntity, float tileSize, Color color) {
        TiledMapTile tiledTile = assetsHandler.getTile(planEntity.getGhostType().getSourceType());
        TextureRegion region = tiledTile.getTextureRegion();
        spriteBatch.setColor(color);
        spriteBatch.draw(region, planEntity.getX() * tileSize, planEntity.getY() * tileSize, tileSize, tileSize);
    }

}
