package core.app.cores;

import arcane.ApplicationListener;
import arcane.Events;
import arcane.Time;

public class Update implements ApplicationListener {

    @Override
    public void update() {
        Events.fire(Events.Trigger.update);

        if (!Time.isPaused()) {
            int ticks = Time.accelerate();
            for (int i = 0; i < ticks; i++) {
                Events.fire(Events.Trigger.beforeUpdate);

                Events.fire(Events.Trigger.tickUpdate);

                Events.fire(Events.Trigger.afterUpdate);
            }
        }


    }

}
