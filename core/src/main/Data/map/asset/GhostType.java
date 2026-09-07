package Data.map.asset;

public final class GhostType<T> {

    private final T sourceType;

    public GhostType(T sourceType) {
        this.sourceType = sourceType;
    }

    public T getSourceType() {
        return sourceType;
    }

    public static <T> GhostType<T> translateToGhost(T objectType) {
        return new GhostType<>(objectType);
    }

}
