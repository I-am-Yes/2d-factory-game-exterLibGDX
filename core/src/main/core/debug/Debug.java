package core.debug;

import com.badlogic.gdx.graphics.Camera;
import core.app.GameContext;
import data.debug.DebugConfig;
import data.debug.DebugType;
import data.map.asset.FloorType;
import data.map.MapConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.world.World;
import core.Player;
import core.event.*;

public class Debug {

    private final GameContext context;
    private final DebugConfig debugConfig;
    private final InputHandler input;
    private final World world;
    private final Viewport viewport;
    private final Player player;
    private final ShapeRenderer shapeRenderer;

    private boolean mapGenReportPending;
    private MapConfig pendingMapConfig;
    private FloorType[][] pendingGrid;

    private final OrthographicCamera camera;

    private final PerformanceDebugger performanceDebugger = new PerformanceDebugger();
    private final CameraDebugger      cameraDebugger      = new CameraDebugger();
    private final InputDebugger       inputDebugger       = new InputDebugger();
    private final EventsDebugger      eventsDebugger      = new EventsDebugger();

    public Debug(GameContext context, DebugConfig debugConfig) {
        this.debugConfig = debugConfig;
        this.context = context;
        this.input = context.input;
        this.world = context.world;
        this.viewport = context.viewport;
        this.player = context.player;
        this.shapeRenderer = context.shapeRenderer;

        this.camera = (OrthographicCamera) viewport.getCamera();

        Events.on(GameEvent.MapGenerated.class, e -> {
            pendingMapConfig = e.mapConfig;
            pendingGrid = e.floorGrid;
            mapGenReportPending = true;
        });

        if (isAllDebugDisabled()) printEnabledDebugMode();
    }

    public void enableDebugMode(DebugType debugMode) {
        debugConfig.enable(debugMode);
        printEnabledDebugMode();
    }
    public void disableDebugMode(DebugType debugMode) {
        debugConfig.disable(debugMode);
        printEnabledDebugMode();
    }

    public void enableDebugModes(DebugType... debugModes) {
        for (DebugType debugMode : debugModes) {
            enableDebugMode(debugMode);
        }
    }

    public void disableDebugModes(DebugType... debugModes) {
        for (DebugType debugMode : debugModes) {
            disableDebugMode(debugMode);
        }
    }

    public void update() {

        if (debugConfig.isEnabled(DebugType.PERFORMANCE)) {
            if (!debugConfig.isEnabled(DebugType.PERFORMANCE)) return;

            performanceDebugger.setEnable("fps", true, 2f);
            performanceDebugger.setEnable("ram", true, 2f);

            performanceDebugger.update();
        }

        if (debugConfig.isEnabled(DebugType.RENDER)) {
            if (!debugConfig.isEnabled(DebugType.RENDER)) return;


        }

        if (debugConfig.isEnabled(DebugType.INPUT)) {
            if (!debugConfig.isEnabled(DebugType.INPUT)) return;

            inputDebugger.update(input);
        }

        if (mapGenReportPending && debugConfig.isEnabled(DebugType.MAP_GENERATION)) {
            if (!debugConfig.isEnabled(DebugType.MAP_GENERATION)) return;

            MapGenDebugger.printReport(pendingMapConfig,  pendingGrid);
            mapGenReportPending = false;
        }

        if (debugConfig.isEnabled(DebugType.CAMERA)) {
            if (!debugConfig.isEnabled(DebugType.CAMERA)) return;

            cameraDebugger.update(viewport, player, world);
        }

        if (debugConfig.isEnabled(DebugType.EVENT)) {
            if (!debugConfig.isEnabled(DebugType.EVENT)) return;

            eventsDebugger.update();
        }

    }

    public void render() {

        //render tile borders
        if (debugConfig.isEnabled(DebugType.RENDER)) {
            shapeRenderer.setProjectionMatrix(camera.combined);

            //begin must have end(); at the end.
            Gdx.gl.glLineWidth(0.1f);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            RenderDebugger.drawTileBorders(world, camera, shapeRenderer);


            //add codes to above not below of this command.
            shapeRenderer.end();
            Gdx.gl.glLineWidth(1f);
        }

        if (debugConfig.isEnabled(DebugType.CAMERA)) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            cameraDebugger.render(shapeRenderer);

            shapeRenderer.end();
        }

    }

    public void isEnabled(DebugType debugMode) {
        debugConfig.isEnabled(debugMode);
    }

    private void printEnabledDebugMode() {
        if (!debugConfig.getEnabledModes().isEmpty()) {
            System.out.println("Enabled debug mode: " + debugConfig.getEnabledModes());
        } else {
            System.out.println("Enabled debug mode: [None]");
        }
    }


    public boolean isAllDebugEnabled() {
        return debugConfig.isAllDebugEnabled();
    }
    public boolean isAllDebugDisabled() {
        return debugConfig.isAllDebugDisabled();
    }

}
