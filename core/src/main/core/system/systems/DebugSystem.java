package core.system.systems;

import arcane.AppListener;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.debug.Debug;
import core.render.RenderLayer;
import data.debug.DebugConfig;

public class DebugSystem implements AppListener {

    private Debug debug;
    private ShapeRenderer debugShapeRenderer;

    public DebugSystem() {

        this.debugShapeRenderer = new ShapeRenderer();

        this.debug = new Debug(
            DebugConfig.createDefaultConfig(), debugShapeRenderer
        );
//
//        this.debugContext = new DebugContext(
//            debug,
//            debug.getCameraDebugger(),
//            debug.getEventsDebugger(),
//            debug.getMapGenDebugger(),
//            debug.getPerformanceDebugger(),
//            debug.getRenderDebugger()
//        );
    }

    @Override
    public void init() {
        AppListener.super.init();
    }

    @Override
    public void update() {

        debug.update();
    }

    @Override
    public void render() {

        debug.render();

    }

    @Override
    public void dispose() {
        debugShapeRenderer.dispose();
    }

    public RenderLayer renderLayer() {
        return RenderLayer.DEBUG_LAYER;
    }

}
