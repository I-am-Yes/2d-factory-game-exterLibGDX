package core;

import com.badlogic.gdx.Gdx;

public class Window {

    private int windowWidth = 1280;
    private int windowHeight = 720;
    private int foregroundFPS = 180;

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

    public boolean setWindowFullscreen(boolean fullscreen) {
        if (fullscreen) {
            return Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            return Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
        }
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

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

}
