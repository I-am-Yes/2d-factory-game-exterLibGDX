package core.render;

import Data.map.asset.AssetType;
import Data.map.asset.FloorType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.Player;
import core.entities.BuildGhostLine;
import core.world.World;
import core.controller.PlayerAction;
import core.entities.BuildPlan;

public class GhostOverlayRenderer {

    private static final float SLIDE_SPEED = 24f;

    private final Vector3 tmp = new Vector3();
    private final Vector2 animate = new Vector2();
    private float targetX, targetY;

    private boolean animInitialized;
    private boolean hoverVisible;
    private boolean hoverWalkable;

    private final PlayerAction playerAction;
    private final Player player;
    private final BuildGhostLine buildGhostLine;
    private final SpriteBatch spriteBatch;

    public GhostOverlayRenderer(PlayerAction playerAction, Player player) {
        this.playerAction = playerAction;
        this.player = player;
        this.spriteBatch = playerAction.getSpriteBatch();

        this.animate.set(player.getPos());
        this.buildGhostLine = playerAction.getBuildGhostLine();
    }

    public void update(World world, Viewport viewport, float delta, AssetType type) {
        if (type == null) {
            animInitialized = false;
            hoverVisible = false;
            return;
        }

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
        int tx = MathUtils.floor(tmp.x / tile);
        int ty = MathUtils.floor(tmp.y / tile);

        if (!world.isInBounds(tx, ty)) {
            hoverVisible = false;
            animInitialized = false;
            return;
        }

        targetX = tx * tile;
        targetY = ty * tile;
        hoverWalkable = world.isWalkable(tx, ty);
        hoverVisible = true;


        animInitialized = AnimateRenderer.animateMove(delta, targetX, targetY, SLIDE_SPEED, animate, hoverVisible);

    }

    public void render(World world, Viewport viewport, SpriteBatch spriteBatch, AssetType type, AssetsHandler assetsHandler) {
        if (type == null || !hoverVisible) return;
        renderGhostOverlay(world, viewport, spriteBatch, type, assetsHandler);

        //render drag line plans
//        renderDragLinePlan(world, spriteBatch, assetsHandler);

        //render build queue
//        renderBuildQueue(world, spriteBatch, blockAssets);

    }

    public void draw() {
        buildGhostLine.drawTilePreview(spriteBatch, buildGhostLine.getCurrentTiledLine(), playerAction.getSelectedType());
    }

    private void renderGhostOverlay(World world, Viewport viewport, SpriteBatch spriteBatch, AssetType type, AssetsHandler assetsHandler) {
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
        int tx = MathUtils.floor(tmp.x / tile);
        int ty = MathUtils.floor(tmp.y / tile);
        if (!world.isInBounds(tx, ty)) return;

        TiledMapTile tiled = assetsHandler.getTile(type);
        if (tiled == null) return;

        TextureRegion region = tiled.getTextureRegion();

        Color oldColor = spriteBatch.getColor();
        boolean walkAble = world.isWalkable(tx, ty);

        if (walkAble) spriteBatch.setColor(1f, 1f, 1f, 0.45f); //ghost normal
        else spriteBatch.setColor(1f, 0.3f, 0.3f, 0.45f); // ghost red

        spriteBatch.draw(region, animate.x, animate.y, tile, tile);
        spriteBatch.setColor(oldColor);
    }






}
