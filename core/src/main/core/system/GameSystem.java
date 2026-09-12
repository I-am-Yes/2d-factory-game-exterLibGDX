package core.system;

import core.render.RenderLayer;

public interface GameSystem {

    default void update(float delta) {}

    default void render() {}

    default RenderLayer renderLayer() {
        return RenderLayer.WORLD_LAYER;
    }

    default void resize(int width, int height) {}

    default void dispose() {}
}
