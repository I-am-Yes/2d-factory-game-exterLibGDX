package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

public class Window {

    private int windowWidth = 1280;
    private int windowHeight = 720;
    private int foregroundFPS = 180;

    private boolean vSync;
    private boolean isBorderlessFullscreen = false;

    public Window() {}

    public void createGameWindow(boolean VSync, int foregroundFPS, int windowWidth, int windowHeight) {
        Gdx.graphics.setVSync(true);
        setForegroundFPS(foregroundFPS);
        setWindowedMode(windowWidth, windowHeight);
    }

    public void createGameWindow() {
        Gdx.graphics.setVSync(true);
        setForegroundFPS(foregroundFPS);
        setWindowedMode(windowWidth, windowHeight);
    }

    public void setWindowedMode() {
        setWindowedMode(windowWidth, windowHeight);
    }

    public void setWindowedMode(int width, int height) {
        windowWidth = width;
        windowHeight = height;
        Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
    }

    public void setWindowFullscreen() {
        setWindowFullscreen(!isFullscreen());
    }

    public void setWindowFullscreen(boolean fullscreen) {
        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
        }
    }

    public void setBorderlessFullscreen() {
        setBorderlessFullscreen(!isBorderlessFullscreen());
    }

    public void setBorderlessFullscreen(boolean borderlessFullscreen) {
        if (borderlessFullscreen) {
            Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setWindowedMode(mode.width, mode.height);
            isBorderlessFullscreen = true;
            return;
        }
        Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
        isBorderlessFullscreen = false;
    }

    public void exitFullscreen() {
        setWindowFullscreen(false);
    }

    public void setForegroundFPS(int fps) {
        Gdx.graphics.setForegroundFPS(fps);
    }

    public int getForegroundFPS() {
        return foregroundFPS;
    }

    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    public boolean isBorderlessFullscreen() {
        return isBorderlessFullscreen;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
        Gdx.graphics.setVSync(vSync);
    }

    public boolean isVSync() {
        return vSync;
    }

    public float getLatestFrameRate() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Prevent division by zero if a frame finishes instantly
        return (deltaTime > 0) ? (1.0f / deltaTime) : 0;
    }

    private float delayTimer;
    private float currentFPS;
    public float getLatestFrameRateAfterDelay(float delay) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (delay <= 0f) return deltaTime > 0f ? 1f / deltaTime : 0f;

        delayTimer += deltaTime;
        if (delayTimer >= delay / 1000f) {
            currentFPS = deltaTime > 0f ? 1f / deltaTime : 0f;
            delayTimer %= delay / 1000f;
        }
        return currentFPS;
    }

    public int getLatestFrameRateAfterDelay(int delay) {
        return (int) getLatestFrameRateAfterDelay((float) delay);
    }

    private float averageTimer;
    private float totalDeltaTime;
    private int frameCount;
    private float averageFPS;
    public float getAverageFrameRate(float avgTime) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (avgTime <= 0f) {
            return deltaTime > 0f ? 1f / deltaTime : 0f;
        }

        float avgTimeSeconds = avgTime / 1000f;

        averageTimer += deltaTime;
        totalDeltaTime += deltaTime;
        frameCount++;

        if (averageTimer >= avgTimeSeconds) {
            averageFPS = totalDeltaTime > 0f
                ? frameCount / totalDeltaTime
                : 0f;

            averageTimer %= avgTimeSeconds;
            totalDeltaTime = 0f;
            frameCount = 0;
        }

        return averageFPS;
    }

    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

    public int getTotalMemory(float delay) {
        if (delayHelper(delay)) {
            return getTotalMemory();
        }
        return 0;
    }
    public int getUsedMemory(float delay) {
        if (delayHelper(delay)) {
            return getUsedMemory();
        }
        return 0;
    }
    public String getUsedMemoryWithTotal(float delay) {
        if (delayHelper(delay)) {
            return getUsedMemoryWithTotal();
        }
        return "0/0";
    }
    public int getMaxMemory(float delay) {
        if (delayHelper(delay)) {
            return getMaxMemory();
        }
        return 0;
    }

    public int getTotalMemory() {
        return Math.toIntExact(Runtime.getRuntime().totalMemory() / (1024 * 1024));
    }

    public int getUsedMemory() {
        return Math.toIntExact((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
    }
    public String getUsedMemoryWithTotal() {
        return getUsedMemory() + "/" + getTotalMemory();
    }
    public int getMaxMemory() {
        return Math.toIntExact(Runtime.getRuntime().maxMemory() / (1024 * 1024));
    }

    private float delayHelperTimer;
    private boolean delayHelper(float delay) {
        if (delay <= 0f) {
            return true;
        }
        delayHelperTimer += Gdx.graphics.getDeltaTime();
        float delaySec = delay / 1000f;
        if (delayHelperTimer >= delaySec) {
            delayHelperTimer %= delaySec;
            return true;
        }
        return false;
    }

}
