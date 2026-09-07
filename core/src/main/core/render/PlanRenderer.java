package core.render;

import Data.map.asset.helperInterface.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.entities.PlanBuilder;
import core.entities.PlanEntity;
import core.world.World;

public class PlanRenderer<T extends AssetType> {
    private final World world;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetsHandler assetsHandler;

    private PlanBuilder<AssetType> planBuilder;

    public PlanRenderer(World world, Viewport viewport, SpriteBatch spriteBatch, PlanBuilder planBuilder, AssetsHandler assetsHandler) {
        this.world = world;
        this.viewport = viewport;
        this.spriteBatch = spriteBatch;
        this.planBuilder = planBuilder;
        this.assetsHandler = assetsHandler;
    }

    public void update() {}

    public void render() {

        renderPlan(planBuilder);

    }

    public void dispose() {}


    private <T extends AssetType> void renderPlan(PlanBuilder planBuilder) {
        Color oldColor = new Color(spriteBatch.getColor());
        float tileSize = world.getTileSize();

        for (int i = 0; i < planBuilder.getPlanQueue().size; i++) {
            PlanEntity<T> planEntity = planBuilder.getPlanEntityAt(i);
            TiledMapTile tiledTile =
                assetsHandler.getTile( planEntity.getGhostType().getSourceType());

            TiledMapTile tile = assetsHandler.getTile(planEntity.getGhostType().getSourceType());
            TextureRegion region = tile.getTextureRegion();
            spriteBatch.setColor(0.5f, 1f, 0.5f, 0.3f);
            spriteBatch.draw(region, planEntity.getX() * tileSize, planEntity.getY() * tileSize, tileSize, tileSize);
        }
    }

}
