package core.entities.plan;

import data.map.asset.AssetType;
import com.badlogic.gdx.utils.Queue;
import core.event.Events;
import core.event.GameEvent;
import core.world.World;

public class PlanManager {

    private final World world;
    private boolean isConstructing;
    private final Queue<PlanBuilder<?>> planRenderQueue = new Queue<>();

    public PlanManager(World world) {
        this.world = world;

        Events.on(GameEvent.PlanBuilderRenderRequest.class, request -> {
            addPlanToRenderQueue(request.planBuilder);
        });

        Events.on(GameEvent.PlanBuilderRequest.class, request -> {

            for (int i = 0; i < request.planBuilder.getPlanQueue().size; i++) {
                PlanEntity<?> currentPlanEntity = request.planBuilder.getPlanEntityAt(i);
                world.placeGhost(
                    currentPlanEntity.getX(),
                    currentPlanEntity.getY(),
                    currentPlanEntity.getGhostType()
                );
            }
        });

        Events.on(GameEvent.PlanConstructRequest.class, request -> {
            PlanConstructor<?> constructor =
                new PlanConstructor<>(world, request.planBuilder);

            isConstructing = constructor.construct();
        });
    }

    public void update() {}

    public boolean isConstructing() {
        return isConstructing;
    }

    public Queue<PlanBuilder<?>> getPlanRenderQueue() {
        return planRenderQueue;
    }

    public void addPlanToRenderQueue(PlanBuilder<AssetType> planBuilder) {
        if (planBuilder == null) return;
        planRenderQueue.addFirst(planBuilder);
    }

}

