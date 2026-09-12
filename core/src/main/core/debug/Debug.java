package core.debug;

import core.app.GameContext;
import core.app.context.PlayerContext;
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
import core.player.Player;
import core.event.*;

public class Debug {

    private final DebugConfig debugConfig;
    private final InputHandler input;
    private final World world;
    private final Viewport viewport;
    private final Player player;
    private final ShapeRenderer debugShapeRenderer;

    private boolean mapGenReportPending;
    private MapConfig pendingMapConfig;
    private FloorType[][] pendingGrid;

    private float delta;

    private final OrthographicCamera camera;

    private final CameraDebugger      cameraDebugger      = new CameraDebugger();
    private final EventsDebugger      eventsDebugger      = new EventsDebugger();
    private final InputDebugger       inputDebugger       = new InputDebugger();
    private final MapGenDebugger      mapGenDebugger      = new MapGenDebugger();
    private final PerformanceDebugger performanceDebugger = new PerformanceDebugger();
    private final RenderDebugger      renderDebugger      = new RenderDebugger();

    public Debug(DebugConfig debugConfig, World world, Viewport viewport, InputHandler input, Player player, ShapeRenderer debugShapeRenderer) {
        this.debugConfig = debugConfig;
        this.world = world;
        this.input = input;
        this.viewport = viewport;
        this.camera = (OrthographicCamera) viewport.getCamera();
        this.player = player;
        this.debugShapeRenderer = debugShapeRenderer;


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

    public void update(float delta) {
        this.delta = delta;

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

            mapGenDebugger.printReport(pendingMapConfig,  pendingGrid);
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
            debugShapeRenderer.setProjectionMatrix(camera.combined);

            //begin must have end(); at the end.
            Gdx.gl.glLineWidth(0.1f);
            debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            renderDebugger.drawTileBorders(world, camera, debugShapeRenderer);


            //add codes to above not below of this command.
            debugShapeRenderer.end();
            Gdx.gl.glLineWidth(1f);
        }

        if (debugConfig.isEnabled(DebugType.CAMERA)) {
            debugShapeRenderer.setProjectionMatrix(camera.combined);
            debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            cameraDebugger.render(debugShapeRenderer);

            debugShapeRenderer.end();
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

    public PerformanceDebugger getPerformanceDebugger() {
        return performanceDebugger;
    }
    public CameraDebugger getCameraDebugger() {
        return cameraDebugger;
    }
    public RenderDebugger getRenderDebugger() {
        return renderDebugger;
    }
    public MapGenDebugger getMapGenDebugger() {
        return mapGenDebugger;
    }
    public InputDebugger getInputDebugger() {
        return inputDebugger;
    }
    public EventsDebugger getEventsDebugger() {
        return eventsDebugger;
    }

}
