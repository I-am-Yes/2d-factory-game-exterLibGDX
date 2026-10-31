package core.event;

import arcane.Events;
import core.assets.textures.AssetType;
import core.utils.unit.units.PlayerUnit;

public class PlayerEvent {
    public PlayerEvent() {}

    public static final class playerStartedMoving {
        public final PlayerUnit player;
        public final float x, y;

        public playerStartedMoving(PlayerUnit player) {
            this.player = player;
            this.x = player.getX();
            this.y = player.getY();

        }

        public static void fire(PlayerUnit player) {
            Events.fire(new playerStartedMoving(player));
        }

    }

    public static final class playerIsMoving {
        public final PlayerUnit player;
        public final float x, y;

        public playerIsMoving(PlayerUnit player) {
            this.player = player;
            this.x = player.getX();
            this.y = player.getY();
        }

        public static void fire(PlayerUnit player) {
            Events.fire(new playerIsMoving(player));
        }
    }

    public static final class playerStoppedMoving {
        public final PlayerUnit player;
        public final float x, y;

        public playerStoppedMoving(PlayerUnit player) {
            this.player = player;
            this.x = player.getX();
            this.y = player.getY();
        }

        public static void fire(PlayerUnit player) {
            Events.fire(new playerStoppedMoving(player));
        }
    }

    //TODO: deprecate this.
        public record blockSelected(AssetType selectedType) {

        public static void fire(AssetType selectedType) {
                Events.fire(new blockSelected(selectedType));
            }
        }




}
