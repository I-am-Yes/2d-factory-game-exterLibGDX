package data.map.asset;

import com.badlogic.gdx.graphics.Color;

public enum GhostState {
    DEFAULT(new Color(0.5f, 0.5f, 0.8f, 0.4f)),
    ACCEPTED(new Color(0.5f, 1f, 0.5f, 0.3f)),
    REJECTED(new Color(1f, 0.4f, 0.4f, 0.8f));

    private final Color color;

    GhostState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        // Color is mutable, so return a copy.
        return new Color(color);
    }
}
