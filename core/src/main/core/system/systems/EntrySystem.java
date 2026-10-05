package core.system.systems;

import arcane.*;
import arcane.input.*;

import core.event.*;

import static core.app.Vars.*;

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
            AppEvent.ScreenShoot.captureScreen();
        }

        if (isKeyPressed(Input.Keys.SHIFT_LEFT) && isKeyJustPressed(Input.Keys.F12)) {
            AppEvent.ScreenShoot.captureWorld();
        }

    }

}
