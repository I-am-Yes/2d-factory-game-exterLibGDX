package core.entities.plan;

import data.map.asset.AssetType;
import core.event.GameEvent;
import core.world.World;

public class PlanConstructor<T extends AssetType> {

    private final World world;
    private final PlanBuilder<T> planBuilder;
    private boolean isConstructing;
    private boolean disposed;

    public PlanConstructor(World world, PlanBuilder<T> planBuilder) {
        this.world = world;
        this.planBuilder = planBuilder;
    }

    public boolean construct() {
        for (int i = 0; i < planBuilder.getPlanQueue().size; i++) {
            if (planBuilder.getPlanQueue().isEmpty()) return false;
            isConstructing = true;

            PlanEntity<T> plan = planBuilder.getPlanEntityAt(i);

            boolean placed = world.getPlacementService().placeBlock(
                plan.getX(), plan.getY(), plan.getDirection(),
                plan.getGhostType().getSourceType()
            );

            if (!placed) dispose(); return false;
        }

        GameEvent.PlanBuilderFinished.fire(planBuilder, this);
        dispose();
        return true;
    }

    public boolean isConstructing() {
        return isConstructing;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void dispose() {
        isConstructing = false;
        disposed = true;
    }

}
