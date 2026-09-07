package core.event;

import Data.map.asset.FloorType;
import Data.map.MapConfig;
import core.entities.BuildPlan;

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

     public static final class BlockPlaceRequest extends CancellableEvent {
         public final int tileX, tileY;
         public final FloorType floorType;

         public BlockPlaceRequest(int tileX, int tileY, FloorType floorType) {
             this.tileX = tileX;
             this.tileY = tileY;
             this.floorType = floorType;
         }

         public static void fire(int tileX, int tileY, FloorType floorType) {
             BlockPlaceRequest request = new BlockPlaceRequest(tileX, tileY, floorType);
             Events.fire(request);
             // handler runs synchronously during fire(); cancelled = placement blocked
         }
     }

     public static final class BlockPlaced {
        public final int tileX, tileY;
        public final FloorType floorType;

        public BlockPlaced(int tileX, int tileY, FloorType floorType) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.floorType = floorType;
        }

        public static void fire(int tileX, int tileY, FloorType floorType) {
            Events.fire(new BlockPlaced(tileX, tileY, floorType));
        }

     }

     public static final class BuildPlanRequest {
        public final int planX, planY;
        public final FloorType floorType;
        public final BuildPlan buildPlan;

        public BuildPlanRequest(int planX, int planY, FloorType floorType) {
            this.planX = planX;
            this.planY = planY;
            this.floorType = floorType;
            this.buildPlan = new BuildPlan(planX, planY, floorType);
        }

        public static void fire(int planX, int planY, FloorType floorType) {
            Events.fire(new BuildPlanRequest(planX, planY, floorType));
        }
     }



}
