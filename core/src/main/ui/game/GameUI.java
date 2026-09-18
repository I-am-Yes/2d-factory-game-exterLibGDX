package ui.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Window;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import core.app.GameLoop;
import core.controller.camera.CameraController;
import core.system.systems.PlayerSystem;
import core.world.World;
import core.world.chunk.Chunk;
import ui.Style;
import ui.UiHelper;

import java.util.Locale;
import java.util.function.Supplier;


public class GameUI {
    private final World world;
    private final Window window;
    private final Viewport viewport;
    private final Stage stage;
    private final Skin skin;
    private final UiHelper uiHelper;
    private final InputHandler input;
    private final Supplier<CameraController> cameraControllerProvider;

    private final Label.LabelStyle textStyle1;

    private final Label gameSpeedlabel;
    private final Label gameTimelabel;
    private final Label realTimelabel;

    private final Label VSyncLabel;
    private final Label UPSLabel;
    private final Label FPSLabel;
    private final Label avgFPSLabel;

    private final Label usedMemoryLabel;
    private final Label totalMemoryLabel;
    private final Label maxMemoryLabel;

    private final Label viewModeLabel;
    private final Label cameraZoomLabel;
    private final Label cameraQualityLabel;

    private final Label worldSeedLabel;
    private final Label worldSizeLabel;
    private final Label worldChunkSizeLabel;
    private final Label worldVisibleTileslabel;
    private final Label worldVisibleChunksLabel;
    private final Label worldLoadedChunkLabel;
    private final Label worldChunkBoundsLabel;
    private final Label worldMousePosLabel;
    private final Label worldMouseHoverPosLabel;
    private final Label worldMouseTileLabel;
    private final Label worldMouseTileNameLabel;
    private final Label worldTileRotationLabel;

    private final Table WorldInfoList;
    private final Table PerformanceList;

    private Vector2 hoverThreshold = new Vector2();
    private final int memoryUpdateDelay = 1000;

    public GameUI(World world, Window window, Viewport viewport, Stage stage, Skin skin, UiHelper uiHelper, InputHandler input, Supplier<CameraController> cameraControllerProvider) {
        this.world = world;
        this.window = window;
        this.viewport = viewport;
        this.stage = stage;
        this.skin = skin;
        this.uiHelper = uiHelper;
        this.input = input;
        this.cameraControllerProvider = cameraControllerProvider;

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        WorldInfoList = uiHelper.createAndAddItems(stage, Align.right,
            worldSeedLabel = uiHelper.createTextLabel("Seed: ", textStyle1),
            worldSizeLabel = uiHelper.createTextLabel("World Size: ", textStyle1),
            worldChunkBoundsLabel = uiHelper.createTextLabel("Chunk Bounds: ", textStyle1),
            worldMousePosLabel = uiHelper.createTextLabel("Mouse Pos: ", textStyle1),
            worldMouseHoverPosLabel = uiHelper.createTextLabel("Mouse Hover Pos: ", textStyle1),
            worldVisibleTileslabel = uiHelper.createTextLabel("Visible Tiles: ", textStyle1),
            worldVisibleChunksLabel = uiHelper.createTextLabel("Visible Chunks: ", textStyle1),
            worldLoadedChunkLabel = uiHelper.createTextLabel("loaded Chunk: ", textStyle1),
            worldChunkSizeLabel = uiHelper.createTextLabel("Chunk Size: ", textStyle1),
            worldMouseTileLabel = uiHelper.createTextLabel("Mouse Tile: ", textStyle1),
            worldMouseTileNameLabel = uiHelper.createTextLabel("Tile Name: ", textStyle1),
            worldTileRotationLabel = uiHelper.createTextLabel("Tile Rotation: ", textStyle1)
        );

        WorldInfoList.setTouchable(Touchable.disabled);
        WorldInfoList.top().right();

        PerformanceList = uiHelper.createAndAddItems(stage, Align.left,
            gameSpeedlabel = uiHelper.createTextLabel("Game Speed: ", textStyle1),
            gameTimelabel = uiHelper.createTextLabel("Game Time: ", textStyle1),
            realTimelabel = uiHelper.createTextLabel("Real Time: ", textStyle1),

            VSyncLabel = uiHelper.createTextLabel("V-Sync: off", textStyle1),
            UPSLabel = uiHelper.createTextLabel("UPS: ", textStyle1),
            FPSLabel = uiHelper.createTextLabel("FPS: ", textStyle1),
            avgFPSLabel = uiHelper.createTextLabel("Avg FPS: ", textStyle1),

            usedMemoryLabel = uiHelper.createTextLabel("Used Mem: ", textStyle1),
            totalMemoryLabel = uiHelper.createTextLabel("Total Mem: ", textStyle1),
            maxMemoryLabel = uiHelper.createTextLabel("Max Mem: ", textStyle1),

            cameraZoomLabel = uiHelper.createTextLabel("Camera Zoom: ", textStyle1),
            cameraQualityLabel = uiHelper.createTextLabel("Camera Quality: ", textStyle1),
            viewModeLabel = uiHelper.createTextLabel("View Mode: ", textStyle1)
        );

        PerformanceList.setTouchable(Touchable.disabled);
        PerformanceList.top().left();

    }

