package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import core.app.GameContext;
import core.event.Events;
import core.event.GameEvent;
import core.machine.Conveyor;
import core.machine.Machine;
import core.machine.ItemManager;
import core.machine.ItemRender;
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

    private final LongMap<Machine> buildings = new LongMap<>();
    private final LongMap<Machine> pendingConveyorTransfers = new LongMap<>();

    //TODO: later migrate this to each machine behavior system for optimizing
    private final Array<Machine> conveyors = new Array<>();
    private final Array<Machine> sources = new Array<>();

    private static final float SOURCE_INTERVAL_SECONDS = 0.1f;
    private static final float BELT_TILES_PER_SECOND = 10f;

    private static final float VISUAL_CATCHUP_MULTIPLIER = 1.25f;

    private static final boolean SMOOTH_CONVEYORS = true;
    private static final float VISUAL_SMOOTHNESS = 20f;


    //debug update time measure
    private boolean measureUpdate = false;
    private long visualUpdateNanos;
    private int visualUpdateSamples;
    private float performanceTimer;

    public FactorySystem(GameContext context) {
        this.context = context;
        this.world = context.world;

        Events.on(GameEvent.BlockPlaced.class, event -> {
            if (!(event.type instanceof BuildingType buildingType)) return;

            MachineType machineType = MachineType.from((buildingType));
            if (machineType == null) return;

            Machine building = new Machine(
                event.tileX,
                event.tileY,
                machineType,
                event.direction
            );

            buildings.put(tileKey(event.tileX, event.tileY), building);

            switch (building.type) {
                case CONVEYOR -> conveyors.add(building);
                case CREATIVE_SOURCE -> sources.add(building);
            }

//            System.out.println("FactorySystem: Added building at (" + event.tileX + ", " + event.tileY + ") of type " + machineType);

            if (getBuildingCount() % 1000 == 0) {
                System.out.println("Building count: " + getBuildingCount());
                System.out.println("Built: " + getBuildingAt(event.tileX, event.tileY).type);
            }
            //            System.out.println("Direction: " + getBuildingDirectionAt(event.tileX, event.tileY));
        });

        itemRender = new ItemRender(world);
        ItemManager itemManager = new ItemManager(itemRender);

        factoryContext = new FactoryContext(
            itemManager,
            itemRender
        );
    }

    @Override
    public void update(float delta) {
        long start = System.nanoTime();


        updateConveyorVisual(delta);



        if (measureUpdate) {
            updateTimeMeasure(delta, start);
        }
    }

    @Override
    public void tickUpdate(float tickDelta) {
        updateSource(tickDelta);
        updateConveyor(tickDelta);
        transferConveyorOutputs();
        transferSourceOutputs();




    }

    @Override
    public void drawBatch(SpriteBatch batch) {

        for (Machine conveyor : conveyors) {
            if (conveyor.item == null) {
                continue;
            }
            //TODO: only draw items in visible chunk
            itemRender.drawBatch(
                batch,
                conveyor.item,
                context.assets
            );
        }
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

    private void updateConveyorVisual(float delta) {
        if (SMOOTH_CONVEYORS) {
            float smoothAlpha =
                1f - (float) Math.exp(-VISUAL_SMOOTHNESS * delta);

            Conveyor.updateSmoothVisuals(conveyors, smoothAlpha);
        } else {
            float visualSpeed = BELT_TILES_PER_SECOND * VISUAL_CATCHUP_MULTIPLIER;
            Conveyor.updateVisuals(conveyors, delta, visualSpeed);
        }
    }

    private void updateConveyor(float tickDelta) {
        for (Machine conveyor : conveyors) {
            if (conveyor.item == null) continue;
            Conveyor.advancedLogical(conveyor, tickDelta, BELT_TILES_PER_SECOND);
        }
    }

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

            if (target.type != MachineType.CONVEYOR) {
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

            target.item = transferredItem;
            source.item = null;

//            System.out.println(
//                "Transferred " + transferredItem.type +
//                    " to conveyor at (" +
//                    target.tileX + ", " + target.tileY + ")"
//            );
        }
    }

    private void transferConveyorOutputs() {
        pendingConveyorTransfers.clear();

        //find valid transfers target
        for (Machine current : conveyors) {
            if (current.item == null || current.item.progress < 1f) {
                continue;
            }

            int targetX = current.tileX + current.direction.dx;
            int targetY = current.tileY + current.direction.dy;

            Machine target = getBuildingAt(targetX, targetY);
            if (target == null) {
                continue;
            }

            boolean targetIsConveyor = target.type == MachineType.CONVEYOR;

            if (targetIsConveyor) {
                if (target.item != null) {
                    continue;
                }
                // basically straight conveyor transfer only
//                if (target.direction != current.direction) {
//                    continue;
//                }
            } else {
                if (!target.acceptItem(current.item.type)) {
                    continue;
                }
            }


            long targetKey = tileKey(targetX, targetY);
            Machine currentPending = pendingConveyorTransfers.get(targetKey);
            // Deterministic winner if two outputs target one conveyor.
            if (currentPending == null
                || Long.compare(
                    tileKey(current.tileX, current.tileY),
                    tileKey(currentPending.tileX, currentPending.tileY)
                ) < 0) {
                pendingConveyorTransfers.put(targetKey, current);
            }
        }

        //apply accepted transfers
        for (LongMap.Entry<Machine> entry : pendingConveyorTransfers.entries()) {
            Machine current = entry.value;
            Machine target = buildings.get(entry.key);

            if (current.item == null || target == null) {
                continue;
            }

            //handle item to target that has storage and isn't conveyor
            if (target.type != MachineType.CONVEYOR) {
                Item receivedItem = current.item;
                if (target.handleItem(receivedItem.type)) {
                    current.item = null;

//                    System.out.println(
//                        target.type + " at (" +
//                            target.tileX + ", " + target.tileY +
//                            ") received " + receivedItem.type +
//                            ". Total: " +
//                            target.storage.getTotalItems() +
//                            "/" + target.storage.getCapacity()
//                    );
                }
                continue;
            }

            //from here onward is for targeting conveyor
            if (target.item != null) {
                continue;
            }

            Item transferredItem = current.item;

            float size = target.type.getSize();
            float targetCenterX = target.tileX + size * 0.5f;
            float targetCenterY = target.tileY + size * 0.5f;

            transferredItem.currentX =
                targetCenterX - target.direction.dx * 0.5f;
            transferredItem.currentY =
                targetCenterY - target.direction.dy * 0.5f;
            transferredItem.progress = 0f;
            //keep visualX/Y unchanged. Both conveyor edges share
            // the same position, so the visual motion stays seamless.
            target.item = transferredItem;
            current.item = null;

//            System.out.println(
//                "Conveyor transferred " + transferredItem.type +
//                    " from (" + current.tileX + ", " + current.tileY + ")" +
//                    " to (" + target.tileX + ", " + target.tileY + ")"
//            );
        }
    }

    private static long tileKey(int x, int y) {
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
