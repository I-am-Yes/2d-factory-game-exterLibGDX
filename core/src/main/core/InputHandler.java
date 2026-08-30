package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntSet;

public class InputHandler extends InputAdapter {


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
        mouseJustReleased.add(button);
        return false;
    }

    public void endFrame() {
        keyJustReleased.clear();
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

}
