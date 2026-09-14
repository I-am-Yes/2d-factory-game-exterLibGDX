package core.system;

import core.app.UpdateDomain;
import core.render.RenderLayer;

public interface GameSysCycle {

    default void update(float delta) {}

    default void render() {}

    default RenderLayer renderLayer() {
        return RenderLayer.WORLD_LAYER;
    }

    default void resize(int width, int height) {}

    default void dispose() {}

    default UpdateDomain updateDomain() {
        return UpdateDomain.GAME_DEFAULT;
    }
}
