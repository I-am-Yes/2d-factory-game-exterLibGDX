package core.player.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.app.context.GameContext;
import core.player.PlayerAction;
import core.system.PlayerSystem;
import core.world.World;

public class Overlay {
    private final World world;
    private final Window window;
    private final Viewport viewport;
    private final InputHandler input;
    private final PlayerAction playerAction;
    private final AssetsHandler assetsHandler;
    private final ShapeRenderer shapeRenderer;

    private final Vector3 tmp = new Vector3();
    private final Vector2 bracketAnimate = new Vector2();
    private final Vector2 hoverTile = new Vector2();

    private float SLIDE_SPEED = 32f;
    private float PADDING = 0.06f;
    private float BRACKET_RESIZE_SPEED = 8f;

    private float cornerBracketThickness = 6f;
    private float cornerBracketGapRatio = 0.27f;


    public enum BracketPaddingMode {
        OUTSIDE,   // bracket sits outside tile
        INSIDE,    // bracket sits inside tile
        CENTER       // centered on tile edge (half in, half out)
    }
    private BracketPaddingMode defaultPaddingMode = BracketPaddingMode.CENTER;
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

    public Overlay(GameContext context, PlayerAction action) {
        this.world = context.world;
        this.window = context.window;
        this.viewport = context.viewport;
        this.input = context.input;
        this.playerAction = action;
        this.assetsHandler = context.assets;
        this.shapeRenderer = context.shapeRenderer;

        //TODO: change bracket color to light blue when hovering overlay on ghost tile
        setBracketPaddingMode(defaultPaddingMode);

    }

    public void update(float delta) {
        this.delta = delta;

        hoverTile.set(
            PlayerSystem.getHoverTileX(),
            PlayerSystem.getHoverTileY()
        );

        //TODO: change overlay bracket to render, resize, update base on current tile, not player mouse
        animateBracketSize();
        if (!hoverVisible) return;


        if (isOverlayRenderAnimationEnabled) {
            animInitialized = OverlayHelper.animateMove(delta, targetX, targetY, SLIDE_SPEED, bracketAnimate, animInitialized);
        } else {
            bracketAnimate.set(targetX, targetY);
        }

        if (world.isGhostAtWorld(input.getMouseWorldPos(viewport))) {
            setExpandBracket(true, 0.1f);
        } else {
            setExpandBracket(false, 0.1f);
        }

        if (playerAction != null && playerAction.getSelectedType() != null) {
            Gdx.app.log(
                "OverlayRenderer",
                "Selected Type: " + playerAction.getSelectedType()
            );
            //TODO: fix to use the actual object size on camera.
            setExpandBracket(true,
                (float) assetsHandler.getTextureHeight(playerAction.getSelectedType())
                    / world.getMapConfig().getTilePixel() / 6f );
        }

    }

    public void render() {
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawCornerBrackets(world, viewport, shapeRenderer, cornerBracketGapRatio, cornerBracketThickness);

        shapeRenderer.end();
    }



    private void setExpandBracket(boolean bool, float expandSize) {
        setBracketPaddingMode(bool ? BracketPaddingMode.OUTSIDE : defaultPaddingMode);
        setBracketPadding(bool ? +expandSize : defaultPadding);
    }

    private void drawCornerBrackets(World world, Viewport viewport, ShapeRenderer sr, float gapRatio, float thicknessPx) {

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

        if (sr == null) return;

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        float wpp = (cam.viewportWidth * cam.zoom) / Gdx.graphics.getWidth();

        float drawX = bracketAnimate.x - bracketSize * 0.5f;
        float drawY = bracketAnimate.y - bracketSize * 0.5f;
        drawBracketShape(sr, drawX, drawY,
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
