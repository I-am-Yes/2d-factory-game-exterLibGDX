package core.system.systems;

import com.badlogic.gdx.Input;
import core.app.cores.AppListener;
import core.app.cores.GameSysCycle;
import core.controller.camera.ScreenshotCapture;

import static core.app.Vars.*;

public class EntrySystem implements AppListener, GameSysCycle {

    @Override
    public void update() {

        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggleSettingPanel();
        }

        if (input.isKeyJustPressed(Input.Keys.F3)) {
            toggleDebugPanel();
        }
        if (input.isKeyJustPressed(Input.Keys.F11)) {
            //window.setWindowFullscreen();
            window.setBorderlessFullscreen();
        }

        if (!input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.requestCapture();
        }

        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.captureMapArea(
                world,
                (int) -world.getWorldWidth(),
                (int) -world.getWorldHeight(),
                (int) world.getWorldWidth(),
                (int) world.getWorldHeight(),
                4
            );
        }

    }

    @Override
    public void render() {
        AppListener.super.render();
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

    private void toggleSettingPanel() {
        settingsPanel.togglePanel();
    }

    private void toggleDebugPanel() {
        interfaceContext.gameUI.setDebugInfoVisible(!interfaceContext.gameUI.isDebugInfoVisible());
    }

    @Override
    public boolean updateWhenPaused() {
        return true;
    }

    @Override
    public void dispose() {
        AppListener.super.dispose();
    }

}
