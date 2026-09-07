package core.entities.plan;

import Data.map.asset.AssetType;
import Data.map.asset.GhostType;
import com.badlogic.gdx.math.Vector2;

public class PlanEntity<T extends AssetType> {

    private int x, y;
    private final GhostType<T> ghostType;

    public PlanEntity(int x, int y, GhostType<T> ghostType) {
        this.x = x;
        this.y = y;
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


    //TODO: add current plan object to be valid/accepted to be able to construct later when we have ghost plan system
    //TODO: plan object coordinates should not be final as it will be moveable

}
