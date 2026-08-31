package core.debug;

import Data.debug.DebugType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntSet;
import core.InputHandler;

public class InputDebugger {

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


    public void update(InputHandler input) {

    }

    private void updateKeys(InputHandler input) {
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

    private void printKeyPressed(int keyCode) {
        System.out.printf("KeyPressed: %s%n", KeyName(keyCode));
    }

    private void printKeyJustPressed(int keyCode) {
        System.out.printf("KeyJustPressed: %s%n", KeyName(keyCode));
    }

    private void printKeyReleased(int keyCode) {
        System.out.printf("KeyReleased: %s%n", KeyName(keyCode));
    }

    private void printMousePressed(int button) {
        System.out.printf("MousePressed: %s%n", ButtonName(button));
    }

    private void printMouseJustPressed(int button) {
        System.out.printf("MouseJustPressed: %s%n", ButtonName(button));
    }

    private void printMouseReleased(int button) {
        System.out.printf("MouseReleased: %s%n", ButtonName(button));
    }

    private void printMousePos(float x, float y) {
        System.out.printf("MousePos: (%.0f, %.0f)%n", x, y);
    }

}
