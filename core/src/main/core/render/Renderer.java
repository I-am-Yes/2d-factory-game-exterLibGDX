package core.render;

import Data.map.FloorType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.BlockAssets;
import core.InputHandler;
import core.Player;
import core.World;
import core.controller.PlayerAction;
import core.entities.BuildPlan;

public class Renderer {

    private final World world;
    private final Player player;
    private final Viewport viewport;
    private final BlockAssets blockAssets;
    private final SpriteBatch spriteBatch;
    private final PlayerAction playerAction;
    private final ShapeRenderer shapeRenderer;
    private final GhostLineRenderer ghostBlockRenderer;

    private BuildPlan buildPlan;
    private FloorType floorType;

    private float delta;

    public Renderer(World world, float delta, Viewport viewport, SpriteBatch spriteBatch, Player player, InputHandler input, PlayerAction playerAction , ShapeRenderer shapeRenderer, BlockAssets blockAssets) {
        this.world = world;
        this.delta = delta;
        this.viewport = viewport;
        this.spriteBatch = spriteBatch;
        this.player = player;
        this.playerAction = playerAction;
        this.shapeRenderer = shapeRenderer;
        this.blockAssets = blockAssets;
        this.ghostBlockRenderer = new GhostLineRenderer(world, viewport, player, input, playerAction, spriteBatch, shapeRenderer, null, blockAssets);


    }

    public void update() {
        ghostBlockRenderer.update();
        if (buildPlan != null) buildPlan.update(delta);
    }

    public void render() {
        ghostBlockRenderer.render();
        buildPlan.renderBuildQueue(world, player, spriteBatch, blockAssets);
    }

    public void draw() {

    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        ghostBlockRenderer.dispose();
    }

}
