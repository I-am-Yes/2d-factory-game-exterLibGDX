package core.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import core.InputHandler;

public class Controller {

    private float vx, vy;
    private static final float ACCEL = 40f;   // how fast you speed up
    private static final float FRICTION = 25f; // how fast you slow down

    public void moveCharacter(Sprite sprite, float delta, float speed, float worldW, float worldH, InputHandler input) {
        float inputX = 0f;
        float inputY = 0f;

        if (input.isKeyPressed(Input.Keys.A, Input.Keys.LEFT))  inputX -= 1f;
        if (input.isKeyPressed(Input.Keys.D, Input.Keys.RIGHT)) inputX += 1f;
        if (input.isKeyPressed(Input.Keys.W, Input.Keys.UP))    inputY += 1f;
        if (input.isKeyPressed(Input.Keys.S, Input.Keys.DOWN))  inputY -= 1f;

        float rate = (inputX != 0f || inputY != 0f) ? ACCEL : FRICTION;

        vx = MathUtils.lerp(vx, inputX * speed, Math.min(1f, rate * delta));
        vy = MathUtils.lerp(vy, inputY * speed, Math.min(1f, rate * delta));

        sprite.translate(vx * delta, vy * delta);

    }
}
