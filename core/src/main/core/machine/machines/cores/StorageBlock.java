package core.machine.machines.cores;

import core.machine.machines.definition.Block;
import core.machine.Machine;
import core.machine.state.Direction;
import core.machine.state.MachineType;
import core.machine.transport.InputAdmission;
import core.machine.utils.Item;

public class StorageBlock extends Block {
    public static final int DEFAULT_CAPACITY = 100;
    public static final int UNLIMITED_CAPACITY = -1;

    private final int capacity;

    public StorageBlock() {
        this(DEFAULT_CAPACITY);
    }

    protected StorageBlock(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public StorageMachine init(int tileX, int tileY, Direction direction) {
        return new StorageMachine(tileX, tileY, direction, capacity);
    }

    public class StorageMachine extends Machine implements InputAdmission {
        public StorageMachine(int tileX, int tileY, Direction direction, int capacity) {
            super(tileX, tileY, direction, MachineType.STORAGE_BLOCK, capacity);
        }

        @Override
        public boolean canReserve(Machine source, Item load, int reservedIncoming, boolean outgoingSelected) {
            return reservedIncoming >= 0
                && canAcceptLoad(source, load)
                && (hasUnlimitedStorage()
                || reservedIncoming < capacity - totalItems);
        }

        @Override
        public boolean canAcceptLoad(Item load) {
            return load != null && acceptItem(load.type);
        }

        @Override
        public boolean isFull() {
            if (storage == null) return true;
            return !hasUnlimitedStorage() && totalItems >= capacity;
        }

        public boolean hasUnlimitedStorage() {
            return capacity == UNLIMITED_CAPACITY;
        }
    }

}
