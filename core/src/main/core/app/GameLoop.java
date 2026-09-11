package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.context.GameContext;

public class GameLoop {

    private final GameContext context;
    private static final float UPDATE_INTERVAL = 1f / 60f; // 60 updates per second

    private float deltaTime;

    public GameLoop(GameContext context) {
        this.context = context;
    }

    public void update() {
        deltaTime = context.getDeltaTime();

        context.systems.update(deltaTime);

        //TODO: change this
        context.controller.setFullScreenByInput();


        context.renderer.update(deltaTime);

        context.interfaceHandler.update(deltaTime);
        context.debug.update(deltaTime);
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
    private void logic() {}

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        context.viewport.apply();

        context.world.render((OrthographicCamera) context.viewport.getCamera());
        context.spriteBatch.setProjectionMatrix(context.viewport.getCamera().combined);

        context.debug.render();

        context.systems.render();

        //BEGIN batch
        context.spriteBatch.begin();

        context.planRenderer.render();

        context.spriteBatch.end();
        //END batch


        context.interfaceHandler.draw();
    }

    public void resize(int width, int height) {
        context.viewport.update(width, height, false);

        context.systems.resize(width, height);

        context.interfaceHandler.resize();
    }

    public void pause() {}
    public void resume() {}

    public void dispose() {
        context.spriteBatch.dispose();

        context.assets.dispose();
        context.world.dispose();

        context.shapeRenderer.dispose();
        context.planRenderer.dispose();

        context.interfaceHandler.dispose();
        context.systems.dispose();
    }


}
