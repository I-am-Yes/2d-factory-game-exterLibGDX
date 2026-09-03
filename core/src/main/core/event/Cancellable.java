package core.event;

public interface Cancellable {
    void cancel();
    boolean isCancelled();
}
