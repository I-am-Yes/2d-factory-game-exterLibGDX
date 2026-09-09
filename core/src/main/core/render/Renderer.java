package core.render;

import core.app.GameContext;
import data.map.asset.FloorType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Player;
import core.world.World;
import core.controller.PlayerAction;
import core.entities.BuildPlan;

public class Renderer {

    private final World world;
    private final Player player;
    private final Viewport viewport;
    private final AssetsHandler assetsHandler;
    private final SpriteBatch spriteBatch;
    private final PlayerAction playerAction;
    private final ShapeRenderer shapeRenderer;
    private final GhostLineRenderer ghostBlockRenderer;

    private BuildPlan buildPlan;
    private FloorType floorType;

    private float delta;

    public Renderer(GameContext context) {
        this.world = context.world;
        this.delta = context.getDeltaTime();
        this.viewport = context.viewport;
        this.spriteBatch = context.spriteBatch;
        this.player = context.player;
        this.playerAction = context.playerAction;
        this.shapeRenderer = context.shapeRenderer;
        this.assetsHandler = context.assets;
        this.ghostBlockRenderer = new GhostLineRenderer(context);


    }

    public void update() {
        ghostBlockRenderer.update();
        if (buildPlan != null) buildPlan.update(delta);
    }

    public void render() {
        ghostBlockRenderer.render();
        buildPlan.renderBuildQueue(world, player, spriteBatch, assetsHandler);
    }

    public void draw() {

    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        ghostBlockRenderer.dispose();
    }

    private void renderGhostLineOverlay() {

    }

}
