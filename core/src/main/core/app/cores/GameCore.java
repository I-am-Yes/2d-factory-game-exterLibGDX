package core.app.cores;

import com.badlogic.gdx.ApplicationAdapter;

public abstract class GameCore extends ApplicationAdapter implements AppListener {
    protected AppListener[] children = new AppListener[0];

    public void add(AppListener child) {
        AppListener[] expanded = new AppListener[children.length + 1];
        System.arraycopy(children, 0, expanded, 0, children.length);
        expanded[children.length] = child;
        children = expanded;
    }

    public void init() {
        for (AppListener child : children) {
            child.init();
        }
    }

    @Override
    public void update() {
        for (AppListener child : children) {
            child.update();
        }
    }

    @Override
    public void create() {
        init();
    }

    @Override
    public void render() {
        update();
    }

    @Override
    public void resize(int width, int height) {
        for (AppListener child : children) {
            child.resize(width, height);
        }
    }

    @Override
    public void pause() {
        for (AppListener child : children) {
            child.pause();
        }
    }

    @Override
    public void resume() {
        for (AppListener child : children) {
            child.resume();
        }
    }

    @Override
    public void dispose() {
        for (AppListener child : children) {
            child.dispose();
        }
    }
}
