package core.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.app.context.PlayerContext;
import core.player.*;
import core.app.context.GameContext;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;
import core.player.mechanic.OverlayHelper;

public class PlayerSystem implements GameSystem {

    private GameContext context;
    private Player player;
    private PlayerAction action;
    private Overlay overlay;
    private GhostOverlay ghostOverlay;

    private PlayerContext playerContext;

    public static final float HOVER_THRESHOLD = 0.20f;
    private final Vector3 mouseWorld = new Vector3();
    private static final Vector2 hoverTile = new Vector2();
    private boolean hoverTargetInitialized;

    private float delta;

    public PlayerSystem(GameContext context) {
        this.context = context;

        this.delta = context.getDeltaTime();

        this.player = new Player(context);
        this.action = new PlayerAction(context, player);
        this.overlay = new Overlay(context, action);
        this.ghostOverlay = new GhostOverlay(context, player, action);



        this.playerContext = new PlayerContext(player, action, overlay, ghostOverlay);
        context.playerContext = getPlayerContext();
    }

    public void update(float delta) {
        updateHoverThreshold();

        player.update(delta);
        action.update(delta);
        overlay.update(delta);
        ghostOverlay.update(delta);
    }

    public void render() {
        draw();
        overlay.render();
    }

    public void draw() {
        //BEGIN batch
        context.spriteBatch.begin();

        player.draw();
        ghostOverlay.draw();

        context.spriteBatch.end();
        //END batch


        action.draw();
    }

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

}
