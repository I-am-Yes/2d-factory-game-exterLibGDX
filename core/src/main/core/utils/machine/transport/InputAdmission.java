package core.utils.machine.transport;

import core.utils.machine.Machine;
import core.utils.machine.utils.ItemConveyor;

public interface InputAdmission {
    boolean canReserve(Machine source, ItemConveyor load,
                       int reservedIncoming, boolean outgoingSelected);
}
