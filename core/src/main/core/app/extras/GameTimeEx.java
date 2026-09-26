package core.app.extras;

import com.badlogic.gdx.math.MathUtils;
import core.app.cores.GameTime;

public class GameTimeEx extends GameTime {

    private static float totalRealTime;
    private static float totalGameTime;

    private static int currentUPS;
    private static int upsCounter;
    private static float upsTimer;

    public static void accumulate() {
        totalRealTime += delta();
        if (!isPaused()) {
            totalGameTime += gameDelta();
        }
    }

    public static float totalRealTime() {
        return totalRealTime;
    }
    public static float totalGameTime() {
        return totalGameTime;
    }

    public static void accumulateTick(int tick, float realDelta) {
        upsCounter += tick;
        upsTimer += realDelta;

        if (upsTimer >= 1f) {
            currentUPS = upsCounter;
            upsCounter = 0;
            upsTimer -= 1f;
        }
    }

    public static int ups() {
        return currentUPS;
    }

    public static float remainingTick() {
        return MathUtils.clamp(tickAccumulated / UPDATE_INTERVAL, 0f, 1f);
    }

}
