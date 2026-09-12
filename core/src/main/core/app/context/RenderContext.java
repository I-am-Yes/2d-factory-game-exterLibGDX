package core.app.context;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.render.PlanRenderer;
import core.world.World;


public class RenderContext {

    public World world;
    public Viewport viewport;
    public AssetsHandler assets;
    public SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;
    public PlanRenderer<?> planRenderer;

    public RenderContext(World world, Viewport viewport, AssetsHandler assets, SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, PlanRenderer<?> planRenderer) {
        this.world = world;
        this.viewport = viewport;
        this.assets = assets;
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.planRenderer = planRenderer;
    }


}
