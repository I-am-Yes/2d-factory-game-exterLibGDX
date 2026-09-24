package core.system.context;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.assets.AssetsHandler;
import core.render.PlanRenderer;
import core.world.World;


public class RenderContext {

    public World world;
    public Viewport viewport;
    public AssetsHandler assets;
    public ShapeRenderer shapeRenderer;
    public PlanRenderer<?> planRenderer;

    public RenderContext(World world, Viewport viewport, AssetsHandler assets, ShapeRenderer shapeRenderer, PlanRenderer<?> planRenderer) {
        this.world = world;
        this.viewport = viewport;
        this.assets = assets;
        this.shapeRenderer = shapeRenderer;
        this.planRenderer = planRenderer;
    }


}
