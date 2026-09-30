package core.system.systems;

import com.badlogic.gdx.Input;
import core.app.cores.AppListener;
import core.app.vars.Cores;
import core.controller.camera.ScreenshotCapture;

import static core.app.vars.Vars.*;

public class EntrySystem implements AppListener {

    @Override
    public void update() {

        if (Cores.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ui.toggleSettings();
        }

        if (Cores.input.isKeyJustPressed(Input.Keys.F3)) {
            ui.toggleDebug();
        }
        if (Cores.input.isKeyJustPressed(Input.Keys.F11)) {
            //window.setWindowFullscreen();
            Cores.window.setBorderlessFullscreen();
        }

        if (!Cores.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Cores.input.isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.requestCapture();
        }

        if (Cores.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Cores.input.isKeyJustPressed(Input.Keys.F12)) {
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
