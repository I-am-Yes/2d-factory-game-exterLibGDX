package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import core.system.context.RenderContext;

public class GameLoop {

    private final GameContext context;
    private static final float UPDATE_INTERVAL = 1f / 60f; // 60 updates per second

    private static float gameSpeed = 1.0f; //default x1 speed

    private static float realDelta;
    private static float gameDelta;

    private static float totalRealTime;
    private static float totalGameTime;

    private static final float maxDeltaUpdate = 0.25f;
    private static final float maxGameSpeed = Float.MAX_VALUE; //maximum game speed multiplier

    public GameLoop(GameContext context) {
        this.context = context;
    }

    public void update() {
        realDelta = Math.min(context.getDeltaTime(), maxDeltaUpdate);
        gameDelta = realDelta * gameSpeed;

        context.systems.update(realDelta, gameDelta);

        totalRealTime += realDelta;
        totalGameTime += gameDelta;
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

    public static void setGameSpeed(float gameSpeed) {
        GameLoop.gameSpeed = MathUtils.clamp(gameSpeed, 0f, maxGameSpeed);
    }

    public static float getGameSpeed() {
        return gameSpeed;
    }

    public static float getGameDelta() {
        return gameDelta;
    }

    public static float getRealDelta() {
        return realDelta;
    }

    public static float getTotalRealTime() {
        return totalRealTime;
    }

    public static float getTotalGameTime() {
        return totalGameTime;
    }

}
