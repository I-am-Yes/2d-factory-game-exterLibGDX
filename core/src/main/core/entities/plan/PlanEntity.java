package core.entities.plan;

import core.machine.state.Direction;
import data.map.asset.AssetType;
import data.map.asset.GhostType;
import com.badlogic.gdx.math.Vector2;

public class PlanEntity<T extends AssetType> {

    private int x, y;
    private final Direction direction;
    private final GhostType<T> ghostType;

    //TODO: set removed to true when the plan is removed from the world
    private boolean removed;

    public PlanEntity(int x, int y, Direction direction, GhostType<T> ghostType) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.ghostType = ghostType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public GhostType<T> getGhostType() {
        return ghostType;
    }
    public Direction getDirection() {
        return direction;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void remove() {
        this.removed = true;
    }


    //TODO: add current plan object to be valid/accepted to be able to construct later when we have ghost plan system
    //TODO: plan object coordinates should not be final as it will be moveable

}
