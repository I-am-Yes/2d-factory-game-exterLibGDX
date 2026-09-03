package core.debug;

import core.event.Events;
import core.event.GameEvent;
import core.event.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

import static core.helper.PrintConsoleHelper.*;

public class EventsDebugger {
    public EventsDebugger() {}

    private static final List<Object> firedEvents = new ArrayList<>();

    private static boolean installed;

    public static void install() {
        if (installed) return;
        installed = true;


        Events.onAny(event -> {
            if (event == null) return;
            firedEvents.add(event);


            println("Event Fired: " + eventToString(event));

            if (event instanceof GameEvent.MapGenerated mapGenerated) {

                println("MapGenerated with seed: " + mapGenerated.mapConfig.seed);

            }

            if (event instanceof PlayerEvent.playerStartedMoving playerMoved) {
                println("PlayerMoved with pos: " + playerMoved.x + " x, " + playerMoved.y + " y");
            }
            if (event instanceof PlayerEvent.playerIsMoving playerIsMoving) {
                println("PlayerIsmoving with pos: " + playerIsMoving.x + " x, " + playerIsMoving.y);
            }

            if (event instanceof PlayerEvent.playerStoppedMoving playerStopped) {
                println("PlayerStopped with pos: " + playerStopped.x + " x, " + playerStopped.y);
            }



        });
    }

    public void update() {
        install();
    }


    private static String eventToString(Object event) {
        return "[" + event.getClass().getSimpleName() + "]";
    }

    private static Object getAllFiredEvents() {
        for (Object event : firedEvents) {
            return event;
        }
        return "None";
    }

}
