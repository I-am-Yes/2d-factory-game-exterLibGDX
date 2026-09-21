package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import core.system.context.CameraContext;
import core.controller.camera.ScreenshotCapture;

public class GameLoop {

    private final GameContext context;

    private float tickAccumulator;
    private static final int MAX_TICK_PER_FRAME = 5; // Maximum number of ticks to process per frame
    private static final float UPDATE_INTERVAL = 1f / 60f; // 60 updates per second

    private static float gameSpeed = 1.0f; //default x1 speed

    private static float realDelta;
    private static float gameDelta;

    private static final float maxDeltaUpdate = 0.25f;
    private static final float maxGameSpeed = Float.MAX_VALUE; //maximum game speed multiplier

    private static int currentUPS;
    private static int upsCounter;
    private static float upsTimer;

    private static float totalRealTime;
    private static float totalGameTime;

    private static boolean paused;

    public GameLoop(GameContext context) {
        this.context = context;
    }

    public void update() {
        realDelta = Math.min(context.getDeltaTime(), maxDeltaUpdate);
        gameDelta = realDelta * gameSpeed;

        context.systems.update(realDelta, gameDelta, paused);

        if (!paused) {
            totalRealTime += realDelta;
            totalGameTime += gameDelta;

            tickAccumulator += gameDelta;

            int tick = 0;
            while (tickAccumulator >= UPDATE_INTERVAL && tick < MAX_TICK_PER_FRAME) {
                context.systems.tickUpdate(UPDATE_INTERVAL);
                tickAccumulator -= UPDATE_INTERVAL;
                tick++;

                upsCounter++;
            }

            upsTimer += realDelta;
            if (upsTimer >= 1f) {
                currentUPS = upsCounter;
                upsCounter = 0;
                upsTimer -= 1f;
            }
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
        paused = true;
    }

    public static void resume() {
        paused = false;
    }

    public void dispose() {

        context.systems.dispose();
        context.world.dispose();

        context.assets.dispose();

        context.renderSpriteBatch.dispose();
        context.shapeRenderer.dispose();

    }

    public float getTickRemaining() {
        return MathUtils.clamp(tickAccumulator / UPDATE_INTERVAL, 0f, 1f);
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

    public static int getCurrentUPS() {
        return currentUPS;
    }

    public static float getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    public static int getTargetUPS() {
        return Math.round(1f / UPDATE_INTERVAL);
    }

    public static boolean isGamePaused() {
        return paused;
    }

    public OrthographicCamera getCamera() {
        return (OrthographicCamera) context.viewport.getCamera();
    }

}
