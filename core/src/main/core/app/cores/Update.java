package core.app.cores;

import core.event.Events;

public class Update implements AppListener {

    @Override
    public void update() {
        Events.fire(Events.Trigger.update);

        int ticks = Time.accelerate();
        for (int i = 0; i < ticks; i++) {
            Events.fire(Events.Trigger.beforeUpdate);

            ticks++;

            Events.fire(Events.Trigger.afterUpdate);
        }
    }

}
