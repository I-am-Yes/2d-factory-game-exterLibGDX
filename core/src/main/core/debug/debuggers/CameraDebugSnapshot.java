package core.debug.debuggers;

public class CameraDebugSnapshot {

    public int frame;

    public float posX, posY, zoom;
    public float viewportWidth, viewportHeight;
    public float visibleWidth, visibleHeight;

    public float worldCenterX, worldCenterY;
    public float followX, followY;

    public int mouseScreenX, mouseScreenY;
    public float mouseWorldX, mouseWorldY;

    // inferred (not from CameraController)
    public boolean inferredZooming;

    // filled by CameraDebugger.update()
    public float deltaPosX, deltaPosY, deltaZoom;
    public float deltaPosLen;

}
