package core.machine.transport;

import com.badlogic.gdx.utils.Array;
import core.machine.Machine;
import core.machine.utils.Item;

import java.util.Arrays;
import java.util.Comparator;

public final class TransferBatch {
    private final Array<Offer> offerPool = new Array<>(false, 128, Offer[]::new);
    private int activeCount;

    public void add(Machine source, Machine target, Item item) {
        if (source == null || target == null || item == null) return;

        if (activeCount == offerPool.size) offerPool.add(new Offer());
        Offer offer = offerPool.get(activeCount++);

        offer.source = source;
        offer.target = target;
        offer.item = item;
        offer.fromX = item.visualX;
        offer.fromY = item.visualY;
        offer.speed = source.getOutputSpeed() * source.timeEfficient;
        offer.selected = false;
        offer.visitState = 0;
        offer.pathIndex = -1;
    }

    public void clear() {
        for (int i = 0; i < activeCount; i++) {
            Offer offer = offerPool.get(i);
            offer.source = null;
            offer.target = null;
            offer.item = null;
            offer.selected = false;
            offer.visitState = 0;
            offer.pathIndex = -1;
        }
        activeCount = 0;
    }

    void sortActive(Comparator<Offer> order) {
        Arrays.sort(offerPool.items, 0, activeCount, order);
    }

    public int size() {
        return activeCount;
    }

    Offer get(int index) {
        return offerPool.get(index);
    }

    static final class Offer {
        Machine source, target;
        Item item;
        float fromX, fromY, speed;
        boolean selected;
        int visitState; // 0: unseen, 1: current path, 2: examined
        int pathIndex;
    }
}
