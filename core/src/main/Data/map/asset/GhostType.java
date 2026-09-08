package Data.map.asset;

public final class GhostType<T extends AssetType> {

    private final T sourceType;
    private GhostState state = GhostState.DEFAULT;


    public GhostType(T sourceType) {
        this.sourceType = sourceType;
    }

    public GhostState getState() {
        return state;
    }

    public void setState(GhostState state) {
        this.state = state;
    }

    public T getSourceType() {
        return sourceType;
    }

    public static <T extends AssetType> GhostType<T> translateToGhost(T objectType) {
        return new GhostType<>(objectType);
    }

}
