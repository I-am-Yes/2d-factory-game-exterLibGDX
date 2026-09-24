package core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Events {

    private static final Map<Object, List<Consumer<?>>> listeners = new HashMap<>();
    private static final List<Consumer<Object>> globalListeners = new ArrayList<>();

    private Events() {}

    /** Subscribe to class events (data payload). Example: Events.on(MapGenerated.class, ...) */
    public static <T> void on(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    /** Subscribe to every fired event. Multiple global listeners may be registered. */
    public static void onAny(Consumer<Object> listener) {
        globalListeners.add(listener);
    }

    /** Subscribe to enum triggers (no payload). Example: Events.run(Trigger.update, ...) */
    public static void run(Enum<?> trigger, Runnable listener) {
        listeners.computeIfAbsent(trigger, k -> new ArrayList<>()).add(e -> listener.run());
    }

    /** Fire event object; notifies on(sameClass) listeners. */
    public static <T> void fire(T event) {
        fire(event.getClass(), event);
    }

    /** Fire by class; internal / explicit type. */
    @SuppressWarnings("unchecked")
    public static <T> void fire(Class<T> type, Object event) {
        notifyGlobal(event != null ? event : type);

        List<Consumer<?>> list = listeners.get(type);
        if (list == null) return;
        for (Consumer<?> consumer : list.toArray(new Consumer[0])) {
            ((Consumer<Object>) consumer).accept(event);
        }
    }

    /** Fire enum trigger; notifies run(trigger) listeners. */
    public static void fire(Enum<?> trigger) {
        notifyGlobal(trigger);

        List<Consumer<?>> list = listeners.get(trigger);
        if (list == null) return;
        for (Consumer<?> consumer : list.toArray(new Consumer[0])) {
            invoke(consumer);
        }
    }

    public static <T> boolean remove(Class<T> type, Consumer<T> listener) {
        List<Consumer<?>> list = listeners.get(type);
        return list != null && list.remove(listener);
    }

    public static boolean removeAny(Consumer<Object> listener) {
        return globalListeners.remove(listener);
    }

    private static void notifyGlobal(Object event) {
        if (globalListeners.isEmpty()) return;
        // copy so a listener may subscribe/unsubscribe during dispatch
        for (Consumer<Object> listener : new ArrayList<>(globalListeners)) {
            listener.accept(event);
        }
    }

    @SuppressWarnings("unchecked")
    private static void invoke(Consumer<?> consumer) {
        ((Consumer<Object>) consumer).accept(null);
    }

}
