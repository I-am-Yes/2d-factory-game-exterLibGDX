package core.app.cores;

import com.badlogic.gdx.ApplicationListener;

public interface AppListener extends ApplicationListener {
    void init();
    void update();

    @Override
    default void create() {
        init();
    }

    @Override default void render() {
        update();
    }

    @Override default void resize(int width, int height) {}
    @Override default void pause() {}
    @Override default void resume() {}
    @Override default void dispose() {}
}
