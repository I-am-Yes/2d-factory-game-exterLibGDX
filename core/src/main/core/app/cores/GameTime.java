package core.app.cores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import core.app.extras.GameTimeEx;

public class GameTime {
    private static float gameSpeed = 1.0f; //default x1 speed
    private static boolean paused;

    public static final int MAX_TICK_PER_FRAME = 5; // Maximum number of ticks to process per frame
    public static final float UPDATE_INTERVAL = 1f / 60f; // 60 updates per second
    public static final float maxDeltaUpdate = 0.25f;
    public static final float maxGameSpeed = Float.MAX_VALUE; //maximum game speed multiplier

    private static float realDelta;
    private static float gameDelta;

    ////game accumulated ticks
    protected static float tickAccumulated;

    //TODO: implement next tick/previous tick update

    public static int accelerate() {
        updateDelta();
        updateGameDelta(delta(), getGameSpeed());

        int ticks = 0;
        if (!isPaused()) {
            ticks = accelerateTick();
        }

        GameTimeEx.accumulate();
        return ticks;
    }

    public static int accelerateTick() {
        tickAccumulated += gameDelta();

        int tick = 0;
        while (tickAccumulated >= UPDATE_INTERVAL && tick < MAX_TICK_PER_FRAME) {
            tickAccumulated -= UPDATE_INTERVAL;
            tick++;
        }

        GameTimeEx.accumulateTick(tick, delta());
        return tick;
    }

    public static int getTargetUPS() {
        return Math.round(1f / UPDATE_INTERVAL);
    }

    public static void setGameSpeed(float gameSpeed) {
        GameTime.gameSpeed = MathUtils.clamp(gameSpeed, 0f, GameTime.maxGameSpeed);
    }

    public static float getGameSpeed() {
        return GameTime.gameSpeed;
    }

    public static void pause() {
        GameTime.paused = true;
    }

    public static void resume() {
        GameTime.paused = false;
    }

    public static boolean isPaused() {
        return GameTime.paused;
    }

    private static float GdxDelta() {
        return Gdx.graphics.getDeltaTime();
    }

    private static void updateDelta() {
        GameTime.realDelta = Math.min(GdxDelta(), GameTime.maxDeltaUpdate);
    }

    private static void updateGameDelta(float delta, float gameSpeed) {
        GameTime.gameDelta = delta * gameSpeed;
    }

    public static float delta() {
        return realDelta;
    }

    public static float gameDelta() {
        return gameDelta;
    }
}
