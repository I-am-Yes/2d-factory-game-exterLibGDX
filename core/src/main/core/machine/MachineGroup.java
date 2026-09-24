package core.machine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import core.machine.category.Batching;
import core.machine.category.Delting;
import core.machine.category.Tickable;
import core.machine.state.ItemType;
import core.machine.transport.OutputEmitter;
import core.machine.transport.TransferBatch;
import core.machine.transport.TransferResolver;

public final class MachineGroup {

    private final Array<Machine> activeMachines = new Array<>(false, 256);
    private final MachineLookup machineLookup;
    private final TransferListener transferListener;

    private final TransferBatch transferBatch = new TransferBatch();
    private final TransferResolver transferResolver = new TransferResolver();

    public MachineGroup(MachineLookup machineLookup, TransferListener transferListener) {
        this.machineLookup = machineLookup;
        this.transferListener = transferListener;
    }

    void reportTransfer(Machine current, Machine target, ItemType itemType, float fromX, float fromY, float speed) {
        transferListener.accepted(current, target, itemType, fromX, fromY, speed);
    }

    private void runTransfers(int updateCount) {
        collectOutputs(updateCount);
        transferResolver.resolve(transferBatch);
        transferResolver.commit(transferBatch, this::reportTransfer);
    }

    private void collectOutputs(int updateCount) {
        transferBatch.clear();

        for (int i = 0; i < updateCount; i++) {
            Machine machine = activeMachines.get(i);
            if (machine instanceof OutputEmitter emitter) {
                emitter.collectOutput(transferBatch);
            }
        }
    }

    @FunctionalInterface
    public interface TransferListener {
        void accepted(Machine current, Machine target, ItemType itemType,
                      float fromX, float fromY, float speed);
    }

    @FunctionalInterface
    public interface MachineLookup {
        Machine get(int tileX, int tileY);
    }

    Machine getMachineAt(int tileX, int tileY) {
        return machineLookup.get(tileX, tileY);
    }

    public void update(float delta) {
        int updateCount = activeMachines.size;

        for (int i = 0; i < updateCount; i++) {
            Machine machine = activeMachines.get(i);

            if (machine instanceof Delting delting) {
                delting.update(delta);
            }
        }
    }

    public void tickUpdate(float tickDelta) {
        // Machines awakened during this tick start next tick.
        int updateCount = activeMachines.size;

        for (int i = 0; i < updateCount; i++) {
            Machine machine = activeMachines.get(i);

            if (machine instanceof Tickable tickable) {
                tickable.tickUpdate(tickDelta);
            }
        }
        runTransfers(updateCount);
    }

    public void drawBatch(SpriteBatch batch) {
        for (Machine machine : activeMachines) {

            if (machine instanceof Batching batching) {
                batching.drawBatch(batch);
            }
        }
    }


    public void register(Machine machine) {
        machine.attachTo(this);
        if (machine.shouldStayActive()) {
            activate(machine);
        }
    }

    public void unregister(Machine machine) {
        deactivate(machine);
        machine.detachFrom(this);
    }

    void activate(Machine machine) {
        if (machine.active) return;

        machine.active = true;
        machine.activeIndex = activeMachines.size;
        activeMachines.add(machine);
    }

    private void deactivate(Machine machine) {
        if (!machine.active) return;

        int index = machine.activeIndex;
        Machine swapped = activeMachines.peek();

        activeMachines.removeIndex(index);

        if (swapped != machine) {
            swapped.activeIndex = index;
        }
        machine.active = false;
        machine.activeIndex = -1;
    }

    public void removeInactive() {
        for (int i = activeMachines.size - 1; i >= 0; i--) {
            Machine machine = activeMachines.get(i);

            if (!machine.shouldStayActive()) {
                deactivate(machine);
            }
        }
    }

}
