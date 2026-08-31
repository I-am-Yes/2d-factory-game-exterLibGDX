package core.debug;

import Data.debug.DebugConfig;
import Data.debug.DebugType;
import Data.map.FloorType;
import Data.map.MapConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.World;
import core.player;

public class Debug {

    private final DebugConfig debugConfig;


    private boolean mapGenReportPending;
    private MapConfig pendingMapConfig;
    private FloorType[][] pendingGrid;

    private final PerformanceDebugger performanceDebugger = new PerformanceDebugger();
    private final CameraDebugger      cameraDebugger      = new CameraDebugger();
    private final InputDebugger       inputDebugger       = new InputDebugger();

    public Debug(DebugConfig debugConfig) {
        this.debugConfig = debugConfig;


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

    public void update(InputHandler input, Viewport viewport, player player, World world) {

        if (debugConfig.isEnabled(DebugType.PERFORMANCE)) {
            if (!debugConfig.isEnabled(DebugType.PERFORMANCE)) return;

            setPrintFPStoConsole(true, 0.5f);

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

    }

    public void render(World world, OrthographicCamera camera, ShapeRenderer shapeRenderer) {

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

    //temporary helper method to call map report debug, update to event later
    //TODO: update to event method.
    public void onMapUpdate(MapConfig mapConfig, FloorType[][] grid) {
        this.pendingMapConfig = mapConfig;
        this.pendingGrid = grid;
        this.mapGenReportPending = true;
    }


    public void setPrintFPStoConsole(boolean bool, float fpsLogInterval) {
        performanceDebugger.setPrintFPStoConsole(bool, fpsLogInterval);
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
