package core.utils;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

public final class TimeUtils {
    private static final ObjectMap<String, Measurement> measurements = new ObjectMap<>();

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

    public static double nanosToMillisDouble(long nanos) {
        return nanos / 1_000_000.0;
    }

    private static final class Measurement {
        long totalNanos;
        int samples;
        float elapsedSeconds;
    }

    public static void measureAndPrint(float delta, Runnable... actions) {
        measureAndPrint(null, delta, 1f, actions);
    }

    public static void measureAndPrint(float delta, float printInterval, Runnable... actions) {
        measureAndPrint(null, delta, printInterval, actions);
    }

    public static void measureAndPrint(String label, float delta, float printInterval, Runnable... actions) {
        if (printInterval <= 0f) {
            throw new IllegalArgumentException(
                "Print interval must be positive."
            );
        }
        if (actions == null || actions.length == 0) {
            throw new IllegalArgumentException(
                "At least one action is required."
            );
        }
        for (Runnable action : actions) {
            if (action == null) {
                throw new IllegalArgumentException(
                    "Actions cannot contain null."
                );
            }
        }
        if (label == null || label.isEmpty()) {
            label = "Update Time";
        }

        Measurement measurement = measurements.get(label);

        if (measurement == null) {
            measurement = new Measurement();
            measurements.put(label, measurement);
        }
        long start = getNanos();

        try {
            for (Runnable action : actions) action.run();

        } finally {
            measurement.totalNanos += getNanos() - start;

            measurement.samples++;
            measurement.elapsedSeconds += delta;

            if (measurement.elapsedSeconds >= printInterval) {
                double averageMilliseconds = measurement.totalNanos / 1_000_000.0 / measurement.samples;

                System.out.printf(
                    "%s average: %.4f ms (%d samples)%n",
                    label,
                    averageMilliseconds,
                    measurement.samples
                );

                measurement.totalNanos = 0L;
                measurement.samples = 0;
                measurement.elapsedSeconds -= printInterval;
            }
        }
    }
}
