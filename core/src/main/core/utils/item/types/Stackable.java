package core.utils.item.types;

public interface Stackable {
    default int getMaxStack() {
        return 64;
    }

    default int getMinStack() {
        return 0;
    }

    int getCurrentStack();
    void setCurrentStack(int amount);

    default boolean add(int amount) {
        long next = (long) getCurrentStack() + amount;

        if (next < getMinStack() || next > getMaxStack()) {
            return false;
        }

        setCurrentStack((int) next);
        return true;
    }

}
