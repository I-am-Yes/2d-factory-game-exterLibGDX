package core;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class UiInputGate extends InputAdapter {

    private final Stage stage;
    private final Vector2 point = new Vector2();

    private boolean pointerOverGUI;

    public UiInputGate(Stage stage) {
        this.stage = stage;
    }

    private boolean updateUiHit(int screenX, int screenY) {
        point.set(screenX, screenY);
        stage.screenToStageCoordinates(point);
        pointerOverGUI = stage.hit(point.x, point.y, true) != null;
        return pointerOverGUI;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        updateUiHit(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateUiHit(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateUiHit(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        updateUiHit(screenX, screenY);
        return false;
    }

    public boolean isPointerOverGUI() {
        return pointerOverGUI;
    }

}
