package core.system.systems;

import arcane.Cores;
import arcane.ApplicationListener;
import arcane.input.InputHandler;
import com.badlogic.gdx.Input;

import core.controller.camera.ScreenshotCapture;

import static core.app.vars.Vars.*;

public class EntrySystem extends InputHandler implements ApplicationListener {

    @Override
    public void update() {

        if (isKeyJustPressed(Input.Keys.ESCAPE)) {
            ui.toggleSettings();
        }

        if (isKeyJustPressed(Input.Keys.F3)) {
            ui.toggleDebug();
        }
        if (isKeyJustPressed(Input.Keys.F11)) {
            //window.setWindowFullscreen();
            Cores.window.setBorderlessFullscreen();
        }

        if (!isKeyPressed(Input.Keys.SHIFT_LEFT) && isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.requestCapture();
        }

        if (isKeyPressed(Input.Keys.SHIFT_LEFT) && isKeyJustPressed(Input.Keys.F12)) {
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

}
