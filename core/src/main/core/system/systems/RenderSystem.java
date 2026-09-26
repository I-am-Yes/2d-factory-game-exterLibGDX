package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.app.cores.AppListener;
import core.system.context.RenderContext;
import core.entities.plan.PlanManager;
import core.render.PlanRenderer;
import core.render.RenderLayer;
import core.app.cores.GameSysCycle;
import data.map.asset.AssetType;

import static core.app.Vars.*;

public class RenderSystem implements AppListener, GameSysCycle {

    private final PlanRenderer<AssetType> planRenderer;
    private final PlanManager planManager;

    public RenderSystem() {
        this.planManager = new PlanManager(world);
        this.planRenderer = new PlanRenderer<>(
            world,
            viewport,
            batch,
            assets,
            planManager
        );

    }

    @Override
    public void init() {
        AppListener.super.init();
    }

    @Override
    public void update() {

        planRenderer.update();
    }

    @Override
    public void create() {
        AppListener.super.create();
    }

    @Override
    public void render() {
    }

    @Override
    public void resize(int width, int height) {
        AppListener.super.resize(width, height);
    }

    @Override
    public void pause() {
        AppListener.super.pause();
    }

    @Override
    public void resume() {
        AppListener.super.resume();
    }

    @Override
    public void drawBatch(SpriteBatch batch) {
        //BEGIN batch

        planRenderer.drawBatch();

        //END batch
    }

    public void dispose() {

        planRenderer.dispose();
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.OBJECT_LAYER;
    }

}
