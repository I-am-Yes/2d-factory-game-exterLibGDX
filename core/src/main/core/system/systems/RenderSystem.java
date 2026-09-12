package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.app.GameContext;
import core.system.ContextProvider;
import core.system.context.RenderContext;
import core.entities.plan.PlanManager;
import core.render.PlanRenderer;
import core.render.RenderLayer;
import core.system.GameSysCycle;
import core.world.World;
import data.map.asset.AssetType;

public class RenderSystem implements GameSysCycle, ContextProvider<RenderContext> {

    private final GameContext context;
    private final RenderContext renderContext;

    private final World world;
    private final Viewport viewport;
    private final SpriteBatch renderSpriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final AssetsHandler assets;

    private final PlanRenderer<AssetType> planRenderer;
    private final PlanManager planManager;

    private float delta;

    public RenderSystem(GameContext context) {
        this.context = context;
        this.world = context.world;
        this.viewport = context.viewport;
        this.assets = context.assets;

        this.renderSpriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.planManager = new PlanManager(world);
        this.planRenderer = new PlanRenderer<>(
            context.world,
            context.viewport,
            renderSpriteBatch,
            context.assets,
            planManager
        );


        this.renderContext = new RenderContext(
            world,
            viewport,
            assets,
            renderSpriteBatch,
            shapeRenderer,
            planRenderer
        );

    }

    @Override
    public void update(float delta) {
        this.delta = delta;
    }

    @Override
    public void render() {
        draw();
    }

    public void draw() {
        renderSpriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        //BEGIN batch
        renderSpriteBatch.begin();

        planRenderer.render();

        renderSpriteBatch.end();
        //END batch

    }

    public void dispose() {
        renderSpriteBatch.dispose();
        shapeRenderer.dispose();
        planRenderer.dispose();
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.OBJECT_LAYER;
    }

    @Override
    public RenderContext getContext() {
        return renderContext;
    }

}
