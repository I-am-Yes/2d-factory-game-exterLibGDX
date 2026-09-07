package core.debug;

import com.badlogic.gdx.Game;
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
            if (event instanceof GameEvent.BlockPlaceRequest) return;
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

            if (event instanceof PlayerEvent.blockSelected blockSelected) {
                println("Selected tile: "  + blockSelected.selectedType);
            }

//            if (event instanceof GameEvent.BlockPlaceRequest blockPlaceRequest) {
//                println("BlockPlaceRequest: " + blockPlaceRequest.type.toString()
//                    + " at x: " + blockPlaceRequest.tileX + " y: " + blockPlaceRequest.tileY);
//            }

            if (event instanceof GameEvent.BlockPlaced blockPlaced) {
                println("BlockPlaced: " + blockPlaced.getClass().getSimpleName()
                    + " at x: " + blockPlaced.tileX + " y: " + blockPlaced.tileY);
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
