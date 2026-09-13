package ui;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import core.Window;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;


public class GameUI {
    private final Window window;
    private final Stage stage;
    private final Skin skin;

    private final Label.LabelStyle textStyle1;

    private final Label VSyncLabel;
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

        VSyncLabel = new Label("V-Sync: off", textStyle1);
        FPSLabel = new Label("FPS: ", textStyle1);
        avgFPSLabel = new Label("Avg FPS: ", textStyle1);

        usedMemoryLabel = new Label("Used Mem: ", textStyle1);
        totalMemoryLabel = new Label("Total Mem: ", textStyle1);
        maxMemoryLabel = new Label("Max Mem: ", textStyle1);

        stage.addActor(VSyncLabel);
        stage.addActor(FPSLabel);
        stage.addActor(avgFPSLabel);

        stage.addActor(usedMemoryLabel);
        stage.addActor(totalMemoryLabel);
        stage.addActor(maxMemoryLabel);

        PerformanceList.setTouchable(Touchable.disabled);

        PerformanceList.top().left();
        PerformanceList.add(VSyncLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(FPSLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(avgFPSLabel).align(Align.left).padLeft(10).padTop(10).row();

        PerformanceList.add(usedMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(totalMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();
        PerformanceList.add(maxMemoryLabel).align(Align.left).padLeft(10).padTop(10).row();

    }

    public void update(float deltaTime) {
        updateFPSLabel();
        updateMemoriesLabel();
        updateVSyncLabel();
    }

    private void updateVSyncLabel() {
        VSyncLabel.setText("V-Sync: " + (window.isVSync() ? "on" : "off"));
    }

    private void updateFPSLabel() {
        FPSLabel.setText("FPS: " + window.getLatestFrameRateAfterDelay(100));
        avgFPSLabel.setText("Avg FPS: " + (int) window.getAverageFrameRate(1000));
    }

    private void updateMemoriesLabel() {
        usedMemoryLabel.setText("Used Mem: " + window.getUsedMemory());
        totalMemoryLabel.setText("Total Mem: " + window.getTotalMemory());
        maxMemoryLabel.setText("Max Mem: " + window.getMaxMemory());
    }

}
