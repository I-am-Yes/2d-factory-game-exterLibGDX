package core.machine.machines.cores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import core.machine.machines.definition.Block;
import core.machine.Machine;
import core.machine.category.Batching;
import core.machine.category.Delting;
import core.machine.category.Tickable;
import core.machine.render.RenderManager;
import core.machine.state.Direction;
import core.machine.category.ExposeInfo;
import core.machine.state.MachineType;
import core.machine.transport.InputAdmission;
import core.machine.transport.OutputEmitter;
import core.machine.transport.TransferBatch;
import core.machine.utils.Item;
import core.utils.Artist;

public class Conveyor extends Block implements ExposeInfo {
    public static final float DEFAULT_SPEED = 10f;

    private final float speed;

    public Conveyor() {
        this(DEFAULT_SPEED);
    }

    protected Conveyor(float speed) {
        super();
        this.speed = speed;
    }

    @Override
    public ConveyorMachine init(int tileX, int tileY, Direction direction) {
        return new ConveyorMachine(tileX, tileY, direction);
    }

    public class ConveyorMachine extends Machine implements InputAdmission, OutputEmitter, Delting, Tickable, Batching {
        public ConveyorMachine(int tileX, int tileY, Direction direction) {
            super(tileX, tileY, direction, MachineType.CONVEYOR);
        }

        //TODO: add arraylist to each conveyor to allow multiple items on a single conveyor
        // with entrance accept and exit pushing

        //// Better TODO: merge long conveyor to segment so every belt doesn't require individual update
        @Override
        public void update(float delta) {
            if (this.item == null) return;

            if (RenderManager.SMOOTH_ANI) {
                float smoothAlpha = 1f - (float) Math.exp((-RenderManager.VISUAL_SMOOTHNESS) * delta);

                updateItemSmoothVisual(smoothAlpha);
            } else {
                updateItemVisual(delta);
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
            if (item == null) return;

            float size = type.getSize();

            item.progress = Math.min(
                1f,
                item.progress + speed * tickDelta
            );

            float centerX = tileX + size * 0.5f;
            float centerY = tileY + size * 0.5f;

            float inputX = centerX - direction.dx * size * 0.5f;
            float inputY = centerY - direction.dy * size * 0.5f;

            item.currentX = inputX + direction.dx * item.progress;
            item.currentY = inputY + direction.dy * item.progress;

        }

        //TODO: later make this default method in Machine
        // to update visual of item/machine animation, etc...
        @Override
        public void updateItemVisual(float delta) {
            float dx = this.item.currentX - this.item.visualX;
            float dy = this.item.currentY - this.item.visualY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance == 0f) return;

            float maxMove = speed * delta;

            if (distance <= maxMove) {
                this.item.visualX = this.item.currentX;
                this.item.visualY = this.item.currentY;
                return;
            }

            this.item.visualX += dx / distance * maxMove;
            this.item.visualY += dy / distance * maxMove;
        }

        @Override
        public void updateItemSmoothVisual(float visualSmoothness) {
            this.item.visualX = MathUtils.lerp(this.item.visualX, this.item.currentX, visualSmoothness);
            this.item.visualY = MathUtils.lerp(this.item.visualY, this.item.currentY, visualSmoothness);
        }

        @Override
        public float getInputSpeed() {
            return speed;
        }

        @Override
        public float getOutputSpeed() {
            return speed;
        }

        @Override
        public boolean shouldStayActive() {
            return this.item != null;
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
        public boolean canAcceptLoad(Machine source, Item load) {
            return super.canAcceptLoad(source, load)
                && passesInputRules(source, load);
        }

        @Override
        public boolean canAcceptLoad(Item load) {
            return item == null && load != null;
        }

        private boolean passesInputRules(Machine source, Item load) {
            if (source == null || load == null) return false;

            int dx = source.tileX - tileX;
            int dy = source.tileY - tileY;

            return Math.abs(dx) + Math.abs(dy) == 1
                && (dx != direction.dx || dy != direction.dy);
        }

        @Override
        public boolean canReserve(Machine source, Item load, int reservedIncoming, boolean outgoingSelected) {
            if (reservedIncoming != 0 || !passesInputRules(source, load)) {
                return false;
            }

            return item == null || (outgoingSelected && canPushLoad());
        }

    }

    public float getSpeed() {
        return speed;
    }


    @Override
    public String getName() {
        return "Conveyor Belt";
    }

}
