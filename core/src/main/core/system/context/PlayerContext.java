package core.system.context;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import core.app.GameContext;
import core.player.Player;
import core.player.PlayerAction;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;
import core.player.mechanic.PlayerController;

public class PlayerContext {

    public GameContext context;
    public Player player;
    public PlayerAction action;
    public PlayerController controller;
    public Overlay overlay;
    public GhostOverlay ghostOverlay;
    public ShapeRenderer playerShapeRenderer;

    public PlayerContext(
        Player player, PlayerAction action, PlayerController controller,
        Overlay overlay, GhostOverlay ghostOverlay,
        ShapeRenderer playerShapeRenderer
    ) {
        this.player = player;
        this.action = action;
        this.controller = controller;
        this.overlay = overlay;
        this.ghostOverlay = ghostOverlay;
        this.playerShapeRenderer = playerShapeRenderer;

    }

}
