package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputHandler extends InputAdapter {

    private boolean middleDragging;
    private int lastPanX,  lastPanY;
    private float panDeltaX, panDeltaY; // world units

    private final Vector3 mouseWorld3 = new Vector3();
    private final Vector2 mousePos = new Vector2();
    private final IntSet keyJustReleased = new IntSet();
    private final IntSet mouseJustReleased = new IntSet();

    private float scrollAmountY = 0f;

    public InputHandler() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollAmountY -= amountY;
        return true;
    }

    public float consumeScrollY() {
        float scroll = scrollAmountY;
        scrollAmountY = 0f;
        return scroll;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyJustReleased.add(keycode);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            middleDragging = false;
        }

        mouseJustReleased.add(button);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            middleDragging = true;
            lastPanX = screenX;
            lastPanY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!middleDragging) return false;
        panDeltaX += screenX - lastPanX;
        panDeltaY += screenY - lastPanY;
        lastPanX = screenX;
        lastPanY = screenY;
        return true;

    }

    public void endFrame() {
        keyJustReleased.clear();
        mouseJustReleased.clear();
    }

    public boolean isKeyPressed(int... keys) {
        for (int key : keys) if (Gdx.input.isKeyPressed(key)) {

            return true;
        }
        return false;
    }

    public boolean isKeyJustPressed(int key) {
        return Gdx.input.isKeyJustPressed(key);
    }

    public boolean isKeyReleased(int key) {
        if (keyJustReleased.contains(key)) return true;
        return false;
    }

    public boolean isMiddleDragging() {
        return middleDragging;
    }

    public boolean isMousePressed(int button) {
        return Gdx.input.isButtonPressed(button);
    }

    public boolean isMouseJustPressed(int button) {
        return Gdx.input.isButtonJustPressed(button);
    }

    public boolean isMouseReleased(int button) {
        return mouseJustReleased.contains(button);
    }

    //why would i need this..
    public boolean isMouseUp(int button) {
        return !Gdx.input.isButtonPressed(button);
    }

    public Vector2 getMousePos() {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        return mousePos;
    }

    public Vector2 getMouseWorldPos(Viewport viewport) {
        mouseWorld3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouseWorld3);
        mousePos.set(mouseWorld3.x, mouseWorld3.y);
        return mousePos;
    }

    public void consumePanDelta(int[] out) {
        out[0] = (int) panDeltaX;
        out[1] = (int) panDeltaY;
        panDeltaX = 0;
        panDeltaY = 0;
    }

}
