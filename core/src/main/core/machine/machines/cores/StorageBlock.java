package core.machine.machines.cores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import core.machine.Block;
import core.machine.Machine;
import core.machine.category.Batching;
import core.machine.category.Delting;
import core.machine.state.Direction;
import core.machine.state.MachineType;
import core.machine.utils.Item;
import core.utils.Artist;

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
    public StorageMachine createBlock(int tileX, int tileY, Direction direction) {
        return new StorageMachine(tileX, tileY, direction, capacity);
    }

    public class StorageMachine extends Machine implements Delting, Batching {
        public boolean limitInputSpeed = true;

        public StorageMachine(int tileX, int tileY, Direction direction, int capacity) {
            super(tileX, tileY, direction, MachineType.STORAGE_BLOCK, capacity);

            //TODO: separate visual render from actual logic.
//            setInputSpeed(2f); // Default input speed
            disableInputSpeed();
            disableSmooth();
        }

        @Override
        public void update(float delta) {
            if (this.item == null) return;

            if (this.smoothVisual) {
                updateItemSmoothVisual(VISUAL_SMOOTHNESS);
            } else {
                updateItemVisual(delta);
            }
        }

        @Override
        public void drawBatch(SpriteBatch batch) {
            if (this.item != null) {
                Artist.DrawItem(batch, this.item);
            }
        }

        @Override
        public void updateItemVisual(float delta) {
            float dx = this.item.currentX - this.item.visualX;
            float dy = this.item.currentY - this.item.visualY;
            float distanceSquared = dx * dx + dy * dy;
            float maxMove = 0;
            if (limitInputSpeed) {
                maxMove = this.itemInputSpeed * delta;
            } else {
                maxMove = this.item.visualSpeed * delta;
            }

            if (distanceSquared <= maxMove * maxMove) {
                this.item.visualX = this.item.currentX;
                this.item.visualY = this.item.currentY;
                this.item = null;
                return;
            }

            float distance = (float) Math.sqrt(distanceSquared);
            float scale = maxMove / distance;

            this.item.visualX += dx * scale;
            this.item.visualY += dy * scale;
        }

        @Override
        public void updateItemSmoothVisual(float visualSmoothness) {
            if (this.item == null) return;
            this.item.visualX = MathUtils.lerp(this.item.visualX, this.item.currentX, visualSmoothness);
            this.item.visualY = MathUtils.lerp(this.item.visualY, this.item.currentY, visualSmoothness);
        }

        @Override
        public boolean acceptLoad(Item load) {
            if (!super.acceptLoad(load)) {
                return false;
            }

            float size = type.getSize();
            // Logical destination is the chest center.
            // Preserve visualX/Y from the conveyor.
            load.currentX = tileX + size * 0.5f;
            load.currentY = tileY + size * 0.5f;

            this.item = load;
            wake();
            return true;
        }

        public void setInputSpeed(float speed) {
            limitInputSpeed = true;
            this.itemInputSpeed = speed;
        }

        public void disableInputSpeed() {
            limitInputSpeed = false;
        }

        @Override
        public boolean canAcceptLoad(Item load) {
            return this.item == null && load != null && acceptItem(load.type);
        }

        @Override
        public boolean shouldStayActive() {
            return this.item != null;
        }

        @Override
        public boolean isFull() {
            if (storage == null) return true;
            if (hasUnlimitedStorage()) return false;
            return totalItems >= capacity;
        }

        public boolean hasUnlimitedStorage() {
            return capacity == UNLIMITED_CAPACITY;
        }
    }

}
