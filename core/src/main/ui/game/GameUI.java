package ui.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Window;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import core.app.GameLoop;
import core.controller.camera.CameraController;
import core.player.PlayerAction;
import core.system.systems.InterfaceSystem;
import core.system.systems.PlayerSystem;
import core.world.World;
import core.world.chunk.Chunk;
import ui.Style;
import ui.UiHelper;
import java.util.List;
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

    private Table WorldInfoList = new Table();
    private Table PerformanceList = new Table();
    private List<Table> panelsList = List.of(
        WorldInfoList,
        PerformanceList
    );

    private Vector2 hoverThreshold = new Vector2();
    private final int memoryUpdateDelay = 1000;

    private enum DebugInfoModes {
        OFF,
        SIMPLE,
        DEFAULT,
        MINIMAL,
        MEDIUM,
        HIGH,
        FULL,
        CUSTOM
    }
    private DebugInfoModes currentMode = DebugInfoModes.FULL;

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

        uiHelper.addTableRows(stage, WorldInfoList.top().right(), Align.right,
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

        uiHelper.addTableRows(stage, PerformanceList.top().left(), Align.left,
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

        for (Actor panel : panelsList) {
            panel.setTouchable(Touchable.disabled);
        }

        setDebugInfoMode(currentMode);
    }

    public void update(float deltaTime) {

        updateWorldInfoLabel();

        updateUPSLabel();
        updateFPSLabel();

    }

    public void tickUpdate() {
        //for expensive operations that don't need to be updated every frame
        //or for operations that need to be updated frequently

        updateTimerLabel();
        updateVSyncLabel();
        updateMemoriesLabel();
        updateCameraLabel();
    }

    public void setDebugInfoVisible(boolean visible) {
        panelsList.forEach(panel -> panel.setVisible(visible));
    }

    public boolean isDebugInfoVisible() {
        return panelsList.stream().allMatch(Actor::isVisible);
    }

    ////Deprecated
    private void updateUiScaling() {
        float oldScale = 0.0f;
        float scale = InterfaceSystem.getUiScale();
//        if (scale < 1) {
//            throw new IllegalStateException("UI scale must be >= than 1. Current scale: " + scale);
//        }
//        if (WorldInfoList.getChild(0) != null) {
//            oldScale = WorldInfoList.getChild(0).getScaleX();
//            if (scale == oldScale) return;
//        }
//
//        WorldInfoList.setScale(scale);
//        for (Actor actor : WorldInfoList.getChildren()) {
//            if (actor instanceof Label label) {
//                label.setFontScale(oldScale*scale);;
//            }
//        }
//
//        PerformanceList.setScale(scale);
//        for (Actor actor : PerformanceList.getChildren()) {
//            if (actor instanceof Label label) {
//                label.setFontScale(oldScale*scale);
//            }
//        }
    }

    //check if the label actually needs to be updated,
    // if it's not visible or removed from stage, no need to update it
    private boolean shouldUpdate(Actor actor) {
        if (actor.getStage() == null) {
            return false; // Removed from its table/stage
        }
        for (Actor current = actor; current != null; current = current.getParent()) {
            if (!current.isVisible()) {
                return false;
            }
        }
        return true;
    }

    private void updateWorldInfoLabel() {
        var chunkManager = world.getChunkManager();
        var camera = (OrthographicCamera) viewport.getCamera();
        var loadedBounds = chunkManager.getLoadedMapBounds();

        if (shouldUpdate(worldSeedLabel)) worldSeedLabel.setText("Seed: " + world.getSeed());

        if (shouldUpdate(worldVisibleTileslabel))
            worldVisibleTileslabel.setText(
                "Visible Tiles: " + formatNumber(chunkManager.getVisibleTileCount(camera, world.getTileSize()))
            );
        if (shouldUpdate(worldVisibleChunksLabel)) {
            worldVisibleChunksLabel.setText(
                "Visible Chunks: " + formatNumber(chunkManager.getVisibleChunkCount(camera, world.getTileSize()))
            );
        }
        if (shouldUpdate(worldLoadedChunkLabel)) {
            worldLoadedChunkLabel.setText("Loaded Chunks: " + formatNumber(chunkManager.getChunkCounts()));
        }
        if (shouldUpdate(worldChunkSizeLabel)) {
            worldChunkSizeLabel.setText("Chunk Size: " + Chunk.SIZE);
        }
        if (shouldUpdate(worldSizeLabel)) {
            worldSizeLabel.setText(
                "World Size: " + loadedBounds.widthInTiles() + "x" + loadedBounds.heightInTiles()
            );
        }
        if (shouldUpdate(worldChunkBoundsLabel)) {
            worldChunkBoundsLabel.setText(
                "Chunk Bounds: "
                    + loadedBounds.widthInChunks()
                    + "x"
                    + loadedBounds.heightInChunks()
            );
        }

        if (shouldUpdate(worldMouseTileLabel)) {
            worldMouseTileLabel.setText(
                "Mouse Tile: " + world.getTileAssetName(PlayerSystem.getHoverTileThreshold())
            );
        }

        if (shouldUpdate(worldMouseTileNameLabel)) {
            worldMouseTileNameLabel.setText(
                "Tile Name: " + world.getTileName(PlayerSystem.getHoverTileThreshold())
            );
        }

        worldMousePosLabel.setText(
            "mouse Tile: " + world.worldToTileX((int) input.getMouseWorldPos(viewport).x) + "x, " + world.worldToTileY((int) input.getMouseWorldPos(viewport).y) + "y"
        );
        if (shouldUpdate(worldMousePosLabel)) {
            worldMousePosLabel.setText(
                "mouse Tile: " + world.worldToTileX((int) input.getMouseWorldPos(viewport).x) + "x, " + world.worldToTileY((int) input.getMouseWorldPos(viewport).y) + "y"
            );
        }

        if (shouldUpdate(worldMouseHoverPosLabel)) {
            worldMouseHoverPosLabel.setText(
                "Hover Tile: " + PlayerSystem.getHoverTileThresholdX() + "x, " + PlayerSystem.getHoverTileThresholdY() + "y"
            );
        }

        if (shouldUpdate(worldTileRotationLabel)) {
            worldTileRotationLabel.setText(
                "Tile Rotation: " + PlayerAction.getPlacementDirection()
            );
        }
    }

    private void updateVSyncLabel() {
        if (shouldUpdate(VSyncLabel)) {
            VSyncLabel.setText("V-Sync: " + (window.isVSync() ? "on" : "off"));
        }
    }

    private void updateUPSLabel() {
        if (shouldUpdate(UPSLabel)) {
            UPSLabel.setText("UPS: " + GameLoop.getCurrentUPS() + "/" + GameLoop.getTargetUPS());
        }
    }

    private void updateFPSLabel() {
        if (shouldUpdate(FPSLabel)) {
            FPSLabel.setText("FPS: " + window.getLatestFrameRateAfterDelay(400));
        }
        if (shouldUpdate(avgFPSLabel)) {
            avgFPSLabel.setText("Avg FPS: " + (int) window.getAverageFrameRate(1000));
        }
    }

    private void updateTimerLabel() {
        if (shouldUpdate(gameSpeedlabel))
            gameSpeedlabel.setText("Game Speed: " + GameLoop.getGameSpeed() + "x");
        if (shouldUpdate(gameTimelabel))
            gameTimelabel.setText("Game Time: " + formatTime(GameLoop.getTotalGameTime()));
        if (shouldUpdate(realTimelabel))
            realTimelabel.setText("Real Time: " + formatTime(GameLoop.getTotalRealTime()));

    }

    private void updateMemoriesLabel() {
        if (shouldUpdate(usedMemoryLabel))
            usedMemoryLabel.setText("Used Mem: " + formatMemoryMB(window.getUsedMemory()));
        if (shouldUpdate(totalMemoryLabel))
            totalMemoryLabel.setText("Total Mem: " + formatMemoryMB(window.getTotalMemory()));
        if (shouldUpdate(maxMemoryLabel))
            maxMemoryLabel.setText("Max Mem: " + formatMemoryMB(window.getMaxMemory()));

    }

    private void updateCameraLabel() {
        CameraController cameraController = cameraControllerProvider.get();

        if (shouldUpdate(viewModeLabel)) {
            viewModeLabel.setText("View Mode: " + cameraController.getCurrentViewMode());
        }
        if (shouldUpdate(worldSizeLabel)) {
            cameraZoomLabel.setText("Camera Zoom: " + cameraController.getZoomValue());
        }
        if (shouldUpdate(cameraQualityLabel)) {
            cameraQualityLabel.setText("Camera Quality: " + world.getCurrentRenderDetail());
        }
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

    private void clearDebugPanels() {
        for (Table panel : panelsList) {
            panel.clearChildren();
        }
    }

    private void addLabels(Table panel, int alignment, Actor... labels) {
        uiHelper.addTableRows(
            stage, panel,
            alignment, labels
        );
    }

    public void setCustomDebugInfo(Actor[] labelsLefts, Actor[] labelsRights) {
        currentMode = DebugInfoModes.CUSTOM;
        clearDebugPanels();

        addLabels(
            PerformanceList, Align.left,
            labelsLefts
        );

        addLabels(
            WorldInfoList, Align.right,
            labelsRights
        );
    }

    public void setDebugInfoMode(DebugInfoModes mode) {
        currentMode = mode;

        clearDebugPanels();

        switch (mode) {
            case OFF -> {
                // Both tables remain empty.
            }

            case SIMPLE -> {
                addLabels(
                    PerformanceList, Align.left,
                    FPSLabel, UPSLabel, usedMemoryLabel
                );
            }

            case MINIMAL -> {
                addLabels(
                    PerformanceList, Align.left,
                    FPSLabel,
                    UPSLabel
                );

                addLabels(
                    WorldInfoList, Align.right,
                    worldMouseTileLabel
                );
            }

            case DEFAULT -> {
                addLabels(
                    PerformanceList, Align.left,
                    FPSLabel,
                    avgFPSLabel,
                    UPSLabel,
                    usedMemoryLabel
                );

                addLabels(
                    WorldInfoList, Align.right,
                    worldMouseTileLabel,
                    worldMousePosLabel
                );
            }

            case MEDIUM -> {
                addLabels(
                    PerformanceList, Align.left,
                    FPSLabel,
                    avgFPSLabel,
                    UPSLabel,
                    VSyncLabel,
                    usedMemoryLabel,
                    cameraZoomLabel,
                    cameraQualityLabel
                );

                addLabels(
                    WorldInfoList, Align.right,
                    worldMouseTileLabel,
                    worldMouseTileNameLabel,
                    worldVisibleChunksLabel,
                    worldLoadedChunkLabel
                );
            }

            case HIGH -> {
                // Add your HIGH labels here.
            }

            case FULL -> {
                addLabels(
                    PerformanceList, Align.left,
                    gameSpeedlabel,
                    gameTimelabel,
                    realTimelabel,
                    VSyncLabel,
                    UPSLabel,
                    FPSLabel,
                    avgFPSLabel,
                    usedMemoryLabel,
                    totalMemoryLabel,
                    maxMemoryLabel,
                    cameraZoomLabel,
                    cameraQualityLabel,
                    viewModeLabel
                );

                addLabels(
                    WorldInfoList, Align.right,
                    worldSeedLabel,
                    worldSizeLabel,
                    worldChunkBoundsLabel,
                    worldMousePosLabel,
                    worldMouseHoverPosLabel,
                    worldVisibleTileslabel,
                    worldVisibleChunksLabel,
                    worldLoadedChunkLabel,
                    worldChunkSizeLabel,
                    worldMouseTileLabel,
                    worldMouseTileNameLabel,
                    worldTileRotationLabel
                );
            }

            case CUSTOM -> {
                // Add custom labels separately.
            }
        }
    }


}
