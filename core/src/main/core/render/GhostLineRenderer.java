package core.render;

import Data.map.asset.FloorType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Player;
import core.world.World;
import core.controller.PlayerAction;
import core.entities.BuildGhostLine;
import core.entities.BuildPlan;

public class GhostLineRenderer {

    private final World world;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final FloorType floorType;
    private final AssetsHandler assetsHandler;
    private final PlayerAction playerAction;

    private BuildPlan buildPlan;
    private BuildGhostLine buildGhostLine;
    private BuildPlan buildLinePlan;
    private int x, y;

    private float delta;

    public GhostLineRenderer(World world, Viewport viewport, Player player, InputHandler input, PlayerAction playerAction, SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, FloorType floorType, AssetsHandler assetsHandler) {
        this.world = world;
        this.viewport = viewport;
        this.playerAction = playerAction;
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.floorType = floorType;
        this.assetsHandler = assetsHandler;

        this.buildGhostLine = playerAction.getBuildGhostLine();
    }

    public void update() {

        this.buildPlan = new BuildPlan(0, 0, null);

        Array<Vector2> tiledLine = buildGhostLine.getCurrentTiledLine();

        if (addLinePlan(tiledLine)) {
            System.out.println("ghost line updated");
        }


    }

    public boolean addLinePlan(Array<Vector2> array) {
        if (array == null) return false;
        for (int i = 0; i < array.size; i++) {
            this.buildLinePlan = new BuildPlan ((int) array.get(i).x, (int) array.get(i).y, playerAction.getSelectedType());
            this.buildPlan.addBuildPlan(this.buildLinePlan);
            if (!clearLinePlan()) return false;
        }
        return clearLinePlan();
    }

    public boolean clearLinePlan() {
        this.buildLinePlan = null;
        return true;
    }

    public void render() {
        //this.buildPlan.renderBuildQueue(world, player, spriteBatch, blockAssets);
    }

    public void dispose() {}


}
