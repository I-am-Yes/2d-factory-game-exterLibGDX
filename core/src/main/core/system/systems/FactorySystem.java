package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.LongMap;
import core.app.GameContext;
import core.blocks.Blocks;
import core.event.Events;
import core.event.GameEvent;
import core.machine.*;
import core.machine.state.Direction;
import core.render.RenderLayer;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.FactoryContext;
import core.utils.TimeUtils;
import core.world.World;
import data.map.asset.BuildingType;

public class FactorySystem implements GameSysCycle, ContextProvider<FactoryContext> {

    private final GameContext context;
    private FactoryContext factoryContext;

    private final World world;
    private final ItemRender itemRender;
    private final MachineGroup machineGroup;

    private final LongMap<Machine> buildings = new LongMap<>();

    //TODO: later use a better extends system see mindustry/world/Block.java
    // should be like Conveyor extends Block

    //TODO: check mindustry/Senseable interface class
    // which allow an object implement this interface to expose it's information to game

    public FactorySystem(GameContext context) {
        this.context = context;
        this.world = context.world;

        this.machineGroup = new MachineGroup(this::getBuildingAt);

        Events.on(GameEvent.BlockPlaced.class, event -> {
            if (!(event.type instanceof BuildingType buildingType)) return;

            Block definition = Blocks.get(buildingType);
            if (definition == null) return;

            Machine building = definition.createBlock(
                event.tileX,
                event.tileY,
                event.direction
            );

            buildings.put(tileKey(event.tileX, event.tileY), building);
            machineGroup.register(building);

//            System.out.println("FactorySystem: Added building at (" + event.tileX + ", " + event.tileY + ") of type " + machineType);

            if (getBuildingCount() % 1000 == 0) {
                System.out.println("Building count: " + getBuildingCount());
                System.out.println("Built: " + getBuildingAt(event.tileX, event.tileY).type);
            }
            //            System.out.println("Direction: " + getBuildingDirectionAt(event.tileX, event.tileY));
        });

        Events.on(GameEvent.BlockRemoved.class, event -> {
            //TODO: implement Block class for general logic on class that extends Block
            // for it's dedicated removal logic
            // the cases in switch should be auto detected
            if (!(event.type instanceof BuildingType)) return;

            Machine removed = buildings.remove(tileKey(event.tileX, event.tileY));
            if (removed == null) return;
            machineGroup.unregister(removed);
            removed.onDestroyed();
            // Decide later whether held items should disappear or drop.
            removed.item = null;
        });

        //TODO: later migrate this to a loader system
        Blocks.load();

        itemRender = new ItemRender(world);
        ItemManager itemManager = new ItemManager(itemRender);

        factoryContext = new FactoryContext(
            itemManager,
            itemRender
        );
    }

    @Override
    public void update(float delta) {

        TimeUtils.measureAndPrint(
            delta,
            () -> machineGroup.update(delta)
        );

    }

    @Override
    public void tickUpdate(float tickDelta) {
        machineGroup.tickUpdate(tickDelta);

        machineGroup.removeInactive();
    }

    @Override
    public void drawBatch(SpriteBatch batch) {

        machineGroup.drawBatch(batch);
    }

    public static long tileKey(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }

    public Machine getBuildingAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY));
    }

    public Direction getBuildingDirectionAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY)).direction;
    }

    public int getBuildingCount() {
        return buildings.size;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.OBJECT_LAYER_2;
    }

    @Override
    public FactoryContext getContext() {
        return factoryContext;
    }
}
