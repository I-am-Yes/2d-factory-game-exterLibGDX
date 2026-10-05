package core.system.systems;

import arcane.*;
import arcane.math.*;

import com.badlogic.gdx.math.Vector3;
import core.client.*;
import core.client.player.*;
import core.client.player.mechanic.*;

import core.config.*;
import core.config.api.*;
import core.config.camera.*;
import core.client.camera.*;

import java.util.Map;

import static core.app.Vars.*;

public class PlayerSystem implements ApplicationListener {

    private final PlayerAction action;
    private final PlayerController controller;
    private final Overlay overlay;
    private final GhostOverlay ghostOverlay;

    private CursorController cursor;

    public static final float HOVER_THRESHOLD = 0.15f;
    private final Vector3 mouseWorld = new Vector3();
    private static final Vector2 hoverTileThreshold = new Vector2();
    private boolean hoverTargetInitialized;

    public PlayerSystem() {
        InitCamera();

        this.controller = new PlayerController();
        this.action = new PlayerAction();

        player = new Player(controller, action);

        this.overlay = new Overlay(action);
        this.ghostOverlay = new GhostOverlay();

    }

    public void InitCamera() {
        CameraSettings cameraSettings = new CameraSettings();
        this.cursor = new CursorController().loadCursor();

        ScriptConfigLoader.loadAll(
            "scripts",
            Map.of("camera", new CameraScriptApi(cameraSettings))
        );

        cameraControl = new CameraControl(cameraSettings);
    }

    @Override
    public void update() {
        updateHoverThreshold();

        cameraControl.update();

        player.update();
        action.update();
        overlay.update();
        ghostOverlay.update();

        overlay.render();

        drawBatch();
        drawShapeRenderer();
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
        cursor.dispose();
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

}
