package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.system.ContextProvider;
import core.system.context.PlayerContext;
import core.player.*;
import core.app.GameContext;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;
import core.player.mechanic.OverlayHelper;
import core.player.mechanic.PlayerController;
import core.render.RenderLayer;
import core.system.GameSysCycle;
import core.world.World;

public class PlayerSystem implements GameSysCycle, ContextProvider<PlayerContext> {

    private GameContext context;
    private World world;
    private Window window;
    private Viewport viewport;
    private InputHandler input;
    private AssetsHandler assets;
    private Player player;
    private PlayerAction action;
    private PlayerController controller;
    private Overlay overlay;
    private GhostOverlay ghostOverlay;
    private SpriteBatch playerSpriteBatch;
    private ShapeRenderer playerShapeRenderer;

    private PlayerContext playerContext;

    public static final float HOVER_THRESHOLD = 0.20f;
    private final Vector3 mouseWorld = new Vector3();
    private static final Vector2 hoverTile = new Vector2();
    private boolean hoverTargetInitialized;

    private float delta;

    public PlayerSystem(GameContext context) {
        this.context = context;
        this.window = context.window;
        this.world = context.world;
        this.assets = context.assets;
        this.viewport = context.viewport;
        this.input = context.input;

        this.delta = context.getDeltaTime();

        this.playerSpriteBatch = new SpriteBatch();
        this.playerShapeRenderer = new ShapeRenderer();

        this.controller = new PlayerController(
            input,
            window
        );

        this.player = new Player(world, controller, playerSpriteBatch);
        this.action = new PlayerAction(
            world,
            viewport,
            player,
            input,
            playerSpriteBatch,
            playerShapeRenderer,
            assets
        );

        this.overlay = new Overlay(
            world,
            window,
            viewport,
            input,
            action,
            assets,
            new com.badlogic.gdx.graphics.glutils.ShapeRenderer()
        );
        this.ghostOverlay = new GhostOverlay(
            world,
            viewport,
            player,
            action,
            playerSpriteBatch,
            assets,
            action.getBuildGhostLine()
        );


        this.playerContext = new PlayerContext(
            player,
            action,
            controller,
            overlay,
            ghostOverlay,
            playerSpriteBatch,
            playerShapeRenderer
        );
    }

    @Override
    public void update(float delta) {
        updateHoverThreshold();

        //TODO: change this
        controller.setFullScreenByInput();

        player.update(delta);
        action.update(delta);
        overlay.update(delta);
        ghostOverlay.update(delta);
    }

    @Override
    public void render() {
        draw();
        overlay.render();
    }

    public void draw() {

        playerSpriteBatch.setProjectionMatrix(context.viewport.getCamera().combined);
        //BEGIN batch
        playerSpriteBatch.begin();

        player.draw();
        ghostOverlay.draw();

        playerSpriteBatch.end();
        //END batch


        action.draw();
    }

    @Override
    public void dispose() {
        action.dispose();
    }

    public void updateHoverThreshold() {
        float tileSize = context.world.getTileSize();
        OverlayHelper.updateMouseWorld(context.viewport, mouseWorld);
        if (!hoverTargetInitialized) {
            hoverTile.set(
                MathUtils.floor(mouseWorld.x / tileSize),
                MathUtils.floor(mouseWorld.y / tileSize)
            );
            hoverTargetInitialized = true;
        }
        OverlayHelper.updateTileWithThreshold(
            mouseWorld,
            tileSize,
            hoverTile
        );
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerContext getPlayerContext() {
        return playerContext;
    }

    public static int getHoverTileX() {
        return (int) hoverTile.x;
    }

    public static int getHoverTileY() {
        return (int) hoverTile.y;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.PLAYER_LAYER;
    }

    @Override
    public PlayerContext getContext() {
        return playerContext;
    }

}
