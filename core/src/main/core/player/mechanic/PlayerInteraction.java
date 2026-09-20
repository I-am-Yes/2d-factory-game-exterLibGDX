package core.player.mechanic;

import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.player.Player;
import core.player.PlayerAction;
import core.world.World;

public final class PlayerInteraction {

    private final World world;
    private final Player player;
    private final Viewport viewport;
    private final PlayerAction playerAction;
    private final InputHandler input;

    public PlayerInteraction(World world, Player player, Viewport viewport, PlayerAction playerAction, InputHandler input) {
        this.world = world;
        this.player = player;
        this.playerAction = playerAction;
        this.viewport = viewport;
        this.input = input;
    }


    public void update(float delta) {

    }

    public void tickUpdate(float tickDelta) {

    }

    public void render() {

    }

    public void dispose() {

    }

}
