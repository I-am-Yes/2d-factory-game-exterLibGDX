package core.system.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.app.cores.AppListener;
import core.app.vars.Cores;
import core.player.*;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;
import core.player.mechanic.OverlayHelper;
import core.player.mechanic.PlayerController;
import core.render.RenderLayer;

import static core.app.vars.Vars.*;

public class PlayerSystem implements AppListener {

    private PlayerAction action;
    private PlayerController controller;
    private Overlay overlay;
    private GhostOverlay ghostOverlay;

    public static final float HOVER_THRESHOLD = 0.15f;
    private final Vector3 mouseWorld = new Vector3();
    private static final Vector2 hoverTileThreshold = new Vector2();
    private boolean hoverTargetInitialized;

    public PlayerSystem() {

        this.controller = new PlayerController();

        this.action = new PlayerAction();

        player = new Player(controller, action);


        this.overlay = new Overlay(action);
        this.ghostOverlay = new GhostOverlay();

    }

    @Override
    public void update() {
        updateHoverThreshold();

        player.update();
        action.update();
        overlay.update();
        ghostOverlay.update();

        overlay.render();

        drawBatch();
        drawShapeRenderer();
    }

    @Override
    public void init() {
        AppListener.super.init();
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

    public void drawBatch() {
        //BEGIN batch
        //deprecated!
        //only needed when there is separate sprite batch for player
        //END batch

        ghostOverlay.drawBatch();
        player.draw();

    }

    public void drawShapeRenderer() {
        action.drawShapeRenderer();
        overlay.drawShapeRenderer();
    }

    @Override
    public void dispose() {
        action.dispose();
    }

    public void updateHoverThreshold() {
        float tileSize = world.getTileSize();
        OverlayHelper.updateMouseWorld(Cores.viewport, mouseWorld);
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

    public RenderLayer renderLayer() {
        return RenderLayer.PLAYER_LAYER;
    }


}
