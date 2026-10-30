package core.utils.machine.machines.cores;

import com.badlogic.gdx.math.MathUtils;
import core.utils.blocks.definition.Block;
import core.utils.machine.Machine;
import core.utils.machine.category.Delting;
import core.utils.machine.category.Tickable;
import core.utils.machine.state.Direction;
import core.assets.textures.types.ItemType;
import core.utils.machine.state.MachineType;
import core.utils.machine.transport.OutputEmitter;
import core.utils.machine.transport.TransferBatch;
import core.utils.machine.utils.ItemConveyor;

public class ItemSource extends Block {
    public boolean infiniteSource = false;
    public int sourceCount = 1000;
    public float sourceRate = 0.1f;

    public final boolean SMOOTH_ANI = true;
    public final float VISUAL_SMOOTHNESS = 20f;
    public final float VISUAL_CATCHUP_MULTIPLIER = 1.25f;

    public ItemType outputItem;

    public ItemSource() {
        this(ItemType.TEST_ITEM, 1000, 0.1f, false);
    }

    protected ItemSource(ItemType outputItem, int sourceCount, float sourceRate, boolean infiniteSource) {
        this.sourceCount = sourceCount;
        this.sourceRate = sourceRate;
        this.outputItem = outputItem;
        this.infiniteSource = infiniteSource;
    }

    @Override
    public ItemSourceMachine init(int tileX, int tileY, Direction direction) {
        return new
            ItemSourceMachine(tileX, tileY, direction, outputItem, sourceCount, sourceRate, infiniteSource);
    }

    public class ItemSourceMachine extends Machine implements OutputEmitter, Delting, Tickable {
        public ItemType outputItem;
        private int remainingSource;
        public float productionTimer;

        private final float sourceRate;
        private final boolean infiniteSource;

        public ItemSourceMachine(
            int tileX, int tileY, Direction direction, ItemType outputItem,
            int sourceCount, float sourceRate, boolean infiniteSource
        ) {
            super(tileX, tileY, direction, MachineType.CREATIVE_SOURCE);
            this.remainingSource = sourceCount;
            this.sourceRate = sourceRate;
            this.infiniteSource = infiniteSource;
            this.outputItem = outputItem;
        }

        @Override
        public void update(float delta) {
            if (this.item == null) return;

            if (SMOOTH_ANI) {
                float smoothAlpha = 1f - (float) Math.exp(-VISUAL_SMOOTHNESS * delta);

                updateItemSmoothVisual(smoothAlpha);
            } else {
                updateItemVisual(delta);
            }
        }

        @Override
        public void tickUpdate(float tickDelta) {
            produceSource(tickDelta);
        }

        @Override
        public boolean shouldStayActive() {
            return infiniteSource || remainingSource > 0 || item != null;
        }

        @Override
        public void collectOutput(TransferBatch transferBatch) {
            if (!canPushLoad()) return;

            Machine target = getMachineAt(
                tileX + direction.dx,
                tileY + direction.dy
            );
            transferBatch.add(this, target, item);
        }

        @Override
        public void updateItemVisual(float delta) {
            if (this.item == null || delta <= 0f) return;

            float dx = this.item.currentX - this.item.visualX;
            float dy = this.item.currentY - this.item.visualY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float maxMove = sourceRate * VISUAL_CATCHUP_MULTIPLIER * delta;

            if (distance <= maxMove || distance == 0f) {
                this.item.visualX = this.item.currentX;
                this.item.visualY = this.item.currentY;
                return;
            }

            float scale = maxMove / distance;
            this.item.visualX += dx * scale;
            this.item.visualY += dy * scale;
        }

        @Override
        public void updateItemSmoothVisual(float smoothness) {
            if (this.item == null) return;

            float alpha = MathUtils.clamp(smoothness, 0f, 1f);
            this.item.visualX = MathUtils.lerp(this.item.visualX, this.item.currentX, alpha);
            this.item.visualY = MathUtils.lerp(this.item.visualY, this.item.currentY, alpha);
        }

        private void produceSource(float tickDelta) {
            if (this.item != null) return;
            if (this.outputItem == null) return;
            if (!this.infiniteSource && this.remainingSource <= 0) return;

            productionTimer += eDelta(tickDelta);

            if (productionTimer < sourceRate) return;

            productionTimer -= sourceRate;

            ItemConveyor newItem = createSourceItem();

            placeLoadAtOutput(newItem);

            float size = type.getSize();
            newItem.visualX = tileX + size * 0.5f;
            newItem.visualY = tileY + size * 0.5f;

            holdOutputLoad(newItem);
            consumeSource();
        }

        @Override
        public boolean canPushLoad() {
            return item != null;
        }

        private ItemConveyor createSourceItem() {
            return new ItemConveyor(outputItem);
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

        public boolean isInfinite() {
            return infiniteSource;
        }
    }

}
