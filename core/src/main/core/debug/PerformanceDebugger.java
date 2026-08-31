package core.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

public class PerformanceDebugger {
    public PerformanceDebugger() {
        initDefault();
    }

    private boolean initialized = false;

    private static class Metric {
        float interval;
        float timer;
        Runnable print;

    }

    private final ObjectMap<String, Metric> metrics = new ObjectMap<>();
    private ObjectSet<String> enabled = new  ObjectSet<>();

    public void register(String id, float intervalSec, Runnable print) {
        Metric metric = new Metric();
        metric.interval = intervalSec;
        metric.print = print;
        metrics.put(id, metric);
    }

    public void initDefault() {
        if (initialized) return;
        initialized = true;

        Runtime runtime = Runtime.getRuntime();

        register("fps", 0.5f, () ->
            System.out.printf("FPS: %d%n", Gdx.graphics.getFramesPerSecond())
        );

        register("ram", 0.5f, () -> {
            System.out.printf("total RAM: %d MB%n", runtime.totalMemory() / (1024 * 1024));
            System.out.printf("used RAM: %d MB%n", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
            System.out.printf("max RAM: %d MB%n", runtime.maxMemory() / (1024 * 1024));
        });


    }

    public void update() {

        //loop metric to regist and run
        float dt = Gdx.graphics.getDeltaTime();
        for (String id : enabled) {
            Metric metric = metrics.get(id);
            metric.timer += dt;
            if (metric.timer >= metric.interval) {
                metric.timer = 0f;
                metric.print.run();
            }
        }

    }

    public void setEnable(String id, boolean bool) {
        if (!metrics.containsKey(id)) return;
        if (bool) enabled.add(id); else enabled.remove(id);
    }

    public void setEnable(String id, boolean bool, float intervalSec) {
        if (!metrics.containsKey(id)) return;

        Metric metric = metrics.get(id);
        if (intervalSec > 0f) metric.interval = intervalSec;
        if (bool) enabled.add(id); else enabled.remove(id);
    }

    public boolean isEnabled(String id) {
        return enabled.contains(id);
    }

    public float getInterval(String id) {
        return metrics.get(id).interval;
    }

    public float getTimer(String id) {
        return metrics.get(id).timer;
    }

}
