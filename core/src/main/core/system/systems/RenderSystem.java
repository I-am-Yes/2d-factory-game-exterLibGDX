package core.system.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.app.cores.AppListener;
import core.app.vars.Cores;
import core.entities.plan.PlanManager;
import core.render.PlanRenderer;
import core.render.RenderLayer;
import core.app.cores.GameSysCycle;
import data.map.asset.AssetType;

import static core.app.vars.Vars.*;

public class RenderSystem implements AppListener {

    private final PlanRenderer<AssetType> planRenderer;
    private final PlanManager planManager;

    public RenderSystem() {
        this.planManager = new PlanManager(world);
        this.planRenderer = new PlanRenderer<>(
            world,
            Cores.viewport,
            Cores.batch,
            Cores.assets,
            planManager
        );

    }

    @Override
    public void update() {

        planRenderer.update();
    }

    public void drawBatch(SpriteBatch batch) {
        //BEGIN batch
        planRenderer.drawBatch();
        //END batch
    }

    public void dispose() {
        planRenderer.dispose();
    }

    public RenderLayer renderLayer() {
        return RenderLayer.OBJECT_LAYER;
    }

}
