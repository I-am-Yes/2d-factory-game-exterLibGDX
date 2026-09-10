package core.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import core.InputHandler;
import core.Player;
import core.Window;
import core.app.GameContext;
import core.event.PlayerEvent.*;

public class Controller {

    private float vx, vy;
    private static final float ACCEL = 40f;   // how fast you speed up
    private static final float FRICTION = 25f; // how fast you slow down

    private boolean playerMovingEventFired = false;
    private float playerMovingEventTimer = 0f;
    private static final float MOVE_NOTICE_DELAY = 0.2f;

    private final InputHandler input;
    private final Window window;

    public Controller(GameContext context) {
        this.input = context.input;
        this.window = context.window;

    }

    public void setFullScreenByInput() {
        if (input.isKeyJustPressed(Input.Keys.F11)) {
            window.setWindowFullscreen(!window.isFullscreen());
        }
    }

    public void moveCharacter(Player player, Sprite sprite, float delta, float speed, float worldW, float worldH) {
        float inputX = 0f;
        float inputY = 0f;

        boolean wasMoving = isPlayerMoving();

        if (input.isKeyPressed(Input.Keys.A)) inputX -= 1f;
        if (input.isKeyPressed(Input.Keys.D)) inputX += 1f;
        if (input.isKeyPressed(Input.Keys.W)) inputY += 1f;
        if (input.isKeyPressed(Input.Keys.S)) inputY -= 1f;

        float rate = (inputX != 0f || inputY != 0f) ? ACCEL : FRICTION;

        vx = MathUtils.lerp(vx, inputX * speed, Math.min(1f, rate * delta));
        vy = MathUtils.lerp(vy, inputY * speed, Math.min(1f, rate * delta));

        sprite.translate(vx * delta, vy * delta);

        boolean nowMoving = isPlayerMoving();

        if (!wasMoving && nowMoving) {
            playerMovingEventTimer = 0;
            playerMovingEventFired = false;
            playerStartedMoving.fire(player);
        }

        if (nowMoving) {
            playerMovingEventTimer += delta;
            if (!playerMovingEventFired && playerMovingEventTimer >= MOVE_NOTICE_DELAY) {
                playerIsMoving.fire(player);
                playerMovingEventFired = true;
            }
        } else {
            playerMovingEventTimer = 0f;
            playerMovingEventFired = false;
        }

        if (wasMoving && !nowMoving) {
            playerStoppedMoving.fire(player);
        }

    }

    public boolean isPlayerMoving() {
        return Math.abs(vx) > 0.01f || Math.abs(vy) > 0.01f;
    }

    public float getSpeed() {
        return vx;
    }


}
