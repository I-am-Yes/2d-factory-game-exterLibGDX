package core.app.cores;

public class Update implements AppListener {

    @Override
    public void update() {
        AppListener.super.update();
    }

    @Override
    public void create() {
        AppListener.super.create();
    }

    @Override
    public void render() {
        AppListener.super.render();
    }

    @Override
    public void resize(int width, int height) {
        AppListener.super.resize(width, height);
    }

    @Override
    public void pause() {
        AppListener.super.pause();
    }

    @Override
    public void resume() {
        AppListener.super.resume();
    }

    @Override
    public void dispose() {
        AppListener.super.dispose();
    }
}
