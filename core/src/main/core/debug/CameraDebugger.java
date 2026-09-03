package core.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.World;
import core.Player;

public final class CameraDebugger {

    private static final float LOG_INTERVAL = 0.25f;
    private static final float SNAP_POS_THRESHOLD = 0.08f;
    private static final float SNAP_ZOOM_THRESHOLD = 0.002f;
    private static final float ZOOM_ACTIVE_THRESHOLD = 0.00001f;

    private final CameraDebugSnapshot snapshot = new CameraDebugSnapshot();
    private final Vector3 tmp = new Vector3();

    private CameraDebugSnapshot previous;
    private float logTimer;

    private static boolean printCameraDebugToConsole = false;

    private int cameraFrame;

    public CameraDebugger() {}


    public void update(Viewport viewport, Player player, World world) {
        capture(viewport, player, world);
        analyze(snapshot);
    }

    public void render(ShapeRenderer shapeRenderer) {
        drawOverlays(snapshot, shapeRenderer);
    }


    //private, internal methods for debugging camera
    private void capture(Viewport viewport, Player player, World world) {
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        float followX = player.sprite.getX() + player.sprite.getWidth() / 2f;
        float followY = player.sprite.getY() + player.sprite.getHeight() / 2f;

        snapshot.frame = ++cameraFrame;

        snapshot.posX = camera.position.x;
        snapshot.posY = camera.position.y;
        snapshot.zoom = camera.zoom;
        snapshot.viewportWidth = camera.viewportWidth;
        snapshot.viewportHeight = camera.viewportHeight;
        snapshot.visibleWidth = camera.viewportWidth * camera.zoom;
        snapshot.visibleHeight = camera.viewportHeight * camera.zoom;

        snapshot.worldCenterX = world.getWorldWidth() * 0.5f;
        snapshot.worldCenterY = world.getWorldHeight() * 0.5f;
        snapshot.followX = followX;
        snapshot.followY = followY;

        snapshot.mouseScreenX = Gdx.input.getX();
        snapshot.mouseScreenY = Gdx.input.getY();

        tmp.set(snapshot.mouseScreenX, snapshot.mouseScreenY, 0f);
        viewport.unproject(tmp);
        snapshot.mouseWorldX = tmp.x;
        snapshot.mouseWorldY = tmp.y;
    }

    private void analyze(CameraDebugSnapshot current) {
        if (previous == null) {
            previous = new CameraDebugSnapshot();
            copyInto(previous, current);
            return;
        }

        current.deltaPosX = current.posX - previous.posX;
        current.deltaPosY = current.posY - previous.posY;
        current.deltaZoom = current.zoom - previous.zoom;
        current.deltaPosLen = (float) Math.hypot(current.deltaPosX, current.deltaPosY);

        // infer zoom animation from changing zoom value
        current.inferredZooming = Math.abs(current.deltaZoom) > ZOOM_ACTIVE_THRESHOLD;

        boolean suspectedSnap =
            current.deltaPosLen > SNAP_POS_THRESHOLD
                || Math.abs(current.deltaZoom) > SNAP_ZOOM_THRESHOLD;

        if (suspectedSnap) {
            printDebugLine("SNAP?", current);
        } else if (current.inferredZooming) {
            logTimer += Gdx.graphics.getDeltaTime();
            if (logTimer >= LOG_INTERVAL) {
                printDebugLine("zooming", current);
                logTimer = 0f;
            }
        }

        copyInto(previous, current);
    }


    private static void drawOverlays(CameraDebugSnapshot snap, ShapeRenderer sr) {
        float hw = snap.visibleWidth * 0.5f;
        float hh = snap.visibleHeight * 0.5f;

        sr.setColor(Color.CYAN);
        sr.rect(snap.posX - hw, snap.posY - hh, snap.visibleWidth, snap.visibleHeight);

        drawCross(sr, snap.worldCenterX, snap.worldCenterY, 3f, Color.RED);
        drawCross(sr, snap.posX, snap.posY, 2f, Color.GREEN);
        drawCross(sr, snap.mouseWorldX, snap.mouseWorldY, 1.5f, Color.MAGENTA);
        drawCross(sr, snap.followX, snap.followY, 1.5f, Color.WHITE);

        if (snap.inferredZooming) {
            sr.setColor(Color.YELLOW);
            sr.circle(snap.mouseWorldX, snap.mouseWorldY, 0.4f);
        }
    }

    private static void drawCross(ShapeRenderer renderer, float worldX, float worldY, float z, Color color) {

        renderer.setColor(color);
        renderer.line(worldX - z, worldY, worldX + z, worldY);
        renderer.line(worldX, worldY - z, worldX, worldY + z);

    }


    private static void printDebugLine(String tag, CameraDebugSnapshot snapshot) {
        if (!printCameraDebugToConsole) return;
        System.out.printf(
            "[CAMERA %s] frame=%d zoom=%.6f dZoom=%.6f pos=(%.4f, %.4f) dPos=%.4f " +
                "inferredZoom=%s mouseWorld=(%.3f, %.3f)%n",
            tag, snapshot.frame, snapshot.zoom, snapshot.deltaZoom,
            snapshot.posX, snapshot.posY, snapshot.deltaPosLen,
            snapshot.inferredZooming, snapshot.mouseWorldX, snapshot.mouseWorldY
        );
    }

    private static void copyInto(CameraDebugSnapshot dst, CameraDebugSnapshot src) {
        dst.frame = src.frame;
        dst.posX = src.posX;
        dst.posY = src.posY;
        dst.zoom = src.zoom;
        dst.viewportWidth = src.viewportWidth;
        dst.viewportHeight = src.viewportHeight;
        dst.visibleWidth = src.visibleWidth;
        dst.visibleHeight = src.visibleHeight;
        dst.worldCenterX = src.worldCenterX;
        dst.worldCenterY = src.worldCenterY;
        dst.followX = src.followX;
        dst.followY = src.followY;
        dst.mouseScreenX = src.mouseScreenX;
        dst.mouseScreenY = src.mouseScreenY;
        dst.mouseWorldX = src.mouseWorldX;
        dst.mouseWorldY = src.mouseWorldY;
        dst.inferredZooming = src.inferredZooming;
        dst.deltaPosX = src.deltaPosX;
        dst.deltaPosY = src.deltaPosY;
        dst.deltaZoom = src.deltaZoom;
        dst.deltaPosLen = src.deltaPosLen;
    }

}
