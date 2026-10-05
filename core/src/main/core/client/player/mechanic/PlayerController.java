package core.client.player.mechanic;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.client.Player;

import arcane.*;
import arcane.math.*;

import static arcane.Cores.*;
import static arcane.input.Input.Keys.*;

public class PlayerController {

    private float vx, vy;
    private static final float ACCEL = 40f;   // how fast you speed up
    private static final float FRICTION = 25f; // how fast you slow down

    private boolean playerMovingEventFired = false;
    private float playerMovingEventTimer = 0f;
    private static final float MOVE_NOTICE_DELAY = 0.2f;

    public void moveCharacter(Player player, Sprite sprite, float speed, float worldW, float worldH) {
        float inputX = 0f;
        float inputY = 0f;
        float delta = Time.delta();

        boolean wasMoving = isPlayerMoving();

        if (input.isKeyPressed(A)) inputX -= 1f;
        if (input.isKeyPressed(D)) inputX += 1f;
        if (input.isKeyPressed(W)) inputY += 1f;
        if (input.isKeyPressed(S)) inputY -= 1f;

        float rate = (inputX != 0f || inputY != 0f) ? ACCEL : FRICTION;

        vx = MathUtils.lerp(vx, inputX * speed, Math.min(1f, rate * delta));
        vy = MathUtils.lerp(vy, inputY * speed, Math.min(1f, rate * delta));

        sprite.translate(vx * delta, vy * delta);

        boolean nowMoving = isPlayerMoving();

        if (!wasMoving && nowMoving) {
            playerMovingEventTimer = 0;
            playerMovingEventFired = false;
        }

        if (nowMoving) {
            playerMovingEventTimer += delta;
            if (!playerMovingEventFired && playerMovingEventTimer >= MOVE_NOTICE_DELAY) {
                playerMovingEventFired = true;
            }
        } else {
            playerMovingEventTimer = 0f;
            playerMovingEventFired = false;
        }

    }

    public boolean isPlayerMoving() {
        return Math.abs(vx) > 0.01f || Math.abs(vy) > 0.01f;
    }

    public float getSpeed() {
        return vx;
    }


}
