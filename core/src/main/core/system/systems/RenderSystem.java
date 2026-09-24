package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.assets.AssetsHandler;
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

        this.renderSpriteBatch = context.renderSpriteBatch;
        this.shapeRenderer = context.shapeRenderer;

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
            shapeRenderer,
            planRenderer
        );

    }

    @Override
    public void update(float delta) {
        this.delta = delta;

        planRenderer.update();
    }

    @Override
    public void render() {
    }

    @Override
    public void drawBatch(SpriteBatch batch) {
        //BEGIN batch

        planRenderer.drawBatch();

        //END batch
    }

    public void dispose() {

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

    public SpriteBatch getBatch() {
        return renderSpriteBatch;
    }

}
