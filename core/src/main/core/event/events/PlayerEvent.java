package core.event.events;

import core.event.Events;
import data.map.asset.AssetType;
import core.player.Player;

public class PlayerEvent {
    public PlayerEvent() {}

    public static final class playerStartedMoving {
        public final Player player;
        public final float x, y;

        public playerStartedMoving(Player player) {
            this.player = player;
            this.x = player.getPlayerPositionX();
            this.y = player.getPlayerPositionY();

        }

        public static void fire(Player player) {
            Events.fire(new playerStartedMoving(player));
        }

    }

    public static final class playerIsMoving {
        public final Player player;
        public final float x, y;

        public playerIsMoving(Player player) {
            this.player = player;
            this.x = player.getPlayerPositionX();
            this.y = player.getPlayerPositionY();
        }

        public static void fire(Player player) {
            Events.fire(new playerIsMoving(player));
        }
    }

    public static final class playerStoppedMoving {
        public final Player player;
        public final float x, y;

        public playerStoppedMoving(Player player) {
            this.player = player;
            this.x = player.getPlayerPositionX();
            this.y = player.getPlayerPositionY();
        }

        public static void fire(Player player) {
            Events.fire(new playerStoppedMoving(player));
        }
    }

    //TODO: deprecate this.
    public static final class blockSelected {
        public final AssetType selectedType;

        public blockSelected(AssetType selectedType) {
            this.selectedType = selectedType;
        }

        public static void fire(AssetType selectedType) {
            Events.fire(new blockSelected(selectedType));
        }
    }




}
