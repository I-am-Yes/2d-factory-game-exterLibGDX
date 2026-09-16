package ui.game;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import core.Window;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import core.app.GameLoop;
import ui.Style;


public class GameUI {
    private final Window window;
    private final Stage stage;
    private final Skin skin;

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

    private final int memoryUpdateDelay = 1000;

    public GameUI(Window window, Stage stage, Skin skin) {
        this.window = window;
        this.stage = stage;
        this.skin = skin;

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        Table PerformanceList = new Table();
        PerformanceList.setFillParent(true);
        stage.addActor(PerformanceList);

        gameSpeedlabel = new Label("Game Speed: ", textStyle1);
        gameTimelabel = new Label("Game Time: ", textStyle1);
        realTimelabel = new Label("Real Time: ", textStyle1);

        VSyncLabel = new Label("V-Sync: off", textStyle1);
        UPSLabel = new Label("UPS: ", textStyle1);
        FPSLabel = new Label("FPS: ", textStyle1);
        avgFPSLabel = new Label("Avg FPS: ", textStyle1);

        usedMemoryLabel = new Label("Used Mem: ", textStyle1);
        totalMemoryLabel = new Label("Total Mem: ", textStyle1);
        maxMemoryLabel = new Label("Max Mem: ", textStyle1);


        stage.addActor(gameSpeedlabel);
        stage.addActor(gameTimelabel);
        stage.addActor(realTimelabel);

        stage.addActor(VSyncLabel);
        stage.addActor(UPSLabel);
        stage.addActor(FPSLabel);
        stage.addActor(avgFPSLabel);

        stage.addActor(usedMemoryLabel);
        stage.addActor(totalMemoryLabel);
        stage.addActor(maxMemoryLabel);

        PerformanceList.setTouchable(Touchable.disabled);

        PerformanceList.top().left();

        PerformanceList.add(gameSpeedlabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(gameTimelabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(realTimelabel).align(Align.left).padLeft(10).padTop(10).row();

        PerformanceList.add(VSyncLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(UPSLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(FPSLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(avgFPSLabel).align(Align.left).padLeft(10).padTop(10).row();

        PerformanceList.add(usedMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(totalMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(maxMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();

    }

    public void update(float deltaTime) {
        updateTimerLabel();
        updateUPSLabel();
        updateFPSLabel();
        updateMemoriesLabel();
        updateVSyncLabel();
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
        usedMemoryLabel.setText("Used Mem: " + window.getUsedMemory());
        totalMemoryLabel.setText("Total Mem: " + window.getTotalMemory());
        maxMemoryLabel.setText("Max Mem: " + window.getMaxMemory());
    }

    private String formatTime(float time) {
        int seconds = (int) time;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

}
