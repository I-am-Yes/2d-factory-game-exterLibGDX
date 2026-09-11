package core.render;

import core.app.context.GameContext;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.world.World;

public class Renderer {

    private final World world;
    private final Viewport viewport;
    private final AssetsHandler assetsHandler;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final GameContext context;

    private float delta;

    //TODO: change to render system
    public Renderer(GameContext context) {
        this.context = context;
        this.world = context.world;
        this.viewport = context.viewport;
        this.spriteBatch = context.spriteBatch;
        this.shapeRenderer = context.shapeRenderer;
        this.assetsHandler = context.assets;

        this.delta = context.getDeltaTime();
    }

    public void update(float delta) {
        this.delta = delta;
    }

    public void render() {

    }

    public void draw() {

    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

}
