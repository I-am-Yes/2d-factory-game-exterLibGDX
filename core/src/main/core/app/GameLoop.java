package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.cores.GameTime;
import core.system.context.CameraContext;
import core.controller.camera.ScreenshotCapture;

import static core.app.Vars.*;

public class GameLoop {
    public void update() {
        int ticks = GameTime.accelerate();

        context.systems.update();

        for (int i = 0; i < ticks; i++) {
            context.systems.tickUpdate();
        }
    }

    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {

        context.input.endFrame();

    }
    private void logic() {
        context.world.update(getCamera());

    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        context.viewport.apply();



        context.world.drawCached(getCamera(),
            () -> context
            .getContext(CameraContext.class)
            .cameraController
        );

        //BEGIN batch
        context.renderSpriteBatch.setProjectionMatrix(getCamera().combined);
        context.renderSpriteBatch.begin();

        //general spriteBatch for all world contents
        context.systems.drawBatch(context.renderSpriteBatch);

        context.renderSpriteBatch.end();
        //END batch


        //BEGIN shapeRender
        context.shapeRenderer.setProjectionMatrix(getCamera().combined);
        context.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Note: add another ShapeType if needed.

        context.systems.drawShapeRenderer(context.shapeRenderer);

        context.shapeRenderer.end();
        //END shapeRender



        //some systems such as scene2D, so this should be at very end.
        context.systems.render();

        ScreenshotCapture.captureIfRequested();
    }

    public void resize(int width, int height) {
        context.viewport.update(width, height, false);

        context.systems.resize(width, height);

    }

    public static void pause() {
        GameTime.pause();
    }

    public static void resume() {
        GameTime.resume();
    }

    public void dispose() {

        context.systems.dispose();
        context.world.dispose();

        context.assets.dispose();

        context.renderSpriteBatch.dispose();
        context.shapeRenderer.dispose();

    }

    private OrthographicCamera getCamera() {
        return (OrthographicCamera) context.viewport.getCamera();
    }

}
