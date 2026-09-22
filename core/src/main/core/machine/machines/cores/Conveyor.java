package core.machine.machines.cores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.LongMap;
import core.machine.Block;
import core.machine.Machine;
import core.machine.category.Batching;
import core.machine.category.Delting;
import core.machine.category.Tickable;
import core.machine.state.Direction;
import core.machine.category.ExposeInfo;
import core.machine.state.MachineType;
import core.machine.utils.Item;
import core.utils.Artist;

import java.util.function.LongFunction;

import static core.system.systems.FactorySystem.tileKey;

public class Conveyor extends Block implements ExposeInfo {
    public static final float DEFAULT_SPEED = 20f;

    public final boolean SMOOTH_CONVEYORS = true;
    public final float VISUAL_SMOOTHNESS = 20f;
    public final float VISUAL_CATCHUP_MULTIPLIER = 1.25f;

    private final float speed;

    public Conveyor() {
        this(DEFAULT_SPEED);
    }

    protected Conveyor(float speed) {
        super();
        this.speed = speed;
    }

    @Override
    public ConveyorMachine createMachine(int tileX, int tileY, Direction direction) {
        return new ConveyorMachine(tileX, tileY, direction);
    }

    @FunctionalInterface
    public interface MachineLookup {
        Machine get(int tileX, int tileY);
    }

    public class ConveyorMachine extends Machine implements Delting, Tickable, Batching {
        public ConveyorMachine(int tileX, int tileY, Direction direction) {
            super(tileX, tileY, direction, MachineType.CONVEYOR);
        }

        //TODO: add arraylist to each conveyor to allow multiple items on a single conveyor
        // with entrance accept and exit pushing
        @Override
        public void update(float delta) {
            if (this.item == null) return;

            if (SMOOTH_CONVEYORS) {
                float smoothAlpha = 1f - (float) Math.exp(-VISUAL_SMOOTHNESS * delta);

                updateSmoothVisual(item, smoothAlpha);
            } else {
                float visualSpeed = speed * VISUAL_CATCHUP_MULTIPLIER;
                updateVisual(item, delta, visualSpeed);
            }
        }

        @Override
        public void tickUpdate(float tickDelta) {
            advancedLogical(tickDelta);
        }

        @Override
        public void drawBatch(SpriteBatch batch) {

            //TODO: only draw items in visible chunk
            if (item == null) {
                return;
            }
            Artist.DrawItem(batch, item);

        }


        public void advancedLogical(float tickDelta) {
            if (this.item == null) {
                return;
            }
            float size = this.type.getSize();
            item.progress = Math.min(1f, item.progress + speed * tickDelta);

            float centerX = this.tileX + size * 0.5f;
            float centerY = this.tileY + size * 0.5f;

            float inputX = centerX - this.direction.dx * size / 2;
            float inputY = centerY - this.direction.dy * size / 2;

            item.currentX = inputX + this.direction.dx * item.progress;
            item.currentY = inputY + this.direction.dy * item.progress;

        }

        public void updateVisual(Item item, float delta, float visualSpeed) {
            float dx = item.currentX - item.visualX;
            float dy = item.currentY - item.visualY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance == 0f) return;

            float maxMove = visualSpeed * delta;

            if (distance <= maxMove) {
                item.visualX = item.currentX;
                item.visualY = item.currentY;
                return;
            }

            item.visualX += dx / distance * maxMove;
            item.visualY += dy / distance * maxMove;
        }

        public void updateSmoothVisual(Item item, float smoothness) {
            item.visualX = MathUtils.lerp(item.visualX, item.currentX, smoothness);
            item.visualY = MathUtils.lerp(item.visualY, item.currentY, smoothness);
        }

        public void collectTransfer(MachineLookup machines, TransferBatch batch) {
            if (!canPushLoad()) return;

            int targetX = tileX + direction.dx;
            int targetY = tileY + direction.dy;

            Machine target = machines.get(targetX, targetY);

            if (target == null || !target.canAcceptLoad(item)) {
                return;
            }

            batch.offer(this, target);
        }

        @Override
        public boolean shouldStayActive() {
            return this.item != null;
        }

        @Override
        public boolean canPushLoad() {
            return item != null && item.progress >= 1f;
        }

        @Override
        public boolean acceptLoad(Item load) {
            if (!canAcceptLoad(load)) {
                return false;
            }
            float size = type.getSize();
            float centerX = tileX + size * 0.5f;
            float centerY = tileY + size * 0.5f;

            load.currentX =
                centerX - direction.dx * size * 0.5f;

            load.currentY =
                centerY - direction.dy * size * 0.5f;

            load.progress = 0f;

            item = load;
            wake();
            return true;
        }

        @Override
        public boolean canAcceptLoad(Item load) {
            return this.item == null && load != null;
        }
    }

    public float getSpeed() {
        return speed;
    }


    @Override
    public String getName() {
        return "Conveyor Belt";
    }

    public static final class TransferBatch {

        private final LongMap<ConveyorMachine> winners = new LongMap<>();

        public void begin() {
            winners.clear();
        }

        public void offer(ConveyorMachine source, Machine target) {
            long targetKey = tileKey(target.tileX, target.tileY);

            ConveyorMachine currentWinner = winners.get(targetKey);

            if (currentWinner == null ||
                Long.compare(
                    tileKey(source.tileX, source.tileY),
                    tileKey(currentWinner.tileX, currentWinner.tileY)
                ) < 0) {

                winners.put(targetKey, source);
            }
        }

        public void apply(LongFunction<Machine> machineByKey) {
            for (LongMap.Entry<ConveyorMachine> entry : winners.entries()) {
                ConveyorMachine source = entry.value;
                Machine target = machineByKey.apply(entry.key);

                if (target != null) {
                    source.pushLoadTo(target);
                }
            }
        }
    }
}
