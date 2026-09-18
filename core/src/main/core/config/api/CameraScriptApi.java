package core.config.api;

import core.config.camera.CameraSettings;
import com.badlogic.gdx.math.MathUtils;
import core.world.chunk.ChunkRenderDetail;

public class CameraScriptApi {

    private final CameraSettings settings;

    public CameraScriptApi(CameraSettings settings) {
        this.settings = settings;
    }

    public void setMapViewEnterThreshold(float threshold) {
        settings.mapViewEnterThreshold = threshold;
    }

    public void setNormalRenderDetail(String name) {
        settings.normalViewDetail =
            ChunkRenderDetail.valueOf(name.trim().toUpperCase());
    }

    public void setMapRenderDetail(String name) {
        settings.mapViewDetail =
            ChunkRenderDetail.valueOf(name.trim().toUpperCase());
    }

    public void setFollowSmoothness(double value) {
        settings.followSmoothness = MathUtils.clamp((float) value, 0.001f, 1f);
    }

    public void setZoomSmoothness(double value) {
        settings.zoomSmoothness = MathUtils.clamp((float) value, 0.001f, 1f);
    }

    public void setPanSpeed(double value) {
        settings.panSpeed = Math.max(0f, (float) value);
    }

    public void setDefaultZoom(double value) {
        settings.defaultZoom = Math.max(0.001f, (float) value);
    }
}
