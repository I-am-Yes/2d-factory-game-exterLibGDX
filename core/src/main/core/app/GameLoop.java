package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameLoop {

    private final GameContext context;
    private final float UPDATE_INTERVAL = 1f / 60f; // 60 updates per second

    public GameLoop(GameContext context) {
        this.context = context;
    }

    public void update() {
        context.playerAction.update();
        context.cameraController.update();

        context.renderer.update();

    }

    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
        context.input.endFrame();
    }
    private void input() {

    }

    private void logic() {
        context.player.update(UPDATE_INTERVAL);
        context.interfaceHandler.update();
        //TODO: change this
        context.controller.setFullScreenByInput();
        context.overlayRenderer.update();
        context.ghostOverlayRenderer.update(UPDATE_INTERVAL);
        context.debug.update();
    }
    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        context.viewport.apply();

        context.world.render((OrthographicCamera) context.viewport.getCamera());
        context.spriteBatch.setProjectionMatrix(context.viewport.getCamera().combined);
        context.debug.render();

        context.overlayRenderer.render();

        //BEGIN batch
        context.spriteBatch.begin();

        context.planRenderer.render();
        context.ghostOverlayRenderer.draw();
        context.player.draw(context.spriteBatch);
        context.ghostOverlayRenderer.render();

        context.spriteBatch.end();
        //END batch

        context.playerAction.draw();
        context.interfaceHandler.draw();
    }


}
