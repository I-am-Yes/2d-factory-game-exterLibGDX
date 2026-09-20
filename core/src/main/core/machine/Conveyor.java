package core.machine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import core.machine.state.MachineType;
import core.machine.utils.Item;

public final class Conveyor {
    private Conveyor() {}

    public static void advancedLogical(Machine conveyor, float tickDelta, float beltSpeed) {
        if (conveyor.type != MachineType.CONVEYOR ||
            conveyor.item == null) {
            return;
        }

        Item item = conveyor.item;
        float size = conveyor.type.getSize();
        item.progress = Math.min(1f, item.progress + beltSpeed * tickDelta);

        float centerX = conveyor.tileX + size * 0.5f;
        float centerY = conveyor.tileY + size * 0.5f;

        float inputX = centerX - conveyor.direction.dx * size / 2;
        float inputY = centerY - conveyor.direction.dy * size / 2;

        item.currentX = inputX + conveyor.direction.dx * item.progress;
        item.currentY = inputY + conveyor.direction.dy * item.progress;

    }

    public static void updateVisuals(Array<Machine> conveyors, float delta, float visualSpeed) {
        for (Machine conveyor : conveyors) {
            if (conveyor.item == null) {
                continue;
            }
            updateVisual(conveyor.item, delta, visualSpeed);
        }
    }

    public static void updateVisual(Item item, float delta, float visualSpeed) {
        float dx = item.currentX - item.visualX;
        float dy = item.currentY - item.visualY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0f) return;

        float maxMove = visualSpeed * delta;

        if (distance <= maxMove) {
            item.visualX = item.currentX;
            item.visualY = item.currentY;
            return;
        }

        item.visualX += dx / distance * maxMove;
        item.visualY += dy / distance * maxMove;
    }

    public static void updateSmoothVisuals(Array<Machine> conveyors, float smoothness) {
        for (Machine conveyor : conveyors) {
            if (conveyor.item == null) {
                continue;
            }
            updateSmoothVisual(conveyor.item, smoothness);
        }
    }

    public static void updateSmoothVisual(Item item, float smoothness) {
        if (item == null) {
            return;
        }

        item.visualX = MathUtils.lerp(
            item.visualX,
            item.currentX,
            smoothness
        );

        item.visualY = MathUtils.lerp(
            item.visualY,
            item.currentY,
            smoothness
        );
    }

}
