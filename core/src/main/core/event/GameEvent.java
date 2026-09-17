package core.event;

import core.machine.state.Direction;
import data.map.asset.FloorType;
import data.map.MapConfig;
import data.map.asset.AssetType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanConstructor;

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

     public static final class BlockPlaceRequest extends CancellableEvent {
         public final int tileX, tileY;
         public final AssetType type;
         public final Direction direction;

         public BlockPlaceRequest(int tileX, int tileY, AssetType type, Direction direction) {
             this.tileX = tileX;
             this.tileY = tileY;
             this.type = type;
             this.direction = direction;
         }

         public static void fire(int tileX, int tileY, AssetType type, Direction direction) {
             Events.fire(new BlockPlaceRequest(tileX, tileY, type, direction));
         }

         public static void fire(int tileX, int tileY, AssetType type) {
             Events.fire(new BlockPlaceRequest(tileX, tileY, type, Direction.EAST));
             // handler runs synchronously during fire(); cancelled = placement blocked
         }
     }

     public static final class BlockPlaced {
        public final int tileX, tileY;
        public final AssetType type;
        public final Direction direction;

        public BlockPlaced(int tileX, int tileY, AssetType type, Direction direction) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.type = type;
            this.direction = direction;
        }

        public static void fire(int tileX, int tileY, AssetType type, Direction direction) {
            Events.fire(new BlockPlaced(tileX, tileY, type, direction));
        }

     }

     public static final class PlanBuilderRenderRequest<T extends AssetType> {
        public final PlanBuilder<T> planBuilder;

        public PlanBuilderRenderRequest(PlanBuilder<T> planBuilder) {
            this.planBuilder = planBuilder;
        }

        public static <T extends AssetType> void fire(PlanBuilder<T> planBuilder) {
            Events.fire(new PlanBuilderRenderRequest<>(planBuilder));
        }

        public static <T extends AssetType> void fire(Queue<PlanBuilder<T>> planBuilders) {
            for (PlanBuilder<T> planBuilder : planBuilders) {
                fire(planBuilder);
            }
        }
     }

     public static final class PlanBuilderRequest<T extends AssetType> {
        public final PlanBuilder<T> planBuilder;

        public PlanBuilderRequest(PlanBuilder<T> planBuilder) {
            this.planBuilder = planBuilder;
        }

        public static <T extends AssetType> void fire(PlanBuilder<T> planBuilder) {
            Events.fire(new PlanBuilderRequest<>(planBuilder));
        }
     }

     public static final class PlanBuilderFinished<T extends AssetType> {
         public final PlanBuilder<T> planBuilder;
         public final PlanConstructor<T> finishedPlan;

         public PlanBuilderFinished(PlanBuilder<T> planBuilder, PlanConstructor<T> finishedPlan) {
            this.planBuilder = planBuilder;
            this.finishedPlan = finishedPlan;
         }

         public static <T extends AssetType> void fire(PlanBuilder<T> planBuilder, PlanConstructor<T> finishedPlan) {
            Events.fire(new PlanBuilderFinished<>(planBuilder, finishedPlan));
         }
     }

     public static final class PlanConstructRequest {
         public final PlanBuilder<? extends AssetType> planBuilder;

         public PlanConstructRequest(PlanBuilder<? extends AssetType> planBuilder) {
            this.planBuilder = planBuilder;
         }

         public static <T extends AssetType> void fire(PlanBuilder<? extends AssetType> planBuilder) {
            Events.fire(new PlanConstructRequest(planBuilder));
         }
     }

     public static final class PlanConstructFinished<T extends AssetType> {
        public final PlanBuilder<T> planBuilder;
        public final PlanConstructor<?> planContructor;
        public final Array<Vector2> finishedPlan;

        public PlanConstructFinished(PlanBuilder<T> planBuilder, PlanConstructor<?> planContructor, Array<Vector2> finishedPlan) {
            this.planBuilder = planBuilder;
            this.planContructor = planContructor;
            this.finishedPlan = finishedPlan;
        }

        public static <T extends AssetType> void fire(PlanBuilder<T> planBuilder, PlanConstructor<? extends AssetType> planContructor, Array<Vector2> finishedPlan) {
            Events.fire(new PlanConstructFinished<>(planBuilder, planContructor, finishedPlan));
        }
     }



}
