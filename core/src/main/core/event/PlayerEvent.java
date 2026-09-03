package core.event;

import Data.map.FloorType;
import core.BlockAssets;
import core.Player;

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

    public static final class blockSelected {
        public final FloorType selectedType;

        public blockSelected(FloorType selectedType) {
            this.selectedType = selectedType;
        }

        public static void fire(FloorType selectedType) {
            Events.fire(new blockSelected(selectedType));
        }
    }




}
