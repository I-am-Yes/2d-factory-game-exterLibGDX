package core.controller.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import core.InputHandler;
import core.player.Player;
import core.app.GameContext;
import core.system.context.PlayerContext;
import core.player.mechanic.PlayerController;
import core.world.World;

import core.config.camera.CameraSettings;
import core.world.chunk.ChunkRenderDetail;

public class CameraController {
    private final CameraSettings settings;
    private final GameContext context;
    private final PlayerContext playerContext;
    private final Viewport viewport;
    private final World world;
    private final Player player;
    private final PlayerController controller;
    private final InputHandler input;
    private final OrthographicCamera camera;

    private static final float MIN_ZOOM = 0.02f;
    private static final float MAX_ZOOM = 0.15f;
    private static final float MAP_VIEW_MAX_ZOOM = 16f + Float.MAX_VALUE;
    private static final float MAX_ZOOM_PLUS = 0.0f;
    private static final float MAX_ZOOM_MULTI = 1f;
    private static float ZOOM_STEP = 0.25f;

    private float panVx, panVy;
    private static final float PAN_ACCEL = 40f;
    private static final float PAN_FRICTION = 25f;
    private static final float PAN_STOP_THRESHOLD = 0.01f;

    private CameraViewMode viewMode = CameraViewMode.GAMEPLAY;

    public enum ZoomMode {
        CURSOR_ZOOM,
        CENTER_ZOOM,
        }
    private ZoomMode zoomMode = ZoomMode.CENTER_ZOOM;

    private float targetZoom;
    private float zoomFocusWorldX;
    private float zoomFocusWorldY;

    private int lastPanScreenX,  lastPanScreenY;

    private float followX, followY;

    private boolean zooming;
    private boolean panning;
    private boolean cameraFollowPlayer = true;

    private final Vector3 tmp = new Vector3();

    private float delta;

    public CameraController(GameContext context, PlayerContext playerContext, CameraSettings settings) {
        this.context = context;
        this.playerContext = playerContext;
        this.settings = settings;
        this.viewport = context.viewport;
        this.camera = (OrthographicCamera) viewport.getCamera();
        this.world = context.world;
        this.input = context.input;
        this.player = playerContext.player.getPlayer();
        this.controller = playerContext.controller;

        camera.zoom = settings.defaultZoom;
        targetZoom = settings.defaultZoom;
        ZOOM_STEP = settings.defaultZoomStep;
    }

    public void update(float delta) {
        this.delta = delta;

        followX = player.getSprite().getX() + player.getSprite().getWidth() / 2f;
        followY = player.getSprite().getY() + player.getSprite().getHeight() / 2f;

        //TODO: later migrate to EntrySystem
        if (!input.isMousePressed(Input.Buttons.MIDDLE) && controller.isPlayerMoving()) {
            cameraFollowPlayer = true;
        }

        if (input.isKeyJustPressed(Input.Keys.M)
            && viewMode != CameraViewMode.FABULOUS
            && !panning
        ) {
            boolean leavingMap =
                viewMode == CameraViewMode.MAP || targetZoom >= settings.mapViewEnterThreshold;

            if (leavingMap) {
                cameraFollowPlayer = true;
                viewMode = CameraViewMode.GAMEPLAY;
                requestZoomToPlayer(settings.defaultZoom, followX, followY
                );
            } else {
                cameraFollowPlayer = false;
                viewMode = CameraViewMode.MAP;
                requestZoomTo(settings.mapViewEnterThreshold, followX, followY);
            }
        }

        updateCamera();
        updateViewMode();
    }

    public void updateCamera() {

        float scroll = input.consumeScrollY();
        if (scroll != 0f && !input.isMousePressed(Input.Buttons.MIDDLE)) {
            requestZoom(scroll, followX, followY);
        }


        handleMiddleMousePan(input);
        animateZoom(delta, followX, followY);
        moveCamera(input, delta);
        zoomCamera(input, delta, followX, followY);

        if (cameraFollowPlayer) {
            float smoothness = settings.followSmoothness;
            float t = 1f - (float) Math.pow(1f - smoothness, delta * 60f);

            camera.position.x = MathUtils.lerp(camera.position.x, followX, t);
            camera.position.y = MathUtils.lerp(camera.position.y, followY, t);
        }

//        clampToWorld(world.getWorldWidth(), world.getWorldHeight());
        camera.update();
    }

