package core.utils;

import com.badlogic.gdx.utils.Timer;

public final class TimeUtils {
    private TimeUtils() {}

    public static void runAfter(float delay, Runnable runnable) {
        Timer.schedule(new Timer.Task(){
            @Override
            public void run(){
                runnable.run();
            }
        }, delay);
    }


    public static long getNanos() {
        return System.nanoTime();
    }

    public static long getMillis() {
        return System.currentTimeMillis();
    }

    public static long nanosToMillis(long nanos) {
        return nanos / 1000000L;
    }

    public static long millisToNanos(long millis) {
        return millis * 1000000L;
    }

    public static long timeSinceNanos(long prevTime) {
        return getNanos() - prevTime;
    }

    public static long timeSinceMillis(long prevTime) {
        return getMillis() - prevTime;
    }
}
