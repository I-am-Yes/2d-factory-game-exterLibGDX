package core.render;

import Data.map.asset.FloorType;
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

    public Renderer(World world, float delta, Viewport viewport, SpriteBatch spriteBatch, Player player, InputHandler input, PlayerAction playerAction , ShapeRenderer shapeRenderer, AssetsHandler assetsHandler) {
        this.world = world;
        this.delta = delta;
        this.viewport = viewport;
        this.spriteBatch = spriteBatch;
        this.player = player;
        this.playerAction = playerAction;
        this.shapeRenderer = shapeRenderer;
        this.assetsHandler = assetsHandler;
        this.ghostBlockRenderer = new GhostLineRenderer(world, viewport, player, input, playerAction, spriteBatch, shapeRenderer, null, assetsHandler);


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
