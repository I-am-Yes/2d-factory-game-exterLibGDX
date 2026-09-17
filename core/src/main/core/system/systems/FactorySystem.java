package core.system.systems;

import com.badlogic.gdx.utils.LongMap;
import core.app.GameContext;
import core.event.Events;
import core.event.GameEvent;
import core.machine.FactoryBuilding;
import core.machine.ItemManager;
import core.machine.ItemRender;
import core.machine.state.Direction;
import core.machine.state.FactoryType;
import core.machine.state.ItemType;
import core.machine.utils.Item;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.FactoryContext;
import data.map.asset.BuildingType;

public class FactorySystem implements GameSysCycle, ContextProvider<FactoryContext> {

    private final GameContext context;
    private FactoryContext factoryContext;

    private final LongMap<FactoryBuilding> buildings = new LongMap<>();

    private static final float SOURCE_INTERVAL_SECONDS = 1f;
    private static final float BELT_TILES_PER_SECOND = 1f;

    public FactorySystem(GameContext context) {
        this.context = context;

        Events.on(GameEvent.BlockPlaced.class, event -> {
            if (!(event.type instanceof BuildingType buildingType)) return;

            FactoryType factoryType = FactoryType.from((buildingType));
            if (factoryType == null) return;

            FactoryBuilding building = new FactoryBuilding(
                event.tileX,
                event.tileY,
                factoryType,
                event.direction
            );

            buildings.put(tileKey(event.tileX, event.tileY), building);
            System.out.println("FactorySystem: Added building at (" + event.tileX + ", " + event.tileY + ") of type " + factoryType);
            System.out.println("Building count: " + getBuildingCount());
            System.out.println("Built: " + getBuildingAt(event.tileX, event.tileY).type);
            System.out.println("Direction: " + getBuildingDirectionAt(event.tileX, event.tileY));
        });

        ItemRender itemRender = new ItemRender();
        ItemManager itemManager = new ItemManager(itemRender);

        factoryContext = new FactoryContext(
            itemManager,
            itemRender
        );
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void tickUpdate(float delta) {
        updateSource(delta);
        updateConveyors(delta);
        transferSourceOutputs();
    }

    private void updateSource(float delta) {
        for (FactoryBuilding building : buildings.values()) {
            if (building.type != FactoryType.CREATIVE_SOURCE) continue;

            if (building.item != null) continue;

            building.productionTimer += delta;

            if (building.productionTimer < SOURCE_INTERVAL_SECONDS) continue;

            building.productionTimer -= SOURCE_INTERVAL_SECONDS;

            Item newItem = new Item(ItemType.TEST_ITEM);

            float centerX = building.tileX + 0.5f;
            float centerY = building.tileY + 0.5f;

            newItem.previousX = centerX;
            newItem.previousY = centerY;
            newItem.currentX = centerX;
            newItem.currentY = centerY;
            newItem.progress = 0f;

            building.item = newItem;

            System.out.println(
                "Source produced " + newItem.type +
                    " at (" + building.tileX + ", " + building.tileY + ")"
            );

        }
    }

    private void transferSourceOutputs() {
        for (FactoryBuilding source : buildings.values()) {
            if (source.type != FactoryType.CREATIVE_SOURCE) {
                continue;
            }

            if (source.item == null) {
                continue;
            }

            int targetX = source.tileX + source.direction.dx;
            int targetY = source.tileY + source.direction.dy;

            FactoryBuilding target = getBuildingAt(targetX, targetY);

            if (target == null) {
                continue;
            }

            if (target.type != FactoryType.CONVEYOR) {
                continue;
            }

            if (target.item != null) {
                continue;
            }

            Item transferredItem = source.item;

            float targetCenterX = target.tileX + 0.5f;
            float targetCenterY = target.tileY + 0.5f;

            transferredItem.previousX = source.tileX + 0.5f;
            transferredItem.previousY = source.tileY + 0.5f;

            transferredItem.currentX =
                targetCenterX - target.direction.dx * 0.5f;

            transferredItem.currentY =
                targetCenterY - target.direction.dy * 0.5f;

            transferredItem.progress = 0f;

            target.item = transferredItem;
            source.item = null;

            System.out.println(
                "Transferred " + transferredItem.type +
                    " to conveyor at (" +
                    target.tileX + ", " + target.tileY + ")"
            );
        }
    }

    private void updateConveyors(float delta) {
        for (FactoryBuilding conveyor : buildings.values()) {
            if (conveyor.type != FactoryType.CONVEYOR) {
                continue;
            }

            if (conveyor.item == null) {
                continue;
            }

            Item item = conveyor.item;

            item.previousX = item.currentX;
            item.previousY = item.currentY;

            float previousProgress = item.progress;

            item.progress = Math.min(
                1f,
                item.progress + BELT_TILES_PER_SECOND * delta
            );

            float centerX = conveyor.tileX + 0.5f;
            float centerY = conveyor.tileY + 0.5f;

            // Start at the conveyor's input edge.
            float inputX = centerX - conveyor.direction.dx * 0.5f;
            float inputY = centerY - conveyor.direction.dy * 0.5f;

            item.currentX =
                inputX + conveyor.direction.dx * item.progress;

            item.currentY =
                inputY + conveyor.direction.dy * item.progress;

            if (previousProgress < 1f && item.progress >= 1f) {
                System.out.println(
                    "Item reached conveyor output at (" +
                        conveyor.tileX + ", " + conveyor.tileY + ")"
                );
            }
        }
    }

    private static long tileKey(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }

    public FactoryBuilding getBuildingAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY));
    }

    public Direction getBuildingDirectionAt(int tileX, int tileY) {
        return buildings.get(tileKey(tileX, tileY)).direction;
    }

    public int getBuildingCount() {
        return buildings.size;
    }


    @Override
    public FactoryContext getContext() {
        return factoryContext;
    }
}
