package core.machine.machines.cores;

import core.machine.Block;
import core.machine.Machine;
import core.machine.category.Delting;
import core.machine.category.Tickable;
import core.machine.state.Direction;
import core.machine.state.ItemType;
import core.machine.state.MachineType;
import core.machine.utils.Item;

public class ItemSource extends Block {
    public boolean infiniteSource = false;
    public int sourceCount = 1000;
    public float sourceRate = 0.1f;

    private int remainingSource;

    public ItemSource() {
        this(1000, 0.1f, false);
    }

    protected ItemSource(int sourceCount, float sourceRate, boolean infiniteSource) {
        this.sourceCount = sourceCount;
        this.sourceRate = sourceRate;
        this.infiniteSource = infiniteSource;
    }

    public int getRemaining() {
        return remainingSource;
    }

    @Override
    public ItemSourceMachine createMachine(int tileX, int tileY, Direction direction) {
        return new
            ItemSourceMachine(tileX, tileY, direction, sourceCount, sourceRate, infiniteSource);
    }

    public class ItemSourceMachine extends Machine implements Delting, Tickable {
        public ItemType outputItem;
        private int remainingSource;

        private final float sourceRate;
        private final boolean infiniteSource;

        public ItemSourceMachine(
            int tileX, int tileY, Direction direction,
            int sourceCount, float sourceRate, boolean infiniteSource
        ) {
            super(tileX, tileY, direction, MachineType.CREATIVE_SOURCE);
            this.remainingSource = sourceCount;
            this.sourceRate = sourceRate;
            this.infiniteSource = infiniteSource;
        }

        @Override
        public void update(float delta) {
            // Implement the logic for updating the machine each tick
        }

        @Override
        public void tickUpdate(float tickDelta) {
            produceSource(tickDelta);
        }

        @Override
        public boolean shouldStayActive() {
            return infiniteSource || remainingSource > 0 || item != null;
        }

        public void produceSource(float tickDelta) {
            if (!canProduce()) return;

            productionTimer += eDelta(tickDelta);
            if (productionTimer < sourceRate) return;

            productionTimer -= sourceRate;
            Item newItem = createSourceItem();
            placeLoadAtOutput(newItem);
            //push and hold one
            holdOutputLoad(newItem);
            consumeSource();
        }

        @Override
        public boolean canPushLoad() {
            return item != null && item.progress >= 1f;
        }

        private Item createSourceItem() {
            return new Item(outputItem);
        }

        private void consumeSource() {
            if (!infiniteSource) {
                remainingSource--;
            }
        }

        private boolean canProduce() {
            return !hasOutputLoad()
                && (infiniteSource || remainingSource > 0) && outputItem != null;
        }

        public void setOutputItem(ItemType outputItem) {
            this.outputItem = outputItem;
        }

        public ItemType getOutputItem() {
            return outputItem;
        }

        public int getRemaining() {
            return remainingSource;
        }

    }


}
