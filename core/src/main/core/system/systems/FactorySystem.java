package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import core.app.GameContext;
import core.blocks.Blocks;
import core.event.Events;
import core.event.GameEvent;
import core.machine.*;
import core.machine.state.Direction;
import core.machine.state.MachineType;
import core.machine.state.ItemType;
import core.machine.utils.Item;
import core.render.RenderLayer;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.FactoryContext;
import core.world.World;
import data.map.asset.BuildingType;

public class FactorySystem implements GameSysCycle, ContextProvider<FactoryContext> {

    private final GameContext context;
    private FactoryContext factoryContext;

    private final World world;
    private final ItemRender itemRender;
    private final MachineGroup machineGroup;

    private final LongMap<Machine> buildings = new LongMap<>();
    private final Conveyor.TransferBatch conveyorTransfers = new Conveyor.TransferBatch();

    //TODO: later migrate to a loader system
    private final ObjectMap<BuildingType, Block> machineDefinitions = new ObjectMap<>();

    //TODO: later migrate this to each machine behavior system for optimizing
    private final Array<Machine> sources = new Array<>();
    private final Array<Machine> conveyors = new Array<>();

    private static final float SOURCE_INTERVAL_SECONDS = 0.1f;

    private static final float VISUAL_CATCHUP_MULTIPLIER = 1.25f;



    //debug update time measure
    private boolean measureUpdate = false;
    private long visualUpdateNanos;
    private int visualUpdateSamples;
    private float performanceTimer;

    //TODO: later use a better extends system see mindustry/world/Block.java
    // should be like Conveyor extends Block

    //TODO: check mindustry/Senseable interface class
    // which allow an object implement this interface to expose it's information to game

    public FactorySystem(GameContext context) {
        this.context = context;
        this.world = context.world;

        this.machineGroup = new MachineGroup();

        Events.on(GameEvent.BlockPlaced.class, event -> {
            if (!(event.type instanceof BuildingType buildingType)) return;

            Block definition = machineDefinitions.get(buildingType);
            if (definition == null) return;



            Machine building = definition.createMachine(
                event.tileX,
                event.tileY,
                event.direction
            );

            buildings.put(tileKey(event.tileX, event.tileY), building);
            machineGroup.register(building);

            switch (building.type) {
                case CONVEYOR ->  conveyors.add(building);
                case CREATIVE_SOURCE -> sources.add(building);
            }

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

            switch (removed.type) {
                case CONVEYOR -> conveyors.removeValue(removed, true);
                case CREATIVE_SOURCE -> sources.removeValue(removed, true);
            }
            // Decide later whether held items should disappear or drop.
            removed.item = null;
            // Prevent a pending transfer from using the removed machine.
        });

        //TODO: later migrate this to a loader system
        Blocks.load();
        registerMachines();

        itemRender = new ItemRender(world);
        ItemManager itemManager = new ItemManager(itemRender);

        factoryContext = new FactoryContext(
            itemManager,
            itemRender
        );
    }

    private void registerMachines() {
        machineDefinitions.put(BuildingType.CONVEYOR_BELT, Blocks.normalConveyor);
        machineDefinitions.put(BuildingType.CONVEYOR_BELT_2, Blocks.fastConveyor);
        machineDefinitions.put(BuildingType.CREATIVE_SOURCE, Blocks.creativeSource);
    }

    @Override
    public void update(float delta) {
        long start = System.nanoTime();

        machineGroup.update(delta);

        if (measureUpdate) {
            updateTimeMeasure(delta, start);
        }
    }

    @Override
    public void tickUpdate(float tickDelta) {
        updateSource(tickDelta);

        machineGroup.tickUpdate(tickDelta);

        transferConveyorOutputs();
        transferSourceOutputs();

        machineGroup.removeInactive();
    }

    @Override
    public void drawBatch(SpriteBatch batch) {

        machineGroup.drawBatch(batch);
    }

    //TODO: regist machine
    private void registMachine() {

    }

    private void updateTimeMeasure(float delta, long start) {
        visualUpdateNanos += System.nanoTime() - start;
        visualUpdateSamples++;
        performanceTimer += delta;

        if (performanceTimer >= 5f) {
            double averageMilliseconds =
                visualUpdateNanos /
                    1_000_000.0 /
                    visualUpdateSamples;

            System.out.printf(
                "Visual update average: %.4f ms%n",
                averageMilliseconds
            );

            visualUpdateNanos = 0L;
            visualUpdateSamples = 0;
            performanceTimer -= 5f;
        }
    }

    ////testing purpose
//    private void updateConveyorVisual(float delta) {
//        for (Machine conveyor : conveyors) {
//            if (conveyor.item == null) {
//                continue;
//            }
//            conveyor.item.visualX =
//                conveyor.item.currentX;
//
//            conveyor.item.visualY =
//                conveyor.item.currentY;
//        }
//    }

    private void updateSource(float delta) {
        for (Machine source : sources) {
            if (source.type != MachineType.CREATIVE_SOURCE) continue;
            //if this source already contains an item, skip item creation for now.
            if (source.item != null) continue;

            source.productionTimer += delta;

            if (source.productionTimer < SOURCE_INTERVAL_SECONDS) continue;

            source.productionTimer -= SOURCE_INTERVAL_SECONDS;

            Item newItem = new Item(ItemType.TEST_ITEM);

            float centerX = source.tileX + 0.5f;
            float centerY = source.tileY + 0.5f;

            newItem.currentX = centerX;
            newItem.currentY = centerY;
            newItem.visualX = centerX;
            newItem.visualY = centerY;
            newItem.progress = 0f;

            source.item = newItem;

//            System.out.println(
//                "Source produced " + newItem.type +
//                    " at (" + building.tileX + ", " + building.tileY + ")"
//            );

        }
    }

    private void transferSourceOutputs() {
        for (Machine source : sources) {
            if (source.type != MachineType.CREATIVE_SOURCE) {
                continue;
            }

            if (source.item == null) {
                continue;
            }

            int targetX = source.tileX + source.direction.dx;
            int targetY = source.tileY + source.direction.dy;

            Machine target = getBuildingAt(targetX, targetY);

            if (target == null) {
                continue;
            }

            //TODO: the item transfer from source to target should be
            // item appearing from source's output gate to current target's input gate
            if (target.type != MachineType.CONVEYOR && target.item != null) {
                continue;
            }

//            if (target.direction != source.direction) {
//                continue;
//            }

            if (target.item != null) {
                continue;
            }

            Item transferredItem = source.item;

            float size = target.type.getSize();
            float targetCenterX = target.tileX + size * 0.5f;
            float targetCenterY = target.tileY + size * 0.5f;

            transferredItem.currentX =
                targetCenterX - target.direction.dx * 0.5f;

            transferredItem.currentY =
                targetCenterY - target.direction.dy * 0.5f;

            transferredItem.visualX = transferredItem.currentX;
            transferredItem.visualY = transferredItem.currentY;

            transferredItem.progress = 0f;

            Item outgoing = source.item;
            if (target.acceptLoad(outgoing)) {
                source.item = null;
            }

//            System.out.println(
//                "Transferred " + transferredItem.type +
//                    " to conveyor at (" +
//                    target.tileX + ", " + target.tileY + ")"
//            );
        }
    }

    private void transferConveyorOutputs() {
        conveyorTransfers.begin();

        for (Machine machine : conveyors) {
            if (machine instanceof
                Conveyor.ConveyorMachine conveyor) {

                conveyor.collectTransfer(
                    this::getBuildingAt,
                    conveyorTransfers
                );
            }
        }

        conveyorTransfers.apply(buildings::get);
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
