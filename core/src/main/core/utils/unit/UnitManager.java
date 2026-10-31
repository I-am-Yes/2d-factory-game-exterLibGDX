package core.utils.unit;

import arcane.*;
import arcane.utils.*;
import core.utils.unit.units.*;

import static core.app.Vars.*;

public class UnitManager {
    private final Array<Unit> units = new Array<>(false, 1024);

    public UnitManager() {
        Events.run(Events.Trigger.tickUpdate, () -> {

            updateUnits();

        });
    }

    public void updateUnits() {
        if (units.contains(player, true)) {
            //TODO: fix player camera with rendering issue
            player.update();
        }
    }

    public PlayerUnit spawnPlayer(float x, float y) {
        return (PlayerUnit) spawnUnit(new PlayerUnit(), x, y, 0f);
    }

    public Unit spawnUnit(float x, float y, float direction) {
        return spawnUnit(null, x, y, direction);
    }

    public Unit spawnUnit(Unit unit, float x, float y, float direction) {
        if (unit == null) unit = new Unit();
        unit.setPos(x, y);
        unit.setDirection(direction);

        addUnit(unit);
        return unit;
    }

    public void addUnit(Unit unit) {
        if (unit != null && !units.contains(unit, true)) {
            units.add(unit);
        }
    }

    public Unit getUnit(Unit unit) {
        return units.contains(unit, true) ? unit : null;
    }

    public void removeUnit(Unit unit) {
        units.removeValue(unit, true);
    }

    public Array<Unit> getUnits() {
        return units;
    }

}
