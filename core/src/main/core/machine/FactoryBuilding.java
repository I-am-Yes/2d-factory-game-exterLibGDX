package core.machine;

import core.machine.state.Direction;
import core.machine.state.FactoryType;
import core.machine.utils.Item;

public final class FactoryBuilding {

    public final int tileX;
    public final int tileY;
    public final FactoryType type;
    public Direction direction;

    public Item item;
    public float productionTimer;
    public long receivedItems;

    public FactoryBuilding(int tileX, int tileY, FactoryType type, Direction direction) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;
        this.direction = direction;
    }

}
