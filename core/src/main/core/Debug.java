package core;

import com.badlogic.gdx.Gdx;

public class Debug {

    private float fpsLogTimer;
    public void printFPStoConsole(float delay) {
        if (delay <= 0) delay = 0.5f;
        fpsLogTimer += Gdx.graphics.getDeltaTime();
        if (fpsLogTimer >= delay) {
            System.out.printf("FPS: %d%n", Gdx.graphics.getFramesPerSecond());
            fpsLogTimer = 0f;
        }
    }

}
