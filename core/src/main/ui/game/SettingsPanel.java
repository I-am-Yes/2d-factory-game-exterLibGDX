package ui.game;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import core.app.GameLoop;
import core.app.cores.GameTime;
import ui.UiHelper;
import ui.Style;

public final class SettingsPanel extends Window {

    private boolean pauseOnOpen;
    private boolean pauseBySettingsPanel;

    public SettingsPanel(Skin skin, UiHelper uiHelper) {
        super("SETTINGS", skin);

        Label.LabelStyle textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        uiHelper.configsPanel(this, skin, 300f, 500f);

        uiHelper.addCloseButton(this, skin);

        Table settingsContent = new Table();
        settingsContent.top().left();
        settingsContent.pad(10f);

        ScrollPane scrollPane = uiHelper.createScrollPane(skin, settingsContent);
        scrollPane = uiHelper.betterScrollBar(skin, scrollPane);

        add(scrollPane).expand().fill().row();

        CheckBox pauseCheck = uiHelper.createCheckbox(
            skin, "Pause Game on open Settings",
            () -> setPauseOnOpen(true),
            () -> setPauseOnOpen(false)
        );

        settingsContent.add(pauseCheck).row();

        settingsContent.add(uiHelper.createTextLabel("Settings content goes here", textStyle1)).left().pad(10f).row();

        for (int i = 1; i <= 30; i++) {
            settingsContent.add(
                uiHelper.createTextLabel("Setting " + i, textStyle1)
            ).left().pad(10f).row();
        }

        getTitleTable().toFront();
    }

    public void open() {
        setVisible(true);

        if (pauseOnOpen && !GameTime.isPaused()) {
            GameLoop.pause();
            pauseBySettingsPanel = true;
        }
    }
    public void close() {
        setVisible(false);

        if (pauseBySettingsPanel) {
            GameLoop.resume();
            pauseBySettingsPanel = false;
        }
    }

    public void togglePanel() {
        if (isPanelOpen()) {
            close();
        } else {
            open();
        }
    }

    public void setPauseOnOpen(boolean pauseOnOpen) {
        this.pauseOnOpen = pauseOnOpen;
    }

    public boolean isPanelOpen() {
        return isVisible();
    }

}
