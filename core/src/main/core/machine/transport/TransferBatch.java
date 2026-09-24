package core.machine.transport;

import com.badlogic.gdx.utils.Array;
import core.machine.Machine;
import core.machine.utils.Item;

import java.util.Arrays;
import java.util.Comparator;

public final class TransferBatch {
    private final Array<Offer> slots = new Array<>(false, 128, Offer[]::new);
    private int size;

    public void add(Machine source, Machine target, Item item) {
        if (source == null || target == null || item == null) return;

        if (size == slots.size) slots.add(new Offer());
        Offer offer = slots.get(size++);

        offer.selected = false;
        offer.source = source;
        offer.target = target;
        offer.item = item;
        offer.fromX = item.visualX;
        offer.fromY = item.visualY;
        offer.speed = source.getOutputSpeed() * source.timeEfficient;
    }

    void sortActive(Comparator<Offer> order) {
        Arrays.sort(slots.items, 0, size, order);
    }

    public int size() {
        return size;
    }

    Offer get(int index) {
        return slots.get(index);
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            Offer offer = slots.get(i);
            offer.source = null;
            offer.target = null;
            offer.item = null;
            offer.selected = false;
        }
        size = 0;
    }

    static final class Offer {
        Machine source, target;
        Item item;
        float fromX, fromY, speed;
        boolean selected;
    }
}
