package core.player.mechanic;

import core.player.Player;
import core.player.PlayerAction;
import core.system.systems.PlayerSystem;
import data.map.asset.AssetType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.assets.AssetsHandler;
import core.world.World;
import ui.PlayerHotbar;

public class GhostOverlay {

    private final Viewport viewport;
    private final World world;
    private final PlayerAction playerAction;
    private final Player player;
    private final BuildGhostLine buildGhostLine;
    private final AssetsHandler assetsHandler;

    private static final float SLIDE_SPEED = 24f;

    private final Vector3 tmp = new Vector3();
    private final Vector2 animate = new Vector2();
    private float targetX, targetY;

    private boolean animInitialized;
    private boolean hoverVisible;
    private boolean hoverWalkable;

    private final Vector2 hoverTile = new Vector2();

    private AssetType type;

    public GhostOverlay(World world, Viewport viewport, Player player, PlayerAction action, AssetsHandler assets, BuildGhostLine buildGhostLine) {
        this.world = world;
        this.player = player;
        this.playerAction = action;
        this.viewport = viewport;
        this.assetsHandler = assets;
        this.buildGhostLine = buildGhostLine;

        this.animate.set(player.getPos());
    }

    public void update(float delta) {
        updateSelectedType();

        hoverTile.set(
            PlayerSystem.getHoverTileThresholdX(),
            PlayerSystem.getHoverTileThresholdY()
        );

        if (type == null) {
            this.type = playerAction.getSelectedType();
            animInitialized = false;
            hoverVisible = false;
            return;
        }

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);

//        float tile = world.getTileSize();
//        int tx = MathUtils.floor(tmp.x / tile);
//        int ty = MathUtils.floor(tmp.y / tile);

        float tile = world.getTileSize();
        int tx = (int) hoverTile.x;
        int ty = (int) hoverTile.y;

        if (!world.isInBounds(tx, ty)) {
            hoverVisible = false;
            animInitialized = false;
            return;
        }

        targetX = tx * tile;
        targetY = ty * tile;
        hoverWalkable = world.isWalkable(tx, ty);
        hoverVisible = true;


        animInitialized =
            OverlayHelper.animateMove(
                delta, targetX, targetY, SLIDE_SPEED, animate, animInitialized
            );

    }

    public void render() {

    }

    public void drawBatch(SpriteBatch spriteBatch) {

        if (type == null || !hoverVisible) return;
        renderGhostOverlay(world, viewport, type, assetsHandler, spriteBatch);
        //TODO: render rotation too
        buildGhostLine.drawTilePreview(spriteBatch, buildGhostLine.getCurrentTiledLine(), playerAction.getSelectedType());

    }

    private void renderGhostOverlay(World world, Viewport viewport , AssetType type, AssetsHandler assetsHandler, SpriteBatch spriteBatch) {
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
//        int tx = MathUtils.floor(tmp.x / tile);
//        int ty = MathUtils.floor(tmp.y / tile);
        int tx = (int) hoverTile.x;
        int ty = (int) hoverTile.y;

        if (!world.isInBounds(tx, ty)) return;

        TiledMapTile tiled = assetsHandler.getTile(type);
        if (tiled == null) return;

        TextureRegion region = tiled.getTextureRegion();

        float oldColor = spriteBatch.getPackedColor();
        boolean walkAble = world.isWalkable(tx, ty);

        if (walkAble) spriteBatch.setColor(1f, 1f, 1f, 0.45f); //ghost normal
        else spriteBatch.setColor(1f, 0.3f, 0.3f, 0.45f); // ghost red

        spriteBatch.draw(region, animate.x, animate.y, tile, tile);
        spriteBatch.setPackedColor(oldColor);
    }


    private AssetType getSelectedType() {
        if (playerAction.getSelectedType() == null) return null;
        if (type == null) return PlayerHotbar.getSelectedType();
        if (type == PlayerHotbar.getSelectedType()) return type;
        else return null;
    }

    private void updateSelectedType() {
        this.type = getSelectedType();
    }



}
