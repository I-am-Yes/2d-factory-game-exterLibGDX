package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import core.system.context.RenderContext;

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

        context.systems.render();

        //Deprecated batch drawing method..
        context.getContext(RenderContext.class).spriteBatch.setProjectionMatrix(context.viewport.getCamera().combined);

        //BEGIN batch
        context.getContext(RenderContext.class).spriteBatch.begin();

        //usually don't use this here, do inside the system render itself.
        //if we have to use, just create new sprite batch instead.

        context.getContext(RenderContext.class).spriteBatch.end();
        //END batch


    }

    public void resize(int width, int height) {
        context.viewport.update(width, height, false);

        context.systems.resize(width, height);

    }

    public void pause() {}
    public void resume() {}

    public void dispose() {

        context.assets.dispose();
        context.world.dispose();

        context.systems.dispose();
    }


}
