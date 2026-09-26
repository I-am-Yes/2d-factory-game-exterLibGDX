package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.cores.GameTime;
import core.controller.camera.ScreenshotCapture;

import static core.app.Vars.*;

public class GameLoop {
    public void update() {
        int ticks = GameTime.accelerate();

        systems.update();

        for (int i = 0; i < ticks; i++) {
            systems.tickUpdate();
        }
    }

    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {

        input.endFrame();

    }
    private void logic() {
        world.update(getCamera());

    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();

        world.drawCached(getCamera(), cameraController);

        //BEGIN batch
        batch.setProjectionMatrix(getCamera().combined);
        batch.begin();

        //general spriteBatch for all world contents
        systems.drawBatch(batch);

        batch.end();
        //END batch


        //BEGIN shapeRender
        shape.setProjectionMatrix(getCamera().combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        //Note: add another ShapeType if needed.

        systems.drawShapeRenderer(shape);

        shape.end();
        //END shapeRender



        //some systems such as scene2D, so this should be at very end.
        systems.render();

        ScreenshotCapture.captureIfRequested();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);

        systems.resize(width, height);

    }

    public static void pause() {
        GameTime.pause();
    }

    public static void resume() {
        GameTime.resume();
    }

    public void dispose() {

        systems.dispose();
        world.dispose();

        assets.dispose();

        batch.dispose();
        shape.dispose();

    }

    private OrthographicCamera getCamera() {
        return (OrthographicCamera) viewport.getCamera();
    }

}
