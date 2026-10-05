package core.client.player.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import core.client.player.PlayerAction;
import core.system.systems.PlayerSystem;

import arcane.*;
import arcane.math.*;
import arcane.graphics.*;

import static arcane.Cores.*;
import static core.app.Vars.*;

public class Overlay {
    private final PlayerAction playerAction;

    private final Vector3 tmp = new Vector3();
    private final Vector2 bracketAnimate = new Vector2();
    private final Vector2 hoverTile = new Vector2();

    private float SLIDE_SPEED = 32f;
    private float PADDING = 0.06f;
    private final float BRACKET_RESIZE_SPEED = 8f;

    private float cornerBracketThickness = 6f;
    private float cornerBracketGapRatio = 0.27f;


    public enum BracketPaddingMode {
        OUTSIDE,   // bracket sits outside tile
        INSIDE,    // bracket sits inside tile
        CENTER       // centered on tile edge (half in, half out)
    }
    private final BracketPaddingMode defaultPaddingMode = BracketPaddingMode.CENTER;
    private BracketPaddingMode bracketPaddingMode = defaultPaddingMode;

    private float bracketSize;
    private float targetBracketSize;

    private final float defaultPadding = 0.06f;
    private final float defaultSpeed = 32f;

    private float delta;
    private float targetX;
    private float targetY;
    private boolean hoverVisible;
    private boolean hoverWalkable;
    private boolean animInitialized;
    private boolean isOverlayRenderAnimationEnabled = true; //true by default

    public Overlay(PlayerAction action) {
        this.playerAction = action;

        //TODO: change bracket color to light blue when hovering overlay on ghost tile
        setBracketPaddingMode(defaultPaddingMode);

    }

    public void update() {
        delta = Time.delta();

        hoverTile.set(
            PlayerSystem.getHoverTileThresholdX(),
            PlayerSystem.getHoverTileThresholdY()
        );

        //TODO: change overlay bracket to render, resize, update base on current tile, not player mouse
        animateBracketSize();
        if (!hoverVisible) return;


        if (isOverlayRenderAnimationEnabled) {
            animInitialized = OverlayHelper.animateMove(delta, targetX, targetY, SLIDE_SPEED, bracketAnimate, animInitialized);
        } else {
            bracketAnimate.set(targetX, targetY);
        }

        setExpandBracket(world.isGhostAtWorld(input.getMouseWorldPos(viewport)), 0.1f);

        if (playerAction != null && playerAction.getSelectedType() != null) {
//            Gdx.app.log(
//                "OverlayRenderer",
//                "Selected Type: " + playerAction.getSelectedType()
//            );
            //TODO: fix to use the actual object size on camera.
            setExpandBracket(true,
                (float) assets.getTextureHeight(playerAction.getSelectedType())
                    / world.getMapConfig().getTilePixel() / 6f );
        }

    }

    public void render() {
    }

    public void drawShapeRenderer() {

        drawCornerBrackets(cornerBracketGapRatio, cornerBracketThickness);

    }



    private void setExpandBracket(boolean bool, float expandSize) {
        setBracketPaddingMode(bool ? BracketPaddingMode.OUTSIDE : defaultPaddingMode);
        setBracketPadding(bool ? +expandSize : defaultPadding);
    }

    private void drawCornerBrackets(float gapRatio, float thicknessPx) {

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
//        int tx = MathUtils.floor(tmp.x / tile),
//            ty = MathUtils.floor(tmp.y / tile);

//        //threshold
        int tx = (int) hoverTile.x;
        int ty = (int) hoverTile.y;

        if (!world.isInBounds(tx, ty)) {
            hoverVisible = false;
            animInitialized = false;
            return;
        }

        float wx = tx * tile, wy = ty * tile;
        applyBracketPadding(wx, wy, tile);

        hoverWalkable = world.isWalkable(tx, ty);
        hoverVisible = true;

        if (shape == null) return;

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        float wpp = (cam.viewportWidth * cam.zoom) / Gdx.graphics.getWidth();

        float drawX = bracketAnimate.x - bracketSize * 0.5f;
        float drawY = bracketAnimate.y - bracketSize * 0.5f;
        drawBracketShape(drawX, drawY,
            bracketSize, tile * gapRatio, thicknessPx * wpp, hoverWalkable);
    }

    //TODO: shape draw rect helper
    private void drawBracketShape(float x, float y, float size,
                                  float gap, float thick, boolean walkAble) {
        float x2 = x + size, y2 = y + size;
        Color color = walkAble ? Color.WHITE : Color.RED;

        Artist.DrawRect(x, y2 - thick, gap, thick, color);
        Artist.DrawRect(x, y2 - gap, thick, gap, color);
        Artist.DrawRect(x2 - gap, y2 - thick, gap, thick, color);
        Artist.DrawRect(x2 - thick, y2 - gap, thick, gap, color);
        Artist.DrawRect(x, y, gap, thick, color);
        Artist.DrawRect(x, y, thick, gap, color);
        Artist.DrawRect(x2 - gap, y, gap, thick, color);
        Artist.DrawRect(x2 - thick, y, thick, gap, color);
    }

    private void animateBracketSize() {
        float alpha = 1f -
            (float) Math.exp(-BRACKET_RESIZE_SPEED * delta);

        bracketSize = MathUtils.lerp(
            bracketSize,
            targetBracketSize,
            alpha
        );

    }

    private void applyBracketPadding(float wx, float wy, float tile) {
        float pad = PADDING;
        float centerX = wx + tile * 0.5f;
        float centerY = wy + tile * 0.5f;
        switch (bracketPaddingMode) {
            case OUTSIDE -> {
                targetBracketSize = tile + pad * 2f;
            }
            case INSIDE -> {
                targetBracketSize = Math.max(0f, tile - pad * 2f);
            }
            case CENTER -> {
                targetBracketSize = tile + pad;
            }
        }
        targetX = centerX;
        targetY = centerY;
    }

    public float getCornerBracketGapRatio() {
        return cornerBracketGapRatio;
    }

    public void setCornerBracketGapRatio(float cornerBracketGapRatio) {
        this.cornerBracketGapRatio = cornerBracketGapRatio;
    }

    public float getCornerBracketThickness() {
        return cornerBracketThickness;
    }

    public void setCornerBracketThickness(float cornerBracketThickness) {
        this.cornerBracketThickness = cornerBracketThickness;
    }

    public boolean isOverlayRenderAnimationEnabled() {
        return isOverlayRenderAnimationEnabled;
    }

    public void setOverlayRenderAnimationEnabled(boolean overlayRenderAnimationEnabled) {
        isOverlayRenderAnimationEnabled = overlayRenderAnimationEnabled;
    }

    public BracketPaddingMode getBracketPaddingMode() {
        return bracketPaddingMode;
    }

    public void setBracketPaddingMode(BracketPaddingMode bracketPaddingMode) {
        this.bracketPaddingMode = bracketPaddingMode;
    }

    public void setBracketPadding(float padding) {
        this.PADDING = padding;
    }

    public void setBracketSlideSpeed(float speed) {
        this.SLIDE_SPEED = speed;
    }

}