    //TODO: change this to global input handling in EntrySystem.java
    public void moveCamera(InputHandler input, float delta) {
        float inputX = 0f;
        float inputY = 0f;

        if (input.isKeyPressed(Input.Keys.LEFT)) inputX -= 1f;
        if (input.isKeyPressed(Input.Keys.RIGHT)) inputX += 1f;
        if (input.isKeyPressed(Input.Keys.UP)) inputY += 1f;
        if (input.isKeyPressed(Input.Keys.DOWN)) inputY -= 1f;

        animateMove(delta, inputX, inputY);
    }

    //TODO: change this to global input handling in EntrySystem.java
    public void zoomCamera(InputHandler input, float delta, float followX, float followY) {
        float zoomDir = 0f;

        if (input.isKeyPressed(Input.Keys.PLUS) || input.isKeyPressed(Input.Keys.EQUALS)) {
            zoomDir += 1f;
        }
        if (input.isKeyPressed(Input.Keys.MINUS)) {
            zoomDir -= 1f;
        }
        if (zoomDir == 0f) return;

        float zoomSpeed = 2.4f; // 0.15f * 16f
        float previousTarget = targetZoom;

        // Multiplicative zoom: consistent perceived zoom speed.
        targetZoom *= (float) Math.exp(-zoomDir * zoomSpeed * delta);
        targetZoom = MathUtils.clamp(targetZoom, MIN_ZOOM, getMapMaxZoom());

        if (targetZoom != previousTarget) {
            if (zoomMode == ZoomMode.CURSOR_ZOOM) {
                captureZoomFocus(followX, followY);
            } else {
                zoomFocusWorldX = camera.position.x;
                zoomFocusWorldY = camera.position.y;
            }
            zooming = true;
        }
    }

    public void centerOn(float worldX, float worldY) {
        camera.position.set(worldX, worldY, 0f);
        camera.update();
    }

    private void updateViewMode() {
        if (viewMode == CameraViewMode.FABULOUS) return;

        float enterThreshold = settings.mapViewEnterThreshold;
        float exitThreshold = enterThreshold * 0.5f;

        if (viewMode == CameraViewMode.GAMEPLAY && camera.zoom >= enterThreshold) {
            viewMode = CameraViewMode.MAP;

        } else if (viewMode == CameraViewMode.MAP
            && targetZoom <= exitThreshold
            && camera.zoom <= exitThreshold) {

            viewMode = CameraViewMode.GAMEPLAY;
        }
    }

    private void requestZoomToPlayer(float requestedZoom, float playerX, float playerY) {
        targetZoom = MathUtils.clamp(requestedZoom, MIN_ZOOM, getMapMaxZoom());
        zoomFocusWorldX = playerX;
        zoomFocusWorldY = playerY;
        zooming = true;
    }

    private void requestZoomTo(float requestedZoom, float followX, float followY) {
        float newTarget = MathUtils.clamp(requestedZoom, MIN_ZOOM, getMapMaxZoom());

        if (MathUtils.isEqual(targetZoom, newTarget, Float.MIN_VALUE)) return;

        targetZoom = newTarget;
        captureZoomFocus(followX, followY);
        zooming = true;
    }

    private void requestZoom(float zoomDir, float followX, float followY) {
        if (zoomDir == 0f) return;

        float step = ZOOM_STEP * MathUtils.clamp(targetZoom, 0.05f, 4f);
        targetZoom = MathUtils.clamp(targetZoom - zoomDir * step, MIN_ZOOM, getMapMaxZoom());

        captureZoomFocus(followX, followY);
        zooming = true;
    }

    private void captureZoomFocus(float followX, float followY) {
        if (zoomMode == ZoomMode.CURSOR_ZOOM) {
            tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(tmp);
            zoomFocusWorldX = tmp.x;
            zoomFocusWorldY = tmp.y;
            return;
        }

        if (cameraFollowPlayer) {
            zoomFocusWorldX = followX;
            zoomFocusWorldY = followY;
        } else {
            zoomFocusWorldX = camera.position.x;
            zoomFocusWorldY = camera.position.y;
        }
    }

