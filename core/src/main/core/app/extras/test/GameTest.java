package core.app.extras.test;

import arcane.ApplicationListener;
import arcane.math.MathUtils;
import core.assets.textures.types.FloorType;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.event.GameEvent;
import core.assets.textures.AssetType;
import core.assets.textures.types.BuildingType;
import core.utils.item.Items;
import core.utils.unit.units.SmallBot;
import ui.InterfaceSystem;

import static core.app.Vars.*;

public class GameTest implements ApplicationListener {
    public GameTest() {

        PlanManager planManager = new PlanManager();
        PlanBuilder<AssetType> planBuilder = new PlanBuilder<>();

        float divideTest = 1f;
        //testers
//        PlanBuilder<AssetType> planBuilderTest1 = new PlanBuilder<>();
//        Random random = new Random();
//        for (int i = 0; i < context.world.getWorldWidth() / divideTest; i++) {
//            for (int j = 0; j < context.world.getWorldHeight() / divideTest; j++) {
//                if (random.nextBoolean()) {
//                    planBuilderTest1.addPlan(i, j, BuildingType.HAZARD_BLOCK);
//                } else {
//                    planBuilderTest1.addPlan(i, j, FloorType.MARBLE);
//                }
//            }
//        }
//        GameEvent.PlanBuilderRequest.fire(planBuilderTest1);

//        planManager.addPlanToRenderQueue(planBuilder);


        int amount = 10;
        for (int y = 0; y < amount; y++) {
            GameEvent.BlockPlaceRequest.fire(0, y, BuildingType.CREATIVE_SOURCE);

            for (int x = 1; x < amount; x++) {
                GameEvent.BlockPlaceRequest.fire(x, y, BuildingType.CONVEYOR_BELT_2);}

            GameEvent.BlockPlaceRequest.fire(amount, y, BuildingType.STORAGE_CHEST_2);
        }

        InterfaceSystem.playerHotbar.
            setHotbarItems(
                1, BuildingType.CONVEYOR_BELT_2,
                2, BuildingType.CREATIVE_SOURCE,
                3, BuildingType.STORAGE_CHEST_2,
                4, FloorType.STONE,
                5, FloorType.MARBLE,
                6, FloorType.WATER,
                7, Items.spawnBotItem,
                8, BuildingType.STORAGE_CHEST_2
        );

        int amount2 = 10;
        int columns = (int) Math.ceil(Math.sqrt(amount2));
        float tileSize = world.getTileSize();
        float spawnX = tileSize;
        float spawnY = tileSize;

        for (int i = 0; i < amount2; i++) {
            botManager.spawnBot(spawnX, spawnY, 90f);
            botManager.spawnBot(new SmallBot(), spawnX, spawnY, 90f);

            if ((i + 1) % columns == 0) {
                spawnX = tileSize;
                spawnY += MathUtils.random(4, 8) * tileSize;
            } else {
                spawnX += MathUtils.random(4, 8) * tileSize;
            }
        }

        botManager.setBotsFollow(true);


//        Gdx.app.log(
//            "Graphics",
//            "Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR)
//        );
//
//        Gdx.app.log(
//            "Graphics",
//            "Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER)
//        );
//
//        Gdx.app.log(
//            "Graphics",
//            "Version: " + Gdx.gl.glGetString(GL20.GL_VERSION)
//        );
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
