package core.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.World;

public class OverlayRenderer {

    private final Vector3 tmp = new Vector3();
    private final Vector2 bracketAnimate = new Vector2();

    private static final float SLIDE_SPEED = 24f;
    private static final float PADDING = 0.06f;

    private float cornerBracketThickness = 8f;
    private float cornerBracketGapRatio = 0.27f;

    public enum BracketPaddingMode {
        OUTSIDE,   // bracket sits outside tile
        INSIDE,    // bracket sits inside tile
        CENTER       // centered on tile edge (half in, half out)
    }
    private BracketPaddingMode bracketPaddingMode = BracketPaddingMode.CENTER;
    private float bracketSize;

    private float targetX;
    private float targetY;
    private boolean hoverVisible;
    private boolean hoverWalkable;
    private boolean animInitialized;
    private boolean isOverlayRenderAnimationEnabled = true; //true by default

    public void update(World world, Viewport viewport, float delta) {
        drawCornerBrackets(world, viewport, null, cornerBracketGapRatio, cornerBracketThickness);
        if (!hoverVisible) return;

        if (isOverlayRenderAnimationEnabled) {
            animateBracketMove(delta, targetX, targetY, SLIDE_SPEED, bracketAnimate);
        } else {
            bracketAnimate.set(targetX, targetY);
        }
    }

    public void render(World world, Viewport viewport, ShapeRenderer shapeRenderer) {

        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawCornerBrackets(world, viewport, shapeRenderer, cornerBracketGapRatio, cornerBracketThickness);

        shapeRenderer.end();
    }

    private void drawCornerBrackets(World world, Viewport viewport, ShapeRenderer sr, float gapRatio, float thicknessPx) {

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(tmp);

        float tile = world.getTileSize();
        int tx = MathUtils.floor(tmp.x / tile), ty = MathUtils.floor(tmp.y / tile);

        if (!world.isInBounds(tx, ty)) {
            hoverVisible = false;
            animInitialized = false;
            return;
        }

        float wx = tx * tile, wy = ty * tile;
        applyBracketPadding(wx, wy, tile);

        hoverWalkable = world.isWalkable(tx, ty);
        hoverVisible = true;

        if (sr == null) return;

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        float wpp = (cam.viewportWidth * cam.zoom) / Gdx.graphics.getWidth();

        drawBracketShape(sr, bracketAnimate.x, bracketAnimate.y,
            bracketSize, tile * gapRatio, thicknessPx * wpp, hoverWalkable);
    }

    private void drawBracketShape(ShapeRenderer sr, float x, float y, float size,
                                  float gap, float thick, boolean walkAble) {
        float x2 = x + size, y2 = y + size;
        sr.setColor(walkAble ? Color.WHITE : Color.RED);

        sr.rect(x, y2 - thick, gap, thick);       sr.rect(x, y2 - gap, thick, gap);
        sr.rect(x2 - gap, y2 - thick, gap, thick); sr.rect(x2 - thick, y2 - gap, thick, gap);
        sr.rect(x, y, gap, thick);               sr.rect(x, y, thick, gap);
        sr.rect(x2 - gap, y, gap, thick);        sr.rect(x2 - thick, y, thick, gap);
    }

    private void animateBracketMove(float delta, float endX, float endY, float speed, Vector2 anim) {

        if (!animInitialized) {
            anim.set(endX, endY);
            animInitialized = true;
            return;
        }

        float smooth = 1f - (float) Math.exp(-speed * delta);
        anim.x = MathUtils.lerp(anim.x, endX, smooth);
        anim.y = MathUtils.lerp(anim.y, endY, smooth);

        if (Math.abs(anim.x - endX) < 0.001f) anim.x = endX;
        if (Math.abs(anim.y - endY) < 0.001f) anim.y = endY;
    }

    private void applyBracketPadding(float wx, float wy, float tile) {
        float pad = PADDING;

        switch (bracketPaddingMode) {
            case OUTSIDE -> {
                targetX = wx - pad;
                targetY = wy - pad;
                bracketSize = tile + pad * 2f;
            }
            case INSIDE -> {
                targetX = wx + pad;
                targetY = wy + pad;
                bracketSize = tile - pad * 2f;
            }
            case CENTER -> {
                float half = pad * 0.5f;
                targetX = wx - half;
                targetY = wy - half;
                bracketSize = tile + pad;
            }
        }
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

}
