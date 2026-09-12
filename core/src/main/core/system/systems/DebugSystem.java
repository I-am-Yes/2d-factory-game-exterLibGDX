package core.system.systems;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.app.GameContext;
import core.app.context.ContextProvider;
import core.app.context.DebugContext;
import core.app.context.PlayerContext;
import core.debug.Debug;
import core.player.Player;
import core.render.RenderLayer;
import core.system.GameSystem;
import core.world.World;
import data.debug.DebugConfig;

public class DebugSystem implements GameSystem, ContextProvider<DebugContext> {

    private GameContext context;
    private DebugContext debugContext;

    private World world;
    private Viewport viewport;
    private Player player;
    private InputHandler input;

    private Debug debug;
    private ShapeRenderer debugShapeRenderer;

    private float delta;

    public DebugSystem(GameContext context) {
        this.context = context;
        this.world = context.world;
        this.viewport = context.viewport;
        this.player = context.getContext(PlayerContext.class).player;
        this.input = context.input;
        this.debugShapeRenderer = new ShapeRenderer();

        this.debug = new Debug(
            DebugConfig.createDefaultConfig(),
            world,
            viewport,
            input,
            player,
            debugShapeRenderer
        );

        this.debugContext = new DebugContext(
            debug,
            debug.getCameraDebugger(),
            debug.getEventsDebugger(),
            debug.getMapGenDebugger(),
            debug.getPerformanceDebugger(),
            debug.getRenderDebugger()
        );
    }

    public void update(float delta) {
        this.delta = delta;

        debug.update(delta);
    }

    public void render() {

        debug.render();

    }

    public void dispos() {

    }

    public Debug getDebug() {
        return debug;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.DEBUG_LAYER;
    }

    @Override
    public DebugContext getContext() {
        return debugContext;
    }
}
