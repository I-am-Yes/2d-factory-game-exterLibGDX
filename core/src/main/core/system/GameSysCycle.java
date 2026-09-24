package core.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.app.UpdateDomain;
import core.render.RenderLayer;

public interface GameSysCycle {

    default void update(float delta) {}

    default void tickUpdate(float tickDelta) {}

    default void render() {}

    default void drawBatch(SpriteBatch batch) {
        //Must BEGIN batch draw

        //Must END batch draw
    }

    default void drawShapeRenderer(ShapeRenderer shapeRenderer) {}

    default RenderLayer renderLayer() {
        return RenderLayer.WORLD_LAYER;
    }

    default void resize(int width, int height) {}

    default void dispose() {}

    default UpdateDomain updateDomain() {
        return UpdateDomain.GAME_DEFAULT;
    }

    default boolean updateWhenPaused() {
        return false;
    }
}
