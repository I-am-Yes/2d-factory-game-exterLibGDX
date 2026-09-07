package core.entities.plan;

import core.event.Events;
import core.event.GameEvent;
import core.world.World;

public class PlanManager {

    private final World world;
    private boolean isConstructing;

    public PlanManager(World world) {
        this.world = world;

        Events.on(GameEvent.PlanConstructRequest.class, request -> {
            PlanContructor<?> constructor =
                new PlanContructor<>(world, request.planBuilder);

            isConstructing = constructor.construct();
        });
    }

    public boolean isConstructing() {
        return isConstructing;
    }

}

