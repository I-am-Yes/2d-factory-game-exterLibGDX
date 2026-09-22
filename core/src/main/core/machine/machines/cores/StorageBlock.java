package core.machine.machines.cores;

import core.machine.Block;
import core.machine.Machine;
import core.machine.category.Delting;
import core.machine.category.Tickable;
import core.machine.state.Direction;
import core.machine.state.MachineType;
import core.machine.utils.Item;

public class StorageBlock extends Block {
    public static final int DEFAULT_CAPACITY = 100;

    private final int capacity;

    public StorageBlock() {
        this(DEFAULT_CAPACITY);
    }

    protected StorageBlock(int capacity) {
        super();
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Storage capacity must be positive."
            );
        }
        this.capacity = capacity;
    }


    @Override
    public StorageMachine createMachine(
        int tileX,
        int tileY,
        Direction direction
    ) {
        return new StorageMachine(
            tileX,
            tileY,
            direction,
            capacity
        );
    }

    public int getCapacity() {
        return capacity;
    }

    public class StorageMachine extends Machine implements Delting, Tickable {
        public StorageMachine(int tileX, int tileY, Direction direction, int capacity) {
            super(tileX, tileY, direction, MachineType.STORAGE_BLOCK, capacity);
        }

        @Override
        public void update(float delta) {
        }

        @Override
        public void tickUpdate(float tickDelta) {
        }

        @Override
        public boolean acceptLoad(Item load) {
            if (!canAcceptLoad(load)) return false;




            return true;
        }

        @Override
        public boolean canAcceptLoad(Item load) {
            return getCapacity() > 0 && isFull();
        }

    }

}