    public void update(float deltaTime) {
        updateWorldInfoLabel();
        updateTimerLabel();
        updateUPSLabel();
        updateFPSLabel();
        updateMemoriesLabel();
        updateVSyncLabel();
        updateCameraLabel();
    }

    public void setDebugInfoVisible(boolean visible) {
        PerformanceList.setVisible(visible);
        WorldInfoList.setVisible(visible);
    }

    public boolean isDebugInfoVisible() {
        return PerformanceList.isVisible() && WorldInfoList.isVisible();
    }

    private void updateWorldInfoLabel() {
        var chunkManager = world.getChunkManager();
        var camera = (OrthographicCamera) viewport.getCamera();
        var loadedBounds = chunkManager.getLoadedMapBounds();

        worldSeedLabel.setText("Seed: " + world.getSeed());
        worldVisibleTileslabel.setText(
            "Visible Tiles: " + formatNumber(chunkManager.getVisibleTileCount(camera, world.getTileSize()))
        );
        worldVisibleChunksLabel.setText(
            "Visible Chunks: " + formatNumber(chunkManager.getVisibleChunkCount(camera, world.getTileSize()))
        );
        worldLoadedChunkLabel.setText("Loaded Chunks: " + formatNumber(chunkManager.getChunkCounts()));
        worldChunkSizeLabel.setText("Chunk Size: " + Chunk.SIZE);
        worldSizeLabel.setText(
            "World Size: " + loadedBounds.widthInTiles() + "x" + loadedBounds.heightInTiles()
        );
        worldChunkBoundsLabel.setText(
            "Chunk Bounds: "
                + loadedBounds.widthInChunks()
                + "x"
                + loadedBounds.heightInChunks()
        );

        worldMouseTileLabel.setText(
            "Mouse Tile: " + world.getTileAssetName(PlayerSystem.getHoverTileThreshold())
        );

        worldMouseTileNameLabel.setText(
            "Tile Name: " + world.getTileName(PlayerSystem.getHoverTileThreshold())
        );

        worldMousePosLabel.setText(
            "mouse Tile: " + world.worldToTileX((int) input.getMouseWorldPos(viewport).x) + "x, " + world.worldToTileY((int) input.getMouseWorldPos(viewport).y) + "y"
        );

        worldMouseHoverPosLabel.setText(
            "Hover Tile: " + PlayerSystem.getHoverTileThresholdX() + "x, " + PlayerSystem.getHoverTileThresholdY() + "y"
        );

        worldTileRotationLabel.setText(
            //TODO: add rotation getter to this text label
            "Tile Rotation: " + "null"
        );
    }

    private void updateVSyncLabel() {
        VSyncLabel.setText("V-Sync: " + (window.isVSync() ? "on" : "off"));
    }

    private void updateUPSLabel() {
        UPSLabel.setText("UPS: " + GameLoop.getCurrentUPS() + "/" + GameLoop.getTargetUPS());
    }

    private void updateFPSLabel() {
        FPSLabel.setText("FPS: " + window.getLatestFrameRateAfterDelay(100));
        avgFPSLabel.setText("Avg FPS: " + (int) window.getAverageFrameRate(1000));
    }

    private void updateTimerLabel() {
        gameSpeedlabel.setText("Game Speed: " + GameLoop.getGameSpeed() + "x");
        gameTimelabel.setText("Game Time: " + formatTime(GameLoop.getTotalGameTime()));
        realTimelabel.setText("Real Time: " + formatTime(GameLoop.getTotalRealTime()));
    }

    private void updateMemoriesLabel() {
        usedMemoryLabel.setText("Used Mem: " + formatMemoryMB(window.getUsedMemory()));
        totalMemoryLabel.setText("Total Mem: " + formatMemoryMB(window.getTotalMemory()));
        maxMemoryLabel.setText("Max Mem: " + formatMemoryMB(window.getMaxMemory()));
    }

    private void updateCameraLabel() {
        CameraController cameraController = cameraControllerProvider.get();

        viewModeLabel.setText("View Mode: " + cameraController.getCurrentViewMode());
        cameraZoomLabel.setText("Camera Zoom: " + cameraController.getZoomValue());
        cameraQualityLabel.setText("Camera Quality: " + world.getCurrentRenderDetail());
    }

    private String formatMemoryMB(long megabytes) {
        return String.format(
            Locale.ROOT,
            "%,d MB",
            megabytes
        );
    }

    private String formatNumber(double number) {
        if (number >= 1_000_000f) {
            return String.format("%.1fM", number / 1_000_000f);
        }

        if (number >= 1_000f) {
            return String.format("%.1fK", number / 1_000f);
        }

        return String.format("%.0f", number);
    }

    private String formatTime(float time) {
        int seconds = (int) time;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

}
