package core.debug;

import com.badlogic.gdx.Gdx;

public class PerformanceDebugger {

    private boolean printFPStoConsole = false;
    private float fpsLogInterval = 0.5f;
    private float fpsLogTimer = 0f;

    public PerformanceDebugger() {}

    public void update() {


        if (printFPStoConsole) {
            printFPStoConsole(fpsLogInterval);
        }

    }

    private void printFPStoConsole(float delay) {
        if (!printFPStoConsole) return;
        if (delay <= 0) delay = 0.5f;

        fpsLogTimer += Gdx.graphics.getDeltaTime();
        if (fpsLogTimer >= delay) {
            System.out.printf("FPS: %d%n", Gdx.graphics.getFramesPerSecond());
            fpsLogTimer = 0f;
        }
    }



    public float getFpsLogInterval() {
        return fpsLogInterval;
    }

    public void setPrintFPStoConsole(boolean bool, float fpsLogInterval) {
        this.printFPStoConsole = bool;
        this.fpsLogInterval = fpsLogInterval;
        if (!bool) fpsLogInterval = 0f;
    }


}
