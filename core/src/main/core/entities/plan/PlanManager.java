package core.entities.plan;

import arcane.utils.Queue;
import arcane.Events;
import core.event.GameEvent;

import static core.app.vars.Vars.world;

public class PlanManager {

    private boolean isConstructing;
    private final Queue<PlanBuilder<?>> planRenderQueue = new Queue<>();

    public PlanManager() {

        Events.on(GameEvent.PlanBuilderRenderRequest.class, request -> {
            //TODO: fix this warning
            addPlanToRenderQueue(request.planBuilder());
        });

        Events.on(GameEvent.PlanBuilderRequest.class, request -> {

            for (int i = 0; i < request.planBuilder().getPlanQueue().size; i++) {
                PlanEntity<?> currentPlanEntity = request.planBuilder().getPlanEntityAt(i);
                world.getPlacementService().placeGhost(
                    currentPlanEntity.getX(),
                    currentPlanEntity.getY(),
                    currentPlanEntity.getDirection(),
                    currentPlanEntity.getGhostType()
                );
            }
        });

        Events.on(GameEvent.PlanConstructRequest.class, request -> {
            PlanConstructor<?> constructor =
                new PlanConstructor<>(world, request.planBuilder());

            isConstructing = constructor.construct();
        });
    }

    public boolean isConstructing() {
        return isConstructing;
    }

    public Queue<PlanBuilder<?>> getPlanRenderQueue() {
        return planRenderQueue;
    }

    public void addPlanToRenderQueue(PlanBuilder<?> planBuilder) {
        if (planBuilder == null) return;
        planRenderQueue.addFirst(planBuilder);
    }

}

