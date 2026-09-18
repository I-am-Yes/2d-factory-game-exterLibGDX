package core.config.camera;

import core.world.chunk.ChunkRenderDetail;

public class CameraSettings {
    public float followSmoothness = 0.08f;
    public float zoomSmoothness = 0.10f;
    public float panSpeed = 700f;
    public float defaultZoom = 0.025f;

    public float mapViewEnterThreshold = 0.5f;

    public ChunkRenderDetail normalViewDetail = ChunkRenderDetail.EXTREME;
    public ChunkRenderDetail mapViewDetail = ChunkRenderDetail.BEYOND_COMPREHENSION;
}
