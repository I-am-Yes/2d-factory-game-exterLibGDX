package core.system.systems;

import arcane.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.debug.*;
import core.assets.debug.*;

public class DebugSystem implements ApplicationListener {

    private final Debug debug;
    private final ShapeRenderer debugShape;

    public DebugSystem() {

        this.debugShape = new ShapeRenderer();

        this.debug = new Debug(
            DebugConfig.createDefaultConfig(), debugShape
        );
    }

    @Override
    public void update() {
        debug.update();

        debug.render();
    }

    @Override
    public void dispose() {
        debugShape.dispose();
    }

}
