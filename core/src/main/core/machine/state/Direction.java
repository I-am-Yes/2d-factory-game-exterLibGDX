package core.machine.state;

public enum Direction {

    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction rotateClockwise() {
        return values()[(ordinal() + 1) % values().length];
    }

    public Direction rotateCounterClockwise() {
        return values()[Math.floorMod(ordinal() - 1, values().length)];
    }

    public Direction rotateHalfClockwise() {
        return values()[(ordinal() + 2) % values().length];
    }

    public Direction rotateHalfCounterClockwise() {
        return values()[Math.floorMod(ordinal() - 2, values().length)];
    }

}
