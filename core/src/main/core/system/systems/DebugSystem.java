package core.system.systems;

import arcane.ApplicationListener;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.debug.Debug;
import data.debug.DebugConfig;

public class DebugSystem implements ApplicationListener {

    private final Debug debug;
    private final ShapeRenderer debugShapeRenderer;

    public DebugSystem() {

        this.debugShapeRenderer = new ShapeRenderer();

        this.debug = new Debug(
            DebugConfig.createDefaultConfig(), debugShapeRenderer
        );
    }

    @Override
    public void update() {
        debug.update();

        debug.render();
    }

    @Override
    public void dispose() {
        debugShapeRenderer.dispose();
    }

}