    private void animateZoom(float delta, float followX, float followY) {
        //                     higher value for further away the exact zoom pos before snap to the point
        if (MathUtils.isEqual(camera.zoom, targetZoom, 0.000001f)) {
            camera.zoom = targetZoom;
            zooming = false;
            return;
        }

        float smoothness = settings.zoomSmoothness;
        float zoomT = 1f - (float) Math.pow(1f - smoothness, delta * 60f);
        float newZoom = MathUtils.lerp(camera.zoom, targetZoom, zoomT);

        if (panning || !zooming) {
            camera.zoom = newZoom;
            camera.update();
            return;
        }

        projectZoomFocusToScreen(followX, followY);
        zoomAtScreenPoint(tmp.x, tmp.y, newZoom);
    }

    private void animateMove(float delta, float inputX, float inputY) {
        boolean hasInput = inputX != 0f || inputY != 0f;
        float rate = hasInput ? PAN_ACCEL : PAN_FRICTION;

        float speed = settings.panSpeed * camera.zoom;  // screen-consistent

        panVx = MathUtils.lerp(panVx, inputX * speed, Math.min(1f, rate * delta));
        panVy = MathUtils.lerp(panVy, inputY * speed, Math.min(1f, rate * delta));

        if (Math.abs(panVx) < PAN_STOP_THRESHOLD && Math.abs(panVy) < PAN_STOP_THRESHOLD) {
            panVx = 0f;
            panVy = 0f;
            return;
        }

        cameraFollowPlayer = false;
        zooming = false;

        camera.position.x += panVx * delta;
        camera.position.y += panVy * delta;
    }

    private void projectZoomFocusToScreen(float followX, float followY) {
        float wx, wy;

        if (zoomMode == ZoomMode.CENTER_ZOOM) {
            wx = cameraFollowPlayer ? followX : camera.position.x;
            wy = cameraFollowPlayer ? followY : camera.position.y;
        } else {
            wx = zoomFocusWorldX;
            wy = zoomFocusWorldY;
        }

        tmp.set(wx, wy, 0f);
        viewport.project(tmp);
    }

    private void zoomAtScreenPoint(float screenX, float screenY, float newZoom) {
        tmp.set(screenX, screenY, 0f);
        viewport.unproject(tmp);

        float worldX = tmp.x;
        float worldY = tmp.y;

        camera.zoom = MathUtils.clamp(newZoom, MIN_ZOOM, getMapMaxZoom());
        camera.update();

        tmp.set(screenX, screenY, 0f);
        viewport.unproject(tmp);

        camera.position.x += worldX - tmp.x;
        camera.position.y += worldY - tmp.y;
    }

    private void clampToWorld(float worldWidth, float worldHeight) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, worldWidth - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, worldHeight - halfHeight);
    }

    //TODO: change this to global input handling in EntrySystem.java
    private void handleMiddleMousePan(InputHandler input) {
        if (!input.isMousePressed(Input.Buttons.MIDDLE)) {
            panning = false;
            return;
        }
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if (!panning) {
            panning = true;
            lastPanScreenX = x;
            lastPanScreenY = y;

            panVx = 0f;
            panVy = 0f;

            cameraFollowPlayer = false;
            zooming = false;
            targetZoom = camera.zoom;
            return;
        }

        tmp.set(lastPanScreenX, lastPanScreenY, 0f);
        viewport.unproject(tmp);

        float previousWorldX = tmp.x;
        float previousWorldY = tmp.y;

        tmp.set(x, y, 0f);
        viewport.unproject(tmp);

        camera.position.x += previousWorldX - tmp.x;
        camera.position.y += previousWorldY - tmp.y;

        lastPanScreenX = x;
        lastPanScreenY = y;
    }

    private float getMapMaxZoom() {
        if (viewMode == CameraViewMode.MAP) {
            return MAP_VIEW_MAX_ZOOM;
        }
        if (viewMode == CameraViewMode.FABULOUS) {
            return Float.MAX_VALUE;
        }
        return MAX_ZOOM;
    }

    public float getZoomValue() {
        return camera.zoom;
    }

    public ZoomMode getZoomMode() {
        return zoomMode;
    }

    public void setZoomMode(ZoomMode zoomMode) {
        this.zoomMode = zoomMode;
    }

    public CameraViewMode getCurrentViewMode() {
        return viewMode;
    }

    public ChunkRenderDetail getMapRenderDetail() {
        return viewMode == CameraViewMode.MAP
            ? settings.mapViewDetail
            : settings.normalViewDetail;
    }

}
