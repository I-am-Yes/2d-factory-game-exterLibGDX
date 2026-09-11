package core.app.context;

import core.player.Player;
import core.player.PlayerAction;
import core.player.mechanic.GhostOverlay;
import core.player.mechanic.Overlay;

public class PlayerContext {


    public GameContext context;
    public Player player;
    public PlayerAction action;
    public Overlay overlay;
    public GhostOverlay ghostOverlay;

    public PlayerContext(Player player, PlayerAction action, Overlay overlay, GhostOverlay ghostOverlay) {
        this.player = player;
        this.action = action;
        this.overlay = overlay;
        this.ghostOverlay = ghostOverlay;
    }

}
