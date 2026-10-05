package core.system.systems;

import arcane.*;
import arcane.utils.TimeUtils;
import com.badlogic.gdx.utils.*;
import core.blocks.*;
import core.event.*;
import core.machine.*;
import core.blocks.definition.Block;
import core.machine.render.ItemRen;
import core.machine.state.*;
import core.assets.textures.BuildingType;

public class FactorySystem implements ApplicationListener {

    private final MachineGroup machineGroup;
    private final ItemRen renderManager = new ItemRen();

    private final LongMap<Machine> buildings = new LongMap<>();

    //TODO: later use a better extends system see mindustry/world/Block.java
    // should be like Conveyor extends Block

    //TODO: check mindustry/Senseable interface class
    // which allow an object implement this interface to expose it's information to game

    public FactorySystem() {

        this.machineGroup = new MachineGroup(this::getBuildingAt, this::reportTransfer);

        Events.on(GameEvent.BlockPlaced.class, event -> {
            if (!(event.type() instanceof BuildingType buildingType)) return;

            Block definition = Blocks.get(buildingType);
            if (definition == null) return;

            Machine building = definition.init(
                event.tileX(),
                event.tileY(),
                event.direction()
            );

            buildings.put(tileKey(event.tileX(), event.tileY()), building);
            machineGroup.register(building);

//            System.out.println("FactorySystem: Added building at (" + event.tileX + ", " + event.tileY + ") of type " + machineType);

            if (getBuildingCount() % 1000 == 0) {
                System.out.println("Building count: " + getBuildingCount());
                System.out.println("Built: " + getBuildingAt(event.tileX(), event.tileY()).type);
            }
            //            System.out.println("Direction: " + getBuildingDirectionAt(event.tileX, event.tileY));
        });

        Events.on(GameEvent.BlockRemoved.class, event -> {
            //TODO: implement Block class for general logic on class that extends Block
            // for it's dedicated removal logic
            // the cases in switch should be auto detected
            if (!(event.type() instanceof BuildingType)) return;

            Machine removed = buildings.remove(tileKey(event.tileX(), event.tileY()));
            if (removed == null) return;
            machineGroup.unregister(removed);
            removed.onDestroyed();
            // Decide later whether held items should disappear or drop.
            removed.item = null;
        });

        //TODO: later migrate this to a loader system
        Blocks.load();


        Events.run(Events.Trigger.tickUpdate, () ->
            TimeUtils.measureAndPrint(
                "TickUpdate: ",
                Time.UPDATE_INTERVAL, 1f,
                this::tickUpdate
            )
        );

    }

    @Override
    public void update() {
        float delta = Time.delta();

        TimeUtils.measureAndPrint("BatchUpdate: ",
            delta, 1f,
                this::drawBatch

        );

        TimeUtils.measureAndPrint(
            delta,
            () -> machineGroup.update(delta)
        );

        renderManager.update();

    }

    private void tickUpdate() {
            machineGroup.tickUpdate();

            machineGroup.removeInactive();
    }

    private void drawBatch() {

        machineGroup.drawBatch();

    }

    public static long tileKey(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }

    public Machine getBuildingAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY));
    }

    public void reportTransfer(Machine current, Machine target, ItemType itemType, float fromX, float fromY, float speed) {
        if (!target.hasStorage()) return;

        float half = target.type.getSize() * 0.5f;
        renderManager.addIntake(
            itemType, fromX, fromY,
            target.tileX + half, target.tileY + half, speed
        );
    }

    public Direction getBuildingDirectionAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY)).direction;
    }

    public int getBuildingCount() {
        return buildings.size;
    }

}
