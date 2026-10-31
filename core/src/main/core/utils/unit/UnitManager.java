package core.utils.unit;

import arcane.utils.Array;

import static core.app.Vars.*;

public class UnitManager {
    public static UnitManager botManager;

    private float tileSize = world.getTileSize();

    private Array<Unit> units = new Array<>(false, 1024);

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

    public void unitAimTo(Unit unit) {

    }

    public void addUnit(Unit unit) {
        if (unit != null && !units.contains(unit, true)) {
            units.add(unit);
        }
    }

    public Unit getUnit(Unit unit) {
        return units.contains(unit, true) ? unit : null;
    }

    public Array<Unit> getUnits() {
        return units;
    }

    public void removeUnit(Unit unit) {
        units.removeValue(unit, true);
    }

}
