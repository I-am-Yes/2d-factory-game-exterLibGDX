package core.event;

import Data.map.FloorType;
import Data.map.MapConfig;

/**
 * Event catalog for this game.
 *
 * Class events = one-off, carry data  -> Events.on / Events.fire(new ...)
 * Trigger enum = fires often, no data -> Events.run / Events.fire(Trigger.x)
 */
public class GameEvent {

    public GameEvent() {}

    /** Fired once after World.generateWorld finishes. */
    public static final class MapGenerated {
        public final MapConfig mapConfig;
        public final FloorType[][] floorGrid;

        public MapGenerated(MapConfig mapConfig, FloorType[][] floorGrid) {
            this.mapConfig = mapConfig;
            this.floorGrid = floorGrid;
        }

        public static void fire(MapConfig mapConfig, FloorType[][] floorGrid) {
                Events.fire(new MapGenerated(mapConfig, floorGrid));
        }

    }




    // /** Mouse/tile hover changed (from OverlayRenderer). */
    // public static final class TileHovered {
    //     public final int tileX, tileY;
    //     public final boolean walkable;
    //
    //     public TileHovered(int tileX, int tileY, boolean walkable) {
    //         this.tileX = tileX;
    //         this.tileY = tileY;
    //         this.walkable = walkable;
    //     }
    // }

    // /** Player confirmed building placement. */
    // public static final class BlockPlaced {
    //     public final int tileX, tileY;
    //     public final String blockId;
    //
    //     public BlockPlaced(int tileX, int tileY, String blockId) {
    //         this.tileX = tileX;
    //         this.tileY = tileY;
    //         this.blockId = blockId;
    //     }
    // }

}
