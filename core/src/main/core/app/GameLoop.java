package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.cores.Time;
import core.app.vars.Cores;
import core.controller.camera.ScreenshotCapture;

import static core.app.vars.Vars.*;

public class GameLoop {
    public void update() {
    }

    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {


    }
    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        Cores.viewport.apply();

        world.drawCached(cameraControl);

        //BEGIN batch
        Cores.batch.setProjectionMatrix(getCamera().combined);
        Cores.batch.begin();

        //general spriteBatch for all world contents
//        systems.drawBatch(batch);

        Cores.batch.end();
        //END batch


        //BEGIN shapeRender
        Cores.shape.setProjectionMatrix(getCamera().combined);
        Cores.shape.begin(ShapeRenderer.ShapeType.Filled);
        //Note: add another ShapeType if needed.

        //        systems.drawShapeRenderer(shape);

        Cores.shape.end();
        //END shapeRender



        //some systems such as scene2D, so this should be at very end.
//        systems.render();

        ScreenshotCapture.captureIfRequested();
    }

    public void resize(int width, int height) {
        Cores.viewport.update(width, height, false);

//        systems.resize(width, height);

    }

    public static void pause() {
        Time.pause();
    }

    public static void resume() {
        Time.resume();
    }

    public void dispose() {

//        systems.dispose();
        world.dispose();

        Cores.assets.dispose();

        Cores.batch.dispose();
        Cores.shape.dispose();

    }

    private OrthographicCamera getCamera() {
        return (OrthographicCamera) Cores.viewport.getCamera();
    }

}
