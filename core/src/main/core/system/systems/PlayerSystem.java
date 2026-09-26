package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.app.cores.AppListener;
import core.system.context.PlayerContext;
import core.player.*;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;
import core.player.mechanic.OverlayHelper;
import core.player.mechanic.PlayerController;
import core.render.RenderLayer;
import core.app.cores.GameSysCycle;

import static core.app.Vars.*;

public class PlayerSystem implements AppListener, GameSysCycle {

    private PlayerAction action;
    private PlayerController controller;
    private Overlay overlay;
    private GhostOverlay ghostOverlay;

    public static final float HOVER_THRESHOLD = 0.15f;
    private final Vector3 mouseWorld = new Vector3();
    private static final Vector2 hoverTileThreshold = new Vector2();
    private boolean hoverTargetInitialized;

    public PlayerSystem() {

        this.controller = new PlayerController(
            input,
            window
        );

        player = new Player(world, controller);

        this.action = new PlayerAction(
            world,
            viewport,
            player,
            input,
            uiInputGate,
            shape,
            assets
        );

        this.overlay = new Overlay(
            world,
            window,
            viewport,
            input,
            action,
            assets
        );
        this.ghostOverlay = new GhostOverlay(
            world,
            viewport,
            player,
            action,
            assets,
            action.getBuildGhostLine()
        );

    }

    @Override
    public void update() {
        updateHoverThreshold();

        player.update();
        action.update();
        overlay.update();
        ghostOverlay.update();
    }

    @Override
    public void init() {
        AppListener.super.init();
    }

    @Override
    public void create() {
        AppListener.super.create();
    }

    @Override
    public void render() {
        overlay.render();
    }

    @Override
    public void resize(int width, int height) {
        AppListener.super.resize(width, height);
    }

    @Override
    public void pause() {
        AppListener.super.pause();
    }

    @Override
    public void resume() {
        AppListener.super.resume();
    }

    @Override
    public void drawBatch(SpriteBatch batch) {
        //BEGIN batch
        //deprecated!
        //only needed when there is separate sprite batch for player
        //END batch

        ghostOverlay.drawBatch(batch);
        player.draw(batch);

    }

    @Override
    public void drawShapeRenderer(ShapeRenderer shapeRenderer) {
        action.drawShapeRenderer(shapeRenderer);
        overlay.drawShapeRenderer(shapeRenderer);
    }

    @Override
    public void dispose() {
        action.dispose();
    }

    public void updateHoverThreshold() {
        float tileSize = world.getTileSize();
        OverlayHelper.updateMouseWorld(viewport, mouseWorld);
        if (!hoverTargetInitialized) {
            hoverTileThreshold.set(
                MathUtils.floor(mouseWorld.x / tileSize),
                MathUtils.floor(mouseWorld.y / tileSize)
            );
            hoverTargetInitialized = true;
        }
        OverlayHelper.updateTileWithThreshold(
            mouseWorld,
            tileSize,
            hoverTileThreshold
        );
    }

    public static int getHoverTileThresholdX() {
        return (int) hoverTileThreshold.x;
    }

    public static int getHoverTileThresholdY() {
        return (int) hoverTileThreshold.y;
    }

    public static Vector2 getHoverTileThreshold() {
        return hoverTileThreshold;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.PLAYER_LAYER;
    }


}
