package core.machine.transport;

import core.machine.Machine;
import core.machine.utils.Item;

public interface InputAdmission {
    boolean canReserve(Machine source, Item load,
                       int reservedIncoming, boolean outgoingSelected);
}
