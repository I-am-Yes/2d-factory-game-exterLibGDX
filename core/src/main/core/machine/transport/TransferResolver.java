package core.machine.transport;

import com.badlogic.gdx.utils.Array;
import core.machine.Machine;
import core.machine.state.ItemType;

import java.util.IdentityHashMap;

public class TransferResolver {
    private final IdentityHashMap<Machine, TransferBatch.Offer> offerBySource = new IdentityHashMap<>();
    private final Array<TransferBatch.Offer> vacancyQueue = new Array<>(false, 128);
    private final Array<TransferBatch.Offer> cyclePath = new Array<>(false, 128);

    public TransferResolver() {}

    @FunctionalInterface
    public interface TransferObserver {
        void accepted(Machine source, Machine target, ItemType type,
                      float fromX, float fromY, float speed);
    }

    public void resolve(TransferBatch transferBatch) {
        if (transferBatch == null) return;

        selectImmediate(transferBatch);
        propagateVacancies(transferBatch);
        indexOffers(transferBatch);
        selectClosedCycles(transferBatch);
    }

    public void commit(TransferBatch transferBatch, TransferObserver observer) {
        if (transferBatch == null) return;

        for (int i = 0; i < transferBatch.size(); i++) {
            TransferBatch.Offer offer = transferBatch.get(i);
            if (!offer.selected) continue;

            if (offer.source.extractOutputLoad(offer.item) != offer.item) {
                throw new IllegalStateException("Selected output changed before commit");
            }
        }

        for (int i = 0; i < transferBatch.size(); i++) {
            TransferBatch.Offer offer = transferBatch.get(i);
            if (!offer.selected) continue;

            if (!offer.target.acceptLoad(offer.source, offer.item)) {
                throw new IllegalStateException("Selected input rejected during commit");
            }
        }

        if (observer != null) {
            for (int i = 0; i < transferBatch.size(); i++) {
                TransferBatch.Offer offer = transferBatch.get(i);
                if (offer.selected) {
                    observer.accepted(offer.source, offer.target, offer.item.type,
                        offer.fromX, offer.fromY, offer.speed);
                }
            }
        }
    }

    private int traceCycle(TransferBatch.Offer start) {
        cyclePath.clear();

        TransferBatch.Offer cursor = start;
        while (cursor != null
            && !cursor.selected
            && isLive(cursor)
            && cursor.visitState != 2) {

            if (cursor.visitState == 1) {
                return cursor.pathIndex;
            }

            cursor.visitState = 1;
            cursor.pathIndex = cyclePath.size;
            cyclePath.add(cursor);
            cursor = offerBySource.get(cursor.target);
        }

        return -1;
    }

    private void selectClosedCycles(TransferBatch transferBatch) {
        for (int i = 0; i < transferBatch.size(); i++) {
            TransferBatch.Offer start = transferBatch.get(i);
            if (start.selected || !isLive(start) || start.visitState != 0) {
                continue;
            }

            int cycleStart = traceCycle(start);
            if (canSelectTracedCycle(transferBatch, cycleStart)) {
                for (int j = cycleStart; j < cyclePath.size; j++) {
                    cyclePath.get(j).selected = true;
                }
            }

            for (int j = 0; j < cyclePath.size; j++) {
                cyclePath.get(j).visitState = 2;
            }
        }
    }

    private boolean canSelectTracedCycle(TransferBatch batch, int cycleStart) {
        if (cycleStart < 0) return false;

        for (int i = cycleStart; i < cyclePath.size; i++) {
            TransferBatch.Offer offer = cyclePath.get(i);
            if (!(offer.target instanceof InputAdmission input)) return false;

            int reservedIncoming = 0;
            for (int j = firstOfferForTarget(batch, offer.target); j < batch.size(); j++) {
                TransferBatch.Offer incoming = batch.get(j);
                if (incoming.target != offer.target) break;
                if (incoming.selected) reservedIncoming++;
            }

            if (!input.canReserve(
                offer.source, offer.item, reservedIncoming, true
            )) return false;
        }

        return true;
    }

    private void indexOffers(TransferBatch transferBatch) {
        offerBySource.clear();

        for (int i = 0; i < transferBatch.size(); i++) {
            TransferBatch.Offer offer = transferBatch.get(i);
            if (isLive(offer)) {
                offerBySource.put(offer.source, offer);
            }
        }
    }

    private void propagateVacancies(TransferBatch batch) {
        vacancyQueue.clear();

        for (int i = 0; i < batch.size(); i++) {
            TransferBatch.Offer offer = batch.get(i);
            if (offer.selected) vacancyQueue.add(offer);
        }

        for (int i = 0; i < vacancyQueue.size; i++) {
            TransferBatch.Offer incoming =
                selectIncomingToVacating(batch, vacancyQueue.get(i).source);

            if (incoming != null) vacancyQueue.add(incoming);
        }
    }

    static boolean isLive(TransferBatch.Offer offer) {
        return offer != null
            && offer.source != null
            && offer.target != null
            && offer.item != null
            && offer.source.item == offer.item
            && offer.source.canPushLoad();
    }

    static int comparePriority(TransferBatch.Offer a, TransferBatch.Offer b) {
        int c = Integer.compare(a.target.tileX, b.target.tileX);
        if (c != 0) return c;
        c = Integer.compare(a.target.tileY, b.target.tileY);
        if (c != 0) return c;
        c = Integer.compare(a.source.tileX, b.source.tileX);
        if (c != 0) return c;
        return Integer.compare(a.source.tileY, b.source.tileY);
    }

    static void selectImmediate(TransferBatch batch) {
        batch.sortActive(TransferResolver::comparePriority);

        Machine currentTarget = null;
        int reservedIncoming = 0;

        for (int i = 0; i < batch.size(); i++) {
            TransferBatch.Offer offer = batch.get(i);

            if (offer.target != currentTarget) {
                currentTarget = offer.target;
                reservedIncoming = 0;
            }

            if (!isLive(offer)
                || !(offer.target instanceof InputAdmission input)
                || !input.canReserve(
                offer.source, offer.item, reservedIncoming, false)) {
                continue;
            }

            offer.selected = true;
            reservedIncoming++;
        }
    }

    private static int firstOfferForTarget(TransferBatch transferBatch, Machine target) {
        int low = 0;
        int high = transferBatch.size();

        while (low < high) {
            int mid = (low + high) >>> 1;
            TransferBatch.Offer offer = transferBatch.get(mid);

            int comparison = Integer.compare(offer.target.tileX, target.tileX);
            if (comparison == 0) {
                comparison = Integer.compare(offer.target.tileY, target.tileY);
            }

            if (comparison < 0) low = mid + 1;
            else high = mid;
        }

        return low;
    }

    private static TransferBatch.Offer selectIncomingToVacating(TransferBatch transferBatch, Machine vacating) {
        if (!(vacating instanceof InputAdmission input)) return null;

        int first = firstOfferForTarget(transferBatch, vacating);
        int reservedIncoming = 0;

        for (int i = first; i < transferBatch.size(); i++) {
            TransferBatch.Offer offer = transferBatch.get(i);
            if (offer.target != vacating) break;
            if (offer.selected) reservedIncoming++;
        }

        for (int i = first; i < transferBatch.size(); i++) {
            TransferBatch.Offer offer = transferBatch.get(i);
            if (offer.target != vacating) break;

            if (!offer.selected
                && isLive(offer)
                && input.canReserve(offer.source, offer.item, reservedIncoming, true)) {
                offer.selected = true;
                return offer;
            }
        }

        return null;
    }
}
