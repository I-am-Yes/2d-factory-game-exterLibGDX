package core.helper;

import core.machine.state.Direction;

public class RenderUtils {


    public static float getRotationDegree(Direction direction) {
        if (direction == null) return 0f;

        return switch (direction) {
            case EAST -> 0f;
            case NORTH -> 90f;
            case WEST -> 180f;
            case SOUTH -> -90f;
        };
    }
}
