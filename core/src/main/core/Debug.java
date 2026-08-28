package core;

import Data.DebugData;
import Data.DebugData.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntSet;

import java.util.EnumSet;

public class Debug {

    private static final int[] TRACKED_KEYS = {
        Input.Keys.W, Input.Keys.A, Input.Keys.S, Input.Keys.D,
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,
        Input.Keys.F1, Input.Keys.F3, Input.Keys.ESCAPE, Input.Keys.ENTER,

    };

    private static final int[] TRACKED_MOUSEBUTTONS = {
        Input.Buttons.LEFT,
        Input.Buttons.RIGHT,
        Input.Buttons.MIDDLE,

    };

    private final IntSet heldKeys = new IntSet();
    private final IntSet heldMouseButtons = new IntSet();
    private float fpsLogTimer;

    public void enableDebugMode(DebugType debugMode) {
        DebugData.enable(debugMode);
    }
    public void disableDebugMode(DebugType debugMode) {
        DebugData.disable(debugMode);
    }

    public void update(InputHandler input) {

        if (DebugData.isActive(DebugType.RENDER)) {
            printFPStoConsole(0.5f);
        }

        if (DebugData.isActive(DebugType.INPUT)) {
            updateInput(input);
        }
    }



    private void updateInput(InputHandler input) {
        if (!DebugData.isActive(DebugType.INPUT)) return;

        for (int key : TRACKED_KEYS) {
            boolean held = input.isKeyPressed(key);
            if (held && !heldKeys.contains(key)) {
                printKeyPressed(key);
                heldKeys.add(key);
            } else if (!held && heldKeys.contains(key)) {
                printKeyReleased(key);
                heldKeys.remove(key);
            }
        }
        updateMouseInput(input);
    }

    private void updateMouseInput(InputHandler input) {
        if (!DebugData.isActive(DebugType.INPUT)) return;
        for (int button : TRACKED_MOUSEBUTTONS) {
            boolean held = input.isMousePressed(button);
            if (held && !heldMouseButtons.contains(button)) {
                printMousePressed(button);
                Vector2 pos = input.getMousePos();
                printMousePos(pos.x, pos.y);
                heldMouseButtons.add(button);
            } else if  (!held && heldMouseButtons.contains(button)) {
                printMouseReleased(button);
                Vector2 pos = input.getMousePos();
                printMousePos(pos.x, pos.y);
                heldMouseButtons.remove(button);
            }
        }
    }




    public void printFPStoConsole(float delay) {
        if (delay <= 0) delay = 0.5f;
        fpsLogTimer += Gdx.graphics.getDeltaTime();
        if (fpsLogTimer >= delay) {
            System.out.printf("FPS: %d%n", Gdx.graphics.getFramesPerSecond());
            fpsLogTimer = 0f;
        }
    }

    private void printKeyPressed(int keyCode) {
        System.out.printf("KeyPressed: %s%n", KeyName(keyCode));
    }

    private void printKeyJustPressed(int keyCode) {
        System.out.printf("KeyJustPressed: %s%n", KeyName(keyCode));
    }

    private void printKeyReleased(int keyCode) {
        System.out.printf("KeyReleased: %s%n", KeyName(keyCode));
    }

    public void printMousePressed(int button) {
        System.out.printf("MousePressed: %s%n", ButtonName(button));
    }

    public void printMouseJustPressed(int button) {
        System.out.printf("MouseJustPressed: %s%n", ButtonName(button));
    }

    public void printMouseReleased(int button) {
        System.out.printf("MouseReleased: %s%n", ButtonName(button));
    }

    public void printMousePos(float x, float y) {
        System.out.printf("MousePos: (%.0f, %.0f)%n", x, y);
    }


    //helper methods
    private String KeyName(int keyCode) {
        return Input.Keys.toString(keyCode);
    }

    private String ButtonName(int button) {
        return switch (button) {
            case Input.Buttons.LEFT -> "LEFT";
            case Input.Buttons.RIGHT -> "RIGHT";
            case Input.Buttons.MIDDLE -> "MIDDLE";
            case Input.Buttons.BACK -> "BACK";
            case Input.Buttons.FORWARD -> "FORWARD";
            default -> "BUTTON_%d".formatted(button);
        };
    }

}
