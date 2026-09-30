package core.player.mechanic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import core.app.cores.Time;
import core.helper.RenderUtils;
import core.machine.state.Direction;
import core.player.PlayerAction;
import core.system.systems.PlayerSystem;
import core.utils.Artist;
import data.map.asset.AssetType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ui.PlayerHotbar;

import static core.app.vars.Cores.*;
import static core.app.vars.Vars.*;

public class GhostOverlay {
    private final BuildGhostLine buildGhostLine;
    private final PlayerAction action;

    private static final float SLIDE_SPEED = 24f;
    private static final float ROTATION_SPEED = 24f;

    private float animateRotation;
    private boolean rotationInitialized;

    private final Vector3 tmp = new Vector3();
    private final Vector2 animate = new Vector2();
    private float targetX, targetY;

    private boolean animInitialized;
    private boolean hoverVisible;
    private boolean hoverWalkable;

    private final Vector2 hoverTile = new Vector2();

    private AssetType type;

    public GhostOverlay() {
        this.buildGhostLine = player.getAction().getBuildGhostLine();
        this.action = player.getAction();

        this.animate.set(player.getPos());
    }

    public void update() {
        updateSelectedType();

        if (type != null) {
            updateGhostRotation();
        } else {
            rotationInitialized = false;
        }

        hoverTile.set(
            PlayerSystem.getHoverTileThresholdX(),
            PlayerSystem.getHoverTileThresholdY()
        );

        if (type == null) {
            this.type = action.getSelectedType();
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
                Time.delta(), targetX, targetY, SLIDE_SPEED, animate, animInitialized
            );

    }

    public void render() {

    }

    public void drawBatch() {

        if (type == null || !hoverVisible) return;
        renderGhostOverlay();
        //TODO: render rotation too
        buildGhostLine.drawTilePreview(batch, buildGhostLine.getCurrentTiledLine(), action.getSelectedType(), animateRotation);

    }

    private void renderGhostOverlay() {
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
//        int tx = MathUtils.floor(tmp.x / tile);
//        int ty = MathUtils.floor(tmp.y / tile);
        int tx = (int) hoverTile.x;
        int ty = (int) hoverTile.y;

        if (!world.isInBounds(tx, ty)) return;

        TextureRegion region = assets.getTile(type).getTextureRegion();

        Color color;
        boolean walkAble = world.isWalkable(tx, ty);

        if (walkAble) color = new Color(1f, 1f, 1f, 0.45f); //ghost normal
        else color = new Color(1f, 0.3f, 0.3f, 0.45f); // ghost red

        Artist.DrawBatch(batch, region, animate.x, animate.y,
            // tile / 2 to keep the animation within 1 tile
            tile / 2, tile / 2,
            tile, tile, 1f, 1f, animateRotation, color
        );

    }

    private AssetType getSelectedType() {
        if (action.getSelectedType() == null) return null;
        if (type == null) return PlayerHotbar.getSelectedType();
        if (type == PlayerHotbar.getSelectedType()) return type;
        else return null;
    }

    private void updateSelectedType() {
        this.type = getSelectedType();
    }

    private Direction getDirection() {
        if (action.getSelectedType() == null) return Direction.EAST;
        if (action.getPlacementDirection() == null) return Direction.EAST;
        return action.getPlacementDirection();
    }

    private void updateGhostRotation() {
        float targetRotation = RenderUtils.getRotationDegree(
            getDirection()
        );

        if (!rotationInitialized) {
            animateRotation = targetRotation;
            rotationInitialized = true;
            return;
        }

        float interpolation =
            1f - (float)Math.exp(-ROTATION_SPEED * Time.delta());

        animateRotation =
            MathUtils.lerpAngleDeg(animateRotation, targetRotation, interpolation);
    }



}
