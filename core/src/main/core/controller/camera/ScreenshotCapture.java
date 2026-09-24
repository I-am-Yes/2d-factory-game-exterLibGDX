package core.controller.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import core.world.World;

import java.nio.IntBuffer;

public final class ScreenshotCapture {

    private static boolean requested;

    private ScreenshotCapture() {
    }

    public static void requestCapture() {
        requested = true;
    }

    /**
     * Captures the current game window, including GUI.
     * Call this at the end of the normal render frame.
     */
    public static void captureIfRequested() {
        if (!requested) return;

        requested = false;

        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();

        byte[] pixels = ScreenUtils.getFrameBufferPixels(
            0,
            0,
            width,
            height,
            true
        );

        Pixmap pixmap = new Pixmap(
            width,
            height,
            Pixmap.Format.RGBA8888
        );

        try {
            BufferUtils.copy(
                pixels,
                0,
                pixmap.getPixels(),
                pixels.length
            );

            FileHandle folder = Gdx.files.local("screenshots");
            folder.mkdirs();

            FileHandle output = folder.child(
                "screenshot-" + TimeUtils.millis() + ".png"
            );

            PixmapIO.writePNG(output, pixmap);

            Gdx.app.log(
                "ScreenshotCapture",
                "Saved screenshot: " + output.file().getAbsolutePath()
            );
        } finally {
            pixmap.dispose();
        }
    }

    /**
     * Captures a selected map area without GUI.
     *
     * @param minTileX      inclusive starting world tile X
     * @param minTileY      inclusive starting world tile Y
     * @param widthTiles    map width in tiles
     * @param heightTiles   map height in tiles
     * @param pixelsPerTile output resolution per tile
     */
    public static FileHandle captureMapArea(
        World world,
        int minTileX,
        int minTileY,
        int widthTiles,
        int heightTiles,
        int pixelsPerTile
    ) {
        if (widthTiles <= 0 || heightTiles <= 0 || pixelsPerTile <= 0) {
            throw new IllegalArgumentException(
                "Capture dimensions must be positive."
            );
        }

        int imageWidth = widthTiles * pixelsPerTile;
        int imageHeight = heightTiles * pixelsPerTile;

        validateTextureSize(imageWidth, imageHeight);

        float tileSize = world.getTileSize();
        float worldWidth = widthTiles * tileSize;
        float worldHeight = heightTiles * tileSize;

        OrthographicCamera camera = new OrthographicCamera(
            worldWidth,
            worldHeight
        );

        camera.position.set(
            minTileX * tileSize + worldWidth * 0.5f,
            minTileY * tileSize + worldHeight * 0.5f,
            0f
        );

        camera.update();

        FrameBuffer frameBuffer = new FrameBuffer(
            Pixmap.Format.RGBA8888,
            imageWidth,
            imageHeight,
            false
        );

        try {
            frameBuffer.begin();

            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

            // Renders only the map. No GUI systems are called.
            world.update(camera);

            byte[] pixels = ScreenUtils.getFrameBufferPixels(
                0,
                0,
                imageWidth,
                imageHeight,
                true
            );

            Pixmap pixmap = new Pixmap(
                imageWidth,
                imageHeight,
                Pixmap.Format.RGBA8888
            );

            try {
                BufferUtils.copy(
                    pixels,
                    0,
                    pixmap.getPixels(),
                    pixels.length
                );

                FileHandle folder = Gdx.files.local("screenshots/maps");
                folder.mkdirs();

                FileHandle output = folder.child(
                    "map-"
                        + minTileX + "_"
                        + minTileY + "_"
                        + widthTiles + "x"
                        + heightTiles
                        + ".png"
                );

                PixmapIO.writePNG(output, pixmap);

                Gdx.app.log(
                    "ScreenshotCapture",
                    "Saved map capture: "
                        + output.file().getAbsolutePath()
                );

                return output;
            } finally {
                pixmap.dispose();
            }
        } finally {
            frameBuffer.end();
            frameBuffer.dispose();
        }
    }

    private static void validateTextureSize(int width, int height) {
        IntBuffer maxTextureSize = BufferUtils.newIntBuffer(1);

        Gdx.gl.glGetIntegerv(
            GL20.GL_MAX_TEXTURE_SIZE,
            maxTextureSize
        );

        int maximum = maxTextureSize.get(0);

        if (width > maximum || height > maximum) {
            throw new IllegalArgumentException(
                "Map screenshot is "
                    + width + "x" + height
                    + ", but this GPU supports at most "
                    + maximum + "x" + maximum
                    + ". Use a smaller map area or pixelsPerTile."
            );
        }
    }
}
