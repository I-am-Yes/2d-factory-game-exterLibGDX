package core.render;

import core.app.GameContext;
import data.map.asset.FloorType;
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
    private final PlayerAction playerAction;

    private int x, y;

    private float delta;

    public GhostLineRenderer(GameContext context) {
        this.world = context.world;
        this.viewport = context.viewport;
        this.playerAction = context.playerAction;
        this.spriteBatch = context.spriteBatch;
        this.shapeRenderer = context.shapeRenderer;

    }

    public void update() {

    }

    public void render() {}

    public void draw() {}

    public void dispose() {}


}
